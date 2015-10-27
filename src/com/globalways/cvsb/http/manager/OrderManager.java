package com.globalways.cvsb.http.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.globalways.cvsb.Config;
import com.globalways.cvsb.entity.OrderEntity;
import com.globalways.cvsb.entity.OrderProductsEntity;
import com.globalways.cvsb.entity.ProductEntity;
import com.globalways.cvsb.http.HttpApi;
import com.globalways.cvsb.http.HttpCode;
import com.globalways.cvsb.http.HttpUtils;
import com.globalways.cvsb.http.HttpClientDao.ErrorCode;
import com.globalways.cvsb.http.HttpClientDao.HttpClientUtilCallBack;
import com.globalways.cvsb.tools.MyApplication;
import com.globalways.cvsb.ui.order.OrderStatus;
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
	 * @param isCash 是否是现金收银; 现金收银为true; 在线付款为false
	 * 
	 * @param order_id
	 *            订单id
	 * @param callBack
	 */
	public void updateSignStatus(boolean isCash,String order_id, final ManagerCallBack<String> callBack) {
		String url = null;
		if(isCash){
			url = HttpApi.ORDER_CASH_DONE;
		}else{
			url = HttpApi.ORDER_ONLINE_DONE;
		}
		HttpUtils.getInstance().sendPostRequest(url.replaceFirst(":oid", order_id + ""), 1, null,
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
	
	/**
	 * 获取订单信息
	 * @param order_id
	 * @param callBack
	 */
	public void getOrder(String order_id, final ManagerCallBack<OrderEntity> callBack){
		HttpUtils.getInstance().sendGetRequest(HttpApi.ORDER_GET.replaceFirst(":oid", order_id + ""), 1, null,
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
								OrderEntity entity;
								entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<OrderEntity>() {
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
	
	/**
	 * 非异步获取订单信息
	 * @param order_id 订单号
	 * @return null if error
	 */
	public OrderEntity getOrderWithoutAsyc(String order_id) {
		OrderEntity entity = null;
        String result = null;
        URL url = null;
        HttpURLConnection connection = null;
        InputStreamReader in = null;
        try {
            url = new URL(HttpApi.ORDER_GET.replaceFirst(":oid", order_id));
            connection = (HttpURLConnection) url.openConnection();
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(line);
            }
            result = strBuffer.toString();
            
            //解析结果
            JSONObject jsonObject;
    		jsonObject = new JSONObject(result);
    		int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
    		if (code == HttpCode.SUCCESS) {
    			
    			Gson gson = new Gson();
    			entity = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<OrderEntity>() {
    			}.getType());
    			
    		} else {
    			jsonObject.getJSONObject(Config.STATUS).getString(Config.MSG);
    		}
            
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
 
        }
        return entity;
    }
	
	/**
	 * 订单列表
	 * @param page
	 * @param size
	 * @param callBack
	 * @param str_status 状态ids  example: "2,3,4"
	 */
	public void loadOrders(int page, int size,long start, long end,List<OrderStatus> statusArray,  final ManagerCallBack2<List<OrderEntity>, Integer> callBack){
		
		StringBuilder status = new StringBuilder();
		if(statusArray != null && statusArray.size() != 0){
			for(int i=0;i<statusArray.size();i++){
				status.append(statusArray.get(i).code).append(",");
			}
		}
		String url = HttpApi.ORDER_LIST.replaceFirst(":sid", String.valueOf(MyApplication.getStoreid()));
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("orderby", "-created");
		params.put("page", page);
		params.put("size", size);
		params.put("start_time", start);
		params.put("end_time", end);
		params.put("status", status.length()==0 ? "" : status.toString().substring(0, status.length()-1));
		
		HttpUtils.getInstance().sendGetRequest(url, 1, params, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					//订单总数
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
	 * 加载订单所含商品列表
	 * @param oid
	 * @param callBack
	 */
	public void loadOrderProuducts(String oid, final ManagerCallBack2<List<OrderProductsEntity>, Integer> callBack){
		String url = HttpApi.ORDER_PRODUCTS_LIST.replaceFirst(":oid", oid);
		
		HttpUtils.getInstance().sendGetRequest(url, 1, null, new HttpClientUtilCallBack<String>() {
			@Override
			public void onSuccess(String url, long flag, String returnContent) {
				super.onSuccess(url, flag, returnContent);
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(returnContent);
					int code = jsonObject.getJSONObject(Config.STATUS).getInt(Config.CODE);
					//订单商品总数
					int total = jsonObject.getInt(Config.TOTAL);
					if (code == HttpCode.SUCCESS) {
						Gson gson = new Gson();
						List<OrderProductsEntity> list = new ArrayList<OrderProductsEntity>();
						list = gson.fromJson(jsonObject.getString(Config.BODY), new TypeToken<List<OrderProductsEntity>>() {
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
	 * 获取支付宝支付二维码
	 * @param oid
	 * @param callBack
	 */
	public void getAlipayQRCode(String oid, final ManagerCallBack<String> callBack){
		String url = HttpApi.ORDER_ALIPAY_QRCODE.replaceFirst(":oid", oid);
		HttpUtils.getInstance().sendPostRequest(url, 1, null,
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
									callBack.onSuccess(jsonObject.getString(Config.BODY));
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
