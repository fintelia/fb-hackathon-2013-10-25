package com.example.photofabricator;

import java.io.File;
import java.io.IOException;

import org.jibble.simpleftp.SimpleFTP;

public class FileUploader {

	private static final String FTP_SERVER = "192.168.1.73";
	private static final String FTP_USERNAME = "anonymous";
	private static final String FTP_PASSWORD = "";

	public static void uploadFile(String path) {
		SimpleFTP ftp = new SimpleFTP();
		try {
			ftp.connect(FTP_SERVER, 21, FTP_USERNAME, FTP_PASSWORD);
			ftp.bin();
			ftp.cwd("/");
			ftp.stor(new File(path));
			ftp.disconnect();
		} catch (IOException e) {
		}
	}
}
