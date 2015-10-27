package com.globalways.cvsb.entity;

import com.google.gson.annotations.Expose;

public class StoreEntity {

	@Expose
	private long id;
	@Expose
	private long pid;
	@Expose
	private long store_id;
	@Expose
	private String store_name;
	@Expose
	private String store_sub;
	@Expose
	private String store_alias;
	@Expose
	private String store_desc;
	@Expose
	private String industry_name;
	@Expose
	private String store_geo_hash;
	@Expose
	private double store_gps_lat;
	@Expose
	private double store_gps_lng;
	@Expose
	private String store_address;
	@Expose
	private String store_avatar;
	@Expose
	private String store_phone;
	@Expose
	private String store_email;
	@Expose
	private long product_hot_limit;
	@Expose
	private long delivery_distance;
	@Expose
	private long delivery_price;
	@Expose
	private int delivery_flag;
	@Expose
	private int canvass_price;
	@Expose
	private int canvass_flag;
	@Expose
	private String store_news;
	@Expose
	private int hits;
	@Expose
	private int sales_cnt;
	@Expose
	private int status;
	@Expose
	private int store_type;
	@Expose
	private String ht_store_id;
	@Expose
	private String bank_account;
	@Expose
	private String bank_account_name;
	@Expose
	private String store_qr;
	@Expose
	private String created;
	@Expose
	private String updated;

	public StoreEntity() {
		super();
	}

	public StoreEntity(long id, long pid, long store_id, String store_name, String store_sub, String store_alias,
			String store_desc, String industry_name, String store_geo_hash, double store_gps_lat, double store_gps_lng,
			String store_address, String store_avatar, String store_phone, String store_email, long product_hot_limit,
			long delivery_distance, long delivery_price, int delivery_flag, int canvass_price, int canvass_flag,
			String store_news, int hits, int sales_cnt, int status, int store_type, String ht_store_id,
			String bank_account, String bank_account_name, String store_qr, String created, String updated) {
		super();
		this.id = id;
		this.pid = pid;
		this.store_id = store_id;
		this.store_name = store_name;
		this.store_sub = store_sub;
		this.store_alias = store_alias;
		this.store_desc = store_desc;
		this.industry_name = industry_name;
		this.store_geo_hash = store_geo_hash;
		this.store_gps_lat = store_gps_lat;
		this.store_gps_lng = store_gps_lng;
		this.store_address = store_address;
		this.store_avatar = store_avatar;
		this.store_phone = store_phone;
		this.store_email = store_email;
		this.product_hot_limit = product_hot_limit;
		this.delivery_distance = delivery_distance;
		this.delivery_price = delivery_price;
		this.delivery_flag = delivery_flag;
		this.canvass_price = canvass_price;
		this.canvass_flag = canvass_flag;
		this.store_news = store_news;
		this.hits = hits;
		this.sales_cnt = sales_cnt;
		this.status = status;
		this.store_type = store_type;
		this.ht_store_id = ht_store_id;
		this.bank_account = bank_account;
		this.bank_account_name = bank_account_name;
		this.store_qr = store_qr;
		this.created = created;
		this.updated = updated;
	}




	@Override
	public String toString() {
		return "StoreEntity [id=" + id + ", pid=" + pid + ", store_id=" + store_id + ", store_name=" + store_name
				+ ", store_sub=" + store_sub + ", store_alias=" + store_alias + ", store_desc=" + store_desc
				+ ", industry_name=" + industry_name + ", store_geo_hash=" + store_geo_hash + ", store_gps_lat="
				+ store_gps_lat + ", store_gps_lng=" + store_gps_lng + ", store_address=" + store_address
				+ ", store_avatar=" + store_avatar + ", store_phone=" + store_phone + ", store_email=" + store_email
				+ ", product_hot_limit=" + product_hot_limit + ", delivery_distance=" + delivery_distance
				+ ", delivery_price=" + delivery_price + ", delivery_flag=" + delivery_flag + ", canvass_price="
				+ canvass_price + ", canvass_flag=" + canvass_flag + ", store_news=" + store_news + ", hits=" + hits
				+ ", sales_cnt=" + sales_cnt + ", status=" + status + ", store_type=" + store_type + ", ht_store_id="
				+ ht_store_id + ", bank_account=" + bank_account + ", bank_account_name=" + bank_account_name
				+ ", store_qr=" + store_qr + ", created=" + created + ", updated=" + updated + "]";
	}




	public String getHt_store_id() {
		return ht_store_id;
	}


	public void setHt_store_id(String ht_store_id) {
		this.ht_store_id = ht_store_id;
	}


	public String getBank_account() {
		return bank_account;
	}


	public void setBank_account(String bank_account) {
		this.bank_account = bank_account;
	}


	public String getBank_account_name() {
		return bank_account_name;
	}


	public void setBank_account_name(String bank_account_name) {
		this.bank_account_name = bank_account_name;
	}


	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public long getStore_id() {
		return store_id;
	}

	public void setStore_id(long store_id) {
		this.store_id = store_id;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public String getStore_sub() {
		return store_sub;
	}

	public void setStore_sub(String store_sub) {
		this.store_sub = store_sub;
	}

	public String getStore_alias() {
		return store_alias;
	}

	public void setStore_alias(String store_alias) {
		this.store_alias = store_alias;
	}

	public String getStore_desc() {
		return store_desc;
	}

	public void setStore_desc(String store_desc) {
		this.store_desc = store_desc;
	}

	public String getIndustry_name() {
		return industry_name;
	}

	public void setIndustry_name(String industry_name) {
		this.industry_name = industry_name;
	}

	public String getStore_geo_hash() {
		return store_geo_hash;
	}

	public void setStore_geo_hash(String store_geo_hash) {
		this.store_geo_hash = store_geo_hash;
	}

	public double getStore_gps_lat() {
		return store_gps_lat;
	}

	public void setStore_gps_lat(double store_gps_lat) {
		this.store_gps_lat = store_gps_lat;
	}

	public double getStore_gps_lng() {
		return store_gps_lng;
	}

	public void setStore_gps_lng(double store_gps_lng) {
		this.store_gps_lng = store_gps_lng;
	}

	public String getStore_address() {
		return store_address;
	}

	public void setStore_address(String store_address) {
		this.store_address = store_address;
	}

	public String getStore_avatar() {
		return store_avatar;
	}

	public void setStore_avatar(String store_avatar) {
		this.store_avatar = store_avatar;
	}

	public String getStore_phone() {
		return store_phone;
	}

	public void setStore_phone(String store_phone) {
		this.store_phone = store_phone;
	}

	public String getStore_email() {
		return store_email;
	}

	public void setStore_email(String store_email) {
		this.store_email = store_email;
	}

	public long getProduct_hot_limit() {
		return product_hot_limit;
	}

	public void setProduct_hot_limit(long product_hot_limit) {
		this.product_hot_limit = product_hot_limit;
	}

	public long getDelivery_distance() {
		return delivery_distance;
	}

	public void setDelivery_distance(long delivery_distance) {
		this.delivery_distance = delivery_distance;
	}

	public long getDelivery_price() {
		return delivery_price;
	}

	public void setDelivery_price(long delivery_price) {
		this.delivery_price = delivery_price;
	}

	public int getDelivery_flag() {
		return delivery_flag;
	}

	public void setDelivery_flag(int delivery_flag) {
		this.delivery_flag = delivery_flag;
	}

	public int getCanvass_price() {
		return canvass_price;
	}

	public void setCanvass_price(int canvass_price) {
		this.canvass_price = canvass_price;
	}

	public int getCanvass_flag() {
		return canvass_flag;
	}

	public void setCanvass_flag(int canvass_flag) {
		this.canvass_flag = canvass_flag;
	}

	public String getStore_news() {
		return store_news;
	}

	public void setStore_news(String store_news) {
		this.store_news = store_news;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public int getSales_cnt() {
		return sales_cnt;
	}

	public void setSales_cnt(int sales_cnt) {
		this.sales_cnt = sales_cnt;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStore_type() {
		return store_type;
	}

	public void setStore_type(int store_type) {
		this.store_type = store_type;
	}

	public String getStore_qr() {
		return store_qr;
	}

	public void setStore_qr(String store_qr) {
		this.store_qr = store_qr;
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
}
