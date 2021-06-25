package org.mm.core.captcha;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.mm.core.config.CaptchaConfig;
import org.mm.core.exception.IllegalParameterException;
import org.mm.core.security.AesCoderUtil;
import org.mm.core.security.Base64CoderUtil;
import org.mm.core.security.Md5CoderUtil;
import org.mm.core.security.RsaCoderUtil;
import org.mm.core.util.ClientKeyUtil;
import org.mm.core.util.ImageUtil;
import org.mm.core.util.ImgCompressUtil;
import org.mm.core.util.RandomUtil;
import org.mm.core.util.RedisUtil;
import org.mm.core.util.TypeParseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaptchaUtil {

	private static String BASE_DIR;                                // 图库文件夹
	private static String BASE_THEME;                              // 主题
	private static String BASE_BACKGROUND;                         // 图片文件名
	private static String BASE_IMG_TYPE;                           // 图片格式

	public static final Integer BACKGROUND_RESIZE = 5;
	public static final Integer DEFAULT_WIDTH = 300 * BACKGROUND_RESIZE;  // 图片默认宽度
	public static final Integer DEFAULT_HEIGHT = 160 * BACKGROUND_RESIZE; // 图片默认高度
	private static final Integer DEFAULT_TIMES = 3;                // 用户操作次数
	private static final Integer DEFAULT_V_CUT = 15;               // 纵切次数
	private static final Integer DEFAULT_H_CUT = 3;                // 横切次数
	private static final Boolean DEFAULT_UPSET = true;             // 是否打乱图片顺序
	
	private static final double DRAG_ERROR_RATIO = 0.05;           // 拖拽验证失误范围比率
	private static final double CLICK_ERROR_RATIO = 0.3;           // 点击验证失误范围比率
	
	private static Integer CAPTCHA_EXPIRE_SECONDS;                 // 验证码有效时间
	private static Integer TOKEN_EXPIRE_SECONDS;                   // 验证码Token有效时间
	private static Integer MAX_FAIL_TIMES;                         // 最大校验失败次数
	
	@Autowired
	private RedisUtil redisUtilBean;
	
	@Autowired
	private CaptchaConfig configBean;
	
	private static RedisUtil redisUtil;
	
	@PostConstruct
	public void init() {
		redisUtil = this.redisUtilBean;
		BASE_DIR = configBean.imgBaseDir;
		BASE_THEME = configBean.baseTheme;
		BASE_BACKGROUND = configBean.baseBackground;
		BASE_IMG_TYPE = configBean.baseImgType;
		CAPTCHA_EXPIRE_SECONDS = configBean.expireSeconds;
		TOKEN_EXPIRE_SECONDS = configBean.tokenExpireSeconds;
		MAX_FAIL_TIMES = configBean.maxFailTimes;
	}
	
	/**
	 * 普通方式生成
	 * @param canvasPath
	 * @param type
	 * @param times
	 * @param iconType
	 * @param vCut
	 * @param hCut
	 * @param upset
	 * @return
	 */
	public static Map<String, Object> generate(String canvasPath, Integer type, Integer times, Integer iconType, Integer vCut, Integer hCut, Boolean upset) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		if (times == null || times < 0) times = DEFAULT_TIMES;
		if (vCut == null || vCut < 0) vCut = DEFAULT_V_CUT;
		if (hCut == null || hCut < 0) hCut = DEFAULT_H_CUT;
		if (upset == null) upset = DEFAULT_UPSET;
		
		if (type.intValue() == CaptchaMode.Drag.getValue().intValue()) {
			resMap = DragCaptchaUtil.createImage(canvasPath, vCut, hCut, upset);
		} else {
			resMap = ClickCaptchaUtil.createImage(canvasPath, times, iconType, vCut, hCut, upset);
		}
		
		if (resMap.isEmpty()) {
			resMap.put("ok", false);
		} else {
			resMap.put("ok", true);
			String captchaKey = ClientKeyUtil.captchaKey();
			resMap.put("key", captchaKey);
			resMap.put("type", type);
			resMap.put("cols", vCut);
			
			Integer[][] positions = (Integer[][]) resMap.get("positions");
			resMap.remove("positions");
			if (type.intValue() == CaptchaMode.Drag.getValue().intValue()) {
				Integer posY = positions[0][1];
				resMap.put("iconY", posY);
			}
			String positionsStr = arr2JsonStr(positions);
			redisUtil.hset(captchaKey, "positions", positionsStr);
			redisUtil.hset(captchaKey, "type", type);
			redisUtil.expire(captchaKey, CAPTCHA_EXPIRE_SECONDS);
		}
		
		return resMap;
	}
	
	/**
	 * 加密方式生成
	 * @param canvasPath
	 * @param type
	 * @param times
	 * @param iconType
	 * @return
	 */
	public static Map<String, Object> securityGenerate(String canvasPath, int type, Integer times, Integer iconType) {
		Map<String, Object> resMap = generate(canvasPath, type, times, iconType, null, null, null);
		boolean success = (Boolean) resMap.get("ok");
		if (success) {
			try {
				String key = (String) resMap.get("key");
				String aesKey = Md5CoderUtil.len16(key);
				Integer iconY = (Integer) resMap.get("iconY");
				String guide = (String) resMap.get("guide");
				Integer cols = (Integer) resMap.get("cols");
				Integer[] series = (Integer[]) resMap.get("series");
				
				Map<String, Object> rsaKeyMap = RsaCoderUtil.genKeyPair();
				String priKey = RsaCoderUtil.getPrivateKey(rsaKeyMap);
				String pubKey = RsaCoderUtil.getPublicKey(rsaKeyMap);
				
				String encryptedPubKey = AesCoderUtil.encrypt(pubKey, aesKey);
				String encryptedGuide = RsaCoderUtil.priEncrypt(guide, priKey);
				String encryptedCols = RsaCoderUtil.priEncrypt(cols.toString(), priKey);
				String[] encryptedSeries = new String[series.length];
				for (int i = 0; i < series.length; i++) {
					String encryptedS = RsaCoderUtil.priEncrypt(series[i].toString(), priKey);
					encryptedSeries[i] = encryptedS;
				}
				
				resMap.put("rsaPubKey", encryptedPubKey);
				resMap.put("guide", encryptedGuide);
				resMap.put("cols", encryptedCols);
				resMap.put("series", encryptedSeries);
				if (iconY != null) {
					String encryptedIconY = RsaCoderUtil.priEncrypt(iconY.toString(), priKey);
					resMap.put("iconY", encryptedIconY);
				}
				
				redisUtil.hset(key, "rsaPriKey", priKey);
				redisUtil.expire(key, CAPTCHA_EXPIRE_SECONDS);
			} catch (Exception e) {
				resMap.clear();
				resMap.put("ok", false);
				e.printStackTrace();
			}
		}
		
		return resMap;
	}
	
	/**
	 * 验证码失效，最大验证次数以内不失效
	 * @param key
	 * @return
	 */
	public static void expire(String key) {
		if (!redisUtil.hasKey(key)) return;
		
		Integer usedTimes = (Integer) redisUtil.hget(key, "times");
		if (usedTimes == null) usedTimes = 0;
		usedTimes++;
		if (usedTimes.intValue() < MAX_FAIL_TIMES) {
			redisUtil.hset(key, "times", usedTimes);
		} else {
			delete(key);
		}
	}
	
	/**
	 * 检验验证码是否可用
	 * @param key
	 * @return
	 */
	public static boolean usable(String key) {
		if (!redisUtil.hasKey(key)) return false;
		Integer usedTimes = (Integer) redisUtil.hget(key, "times");
		
		return usedTimes == null || usedTimes < MAX_FAIL_TIMES;
	}
	
	/**
	 * 删除验证码信息
	 * @param key
	 */
	public static void delete(String key) {
		redisUtil.del(key);
	}
	
	/**
	 * 校验验证码
	 * @param key
	 * @param clientPositions
	 * @return
	 */
	public static boolean verify(String key, double[][] clientPositions) {
		try {
			Integer type = (Integer) redisUtil.hget(key, "type");
			String positionsStr = (String) redisUtil.hget(key, "positions");
			Integer[][] positions = jsonStr2Arr(positionsStr);
			if (positions.length != clientPositions.length) {
				throw new IllegalParameterException("captcha " + key + " positions not match");
			}
			
			int iconWidth = 0;
			int iconHeight = 0;
			double radius = 0;
			boolean iconCore = true;
			switch (type) {
			case 1:
				iconWidth = DragCaptchaUtil.getIconWidth();
				iconHeight = DragCaptchaUtil.getIconHeight();
				radius = iconWidth * DRAG_ERROR_RATIO;
				iconCore = false;
				break;
			default:
				iconWidth = ClickCaptchaUtil.getIconWidth();
				iconHeight = ClickCaptchaUtil.getIconHeight();
				radius = iconWidth * CLICK_ERROR_RATIO;
				break;
			}
			
			for (int i = 0; i < positions.length; i++) {
				Integer[] position = positions[i];
				double[] clientPos = clientPositions[i];
				if (position.length != clientPos.length) {
					throw new IllegalParameterException("captcha " + key + " positions not match");
				}
				
				int x = position[0];
				int y = position[1];
				int coreX = iconCore ? x + iconWidth / 2 : x;
				int coreY = iconCore ? y + iconHeight / 2 : y;
				
				double clientX = clientPos[0];
				double clientY = clientPos[1];
				
				double clientWidth = clientX - coreX;
				double clientHeight = clientY - coreY;
				if (clientWidth * clientWidth + clientHeight * clientHeight > radius * radius) {
					throw new IllegalParameterException("captcha " + key + " verify failed");
				}
			}
		} catch (Exception e) {
			expire(key);
			System.out.println(e.getMessage());
			return false;
		}
		delete(key);
		
		return true;
	}
	
	/**
	 * 加密模式校验验证码
	 * @param key
	 * @param clientPositions
	 * @return
	 */
	public static boolean securityVerify(String key, String[][] clientPositions) {
		String aesKey = Md5CoderUtil.len16(key);
		String rsaPriKey = (String) redisUtil.hget(key, "rsaPriKey");
		if (StringUtils.isBlank(rsaPriKey)) return false;
		
		try {
			double[][] clientPoss = new double[clientPositions.length][];
			for (int i = 0; i < clientPositions.length; i++) {
				String[] clientPosition = clientPositions[i];
				clientPoss[i] = new double[clientPosition.length];
				for (int j = 0; j < clientPosition.length; j++) {
					String aesDecrypt = AesCoderUtil.decrypt(clientPosition[j], aesKey);
					String rsaDecrypt = RsaCoderUtil.priDecrypt(aesDecrypt, rsaPriKey);
					clientPoss[i][j] = Double.parseDouble(rsaDecrypt);
				}
			}
			
			return verify(key, clientPoss);
		} catch (Exception e) {
			expire(key);
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	/**
	 * 生成验证token
	 * @param apiKey
	 * @param key
	 * @return
	 */
	public static String generateToken(String apiKey, String key) {
		try {
			String uuid = UUID.randomUUID().toString();
			String uuidMd5 = Md5CoderUtil.len32(uuid);
			String aesKey = Md5CoderUtil.len16(apiKey + uuidMd5.substring(0, 16));
			String token = uuidMd5 + AesCoderUtil.encrypt(Md5CoderUtil.len32(key) + uuid, aesKey);
			token = Base64CoderUtil.encrypt(token);
			
			redisUtil.set(uuid.replace("-", ""), token, TOKEN_EXPIRE_SECONDS);
			
			return token;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 校验token
	 * @param apiKey
	 * @param token
	 * @return
	 */
	public static boolean verifyToken(String apiKey, String token) {
		String decryptUuid = null;
		String lockValue = UUID.randomUUID().toString();
		boolean success = false;
		try {
			String decryptToken = Base64CoderUtil.decrypt(token);
			String aesKey = Md5CoderUtil.len16(apiKey + decryptToken.substring(0, 16));
			decryptToken = AesCoderUtil.decrypt(decryptToken.substring(32), aesKey);
			decryptUuid = decryptToken.substring(32).replace("-", "");
			if (redisUtil.hsetIfNull("verifyTokenLock", decryptUuid, lockValue, TOKEN_EXPIRE_SECONDS)) {
				String savedToken = (String) redisUtil.get(decryptUuid);
				
				if (token.equals(savedToken)) {
					redisUtil.del(decryptUuid);
					success = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		} finally {
			if (decryptUuid != null) {
				String lockedValue = TypeParseUtil.convertToString(redisUtil.hget("verifyTokenLock", decryptUuid));
				
				if (lockedValue.equals(lockValue)) {
					redisUtil.hdel("verifyTokenLock", decryptUuid);
					System.out.println("Token UUID: " + decryptUuid + " is released, and verify result is " + success);
				} else {
					System.out.println("Token UUID: " + decryptUuid + " is locked, reject verify, and verify result is " + success);
				}
			}
		}
		return success;
	}
	
	/**
	 * 删除token
	 * @param apiKey
	 * @param token
	 * @return
	 */
	public static boolean delToken(String apiKey, String token) {
		try {
			String decryptToken = Base64CoderUtil.decrypt(token);
			String aesKey = Md5CoderUtil.len16(apiKey + decryptToken.substring(0, 16));
			decryptToken = AesCoderUtil.decrypt(decryptToken.substring(32), aesKey);
			String decryptUuid = decryptToken.substring(32);
			
			if (redisUtil.hasKey(decryptUuid)) {
				redisUtil.del(decryptUuid);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 获取背景图
	 * @param themeNum 主题编号
	 * @return String 背景图片地址
	 */
	public static String backgroundPath(Integer themeNum) {
		StringBuilder sb = new StringBuilder(BASE_DIR);
		
		List<Integer> dirNums = childDirs(sb.toString());
		if (themeNum == null || !dirNums.contains(themeNum)) {
			themeNum = null;
			if (dirNums.size() > 0) themeNum = dirNums.get(RandomUtil.randomInt(0, dirNums.size()));
		}
		
		if (themeNum == null) {
			return null;
		} else {
			sb.append(File.separator).append(BASE_THEME).append(themeNum);
			
			List<Integer> fileNums = childFiles(sb.toString());
			if (fileNums.size() > 0) {
				Integer backgroundNum = fileNums.get(RandomUtil.randomInt(0, fileNums.size()));
				return sb.append(File.separator).append(BASE_BACKGROUND).append(backgroundNum).append(".").append(BASE_IMG_TYPE).toString();
			} else {
				return null;
			}
		}
	}
	
	/**
	 * 获取背景图片
	 * @param themeNum 主题编号
	 * @param ratio 背景图片缩放比率
	 * @return
	 */
	public static String backgroundPath(Integer themeNum, Double ratio) {
		String filePath = backgroundPath(themeNum);
		
		StringBuilder sb = new StringBuilder();
		Integer dirIndex = filePath.lastIndexOf(File.separator);
		sb.append(filePath.substring(0, dirIndex)).append(File.separator).append(ratio);
		File file = new File(sb.toString());
		if (!file.exists()) {
			file.mkdirs();
		}
		sb.append(filePath.substring(dirIndex));
		file = new File(sb.toString());
		if (file.exists()) {
			return sb.toString();
		} else {
			return ImgCompressUtil.resizeRatio(filePath, DEFAULT_WIDTH, DEFAULT_HEIGHT, ratio, sb.toString());
		}
	}
	
	public static List<String> backgrounds(Integer themeNum, Double ratio) {
		List<String> backgrounds = new ArrayList<>();
		if (themeNum != null) {
			StringBuilder sb = new StringBuilder(BASE_DIR);
			sb.append(File.separator).append(BASE_THEME).append(themeNum);
			String baseBgDir = sb.toString();
			
			File file = new File(baseBgDir);
			if (file.exists()) {
				Pattern pattern = Pattern.compile("^" + BASE_BACKGROUND + "([0-9]*)\\." + BASE_IMG_TYPE + "$", Pattern.CASE_INSENSITIVE);
				Matcher matcher;
				
				sb.append(File.separator).append(ratio);
				String ratioDir = sb.toString();
				File ratioFile = new File(ratioDir);
				if (!ratioFile.exists()) ratioFile.mkdirs();
				
				File[] list = file.listFiles();
				BufferedImage bufferedImage;
				for (File f: list) {
					matcher = pattern.matcher(f.getName());
					if (f.isFile() && matcher.matches()) {
						try {
							String raitoPath = ratioDir + File.separator + f.getName();
							File ratioF = new File(raitoPath);
							if (!ratioF.exists())
								raitoPath = ImgCompressUtil.resizeRatio(f.getAbsolutePath(), DEFAULT_WIDTH, DEFAULT_HEIGHT, ratio, raitoPath);
							bufferedImage = ImageIO.read(new FileInputStream(raitoPath));
							backgrounds.add(ImageUtil.getImageBASE64(bufferedImage));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return backgrounds;
	}
	
	/**
	 * 获取主题总数
	 * @param path
	 * @return
	 */
	public static List<Integer> childDirs(String path) {
		List<Integer> dirNums = new ArrayList<>();
		
		File file = new File(path);
		if (file.exists()) {
			Pattern pattern = Pattern.compile("^" + BASE_THEME + "([0-9]*)$", Pattern.CASE_INSENSITIVE);
			Matcher matcher;
			
			File[] list = file.listFiles();
			for (File dir: list) {
				matcher = pattern.matcher(dir.getName());
				if (dir.isDirectory() && matcher.matches()) {
					dirNums.add(Integer.parseInt(matcher.group(1)));
				}
			}
		}
		
		return dirNums;
	}
	
	/**
	 * 获取主题的背景图片总数
	 * @param path
	 * @return
	 */
	public static List<Integer> childFiles(String path) {
		List<Integer> fileNums = new ArrayList<>();
		
		File file = new File(path);
		if (file.exists()) {
			Pattern pattern = Pattern.compile("^" + BASE_BACKGROUND + "([0-9]*)\\." + BASE_IMG_TYPE + "$", Pattern.CASE_INSENSITIVE);
			Matcher matcher;
			
			File[] list = file.listFiles();
			for (File f: list) {
				matcher = pattern.matcher(f.getName());
				if (f.isFile() && matcher.matches()) {
					fileNums.add(Integer.parseInt(matcher.group(1)));
				}
			}
		}
		
		return fileNums;
	}
	
	/**
	 * 二维数组转Json字符串
	 * @param array
	 * @return
	 */
	public static String arr2JsonStr(Integer[][] array) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < array.length; i++) {
			JSONArray jsonArray2 = new JSONArray(Arrays.asList(array[i]));
			jsonArray.put(jsonArray2);
		}
		return jsonArray.toString();
	}
	
	/**
	 * Json字符串转二维数组
	 * @param jsonStr
	 * @return
	 */
	public static Integer[][] jsonStr2Arr(String jsonStr) {
		Integer[][] array = new Integer[][]{};
		try {
			JSONArray jsonArray = new JSONArray(jsonStr);
			array = new Integer[jsonArray.length()][];
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONArray jsonArray2 = jsonArray.getJSONArray(i);
				Integer[] array2 = new Integer[jsonArray2.length()];
				for (int j = 0; j < jsonArray2.length(); j++) {
					array2[j] = jsonArray2.getInt(j);
				}
				array[i] = array2;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return array;
	}

}
