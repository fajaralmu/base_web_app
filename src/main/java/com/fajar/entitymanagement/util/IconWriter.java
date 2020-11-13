package com.fajar.entitymanagement.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IconWriter {
	static final int ICO_WIDTH = 100;
	static final int ICO_HEIGHT = 100;
	public static String writeIcon(Image srcImg, String filePath) {
		try {
			log.info("Writing icon");
			// note the double height
			BufferedImage img = new BufferedImage(ICO_WIDTH, ICO_HEIGHT*2, BufferedImage.TYPE_INT_RGB);
			Graphics g = img.getGraphics(); 
			//g.fillRect(0, 16, 16, 16);// added 16 to y coordinate 
			g.drawImage(srcImg, 0, ICO_HEIGHT, ICO_WIDTH, ICO_HEIGHT , null);
			
			byte[] imgBytes = getImgBytes(img);
			int fileSize = imgBytes.length + 22;
			ByteBuffer bytes = ByteBuffer.allocate(fileSize);
			bytes.order(ByteOrder.LITTLE_ENDIAN);
			bytes.putShort((short) 0);// Reserved must be 0
			bytes.putShort((short) 1);// Image type
			bytes.putShort((short) 1);// Number of images in file
			bytes.put((byte) img.getWidth());// image width
			bytes.put((byte) (img.getHeight() >> 1));// image height, half the BMP height
			bytes.put((byte) 0);// number of colors in color palette
			bytes.put((byte) 0);// reserved must be 0
			bytes.putShort((short) 0);// color planes
			bytes.putShort((short) 0);// bits per pixel
			bytes.putInt(imgBytes.length);// image size
			bytes.putInt(22);// image offset
			bytes.put(imgBytes);
			byte[] result = bytes.array();
			String fileName = "ICO_" + StringUtil.generateRandomNumber(10)+".ico";
			File outputfile = new File(filePath + "/" + fileName + ".ico");
			outputfile.createNewFile();

			FileOutputStream fos = new FileOutputStream(filePath + "/" + fileName);
			fos.write(result);
			fos.close();
			fos.flush();
			log.info("Created icon file: {}", fileName);
			return fileName;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	private static byte[] getImgBytes(BufferedImage img) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(img, "bmp", bos);
		byte[] bytes = bos.toByteArray();
		return Arrays.copyOfRange(bytes, 14, bytes.length);
	}

}
