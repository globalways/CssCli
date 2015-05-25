package com.globalways.csscli.ui.purchase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseFragmentActivity;

/**
 * 进货渠道管理
 * 
 * @author james
 *
 */
public class PurchaseActivity extends BaseFragmentActivity implements OnClickListener, OnCheckedChangeListener {

	private TextView textCenter;
	private ImageButton imgBtnLeft;
	private View layoutContainer;
	private RadioButton rbtnPurchase, rbtnSupplier;
	
	private PurchaseFragment mPurchaseFragment;
	private SupplierFragment mSupplierFragment;
	private String currentFragmentTag;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.purchase_activity);
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
		initFragment();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			finish();
			break;
		}
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("采购与供应");
		
		rbtnPurchase = (RadioButton) findViewById(R.id.rbtn_purchase);
		rbtnPurchase.setOnCheckedChangeListener(this);
		rbtnSupplier = (RadioButton) findViewById(R.id.rbtn_supplier);
		rbtnSupplier.setOnCheckedChangeListener(this);

		layoutContainer = findViewById(R.id.purchase_viewContainer);
		layoutContainer.setVisibility(View.VISIBLE);
		
	}
	
	private void initFragment()
	{
		mSupplierFragment = new SupplierFragment();
		mPurchaseFragment = new PurchaseFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(R.id.purchase_viewContainer, mPurchaseFragment);
		transaction.add(R.id.purchase_viewContainer, mSupplierFragment);
		transaction.commit();
		currentFragmentTag = "PUR";
		switchFragment();
	}
	
	private void switchFragment()
	{
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		switch(currentFragmentTag){
		case "PUR" :
			
			transaction.hide(mSupplierFragment);
			transaction.show(mPurchaseFragment);
			transaction.commit();
			textCenter.setText("采购管理");
			break;
		case "SUP" :
			transaction.hide(mPurchaseFragment);
			transaction.show(mSupplierFragment);
			transaction.commit();
			textCenter.setText("供应管理");
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rbtn_purchase:
			if(isChecked)
			{
				currentFragmentTag = "PUR";
				switchFragment();
			}
			break;
		case R.id.rbtn_supplier:
			if(isChecked)
			{
				currentFragmentTag = "SUP";
				switchFragment();
			}
			break;
		default:
			break;
		}
		
	}
}
