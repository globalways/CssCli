package com.globalways.csscli.tools;

import com.globalways.csscli.http.HttpApi;

public class QRCodeTools {

	private static final String PRODUCT = "PRODUCT:";

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
}
