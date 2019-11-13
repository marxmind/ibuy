package com.italia.buynsell.controller;

public class Corns {

	private Long cornId;
	private String cornName;
	private String cornType;
	private String cornColor;
	private String timestamp;
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getCornColor() {
		return cornColor;
	}
	public void setCornColor(String cornColor) {
		this.cornColor = cornColor;
	}
	public Long getCornId() {
		return cornId;
	}
	public void setCornId(Long cornId) {
		this.cornId = cornId;
	}
	public String getCornName() {
		return cornName;
	}
	public void setCornName(String cornName) {
		this.cornName = cornName;
	}
	public String getCornType() {
		return cornType;
	}
	public void setCornType(String cornType) {
		this.cornType = cornType;
	}
	
	
}
