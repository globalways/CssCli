package com.globalways.csscli.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.globalways.csscli.R;
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
				toLogin();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
	}

	private void toLogin() {
		String username = SharedPreferencesHelper.getInstance(this).getUserName();
		if (username == null || username.isEmpty()) {
			UITools.jumpLoginActivity(WelcomeActivity.this);
			WelcomeActivity.this.finish();
		}
		String password = SharedPreferencesHelper.getInstance(this).getUserPsd();
		if (password == null || password.isEmpty()) {
			UITools.jumpLoginActivity(WelcomeActivity.this);
			WelcomeActivity.this.finish();
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
				UITools.jumpLoginActivity(WelcomeActivity.this);
				WelcomeActivity.this.finish();
			}
		});
	}
}
