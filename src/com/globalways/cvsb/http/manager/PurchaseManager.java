package com.globalways.cvsb.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.PurchaseEntity;
import com.globalways.cvsb.entity.PurchaseGoodsEntity;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpCode;
import com.globalways.cvsb.http.HttpUtils;
import com.globalways.cvsb.http.HttpClientDao.ErrorCode;
import com.globalways.cvsb.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.tools.MyLog;
import com.globalways.cvsb.ui.gallery.GalleryPicEntity;
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
	public void getPurchaseList(final ManagerCallBack2<List<PurchaseEntity>,Integer> callBack )
	{
		String url = HttpApi.PURCHASES_ALL.replaceAll(":sid", String.valueOf( MyApplication.getStoreid()));
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
						int total = jsonObject.getInt(Config.TOTAL);
						List<PurchaseEntity> list = new ArrayList<PurchaseEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<PurchaseEntity>>() {
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
	 * 获取采购清单list
	 * @param store_id
	 * @param callBack
	 */
	public void getPurchaseList(int page,int pagesize, final ManagerCallBack2<List<PurchaseEntity>,Integer> callBack )
	{
		String url = HttpApi.PURCHASES_GET_LIST.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		StringBuilder sb = new StringBuilder(url);
		sb.append("?size=").append(pagesize)
		.append("&page=").append(page).append("&orderby=-created");
		HttpUtils.getInstance().sendGetRequest(sb.toString(), 0, null, new HttpClientUtilCallBack<String>() {

			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
						int total = jsonObject.getInt(Config.TOTAL);
						Gson gson = new Gson();
						List<PurchaseEntity> list = new ArrayList<PurchaseEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<PurchaseEntity>>() {
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
			sbQrs.append(e.getProduct_qr()).append(",");
			sbCounts.append(e.getPurchase_count()).append(",");
			sbPrices.append(e.getPurchase_price()).append(",");
			if(e.getSupplier() == null){
				sbSuppliers.append("0,");
			}else{
				sbSuppliers.append(e.getSupplier().getId()).append(",");
			}
		}
		
		
		
		params.put("product_qrs", sbQrs.toString().substring(0, sbQrs.toString().lastIndexOf(',')));
		params.put("purchase_counts", sbCounts.toString().substring(0, sbCounts.toString().lastIndexOf(',')));
		params.put("purchase_prices", sbPrices.toString().substring(0, sbPrices.toString().lastIndexOf(',')));
		params.put("purchase_suppliers", sbSuppliers.toString().substring(0, sbSuppliers.toString().lastIndexOf(',')));
		
		
		params.put("comment", "可选");
		
		
		
		if (selectedImageList != null && selectedImageList.size() > 1) {
			MyLog.e("imagelist", selectedImageList.toString());
			String[] path = new String[selectedImageList.size() - 1];
			//第一张图片是"+"号
			for (int i = 1; i < selectedImageList.size(); i++) {
				path[i-1] = selectedImageList.get(i).imagePath;
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
				public void onProgress(int progress) {
					callBack.onProgress(progress);
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
