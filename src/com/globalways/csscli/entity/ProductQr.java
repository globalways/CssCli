package com.globalways.csscli.entity;

import org.json.JSONException;
import org.json.JSONObject;



/**
 * QR entity
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年6月15日 下午3:05:38
 */
public class ProductQr {
	private String store_id;
	private String product_id;
	
	
	public ProductQr(){
	}
	public ProductQr(String store_id, String product_id)
	{
		this.store_id = store_id;
		this.product_id = product_id;
	}
	
	/**
	 * generate qr string of this {@link ProductQr} object.
	 * @return
	 */
	public String qr()
	{
		StringBuilder sb = new StringBuilder("{\"s\":");
		sb.append(getStore_id())
		.append(",\"p\":")
		.append(getProduct_id()).append("}");
		return sb.toString();
	}
	
	/**
	 * generate {@link ProductQr} Object by qr json str.
	 * @param qr
	 * @return
	 */
	public static ProductQr getInstance(String qr)
	{
		String str_store_id = null;
		String str_product_id = null;
		try {
			JSONObject j = new JSONObject(qr);
			str_store_id = (String) j.get("s");
			str_product_id = (String) j.get("p");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new ProductQr(str_store_id, str_product_id);
		
	}
	public String getStore_id() {
		return store_id;
	}
	public void setStore_id(String store_id) {
		this.store_id = store_id;
	}
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	
}
