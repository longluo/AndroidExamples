package com.wordpress.enjoyandroid.viewbinder;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class DefaultActivity extends ListActivity {

	private static final String[] UI_BINDING_FROM = new String[] {
		ClubCP.KEY_NAME, ClubCP.KEY_IS_STAR
	};
	
	private static final int[] UI_BINDING_TO = new int[] {
		R.id.name, R.id.star
	};
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// managedQuery takes care of the lifecycle of the cursor.
		// it will close the cursor itself.
		// However, the use of managedQuery is discouraged and Loaders are now
		// the prefered way of loading data in activity. It is a topic for the 
		// future.
		Cursor cursor = managedQuery(ClubCP.CONTENT_URI, null, null, null, null);

		// adapter to show the data. Go through android docs about SimpleCursorAdapter.
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(
				getApplicationContext(), R.layout.club_row, cursor, UI_BINDING_FROM,
				UI_BINDING_TO);
		
		// We set the view binder for the adapter to our own CustomViewBinder.
		// The code for the custom view binder is below.
		adapter.setViewBinder(new CustomViewBinder());
		
		// Provides the cursor for the list view. The list view should be defined
		// in main.xml
		setListAdapter(adapter);
	}
	
	/**
	 * Custom ViewBinder to handle custom view showing in SimpleCursorAdapter.
	 * @author gautham
	 *
	 */
	private class CustomViewBinder implements ViewBinder {

		@Override
		public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
			if (columnIndex == cursor.getColumnIndex(ClubCP.KEY_IS_STAR)) {
				// If the column is IS_STAR then we use custom view.
				int is_star = cursor.getInt(columnIndex);
				if (is_star != 1) {
					// set the visibility of the view to GONE
					view.setVisibility(View.GONE);
				} else {
					/*
					 *  Otherwise set the view to be visible.
					 *  You might ask that the view is visible by default so why we should
					 *  set the visibility explicitly. Android system reuses the list
					 *  rows so when it reuses any row which has Visibility gone to this row,
					 *  the visibility will still be gone. So we need to.  
					 */
					view.setVisibility(View.VISIBLE);
				}
				return true;
			}
			// For others, we simply return false so that the default binding
			// happens.
			return false;
		}
		
	}
}