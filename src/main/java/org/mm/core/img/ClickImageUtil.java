package org.mm.core.img;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.mm.core.util.ArrayUtil;
import org.mm.core.util.RandomUtil;

public class ClickImageUtil {

	private static final int ICON_WIDTH = 30;
	private static final int ICON_HEIGHT = 30;
	
	public static final int getIconWidth() {
		return ICON_WIDTH;
	}
	
	public static final int getIconHeight() {
		return ICON_HEIGHT;
	}
	
	enum Icon {
		Star(0, "星星", "/images/starIcon.png"), Triangle(1, "三角形", "/images/triangleIcon.png"),
		Square(2, "正方形", "/images/squareIcon.png"), Circular(3, "圆形", "/images/circularIcon.png"),
		Cloud(4, "云朵", "/images/cloudIcon.png"), Rhombus(5, "菱形", "/images/rhombusIcon.png"),
		Heart(6, "心形", "/images/heartIcon.png");
		
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
	
	public static void drawImg(BufferedImage oriImage, BufferedImage[] icons, Integer[][] posXY) {
		if (posXY == null || posXY.length != icons.length) {
			throw new RuntimeException("positions size not equal icons size");
		}
		
		BufferedImage icon;
		int w, h, x, y, rgbIcon;
		for (int iconInd = 0; iconInd < icons.length; iconInd++) {
			icon = icons[iconInd];
			w = posXY[iconInd][0];
			h = posXY[iconInd][1];
			for (int i = 0; i < icon.getWidth(); i++) {
				for (int j = 0; j < icon.getHeight(); j++) {
					x = w + i;
					y = h + j;
					rgbIcon = icon.getRGB(i, j);
					if ((rgbIcon & 0xFF000000) < 0) {
						oriImage.setRGB(x, y, rgbIcon);
					}
				}
			}
		}
	}
	
	public static Integer[][] getPositions(int w, int h, int n) {
		if (5 * n * ICON_WIDTH * ICON_HEIGHT > w * h) {
			throw new RuntimeException("background image too small");
		}
		
		Integer[][] positions = new Integer[n][2];
		
		boolean tooClose;
		int posX, posY, px, py;
		Integer[] position;
		for (int i = 0; i < n; i++) {
			tooClose = false;
			do {
				posX = RandomUtil.randomInt(10, w - 10 - ICON_WIDTH);
				posY = RandomUtil.randomInt(10, h - 10 - ICON_HEIGHT);
				for (int j = 0; j < positions.length; j++) {
					position = positions[j];
					px = position[0] == null ? 0 : position[0];
					py = position[1] == null ? 0 : position[1];
					if (px - ICON_WIDTH <= posX && px + ICON_WIDTH >= posX
						&& py - ICON_HEIGHT <= posY && py + ICON_HEIGHT >= posY) {
						tooClose = true;
						break;
					}
				}
				if (!tooClose) positions[i] = new Integer[]{posX, posY};
			} while (tooClose);
		}
		
		return positions;
	}
	
	public static Icon[] getIcons(int n) {
		int iconTotal = Icon.values().length;
		Integer[] upsetNumbers = ArrayUtil.upsetNumbers(iconTotal);
		if (n > iconTotal) n = iconTotal;
		Icon[] icons = new Icon[n];
		for (int i = 0; i < n; i++) {
			icons[i] = Icon.valueOf(upsetNumbers[i]);
		}
		
		return icons;
	}
	
	public static BufferedImage getIconImage(Icon icon) throws IOException {
		BufferedImage iconImg = ImageIO.read(ImageUtil.class.getResourceAsStream(icon.imgName));
		if (iconImg.getWidth() != ICON_WIDTH || iconImg.getHeight() != ICON_HEIGHT) {
			return ImageUtil.scaleImage(iconImg, ICON_WIDTH);
		} else {
			return iconImg;
		}
	}
	
	/**
	 * 创建图片
	 * @param url
	 * @param n
	 * @return Map<String, Object> {
	 *     background: 主图 BufferedImage,
	 *     guide: 提示文字,
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	private static Map<String, Object> doCreateImage(String url, int n) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(url));
			
			Icon[] icons = getIcons(n);
			n = icons.length;
			BufferedImage[] iconImgs = new BufferedImage[n];
			StringBuffer sb = new StringBuffer();
			sb.append("请依次点击：");
			Icon icon;
			for (int i = 0; i < n; i++) {
				icon = icons[i];
				iconImgs[i] = getIconImage(icon);
				sb.append(icon.name);
				if (i < n - 1) sb.append(",");
			}
			Integer[][] posXY = getPositions(bufferedImage.getWidth(), bufferedImage.getHeight(), n);
			drawImg(bufferedImage, iconImgs, posXY);
			
			resultMap.put("background", bufferedImage); // 主图
			if (posXY == null || posXY.length != iconImgs.length) {
				throw new RuntimeException("draw image falied");
			} else {
				resultMap.put("guide", sb.toString()); // 操作提示
				resultMap.put("times", posXY.length); // 操作次数
				resultMap.put("positions", posXY); // icon在主图的坐标
			}
		} catch (IOException e) {
			resultMap.clear();
			e.printStackTrace();
		}
		
		return resultMap;
	}
	
	/**
	 * 创建图片Base64
	 * @param url
	 * @param n
	 * @return Map<String, Object> {
	 *     background: 主图 Base64,
	 *     guide: 提示文字,
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
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
	 * @return Map<String, Object> {
	 *     background: [主图分片 Base64],
	 *     guide: 提示文字,
	 *     series: [主图分片顺序],
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	public static Map<String, Object> createImage(String url, int n, int vCut, int hCut, boolean upset) {
		Map<String, Object> resultMap = doCreateImage(url, n);
		Integer[] upsetSeries = null;
		if (upset) upsetSeries = ArrayUtil.upsetNumbers(vCut * hCut);

		resultMap.put("background", ImageUtil.imagePieces((BufferedImage) resultMap.get("background"), vCut, hCut, upsetSeries));
		resultMap.put("series", upsetSeries); // 主图打乱的顺序
		return resultMap;
	}

}
