package com.globalways.csscli.ui.cashier;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.entity.ShoppingEntity;
import com.globalways.csscli.ui.BaseFragmentActivity;

/**
 * 收银台
 * 
 * @author james
 *
 */
public class CashierActivity extends BaseFragmentActivity implements OnClickListener {

	private TextView textLeft, textCenter;
	private View layoutContainer;
	private ListView listViewShopping;
	private CashierListAdapter cashierListAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.cashier_fragment);
		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.textleft:
			finish();
			break;
		}
	}

	/** 初始化UI、设置监听 */
	private void initView() {
		textLeft = (TextView) findViewById(R.id.textleft);
		textLeft.setText("返回");
		textLeft.setVisibility(View.VISIBLE);
		textLeft.setOnClickListener(this);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("收银台");
		textCenter.setVisibility(View.VISIBLE);

//		layoutContainer = findViewById(R.id.layoutContainer);
//		layoutContainer.setVisibility(View.GONE);

		listViewShopping = (ListView) findViewById(R.id.listViewShopping);
		cashierListAdapter = new CashierListAdapter(this);
		listViewShopping.setAdapter(cashierListAdapter);
		
		ShoppingEntity entity=new ShoppingEntity();
		entity.setProduct_name("美味辣条");
		entity.setProduct_price(300);
		entity.setProduct_unit("袋");
		entity.setShoppingNumber(5);
		List<ShoppingEntity> list=new ArrayList<ShoppingEntity>();
		list.add(entity);
		cashierListAdapter.setData(list);
	}
}
