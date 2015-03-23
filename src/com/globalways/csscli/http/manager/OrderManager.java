package com.globalways.csscli.http.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.csscli.Config;
import com.globalways.csscli.entity.OrderEntity;
import com.globalways.csscli.entity.ProductEntity;
import com.globalways.csscli.http.HttpApi;
import com.globalways.csscli.http.HttpCode;
import com.globalways.csscli.http.HttpUtils;
import com.globalways.csscli.http.HttpClientDao.ErrorCode;
import com.globalways.csscli.http.HttpClientDao.HttpClientUtilCallBack;
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

	// order_type "订单类型：
	// store_id 商铺ID 必须
	// store_name 商铺名称 必须
	// buyer 买家ID 必须
	// card 会员卡号 可选
	// address_id 订单地址ID，当为送货上门时有值 可选
	// payment_type "付款类型：
	// 1：现金支付
	// 2：在线支付" 必须
	// delivery_type "送货类型：
	// 1：自提
	// 2：送货上门
	// 3：揽货" 必须
	// discount_amount 折扣总额，单位：分 可选
	// delivery_price 送货价格，单位：分，当送货类型为送货上门时有效 可选
	// canvass_price 揽货价格，单位：分，当送货类型为揽货时有效 可选
	// comment 订单备注 可选
	// product_ids 订单商品id列表，用英文逗号分割，与qrs二选一，或者全部 可选
	// product_qrs 订单商品二维码列表，同上 可选
	// product_names 订单商品名称列表 必须
	// product_counts 订单商品数量列表 必须
	// product_prices 订单商品价格列表 必须
	// product_units 订单商品单位列表 必须

	public void toSignOrder(final ManagerCallBack<OrderEntity> callBack) {
		Map<String, Object> params = new HashMap<String, Object>();
		 params.put("store_id", MyApplication.getStoreid());
		// params.put("search", search);
		HttpUtils.getInstance().sendGetRequest(HttpApi.ORDER_SIGN, 1, params, new HttpClientUtilCallBack<String>() {
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

}
