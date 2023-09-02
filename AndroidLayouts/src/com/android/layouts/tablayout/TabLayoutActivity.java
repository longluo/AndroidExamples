package com.android.layouts.tablayout;

import com.android.layouts.R;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class TabLayoutActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tab_layout);

		Resources res = getResources();
		// Resource object to get Drawables
		TabHost tabHost = getTabHost();
		// The activity TabHost
		TabHost.TabSpec spec;
		// Resusable TabSpec for each tab
		Intent intent;
		// Reusable Intent for each tab
		// Create an Intent to launch an Activity for the tab (to be reused)
		intent = new Intent().setClass(this, MmsActivity.class); // Initialize
																	// a
																	// TabSpec
																	// for
																	// each
																	// tab
																	// and
																	// add
																	// it to
																	// the
																	// TabHost
		spec = tabHost.newTabSpec("mms")
				.setIndicator("Mms", res.getDrawable(R.drawable.tab_mms))
				.setContent(intent);
		tabHost.addTab(spec);
		// Do the same for the other tabs
		intent = new Intent().setClass(this, DialActivity.class);
		spec = tabHost.newTabSpec("Dial")
				.setIndicator("Dial", res.getDrawable(R.drawable.tab_dial))
				.setContent(intent);
		tabHost.addTab(spec);
		intent = new Intent().setClass(this, ContactsActivity.class);
		spec = tabHost
				.newTabSpec("contacts")
				.setIndicator("Contacts", res.getDrawable(R.drawable.tab_contacts))
				.setContent(intent);
		tabHost.addTab(spec);
		tabHost.setCurrentTab(2);
	}
}
