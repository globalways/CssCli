package com.globalways.csscli.http;

public class HttpApi {

	private static final String HOME_URI = "http://123.57.132.7:8081";
	private static final String VERSION = "";

	public static final String ACCOUNT_LOGIN = HOME_URI + VERSION + "";

	public static final String HONG_ID_GET_BY_PAGE = HOME_URI + VERSION + "/v1/admins/members";

	public static final String HONG_ID_GET_ALL = HOME_URI + VERSION + "/v1/admins/members/all";

	public static final String PRODUCT_GET_LIST = HOME_URI + "/v2/merchants/stores/:sid/products";
	public static final String PRODUCT_ADD_PRODUCT = HOME_URI + "/v2/merchants/stores/:sid/products";
}
