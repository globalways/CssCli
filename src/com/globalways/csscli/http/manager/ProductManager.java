package com.globalways.csscli.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.ProductCategoryEntity;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.MyApplication;
import com.globalways.csscli.tools.MyLog;
import com.globalways.csscli.tools.QRCodeTools;
import com.globalways.csscli.tools.QRCodeTools.CodeType;
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

	/**
	 * 根据条形码或者二维码获取商品详细信息
	 * 
	 * @param storeid
	 *            店铺id
	 * @param productCode
	 *            商品的条形码或者二维码
	 * @param callBack
	 */
	public void getProductDetail(long storeid, String productCode, final ManagerCallBack<ProductEntity> callBack) {
		CodeType codeType = new QRCodeTools().getCodeType(productCode);
		HttpUtils.getInstance().sendGetRequest(
				(codeType.getUrl().replaceFirst(codeType.getSid(), String.valueOf(storeid))).replaceFirst(
						codeType.getCode(), codeType.getContext()), 1, null, new HttpClientUtilCallBack<String>() {
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

	/**
	 * 分页获取指定storeId的商品列表
	 * 
	 * @param storeid
	 * @param isRefresh
	 * @param callBack
	 */
	public void getProductList(long storeid, int page, final ManagerCallBack<List<ProductEntity>> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		// params.put("fields", fields);
		// params.put("search", search);
		params.put("size", PAGE_SIZE);
		params.put("page", page);
		HttpUtils.getInstance().sendGetRequest(HttpApi.PRODUCT_GET_LIST.replaceFirst(":sid", String.valueOf(storeid))+"?orderby=-created",
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
	 * 添加商品或修改商品信息
	 * 
	 * @param isAdd
	 *            新增商品时，为true
	 * @param selectedImageList
	 *            修改商品照片时，选择的本地照片列表
	 * @param productPic
	 *            商品原有照片列表
	 * @param product_name
	 *            商品名称，必填
	 * @param product_brand
	 *            商品品牌，必填
	 * @param product_bar
	 *            条形码，选填
	 * @param product_desc
	 *            商品描述，可选
	 * @param product_retail_price
	 *            商品价格，单位：分，必填
	 * @param product_retail_apr
	 * 			      商品折扣
	 * @param product_original_price
	 *            商品原价
	 * @param product_unit
	 *            商品单位，必填
	 * @param stock_cnt
	 *            库存剩余量，选填
	 * @param is_recommend
	 *            是否是店长推荐的商品，选填
	 * @param status
	 *            是否启用商品
	 * @param product_tag
	 *            商品标签，选填
	 * @param callBack
	 */
	public void updateOrAdd(final boolean isAdd, ArrayList<GalleryPicEntity> selectedImageList,
			final ArrayList<String> productPic, String product_name, String product_brand, final String product_qr,
			String product_bar, String product_desc, long product_retail_price,String product_retail_apr,String product_category, long product_original_price,int product_type, String product_unit, double stock_cnt,
			boolean is_recommend, boolean status, String product_tag, final ManagerCallBack<String> callBack) {
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
		addProductParams.put("product_category", product_category);
		addProductParams.put("product_retail_price", product_retail_price);
		addProductParams.put("product_original_price", product_original_price);
		addProductParams.put("product_retail_apr", product_retail_apr);
		addProductParams.put("product_type", product_type);
		if (product_unit == null || product_unit.isEmpty()) {
			return;
		}
		addProductParams.put("product_unit", product_unit);
		if (stock_cnt != 0) {
			addProductParams.put("stock_cnt", stock_cnt);
		}
		// 1，推荐，2不推荐
		addProductParams.put("is_recommend", is_recommend ? 1 : 2);
		// 1，推荐，2不推荐
		addProductParams.put("status", status ? 1 : 2);
		if (product_tag != null && !product_tag.isEmpty()) {
			addProductParams.put("product_tag", product_tag);
		}
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
					if (productPic != null) {
						for (int i = 0; i < productPic.size(); i++) {
							qiniuRequest.append(productPic.get(i));
							qiniuRequest.append(",");
						}
					}
					qiniuRequest.delete(qiniuRequest.length() - 1, qiniuRequest.length());

					addProductParams.put("product_avatar", qiniuRequest.toString());
					AddOrUpdateProduct(isAdd ? null : product_qr, addProductParams, callBack);
				}

				@Override
				public void onFailure(int code, String msg) {
					if (null != callBack) {
						callBack.onFailure(code, msg);
					}
				};
			});
		} else {
			if (productPic != null) {
				StringBuilder qiniuRequest = new StringBuilder();
				for (int i = 0; i < productPic.size(); i++) {
					qiniuRequest.append(productPic.get(i));
					qiniuRequest.append(",");
				}
				qiniuRequest.delete(qiniuRequest.length() - 1, qiniuRequest.length());

				addProductParams.put("product_avatar", qiniuRequest.toString());
			} else {
				addProductParams.put("product_avatar", "");
			}
			AddOrUpdateProduct(isAdd ? null : product_qr, addProductParams, callBack);
		}
	}

	private void AddOrUpdateProduct(String qrCode, Map<String, Object> params, final ManagerCallBack<String> callBack) {
		if (qrCode != null && params != null) {
			StringBuilder fields = new StringBuilder();
			for (String key : params.keySet()) {
				fields.append(key).append(",");
			}
			fields.delete(fields.length() - 1, fields.length());
			params.put("fields", fields.toString());
		}
		String url;
		if (qrCode == null) {
			url = HttpApi.PRODUCT_ADD_PRODUCT.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		} else {
			url = HttpApi.PRODUCT_UPDATE_INFO.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()))
					.replaceFirst(":qr", String.valueOf(qrCode));
			MyLog.e(qrCode, qrCode);
		}
		HttpUtils.getInstance().sendPostRequest(url, 1, params, new HttpClientUtilCallBack<String>() {
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
				addProductParams = null;
				if (null != callBack) {
					callBack.onFailure(errorCode.code(), errorCode.msg());
				}
			}
		});
	}
	
	//------------------- category -------------------------------------//
	
	/**
	 * 新增商品分类 根分类id -1
	 * <br/>
	 * @param parent_id 为-1时表示新建一级分类
	 * @param name
	 * @param callBack
	 */
	public void newCategory(int parent_id, String name, final ManagerCallBack<String> callBack){
		
		Map<String,Object> params = new HashMap<String, Object>();
		params.put("parent_id", parent_id);
		params.put("store_id", MyApplication.getStoreid());
		params.put("name", name);
		String url = HttpApi.PRODUCT_CATEGORY_NEW.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		HttpUtils.getInstance().sendPostRequest(url, 0, params, new HttpClientUtilCallBack<String>() {

			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				callBack.onSuccess(returnContent);
			}

			@Override
			public void onFailure(String url, long flag, ErrorCode errorCode) {
				super.onFailure(url, flag, errorCode);
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
			
		});
	}
	
	/**
	 * 加载指定分类的子分类
	 * @param parent_id 为-1时表示查询一级分类
	 * @param callBack
	 */
	public void loadCategoryChildren(int parent_id, final ManagerCallBack<List<ProductCategoryEntity>> callBack){
		String url = HttpApi.PRODUCT_CATEGORY_CHILDREN.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()))
				.replaceFirst(":cid", String.valueOf(parent_id));
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
						List<ProductCategoryEntity> list = new ArrayList<ProductCategoryEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY),
								new TypeToken<List<ProductCategoryEntity>>() {
								}.getType());
						if (null != callBack) {
							callBack.onSuccess(list);
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
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
			
		});
	}
	
	/**
	 * 删除指定分类
	 * @param cid
	 * @param callBack
	 */
	public void deleteCategory(int cid, final ManagerCallBack<String> callBack){
		String url = HttpApi.PRODUCT_CATEGORY_DELETE.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()))
				.replaceFirst(":cid", String.valueOf(cid));
		HttpUtils.getInstance().sendDeleteRequest(url, 0, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				callBack.onSuccess(returnContent);
			}

			@Override
			public void onFailure(String url, long flag, ErrorCode errorCode) {
				super.onFailure(url, flag, errorCode);
				callBack.onFailure(errorCode.code(), errorCode.msg());
			}
		});
	}

}
