package com.globalways.csscli.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseFragment;

/**
 * 添加商品弹出层
 * 
 * @author James
 *
 */
public class ProductAddNewFragment extends BaseFragment {

	private View layoutView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.product_addnew_fragment, container, false);
		}
		return layoutView;
	}

}
