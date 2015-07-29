package com.globalways.csscli.ui.order;

public enum SettleStatus {
	
	WATTING(0,"等待结算"),
	SUBMIT(1,"工单提交"),
	AUDIT(2,"平台审计"),
	PAYED(3,"已经打款"),
	RECEIVED(4,"确认收款"),
	CLOSE(5,"工单关闭"),
	ERROR(-1,"未知状态");
	
	private int code;
	private String desc;
	private SettleStatus(int code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	/**
	 * get status by specify code
	 * @param code
	 * @return
	 */
	public static SettleStatus valueOf(int code){
		switch (code) {
		case 0 : return WATTING;
		case 1 : return SUBMIT;
		case 2 : return AUDIT;
		case 3 : return PAYED;
		case 4 : return RECEIVED;
		case 5 : return CLOSE;
		default : return ERROR;
		}
	}

	/**
	 * 获取状态描述信息
	 * @return
	 */
	public String getDesc() {
		return desc;
	}

	public int getCode() {
		return code;
	}
}
