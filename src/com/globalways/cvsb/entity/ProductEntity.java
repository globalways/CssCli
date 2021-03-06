package com.globalways.cvsb.entity;

import java.io.Serializable;

import com.globalways.cvsb.tools.PinYin;
import com.globalways.cvsb.tools.QRCodeTools;
import com.globalways.cvsb.ui.product.ProductType;
import com.globalways.cvsb.view.IndexItemInterface;
import com.google.gson.annotations.Expose;

/**
 * 商品分类结构
 * @author wyp E-mail:onebyte@qq.com
 * @version Time: 2015年7月27日 下午3:24:30
 */
public class ProductEntity implements Serializable,IndexItemInterface,Cloneable{

	private static final long serialVersionUID = -3669688556457790489L;
	@Expose
	private long id;
	@Expose
	private long store_id;
	@Expose
	private String product_brand;
	@Expose
	private String product_name;
	 /**
	 * 商品类型
	 * 1: 单体型<br/>2：称重型。默认1
	 */
	@Expose
	private int product_type;
	@Expose
	private int product_category;
	@Expose
	private String product_desc;
	@Expose
	private String product_qr;
	@Expose
	private String product_bar;
	@Expose
	private String product_avatar;
	@Expose
	private long product_retail_price;
	@Expose
	private long product_original_price;
	@Expose 
	private long product_retail_apr; 
	@Expose
	private String product_unit;
	@Expose
	private long product_apr;
	@Expose
	private double sales_cnt;
	@Expose
	private double stock_cnt;
	@Expose
	private long hits;
	@Expose
	private int is_recommend;
	@Expose
	private double stock_limit;
	@Expose
	private String product_tag;
	@Expose
	private long purchase_channel;
	/**
	 * 是否上架: 1-上架   2-不上架
	 */
	@Expose
	private int status;
	@Expose
	private String created;
	@Expose
	private String updated;

	/** 购物车数量 */
	private float shoppingNumber;

	public ProductEntity() {
		super();
	}

	public ProductEntity(long id, long store_id, String product_brand, String product_name, String product_desc,
			String product_qr, String product_bar, String product_avatar, long product_price, String product_unit,
			long product_apr, double sales_cnt, double stock_cnt, long hits, int is_recommend, double stock_limit,
			String product_tag, long purchase_channel, int status, String created, String updated) {
		super();
		this.id = id;
		this.store_id = store_id;
		this.product_brand = product_brand;
		this.product_name = product_name;
		this.product_desc = product_desc;
		this.product_qr = product_qr;
		this.product_bar = product_bar;
		this.product_avatar = product_avatar;
		this.product_retail_price = product_price;
		this.product_unit = product_unit;
		this.product_apr = product_apr;
		this.sales_cnt = sales_cnt;
		this.stock_cnt = stock_cnt;
		this.hits = hits;
		this.is_recommend = is_recommend;
		this.stock_limit = stock_limit;
		this.product_tag = product_tag;
		this.purchase_channel = purchase_channel;
		this.status = status;
		this.created = created;
		this.updated = updated;
	}
	
	@Override
	public String toString() {
		return "ProductEntity [id=" + id + ", store_id=" + store_id + ", product_brand=" + product_brand
				+ ", product_name=" + product_name + ", product_desc=" + product_desc + ", product_qr=" + product_qr
				+ ", product_bar=" + product_bar + ", product_avatar=" + product_avatar + ", product_price="
				+ product_retail_price + ", product_unit=" + product_unit + ", product_apr=" + product_apr + ", sales_cnt="
				+ sales_cnt + ", stock_cnt=" + stock_cnt + ", hits=" + hits + ", is_recommend=" + is_recommend
				+ ", stock_limit=" + stock_limit + ", product_tag=" + product_tag + ", purchase_channel="
				+ purchase_channel + ", status=" + status + ", created=" + created + ", updated=" + updated
				+ ", shoppingNumber=" + shoppingNumber + "]";
	}

	public float getShoppingNumber() {
		return shoppingNumber;
	}

	public void setShoppingNumber(float shoppingNumber) {
		this.shoppingNumber = shoppingNumber;
	}

	public String getProduct_brand() {
		return product_brand;
	}

	public void setProduct_brand(String product_brand) {
		this.product_brand = product_brand;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getStore_id() {
		return store_id;
	}

	public void setStore_id(long store_id) {
		this.store_id = store_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getProduct_desc() {
		return product_desc;
	}

	public void setProduct_desc(String product_desc) {
		this.product_desc = product_desc;
	}

	/**
	 * @return 加上开头 PRODUCT:，方便扫描的时候判断是二维码还是条形码
	 * @see QRCodeTools
	 * @author wyp
	 */
	public String getProduct_qr() {
		return QRCodeTools.PRODUCT+product_qr;
	}

	public void setProduct_qr(String product_qr) {
		this.product_qr = product_qr;
	}

	public String getProduct_bar() {
		return product_bar;
	}

	public void setProduct_bar(String product_bar) {
		this.product_bar = product_bar;
	}

	public String getProduct_avatar() {
		return product_avatar;
	}

	public void setProduct_avatar(String product_avatar) {
		this.product_avatar = product_avatar;
	}

	public String getProduct_unit() {
		return product_unit;
	}

	public void setProduct_unit(String product_unit) {
		this.product_unit = product_unit;
	}

	public long getProduct_apr() {
		return product_apr;
	}

	public void setProduct_apr(long product_apr) {
		this.product_apr = product_apr;
	}

	public double getSales_cnt() {
		return sales_cnt;
	}

	public void setSales_cnt(double sales_cnt) {
		this.sales_cnt = sales_cnt;
	}

	public double getStock_cnt() {
		return stock_cnt;
	}

	public void setStock_cnt(double stock_cnt) {
		this.stock_cnt = stock_cnt;
	}

	public long getHits() {
		return hits;
	}

	public void setHits(long hits) {
		this.hits = hits;
	}

	public int getIs_recommend() {
		return is_recommend;
	}

	public void setIs_recommend(int is_recommend) {
		this.is_recommend = is_recommend;
	}

	public double getStock_limit() {
		return stock_limit;
	}

	public void setStock_limit(double stock_limit) {
		this.stock_limit = stock_limit;
	}

	public String getProduct_tag() {
		return product_tag;
	}

	public void setProduct_tag(String product_tag) {
		this.product_tag = product_tag;
	}

	public long getPurchase_channel() {
		return purchase_channel;
	}

	public void setPurchase_channel(long purchase_channel) {
		this.purchase_channel = purchase_channel;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getUpdated() {
		return updated;
	}

	public void setUpdated(String updated) {
		this.updated = updated;
	}

	public long getProduct_retail_price() {
		return product_retail_price;
	}

	public void setProduct_retail_price(long product_retail_price) {
		this.product_retail_price = product_retail_price;
	}

	public long getProduct_original_price() {
		return product_original_price;
	}

	public void setProduct_original_price(long product_original_price) {
		this.product_original_price = product_original_price;
	}

	public long getProduct_retail_apr() {
		return product_retail_apr;
	}

	public void setProduct_retail_apr(long product_retail_apr) {
		this.product_retail_apr = product_retail_apr;
	}

	public int getProduct_type() {
		return product_type;
	}

	public void setProduct_type(int product_type) {
		this.product_type = product_type;
	}

	public int getProduct_category() {
		return product_category;
	}

	public void setProduct_category(int product_category) {
		this.product_category = product_category;
	}

	@Override
	public String getItemForIndex() {
		return PinYin.getPinYin(this.product_name);
	}

	@Override
	public String getDisplayInfo() {
		return this.product_name;
	}
	
	/**
	 * 是否是单体型商品
	 * @return true单体,false称重
	 */
	public boolean isDanti(){
		return ProductType.codeOf(getProduct_type()).equals(ProductType.DANTI);
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
