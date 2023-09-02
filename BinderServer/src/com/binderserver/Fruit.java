package com.binderserver;

import android.os.Parcel;
import android.os.Parcelable;

public class Fruit implements Parcelable {

	private String name;
	private String color;
	private int number;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public static final Parcelable.Creator<Fruit> CREATOR = new Creator<Fruit>() {

		@Override
		public Fruit createFromParcel(Parcel source) {
			Fruit fruit = new Fruit();
			fruit.name = source.readString();
			fruit.color = source.readString();
			fruit.number = source.readInt();
			return fruit;
		}

		@Override
		public Fruit[] newArray(int size) {
			return new Fruit[size];
		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(color);
		dest.writeInt(number);

	}

}
