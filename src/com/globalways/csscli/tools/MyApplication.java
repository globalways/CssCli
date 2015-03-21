package com.globalways.csscli.tools;

import android.app.Application;

/**
 * @author James
 *
 */
public class MyApplication extends Application {

	public static MyApplication mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;
		// 保存漰溃日志信息
		// CrashHandler crashHandler = CrashHandler.getInstance();
		// crashHandler.init(getApplicationContext());
	}

	private static long storeid = 46758;

	public static long getStoreid() {
		return storeid;
	}

	public static void setStoreid(long storeid) {
		MyApplication.storeid = storeid;
	}

}