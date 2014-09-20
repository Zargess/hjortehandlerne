package com.example.hjortehandlerneapp;

public class Users {
	
	@com.google.gson.annotations.SerializedName("id")
	private String id;
	
	@com.google.gson.annotations.SerializedName("name")
	private String name;
	
	@com.google.gson.annotations.SerializedName("password")
	private String password;
	
	@com.google.gson.annotations.SerializedName("location")
	private String location;

	public String getId() {
		return id;
	}

	public void setId(String uId) {
		this.id = uId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Users && ((Users) o).id.equals(id);
	}
}
