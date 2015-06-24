package com.globalways.csscli.entity;

/**
 * 进货单 货品列表
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年5月8日 下午2:13:42
 */
public class PurchaseGoodsEntity {
	
	
	
	
	private int supplier_id;
	/**
	 * 供应商
	 */
	private SupplierEntity supplier;
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
	private String product_qr;
	/**
	 * 单价
	 */
	private long purchase_price; //分
	
	/**
	 * 数量
	 */
	private String purchase_count;
	
	
	public PurchaseGoodsEntity() {
		// TODO Auto-generated constructor stub
	}
	public PurchaseGoodsEntity(PurchaseGoodsEntity entity) {
		setPurchase_count(entity.getPurchase_count());
		setProduct_name(entity.getProduct_name());
		setProduct_qr(entity.getProduct_qr());
		setPurchase_price(entity.getPurchase_price());
		setPurchase_price(entity.getPurchase_price());
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getProduct_qr() {
		return product_qr;
	}
	public void setProduct_qr(String product_qr) {
		this.product_qr = product_qr;
	}
	public long getPurchase_price() {
		return purchase_price;
	}
	public void setPurchase_price(long purchase_price) {
		this.purchase_price = purchase_price;
	}
	public String getPurchase_count() {
		return purchase_count;
	}
	public void setPurchase_count(String purchase_count) {
		this.purchase_count = purchase_count;
	}
	@Override
	public boolean equals(Object o) {
		PurchaseGoodsEntity oEntity = (PurchaseGoodsEntity)o;
		return this.getProduct_qr().equals(oEntity.getProduct_qr());
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
	public SupplierEntity getSupplier() {
		return supplier;
	}
	public void setSupplier(SupplierEntity supplier) {
		this.supplier = supplier;
	}
	public int getSupplier_id() {
		return supplier_id;
	}
	public void setSupplier_id(int supplier_id) {
		this.supplier_id = supplier_id;
	}
}
