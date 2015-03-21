package com.globalways.csscli.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.MyLog;
import com.globalways.csscli.ui.gallery.GalleryPicEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ProductManager {

	private static ProductManager productManager;

	private ProductManager() {
	}

	public static ProductManager getInstance() {
		if (productManager == null) {
			productManager = new ProductManager();
		}
		return productManager;
	}

	/**
	 * this function make sure HttpUtils is singleton.
	 *
	 * @return
	 */
	private Object readResolve() {
		return productManager;
	}

	public enum GetProductType {
		/** 条形码 */
		BAR_CODE(HttpApi.PRODUCT_GET_DETAIL_BAR_CODE, ":bar"),
		/** 二维码 */
		QR_CODE(HttpApi.PRODUCT_GET_DETAIL_QR_CODE, ":qr");

		private GetProductType(String url, String code) {
			this.url = url;
			this.code = code;
		}

		private String url;
		private String sid = ":sid";
		private String code;

		public String getUrl() {
			return url;
		}

		public String getSid() {
			return sid;
		}

		public String getCode() {
			return code;
		}
	}

	/**
	 * 根据条形码或者二维码获取商品详细信息
	 * 
	 * @param storeid
	 *            店铺id
	 * @param type
	 *            获取方式：条形码、二维码
	 * @param productCode
	 *            商品的条形码或者二维码
	 * @param callBack
	 */
	public void getProductDetail(long storeid, GetProductType type, String productCode,
			final ManagerCallBack<ProductEntity> callBack) {
		HttpUtils.getInstance().sendGetRequest(
				(type.getUrl().replaceFirst(type.getSid(), String.valueOf(storeid))).replaceFirst(type.getCode(),
						productCode), 1, null, new HttpClientUtilCallBack<String>() {
					@Override
					public void onSuccess(String url, long flag, String returnContent) {
						super.onSuccess(url, flag, returnContent);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(returnContent);
							int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
							if (code == HttpCode.SUCCESS) {
								Gson gson = new Gson();
								ProductEntity entity = new ProductEntity();
								entity = gson.fromJson(jsonObject.getString(Config.BODY),
										new TypeToken<ProductEntity>() {
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

	private static final int PAGE_SIZE = 20;
	private int page = 1;

	/**
	 * 分页获取指定storeId的商品列表
	 * 
	 * @param storeid
	 * @param isRefresh
	 * @param callBack
	 */
	public void getProductList(long storeid, final boolean isRefresh,
			final ManagerCallBack<List<ProductEntity>> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		// params.put("fields", fields);
		// params.put("search", search);
		params.put("size", PAGE_SIZE);
		params.put("page", isRefresh ? 1 : page);
		HttpUtils.getInstance().sendGetRequest(HttpApi.PRODUCT_GET_LIST.replaceFirst(":sid", String.valueOf(storeid)),
				1, params, new HttpClientUtilCallBack<String>() {
					@Override
					public void onSuccess(String url, long flag, String returnContent) {
						super.onSuccess(url, flag, returnContent);
						JSONObject jsonObject;
						try {
							jsonObject = new JSONObject(returnContent);
							int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
							if (code == HttpCode.SUCCESS) {
								Gson gson = new Gson();
								List<ProductEntity> list = new ArrayList<ProductEntity>();
								list = gson.fromJson(jsonObject.getString(Config.BODY),
										new TypeToken<List<ProductEntity>>() {
										}.getType());
								if (null != callBack) {
									callBack.onSuccess(list);
								}
								if (isRefresh) {
									page = 1;
								} else {
									page++;
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

	private Map<String, Object> addProductParams;

	/**
	 * 添加商品
	 * 
	 * @param storeid
	 * @param product_name
	 *            商品名称，必填
	 * @param product_brand
	 *            商品品牌，必填
	 * @param product_bar
	 *            条形码，选填
	 * @param product_desc
	 *            商品描述，可选
	 * @param product_avatar
	 *            商品图片key列表，用英文逗号分割，选填
	 * @param product_price
	 *            商品价格，单位：分，必填
	 * @param product_unit
	 *            商品单位，必填
	 * @param product_apr
	 *            商品的会员折扣0-100，选填
	 * @param stock_cnt
	 *            库存剩余量，选填
	 * @param is_recommend
	 *            是否是店长推荐的商品，选填
	 * @param stock_limit
	 *            库存告急线，库存低于该值则该进货了，选填
	 * @param product_tag
	 *            商品标签，选填
	 * @param purchase_channel
	 *            送货渠道ID，选填
	 * @param callBack
	 */
	public void addProduct(ArrayList<GalleryPicEntity> selectedImageList, final long storeid, String product_name,
			String product_brand, String product_bar, String product_desc, int product_price, String product_unit,
			int product_apr, int stock_cnt, boolean is_recommend, boolean status, int stock_limit, String product_tag,
			long purchase_channel, final ManagerCallBack<String> callBack) {
		addProductParams = new HashMap<String, Object>();
		if (product_name == null || product_name.isEmpty()) {
			return;
		}
		addProductParams.put("product_name", product_name);
		if (product_brand != null && !product_brand.isEmpty()) {
			addProductParams.put("product_brand", product_brand);
		}
		if (product_bar != null && !product_bar.isEmpty()) {
			addProductParams.put("product_bar", product_bar);
		}
		if (product_desc != null && !product_desc.isEmpty()) {
			addProductParams.put("product_desc", product_desc);
		}
		addProductParams.put("product_price", product_price);
		if (product_unit == null || product_unit.isEmpty()) {
			return;
		}
		addProductParams.put("product_unit", product_unit);
		if (product_apr != 100) {
			addProductParams.put("product_apr", product_apr);
		}
		if (stock_cnt != 0) {
			addProductParams.put("stock_cnt", stock_cnt);
		}
		// 1，推荐，2不推荐
		addProductParams.put("is_recommend", is_recommend ? 1 : 2);
		// 1，推荐，2不推荐
		addProductParams.put("status", status ? 1 : 2);
		if (stock_limit != 0) {
			addProductParams.put("stock_limit", stock_limit);
		}
		if (product_tag != null && !product_tag.isEmpty()) {
			addProductParams.put("product_tag", product_tag);
		}
		if (purchase_channel != 0) {
			addProductParams.put("purchase_channel", purchase_channel);
		}
		if (selectedImageList != null && selectedImageList.size() > 1) {
			MyLog.e("imagelist", selectedImageList.toString());
			String[] key = null, path = new String[selectedImageList.size() - 1];
			for (int i = 0; i < selectedImageList.size() - 1; i++) {
				path[i] = selectedImageList.get(i).imagePath;
			}
			ImageUpLoadManager.getInstance().upLoadImage(key, path, new ManagerCallBack<String>() {
				@Override
				public void onSuccess(String returnContent) {
					addProductParams.put("product_avatar", returnContent);
					toAddProduct(addProductParams, storeid, callBack);
				};

				@Override
				public void onProgress(int progress) {
					super.onProgress(progress);
				}

				@Override
				public void onFailure(int code, String msg) {
					if (null != callBack) {
						callBack.onFailure(code, msg);
					}
				};
			});
		} else {
			toAddProduct(addProductParams, storeid, callBack);
		}
	}

	private void toAddProduct(Map<String, Object> params, long storeid, final ManagerCallBack<String> callBack) {
		HttpUtils.getInstance().sendPostRequest(
				HttpApi.PRODUCT_ADD_PRODUCT.replaceFirst(":sid", String.valueOf(storeid)), 1, params,
				new HttpClientUtilCallBack<String>() {
					@Override
					public void onSuccess(String url, long flag, String returnContent) {
						super.onSuccess(url, flag, returnContent);
						addProductParams = null;
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

	public void update(long storeid, String product_bar, String product_name, String product_brand,
			String product_desc, String product_avatar, int product_price, String product_unit, int product_apr,
			int stock_cnt, boolean is_recommend, boolean status, int stock_limit, String product_tag,
			long purchase_channel, final ManagerCallBack<String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		if (product_name == null || product_name.isEmpty()) {
			return;
		}
		params.put("product_name", product_name);
		if (product_brand != null && !product_brand.isEmpty()) {
			params.put("product_brand", product_brand);
		}
		if (product_bar != null && !product_bar.isEmpty()) {
			params.put("product_bar", product_bar);
		}
		if (product_desc != null && !product_desc.isEmpty()) {
			params.put("product_desc", product_desc);
		}
		if (product_avatar != null && !product_avatar.isEmpty()) {
			params.put("product_avatar", product_avatar);
		}
		params.put("product_price", product_price);
		if (product_unit == null || product_unit.isEmpty()) {
			return;
		}
		params.put("product_unit", product_unit);
		if (product_apr != 100) {
			params.put("product_apr", product_apr);
		}
		if (stock_cnt != 0) {
			params.put("stock_cnt", stock_cnt);
		}
		// 1，推荐，2不推荐
		params.put("is_recommend", is_recommend ? 1 : 2);
		// 1，推荐，2不推荐
		params.put("status", status ? 1 : 2);
		if (stock_limit != 0) {
			params.put("stock_limit", stock_limit);
		}
		if (product_tag != null && !product_tag.isEmpty()) {
			params.put("product_tag", product_tag);
		}
		if (purchase_channel != 0) {
			params.put("purchase_channel", purchase_channel);
		}
		HttpUtils.getInstance().sendPostRequest(
				HttpApi.PRODUCT_UPDATE_INFO.replaceFirst(":sid", String.valueOf(storeid)).replaceFirst(":bar",
						product_bar), 1, params, new HttpClientUtilCallBack<String>() {
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

	public void deleteProduct(final ManagerCallBack<String> callBack) {
	}

}
