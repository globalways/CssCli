package com.globalways.csscli.http;

public class HttpApi {

	private static final String HOME_URI = "http://123.57.132.7:8081";
	private static final String VERSION = "";

	public static final String VERSION_CHECK = HOME_URI + "/v1/version/varify";

	public static final String ACCOUNT_LOGIN = HOME_URI + VERSION + "/v1/access/users/login";

	public static final String HONG_ID_GET_BY_PAGE = HOME_URI + VERSION + "/v1/admins/members";

	public static final String HONG_ID_GET_ALL = HOME_URI + VERSION + "/v1/admins/members/all";

	public static final String PRODUCT_GET_LIST = HOME_URI + "/v2/merchants/stores/:sid/products";
	public static final String PRODUCT_ADD_PRODUCT = HOME_URI + "/v2/merchants/stores/:sid/products";
	public static final String PRODUCT_UPDATE_INFO = HOME_URI + "/v2/merchants/stores/:sid/products/qr/:qr";

	public static final String PRODUCT_GET_DETAIL_BAR_CODE = HOME_URI + "/v2/merchants/stores/:sid/products/bar/:bar";
	public static final String PRODUCT_GET_DETAIL_QR_CODE = HOME_URI + "/v2/merchants/stores/:sid/products/qr/:qr";

	public static final String QINIU_GET_UPLOAD_TOKEN = HOME_URI + "/v2/public/qiniu/uptoken";

	public static final String ORDER_SIGN = HOME_URI + "/v2/orders";
	public static final String ORDER_CASH_DONE = HOME_URI + "/v2/orders/:oid/cashDone";
	public static final String ORDER_CANCEL = HOME_URI + "/v2/orders/:oid/storeCancel";

	// add by wyp
	public static final String STORE_CHILDERN_ALL = HOME_URI + "/v2/merchants/stores/:sid/children/all";
	// end
	
	/** 销售统计 */
	public static final String STATISTICS_SALL = HOME_URI + "/v2/orders/stat";
	/** 采购统计 */
	public static final String STATISTICS_BUY = HOME_URI + "/v2/merchants/stores/purchases/stat";
	
	public static final String PURCHASES_ALL = HOME_URI + "/v2/merchants/stores/:sid/purchases/all";
	public static final String PURCHASES_NEW = HOME_URI + "/v2/merchants/stores/:sid/purchases";
	public static final String PURCHASES_PRODUCT_LIST = HOME_URI + "/v2/merchants/stores/:sid/purchases/:batchid/products";
	
	public static final String SUPPLIERS_ALL = HOME_URI + "/v2/merchants/stores/:sid/suppliers/all";
	public static final String SUPPLIERS_NEW = HOME_URI + "/v2/merchants/stores/:sid/suppliers";
	public static final String SUPPLIERS_UPDATE = HOME_URI + "/v2/merchants/stores/:sid/suppliers/:supplierid";
	public static final String SUPPLIERS_DELETE = HOME_URI + "/v2/merchants/stores/:sid/suppliers/:supplierid";
}
