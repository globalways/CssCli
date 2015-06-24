package com.globalways.csscli.ui.product;

public enum ProductType {
	/**
	 * 单体型
	 */
	DANTI(1),
	/**
	 * 称重型
	 */
	CHENGZHONG(2);
	int code;
	private ProductType(int code)
	{
		this.code = code;
	}
	
	public static ProductType nameOf(String s)
	{
		switch (s.trim()) {
		case "称重型":
			return ProductType.CHENGZHONG;
		default:
			return ProductType.DANTI;
		}
	}
	public static ProductType codeOf(int code)
	{
		switch (code) {
		case 2:
			return ProductType.CHENGZHONG;

		default:
			return ProductType.DANTI;
		}
	}
	@Override
	public String toString() {
		String result = "";
		switch (this) {
		case DANTI:
			result = "单体型";
			break;
		case CHENGZHONG:
			result = "称重型";
			break;
		}
		return result;
	}
	public int getCode() {
		return code;
	}
}
