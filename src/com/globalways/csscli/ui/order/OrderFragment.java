package com.globalways.csscli.ui.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseFragment;

public class OrderFragment extends BaseFragment {
	private View fragmentView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(null ==  fragmentView){
			fragmentView = inflater.inflate(R.layout.order_fragment, container, false);
		}
		return fragmentView;
	}
}
