package com.fajar.entitymanagement.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.util.IconWriter;

@Service
public class FileService {

	@Autowired
	private WebConfigService webAppConfiguration;

	public static void main(String[] args) {
		File file = new File("D:/Development/Files");
		System.out.println(file.toURI().toString());
	}

	public String writeIcon(String code, String data) throws IOException {
		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}
		// create a buffered image
		String imageString = imageData[1];
		BufferedImage image = null;
		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();
 
		String path = webAppConfiguration.getUploadedImageRealPath();

		String iconName = IconWriter.writeIcon(image, path + "/ICON");

		return iconName;

	}

	public String writeImage(String code, String data) throws IOException {

		String[] imageData = data.split(",");
		if (imageData == null || imageData.length < 2) {
			return null;
		}
		// create a buffered image
		String imageString = imageData[1];
		BufferedImage image = null;
		byte[] imageByte;

		Base64.Decoder decoder = Base64.getDecoder();
		imageByte = decoder.decode(imageString);
		ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
		image = ImageIO.read(bis);
		bis.close();

		// write the image to a file
		String imageIdentity = imageData[0];
		String imageType = imageIdentity.replace("data:image/", "").replace(";base64", "");
		String imageName = UUID.randomUUID().toString();
		// String path = servletContext.getRealPath("/resources/img/upload");
		// String path ="D:/Development/Files/Web/Shop1/Images";
		String path = webAppConfiguration.getUploadedImageRealPath();
 

		String imageFileName = code + "_" + imageName + "." + imageType;
		File outputfile = new File(path + "/" + imageFileName);
		System.out.println("==========UPLOADED FILE: " + outputfile.getAbsolutePath());
		ImageIO.write(image, imageType, outputfile);
		System.out.println("==output file: " + outputfile.getAbsolutePath());
		return imageFileName;
	}

}
