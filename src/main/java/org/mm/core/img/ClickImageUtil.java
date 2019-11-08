package org.mm.core.img;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;


public class ClickImageUtil {

	enum Icon {
		Star(0, "星星", "/starIcon.png"), Triangle(1, "三角形", "/triangleIcon.png"),
		Square(2, "正方形", "/squareIcon.png"), Circular(3, "圆形", "/circularIcon.png"),
		Cloud(4, "云朵", "/cloudIcon.png"), Rhombus(5, "菱形", "/rhombusIcon.png"),
		Heart(6, "心形", "/heartIcon.png");
		
		int value;
		String name;
		String imgName;
		
		private Icon(int value, String name, String imgName) {
			this.value = value;
			this.name = name;
			this.imgName = imgName;
		}
		
		public static Icon valueOf(int value) {
			switch (value) {
				case 1:
					return Triangle;
				case 2:
					return Square;
				case 3:
					return Circular;
				case 4:
					return Cloud;
				case 5:
					return Rhombus;
				case 6:
					return Heart;
				default:
					return Star;
			}
		}
	}
	
	public static void drawImg(BufferedImage oriImage, BufferedImage[] icons, int[][] posXY) {
		if (posXY == null || posXY.length != icons.length) return;
		
		for (int iconInd = 0; iconInd < icons.length; iconInd++) {
			BufferedImage icon = icons[iconInd];
			int w = posXY[iconInd][0];
			int h = posXY[iconInd][1];
			for (int i = 0; i < icon.getWidth(); i++) {
				for (int j = 0; j < icon.getHeight(); j++) {
					int x = w + i;
					int y = h + j;
					int rgb_icon = icon.getRGB(i, j);
					if ((rgb_icon & 0xFF000000) < 0) {
						oriImage.setRGB(x, y, rgb_icon);
					}
				}
			}
		}
	}
	
	// TODO check background img size
	public static int[][] getPositions(int w, int h, int[][] iconSizes) {
		int iconTotalArea = 0;
		for (int[] iconSize: iconSizes) {
			iconTotalArea += iconSize[0] * iconSize[1];
		}
		if (iconTotalArea * 5 > w * h) return null;
		
		int[][] positions = new int[iconSizes.length][2];
		
		for (int i = 0; i < iconSizes.length; i++) {
			int[] iconSize = iconSizes[i];
			int iconW = iconSize[0];
			int iconH = iconSize[1];
			boolean tooClose = false;
			do {
				int posX = RandomUtil.randomInt(10, w - 10 - iconW);
				int posY = RandomUtil.randomInt(10, h - 10 - iconH);
				for (int j = 0; j < positions.length; j++) {
					int[] position = positions[j];
					if (position[0] - iconW <= posX && position[0] + iconSizes[j][0] >= posX
						&& position[1] - iconH <= posY && position[1] + iconSizes[j][1] >= posY) {
						tooClose = true;
						break;
					}
				}
				if (!tooClose) positions[i] = new int[]{posX, posY};
			} while (tooClose);
		}
		
		return positions;
	}
	
	public static Icon[] getIcons(int n) {
		int iconTotal = Icon.values().length;
		Integer[] upsetNumbers = ImageUtil.upsetNumbers(iconTotal);
		if (n > iconTotal) n = iconTotal;
		Icon[] icons = new Icon[n];
		for (int i = 0; i < n; i++) {
			icons[i] = Icon.valueOf(upsetNumbers[i]);
		}
		
		return icons;
	}
	
	/**
	 * 创建图片
	 * @param url
	 * @param n
	 * @return
	 */
	private static Map<String, Object> doCreateImage(String url, int n) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(url));
			Icon[] icons = getIcons(n);
			n = icons.length;
			BufferedImage[] iconImgs = new BufferedImage[n];
			int[][] iconSizes = new int[n][2];
			StringBuffer sb = new StringBuffer();
			sb.append("请依次点击：");
			for (int i = 0; i < n; i++) {
				Icon icon = icons[i];
				BufferedImage iconImg = ImageIO.read(ImageUtil.class.getResourceAsStream(icon.imgName));
				iconImgs[i] = iconImg;
				iconSizes[i][0] = iconImg.getWidth();
				iconSizes[i][1] = iconImg.getHeight();
				sb.append(icon.name);
				if (i < n - 1) sb.append(",");
			}
			int[][] posXY = getPositions(bufferedImage.getWidth(), bufferedImage.getHeight(), iconSizes);
			drawImg(bufferedImage, iconImgs, posXY);
			
			resultMap.put("background", bufferedImage);
			if (posXY == null || posXY.length != iconImgs.length) {
				resultMap.put("guide", "生成失败");
				resultMap.put("n", n);
			} else {
				resultMap.put("guide", sb.toString());
				resultMap.put("p", posXY);
				resultMap.put("n", posXY.length);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	/**
	 * 创建图片Base64
	 * @param url
	 * @param n
	 * @return
	 */
	public static Map<String, Object> createImage(String url, int n) {
		Map<String, Object> resultMap = doCreateImage(url, n);
		resultMap.put("background", ImageUtil.getImageBASE64((BufferedImage) resultMap.get("background")));
		
		return resultMap;
	}
	
	/**
	 * 打乱图片
	 * @param url
	 * @param n
	 * @param vCut
	 * @param hCut
	 * @param upset
	 * @return
	 */
	public static Map<String, Object> createImage(String url, int n, int vCut, int hCut, boolean upset) {
		Map<String, Object> resultMap = doCreateImage(url, n);
		Integer[] upsetSeries = null;
		if (upset) upsetSeries = ImageUtil.upsetNumbers(vCut * hCut);

		resultMap.put("background", ImageUtil.imagePieces((BufferedImage) resultMap.get("background"), vCut, hCut, upsetSeries));
		resultMap.put("s", upsetSeries);
		return resultMap;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Map<String, Object> resultMap = doCreateImage("D:/personal/images/captcha/131.png", 3);
		ImageIO.write((BufferedImage) resultMap.get("background"), "png", new File("D:/personal/images/captcha/131_copy.png"));
	}
}
