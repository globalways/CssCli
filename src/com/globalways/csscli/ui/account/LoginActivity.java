package com.globalways.csscli.ui.account;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.ui.UITools;

public class LoginActivity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_login_activity);
		initView();
	}

	private void initView() {
		findViewById(R.id.btnLogin).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			UITools.jumpMainActivity(this);
			finish();
			break;
		}
	}
}
