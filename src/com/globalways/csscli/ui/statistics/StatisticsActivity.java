package com.globalways.csscli.ui.statistics;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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

	private TextView textLeft, textCenter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.statistics_activity);
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
		textCenter.setText("统计");
		textCenter.setVisibility(View.VISIBLE);
	}
}
