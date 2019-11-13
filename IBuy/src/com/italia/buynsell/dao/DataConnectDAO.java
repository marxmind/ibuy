package com.italia.buynsell.dao;

import java.sql.Connection;
import java.sql.DriverManager;

import com.italia.buynsell.controller.ReadApplicationDetails;

public class DataConnectDAO {

	
	public static Connection getConnection(){
		Connection conn = null;
		ReadApplicationDetails db = new ReadApplicationDetails();
		try{
			Class.forName(db.getDriver());
			String url = db.getAddressPort()+db.getDatabaseName()+"?"+db.getSSL();
			conn = DriverManager.getConnection(url, db.getUserName(), db.getPassword());
			
			
			return conn;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static void close(Connection conn){
		try{
			if(conn!=null){
				conn.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		Connection c = getConnection();
		
	}
	
}
