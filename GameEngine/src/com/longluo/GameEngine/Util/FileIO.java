package com.longluo.GameEngine.Util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;

//文件IO类
public class FileIO {
	// GBK编码方式
	public static final String GBK_CODE = "GBK";
	// UTF-8编码方式
	public static final String UTF8_CODE = "UTP-8";

	/**
	 * 读取文本文件
	 * 
	 * @param originalObject
	 *            载有资源包的类对象
	 * @param resURL
	 *            文件在资源包中的URL
	 * @param textCodeMethod
	 *            文字编码方式
	 * @return 编码后的文本
	 */
	public static String readFileText(Object originalObject, String resURL,
			String textCodeType) {
		InputStream is = originalObject.getClass().getResourceAsStream(resURL);
		try {
			DataInputStream dis = new DataInputStream(is);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int ch;

			while ((ch = dis.read()) != -1)
				baos.write(ch);

			dis.close();

			return new String(baos.toByteArray(), textCodeType);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
