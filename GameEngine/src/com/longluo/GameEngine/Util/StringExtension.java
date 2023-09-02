package com.longluo.GameEngine.Util;

import java.util.Vector;

//字符串扩展类 
public class StringExtension {
	// 回车分割符
	public static final String ENTER_SEPARATOR = "\r\n";
	// 分割操作的类型-分割为字符串数组
	public static final int STRING_ARRAY = 0;
	// 分割操作的类型-分割为整数数组
	public static final int INTEGER_ARRAY = 1;

	/**
	 * 使用分割字符串来分离字符串
	 * 
	 * @param strbf
	 *            待分离的字符串Buffer
	 * @param separator
	 *            分割字符串
	 * @param resultType
	 *            返回结果的类型（字符串数组、整型数组）
	 * @param drop
	 *            是否丢弃分离后剩余的字符串
	 * @return 分割后的字符串对象数组
	 */
	public static Object[] split(StringBuffer strbf, String separator,
			int resultType, boolean drop) {

		int pos = 0;
		Vector tmp = null;

		pos = strbf.toString().indexOf(separator);
		tmp = new Vector();

		while (pos > 0) {
			switch (resultType) {
			case STRING_ARRAY:
				tmp.addElement(strbf.toString().substring(0, pos));
				break;
			case INTEGER_ARRAY:
				tmp.addElement(new Integer(Integer.parseInt(strbf.toString()
						.substring(0, pos))));
				break;
			}

			strbf.delete(0, pos + separator.length());
			pos = strbf.toString().indexOf(separator);
		}

		// 如果保留余下的字符串，则将它加入到返回结果中。
		if (!drop) {
			if (strbf.length() > 0) {
				switch (resultType) {
				case STRING_ARRAY:
					tmp.addElement(strbf.toString());
					break;
				case INTEGER_ARRAY:
					tmp.addElement(new Integer(Integer.parseInt(strbf
							.toString())));
					break;
				}
			}
		}
		Object[] result = new Object[tmp.size()];
		tmp.copyInto(result);
		return result;
	}

	/**
	 * 将对象数组中的原本为String的对象转换为String类型
	 * 
	 * @param objArray
	 *            对象数组
	 * @return 转换后的字符串数组
	 */
	public static String[] objectArrayBatchToStringArray(Object[] objArray) {
		String[] result = new String[objArray.length];
		for (int i = 0; i < objArray.length; i++) {
			result[i] = (String) objArray[i];
		}
		return result;
	}

	/**
	 * 将对象数组中的原本为Integer的对象转换为int类型
	 * 
	 * @param objArray
	 *            对象数组
	 * @return 转换后的int数组
	 */
	public static int[] objectArrayBatchToIntArray(Object[] objArray) {
		int[] result = new int[objArray.length];
		for (int i = 0; i < objArray.length; i++) {
			Integer t = (Integer) objArray[i];
			result[i] = t.intValue();
		}
		return result;
	}

	/**
	 * 返回字符串中某个子字符串出现的次数
	 * 
	 * @param str
	 *            字符串
	 * @param token
	 *            子字符串
	 * @return 子字符串出现的次数
	 */
	public static int getTokenCount(String str, String token) {
		int count = 0;
		int beginPos = 0;
		int pos = 0;
		while ((pos = str.indexOf(token, beginPos)) >= 0) {
			count++;
			beginPos = pos + token.length();
		}
		return count;
	}

	/**
	 * 切除字符串中的token子字符串
	 * 
	 * @param content
	 *            字符串
	 * @param cutToken
	 *            要被切除的子字符串
	 * @return 切除后的字符串
	 */
	public static String removeToken(String content, String cutToken) {
		StringBuffer s = new StringBuffer(content);
		int pos = 0;

		while ((pos = s.toString().indexOf(cutToken)) >= 0) {
			s.delete(pos, pos + cutToken.length());
		}
		return s.toString();
	}

	/**
	 * 切除字符串中的token子字符串
	 * 
	 * @param content
	 *            字符串
	 * @param cutToken
	 *            由要被切除的子字符串组成的数组
	 * @return 切除后的字符串
	 */
	public static String removeToken(String content, String[] cutToken) {
		StringBuffer s = new StringBuffer(content);
		int pos = 0;

		for (int i = 0; i < cutToken.length; i++) {
			while ((pos = s.toString().indexOf(cutToken[i])) >= 0) {
				s.delete(pos, pos + cutToken[i].length());
			}
		}
		return s.toString();
	}

	/**
	 * 替换字符串中的某个子字符串
	 * 
	 * @param content
	 *            字符串
	 * @param replacedToken
	 *            被替换的子字符串
	 * @param replaceStr
	 *            替换后的子字符串
	 * @return 替换完成过的字符串
	 */
	public static String replaceToken(String content, String replacedToken,
			String replaceStr) {
		StringBuffer s = new StringBuffer(content);
		int pos = 0;

		while ((pos = s.toString().indexOf(replacedToken)) >= 0) {
			s.delete(pos, pos + replacedToken.length());
			s.insert(pos, replaceStr);
		}
		return s.toString();
	}
}
