package com.globalways.csscli.ui.order;

public enum SettleApplyType {
	STORE(1,"商户"),HUANTU(2,"平台");
	private int code;
	private String desc;
	private SettleApplyType(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	public static SettleApplyType valueOf(int code){
		switch (code) {
		case 1: return STORE;
		case 2: return HUANTU;
		default : return null;
		}
	}
	public int getCode() {
		return code;
	}
	public String getDesc() {
		return desc;
	}
}
