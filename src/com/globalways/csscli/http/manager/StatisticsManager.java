package com.globalways.csscli.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.StatEntity;
import com.globalways.csscli.entity.StoreEntity;
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

	private boolean isLoading = false;

	public void getStat(boolean isSell, String startDate, String endDate, List<Long> ids,
			final ManagerCallBack2<StatEntity, String> callBack) {
		
		StringBuilder sb = new StringBuilder();
		for(long id : ids)
		{
			sb.append(id).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		
		if (isLoading) {
			return;
		}
		isLoading = true;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("store_ids", sb.toString());
		params.put("start_date", startDate);
		params.put("end_date", endDate);
		HttpUtils.getInstance().sendGetRequest(isSell ? HttpApi.STATISTICS_SALL : HttpApi.STATISTICS_BUY, 1, params,
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
								StatEntity entity = new StatEntity();
								entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<StatEntity>() {
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
						isLoading = false;
					}

					@Override
					public void onFailure(String url, long flag, ErrorCode errorCode) {
						super.onFailure(url, flag, errorCode);
						if (callBack != null) {
							callBack.onFailure(errorCode.code(), errorCode.msg());
						}
						isLoading = false;
					}
				});
	}
	
	/**
	 * get children of a chain store
	 * @author wyp
	 */
	public void getSubStores(String storeID, final ManagerCallBack<List<StoreEntity>> callBack)
	{
		String url = HttpApi.STORE_CHILDERN_ALL.replaceAll(":sid", storeID);
		HttpUtils.getInstance().sendGetRequest(url, 0, null, new HttpClientUtilCallBack<String>() {

			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						List<StoreEntity> list = new ArrayList<StoreEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<StoreEntity>>() {
						}.getType());
						if (null != callBack) {
							callBack.onSuccess(list);
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
