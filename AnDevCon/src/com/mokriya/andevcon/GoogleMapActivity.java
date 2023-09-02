package com.mokriya.andevcon;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class GoogleMapActivity extends MapActivity {

	private MapView mapView;
	private MapController mapController;
	private MyLocOverlay myLocationOverlay;
	private PushPinOverlay pushPinOverlay;
	private RouteOverlay myRouteOverlay;
	public boolean markSample;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// REPLACE "MapApiKey" with your own key from http://code.google.com/android/add-ons/google-apis/mapkey.html
		mapView = new MapView(this, "MapApiKey");
		mapView.setBuiltInZoomControls(true);
		mapView.setClickable(true);		
		mapController = mapView.getController();
		mapController.setZoom(18); // zoom levels between 1 and 21		
		setContentView(mapView);

		// Create a MyLocationOverlay
		myLocationOverlay = new MyLocOverlay(this, mapView);
		myLocationOverlay.enableMyLocation();
		mapView.getOverlays().add(myLocationOverlay);

		// Create a Pushpin/marker overlay
		pushPinOverlay = new PushPinOverlay();
		mapView.getOverlays().add(pushPinOverlay);
		
		//Create a Route overlay
		myRouteOverlay = new RouteOverlay();
		mapView.getOverlays().add(myRouteOverlay);

	}

	@Override
	protected boolean isRouteDisplayed() {
		return true;
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(1, 100, 1, "My Location");
		menu.add(1, 101, 2, "Place Marker");
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		
		switch (item.getItemId()) {
		case 100:
			if (myLocationOverlay.getMyLocation() != null) {
				mapController.animateTo(myLocationOverlay.getMyLocation());
			} else {
				mapController.setCenter(new GeoPoint((int)(37.5569 * 1E6), (int)(-122.3006 * 1E6)));
			}
			break;
		case 101:
			markSample = true;
			mapView.invalidate();
			break;
				
		}
		return true;
	}
	
	class MyLocOverlay extends MyLocationOverlay {

		public MyLocOverlay(Context context, MapView mapview) {
			super(context, mapview);
		}
		
		@Override
		public synchronized void onLocationChanged(Location location) {
			super.onLocationChanged(location);			
			mapController.animateTo(myLocationOverlay.getMyLocation());
		}
		
		@Override
		protected void drawMyLocation(Canvas arg0, MapView arg1, Location arg2,
				GeoPoint arg3, long arg4) {
		}
		
	}
	
	class PushPinOverlay extends Overlay {
		private GeoPoint geoPoint;
		
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			if (markSample && !shadow && myLocationOverlay.getMyLocation() != null) {
				markSample = false;
				geoPoint = myLocationOverlay.getMyLocation();
			}
			
			if (geoPoint != null) {
				Bitmap pushpin = BitmapFactory.decodeResource(GoogleMapActivity.this.getResources(), R.drawable.pushpin);
				Point point = mapView.getProjection().toPixels(geoPoint, null);
				canvas.drawBitmap(pushpin, point.x, point.y-pushpin.getHeight(), new Paint());
			}
		}
	}
	
	class RouteOverlay extends Overlay {
		private ArrayList<GeoPoint> trackPoints = new ArrayList<GeoPoint>();
		Paint paint = new Paint();
		
		public RouteOverlay() {
			paint.setColor(Color.BLUE);
		}
		
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {

			if (!shadow && myLocationOverlay.getMyLocation() != null) {
				// This could be improved to store less locations by implementing a Douglas-Peucker algorithm
				trackPoints.add(myLocationOverlay.getMyLocation());
			}

			for (int i = 0; i < trackPoints.size(); i++) {
				Point point = mapView.getProjection().toPixels(trackPoints.get(i), null);
				canvas.drawCircle(point.x, point.y, 5, paint);
			}

		}
	}
}
