package com.globalways.cvsb.http.manager;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.StoreEntity;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpCode;
import com.globalways.cvsb.http.HttpUtils;
import com.globalways.cvsb.http.HttpClientDao.ErrorCode;
import com.globalways.cvsb.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class StoreManager {
	private static StoreManager storeManager;

	private StoreManager() {
	}

	public static StoreManager getInstance() {
		if (storeManager == null) {
			storeManager = new StoreManager();
		}
		return storeManager;
	}

	/**
	 * this function make sure HttpUtils is singleton.
	 *
	 * @return
	 */
	private Object readResolve() {
		return storeManager;
	}

	/**
	 * 根据storeid获取商铺信息
	 * 
	 * @param storeid
	 *            店铺id
	 * @param callBack
	 */
	public void getStoreInfo(long storeid, final ManagerCallBack<StoreEntity> callBack) {
		HttpUtils.getInstance().sendGetRequest((HttpApi.STORE_GET_INFO.replaceFirst(":sid", String.valueOf(storeid))),
				1, null, new HttpClientUtilCallBack<String>() {
					@Override
					public void onSuccess(String url, long flag, String returnContent) {
						super.onSuccess(url, flag, returnContent);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(returnContent);
							int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
							if (code == HttpCode.SUCCESS) {
								Gson gson = new Gson();
								StoreEntity entity = new StoreEntity();
								entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<StoreEntity>() {
								}.getType());
								if (null != callBack) {
									callBack.onSuccess(entity);
								}
							} else {
								if (null != callBack) {
									callBack.onFailure(code,
											jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
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
