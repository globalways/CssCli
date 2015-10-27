package com.globalways.cvsb.ui.stock;

import com.globalways.cvsb.R;
import com.globalways.cvsb.ui.BaseFragmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class StockActivity extends BaseFragmentActivity implements OnClickListener{

	private TextView tvCenter;
	private ImageButton imgBtnLeft;
	private StockOverviewFragment mStockOverviewFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stock_activity);
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
		showFragment();
	}
	
	private void initView(){
		tvCenter = (TextView) findViewById(R.id.textCenter);
		tvCenter.setText("库存");
		
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setVisibility(View.VISIBLE);
		imgBtnLeft.setOnClickListener(this);
		
		mStockOverviewFragment = new StockOverviewFragment();
	}
	
	
	private void showFragment(){
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.viewContainer, mStockOverviewFragment).show(mStockOverviewFragment).commit();
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		mStockOverviewFragment.onActivityResult(arg0, arg1, arg2);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBtnLeft:
			finish();
			break;

		default:
			break;
		}
	}
}
