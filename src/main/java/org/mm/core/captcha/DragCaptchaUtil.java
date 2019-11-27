package org.mm.core.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.mm.core.util.ArrayUtil;
import org.mm.core.util.ImageUtil;
import org.mm.core.util.RandomUtil;

public class DragCaptchaUtil {

	private static final int ICON_WIDTH = 60;
	private static final int ICON_HEIGHT = 60;
	private static final int CIRCLE_RADIUS = 7;
	private static final int RECTANGLE_PADDING = 9;
	private static final int SLIDER_IMG_OUT_PADDING = 2;
	
	public static final int getIconWidth() {
		return ICON_WIDTH;
	}
	
	public static final int getIconHeight() {
		return ICON_HEIGHT;
	}

	private static class CutPosParams {
		int x, y;
	}
	
	/**
	 * 生成随机滑块坐标
	 * @param width
	 * @param height
	 * @return
	 */
	private static CutPosParams getCutPosition (int width, int height) {
		CutPosParams params = new CutPosParams();
		params.x = RandomUtil.randomInt(width / 2, width - ICON_WIDTH);
		params.y = RandomUtil.randomInt(0, height - ICON_HEIGHT);
		
		return params;
	}
	
	/**
	 * 生成随机滑块形状
	 * 
	 * 0 透明像素
	 * 1 滑块像素
	 * 2 阴影像素
	 *
	 * @return
	 */
	private static int[][] getBlockData() {
		int[][] data = new int[ICON_WIDTH][ICON_HEIGHT];

		//(x-a)²+(y-b)²=r²
		//x中心位置左右5像素随机
		double x1 = RECTANGLE_PADDING + (ICON_WIDTH - 2 * RECTANGLE_PADDING) / 2.0 - 5 + RandomUtil.randomInt(0, 11);
		//y 矩形上边界半径-1像素移动
		double y1_top = RECTANGLE_PADDING - RandomUtil.randomInt(0, 3);
		double y1_bottom = ICON_HEIGHT - RECTANGLE_PADDING + RandomUtil.randomInt(0, 3);
		double y1 = RandomUtil.randomInt(0, 2) == 1 ? y1_top : y1_bottom;


		double x2_right = ICON_WIDTH - RECTANGLE_PADDING - CIRCLE_RADIUS + RandomUtil.randomInt(0, 2 * CIRCLE_RADIUS - 3);
		double x2_left = RECTANGLE_PADDING + CIRCLE_RADIUS - 2 - RandomUtil.randomInt(0, 2 * CIRCLE_RADIUS - 3);
		double x2 = RandomUtil.randomInt(0, 2) == 1 ? x2_right : x2_left;
		double y2 = RECTANGLE_PADDING + (ICON_HEIGHT - 2 * RECTANGLE_PADDING) / 2.0 - 5 + RandomUtil.randomInt(0, 11);

		double po = CIRCLE_RADIUS * CIRCLE_RADIUS;
		for (int i = 0; i < ICON_WIDTH; i++) {
			for (int j = 0; j < ICON_HEIGHT; j++) {
				//矩形区域
				boolean fill;
				if ((i >= RECTANGLE_PADDING && i < ICON_WIDTH - RECTANGLE_PADDING)
						&& (j >= RECTANGLE_PADDING && j < ICON_HEIGHT - RECTANGLE_PADDING)) {
					data[i][j] = 1;
					fill = true;
				} else {
					data[i][j] = 0;
					fill = false;
				}
				//凸出区域
				double d3 = Math.pow(i - x1, 2) + Math.pow(j - y1, 2);
				if (d3 < po) {
					data[i][j] = 1;
				} else {
					if (!fill) {
						data[i][j] = 0;
					}
				}
				//凹进区域
				double d4 = Math.pow(i - x2, 2) + Math.pow(j - y2, 2);
				if (d4 < po) {
					data[i][j] = 0;
				}
			}
		}

		//边界阴影
		for (int i = 0; i < ICON_WIDTH; i++) {
			for (int j = 0; j < ICON_HEIGHT; j++) {
				//四个正方形边角处理
				for (int k = 1; k <= SLIDER_IMG_OUT_PADDING; k++) {
					//左上、左下
					if (i >= RECTANGLE_PADDING - k && i < RECTANGLE_PADDING
							&& ((j >= RECTANGLE_PADDING - k && j < RECTANGLE_PADDING)
							|| (j >= ICON_HEIGHT - RECTANGLE_PADDING - k && j < ICON_HEIGHT - RECTANGLE_PADDING + 1))) {
						data[i][j] = 2;
					}

					//右上、右下
					if (i >= ICON_WIDTH - RECTANGLE_PADDING + k - 1 && i < ICON_WIDTH - RECTANGLE_PADDING + 1) {
						for (int n = 1; n <= SLIDER_IMG_OUT_PADDING; n++) {
							if (((j >= RECTANGLE_PADDING - n && j < RECTANGLE_PADDING)
									|| (j >= ICON_HEIGHT - RECTANGLE_PADDING - n && j <= ICON_HEIGHT - RECTANGLE_PADDING ))) {
								data[i][j] = 2;
							}
						}
					}
				}

				if (data[i][j] == 1 && j - SLIDER_IMG_OUT_PADDING > 0 && data[i][j - SLIDER_IMG_OUT_PADDING] == 0) {
					data[i][j - SLIDER_IMG_OUT_PADDING] = 2;
				}
				if (data[i][j] == 1 && j + SLIDER_IMG_OUT_PADDING > 0 && j + SLIDER_IMG_OUT_PADDING < ICON_HEIGHT && data[i][j + SLIDER_IMG_OUT_PADDING] == 0) {
					data[i][j + SLIDER_IMG_OUT_PADDING] = 2;
				}
				if (data[i][j] == 1 && i - SLIDER_IMG_OUT_PADDING > 0 && data[i - SLIDER_IMG_OUT_PADDING][j] == 0) {
					data[i - SLIDER_IMG_OUT_PADDING][j] = 2;
				}
				if (data[i][j] == 1 && i + SLIDER_IMG_OUT_PADDING > 0 && i + SLIDER_IMG_OUT_PADDING < ICON_WIDTH && data[i + SLIDER_IMG_OUT_PADDING][j] == 0) {
					data[i + SLIDER_IMG_OUT_PADDING][j] = 2;
				}
			}
		}

		return data;
	}
	
	/**
	 * 裁剪区块
	 *
	 * @param oriImage    原图
	 * @param targetImage 裁剪图
	 * @param blockImage  滑块
	 * @param x           裁剪点x
	 * @param y           裁剪点y
	 */
	private static void cutImg(BufferedImage oriImage, BufferedImage targetImage, int[][] blockImage, int x, int y) {
		for (int i = 0; i < ICON_WIDTH; i++) {
			for (int j = 0; j < ICON_HEIGHT; j++) {
				int _x = x + i;
				int _y = y + j;
				int rgb = blockImage[i][j];
				int rgbOri = oriImage.getRGB(_x, _y);
				// 原图中对应位置变色处理
				if (rgb == 1) {
					//抠图上复制对应颜色值
					targetImage.setRGB(i, j, rgbOri);
					//原图对应位置颜色变化
					oriImage.setRGB(_x, _y, Color.WHITE.getRGB());
				} else if (rgb == 2) {
					targetImage.setRGB(i, j, Color.WHITE.getRGB());
					oriImage.setRGB(_x, _y, Color.GRAY.getRGB());
				}
			}
		}
	}
	
	/**
	 * 获取主图，icon
	 * @param url
	 * @param width
	 * @param height
	 * @return Map<String, Object> {
	 *     background: 主图 BufferedImage,
	 *     guide: icon BufferedImage,
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	private static Map<String, Object> doCreateImage(String url) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(url));
			CutPosParams params = getCutPosition(bufferedImage.getWidth(), bufferedImage.getHeight());
			BufferedImage target= new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
			cutImg(bufferedImage, target, getBlockData(), params.x, params.y);
			resultMap.put("background", bufferedImage); // 主图
			resultMap.put("guide", target);             // icon
			Integer[][] positions = new Integer[][]{{params.x, params.y}};
			resultMap.put("positions", positions);      // icon在主图的坐标
			resultMap.put("times", 1);                  // 操作次数
		} catch (IOException e) {
			resultMap.clear();
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 
	 * 获取主图，iconBase64码
	 * @param url
	 * @param width
	 * @param height
	 * @return Map<String, Object> {
	 *     background: 主图 Base64,
	 *     guide: icon Base64,
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	public static Map<String, Object> createImage(String url){
		Map<String, Object> resultMap = doCreateImage(url);
		resultMap.put("background", ImageUtil.getImageBASE64((BufferedImage) resultMap.get("background"))); // 主图
		resultMap.put("guide", ImageUtil.getImageBASE64((BufferedImage) resultMap.get("guide")));           // icon
		
		return resultMap;
	}
	
	/**
	 * 获取打乱的主图
	 * @param url
	 * @param width
	 * @param height
	 * @param vCut
	 * @param hCut
	 * @param upset
	 * @return Map<String, Object> {
	 *     background: [主图分片 Base64],
	 *     guide: icon Base64,
	 *     series: [主图分片顺序],
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	public static Map<String, Object> createImage(String url, int vCut, int hCut, boolean upset){
		Map<String, Object> resultMap = doCreateImage(url);
		Integer[] upsetSeries = null;
		if (upset) upsetSeries = ArrayUtil.upsetNumbers(vCut * hCut);

		resultMap.put("background",
			ImageUtil.imagePieces((BufferedImage) resultMap.get("background"), vCut, hCut, upsetSeries)); // 主图
		resultMap.put("guide", ImageUtil.getImageBASE64((BufferedImage) resultMap.get("guide")));         // icon
		resultMap.put("series", upsetSeries);                                                             // 主图打乱的顺序
		return resultMap;
	}

}
