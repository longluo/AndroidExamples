package com.android.actionbar.googlenative;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListFragment extends Fragment {
	private static final String TAG = "ListFragment";
	private String tag;

	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "onAttach");
		tag = getTag();
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		TextView textView = new TextView(getActivity());
		textView.setText(tag);
		Log.i(TAG, "onCreateView");
		return textView;

	}

}
