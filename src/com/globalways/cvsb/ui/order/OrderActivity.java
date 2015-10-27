package com.globalways.cvsb.ui.order;

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

import com.globalways.cvsb.R;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.ui.BaseFragmentActivity;
import com.globalways.cvsb.ui.cashier.CashierOrderFragment;

/**
 * 订单管理
 * 
 * @author james & wyp
 *
 */
public class OrderActivity extends BaseFragmentActivity implements OnClickListener, OnCheckedChangeListener {

	//fragment ids
	private static final int FRAGMENT_ORDER = 0;
	private static final int FRAGMENT_SETTLE = 1;
	private int current = FRAGMENT_ORDER;
	//fragment
	private OrderFragment mOrderFragment;
	private OrderSettleFragment mOrderSettleFragment;
	private CashierOrderFragment cashierOrderFragment;
	private Fragment[] fragmentArray;
	private View dialogContainer;
	
	private TextView textCenter;
	private ImageButton imgBtnLeft;
	private View layoutContainer;
	//radios
	private RadioButton rbOrder, rbSettle;
	

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.order_activity);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.rbOrder:
			if(isChecked && current != FRAGMENT_ORDER ){
				switchToFragment(FRAGMENT_ORDER);
				textCenter.setText("订单管理");
			}
			break;
		case R.id.rbSettle:
			if(isChecked && current != FRAGMENT_SETTLE){
				switchToFragment(FRAGMENT_SETTLE);
				textCenter.setText("结算工单");
			}
			break;
		default:
			break;
		}
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		
		//title bar
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);
		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("订单管理");
		
		//container
		layoutContainer = findViewById(R.id.viewContainer);
		
		//radio buttons
		rbOrder = (RadioButton) findViewById(R.id.rbOrder);
		rbOrder.setOnCheckedChangeListener(this);
		rbSettle = (RadioButton) findViewById(R.id.rbSettle);
		rbSettle.setOnCheckedChangeListener(this);
		
		//fragment init
		mOrderFragment = new OrderFragment();
		mOrderSettleFragment = new OrderSettleFragment();
		fragmentArray = new Fragment[] { mOrderFragment, mOrderSettleFragment };
		switchToFragment(current);
		
		dialogContainer = findViewById(R.id.dialogContainer);
		dialogContainer.setVisibility(View.GONE);
		cashierOrderFragment = new CashierOrderFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.dialogContainer, cashierOrderFragment)
				.show(cashierOrderFragment).commit();
	}
	
	/**
	 * switch fragment
	 * @param targetFragment
	 */
	private void switchToFragment(int targetFragment){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.hide(fragmentArray[current]);
		if( !fragmentArray[targetFragment].isAdded() ){
			ft.add(R.id.viewContainer, fragmentArray[targetFragment]);
		}
		ft.show(fragmentArray[targetFragment]).commit();
		current = targetFragment;
	}

	public void showPayDialog(OrderEntity order){
		dialogContainer.setVisibility(View.VISIBLE);
		cashierOrderFragment.showOrderStepTwo(order);
	}
	/**
	 * 隐藏支付订单窗口
	 */
	public void hidePayDialog() {
		dialogContainer.setVisibility(View.GONE);
		//支付完成，刷新UI
		mOrderFragment.paySuccess();
	}
	
	
	
}
