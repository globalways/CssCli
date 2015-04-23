package com.globalways.csscli.http.manager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.StatBuyEntity;
import com.globalways.csscli.entity.StatSellEntity;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class StatisticsManager {

	private static StatisticsManager statisticsManager;

	private StatisticsManager() {
	}

	public static StatisticsManager getInstance() {
		if (statisticsManager == null) {
			statisticsManager = new StatisticsManager();
		}
		return statisticsManager;
	}

	/**
	 * this function make sure HttpUtils is singleton.
	 *
	 * @return
	 */
	private Object readResolve() {
		return statisticsManager;
	}

	public void getSellStat(final ManagerCallBack2<StatSellEntity, String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("store_ids", MyApplication.getStoreid());
		params.put("start_date", "2015-04-02");
		params.put("end_date", "2015-04-23");
		HttpUtils.getInstance().sendGetRequest(HttpApi.STATISTICS_SALL, 1, params,
				new HttpClientUtilCallBack<String>() {
					@Override
					public void onSuccess(String url, long flag, String returnContent) {
						super.onSuccess(url, flag, returnContent);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(returnContent);
							int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
							if (code == HttpCode.SUCCESS) {
								Gson gson = new Gson();
								StatSellEntity entity = new StatSellEntity();
								entity = gson.fromJson(jsonObject.getString(Config.BODY),
										new TypeToken<StatSellEntity>() {
										}.getType());
								if (null != callBack) {
									callBack.onSuccess(entity,
											jsonObject.getJSONObject(Config.BODY).getString("stat_items"));
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
						if (callBack != null) {
							callBack.onFailure(errorCode.code(), errorCode.msg());
						}
					}
				});
	}

	public void getBuyStat(final ManagerCallBack2<StatBuyEntity, String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("store_ids", MyApplication.getStoreid());
		params.put("start_date", "2015-04-02");
		params.put("end_date", "2015-04-23");
		HttpUtils.getInstance().sendGetRequest(HttpApi.STATISTICS_SALL, 1, params,
				new HttpClientUtilCallBack<String>() {
					@Override
					public void onSuccess(String url, long flag, String returnContent) {
						super.onSuccess(url, flag, returnContent);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(returnContent);
							int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
							if (code == HttpCode.SUCCESS) {
								Gson gson = new Gson();
								StatBuyEntity entity = new StatBuyEntity();
								entity = gson.fromJson(jsonObject.getString(Config.BODY),
										new TypeToken<StatBuyEntity>() {
										}.getType());
								if (null != callBack) {
									callBack.onSuccess(entity,
											jsonObject.getJSONObject(Config.BODY).getString("stat_items"));
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
						if (callBack != null) {
							callBack.onFailure(errorCode.code(), errorCode.msg());
						}
					}
				});
	}

}
