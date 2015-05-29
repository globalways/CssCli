package com.globalways.csscli.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.PurchaseEntity;
import com.globalways.csscli.entity.PurchaseGoodsEntity;
import com.globalways.csscli.entity.StoreEntity;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.MyLog;
import com.globalways.csscli.tools.Tool;
import com.globalways.csscli.ui.gallery.GalleryPicEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PurchaseManager {
	public static PurchaseManager purchaseManager;
	private PurchaseManager()
	{
		
	}
	public static PurchaseManager getInstance()
	{
		if(purchaseManager == null)
			purchaseManager = new PurchaseManager();
		return purchaseManager;
	}
	/**
	 * this function make sure HttpUtils is singleton.
	 *
	 * @return
	 */
	private Object readResolve() {
		return purchaseManager;
	}
	
	/**
	 * 获取采购清单list
	 * @param store_id
	 * @param callBack
	 */
	public void getPurchaseList(long store_id, final ManagerCallBack<List<PurchaseEntity>> callBack )
	{
		String url = HttpApi.PURCHASES_ALL.replaceAll(":sid", String.valueOf(store_id));
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
						List<PurchaseEntity> list = new ArrayList<PurchaseEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<PurchaseEntity>>() {
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
	
	public void savePurchase(String batch_id, String out_id, ArrayList<GalleryPicEntity> selectedImageList, List<PurchaseGoodsEntity> goods, final ManagerCallBack<String> callBack){
		
		final HashMap<String, Object> params = new HashMap<String, Object>();
		
		params.put("store_id", String.valueOf(MyApplication.getStoreid()));
		params.put("batch_id", batch_id);
		params.put("out_id", out_id);
		
		StringBuilder sbQrs = new StringBuilder();
		StringBuilder sbCounts = new StringBuilder();
		StringBuilder sbPrices = new StringBuilder();
		StringBuilder sbSuppliers = new StringBuilder();
		
		for(PurchaseGoodsEntity e : goods)
		{
			sbQrs.append(e.getQr()).append(",");
			sbCounts.append(e.getAmount()).append(",");
			sbPrices.append((long)Float.parseFloat(e.getTotal())*100).append(",");
			sbSuppliers.append("0").append(",");
		}
		
		
		
		params.put("product_qrs", sbQrs.toString().substring(0, sbQrs.toString().lastIndexOf(',')));
		params.put("purchase_counts", sbCounts.toString().substring(0, sbCounts.toString().lastIndexOf(',')));
		params.put("purchase_prices", sbPrices.toString().substring(0, sbPrices.toString().lastIndexOf(',')));
		params.put("purchase_suppliers", sbSuppliers.toString().substring(0, sbSuppliers.toString().lastIndexOf(',')));
		
		
		params.put("comment", "可选");
		
		
		
		if (selectedImageList != null && selectedImageList.size() > 1) {
			MyLog.e("imagelist", selectedImageList.toString());
			String[] path = new String[selectedImageList.size() - 1];
			for (int i = 0; i < selectedImageList.size() - 1; i++) {
				path[i] = selectedImageList.get(i).imagePath;
			}
			new ImageUpLoadManager().upLoadImage(path, new ManagerCallBack<List<String>>() {
				@Override
				public void onSuccess(List<String> returnContent) {
					super.onSuccess(returnContent);
					StringBuilder qiniuRequest = new StringBuilder();
					for (int i = 0; i < returnContent.size(); i++) {
						qiniuRequest.append(returnContent.get(i));
						qiniuRequest.append(",");
					}
					qiniuRequest.delete(qiniuRequest.length() - 1, qiniuRequest.length());

					params.put("purchase_avatar", qiniuRequest.toString());
					
					// after upload imgs successfully 
					AddPurchase(params, callBack);
				}

				@Override
				public void onFailure(int code, String msg) {
					if (null != callBack) {
						callBack.onFailure(code, msg);
					}
				};
			});
		}else{
			// after upload imgs successfully 
			AddPurchase(params, callBack);
		}
		
		
	}
	
	protected void AddPurchase(HashMap<String, Object> params,
			final ManagerCallBack<String> callBack) {
		
			String url = HttpApi.PURCHASES_NEW.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
			HttpUtils.getInstance().sendPostRequest(url, 1, params, new HttpClientUtilCallBack<String>() {
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
	
	/**
	 * 获取单个采购单货品列表 
	 * @param batch_id
	 * @param callBack
	 */
	public void getPurchaseProducts(String batch_id, final ManagerCallBack<List<PurchaseGoodsEntity>> callBack)
	{
		String url = HttpApi.PURCHASES_PRODUCT_LIST.replaceAll(":sid", String.valueOf(String.valueOf(MyApplication.getStoreid())));
		url = url.replaceFirst(":batchid", batch_id);
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
						List<PurchaseGoodsEntity> list = new ArrayList<PurchaseGoodsEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<PurchaseGoodsEntity>>() {
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
	
}
