package org.mm.core.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import net.coobird.thumbnailator.Thumbnails;

public final class ImgCompressUtil {

	private static Logger log = Logger.getLogger(ImgCompressUtil.class);
	
	private static long fileMinSize = 100 * 1024;
	
	public static final String COMPRESS_APPEND_NAME = "_compress";
	
	/**
	 * 根据比例调整图片大小
	 * @param filePath 源文件路径
	 * @param width 基础宽度
	 * @param ratio 比例
	 * @return
	 */
	public static String resizeRatio(String filePath, Integer width, Integer height, Double ratio, String targetPath) {
		try {
			if (ratio != null && width != null && height != null) {
				File file = new File(filePath); // 读入文件
				Image image = ImageIO.read(file);
				if (image.getWidth(null) == width.intValue() * ratio.doubleValue()
						&& image.getHeight(null) == height.intValue() * ratio.doubleValue()) {
					return filePath;
				} else {
					Double w = width * ratio;
					Double h = height * ratio;
					resize(image, w.intValue(), h.intValue(), targetPath);
					return targetPath;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 自适应进行压缩
	 * @param filePath
	 * @param w
	 * @param h
	 */
	public static String resizeFix(String filePath, Integer w, Integer h) {
		try {
			Integer dotIndex = filePath.lastIndexOf(".");
			String newFilePath = filePath.substring(0, dotIndex) + COMPRESS_APPEND_NAME + filePath.substring(dotIndex);
			
			File file = new File(filePath);// 读入文件
			if (fileMinSize > file.length()) return filePath;
			
			Image image = ImageIO.read(file); // 构造image对象
			Integer width = image.getWidth(null);
			Integer height = image.getHeight(null);
			if (width / height > w / h) {
				if (w < width) {
					h = (Integer) (height * w / width);
					resize(image, w, h, newFilePath);
					
					return newFilePath;
				}
			} else {
				if (h < height) {
					w = (Integer) (width * h / height);
					resize(image, w, h, newFilePath);
					
					return newFilePath;
				}
			}
		} catch (IOException e) {
			log.error("未找到文件: " + filePath);
		} catch (Exception e) {
			log.error("压缩失败: " + e.getMessage());
		}
		
		return filePath;
	}
	
	/**
	 * 以宽度为基准，等比例放缩图片
	 * @param filePath
	 * @param w
	 */
	public static String resizeByWidth(String filePath, Integer w) {
		try {
			Integer dotIndex = filePath.lastIndexOf(".");
			String newFilePath = filePath.substring(0, dotIndex) + COMPRESS_APPEND_NAME + filePath.substring(dotIndex);
			
			File file = new File(filePath);// 读入文件
			if (fileMinSize > file.length()) return filePath;
			
			Image image = ImageIO.read(file); // 构造image对象
			Integer width = image.getWidth(null);
			Integer height = image.getHeight(null);
			if (w < width) {
				Integer h = (Integer) (height * w / width);
				resize(image, w, h, newFilePath);
				
				return newFilePath;
			}
		} catch (IOException e) {
			log.error("未找到文件: " + filePath);
		} catch (Exception e) {
			log.error("压缩失败: " + e.getMessage());
		}
		
		return filePath;
	}
	
	/**
	 * 以宽度为基准，等比例放缩图片
	 * @param filePath
	 * @param w
	 */
	public static String resizeByHeight(String filePath, Integer h) {
		try {
			Integer dotIndex = filePath.lastIndexOf(".");
			String newFilePath = filePath.substring(0, dotIndex) + COMPRESS_APPEND_NAME + filePath.substring(dotIndex);
			
			File file = new File(filePath);// 读入文件
			if (fileMinSize > file.length()) return filePath;
			
			Image image = ImageIO.read(file); // 构造image对象
			Integer width = image.getWidth(null);
			Integer height = image.getHeight(null);
			if (h < height) {
				Integer w = (Integer) (width * h / height);
				resize(image, w, h, newFilePath);
				
				return newFilePath;
			}
		} catch (IOException e) {
			log.error("未找到文件: " + filePath);
		} catch (Exception e) {
			log.error("压缩失败: " + e.getMessage());
		}
		
		return filePath;
	}
	
	/** 
	 * 强制压缩/放大图片到固定的大小
	 * @param image
	 * @param w int 新宽度
	 * @param h int 新高度
	 */
	
	public static void resize(Image image, Integer width, Integer height, String newFilePath) throws Exception {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		bufferedImage.getGraphics().drawImage(image, 0, 0, width, height, null);
		
		String formatName = newFilePath.substring(newFilePath.lastIndexOf(".") + 1);
		File newFile = new File(newFilePath);
		ImageIO.write(bufferedImage, formatName , newFile);
	}
	
	/**
	 * * 转换图片大小，不变形
	 * @param img 图片文件
	 * @param width 图片宽
	 * @param height 图片高
	 */
	public static final void changeImge(File img, int width, int height) {
		try {
			Thumbnails.of(img).size(width, height).keepAspectRatio(false).toFile(img);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException("图片转换出错！", e);
		}
	}

	/**
	 * 根据比例缩放图片
	 * @param orgImg 源图片
	 * @param scale 比例
	 * @param targetFile 缩放后的图片存放路径
	 * @throws IOException
	 */
	public static final void scale(BufferedImage orgImg, double scale, String targetFile) throws IOException {
		Thumbnails.of(orgImg).scale(scale).toFile(targetFile);
	}

	/**
	 * 根据比例缩放图片
	 * @param orgImgFile 源图片路径
	 * @param scale 比例
	 * @param targetFile 缩放后的图片存放路径
	 * @throws IOException
	 */
	public static final void scale(String orgImgFile, double scale, String targetFile) throws IOException {
		Thumbnails.of(orgImgFile).scale(scale).toFile(targetFile);
	}

	/**
	 * 图片格式转换
	 * @param orgImgFile
	 * @param width
	 * @param height
	 * @param suffixName
	 * @param targetFile
	 * @throws IOException
	 */
	public static final void format(String orgImgFile, int width, int height, String suffixName, String targetFile)
			throws IOException {
		Thumbnails.of(orgImgFile).size(width, height).outputFormat(suffixName).toFile(targetFile);
	}

	/**
	 * 根据宽度同比缩放
	 * @param orgImg 源图片
	 * @param targetWidth 缩放后的宽度
	 * @param targetFile 缩放后的图片存放路径
	 * @throws IOException
	 */
	public static final double scaleWidth(BufferedImage orgImg, int targetWidth, String targetFile) throws IOException {
		int orgWidth = orgImg.getWidth();
		// 计算宽度的缩放比例
		double scale = targetWidth * 1.00 / orgWidth;
		// 裁剪
		scale(orgImg, scale, targetFile);

		return scale;
	}

	/**
	 * 根据宽度同比缩放
	 * @param orgImgFile 源图片路径
	 * @param targetWidth 缩放后的宽度
	 * @param targetFile 缩放后的图片存放路径
	 * @throws IOException
	 */
	public static final void scaleWidth(String orgImgFile, int targetWidth, String targetFile) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File(orgImgFile));
		scaleWidth(bufferedImage, targetWidth, targetFile);
	}

	/**
	 * 根据高度同比缩放
	 * @param orgImgFile 源图片
	 * @param targetHeight 缩放后的高度
	 * @param targetFile 缩放后的图片存放地址
	 * @throws IOException
	 */
	public static final double scaleHeight(BufferedImage orgImg, int targetHeight, String targetFile) throws IOException {
		int orgHeight = orgImg.getHeight();
		double scale = targetHeight * 1.00 / orgHeight;
		scale(orgImg, scale, targetFile);
		return scale;
	}

	/**
	 * 根据高度同比缩放
	 * @param orgImgFile 源图片路径
	 * @param targetHeight 缩放后的高度
	 * @param targetFile 缩放后的图片存放地址
	 * @throws IOException
	 */
	public static final void scaleHeight(String orgImgFile, int targetHeight, String targetFile) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File(orgImgFile));
		scaleHeight(bufferedImage, targetHeight, targetFile);
	}

}
