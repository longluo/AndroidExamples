package com.longluo.weather;

import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Weather extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		Spinner city_spr = (Spinner) findViewById(R.id.Spinner01);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, ConstData.city);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		city_spr.setAdapter(adapter);

		Button submit = (Button) findViewById(R.id.Button01);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Spinner spr = (Spinner) findViewById(R.id.Spinner01);
				Long l = spr.getSelectedItemId();
				int index = l.intValue();
				String cityParamString = ConstData.cityCode[index];

				try {
					URL url = new URL(ConstData.queryString + cityParamString);
					getCityWeather(url);
				} catch (Exception e) {
					Log.e("CityWeather", e.toString());
				}

			}
		});

		Button submit_input = (Button) findViewById(R.id.Button001);
		submit_input.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				EditText inputcity = (EditText) findViewById(R.id.EditText001);
				String tmp = inputcity.getText().toString();

				try {
					URL url = new URL(ConstData.queryString_intput + tmp);
					getCityWeather(url);
				} catch (Exception e) {
					Log.e("Weather", e.toString());
				}
			}

		});
	}

	// 更新显示实时天气信息
	private void updateWeatherInfoView(int aResourceID,
			WeatherCurrentCondition aWCC) throws MalformedURLException {

		URL imgURL = new URL("http://www.google.com/" + aWCC.getIcon());
		((SingleWeatherInfoView) findViewById(aResourceID))
				.setWeatherIcon(imgURL);
		((SingleWeatherInfoView) findViewById(aResourceID))
				.setWeatherString(aWCC.toString());
	}

	// 更新显示天气预报
	private void updateWeatherInfoView(int aResourceID,
			WeatherForecastCondition aWFC) throws MalformedURLException {

		URL imgURL = new URL("http://www.google.com/" + aWFC.getIcon());
		((SingleWeatherInfoView) findViewById(aResourceID))
				.setWeatherIcon(imgURL);
		((SingleWeatherInfoView) findViewById(aResourceID))
				.setWeatherString(aWFC.toString());
	}

	// 获取天气信息
	// 通过网络获取数据
	// 传递给XMLReader解析
	public void getCityWeather(URL url) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			XMLReader xr = sp.getXMLReader();

			GoogleWeatherHandler gwh = new GoogleWeatherHandler();
			xr.setContentHandler(gwh);

			InputStreamReader isr = new InputStreamReader(url.openStream(),
					"GBK");
			InputSource is = new InputSource(isr);

			xr.parse(is);

			WeatherSet ws = gwh.getMyWeatherSet();

			updateWeatherInfoView(R.id.weather_0, ws.getMyCurrentCondition());
			updateWeatherInfoView(R.id.weather_1, ws.getMyForecastConditions()
					.get(0));
			updateWeatherInfoView(R.id.weather_2, ws.getMyForecastConditions()
					.get(1));
			updateWeatherInfoView(R.id.weather_3, ws.getMyForecastConditions()
					.get(2));
			updateWeatherInfoView(R.id.weather_4, ws.getMyForecastConditions()
					.get(3));
		} catch (Exception e) {
			Log.e("CityWeather", e.toString());
		}
	}
}
