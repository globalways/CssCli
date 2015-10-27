package com.globalways.cvsb.tools;

import java.math.BigDecimal;


/**
 * 用户输入验证验证
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年6月20日 下午4:03:39
 */
public class InputVali {
	/**
	 * 是否为大于0的浮点数
	 * @return true 验证通过, false验证失败
	 */
	public static boolean isFloatValue(String s)
	{
		double value = 0d;
		try {
			value = Double.parseDouble(s);
			if(s==null || s.isEmpty() || value <= 0 )
			{
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/**
	 * 价格单位为元,是否最小表示到分<br>example<br> 12.32 true 12.352 false
	 * @param price
	 * @return
	 */
	public static boolean isFloatAsFen(String price)
	{
		if(isFloatValue(price))
		{
			if(new BigDecimal(price).scale()>2)
				return false;
			else
				return true;
		}else
			return false;
	}
	
	/**
	 * 是否为合法的折扣(大于0的整数)
	 * @param apr
	 * @return
	 */
	public static boolean isLegalApr(String apr){
		try {
			int apr_int = Integer.parseInt(apr);
			if(apr_int<0){
				return false;
			}
			if(apr_int>100){
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
