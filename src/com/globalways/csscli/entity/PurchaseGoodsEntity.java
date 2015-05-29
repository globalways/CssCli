package com.globalways.csscli.entity;

/**
 * 进货单 货品列表
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月8日 下午2:13:42
 */
public class PurchaseGoodsEntity {
	
	/**
	 * 批次号
	 */
	private String batch_id;
	/**
	 * 品牌
	 */
	private String product_brand;
	/**
	 * 单位
	 */
	private String product_unit;
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
	public String getBatch_id() {
		return batch_id;
	}
	public void setBatch_id(String batch_id) {
		this.batch_id = batch_id;
	}
	public String getProduct_brand() {
		return product_brand;
	}
	public void setProduct_brand(String product_brand) {
		this.product_brand = product_brand;
	}
	public String getProduct_unit() {
		return product_unit;
	}
	public void setProduct_unit(String product_unit) {
		this.product_unit = product_unit;
	}
}
