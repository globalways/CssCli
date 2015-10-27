package com.globalways.cvsb.ui.order;

public enum SettleStatus {
	
	WATTING(0,"未结算"),
	SUBMIT(1,"工单提交"),
	AUDIT(2,"平台审计"),
	AUDIT_COMPLETE(3,"审计完成"),
	PAYED(4,"已经打款"),
	RECEIVED(5,"已经收款"),
	CLOSE(6,"工单关闭"),
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
		case 3 : return SettleStatus.AUDIT_COMPLETE;
		case 4 : return SettleStatus.PAYED;
		case 5 : return SettleStatus.RECEIVED;
		case 6 : return SettleStatus.CLOSE;
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
