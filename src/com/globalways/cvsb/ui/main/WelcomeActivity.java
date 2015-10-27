package com.globalways.cvsb.ui.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import cn.jpush.android.api.JPushInterface;
import im.fir.sdk.FIR;
import im.fir.sdk.callback.VersionCheckCallback;
import im.fir.sdk.version.AppVersion;

import com.globalways.cvsb.R;
import com.globalways.cvsb.Config;
import com.globalways.cvsb.http.manager.AppManager;
import com.globalways.cvsb.http.manager.LoginManager;
import com.globalways.cvsb.http.manager.ManagerCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.SharedPreferencesHelper;
import com.globalways.cvsb.tools.Tool;
import com.globalways.cvsb.ui.UITools;
import com.globalways.cvsb.ui.settings.UpdateFragment;
import com.globalways.cvsb.view.MyDialogManager;

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
				//toCheckVersion();
				//检查版本更新
				toCheckVersionFIR(WelcomeActivity.this);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}
		});
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    JPushInterface.onResume(this);
	    JPushInterface.clearAllNotifications(this);
	    JPushInterface.clearLocalNotifications(this);
	}
	@Override
	protected void onPause() {
	    super.onPause();
	    JPushInterface.onPause(this);
	}

	private void toCheckVersion() {
		new AppManager().toCheckVersion(this, new ManagerCallBack<String>() {
			@Override
			public void onSuccess(final String url) {
				super.onSuccess(url);
				AlertDialog.Builder builder = MyDialogManager.builder(WelcomeActivity.this);
				builder.setMessage("当前有版本更新！会有新的功能开放哦！");
				builder.setTitle("提示！");
				builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//采用浏览器打开URL
						//采用系统下载
//						UITools.jumpAppUpdateActivity(WelcomeActivity.this, url);
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
	
	public void toCheckVersionFIR(final Context context){
		FIR.checkForUpdateInFIR(Config.FIR_APPID , new VersionCheckCallback() {

			@Override
			public void onError(Exception arg0) {
				UITools.ToastMsg(context, "无法获取最新版本："+arg0);
				toLogin();
			}

			@Override
			public void onFail(String arg0, int arg1) {
				UITools.ToastMsg(context, "无法获取最新版本："+arg0);
				toLogin();
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onStart() {
			}

			@Override
			public void onSuccess(final AppVersion arg0, boolean arg1) {
				if(arg0.getVersionName().equals(MyApplication.mPackageInfo.versionName) && arg0.getVersionCode()==MyApplication.mPackageInfo.versionCode){
					toLogin();
				}else{
					StringBuilder sb = new StringBuilder();
					sb.append("版本:").append(arg0.getVersionName()).append("\n").
					append("版本号:").append(arg0.getVersionCode()).append("\n");
					if(!Tool.isEmpty(arg0.getChangeLog()))
					  sb.append("更新日志:").append(arg0.getChangeLog()).append("\n");
					AlertDialog.Builder builder = MyDialogManager.builder(context);
					builder.setMessage(sb);
					builder.setTitle("发现新版本！");
					builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Uri uri = Uri.parse(arg0.getUpdateUrl());  
							Intent it = new Intent(Intent.ACTION_VIEW, uri);  
							context.startActivity(it);
							WelcomeActivity.this.finish();
						}
					});
					builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							toLogin();
						}
					});
					builder.create().show();
				}
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
				UITools.jumpLoginActivity(WelcomeActivity.this);
//				UITools.jumpMainActivity(WelcomeActivity.this);
				WelcomeActivity.this.finish();
			}
		});
	}
}
