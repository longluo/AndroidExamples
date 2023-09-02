package com.longluo.GameEngine.Material;

//边界测量：测量物体是否出边界
public class Measure {
	// 测量类型：在矩形中检测
	public static final int RECTANGLE_MEASURE = 1;

	/**
	 * 判断是否出边界
	 * 
	 * @param x
	 *            物体的x坐标
	 * @param y
	 *            物体的y坐标
	 * @param border
	 *            边界对象
	 * @param type
	 *            测量类型
	 * @param superposition
	 *            判断边界时是否包含与边界重合的情况
	 * @return 是否出边界
	 */
	public static boolean isOutOfBorder(int x, int y, Border border, int type,
			boolean superposition) {
		boolean result = false;

		switch (type) {
		case RECTANGLE_MEASURE:
			// 如果包含与边界重合的情况
			if (superposition) {
				if ((x > border.getMaxX()) || (x < border.getMinX())
						|| (y > border.getMaxY()) || (y < border.getMinY())) {
					result = true;
				} else {
					result = false;
				}
			} else {
				if ((x >= border.getMaxX()) || (x <= border.getMinX())
						|| (y >= border.getMaxY()) || (y <= border.getMinY())) {
					result = true;
				} else {
					result = false;
				}
			}
			break;
		}
		return result;
	}
}
