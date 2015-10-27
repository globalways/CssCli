package com.globalways.cvsb.http.manager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.UserEntity;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpCode;
import com.globalways.cvsb.http.HttpUtils;
import com.globalways.cvsb.http.HttpClientDao.ErrorCode;
import com.globalways.cvsb.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.SharedPreferencesHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LoginManager {

	/**
	 * 登录
	 * 
	 * @param username
	 *            帐号
	 * @param password
	 *            密码（加密过的)
	 * @param callBack
	 */
	public void toLogin(final Context context, final String username, final String password,
			final ManagerCallBack<String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("username", username);
		params.put("password", password);
		HttpUtils.getInstance().sendPostRequest(HttpApi.ACCOUNT_LOGIN, 1, params, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						UserEntity entity = new UserEntity();
						entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<UserEntity>() {
						}.getType());
						SharedPreferencesHelper.getInstance(context).saveUserInfo(entity);
						SharedPreferencesHelper.getInstance(context).saveUserAccount(username, password);
						MyApplication.setUserEntity(entity);
						try {
							MyApplication.setStoreid(Long.valueOf(username));
						} catch (NumberFormatException e) {
							e.printStackTrace();
						}
						if (null != callBack) {
							callBack.onSuccess(null);
						}
					} else {
						if (null != callBack) {
							callBack.onFailure(code, jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(String url, long flag, ErrorCode errorCode) {
				super.onFailure(url, flag, errorCode);
				if (null != callBack) {
					callBack.onFailure(errorCode.code(), errorCode.msg());
				}
			}
		});
	}
}
