package com.italia.buynsell.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

	public static boolean validate(String unme, String pazzword){
		Connection conn = null;
		PreparedStatement ps = null;
		
		try{
			conn = DataConnectDAO.getConnection();
			ps = conn.prepareStatement("SELECT username, password FROM login WHERE username= ? AND password=?");
			ps.setString(1, unme);
			ps.setString(2, pazzword);
			
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				System.out.println(rs.getString("username"));
				System.out.println(rs.getString("password"));
				return true;
			}
			
		}catch(SQLException sql){
			sql.printStackTrace();
			System.out.println("Login error>>>> ");
			return false;
		}finally{
			DataConnectDAO.close(conn);
		}
		return false;
	}
	public static void main(String[] args) {
		
		LoginDAO.validate("mark", "1");
		
	}
	
}
