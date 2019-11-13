package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;

public class StatusTrans {

	private Long statusid;
	private String useTable;
	private String statusType;
	private String statusName;
	private String statusNumber;
	
	public static List<StatusTrans> retrieveStatusTrans(String sql, String[] params){
		List<StatusTrans> transList = new ArrayList<>();
		
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
			StatusTrans trans = new StatusTrans();
			
			try{trans.setStatusid(rs.getLong("statusid"));}catch(Exception e){}
			try{trans.setUseTable(rs.getString("use_table"));}catch(Exception e){}
			try{trans.setStatusType(rs.getString("status_type"));}catch(Exception e){}
			try{trans.setStatusName(rs.getString("status_name"));}catch(Exception e){}
			try{trans.setStatusNumber(rs.getString("status_number"));}catch(Exception e){}
			transList.add(trans);
			
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return transList;
	}
	
	public Long getStatusid() {
		return statusid;
	}
	public void setStatusid(Long statusid) {
		this.statusid = statusid;
	}
	public String getUseTable() {
		return useTable;
	}
	public void setUseTable(String useTable) {
		this.useTable = useTable;
	}
	public String getStatusType() {
		return statusType;
	}
	public void setStatusType(String statusType) {
		this.statusType = statusType;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public String getStatusNumber() {
		return statusNumber;
	}
	public void setStatusNumber(String statusNumber) {
		this.statusNumber = statusNumber;
	}
	
}
