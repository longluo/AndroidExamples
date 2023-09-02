package com.longluo.weather;

import java.util.ArrayList;

public class WeatherSet {
	// 实时天气信息
	private WeatherCurrentCondition myCurrentCondition = null;
	// 预报的后四天的天气信息
	private ArrayList<WeatherForecastCondition> myForecastConditions = new ArrayList<WeatherForecastCondition>();

	public WeatherSet() {

	}

	// 得到实时天气信息的对象
	public WeatherCurrentCondition getMyCurrentCondition() {
		return myCurrentCondition;
	}

	// 设置实时天气信息的对象
	public void setMyCurrentCondition(WeatherCurrentCondition myCurrentCondition) {
		this.myCurrentCondition = myCurrentCondition;
	}

	// 得到预报天气
	public ArrayList<WeatherForecastCondition> getMyForecastConditions() {
		return myForecastConditions;
	}

	// 得到最后一个预报天气
	// 这里我们每次添加一个数据都是在最后
	// 所以得到最后一个
	public WeatherForecastCondition getLastForecastCondition() {
		return myForecastConditions.get(myForecastConditions.size() - 1);
	}
}
