package com.example.portfolio.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name="admin")
public class AdminEntity {
	
	protected AdminEntity() {}
	
	public AdminEntity(String id, String password) {
		super();
		this.id = id;
		this.password = password;
	}
	
	@Id
	private String id;
	
	private String password;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "AdminEntity [id=" + id + ", password=" + password + "]";
	}
}
