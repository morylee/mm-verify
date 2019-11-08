package org.mm.core.img;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class DragImageUtil {

	private static final int CUT_WIDTH = 100;
	private static final int CUT_HEIGHT = 100;
	private static final int circleR = 12;
	private static final int RECTANGLE_PADDING = 15;
	private static final int SLIDER_IMG_OUT_PADDING = 1;

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
		params.x = RandomUtil.randomInt(width / 2, width - CUT_WIDTH);
		params.y = RandomUtil.randomInt(0, height - CUT_HEIGHT);
		
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
		int[][] data = new int[CUT_WIDTH][CUT_HEIGHT];

		//(x-a)²+(y-b)²=r²
		//x中心位置左右5像素随机
		double x1 = RECTANGLE_PADDING + (CUT_WIDTH - 2 * RECTANGLE_PADDING) / 2.0 - 5 + RandomUtil.randomInt(0, 11);
		//y 矩形上边界半径-1像素移动
		double y1_top = RECTANGLE_PADDING - RandomUtil.randomInt(0, 3);
		double y1_bottom = CUT_HEIGHT - RECTANGLE_PADDING + RandomUtil.randomInt(0, 3);
		double y1 = RandomUtil.randomInt(0, 2) == 1 ? y1_top : y1_bottom;


		double x2_right = CUT_WIDTH - RECTANGLE_PADDING - circleR + RandomUtil.randomInt(0, 2 * circleR - 3);
		double x2_left = RECTANGLE_PADDING + circleR - 2 - RandomUtil.randomInt(0, 2 * circleR - 3);
		double x2 = RandomUtil.randomInt(0, 2) == 1 ? x2_right : x2_left;
		double y2 = RECTANGLE_PADDING + (CUT_HEIGHT - 2 * RECTANGLE_PADDING) / 2.0 - 5 + RandomUtil.randomInt(0, 11);

		double po = circleR * circleR;
		for (int i = 0; i < CUT_WIDTH; i++) {
			for (int j = 0; j < CUT_HEIGHT; j++) {
				//矩形区域
				boolean fill;
				if ((i >= RECTANGLE_PADDING && i < CUT_WIDTH - RECTANGLE_PADDING)
						&& (j >= RECTANGLE_PADDING && j < CUT_HEIGHT - RECTANGLE_PADDING)) {
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
		for (int i = 0; i < CUT_WIDTH; i++) {
			for (int j = 0; j < CUT_HEIGHT; j++) {
				//四个正方形边角处理
				for (int k = 1; k <= SLIDER_IMG_OUT_PADDING; k++) {
					//左上、左下
					if (i >= RECTANGLE_PADDING - k && i < RECTANGLE_PADDING
							&& ((j >= RECTANGLE_PADDING - k && j < RECTANGLE_PADDING)
							|| (j >= CUT_HEIGHT - RECTANGLE_PADDING - k && j < CUT_HEIGHT - RECTANGLE_PADDING + 1))) {
						data[i][j] = 2;
					}

					//右上、右下
					if (i >= CUT_WIDTH - RECTANGLE_PADDING + k - 1 && i < CUT_WIDTH - RECTANGLE_PADDING + 1) {
						for (int n = 1; n <= SLIDER_IMG_OUT_PADDING; n++) {
							if (((j >= RECTANGLE_PADDING - n && j < RECTANGLE_PADDING)
									|| (j >= CUT_HEIGHT - RECTANGLE_PADDING - n && j <= CUT_HEIGHT - RECTANGLE_PADDING ))) {
								data[i][j] = 2;
							}
						}
					}
				}

				if (data[i][j] == 1 && j - SLIDER_IMG_OUT_PADDING > 0 && data[i][j - SLIDER_IMG_OUT_PADDING] == 0) {
					data[i][j - SLIDER_IMG_OUT_PADDING] = 2;
				}
				if (data[i][j] == 1 && j + SLIDER_IMG_OUT_PADDING > 0 && j + SLIDER_IMG_OUT_PADDING < CUT_HEIGHT && data[i][j + SLIDER_IMG_OUT_PADDING] == 0) {
					data[i][j + SLIDER_IMG_OUT_PADDING] = 2;
				}
				if (data[i][j] == 1 && i - SLIDER_IMG_OUT_PADDING > 0 && data[i - SLIDER_IMG_OUT_PADDING][j] == 0) {
					data[i - SLIDER_IMG_OUT_PADDING][j] = 2;
				}
				if (data[i][j] == 1 && i + SLIDER_IMG_OUT_PADDING > 0 && i + SLIDER_IMG_OUT_PADDING < CUT_WIDTH && data[i + SLIDER_IMG_OUT_PADDING][j] == 0) {
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
		for (int i = 0; i < CUT_WIDTH; i++) {
			for (int j = 0; j < CUT_HEIGHT; j++) {
				int _x = x + i;
				int _y = y + j;
				int rgb = blockImage[i][j];
				int rgb_ori = oriImage.getRGB(_x, _y);
				// 原图中对应位置变色处理
				if (rgb == 1) {
					//抠图上复制对应颜色值
					targetImage.setRGB(i, j, rgb_ori);
					//原图对应位置颜色变化
					oriImage.setRGB(_x, _y, Color.LIGHT_GRAY.getRGB());
				} else if (rgb == 2) {
					targetImage.setRGB(i, j, Color.WHITE.getRGB());
					oriImage.setRGB(_x, _y, Color.GRAY.getRGB());
				}
			}
		}
	}
	
	/**
	 * 获取大图，小图
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	private static Map<String, Object> doCreateImage(String url) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(url));
			CutPosParams params = getCutPosition(bufferedImage.getWidth(), bufferedImage.getHeight());
			BufferedImage target= new BufferedImage(CUT_WIDTH, CUT_HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
			cutImg(bufferedImage, target, getBlockData(), params.x, params.y);
			resultMap.put("background", bufferedImage); // 大图
			resultMap.put("guide", target); // 小图
			int[][] positions = new int[][]{{params.x, params.y}};
			resultMap.put("p", positions);
			resultMap.put("n", 1);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 
	 * 获取大图，小图Base64码
	 * @param url
	 * @param width
	 * @param height
	 * @return Map<String, Object>
	 * @throws
	 */
	public static Map<String, Object> createImage(String url){
		Map<String, Object> resultMap = doCreateImage(url);
		resultMap.put("background", ImageUtil.getImageBASE64((BufferedImage) resultMap.get("background"))); // 大图
		resultMap.put("guide", ImageUtil.getImageBASE64((BufferedImage) resultMap.get("guide"))); // 小图
		
		return resultMap;
	}
	
	/**
	 * 获取打乱的大图
	 * @param url
	 * @param width
	 * @param height
	 * @param vCut
	 * @param hCut
	 * @param upset
	 * @return
	 */
	public static Map<String, Object> createImage(String url, int vCut, int hCut, boolean upset){
		Map<String, Object> resultMap = doCreateImage(url);
		Integer[] upsetSeries = null;
		if (upset) upsetSeries = ImageUtil.upsetNumbers(vCut * hCut);

		resultMap.put("background", ImageUtil.imagePieces((BufferedImage) resultMap.get("background"), vCut, hCut, upsetSeries)); // 大图
		resultMap.put("guide", ImageUtil.getImageBASE64((BufferedImage) resultMap.get("guide"))); // 小图
		resultMap.put("s", upsetSeries);
		return resultMap;
	}
	
	public static void main(String[] args) {
		Map<String, Object> res = createImage("D:\\personal\\images\\131.jpg");
		String background = (String) res.get("background");
		String guide = (String) res.get("guide");
		System.out.println("原图");
		System.out.println("data:image/png;base64," + background);
		System.out.println("小图");
		System.out.println("data:image/png;base64," + guide);
//		ImageUtil.BASE64ToImage(background, "D:\\personal\\images\\131_origin.png");
//		ImageUtil.BASE64ToImage(guide, "D:\\personal\\images\\131_min.png");
		
//		getCutPosition(500, 300);
	}
}
