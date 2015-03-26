package com.globalways.csscli.ui.main;

import com.globalways.csscli.R;
import com.globalways.csscli.ui.UITools;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

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
				UITools.jumpMainActivity(WelcomeActivity.this);
				// UITools.jumpLoginActivity(WelcomeActivity.this);
				WelcomeActivity.this.finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
	}

}
