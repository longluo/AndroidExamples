package com.longluo.GameEngine.Script.pak;


/**
 * Pak文件table类
 * 文件table结构：
 * 	文件名：30字节char数组
 * 	文件大小：32位整型
 * 	文件在pak文件中的位移：32位整数
 *
 */
public class PakFileTable
{
	public static final int FILENAME_LENGTH=30;
	//文件名
	private char[] fileName=new char[FILENAME_LENGTH];
	//文件大小
	private long fileSize=0L;
	//文件在pak文件中的位移
	private long offSet=0L;
	
	public PakFileTable(){
	}
	
	/**
	 * 构造方法
	 * @param fileName 文件名
	 * @param fileSize 文件大小
	 * @param offSet 文件在Pak文件中的位移
	 */
	public PakFileTable(char[] fileName,
			long fileSize,long offSet){
		for(int i=0;i<FILENAME_LENGTH;this.fileName[i]=fileName[i],i++)
			;
		this.fileSize=fileSize;
		this.offSet=offSet;
	}
	
	public char[] getFileName() {
		return fileName;
	}
	public void setFileName(char[] fileName) {
		for(int i=0;i<fileName.length;this.fileName[i]=fileName[i],i++)
			;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public long getOffSet() {
		return offSet;
	}
	public void setOffSet(long offSet) {
		this.offSet = offSet;
	}
	/**
	 * 返回文件Table的大小
	 * @return 返回文件Table的大小
	 */
	public static int size(){
		return FILENAME_LENGTH+4+4;
	}
	
	public String toString(){
		return "\t文件名:"+new String(this.fileName).trim()
			+"\t文件大小:"+this.fileSize
			+"\t文件位移:"+this.offSet;
	}
}

