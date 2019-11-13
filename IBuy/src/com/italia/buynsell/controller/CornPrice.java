package com.italia.buynsell.controller;

public class CornPrice {

	private String volume;
	private String price;
	private Long cornId;
	private String variant;
	private String conditions;
	private Corns corns;
	private Long priceId;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getPriceId() {
		return priceId;
	}
	public void setPriceId(Long priceId) {
		this.priceId = priceId;
	}
	public Corns getCorns() {
		return corns;
	}
	public void setCorns(Corns corns) {
		this.corns = corns;
	}
	public String getVariant() {
		return variant;
	}
	public void setVariant(String variant) {
		this.variant = variant;
	}
	public String getConditions() {
		return conditions;
	}
	public void setConditions(String condition) {
		this.conditions = condition;
	}
	
	
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public Long getCornId() {
		return cornId;
	}
	public void setCornId(Long cornId) {
		this.cornId = cornId;
	}
	
	
	
}
