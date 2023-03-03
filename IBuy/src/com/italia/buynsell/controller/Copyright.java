package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author mark italia
 * @since 09/28/2016
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class Copyright {
	
	private Long id;
	private String copyrightname;
	private String expdate;
	private String appname;
	private String currentversion;
	private String oldversion;
	private String author;
	private String contactno;
	private String email;
	private Timestamp timestamp;
	
	public static List<Copyright> retrieve(String sql, String[] params){
		List<Copyright> data = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
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
			Copyright app = new Copyright();
			try{app.setId(rs.getLong("id"));}catch(NullPointerException e){}
			try{app.setCopyrightname(rs.getString("copyrightname"));}catch(NullPointerException e){}
			try{app.setExpdate(rs.getString("expdate"));}catch(NullPointerException e){}
			try{app.setAppname(rs.getString("appname"));}catch(NullPointerException e){}
			try{app.setCurrentversion(rs.getString("currentversion"));}catch(NullPointerException e){}
			try{app.setOldversion(rs.getString("oldversion"));}catch(NullPointerException e){}
			try{app.setAuthor(rs.getString("author"));}catch(NullPointerException e){}
			try{app.setContactno(rs.getString("contactno"));}catch(NullPointerException e){}
			try{app.setEmail(rs.getString("email"));}catch(NullPointerException e){}
			try{app.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			data.add(app);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			data.add(errorProvider());
		}
		return data;
	}
	
	private static Copyright errorProvider() {
		Copyright app = Copyright.builder()
				.id(1l)
				.copyrightname("MARXMIND IT SOLUTIONS")
				.expdate("None")
				.appname("TDH")
				.currentversion("1.0")
				.oldversion("1.0")
				.author("Mark Italia")
				.contactno("09175121252")
				.email("markritalia@gmail.com")
				.build();
				
				return app;
	}
	
	private static String dbLicense(){
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		String sql = "SELECT expdate FROM copyright ORDER BY id desc limit 1";	
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		rs = ps.executeQuery();
		
		while(rs.next()){
			return rs.getString("expdate");
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){}
		
		return null;
	}
	
	
	
	/**
	 * 
	 * @return true if expired
	 */
	private static boolean checkdate(String dbLicense){
		
		String systemDate = DateUtils.getCurrentDateMMDDYYYY();
		
		boolean isYear = false;
		boolean isMonth = false;
		boolean isDay = false;
		int sxMonth = Integer.valueOf(systemDate.split("-")[0]);
		int sxDay = Integer.valueOf(systemDate.split("-")[1]);
		int sxYear = Integer.valueOf(systemDate.split("-")[2]);
		
		int dbMonth = Integer.valueOf(dbLicense.split("-")[0]);
		int dbDay = Integer.valueOf(dbLicense.split("-")[1]);
		int dbYear = Integer.valueOf(dbLicense.split("-")[2]);
		
		
		//first check year
		if(dbYear<=sxYear){
			isYear = true;
		}
		//second check month
		if(dbMonth<=sxMonth){
			isMonth = true;
		}
		//third check day
		if(dbDay<=sxDay){
			isDay = true;
		}
		
		if(isYear && isMonth && isDay){
			return true;
		}
		
		/*if(db<=sx){
			//System.out.println("true");
			return true;
		}*/
		
		return false;
	}
	
	private static String[] days(){
		char[] addChar = "markitalia".toCharArray();
		String[] days = new String[10];
		for(int i=0; i<=9;i++){
			days[i] = "0"+i+addChar[i];
		}
		return days;
	}
	
	private static String[] months(){
		char[] addChar = "mritaliamark".toCharArray();
		String[] months = new String[12];
		for(int i=0; i<12;i++){
			months[i] = "0" + (i+1) + addChar[i];
		}
		return months;
	}
	
	private static String[] years(){
		char[] addChar = "markitalia".toCharArray();
		String[] years = new String[12];
		for(int i=0; i<=9;i++){
			years[i] = "0" + i + addChar[i];
		}
		return years;
	}

}
