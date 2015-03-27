package com.globalways.csscli.ui.statistics;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseFragmentActivity;

/**
 * 统计
 * 
 * @author james
 *
 */
public class StatisticsActivity extends BaseFragmentActivity implements OnClickListener {

	private TextView textCenter;
	private ImageButton imgBtnLeft;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.statistics_activity);
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

	/** 初始化UI、设置监听 */
	private void initView() {
		imgBtnLeft = (ImageButton) findViewById(R.id.imgBtnLeft);
		imgBtnLeft.setOnClickListener(this);
		imgBtnLeft.setVisibility(View.VISIBLE);

		textCenter = (TextView) findViewById(R.id.textCenter);
		textCenter.setText("统计");
	}
}
