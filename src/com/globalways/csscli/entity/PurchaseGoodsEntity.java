package com.globalways.csscli.entity;

/**
 * 进货单 货品列表
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月8日 下午2:13:42
 */
public class PurchaseGoodsEntity {
	
	
	
	/**
	 * 品名
	 */
	private String product_name;
	
	/**
	 * 二维码
	 */
	private String qr;
	/**
	 * 单价
	 */
	private long unit_price; //分
	
	/**
	 * 数量
	 */
	private float amount;
	
	/**
	 * 总价
	 */
	private String total;
	
	public PurchaseGoodsEntity() {
		// TODO Auto-generated constructor stub
	}
	public PurchaseGoodsEntity(PurchaseGoodsEntity entity) {
		setAmount(entity.getAmount());
		setProduct_name(entity.getProduct_name());
		setQr(entity.getQr());
		setTotal(entity.getTotal());
		setUnit_price(entity.getUnit_price());
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getQr() {
		return qr;
	}
	public void setQr(String qr) {
		this.qr = qr;
	}
	public long getUnit_price() {
		return unit_price;
	}
	public void setUnit_price(long unit_price) {
		this.unit_price = unit_price;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	@Override
	public boolean equals(Object o) {
		PurchaseGoodsEntity oEntity = (PurchaseGoodsEntity)o;
		return this.getQr().equals(oEntity.getQr());
	}
}
