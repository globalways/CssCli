package com.globalways.csscli.ui.account;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.globalways.csscli.R;
import com.globalways.csscli.http.manager.LoginManager;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.tools.MD5;
import com.globalways.csscli.ui.BaseActivity;
import com.globalways.csscli.ui.UITools;
import com.globalways.csscli.view.ClearableEditText;
import com.globalways.csscli.view.SimpleProgressDialog;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private ClearableEditText editAccount, editPsd;
	/** 进度条 **/
	private SimpleProgressDialog mSimpleProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_login_activity);
		// 布局内容会从view以下开始
		findViewById(R.id.view).setFitsSystemWindows(true);
		initView();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnLogin:
			toLogin();
			break;
		}
	}

	private void toLogin() {
		String username = editAccount.getText().toString().trim();
		if (username == null || username.isEmpty()) {
			UITools.ToastMsg(LoginActivity.this, "请输入帐号");
		}
		String password = editPsd.getText().toString().trim();
		if (password == null || password.isEmpty()) {
			UITools.ToastMsg(LoginActivity.this, "请输入密码");
		}
		mSimpleProgressDialog.showDialog();
		new LoginManager().toLogin(this, username, new MD5().getMD5(password), new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				mSimpleProgressDialog.cancleDialog();
				UITools.jumpMainActivity(LoginActivity.this);
				UITools.ToastMsg(LoginActivity.this, "登录成功");
				LoginActivity.this.finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				UITools.ToastMsg(LoginActivity.this, msg);
				mSimpleProgressDialog.cancleDialog();
			}
		});
	}

	private void initView() {
		findViewById(R.id.btnLogin).setOnClickListener(this);
		editAccount = (ClearableEditText) findViewById(R.id.editAccount);
		editPsd = (ClearableEditText) findViewById(R.id.editPsd);

		mSimpleProgressDialog = new SimpleProgressDialog(this, true);
		mSimpleProgressDialog.setText("正在登录…");
	}
}
