package com.globalways.csscli.ui.cashier;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.ui.BaseFragmentActivity;

/**
 * 收银台
 * 
 * @author James.Fan
 *
 */
public class CashierActivity extends BaseFragmentActivity implements OnClickListener {

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

	/** 显示下单窗口 */
	public void showSignDialog() {
		dialogContainer.setVisibility(View.VISIBLE);
		cashierOrderFragment.setOrderData(cashierListAdapter.getCashierList(), cashierListAdapter.getTotalPrice());
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

		ProductEntity entity = new ProductEntity();
		entity.setProduct_name("美味辣条");
		entity.setProduct_price(300);
		entity.setProduct_unit("袋");
		entity.setShoppingNumber(5);
		List<ProductEntity> list = new ArrayList<ProductEntity>();
		list.add(entity);
		cashierListAdapter.setData(list);
	}
}
