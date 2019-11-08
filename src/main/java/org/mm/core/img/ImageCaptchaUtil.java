package org.mm.core.img;

import java.util.HashMap;
import java.util.Map;

public class ImageCaptchaUtil {

	private static final Integer DEFAULT_V_CUT = 15;
	private static final Integer DEFAULT_H_CUT = 3;
	private static final Boolean DEFAULT_UPSET = true;
	
	public static Map<String, Object> generate(String canvasPath, int type, int times, Integer vCut, Integer hCut, Boolean upset) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		
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
		
		return resMap;
	}
}
