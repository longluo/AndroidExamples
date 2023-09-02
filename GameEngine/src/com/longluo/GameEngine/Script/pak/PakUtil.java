package com.longluo.GameEngine.Script.pak;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.Vector;

/**
 * Pak工具类 功能： 从Pak文件中取出png图片，构造byte数组（可以用来构造Image对象）
 * 
 */
public class PakUtil {
	public PakUtil() {
	}

	/**
	 * 计算文件位移的起始点
	 * 
	 * @return 文件位移的起始点
	 */
	private long workOutOffsetStart(PakHeader header) {
		// 计算出文件头+文件table的长度
		return PakHeader.size() + header.getNumFileTableEntries()
				* PakFileTable.size();
	}

	/**
	 * 从DataInputStream读取char数组
	 * 
	 * @param dis
	 *            DataInputStream
	 * @param readLength
	 *            读取长度
	 * @return char数组
	 * @throws Exception
	 */
	private char[] readCharArray(DataInputStream dis, int readLength)
			throws Exception {
		char[] readCharArray = new char[readLength];

		for (int i = 0; i < readLength; i++) {
			readCharArray[i] = dis.readChar();
		}
		return readCharArray;
	}

	/**
	 * 从PAK文件中读取文件头
	 * 
	 * @param dis
	 *            DataInputStream
	 * @return PakHeader
	 * @throws Exception
	 */
	private PakHeader readHeader(DataInputStream dis) throws Exception {
		PakHeader header = new PakHeader();
		char[] signature = readCharArray(dis, PakHeader.SIGNATURE_LENGTH);
		header.setSignature(signature);
		header.setVersion(dis.readFloat());
		header.setNumFileTableEntries(dis.readLong());
		header.setCipherAction(dis.readByte());
		header.setCipherValue(dis.readByte());
		char[] uniqueID = readCharArray(dis, PakHeader.UNIQUEID_LENGTH);
		header.setUniqueID(uniqueID);
		header.setReserved(dis.readLong());
		return header;
	}

	/**
	 * 读取所有的文件table
	 * 
	 * @param dis
	 *            DataInputStream
	 * @param fileTableNumber
	 *            文件表总数
	 * @return 文件table数组
	 * @throws Exception
	 */
	private PakFileTable[] readFileTable(DataInputStream dis,
			int fileTableNumber) throws Exception {
		PakFileTable[] fileTable = new PakFileTable[fileTableNumber];
		for (int i = 0; i < fileTableNumber; i++) {
			PakFileTable ft = new PakFileTable();
			ft.setFileName(readCharArray(dis, PakFileTable.FILENAME_LENGTH));
			ft.setFileSize(dis.readLong());
			ft.setOffSet(dis.readLong());
			fileTable[i] = ft;
		}
		return fileTable;
	}

	/**
	 * 从pak文件读取文件到byte数组
	 * 
	 * @param dis
	 *            DataInputStream
	 * @param fileTable
	 *            PakFileTable
	 * @return byte数组
	 * @throws Exception
	 */
	private byte[] readFileFromPak(DataInputStream dis, PakHeader header,
			PakFileTable fileTable) throws Exception {
		dis.skip(fileTable.getOffSet() - workOutOffsetStart(header));
		//
		int fileLength = (int) fileTable.getFileSize();
		byte[] fileBuff = new byte[fileLength];
		int readLength = dis.read(fileBuff, 0, fileLength);
		if (readLength < fileLength) {
			System.out.println("读取数据长度不正确");
			return null;
		} else {
			decryptBuff(fileBuff, readLength, header);
		}
		return fileBuff;
	}

	/**
	 * 使用文件头中的密码对数据进行解密
	 * 
	 * @param buff
	 *            被解密的数据
	 * @param buffLength
	 *            数据的长度
	 * @param header
	 *            文件头
	 */
	private void decryptBuff(byte[] buff, int buffLength, PakHeader header) {
		for (int i = 0; i < buffLength; i++) {
			switch (header.getCipherAction()) {
			case PakHeader.ADDITION_CIPHERACTION:
				buff[i] -= header.getCipherValue();
				break;
			case PakHeader.SUBTRACT_CIHOERACTION:
				buff[i] += header.getCipherValue();
				break;
			}
		}
	}

	/**
	 * 从pak文件中取出指定的文件到byte数组
	 * 
	 * @param pakResourceURL
	 *            pak文件的资源路径
	 * @param extractResourceName
	 *            pak文件中将要被取出的文件名
	 * @return byte数组
	 * @throws Exception
	 */
	public byte[] extractResourceFromPak(String pakResourceURL,
			String extractResourceName) throws Exception {
		InputStream is = this.getClass().getResourceAsStream(pakResourceURL);
		DataInputStream dis = new DataInputStream(is);
		PakHeader header = readHeader(dis);
		// System.out.println("文件头:");
		// System.out.println(header);
		PakFileTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());
		// for(int i=0;i<fileTable.length;i++){
		// System.out.println("文件table["+i+"]:");
		// System.out.println(fileTable[i]);
		// }
		boolean find = false;
		int fileIndex = 0;
		for (int i = 0; i < fileTable.length; i++) {
			String fileName = new String(fileTable[i].getFileName()).trim();
			if (fileName.equals(extractResourceName)) {
				find = true;
				fileIndex = i;
				break;
			}
		}
		if (find == false) {
			System.out.println("没有找到指定的文件");
			return null;
		} else {
			byte[] buff = readFileFromPak(dis, header, fileTable[fileIndex]);
			return buff;
		}
	}

	/**
	 * 从pak文件中取出指定的Pak文件的信息
	 * 
	 * @param pakResourcePath
	 *            pak文件资源路径
	 * @return 装载文件头和文件table数组的Vector
	 * @throws Exception
	 */
	public Vector showPakFileInfo(String pakResourcePath) throws Exception {
		InputStream is = this.getClass().getResourceAsStream(pakResourcePath);
		DataInputStream dis = new DataInputStream(is);

		PakHeader header = readHeader(dis);
		PakFileTable[] fileTable = readFileTable(dis,
				(int) header.getNumFileTableEntries());

		Vector result = new Vector();
		result.addElement(header);
		result.addElement(fileTable);
		return result;
	}

	public static void main(String[] argv) throws Exception {
		PakUtil pu = new PakUtil();
		String extractResourcePath = "/test.pak";
		// 从Pak文件中取出所有的图片文件
		Vector pakInfo = pu.showPakFileInfo(extractResourcePath);
		PakHeader header = (PakHeader) pakInfo.elementAt(0);
		System.out.println("Pak文件信息:");
		System.out.println("文件头:");
		System.out.println(header);

		PakFileTable[] fileTable = (PakFileTable[]) pakInfo.elementAt(1);
		for (int i = 0; i < fileTable.length; i++) {
			System.out.println("文件table[" + i + "]:");
			System.out.println(fileTable[i]);
		}

		String restoreFileName = null;
		byte[] fileBuff = null;
		for (int i = 0; i < fileTable.length; i++) {
			restoreFileName = new String(fileTable[i].getFileName()).trim();
			System.out.println("从Pak文件中取出" + restoreFileName + "文件数据...");
			fileBuff = pu.extractResourceFromPak(extractResourcePath,
					restoreFileName);
			System.out.println("从Pak文件中取出" + restoreFileName + "文件数据完成");
		}
	}
}
