package com.longluo.weather;

//预报后四天的天气信息
/**
 * <forecast_conditions> <day_of_week data="周二"/> <low data="24"/> <high
 * data="33"/> <icon data="/ig/images/weather/chance_of_rain.gif"/> <condition
 * data="可能有雨"/> </forecast_conditions>
 */
public class WeatherForecastCondition {

	private String day_of_week; // 星期
	private String low; // 最低温度
	private String high; // 最高温度
	private String icon; // 图标
	private String condition; // 提示

	public WeatherForecastCondition() {

	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getDay_of_week() {
		return day_of_week;
	}

	public void setDay_of_week(String day_of_week) {
		this.day_of_week = day_of_week;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(" ").append(day_of_week);
		sb.append(" : ").append(high);
		sb.append("/").append(low).append(" °C");
		sb.append(" ").append(condition);
		return sb.toString();
	}
}