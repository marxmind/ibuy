package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;

public class Purchased {

	private Long id;
	private String description;
	private String amountIn;
	private String dateIn;
	private String addedBy;
	private int countAdded;
	private int cnt;
	
	public Purchased(){}
	
	public Purchased(Long id,
	String description,
	String amountIn,
	String dateIn,
	String addedBy,
	int countAdded){
		this.id = id;
		this.description = description;
		this.amountIn = amountIn;
		this.dateIn = dateIn;
		this.addedBy = addedBy;
		this.countAdded = countAdded;
	}
	
	public static List<Purchased> retrievePurchased(String sql, String[] params){
		List<Purchased> pureList = new ArrayList<>();
		
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
			Purchased pure = new Purchased();
			try{pure.setId(rs.getLong("pureId"));}catch(Exception e){}
			try{pure.setDescription(rs.getString("description"));}catch(Exception e){}
			try{pure.setAmountIn(rs.getString("amountIn"));}catch(Exception e){}
			try{pure.setAddedBy(rs.getString("processedBy"));}catch(Exception e){}
			try{pure.setDateIn(rs.getString("dateIn"));}catch(Exception e){}
			try{pure.setCountAdded(rs.getInt("countAdded"));}catch(Exception e){}
			pureList.add(pure);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}	
			
		return pureList; 
	}
	
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmountIn() {
		return amountIn;
	}
	public void setAmountIn(String amountIn) {
		this.amountIn = amountIn;
	}
	public String getDateIn() {
		return dateIn;
	}
	public void setDateIn(String dateIn) {
		this.dateIn = dateIn;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public int getCountAdded() {
		return countAdded;
	}
	public void setCountAdded(int countAdded) {
		this.countAdded = countAdded;
	}
	
	
	
}
