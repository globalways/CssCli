package com.globalways.cvsb.ui.cashier;

import com.alertdialogpro.AlertDialogPro;
import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.ui.BaseFragmentActivity;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.MyDialogManager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 收银台
 * 
 * @author James.Fan
 *
 */
public class CashierActivity extends BaseFragmentActivity implements OnClickListener {

	
	//禁用返回键
	public static boolean isBlockBackKey = true;
	
	private TextView textCenter;
	private ImageButton imgBtnLeft;
	private View dialogContainer;
	private CashierOrderFragment cashierOrderFragment;
	private ListView listViewShopping;
	private CashierListAdapter cashierListAdapter;
	private CashierQRCodeFragment cashierQRCodeFragment;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cashier_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
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
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			if(isBlockBackKey){
//				UITools.ToastMsg(this, "返回键已经禁用，请通过程序界面操作");
//				return true;
//			}
//		}
//		return super.onKeyDown(keyCode, event);
//	}

	/** 显示下单窗口 */
	public void showSignDialog() {
		dialogContainer.setVisibility(View.VISIBLE);
		cashierOrderFragment.setOrderData(cashierListAdapter.getCashierList(), cashierListAdapter.getTotalPrice());
	}

	public int getCount() {
		return cashierListAdapter.getCount();
	}

	/**
	 * 隐藏下单窗口
	 * 
	 * @param isSign
	 *            true，已下单就就清空收银台购物车
	 */
	public void hideSignDialog(boolean isSign) {
		dialogContainer.setVisibility(View.GONE);
		if (isSign) {
			cashierListAdapter.clear();
			if (null != cashierQRCodeFragment) {
				cashierQRCodeFragment.orderFinish();
			}
		}
	}

	/**
	 * 向购物车添加商品
	 * 
	 * @param entity
	 */
	public void addCashierProduct(ProductEntity entity) {
		cashierListAdapter.addItem(entity);
	}

	/** 刷新总价 */
	public void setTotalPrice(long totalPrice) {
		if (null != cashierQRCodeFragment) {
			cashierQRCodeFragment.setTotalPrice(totalPrice);
		}
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("收银台");

		dialogContainer = findViewById(R.id.dialogContainer);
		dialogContainer.setVisibility(View.GONE);
		cashierOrderFragment = new CashierOrderFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.dialogContainer, cashierOrderFragment)
				.show(cashierOrderFragment).commit();

		cashierQRCodeFragment = new CashierQRCodeFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.layoutContainer, cashierQRCodeFragment)
				.show(cashierQRCodeFragment).commit();

		listViewShopping = (ListView) findViewById(R.id.listViewShopping);
		cashierListAdapter = new CashierListAdapter(this);
		listViewShopping.setAdapter(cashierListAdapter);
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
					CashierActivity.this.finish();
				}
			}).create();
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
			dialog.show();
		}
	}
	
}
