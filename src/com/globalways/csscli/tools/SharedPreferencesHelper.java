package com.globalways.csscli.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences数据存储类
 * 
 * @author James
 *
 */
public class SharedPreferencesHelper {
	static final String TAG = SharedPreferencesHelper.class.getSimpleName();
	static Context mContext;
	private static SharedPreferencesHelper mSharedPreferencesHelper;

	/** 错误记录 **/
	private static final String ERROR_FLAG = "errorFlag";

	private SharedPreferencesHelper() {
	}

	public static synchronized SharedPreferencesHelper getInstance(Context context) {
		if (null == mSharedPreferencesHelper) {
			mSharedPreferencesHelper = new SharedPreferencesHelper();
			mContext = context;
		}
		return mSharedPreferencesHelper;
	}

	/**
	 * 保存错误记录
	 */
	public synchronized void saveErrorFlag() {
		SharedPreferences errorFlag = mContext.getSharedPreferences(ERROR_FLAG, Context.MODE_PRIVATE);
		Editor editor = errorFlag.edit();
		editor.putString(ERROR_FLAG, "error");
		editor.commit();
		MyLog.d(TAG, "saveErrorFlag");
	}

	/**
	 * 清除错误记录
	 */
	public synchronized void clearErrorFlag() {
		SharedPreferences errorFlag = mContext.getSharedPreferences(ERROR_FLAG, Context.MODE_PRIVATE);
		Editor editor = errorFlag.edit();
		editor.remove(ERROR_FLAG);
		editor.commit();
		MyLog.d(TAG, "clearErrorFlag");
	}

	/**
	 * 获取程序是否存在过异常
	 * 
	 * @return
	 */
	public boolean isError() {
		SharedPreferences errorFlag = mContext.getSharedPreferences(ERROR_FLAG, Context.MODE_PRIVATE);
		String exception = errorFlag.getString(ERROR_FLAG, null);
		MyLog.d(TAG, "isError");
		return null != exception;
	}
}