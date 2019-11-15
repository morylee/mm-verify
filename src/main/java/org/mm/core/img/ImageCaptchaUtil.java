package org.mm.core.img;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.mm.core.security.AesCoderUtil;
import org.mm.core.security.Md5CoderUtil;
import org.mm.core.security.RsaCoderUtil;
import org.mm.core.util.ClientKeyUtil;
import org.mm.core.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ImageCaptchaUtil {

	private static final Integer DEFAULT_TIMES = 3;
	private static final Integer DEFAULT_V_CUT = 15;
	private static final Integer DEFAULT_H_CUT = 3;
	private static final Boolean DEFAULT_UPSET = true;
	
	private static final double DRAG_ERROR_RATIO = 0.03;
	private static final double CLICK_ERROR_RATIO = 0.3;
	
	@Autowired
	private RedisUtil redisUtilBean;
	
	private static RedisUtil redisUtil;
	
	@PostConstruct
	public void init() {
		redisUtil = this.redisUtilBean;
	}
	
	public static Map<String, Object> generate(String canvasPath, int type, Integer times, Integer vCut, Integer hCut, Boolean upset) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		
		if (times == null || times < 0) times = DEFAULT_TIMES;
		if (vCut == null || vCut < 0) vCut = DEFAULT_V_CUT;
		if (hCut == null || hCut < 0) hCut = DEFAULT_H_CUT;
		if (upset == null) upset = DEFAULT_UPSET;
		
		switch (type) {
		case 1:
			resMap = DragImageUtil.createImage(canvasPath, vCut, hCut, upset);
			break;
		default:
			resMap = ClickImageUtil.createImage(canvasPath, times, vCut, hCut, upset);
			break;
		}
		if (resMap.isEmpty()) {
			resMap.put("ok", false);
		} else {
			resMap.put("ok", true);
			String captchaKey = ClientKeyUtil.captchaKey();
			resMap.put("key", captchaKey);
			
			Integer[][] positions = (Integer[][]) resMap.get("positions");
			resMap.remove("positions");
			String positionsStr = arr2JsonStr(positions);
			redisUtil.hset(captchaKey, "positions", positionsStr);
		}
		
		return resMap;
	}
	
	public static Map<String, Object> securityGenerate(String canvasPath, int type, Integer times) {
		Map<String, Object> resMap = generate(canvasPath, type, times, null, null, null);
		boolean success = (Boolean) resMap.get("ok");
		if (success) {
			try {
				String key = (String) resMap.get("key");
				String aesKey = Md5CoderUtil.len16(key);
				Integer[] series = (Integer[]) resMap.get("series");
				String guide = (String) resMap.get("guide");
				
				Map<String, Object> rsaKeyMap = RsaCoderUtil.genKeyPair();
				String priKey = RsaCoderUtil.getPrivateKey(rsaKeyMap);
				String pubKey = RsaCoderUtil.getPublicKey(rsaKeyMap);
				String encryptedPubKey = AesCoderUtil.encrypt(pubKey, aesKey);
				String encryptedGuide = RsaCoderUtil.priEncrypt(guide, priKey);
				String[] encryptedSeries = new String[series.length];
				for (int i = 0; i < series.length; i++) {
					String encryptedS = RsaCoderUtil.priEncrypt(series[i].toString(), priKey);
					encryptedSeries[i] = encryptedS;
				}
				
				redisUtil.hset(key, "rsaPriKey", priKey);
				resMap.put("rsaPubKey", encryptedPubKey);
				resMap.put("guide", encryptedGuide);
				resMap.put("series", encryptedSeries);
			} catch (Exception e) {
				resMap.clear();
				resMap.put("ok", false);
				e.printStackTrace();
			}
		}
		
		return resMap;
	}
	
	public static boolean verify(String key, int type, double[][] clientPositions) {
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
	
	public static boolean securityVerify(String key, int type, String[][] clientPositions) {
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
			
			return verify(key, type, clientPoss);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static String arr2JsonStr(Integer[][] array) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < array.length; i++) {
			JSONArray jsonArray2 = new JSONArray(Arrays.asList(array[i]));
			jsonArray.put(jsonArray2);
		}
		return jsonArray.toString();
	}
	
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
