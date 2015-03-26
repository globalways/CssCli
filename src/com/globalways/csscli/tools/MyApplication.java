package com.globalways.csscli.tools;

import android.app.Application;

import com.globalways.csscli.entity.UserEntity;
import com.globalways.csscli.ui.AppManager;

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
	private static UserEntity userEntity;

	public static long getStoreid() {
		return storeid;
	}

	public static void setStoreid(long storeid) {
		MyApplication.storeid = storeid;
	}

	public static UserEntity getUserEntity() {
		if (userEntity == null) {
			AppManager.getAppManager().finishAllActivity();
			return null;
		}
		return userEntity;
	}

	public static void setUserEntity(UserEntity userEntity) {
		MyApplication.userEntity = userEntity;
	}

}