package com.globalways.cvsb.ui.purchase;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alertdialogpro.AlertDialogPro;
import com.globalways.cvsb.R;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.ui.BaseFragmentActivity;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.MyDialogManager;

/**
 * 进货渠道管理
 * 
 * @author james
 *
 */
public class PurchaseActivity extends BaseFragmentActivity implements OnClickListener, OnCheckedChangeListener {

	/**
	 * default page size
	 */
	public static final int DEFAUL_PAGE_SIZE = 12;
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
	
	@Override
	public void onBackPressed() {
		UITools.ToastMsg(this, getString(R.string.common_disable_backkey_notice));
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
			textCenter.setText("供应商管理");
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
	
	@Override
	protected void onResume() {
		super.onResume();
		if(MyApplication.getCurrentEntity()==null){
			UITools.ToastMsg(this, "无法获取本店信息，即将退出系统");
			this.finish();
			return;
		}
		if(MyApplication.getCurrentEntity().getStatus() == 2){
			AlertDialogPro dialog = MyDialogManager.builder(this).setTitle("提示")
			.setMessage("当前店铺已被平台锁定，停止服务。请联系平台解锁店铺")
			.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					PurchaseActivity.this.finish();
				}
			}).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.show();
		}
	}
}
