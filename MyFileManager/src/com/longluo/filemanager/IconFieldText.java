package com.longluo.filemanager;

import android.graphics.drawable.Drawable;

public class IconFieldText implements Comparable<IconFieldText> {
	/* 文件名 */
	private String mText = "";
	/* 文件的图标ICNO */
	private Drawable mIcon = null;
	/* 能否选中 */
	private boolean mSelectable = true;

	public IconFieldText(String text, Drawable bullet) {
		mIcon = bullet;
		mText = text;
	}

	// 是否可以选中
	public boolean isSelectable() {
		return mSelectable;
	}

	// 设置是否可用选中
	public void setSelectable(boolean selectable) {
		mSelectable = selectable;
	}

	// 得到文件名
	public String getText() {
		return mText;
	}

	// 设置文件名
	public void setText(String text) {
		mText = text;
	}

	// 设置图标
	public void setIcon(Drawable icon) {
		mIcon = icon;
	}

	// 得到图标
	public Drawable getIcon() {
		return mIcon;
	}

	// 比较文件名是否相同
	public int compareTo(IconFieldText other) {
		if (this.mText != null)
			return this.mText.compareTo(other.getText());
		else
			throw new IllegalArgumentException();
	}
}
