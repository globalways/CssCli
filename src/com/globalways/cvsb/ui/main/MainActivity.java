package com.globalways.cvsb.ui.main;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.globalways.cvsb.R;
import com.globalways.cvsb.ui.BaseNoTitleActivity;
import com.globalways.cvsb.ui.UITools;

public class MainActivity extends BaseNoTitleActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initView();
	}

	private void initView() {
		findViewById(R.id.btnStoreManager).setOnClickListener(this);
		findViewById(R.id.btnProductManager).setOnClickListener(this);
		findViewById(R.id.btnEmployeeManager).setOnClickListener(this);
		findViewById(R.id.btnCashierManager).setOnClickListener(this);
		findViewById(R.id.btnPurchaseManager).setOnClickListener(this);
		findViewById(R.id.btnOrderManager).setOnClickListener(this);
		findViewById(R.id.btnStockManager).setOnClickListener(this);
		findViewById(R.id.btnStatistics).setOnClickListener(this);
		findViewById(R.id.btnSettings).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStoreManager:
			UITools.ToastMsg(this, "正在开发，敬请期待……");
			break;
		case R.id.btnProductManager:
			UITools.jumpProductActivity(this);
			break;
		case R.id.btnEmployeeManager:
			UITools.ToastMsg(this, "正在开发，敬请期待……");
			break;
		case R.id.btnCashierManager:
			UITools.jumpCashierActivity(this);
			break;
		case R.id.btnPurchaseManager:
			UITools.jumpPerchaseActivity(this);
			break;
		case R.id.btnOrderManager:
			UITools.jumpOrderActivity(this);
			break;
		case R.id.btnStockManager:
			UITools.jumpStockActivity(this);
			break;
		case R.id.btnStatistics:
			UITools.jumpStatisticsActivity(this);
			break;
		case R.id.btnSettings:
			UITools.jumpSettingsActivity(this);
			break;
		}
	}

}
