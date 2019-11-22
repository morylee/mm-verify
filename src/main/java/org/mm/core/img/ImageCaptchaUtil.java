package org.mm.core.img;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.mm.core.security.AesCoderUtil;
import org.mm.core.security.Base64CoderUtil;
import org.mm.core.security.Md5CoderUtil;
import org.mm.core.security.RsaCoderUtil;
import org.mm.core.util.ClientKeyUtil;
import org.mm.core.util.RandomUtil;
import org.mm.core.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageCaptchaUtil {

	public enum CaptchaMode {
		Auto(2), Drag(1), Click(0);
		Integer value;
		
		CaptchaMode(Integer value) {
			this.value = value;
		}
		public Integer getValue() {
			return this.value;
		}
	}
	
	// TODO set from environment config
	private static String BASE_DIR = "D:/personal/image_lib";
	private static String BASE_THEME = "theme";
	private static String BASE_BACKGROUND = "background";
	private static String BASE_IMG_SUFFIX = "png";
	
	public static final Integer DEFAULT_WIDTH = 300;
	public static final Integer DEFAULT_HEIGHT = 160;
	private static final Integer DEFAULT_TIMES = 3;
	private static final Integer DEFAULT_V_CUT = 15;
	private static final Integer DEFAULT_H_CUT = 3;
	private static final Boolean DEFAULT_UPSET = true;
	
	private static final double DRAG_ERROR_RATIO = 0.03;
	private static final double CLICK_ERROR_RATIO = 0.3;
	
	private static final Integer CAPTCHA_EXPIRE_SECONDS = 60 * 60;
	private static final Integer TOKEN_EXPIRE_SECONDS = 60 * 5;
	
	private static final Integer MAX_ERROR_TIMES = 3;
	
	@Autowired
	private RedisUtil redisUtilBean;
	
	private static RedisUtil redisUtil;
	
	@PostConstruct
	public void init() {
		redisUtil = this.redisUtilBean;
	}
	
	/**
	 * 普通方式生成
	 * @param canvasPath
	 * @param type
	 * @param times
	 * @param vCut
	 * @param hCut
	 * @param upset
	 * @return
	 */
	public static Map<String, Object> generate(String canvasPath, Integer type, Integer times, Integer vCut, Integer hCut, Boolean upset) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		if (times == null || times < 0) times = DEFAULT_TIMES;
		if (vCut == null || vCut < 0) vCut = DEFAULT_V_CUT;
		if (hCut == null || hCut < 0) hCut = DEFAULT_H_CUT;
		if (upset == null) upset = DEFAULT_UPSET;
		
		if (type.intValue() == CaptchaMode.Drag.getValue().intValue()) {
			resMap = DragImageUtil.createImage(canvasPath, vCut, hCut, upset);
		} else {
			resMap = ClickImageUtil.createImage(canvasPath, times, vCut, hCut, upset);
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
	 * @return
	 */
	public static Map<String, Object> securityGenerate(String canvasPath, int type, Integer times) {
		Map<String, Object> resMap = generate(canvasPath, type, times, null, null, null);
		boolean success = (Boolean) resMap.get("ok");
		if (success) {
			try {
				String key = (String) resMap.get("key");
				String aesKey = Md5CoderUtil.len16(key);
				Integer iconY = (Integer) resMap.get("iconY");
				Integer cols = (Integer) resMap.get("cols");
				Integer[] series = (Integer[]) resMap.get("series");
				
				Map<String, Object> rsaKeyMap = RsaCoderUtil.genKeyPair();
				String priKey = RsaCoderUtil.getPrivateKey(rsaKeyMap);
				String pubKey = RsaCoderUtil.getPublicKey(rsaKeyMap);
				
				String encryptedPubKey = AesCoderUtil.encrypt(pubKey, aesKey);
//				String encryptedGuide = RsaCoderUtil.priEncrypt(guide, priKey);
				String encryptedCols = RsaCoderUtil.priEncrypt(cols.toString(), priKey);
				String[] encryptedSeries = new String[series.length];
				for (int i = 0; i < series.length; i++) {
					String encryptedS = RsaCoderUtil.priEncrypt(series[i].toString(), priKey);
					encryptedSeries[i] = encryptedS;
				}
				
				resMap.put("rsaPubKey", encryptedPubKey);
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
	 * 检验验证码是否已失效
	 * @param key
	 * @return
	 */
	public static boolean isExpire(String key) {
		if (!redisUtil.hasKey(key)) return true;
		
		Integer usedTimes = (Integer) redisUtil.hget(key, "times");
		if (usedTimes == null) usedTimes = 0;
		if (usedTimes.intValue() < MAX_ERROR_TIMES) {
			usedTimes++;
			redisUtil.hset(key, "times", usedTimes);
		} else {
			redisUtil.del(key);
		}
		
		return usedTimes.intValue() >= MAX_ERROR_TIMES;
	}
	
	/**
	 * 校验验证码
	 * @param key
	 * @param clientPositions
	 * @return
	 */
	public static boolean verify(String key, double[][] clientPositions) {
		Integer type = (Integer) redisUtil.hget(key, "type");
		String positionsStr = (String) redisUtil.hget(key, "positions");
		Integer[][] positions = jsonStr2Arr(positionsStr);
		if (positions.length != clientPositions.length) return false;
		
		int iconWidth = 0;
		int iconHeight = 0;
		double radius = 0;
		boolean iconCore = true;
		switch (type) {
		case 1:
			iconWidth = DragImageUtil.getIconWidth();
			iconHeight = DragImageUtil.getIconHeight();
			radius = iconWidth * DRAG_ERROR_RATIO;
			iconCore = false;
			break;
		default:
			iconWidth = ClickImageUtil.getIconWidth();
			iconHeight = ClickImageUtil.getIconHeight();
			radius = iconWidth * CLICK_ERROR_RATIO;
			break;
		}
		
		for (int i = 0; i < positions.length; i++) {
			Integer[] position = positions[i];
			double[] clientPos = clientPositions[i];
			if (position.length != clientPos.length) return false;
			
			int x = position[0];
			int y = position[1];
			int coreX = iconCore ? x + iconWidth / 2 : x;
			int coreY = iconCore ? y + iconHeight / 2 : y;
			
			double clientX = clientPos[0];
			double clientY = clientPos[1];
			
			if (clientX >= x && clientX <= x + iconWidth && clientY >= y && clientY <= y + iconHeight) {
				double clientWidth = clientX - coreX;
				double clientHeight = clientY - coreY;
				if (clientWidth * clientWidth + clientHeight * clientHeight > radius * radius) {
					return false;
				}
			} else {
				return false;
			}
		}
		redisUtil.del(key);
		
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
			e.printStackTrace();
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
			
			redisUtil.set(uuid, token, TOKEN_EXPIRE_SECONDS);
			
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
		try {
			String decryptToken = Base64CoderUtil.decrypt(token);
			String aesKey = Md5CoderUtil.len16(apiKey + decryptToken.substring(0, 16));
			decryptToken = AesCoderUtil.decrypt(decryptToken.substring(32), aesKey);
			String decryptUuid = decryptToken.substring(32);
			String savedToken = (String) redisUtil.get(decryptUuid);
			
			if (token.equals(savedToken)) {
				redisUtil.del(decryptUuid);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 获取背景图
	 * @param themeNum 主题编号
	 * @param backgroundNum 背景图片编号
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
				return sb.append(File.separator).append(BASE_BACKGROUND).append(backgroundNum).append(".").append(BASE_IMG_SUFFIX).toString();
			} else {
				return null;
			}
		}
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
			Pattern pattern = Pattern.compile("^" + BASE_BACKGROUND + "([0-9]*)\\." + BASE_IMG_SUFFIX + "$", Pattern.CASE_INSENSITIVE);
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
