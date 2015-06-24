package com.globalways.csscli.http.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.SupplierEntity;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.BeanUtils;
import com.globalways.csscli.tools.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SupplierManager {
	public static SupplierManager supplierManager;
	private SupplierManager() {
	}
	public static SupplierManager getInstance()
	{
		if(supplierManager == null)
		{
			supplierManager = new SupplierManager();
		}
		return supplierManager;
	}
	
	
	public void loadSuppliers(final ManagerCallBack<List<SupplierEntity>> callBack)
	{
		String url  = HttpApi.SUPPLIERS_ALL.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
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
						List<SupplierEntity> list = new ArrayList<SupplierEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<SupplierEntity>>() {
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	public void loadSuppliers(int page, int pagesize, final ManagerCallBack<List<SupplierEntity>> callBack)
	{
		String url  = HttpApi.SUPPLIERS_GET_LIST.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		StringBuilder sb = new StringBuilder(url)
		.append("?page=").append(page)
		.append("&size=").append(pagesize);
		HttpUtils.getInstance().sendGetRequest(sb.toString(), 0, null, new HttpClientUtilCallBack<String>() {

			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						List<SupplierEntity> list = new ArrayList<SupplierEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<SupplierEntity>>() {
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}
	
	/**
	 * get single supplier info by given supplier id
	 * @param supplierid
	 * @param callback
	 */
	public void getSupplier(int supplierid,final ManagerCallBack<SupplierEntity>  callBack)
	{
		String url  = HttpApi.SUPPLIER_SINGLE.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()))
		.replaceFirst(":supplierid", String.valueOf(supplierid));
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
						SupplierEntity supplier = new SupplierEntity();
						supplier = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<SupplierEntity>() {
						}.getType());
						if (null != callBack) {
							callBack.onSuccess(supplier);
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
	 * save or update supplier
	 * @param supplier
	 * @param callBack
	 */
	public void saveSupplier(SupplierEntity supplier, final ManagerCallBack<String> callBack)
	{
		String url;
		
		if(supplier.getId() != null)
		{
			//update
			
			
			
			url = HttpApi.SUPPLIERS_UPDATE.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()))
					.replaceFirst(":supplierid", supplier.getId().toString());
			Map<String,Object> params = BeanUtils.beanToMap(supplier);
			
			// fields
			StringBuilder apiurl = new StringBuilder( url );
			apiurl.append("?fields=");
			Set<String> fieldsSet = params.keySet();
			fieldsSet.remove("id");
			for(String field : fieldsSet)
			{
				apiurl.append(field);
				apiurl.append(",");
			}
			HttpUtils.getInstance().sendPutRequest(apiurl.toString(), 0, params, new HttpClientUtilCallBack<String>() {
				
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
					if (null != callBack) {
						callBack.onFailure(errorCode.code(), errorCode.msg());
					}
				}
			});
			
			
		}else{
			//new 
			url = HttpApi.SUPPLIERS_NEW.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
			supplier.setStore_id(MyApplication.getStoreid());
			Map<String,Object> params = BeanUtils.beanToMap(supplier);
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
					if (null != callBack) {
						callBack.onFailure(errorCode.code(), errorCode.msg());
					}
				}
				
			});
		}
		
		
	}
	
	
	/**
	 * delete supplier
	 * @param supplier
	 * @param callBack
	 */
	public void deleteSupplier(SupplierEntity supplier, final ManagerCallBack<String> callBack)
	{
		String url = HttpApi.SUPPLIERS_DELETE.replaceFirst(":sid",
				String.valueOf(supplier.getStore_id())).replaceFirst(":supplierid", String.valueOf(supplier.getId()));
		HttpUtils.getInstance().sendDeleteRequest(url, 0, null, new HttpClientUtilCallBack<String>() {
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
				if (null != callBack) {
					callBack.onFailure(errorCode.code(), errorCode.msg());
				}
			}
		});
	}
}
