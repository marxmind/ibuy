package com.italia.buynsell.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;

public class PurchasingCorn {

	private Long chasedId;
	private String corncolor;
	private String conditions;
	private String dateIn;
	private double kilo;
	private double discount;
	private BigDecimal driver;
	private BigDecimal amount;
	private BigDecimal totalAmount;
	private String procBy;
	private int counter;
	private BigDecimal netPrice;
	private String timeStamp;
	private ClientProfile clientProfile;
	
	public PurchasingCorn(){}
	
	public PurchasingCorn( Long chasedId,
			 String corncolor,
			 String conditions,
			 String dateIn,
			 double kilo,
			 double discount,
			 BigDecimal driver,
			 BigDecimal amount,
			 BigDecimal totalAmount,
			 String procBy,
			 int counter,
			 BigDecimal netPrice,
			 String timeStamp,
			 ClientProfile clientProfile){
		
		this.chasedId = chasedId;
		this.corncolor = corncolor;
		this.conditions = conditions;
		this.dateIn = dateIn;
		this.kilo = kilo;
		this.discount = discount;
		this.driver = driver;
		this.amount = amount;
		this.totalAmount = totalAmount;
		this.procBy = procBy;
		this.counter = counter;
		this.netPrice = netPrice;
		this.timeStamp = timeStamp;
		this.clientProfile = clientProfile;
		
	}
	
	public static List<PurchasingCorn> retrievePurchasingCorn(String sql, String[] params){
		List<PurchasingCorn> cornList = new ArrayList<>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			PurchasingCorn corn = new PurchasingCorn();
			
			try{corn.setChasedId(rs.getLong("chasedid"));}catch(Exception e){}
			try{corn.setCorncolor(rs.getString("corncolor"));}catch(Exception e){}
			try{corn.setConditions(rs.getString("conditions"));}catch(Exception e){}
			try{corn.setDateIn(rs.getString("datein"));}catch(Exception e){}
			try{corn.setKilo(rs.getDouble("kilo"));}catch(Exception e){}
			try{corn.setDiscount(rs.getDouble("discount"));}catch(Exception e){}
			try{corn.setAmount(rs.getBigDecimal("amount"));}catch(Exception e){}
			try{corn.setDriver(rs.getBigDecimal("driver"));}catch(Exception e){}
			try{corn.setTotalAmount(rs.getBigDecimal("totalamount"));}catch(Exception e){}
			try{corn.setProcBy(rs.getString("procby"));}catch(Exception e){}
			try{corn.setTimeStamp(rs.getString("timestamp").replace(".0", ""));}catch(Exception e){}
			try{ClientProfile clientProfile  = new ClientProfile();
			clientProfile.setClientId(rs.getLong("clientId"));
			corn.setClientProfile(clientProfile);}catch(Exception e){}
			
			cornList.add(corn);
			
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return cornList;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public BigDecimal getNetPrice() {
		return netPrice;
	}
	public void setNetPrice(BigDecimal netPrice) {
		this.netPrice = netPrice;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
	public Long getChasedId() {
		return chasedId;
	}
	public void setChasedId(Long chasedId) {
		this.chasedId = chasedId;
	}
	public String getCorncolor() {
		return corncolor;
	}
	public void setCorncolor(String corncolor) {
		this.corncolor = corncolor;
	}
	public String getConditions() {
		return conditions;
	}
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	public String getDateIn() {
		return dateIn;
	}
	public void setDateIn(String dateIn) {
		this.dateIn = dateIn;
	}
	public double getKilo() {
		return kilo;
	}
	public void setKilo(double kilo) {
		this.kilo = kilo;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	public BigDecimal getDriver() {
		return driver;
	}
	public void setDriver(BigDecimal driver) {
		this.driver = driver;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getProcBy() {
		return procBy;
	}
	public void setProcBy(String procBy) {
		this.procBy = procBy;
	}
	
	public ClientProfile getClientProfile() {
		return clientProfile;
	}
	public void setClientProfile(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
	}
	public static String conditionName(String str){
		switch(str){
		case "0" : { return "ALL";}
		case "1" : { return CornConditions.WCD_NATIVE.getName();}
		case "2" : { return CornConditions.WHITE_WET.getName();}
		case "3" : { return CornConditions.WHITE_BILOG.getName();}
		case "4" : { return CornConditions.YELLOW_DRY.getName();}
		case "5" : { return CornConditions.YELLOW_BASA.getName();}
		case "6" : { return CornConditions.YELLOW_BILOG.getName();}
		case "7" : { return CornConditions.WHITE_SEMI_BASA.getName();}
		case "8" : { return CornConditions.YELLOW_SEMI_BASA.getName();}
		}
		return "";
	}
	
	public static String ConditionCode(String str){
		
		if(CornConditions.WCD_NATIVE.getName().equalsIgnoreCase(str)){return "1";
		}else if(CornConditions.WHITE_WET.getName().equalsIgnoreCase(str) ){ return "2";
		}else if(CornConditions.WHITE_BILOG.getName().equalsIgnoreCase(str)){ return "3";
		}else if(CornConditions.YELLOW_DRY.getName().equalsIgnoreCase(str)){ return "4";
		}else if(CornConditions.YELLOW_BASA.getName().equalsIgnoreCase(str)){ return "5";
		}else if(CornConditions.YELLOW_BILOG.getName().equalsIgnoreCase(str)) { return "6";
		}else if(CornConditions.WHITE_SEMI_BASA.getName().equalsIgnoreCase(str)) { return "7";
		}else if(CornConditions.YELLOW_SEMI_BASA.getName().equalsIgnoreCase(str)){ return "8";}
		return "";
	}
			
}
