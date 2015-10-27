package com.globalways.cvsb.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.globalways.cvsb.entity.UserEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
	/** 用户信息 */
	private static final String USER_INFO = "userInfo";
	private static final String USER_ACCOUNT = "userAccount";

	/** 用户帐号 */
	private class UserAccount {
		private static final String USER_NAME = "userName";
		private static final String USER_PSD = "userPsd";
	}

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
	 * 保存用户帐号
	 */
	public synchronized void saveUserAccount(String username, String password) {
		if (username == null || password == null) {
			return;
		}
		SharedPreferences errorFlag = mContext.getSharedPreferences(USER_ACCOUNT, Context.MODE_PRIVATE);
		Editor editor = errorFlag.edit();
		editor.putString(UserAccount.USER_NAME, username);
		editor.putString(UserAccount.USER_PSD, password);
		editor.commit();
		MyLog.d(TAG, "saveUserAccount");
	}

	/**
	 * 获取用户帐号
	 * 
	 * @return
	 */
	public synchronized String getUserName() {
		SharedPreferences errorFlag = mContext.getSharedPreferences(USER_ACCOUNT, Context.MODE_PRIVATE);
		String userInfo = errorFlag.getString(UserAccount.USER_NAME, null);
		if (userInfo == null || userInfo.isEmpty()) {
			return null;
		}
		MyLog.d(TAG, "getUserName");
		return userInfo;
	}

	/**
	 * 获取用户密码
	 * 
	 * @return
	 */
	public synchronized String getUserPsd() {
		SharedPreferences errorFlag = mContext.getSharedPreferences(USER_ACCOUNT, Context.MODE_PRIVATE);
		String userInfo = errorFlag.getString(UserAccount.USER_PSD, null);
		if (userInfo == null || userInfo.isEmpty()) {
			return null;
		}
		MyLog.d(TAG, "getUserPsd");
		return userInfo;
	}

	/**
	 * 保存用户信息
	 * 
	 * @param entity
	 */
	public synchronized void saveUserInfo(UserEntity entity) {
		if (entity == null) {
			return;
		}
		SharedPreferences errorFlag = mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		Gson gson = new Gson();
		String userInfo = gson.toJson(entity, new TypeToken<UserEntity>() {
		}.getType());
		Editor editor = errorFlag.edit();
		editor.putString(USER_INFO, userInfo);
		editor.commit();
		MyLog.d(TAG, "saveUserInfo");
	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public synchronized UserEntity getUserInfo() {
		SharedPreferences errorFlag = mContext.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
		String userInfo = errorFlag.getString(USER_INFO, null);
		if (userInfo == null) {
			return null;
		}
		Gson gson = new Gson();
		UserEntity entity = new UserEntity();
		entity = gson.fromJson(userInfo, new TypeToken<UserEntity>() {
		}.getType());
		MyLog.d(TAG, "getUserInfo");
		return entity;
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