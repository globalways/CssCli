package com.globalways.csscli.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.globalways.csscli.R;
import com.globalways.csscli.http.manager.AppManager;
import com.globalways.csscli.http.manager.LoginManager;
import com.globalways.csscli.http.manager.ManagerCallBack;
import com.globalways.csscli.tools.SharedPreferencesHelper;
import com.globalways.csscli.ui.UITools;

public class WelcomeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_welcome_activity);

		ImageView imageWelcome = (ImageView) findViewById(R.id.imageWelcome);

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		imageWelcome.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				toCheckVersion();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
	}

	private void toCheckVersion() {
		new AppManager().toCheckVersion(this, new ManagerCallBack<String>() {
			@Override
			public void onSuccess(final String url) {
				super.onSuccess(url);
				AlertDialog.Builder builder = new Builder(WelcomeActivity.this);
				builder.setMessage("当前有版本更新！会有新的功能开放哦！");
				builder.setTitle("提示！");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						UITools.jumpAppUpdateActivity(WelcomeActivity.this, url);
						WelcomeActivity.this.finish();
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						toLogin();
					}
				});
				builder.create().show();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				toLogin();
			}
		});
	}

	private void toLogin() {
		String username = SharedPreferencesHelper.getInstance(this).getUserName();
		if (username == null || username.isEmpty()) {
			UITools.jumpLoginActivity(WelcomeActivity.this);
			WelcomeActivity.this.finish();
			return;
		}
		String password = SharedPreferencesHelper.getInstance(this).getUserPsd();
		if (password == null || password.isEmpty()) {
			UITools.jumpLoginActivity(WelcomeActivity.this);
			WelcomeActivity.this.finish();
			return;
		}
		new LoginManager().toLogin(this, username, password, new ManagerCallBack<String>() {
			@Override
			public void onSuccess(String returnContent) {
				super.onSuccess(returnContent);
				UITools.jumpMainActivity(WelcomeActivity.this);
				WelcomeActivity.this.finish();
			}

			@Override
			public void onFailure(int code, String msg) {
				super.onFailure(code, msg);
				//开发方便，暂禁登录
				//UITools.jumpLoginActivity(WelcomeActivity.this);
				UITools.jumpMainActivity(WelcomeActivity.this);
				WelcomeActivity.this.finish();
			}
		});
	}
}
