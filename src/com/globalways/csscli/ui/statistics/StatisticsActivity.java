package com.globalways.csscli.ui.statistics;

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
 * 统计
 * 
 * @author james
 *
 */
public class StatisticsActivity extends BaseFragmentActivity implements OnClickListener, OnCheckedChangeListener {

	private static final int CODE_SALL = 0;
	private static final int CODE_BUY = 1;
	private int currentItem = CODE_SALL;

	private TextView textCenter;
	private ImageButton imgBtnLeft;
	private RadioButton radioSallStatistics, radioBuyStatistics;
	private StatisticsBuyFragment buyFragment;
	private StatisticsSellFragment sellFragment;
	private Fragment[] fragmentArray;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.statistics_activity);
		// 布局内容会从view以下开始
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
		textCenter.setText("统计");

		radioSallStatistics = (RadioButton) findViewById(R.id.radioSallStatistics);
		radioSallStatistics.setOnCheckedChangeListener(this);
		radioBuyStatistics = (RadioButton) findViewById(R.id.radioBuyStatistics);
		radioBuyStatistics.setOnCheckedChangeListener(this);
	}

	/**
	 * 初始化Fragment，默认显示销售内容
	 */
	private void initFragment() {
		sellFragment = new StatisticsSellFragment();
		buyFragment = new StatisticsBuyFragment();
		fragmentArray = new Fragment[] { sellFragment, buyFragment };
		radioSallStatistics.setChecked(true);
		getSupportFragmentManager().beginTransaction().add(R.id.viewContainer, sellFragment).show(sellFragment)
				.commit();

		textCenter.setText("销售统计");
	}

	private void switchFragment(int targetFragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.hide(fragmentArray[currentItem]);
		if (!fragmentArray[targetFragment].isAdded()) {
			transaction.add(R.id.viewContainer, fragmentArray[targetFragment]);
		}
		transaction.show(fragmentArray[targetFragment]);
		transaction.commit();
		currentItem = targetFragment;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.radioSallStatistics:
			if (isChecked && currentItem != CODE_SALL) {
				switchFragment(CODE_SALL);
				textCenter.setText("销售统计");
			}
			break;
		case R.id.radioBuyStatistics:
			if (isChecked && currentItem != CODE_BUY) {
				switchFragment(CODE_BUY);
				textCenter.setText("采购统计");
			}
			break;
		}
	}
}
