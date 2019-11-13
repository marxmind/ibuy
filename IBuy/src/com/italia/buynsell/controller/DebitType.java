package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.dao.DataConnectDAO;

public class DebitType {

	private int id;
	private String description;
	private Timestamp timestamp;
	
	public static void save(DebitType deb){
		if(deb!=null){
			int id = DebitType.getDebitTypeInfo(deb.getId()==0? DebitType.getLatestDebitTypeId()+1 : deb.getId());
			if(id==1){
				DebitType.insertData(deb, "1");
			}else if(id==2){
				DebitType.updateData(deb);
			}else if(id==3){
				DebitType.insertData(deb, "3");
			}
		}
	}
	
public static List<DebitType> retrieveDebitType(String sql, String[] params){
		
		List<DebitType> debList = new ArrayList<>();
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
			DebitType deb = new DebitType();
			deb.setId(rs.getInt("id"));
			deb.setDescription(rs.getString("description"));
			deb.setTimestamp(rs.getTimestamp("timestamp"));
			debList.add(deb);
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		return debList;
	}
	
	public static DebitType insertData(DebitType deb, String type){
		String sql = "INSERT INTO debittype ("
				+ "id,"
				+ "description) " 
				+ "values(?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		int id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			deb.setId(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestDebitTypeId()+1;
			ps.setInt(1, id);
			deb.setId(id);
		}
		ps.setString(2, deb.getDescription());
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
		return deb;
	}
	
	public static DebitType updateData(DebitType deb){
		String sql = "UPDATE debittype SET description=? " 
				+ " WHERE id=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, deb.getDescription());
		ps.setLong(2,deb.getId());
		ps.executeUpdate();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		return deb;
	}
	
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	public static int getLatestDebitTypeId(){
		int id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT id FROM debittype  ORDER BY id DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getInt("id");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static Integer getDebitTypeInfo(int id){
		boolean isNotNull=false;
		int result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		int val = getLatestDebitTypeId();	
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT id FROM debittype WHERE id=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
}
