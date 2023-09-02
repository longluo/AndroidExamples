package com.longluo.weather;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleWeatherInfoView extends LinearLayout
{
	private ImageView	myWeatherImageView	= null;
	private TextView	myTempTextView		= null;


	public SingleWeatherInfoView(Context context)
	{
		super(context);
	}


	public SingleWeatherInfoView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		this.myWeatherImageView = new ImageView(context);
		this.myWeatherImageView.setPadding(10, 5, 5, 5);

		this.myTempTextView = new TextView(context);
		this.myTempTextView.setTextColor(R.color.black);
		this.myTempTextView.setTextSize(16);

		this.addView(this.myWeatherImageView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(this.myTempTextView, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}


	public void setWeatherString(String aWeatherString)
	{
		this.myTempTextView.setText(aWeatherString);
	}


	public void setWeatherIcon(URL aURL){
		try{
			URLConnection conn = aURL.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			Bitmap bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();
			this.myWeatherImageView.setImageBitmap(bm);
		}catch (Exception e){}
	}
}
