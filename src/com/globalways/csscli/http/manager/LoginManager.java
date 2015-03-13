package com.globalways.csscli.http.manager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.MD5;

public class LoginManager {

	/**
	 * 登录
	 * 
	 * @param account
	 *            帐号
	 * @param pwd
	 *            密码（未加密)
	 * @param callBack
	 */
	public void toLogin(String account, String pwd, final ManagerCallBack<String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", 0);
		params.put("account", account);
		params.put("pwd", new MD5().getMD5(pwd));
		HttpUtils.getInstance().sendPostRequest(HttpApi.ACCOUNT_LOGIN, 1, params, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getInt("code");
					if (code == HttpCode.SUCCESS) {
						if (null != callBack) {
							callBack.onSuccess(returnContent);
						}
					} else {
						if (null != callBack) {
							callBack.onFailure(code, jsonObject.getString("msg"));
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
