package com.globalways.csscli.entity;

import com.google.gson.annotations.Expose;

public class OrderEntity {

	@Expose
	private long id;
	/** 订单ID */
	@Expose
	private String order_id;
	/** ping++订单id */
	@Expose
	private String ping_charge_id;
	/** 默认1 */
	@Expose
	private int order_type;
	@Expose
	private long store_id;
	@Expose
	private String store_name;
	/** 用户ID */
	@Expose
	private String buyer;
	/** 会员卡 */
	@Expose
	private String card;
	/** 订单地址ID */
	@Expose
	private long address_id;
	/** 付款方式 */
	@Expose
	private int payment_type;
	/** 送货方式 */
	@Expose
	private int delivery_type;
	/** 订单总价 */
	@Expose
	private long order_amount;
	/** 商家主动省了多少钱 */
	@Expose
	private long discount_amount;
	/** 送货费用 */
	@Expose
	private long delivery_price;
	/** 揽货费用 */
	@Expose
	private long canvass_price;
	/** 备注 */
	@Expose
	private String comment;
	/** 最新状态 */
	@Expose
	private int last_status;
	/** 最新状态描述 */
	//and by wyp
	@Expose
	private long order_time;
	@Expose
	private String products_count;
	@Expose
	private String settle_serial_no;
	@Expose
	private int settle_status;
	
	@Expose
	private String last_status_desc;
	@Expose
	private String created;
	@Expose
	private String updated;

	public OrderEntity() {
		super();
	}

	public OrderEntity(long id, String order_id, String ping_charge_id, int order_type, long store_id,
			String store_name, String buyer, String card, long address_id, int payment_type, int delivery_type,
			long order_amount, long discount_amount, long delivery_price, long canvass_price, String comment,
			int last_status, String last_status_desc, String created, String updated) {
		super();
		this.id = id;
		this.order_id = order_id;
		this.ping_charge_id = ping_charge_id;
		this.order_type = order_type;
		this.store_id = store_id;
		this.store_name = store_name;
		this.buyer = buyer;
		this.card = card;
		this.address_id = address_id;
		this.payment_type = payment_type;
		this.delivery_type = delivery_type;
		this.order_amount = order_amount;
		this.discount_amount = discount_amount;
		this.delivery_price = delivery_price;
		this.canvass_price = canvass_price;
		this.comment = comment;
		this.last_status = last_status;
		this.last_status_desc = last_status_desc;
		this.created = created;
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "OrderEntity [id=" + id + ", order_id=" + order_id + ", ping_charge_id=" + ping_charge_id
				+ ", order_type=" + order_type + ", store_id=" + store_id + ", store_name=" + store_name + ", buyer="
				+ buyer + ", card=" + card + ", address_id=" + address_id + ", payment_type=" + payment_type
				+ ", delivery_type=" + delivery_type + ", order_amount=" + order_amount + ", discount_amount="
				+ discount_amount + ", delivery_price=" + delivery_price + ", canvass_price=" + canvass_price
				+ ", comment=" + comment + ", last_status=" + last_status + ", last_status_desc=" + last_status_desc
				+ ", created=" + created + ", updated=" + updated + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

	public String getPing_charge_id() {
		return ping_charge_id;
	}

	public void setPing_charge_id(String ping_charge_id) {
		this.ping_charge_id = ping_charge_id;
	}

	public int getOrder_type() {
		return order_type;
	}

	public void setOrder_type(int order_type) {
		this.order_type = order_type;
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

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public long getAddress_id() {
		return address_id;
	}

	public void setAddress_id(long address_id) {
		this.address_id = address_id;
	}

	public int getPayment_type() {
		return payment_type;
	}

	public void setPayment_type(int payment_type) {
		this.payment_type = payment_type;
	}

	public int getDelivery_type() {
		return delivery_type;
	}

	public void setDelivery_type(int delivery_type) {
		this.delivery_type = delivery_type;
	}

	public long getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(long order_amount) {
		this.order_amount = order_amount;
	}

	public long getDiscount_amount() {
		return discount_amount;
	}

	public void setDiscount_amount(long discount_amount) {
		this.discount_amount = discount_amount;
	}

	public long getDelivery_price() {
		return delivery_price;
	}

	public void setDelivery_price(long delivery_price) {
		this.delivery_price = delivery_price;
	}

	public long getCanvass_price() {
		return canvass_price;
	}

	public void setCanvass_price(long canvass_price) {
		this.canvass_price = canvass_price;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getLast_status() {
		return last_status;
	}

	public void setLast_status(int last_status) {
		this.last_status = last_status;
	}

	public String getLast_status_desc() {
		return last_status_desc;
	}

	public void setLast_status_desc(String last_status_desc) {
		this.last_status_desc = last_status_desc;
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

	/**
	 * 付款方式
	 * @author wyp E-mail:onebyte@qq.com
	 * @version Time: 2015年7月27日 下午4:54:17
	 */
	public enum PayType{
		CASH(1,"现金支付"),ONLINE(2,"在线支付"),UNKNOWN(-1,"未知");
		private int code;
		public String name;
		private PayType(int code, String name) {
			this.code = code;
			this.name = name;
		}
		public static PayType valueOf(int code){
			switch (code) {
			case 1 : return CASH;
			case 2 : return ONLINE;
			default: return UNKNOWN;
			}
		}
	}

	public long getOrder_time() {
		return order_time;
	}

	public void setOrder_time(long order_time) {
		this.order_time = order_time;
	}

	public String getProducts_count() {
		return products_count;
	}

	public void setProducts_count(String products_count) {
		this.products_count = products_count;
	}

	public String getSettle_serial_no() {
		return settle_serial_no;
	}

	public void setSettle_serial_no(String settle_serial_no) {
		this.settle_serial_no = settle_serial_no;
	}

	public int getSettle_status() {
		return settle_status;
	}

	public void setSettle_status(int settle_status) {
		this.settle_status = settle_status;
	}
	
}
