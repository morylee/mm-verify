package org.mm.core.captcha;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.coobird.thumbnailator.Thumbnails;
import org.mm.core.util.ArrayUtil;
import org.mm.core.util.ImageUtil;
import org.mm.core.util.RandomUtil;

public class ClickCaptchaUtil {

	private static final int ICON_WIDTH = 30 * CaptchaUtil.BACKGROUND_RESIZE;
	private static final int ICON_HEIGHT = 30 * CaptchaUtil.BACKGROUND_RESIZE;
	private static final int ICON_X_PADDING = 40 * CaptchaUtil.BACKGROUND_RESIZE;
	private static final int ICON_Y_PADDING = 40 * CaptchaUtil.BACKGROUND_RESIZE;
	private static final int WHITE_COLOR_NUM = 192;
	private static final int ICON_TO_BACKGROUND_BORDER = 10 * CaptchaUtil.BACKGROUND_RESIZE;
	private static final int MAX_OPERATE_TIMES = 3;

	public static final int getIconWidth() {
		return ICON_WIDTH / CaptchaUtil.BACKGROUND_RESIZE;
	}
	
	public static final int getIconHeight() {
		return ICON_HEIGHT / CaptchaUtil.BACKGROUND_RESIZE;
	}

	enum Icon {
		/** icons */
		Circular(0, 0, "圆形", "/images/circularIcon.png"), Cloud(1, 0, "云朵", "/images/cloudIcon.png"),
		Heart(2, 0, "心形", "/images/heartIcon.png"), Hexagon(3, 0, "六边形", "/images/hexagonIcon.png"),
		Lighting(4, 0, "闪电", "/images/lightingIcon.png"), Moon(5, 0, "月亮", "/images/moonIcon.png"),
		Pentagon(6, 0, "五边形", "/images/pentagonIcon.png"), Square(7, 0, "方形", "/images/squareIcon.png"),
		Star(8, 0, "星星", "/images/starIcon.png"), Sun(9, 0, "太阳", "/images/sunIcon.png"),
		Triangle(10, 0, "三角", "/images/triangleIcon.png");
		
		int value;
		int type;
		String name;
		String imgName;
		
		private Icon(int value, int type, String name, String imgName) {
			this.value = value;
			this.type = type;
			this.name = name;
			this.imgName = imgName;
		}
		
		public static Icon valueOf(int value) {
			switch (value) {
				case 1:
					return Cloud;
				case 2:
					return Heart;
				case 3:
					return Hexagon;
				case 4:
					return Lighting;
				case 5:
					return Moon;
				case 6:
					return Pentagon;
				case 7:
					return Square;
				case 8:
					return Star;
				case 9:
					return Sun;
				case 10:
					return Triangle;
				default:
					return Circular;
			}
		}
		
		public final static List<Icon> shape = new ArrayList<>();

		static {
			for (Icon icon: Icon.values()) {
				switch(icon.type) {
				case 0:
					shape.add(icon);
					break;
				default:
					break;
				}
			}
		}
	}

	public static boolean isWhite(int r, int g, int b) {
		return colorNum(r, g, b) >= WHITE_COLOR_NUM;
	}

	public static int colorNum(int r, int g, int b) {
		return (int) (r * 0.299 + g * 0.587 + b * 0.114);
	}
	
	public static final int TRANSPARENT = new Color(0f, 0f, 0f, 0f).getRGB();

	public static void drawImg(BufferedImage oriImage, BufferedImage[] icons, Integer[][] posXY) {
		if (posXY == null || posXY.length != icons.length) {
			throw new RuntimeException("positions size not equal icons size");
		}
		
		BufferedImage icon;
		int iconSize, w, h, x, y, rgbIcon, rIcon, gIcon, bIcon, rgbBg, rBg, gBg, bBg, color;
		for (int iconInd = 0; iconInd < icons.length; iconInd++) {
			icon = icons[iconInd];
			w = posXY[iconInd][0];
			h = posXY[iconInd][1];
			posXY[iconInd][0] /= CaptchaUtil.BACKGROUND_RESIZE;
			posXY[iconInd][1] /= CaptchaUtil.BACKGROUND_RESIZE;
			iconSize = icon.getWidth() > icon.getHeight() ? icon.getHeight() : icon.getWidth();
			for (int i = 0; i < iconSize; i++) {
				for (int j = 0; j < iconSize; j++) {
					if (iconSize * iconSize < (2 * i - iconSize) * (2 * i - iconSize) + (2 * j - iconSize) * (2 * j - iconSize)) {
						continue;
					}

					x = w + i;
					y = h + j;
					rgbIcon = icon.getRGB(i, j);
					rIcon = (rgbIcon & 0xff0000) >> 16;
					gIcon = (rgbIcon & 0xff00) >> 8;
					bIcon = (rgbIcon & 0xff);

					rgbBg = oriImage.getRGB(x, y);
					rBg = (rgbBg & 0xff0000) >> 16;
					gBg = (rgbBg & 0xff00) >> 8;
					bBg = (rgbBg & 0xff);

					if (rIcon == 255 && gIcon == 255 && bIcon == 255) {
						color = ((0xFF << 24) | (RandomUtil.randomInt(220, 255) << 16) | (RandomUtil.randomInt(220, 255) << 8) | RandomUtil.randomInt(220, 255));
					} else {
						color = isWhite(rBg, gBg, bBg) ? WHITE_COLOR_NUM - 30 : WHITE_COLOR_NUM + 30;
						color = ((0xFF << 24) | (((rBg + color) / 2) << 16) | (((gBg + color) / 2) << 8) | ((bBg + color) / 2));
					}

					oriImage.setRGB(x, y, color);
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
			do {
				tooClose = false;
				posX = RandomUtil.randomInt(ICON_TO_BACKGROUND_BORDER, w - ICON_TO_BACKGROUND_BORDER - ICON_WIDTH);
				posY = RandomUtil.randomInt(ICON_TO_BACKGROUND_BORDER, h - ICON_TO_BACKGROUND_BORDER - ICON_HEIGHT);
				for (int j = 0; j < positions.length; j++) {
					position = positions[j];
					px = position[0] == null ? 0 : position[0];
					py = position[1] == null ? 0 : position[1];
					if (px - ICON_X_PADDING <= posX && px + ICON_X_PADDING >= posX
						&& py - ICON_Y_PADDING <= posY && py + ICON_Y_PADDING >= posY) {
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
		return getIcons(n, null);
	}
	
	public static Icon[] getIcons(int n, Integer type) {
		List<Icon> iconList;
		int baseNum;
		switch (type) {
		default:
			iconList = Icon.shape;
			baseNum = 0;
			break;
		}
		int iconTotal = iconList.size();
		Integer[] upsetNumbers = ArrayUtil.upsetNumbers(iconTotal);
		if (n > iconTotal) n = iconTotal;
		Icon[] icons = new Icon[n];
		for (int i = 0; i < n; i++) {
			icons[i] = Icon.valueOf(baseNum + upsetNumbers[i]);
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
	 * @param iconType
	 * @return Map<String, Object> {
	 *     background: 主图 BufferedImage,
	 *     guide: 提示文字,
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	private static Map<String, Object> doCreateImage(String url, int n, Integer iconType) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try {
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(url));

			Icon[] icons = getIcons(MAX_OPERATE_TIMES, iconType);
			if (n > MAX_OPERATE_TIMES) n = MAX_OPERATE_TIMES;
			BufferedImage[] iconImgs = new BufferedImage[MAX_OPERATE_TIMES];
			StringBuffer sb = new StringBuffer();
			sb.append("请依次点击：");
			Icon icon;
			for (int i = 0; i < MAX_OPERATE_TIMES; i++) {
				icon = icons[i];
				iconImgs[i] = getIconImage(icon);
				if (i < n) sb.append(icon.name);
				if (i < n - 1) sb.append("，");
			}
			Integer[][] posXY = getPositions(bufferedImage.getWidth(), bufferedImage.getHeight(), MAX_OPERATE_TIMES);
			drawImg(bufferedImage, iconImgs, posXY);
			bufferedImage = Thumbnails.of(bufferedImage).scale(1.0 / CaptchaUtil.BACKGROUND_RESIZE).asBufferedImage();
			
			resultMap.put("background", bufferedImage); // 主图
			if (posXY == null || posXY.length != iconImgs.length) {
				throw new RuntimeException("draw image falied");
			} else {
				resultMap.put("guide", sb.toString());  // 操作提示
				resultMap.put("times", n);              // 操作次数
				Integer[][] realPosXY = new Integer[n][];
				System.arraycopy(posXY, 0, realPosXY, 0, n);
				resultMap.put("positions", realPosXY);  // icon在主图的坐标
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
	 * @param iconType
	 * @return Map<String, Object> {
	 *     background: 主图 Base64,
	 *     guide: 提示文字,
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	public static Map<String, Object> createImage(String url, int n, Integer iconType) {
		Map<String, Object> resultMap = doCreateImage(url, n, iconType);
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
	 * @param iconType
	 * @return Map<String, Object> {
	 *     background: [主图分片 Base64],
	 *     guide: 提示文字,
	 *     series: [主图分片顺序],
	 *     positions: [[icon 在主图中的坐标]],
	 *     times: icon 个数
	 * }
	 */
	public static Map<String, Object> createImage(String url, int n, Integer iconType, int vCut, int hCut, boolean upset) {
		Map<String, Object> resultMap = doCreateImage(url, n, iconType);
		Integer[] upsetSeries = null;
		if (upset) upsetSeries = ArrayUtil.upsetNumbers(vCut * hCut);

		resultMap.put("background", ImageUtil.imagePieces((BufferedImage) resultMap.get("background"), vCut, hCut, upsetSeries));
		resultMap.put("series", upsetSeries); // 主图打乱的顺序
		return resultMap;
	}

}
