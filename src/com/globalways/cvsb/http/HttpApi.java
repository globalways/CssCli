package com.globalways.cvsb.http;

public class HttpApi {

	private static final String HOME_URI = "http://123.57.132.7:8081";
	private static final String VERSION = "";

	public static final String VERSION_CHECK = HOME_URI + "/v1/version/varify";
	public static final String P_CHANNEL = "channel";
	public static final String P_APP_NAME = "app_name";
	public static final String P_VERSION = "version";

	public static final String ACCOUNT_LOGIN = HOME_URI + VERSION + "/v1/access/users/login";

	public static final String HONG_ID_GET_BY_PAGE = HOME_URI + VERSION + "/v1/admins/members";

	public static final String HONG_ID_GET_ALL = HOME_URI + VERSION + "/v1/admins/members/all";

	// 商铺
	public static final String STORE_GET_INFO = HOME_URI + "/v2/merchants/stores/:sid";
	
	public static final String PRODUCT_GET_LIST = HOME_URI + "/v2/merchants/stores/:sid/products";
	public static final String PRODUCT_ADD_PRODUCT = HOME_URI + "/v2/merchants/stores/:sid/products";
	public static final String PRODUCT_UPDATE_INFO = HOME_URI + "/v2/merchants/stores/:sid/products/qr/:qr";
	public static final String PRODUCT_CASHIER_SEARCH_ALL = HOME_URI + "/v2/merchants/stores/:sid/products/search/all";
	
	public static final String PRODUCT_CATEGORY_NEW = HOME_URI + "/v2/merchants/stores/:sid/categories";
	/**
	 * 获取分类的子分类（指定商铺）
	 */
	public static final String PRODUCT_CATEGORY_CHILDREN = HOME_URI + "/v2/merchants/stores/:sid/categories/:cid/children";
	/**
	 * 获取分类的子分类（不指定商铺，全平台）
	 */
	public static final String PRODUCT_CATEGORY_CHILDREN_JOINT = HOME_URI + "/v2/joint/categories/:cid/children";
	public static final String PRODUCT_CATEGORY_DELETE = HOME_URI + "/v2/merchants/stores/:sid/categories/:cid";
	public static final String PRODUCT_CATEGORY = HOME_URI + "/v2/merchants/stores/:sid/categories/:cid";
	public static final String CATEGORY_PRODUCT_ALL = HOME_URI + "/v2/merchants/stores/:sid/categories/:cid/products";
	public static final String CATEGORY_PRODUCT_ALL_LOOP = HOME_URI + "/v2/merchants/stores/:sid/categories/:cid/products/loop";
	
	public static final String PRODUCT_GET_DETAIL_BAR_CODE = HOME_URI + "/v2/merchants/stores/:sid/products/bar/:bar";
	public static final String PRODUCT_GET_DETAIL_QR_CODE = HOME_URI + "/v2/merchants/stores/:sid/products/qr/:qr";

	public static final String QINIU_GET_UPLOAD_TOKEN = HOME_URI + "/v2/public/qiniu/uptoken";
	//平台商品库
	public static final String PRODUCTLAB_BARCODE_SEARCH = HOME_URI + "/v2/merchants/productsLab/search";
	
	
	//订单
	public static final String ORDER_SIGN = HOME_URI + "/v2/orders";
	public static final String ORDER_CASH_DONE = HOME_URI + "/v2/orders/:oid/cashDone";
	public static final String ORDER_ONLINE_DONE = HOME_URI + "/v2/orders/:oid/onlinePayDone";
	//删去 public static final String ORDER_MAKING_DELIVERY = HOME_URI + "/v2/orders/:oid/makingDelivery";
	public static final String ORDER_CANCEL = HOME_URI + "/v2/orders/:oid/storeCancel";
	public static final String ORDER_GET = HOME_URI + "/v2/orders/:oid";
	public static final String ORDER_LIST = HOME_URI + "/v2/orders/stores/:sid";
	public static final String ORDER_PRODUCTS_LIST = HOME_URI + "/v2/orders/:oid/details";
	//支付宝支付二维码
	public static final String ORDER_ALIPAY_QRCODE = HOME_URI + "/v2/orders/:oid/alipay";

	public static final String SETTLE_NEW = HOME_URI + "/v2/merchants/stores/:sid/settle";
	public static final String SETTLE_LIST = HOME_URI + "/v2/merchants/stores/:sid/settle";
	public static final String SETTLE_ORDER_LIST = HOME_URI + "/v2/orders/settles/:sno/orders";
	public static final String SETTLE_AUDIT_AUTO = HOME_URI + "/v2/orders/settles/:sno/auditByComputer";
	public static final String SETTLE_DETAIL = HOME_URI + "/v2/orders/settles/:sno";
	public static final String SETTLE_PAY = HOME_URI + "/v2/orders/settles/:sno/payed";
	public static final String SETTLE_RECEIVE = HOME_URI + "/v2/orders/settles/:sno/received";
	public static final String SETTLE_CLOSE = HOME_URI + "/v2/orders/settles/:sno/close";
	
	
	// add by wyp
	public static final String STORE_CHILDERN_ALL = HOME_URI + "/v2/merchants/stores/:sid/children/all";
	// end
	
	/** 销售统计 */
	public static final String STATISTICS_SALL = HOME_URI + "/v2/orders/stat";
	/** 采购统计 */
	public static final String STATISTICS_BUY = HOME_URI + "/v2/merchants/stores/purchases/stat";
	public static final String STATISTICS_PRODUCT_RANK = HOME_URI + "/v2/merchants/stores/stat/productsRank";
	public static final String STATISTICS_PRODUCT_COMPARE = HOME_URI + "/v2/merchants/stores/stat/productsCompare";
	
	public static final String PURCHASES_ALL = HOME_URI + "/v2/merchants/stores/:sid/purchases/all";
	public static final String PURCHASES_GET_LIST = HOME_URI + "/v2/merchants/stores/:sid/purchases";
	public static final String PURCHASES_NEW = HOME_URI + "/v2/merchants/stores/:sid/purchases";
	public static final String PURCHASES_PRODUCT_LIST = HOME_URI + "/v2/merchants/stores/:sid/purchases/:batchid/products";
	
	public static final String SUPPLIERS_ALL = HOME_URI + "/v2/merchants/stores/:sid/suppliers/all";
	public static final String SUPPLIERS_GET_LIST = HOME_URI + "/v2/merchants/stores/:sid/suppliers";
	public static final String SUPPLIER_SINGLE = HOME_URI + "/v2/merchants/stores/:sid/suppliers/:supplierid";
	public static final String SUPPLIERS_NEW = HOME_URI + "/v2/merchants/stores/:sid/suppliers";
	public static final String SUPPLIERS_UPDATE = HOME_URI + "/v2/merchants/stores/:sid/suppliers/:supplierid";
	public static final String SUPPLIERS_DELETE = HOME_URI + "/v2/merchants/stores/:sid/suppliers/:supplierid";
	
	public static final String STOCK_SUMMARY = HOME_URI + "/v2/merchants/stores/:sid/stocks/summary/total";
	public static final String STOCK_PRODUCT_LIST = HOME_URI + "/v2/merchants/stores/:sid/stocks/summary/products";
	public static final String STOCK_SEARCH = HOME_URI + "/v2/merchants/stores/:sid/stocks/search";
	
}
