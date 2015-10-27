package com.globalways.cvsb.http.manager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.StockSummaryEntity;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpCode;
import com.globalways.cvsb.http.HttpUtils;
import com.globalways.cvsb.http.HttpClientDao.ErrorCode;
import com.globalways.cvsb.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 库存管理
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月2日 下午9:30:46
 */
public class StockManager {
	private static StockManager mStockManager;
	private StockManager() {
	}
	public static StockManager getInstance(){
		if(null == mStockManager){
			mStockManager = new StockManager();
		}
		return mStockManager;
	}
	
	/**
	 * 所有库存总计
	 * @param callBack
	 */
	public void getStockSummary(final ManagerCallBack<StockSummaryEntity> callBack){
		String url = HttpApi.STOCK_SUMMARY.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		HttpUtils.getInstance().sendGetRequest(url, 0, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int resultCode = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if(resultCode == HttpCode.SUCCESS)
					{
						Gson gson = new Gson();
						StockSummaryEntity entity;
						entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<StockSummaryEntity>(){}.getType());
						if(null != callBack){
							callBack.onSuccess(entity);
						}
					}else {
						if (null != callBack) {
							callBack.onFailure(resultCode, jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
						}
					}
				} catch (Exception e) {
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
	 * 加载当前列表总计和商品列表
	 * @param page
	 * @param size
	 * @param callBack
	 */
	public void loadStockProducts(int page, int size, final ManagerCallBack<StockSummaryEntity> callBack){
		String url = HttpApi.STOCK_PRODUCT_LIST.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("page", page);
		params.put("size", size);
		HttpUtils.getInstance().sendGetRequest(url, 0, params, new HttpClientUtilCallBack<String>() {

			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int resultCode = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if(resultCode == HttpCode.SUCCESS)
					{
						Gson gson = new Gson();
						StockSummaryEntity entity;
						entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<StockSummaryEntity>(){}.getType());
						if(null != callBack){
							callBack.onSuccess(entity);
						}
					}else {
						if (null != callBack) {
							callBack.onFailure(resultCode, jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
						}
					}
				} catch (Exception e) {
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
	 * 搜索商品
	 * @param page
	 * @param size
	 * @param callBack
	 */
	public void searchProducts(String key,int page,int size,final ManagerCallBack<StockSummaryEntity> callBack){
		String url = HttpApi.STOCK_SEARCH.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("page", page);
		params.put("size", size);
		params.put("keyword", key);
		HttpUtils.getInstance().sendGetRequest(url, 0, params, new HttpClientUtilCallBack<String>() {

			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int resultCode = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if(resultCode == HttpCode.SUCCESS)
					{
						Gson gson = new Gson();
						StockSummaryEntity entity;
						entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<StockSummaryEntity>(){}.getType());
						if(null != callBack){
							callBack.onSuccess(entity);
						}
					}else {
						if (null != callBack) {
							callBack.onFailure(resultCode, jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG));
						}
					}
				} catch (Exception e) {
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
