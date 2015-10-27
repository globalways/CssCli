package com.globalways.cvsb.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.entity.SettleAuditEntity;
import com.globalways.cvsb.entity.SettleEntity;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpCode;
import com.globalways.cvsb.http.HttpUtils;
import com.globalways.cvsb.http.HttpClientDao.ErrorCode;
import com.globalways.cvsb.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.ui.order.SettleStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SettleManager {
	private static SettleManager mSettleManager;
	private SettleManager() {
	}
	public static SettleManager getInstance(){
		if(mSettleManager == null){
			mSettleManager = new SettleManager();
		}
		return mSettleManager;
	}
	
	public void loadSettles(SettleStatus s,int page, int size, final ManagerCallBack2<List<SettleEntity>, Integer> callBack){
		String url = HttpApi.SETTLE_LIST.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("page", page);
		params.put("size", size);
		params.put("orderby", "-created");
		if(s != null){
			params.put("status", s.getCode());
		}
		
		HttpUtils.getInstance().sendGetRequest(url, 1, params, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				int code = 0;
				try {
					jsonObject = new JSONObject(returnContent);
					code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					//工单总数
					int total = jsonObject.getInt(Config.TOTAL);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						List<SettleEntity> list = new ArrayList<SettleEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<SettleEntity>>() {
						}.getType());
						if (null != callBack) {
							callBack.onSuccess(list,total);
						}
					} else {
						if (null != callBack) {
							callBack.onFailure(code, jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callBack.onFailure(code, "");
				}
				
			}

			@Override
			public void onFailure(String url, long flag, ErrorCode errorCode) {
				super.onFailure(url, flag, errorCode);
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	public void loadSettleOrders(String sno, final ManagerCallBack2<List<OrderEntity>, Integer> callBack){
		String url = HttpApi.SETTLE_ORDER_LIST.replaceFirst(":sno", sno);
		HttpUtils.getInstance().sendGetRequest(url, 1, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				int code = 0;
				try {
					jsonObject = new JSONObject(returnContent);
					code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					//工单总数
					int total = jsonObject.getInt(Config.TOTAL);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						List<OrderEntity> list = new ArrayList<OrderEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<OrderEntity>>() {
						}.getType());
						if (null != callBack) {
							callBack.onSuccess(list,total);
						}
					} else {
						if (null != callBack) {
							callBack.onFailure(code, jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					callBack.onFailure(code, null);
				}
				
			}

			@Override
			public void onFailure(String url, long flag, ErrorCode errorCode) {
				super.onFailure(url, flag, errorCode);
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
		
	}
	
	/**
	 * 工单详情
	 * @param sno
	 * @param callBack
	 */
	public void getSettle(String sno, final ManagerCallBack<SettleEntity> callBack){
		String url = HttpApi.SETTLE_DETAIL.replaceFirst(":sno", sno);
		HttpUtils.getInstance().sendGetRequest(url, 1, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						SettleEntity entity;
						entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<SettleEntity>() {
						}.getType());
						if (null != callBack) {
							callBack.onSuccess(entity);
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	/**
	 * 关闭工单
	 * @param sno
	 * @param callBack
	 */
	public void closeSettle(String sno, final ManagerCallBack<String> callBack){
		String url = HttpApi.SETTLE_CLOSE.replaceFirst(":sno", sno);
		HttpUtils.getInstance().sendPostRequest(url, 0, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	/**
	 * 工单确认收款
	 * @param sno
	 * @param callBack
	 */
	public void receiveSettle(String sno, final ManagerCallBack<String> callBack){
		String url = HttpApi.SETTLE_RECEIVE.replaceFirst(":sno", sno);
		HttpUtils.getInstance().sendPostRequest(url, 0, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	/**
	 * 工单放款
	 * @param sno
	 * @param callBack
	 */
	public void paySettle(String sno, final ManagerCallBack<String> callBack){
		String url = HttpApi.SETTLE_PAY.replaceFirst(":sno", sno);
		HttpUtils.getInstance().sendPostRequest(url, 0, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	/**
	 * 新建工单
	 * @param sno
	 * @param callBack
	 */
	public void newSettle(String end_date, String comment, final ManagerCallBack<String> callBack){
		String url = HttpApi.SETTLE_NEW.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("end_date", end_date);
		params.put("comment", comment);
		HttpUtils.getInstance().sendPostRequest(url, 0, params, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	/**
	 * 电脑审计
	 * @param callBack
	 * @param sno 工单号
	 */
	public void auditAuto(String sno, final ManagerCallBack<SettleAuditEntity> callBack){
		String url = HttpApi.SETTLE_AUDIT_AUTO.replaceFirst(":sno", sno);
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
						SettleAuditEntity entity;
						entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<SettleAuditEntity>() {
						}.getType());
						
						if (null != callBack) {
							callBack.onSuccess(entity);
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	
}
