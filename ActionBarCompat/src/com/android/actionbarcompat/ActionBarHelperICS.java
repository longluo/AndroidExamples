package com.android.actionbarcompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;

/**
 * An extension of {@link com.example.android.actionbarcompat.ActionBarHelper}
 * that provides Android 4.0-specific functionality for IceCreamSandwich
 * devices. It thus requires API level 14.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ActionBarHelperICS extends ActionBarHelperHoneycomb {
	protected ActionBarHelperICS(Activity activity) {
		super(activity);
	}

	@Override
	protected Context getActionBarThemedContext() {
		return mActivity.getActionBar().getThemedContext();
	}
}
