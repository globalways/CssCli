package com.globalways.cvsb.ui.account;

import com.globalways.cvsb.R;
import com.globalways.cvsb.http.manager.LoginManager;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.tools.MD5;
import com.globalways.cvsb.ui.BaseNoTitleActivity;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.view.ClearableEditText;
import com.globalways.cvsb.view.SimpleProgressDialog;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class LoginActivity extends BaseNoTitleActivity implements OnClickListener {

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
