package com.globalways.cvsb.entity;

import com.google.gson.annotations.Expose;

public class UserEntity {

	@Expose
	private long id;
	@Expose
	private long hong_id;
	@Expose
	private String tel;
	@Expose
	private String email;
	@Expose
	private String password;
	@Expose
	private String nick_name;
	@Expose
	private String member_avatar;
	@Expose
	private int member_type;
	@Expose
	private int status;
	@Expose
	private String created;
	@Expose
	private String updated;

	public UserEntity() {
		super();
	}

	public UserEntity(long id, long hong_id, String tel, String email, String password, String nick_name,
			String member_avatar, int member_type, int status, String created, String updated) {
		super();
		this.id = id;
		this.hong_id = hong_id;
		this.tel = tel;
		this.email = email;
		this.password = password;
		this.nick_name = nick_name;
		this.member_avatar = member_avatar;
		this.member_type = member_type;
		this.status = status;
		this.created = created;
		this.updated = updated;
	}

	@Override
	public String toString() {
		return "UserEntity [id=" + id + ", hong_id=" + hong_id + ", tel=" + tel + ", email=" + email + ", password="
				+ password + ", nick_name=" + nick_name + ", member_avatar=" + member_avatar + ", member_type="
				+ member_type + ", status=" + status + ", created=" + created + ", updated=" + updated + "]";
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getHong_id() {
		return hong_id;
	}

	public void setHong_id(long hong_id) {
		this.hong_id = hong_id;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getMember_avatar() {
		return member_avatar;
	}

	public void setMember_avatar(String member_avatar) {
		this.member_avatar = member_avatar;
	}

	public int getMember_type() {
		return member_type;
	}

	public void setMember_type(int member_type) {
		this.member_type = member_type;
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
}
