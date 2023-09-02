package com.longluo.weather;


//实时天气信息处理(当前天气信息)
/**
 * <current_conditions> <condition data="多云"/> <temp_f data="88"/> <temp_c
 * data="31"/> <humidity data="湿度： 58%"/> <icon
 * data="/ig/images/weather/cn_cloudy.gif"/> <wind_condition
 * data="风向： 东、风速：4 米/秒"/> </current_conditions>
 */

public class WeatherCurrentCondition {

	private String condition; // 多云
	private String temp_celcius; // 摄氏温度
	private String temp_fahrenheit; // 华氏温度
	private String humidity; // 湿度:58%
	private String wind_condition; // 风向...
	private String icon; // 图标

	public WeatherCurrentCondition() {

	}

	// 得到Condition（多云）
	public String getCondition() {
		return condition;
	}

	// 设置Condition（多云）
	public void setCondition(String condition) {
		this.condition = condition;
	}

	// 得到设置温度
	public String getTemp_c() {
		return temp_celcius;
	}

	// 得到华氏温度
	public String getTemp_f() {
		return temp_fahrenheit;
	}

	// 设置摄氏温度
	public void setTemp_celcius(String temp_celcius) {
		this.temp_celcius = temp_celcius;
	}

	// 设置华氏温度
	public void setTemp_fahrenheit(String temp_fahrenheit) {
		this.temp_fahrenheit = temp_fahrenheit;
	}

	// 得到（湿度:58%）
	public String getHumidity() {
		return humidity;
	}

	// 设置（湿度:58%）
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	// 得到风向指示
	public String getWind_condition() {
		return wind_condition;
	}

	// 设置风向指示
	public void setWind_condition(String wind_condition) {
		this.wind_condition = wind_condition;
	}

	// 得到图标地址
	public String getIcon() {
		return icon;
	}

	// 设置图标地址
	public void setIcon(String icon) {
		this.icon = icon;
	}

	// 得到一个封装打包的字符串，包括除icno外的所有东西
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("实时天气: ").append(temp_celcius).append(" °C");
		sb.append(" ").append(temp_fahrenheit).append(" F");
		sb.append(" ").append(condition);
		sb.append(" ").append(humidity);
		sb.append(" ").append(wind_condition);
		return sb.toString();
	}
}
