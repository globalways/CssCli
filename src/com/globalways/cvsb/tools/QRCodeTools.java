package com.globalways.cvsb.tools;

import com.globalways.cvsb.http.HttpApi;

/**
 * 二维码、条形码工具类
 * 
 * @author James.Fan
 *
 */
public class QRCodeTools {

	public static final String PRODUCT = "PRODUCT:";

	public enum CodeType {
		/** 条形码 */
		BAR_CODE(HttpApi.PRODUCT_GET_DETAIL_BAR_CODE, ":bar"),
		/** 二维码 */
		QR_CODE(HttpApi.PRODUCT_GET_DETAIL_QR_CODE, ":qr");

		private CodeType(String url, String code) {
			this.url = url;
			this.code = code;
		}

		private String url;
		private String sid = ":sid";
		private String code;
		private String context;

		public String getContext() {
			return context;
		}

		public void setContext(String context) {
			this.context = context;
		}

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
	 * 获取Code类型
	 * 
	 * @param content
	 * @return
	 */
	public CodeType getCodeType(String content) {
		CodeType type;
		if (content.startsWith(PRODUCT)) {
			type = CodeType.QR_CODE;
			type.setContext(content.replace(PRODUCT, ""));
		} else {
			type = CodeType.BAR_CODE;
			type.setContext(content);
		}
		return type;
	}

	/**
	 * 判断是否是条形码
	 * 
	 * @param content
	 * @return 如果不是我们内部的二维码就认为是条形码
	 */
	public boolean isBarCode(String content) {
		if (content.startsWith(PRODUCT)) {
			return false;
		}
		return true;
	}
}
