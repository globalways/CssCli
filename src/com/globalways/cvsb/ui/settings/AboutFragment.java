package com.globalways.cvsb.ui.settings;

import com.globalways.cvsb.R;
import com.globalways.cvsb.ui.BaseFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends BaseFragment {
	private View fragmentView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if(fragmentView == null){
			fragmentView = inflater.inflate(R.layout.settings_about_fragment, container, false);
		}
		return fragmentView;
	}
}
