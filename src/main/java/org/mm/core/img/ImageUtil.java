package org.mm.core.img;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import static org.mm.core.img.ImageUtil.Policy.CropAtCenter;
import static org.mm.core.img.ImageUtil.Policy.CropAtTop;

@SuppressWarnings("restriction")
public class ImageUtil {

	private static BufferedImage shadowImage = null;
	private static BufferedImage backGroundImage = null;
	private static int resizeBaseSize = 1000;
	
	static {
		try {
			shadowImage = ImageIO.read(ImageUtil.class.getResourceAsStream("/images/shadow.png"));
			backGroundImage = ImageIO.read(ImageUtil.class.getResourceAsStream("/images/white.png"));
			resizeBaseSize = shadowImage.getWidth();
		} catch (IOException e) {
		}
	}

	enum Policy {
		Scaled(0), CropAtTop(1), CropAtCenter(2), CropAtBottom(3);
		int value;

		private Policy(int val) {
			this.value = val;
		}

		public static Policy valueOf(int value) {
			switch (value) {
				case 1:
					return CropAtTop;
				case 2:
					return CropAtCenter;
				case 3:
					return CropAtBottom;
				default:
					return Scaled;
			}
		}
	}

	private static class TransformParams {
		float mXScale, mYScale;
		int mCropAtX, mCropAtY;
	}

	private static TransformParams getTransformParams(BufferedImage image, Policy policy, int targetSize) {
		if (image == null || policy == null) {
			throw new NullPointerException("Empty image or policy!!! ");
		}
		int w = image.getWidth();
		int h = image.getHeight();
		float xscale = (float) targetSize / w;
		float yscale = (float) targetSize / h;

		TransformParams params = new TransformParams();
		if (policy == Policy.Scaled) {
			params.mXScale = xscale;
			params.mYScale = yscale;
			params.mCropAtX = params.mCropAtY = 0;
		} else {
			params.mXScale = params.mYScale = Math.max(xscale, yscale);
			if (policy == CropAtTop) {
				params.mCropAtX = params.mCropAtY = 0;
			} else if (policy == CropAtCenter) {
				params.mCropAtX = xscale > yscale ? 0 : (int) (w * yscale - targetSize) / 2;
				params.mCropAtY = xscale < yscale ? 0 : (int) (h * xscale - targetSize) / 2;
			} else {
				params.mCropAtX = xscale > yscale ? 0 : (int) (w * yscale - targetSize);
				params.mCropAtY = xscale < yscale ? 0 : (int) (h * xscale - targetSize);
			}
		}
		return params;
	}

	private static InputStream getImageInputStreamByUrl(String url) {
		URL imgUrl = null;
		try {
			imgUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			return conn.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static InputStream getImageInputStreamByFilePath(String filePath) {
		try {
			return new FileInputStream(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static BufferedImage getImage(String uri) throws IOException {
		if (uri == null) {
			throw new NullPointerException("uri is null!!!");
		}
		InputStream inputStream = null;
		if (uri.startsWith("http://")) {
			inputStream = getImageInputStreamByUrl(uri);
		} else {
			inputStream = getImageInputStreamByFilePath(uri);
		}
		try {
			return ImageIO.read(inputStream);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable e) {
				}
			}
		}
	}

	private static BufferedImage scaleImage(BufferedImage image, TransformParams params) {
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.setToScale(params.mXScale, params.mYScale);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BICUBIC);
		BufferedImage outputImage = new BufferedImage((int) (image.getWidth() * params.mXScale),
				(int) (image.getHeight() * params.mYScale), BufferedImage.TYPE_INT_ARGB);
		affineTransformOp.filter(image, outputImage);
		return outputImage;
	}

	public static BufferedImage scaleImage(BufferedImage image, int targetSize) {
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.setToScale(targetSize * 1f / image.getWidth(), targetSize * 1f / image.getHeight());
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BICUBIC);
		BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
		affineTransformOp.filter(image, outputImage);
		return outputImage;
	}

	private static BufferedImage cropImage(BufferedImage image, TransformParams params, int targetSize) {
		ImageFilter cropFilter = new CropImageFilter(params.mCropAtX, params.mCropAtY, targetSize, targetSize);
		Image img = Toolkit.getDefaultToolkit().createImage(
				new FilteredImageSource(image.getSource(), cropFilter));
		BufferedImage outputImage = new BufferedImage(targetSize, targetSize,
				BufferedImage.TYPE_INT_ARGB);

		Graphics g = outputImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return outputImage;
	}

	private static BufferedImage roundImage(BufferedImage image, int targetSize, int cornerRadius) {
		BufferedImage outputImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = outputImage.createGraphics();
		g2.setComposite(AlphaComposite.Src);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.WHITE);
		g2.fill(new RoundRectangle2D.Float(0, 0, targetSize, targetSize, cornerRadius, cornerRadius));
		g2.setComposite(AlphaComposite.SrcAtop);
		g2.drawImage(image, 0, 0, null);
		g2.dispose();
		return outputImage;
	}

	private static int[] getPixArray(BufferedImage im) {
		int w = im.getWidth();
		int h = im.getHeight();
		int[] pix = new int[w * h];
		PixelGrabber pg = null;
		try {
			pg = new PixelGrabber(im, 0, 0, w, h, pix, 0, w);
			if (pg.grabPixels()) {
				return pix;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private static int[] convertImage(int[] ImageSource, int[] backGroundPixArray, int w, int h) {
		if (backGroundPixArray == null || ImageSource == null) {
			return null;
		}
		for (int i = 0; i < ImageSource.length; i++) {
			int tmp = i + (i / w + 1) * (resizeBaseSize - w) - (resizeBaseSize - w) / 2;
			if (((ImageSource[i] & 0xFF000000) != 0) &&
					(0xFFFFFFFF != (backGroundPixArray[tmp] & 0xFF000000))) {
				ImageSource[i] = (ImageSource[i] & 0xFFFFFF | backGroundPixArray[tmp] & 0xFF000000);
			}
		}
		return ImageSource;
	}

	private static BufferedImage circleImage(BufferedImage image, int targetSize) {
		BufferedImage newImage = scaleImage(image, resizeBaseSize);
		
		int width = newImage.getWidth();
		int height = newImage.getHeight();
		
		int[] backGroundPixArray = getPixArray(backGroundImage);
		int[] currentPixArray = getPixArray(newImage);
		
		int[] resultArray = convertImage(currentPixArray, backGroundPixArray, width, height);
		
		Image pic = Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height,
				resultArray, 0, width));
		
		Graphics2D tmp = shadowImage.createGraphics();
		tmp.drawImage(backGroundImage, 0, 0, null);
		tmp.drawImage(pic, 0, 0, null);
		tmp.dispose();
		
		return scaleImage(shadowImage,targetSize);
	}

	private static BufferedImage addPaddingToImage(BufferedImage image, int padding) {
		int fullSize = image.getWidth();
		int viewSize = fullSize - 2 * padding;
		float scale = viewSize * 1f / fullSize;
		AffineTransform affineTransform = new AffineTransform();
		affineTransform.setToScale(scale, scale);
		affineTransform.translate(padding / scale, padding / scale);
		AffineTransformOp affineTransformOp = new AffineTransformOp(
				affineTransform, AffineTransformOp.TYPE_BICUBIC);
		BufferedImage outputImage = new BufferedImage(fullSize, fullSize, BufferedImage.TYPE_INT_ARGB);
		affineTransformOp.filter(image, outputImage);
		return outputImage;
	}
	
	public static boolean generateIcon(String inputPath, String outputPath, int iconSize, int cornerRadius, int padding) {
		return generateIcon(inputPath, outputPath, iconSize, cornerRadius, Policy.Scaled, padding);
	}

	/**
	 * 生成icon
	 * @param inputPath 原图片路径
	 * @param targetPath 输出图片路径
	 * @param targetSize 输出图片大小
	 * @param cornerRadius 圆角，0生成圆
	 * @param policy
	 * @param padding
	 * @return
	 */
	public static boolean generateIcon(String inputPath, String targetPath, int targetSize, int cornerRadius, Policy policy, int padding) {
		BufferedImage image = null;
		try {
			image = getImage(inputPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (image == null) {
			return false;
		}
		TransformParams params = getTransformParams(image, policy, targetSize);
		BufferedImage outputImage = scaleImage(image, params);
		if (policy != Policy.Scaled) {
			outputImage = cropImage(outputImage, params, targetSize);
		}
		if (cornerRadius == 0) {
			outputImage = circleImage(outputImage, targetSize);
		} else {
			outputImage = roundImage(outputImage, targetSize, cornerRadius);
		}
		if (padding > 0) {
			outputImage = addPaddingToImage(outputImage, padding);
		}
		try {
			ImageIO.write(outputImage, "png", new File(targetPath));
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @Title: getImageStr
	 * @Description: 图片转BASE64
	 * @param image
	 * @return
	 * @throws IOException String
	 */
	public static String getImageBASE64(BufferedImage image) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(image, "png", out);
			byte[] b = out.toByteArray(); // 转成byte数组
			BASE64Encoder encoder = new BASE64Encoder();
			return encoder.encode(b); // 生成base64编码  
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * BASE64转图片
	 * @param base64
	 * @param filePath
	 */
	public static BufferedImage base64ToImage(String base64) {
		if (base64 == null) return null;
		try {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] b = decoder.decodeBuffer(base64);
			ByteArrayInputStream in = new ByteArrayInputStream(b);
			BufferedImage image = ImageIO.read(in);
			in.close();
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String imagePiece(BufferedImage image, int w, int h, int x, int y) {
		BufferedImage targetImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				int _x = x + i;
				int _y = y + j;
				int rgbImg = image.getRGB(_x, _y);
				targetImage.setRGB(i, j, rgbImg);
			}
		}
		String imageBase64 = getImageBASE64(targetImage);
//		if (imageBase64 != null) imageBase64 = "data:image/png;base64," + imageBase64;
		return imageBase64;
	}
	
	public static String[] imagePieces(BufferedImage image, int vCutCount, int hCutCount, Integer[] upsetSeries) {
		int w = image.getWidth();
		int h = image.getHeight();
		int[] pieceWidths = new int[vCutCount];
		int[] pieceHeights = new int[hCutCount];
		int pieceWidth = w / vCutCount;
		int imageWidthAva = w % vCutCount;
		for (int i = 0; i < vCutCount; i++) {
			if (i < imageWidthAva) {
				pieceWidths[i] = pieceWidth + 1;
			} else {
				pieceWidths[i] = pieceWidth;
			}
		}
		int pieceHeight = h / hCutCount;
		int imageHeightAva = h % hCutCount;
		for (int i = 0; i < hCutCount; i++) {
			if (i < imageHeightAva) {
				pieceHeights[i] = pieceHeight + 1;
			} else {
				pieceHeights[i] = pieceHeight;
			}
		}
		String[] imageBase64s = new String[hCutCount * vCutCount];
		boolean needUpset = upsetSeries != null && upsetSeries.length == hCutCount * vCutCount;
		int x = 0;
		int y = 0;
		for (int i = 0; i < hCutCount; i++) {
			x = 0;
			int pieceH = pieceHeights[i];
			for (int j = 0; j < vCutCount; j++) {
				int pieceW = pieceWidths[j];
				String imageBase64 = imagePiece(image, pieceW, pieceH, x, y);
				int index = i * vCutCount + j;
				if (needUpset) index = upsetSeries[index];
				imageBase64s[index] = imageBase64;
				x += pieceW;
			}
			y += pieceH;
		}
		
		return imageBase64s;
	}

}
