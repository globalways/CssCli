package com.globalways.csscli.http.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.OrderEntity;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.tools.MyApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 订单管理
 * 
 * @author James
 *
 */
public class OrderManager {

	private static OrderManager orderManager;

	private OrderManager() {
	}

	public static OrderManager getInstance() {
		if (orderManager == null) {
			orderManager = new OrderManager();
		}
		return orderManager;
	}

	/**
	 * this function make sure HttpUtils is singleton.
	 *
	 * @return
	 */
	private Object readResolve() {
		return orderManager;
	}

	public enum OrderType {
		/** 平台订单 */
		OWN(1),
		/** 天猫订单 */
		TMALL(2),
		/** 京东订单 */
		JD(3),
		/** 苏宁订单 */
		SUNING(4),
		/** 国美订单 */
		GOME(5),
		/** 亚马逊订单 */
		AMAZON(6);
		private OrderType(int type) {
			this.type = type;
		}

		private int type;

		public int getType() {
			return type;
		}
	}

	/**
	 * 下单
	 * 
	 * @param cashierList
	 *            购物车商品列表
	 * @param discount_amount
	 *            折扣总额
	 * @param comment
	 *            订单备注
	 * @param callBack
	 */
	public void toSignOrder(List<ProductEntity> cashierList, long discount_amount, String comment,
			final ManagerCallBack<OrderEntity> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("store_id", MyApplication.getStoreid());
		// 折扣总额
		params.put("discount_amount", discount_amount);
		// 订单备注
		params.put("comment", comment);
		StringBuilder sbQrs = new StringBuilder();
		StringBuilder sbCounts = new StringBuilder();
		for (int i = 0; i < cashierList.size(); i++) {
			sbQrs.append(cashierList.get(i).getProduct_qr()).append(",");
			sbCounts.append(cashierList.get(i).getShoppingNumber()).append(",");
		}
		sbQrs.deleteCharAt(sbQrs.length() - 1);
		sbCounts.deleteCharAt(sbCounts.length() - 1);
		// 订单商品二维码列表
		params.put("product_qrs", sbQrs.toString());
		// 订单商品数量列表
		params.put("product_counts", sbCounts.toString());
		HttpUtils.getInstance().sendPostRequest(HttpApi.ORDER_SIGN, 1, params, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						OrderEntity entity = new OrderEntity();
						entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<OrderEntity>() {
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
				if (null != callBack) {
					callBack.onFailure(errorCode.code(), errorCode.msg());
				}
			}
		});
	}

	/**
	 * 完成订单
	 * 
	 * @param order_id
	 *            订单id
	 * @param callBack
	 */
	public void updateSignStatus(String order_id, final ManagerCallBack<String> callBack) {
		HttpUtils.getInstance().sendPostRequest(HttpApi.ORDER_CASH_DONE.replaceFirst(":oid", order_id + ""), 1, null,
				new HttpClientUtilCallBack<String>() {
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

	/**
	 * 取消订单
	 * 
	 * @param order_id
	 *            订单id
	 * @param desc
	 *            取消原因
	 * @param callBack
	 */
	public void cancelSignStatus(String order_id, String desc, final ManagerCallBack<String> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("desc", desc);
		HttpUtils.getInstance().sendPostRequest(HttpApi.ORDER_CANCEL.replaceFirst(":oid", order_id + ""), 1, params,
				new HttpClientUtilCallBack<String>() {
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

}
