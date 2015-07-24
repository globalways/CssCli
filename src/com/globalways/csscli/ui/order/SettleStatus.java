package com.globalways.csscli.ui.order;

public enum SettleStatus {
	
	WATTING(0,"订单等待结算"),
	SUBMIT(1,"提交工单（商户端和平台都可以发起）"),
	AUDIT(2,"财务审计。特指平台审计"),
	PAYED(3,"已经打款"),
	RECEIVED(4,"确认收款");
	
	public int code;
	public String desc;
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
		}
		return null;
	}
}
