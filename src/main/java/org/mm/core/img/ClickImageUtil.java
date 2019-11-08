package org.mm.core.img;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;


public class ClickImageUtil {

	private static final String baseDir = "D:/personal/images/captcha/";
	
	enum Icon {
		Star(0, "星星", "starIcon.png"), Triangle(1, "三角形", "triangleIcon.png"),
		Square(2, "正方形", "squareIcon.png"), Circular(3, "圆形", "circularIcon.png"),
		Cloud(4, "云朵", "cloudIcon.png"), Rhombus(5, "菱形", "rhombusIcon.png"),
		Heart(6, "心形", "heartIcon.png");
		
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
		if (n > iconTotal) n = iconTotal;
		Icon[] icons = new Icon[n];
		List<Integer> indexs = new ArrayList<Integer>();
		for (int i = 0; i < n; i++) {
			Integer index = null;
			do {
				index = RandomUtil.randomInt(0, iconTotal);
			} while (indexs.contains(index));
			indexs.add(index);
			icons[i] = Icon.valueOf(index);
		}
		
		return icons;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		createImage(baseDir + "131.png", 3);
		
//		BufferedImage bufferedImage = ImageIO.read(new FileInputStream(baseDir + "131.png"));
//		BufferedImage icon1 = ImageIO.read(new FileInputStream(baseDir + "starIcon.png"));
//		BufferedImage icon2 = ImageIO.read(new FileInputStream(baseDir + "heartIcon.png"));
//		BufferedImage icon3 = ImageIO.read(new FileInputStream(baseDir + "rhombusIcon.png"));
//		int[][] iconSizes = new int[][]{{30, 30}, {30, 30}, {30, 30}};
//		BufferedImage[] icons = new BufferedImage[]{icon1, icon2, icon3};
//		int[][] posXY = getPositions(bufferedImage.getWidth(), bufferedImage.getHeight(), iconSizes);
//		drawImg(bufferedImage, icons, posXY);
//		ImageIO.write(bufferedImage, "png", new File(baseDir + "131_copy.png"));
		
//		for (int i = 0; i < 10; i++) {
//			Date s = new Date();
//			int[][] positions = getPositions(600, 600, new int[][]{{30, 30}, {30, 30}, {30, 30}});
//			for (int[] position: positions) {
//				System.out.println("posX: " + position[0] + ", posY: " + position[1]);
//			}
//			System.out.println("cost time: " + (new Date().getTime() - s.getTime()) + "ms");
//		}
	}
	
	public static Map<String, Object> createImage(String url, int n) {
		Map<String, Object> res = new HashMap<String, Object>();
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
				BufferedImage iconImg = ImageIO.read(new FileInputStream(baseDir + icon.imgName));
				iconImgs[i] = iconImg;
				iconSizes[i][0] = iconImg.getWidth();
				iconSizes[i][1] = iconImg.getHeight();
				sb.append(icon.name);
				if (i < n - 1) sb.append(",");
			}
			int[][] posXY = getPositions(bufferedImage.getWidth(), bufferedImage.getHeight(), iconSizes);
			drawImg(bufferedImage, iconImgs, posXY);
			
			res.put("success", true);
			res.put("actionInfo", sb.toString());
			res.put("positions", posXY);
		} catch (IOException e) {
			res.put("success", false);
			e.printStackTrace();
		}
		
		return res;
	}
}
