package com.globalways.csscli.ui.order;

/**
 * 订单状态
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月17日 下午7:15:55
 */
public enum OrderStatus {
	
	ERROR(1,"订单错误"),
	WATTING_FOR_PAY(2,"等待付款"),
	PAY_SUCCESS(3,"付款成功"),
	SHIPPED_WITHOUT_EXPRESS(4,"用户自提发货"),
	SHIPPED_WITH_EXPRESS(5,"配送发货");
	public int code;
	public String name;
	OrderStatus(int code,String name){
		this.code = code;
		this.name = name;
	}
	
	public static OrderStatus valueOf(int code){
		switch (code) {
		case 1 : return ERROR;
		case 2 : return WATTING_FOR_PAY;
		case 3 : return PAY_SUCCESS;
		case 4 : return SHIPPED_WITHOUT_EXPRESS;
		case 5 : return OrderStatus.SHIPPED_WITH_EXPRESS;
		default : return null;
		}
	}
}
