package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.ei.EICashOut;
import com.italia.buynsell.ei.EICashPay;
import com.italia.buynsell.utils.LogUserActions;

public class ClientProfile {

	private Long clientId;
	private String registeredDate;
	private String contactNumber;
	private String fullName;
	private String address;
	private String addedBy;
	private String picturePath;
	private String amntCollectible;
	private int isActive;
	
	public ClientProfile(){}
	
	public ClientProfile(
		long clientId,
		String registeredDate,
		String contactNumber,
		String fullName,
		String address,
		String addedBy,
		String picturePath
			){
		
		this.clientId = clientId;
		this.registeredDate = registeredDate;
		this.contactNumber = contactNumber;
		this.fullName = fullName;
		this.address = address;
		this.addedBy = addedBy;
		this.picturePath = picturePath;
	}
	
	
	public static List<ClientProfile> retrieveClientProfile(String sql, String[] params){
		List<ClientProfile> profList = new ArrayList<>();
		
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
		System.out.println("SQL ClientProfile: "+ ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			ClientProfile prof = new ClientProfile();
			
			try{prof.setClientId(rs.getLong("clientId"));}catch(Exception e){}
			try{prof.setRegisteredDate(rs.getString("registeredDate"));}catch(Exception e){}
			try{prof.setContactNumber(rs.getString("contactNumber"));}catch(Exception e){}
			try{prof.setFullName(rs.getString("fullName"));}catch(Exception e){}
			try{prof.setAddress(rs.getString("address"));}catch(Exception e){}
			try{prof.setAddedBy(rs.getString("addedBy"));}catch(Exception e){}
			String photo = "";
			if(rs.getString("picpath").contains("\\")) {
				photo = "tmp";
			}else {
				photo = rs.getString("picpath");
			}
			try{prof.setPicturePath(photo);}catch(Exception e){}
			try{prof.setIsActive(rs.getInt("isactiveclient"));}catch(Exception e){}
			profList.add(prof);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return profList;
	}
	
	public static ClientProfile save(ClientProfile prof){
		if(prof!=null){
			List<String> actions = new ArrayList<>();
			long id = ClientProfile.getClientInfo(prof.getClientId()==null? ClientProfile.getLatestClientId()+1 : prof.getClientId());
			actions.add("checking for new added data");
			if(id==1){
				actions.add("insert new Data ");
				LogUserActions.logUserActions(actions);
				prof = ClientProfile.insertData(prof, "1");
			}else if(id==2){
				actions.add("update Data ");
				LogUserActions.logUserActions(actions);
				prof = ClientProfile.updateData(prof);
			}else if(id==3){
				actions.add("added new Data ");
				LogUserActions.logUserActions(actions);
				prof = ClientProfile.insertData(prof, "3");
			}
			
		}
		return prof;
	}
	
	public void save(){
			List<String> actions = new ArrayList<>();
			long id = getClientInfo(getClientId()==null? getLatestClientId()+1 : getClientId());
			actions.add("checking for new added data");
			if(id==1){
				actions.add("insert new Data ");
				LogUserActions.logUserActions(actions);
				insertData("1");
			}else if(id==2){
				actions.add("update Data ");
				LogUserActions.logUserActions(actions);
				updateData();
			}else if(id==3){
				actions.add("added new Data ");
				LogUserActions.logUserActions(actions);
				insertData("3");
			}
		
	}
	
	public static ClientProfile insertData(ClientProfile prof, String type){
		String sql = "INSERT INTO clientprofile ("
				+ "clientId,"
				+ "registeredDate,"
				+ "contactNumber,"
				+ "fullName,"
				+ "address,"
				+ "addedBy,"
				+ "picpath,"
				+ "isactiveclient)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		actions.add("inserting data into table clientprofile");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			prof.setClientId(Long.valueOf(id));
			actions.add("clientId: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestClientId()+1;
			ps.setLong(1, id);
			prof.setClientId(Long.valueOf(id));
			actions.add("clientId: " + id);
		}
		
		ps.setString(2, prof.getRegisteredDate());
		actions.add("getRegisteredDate : " + prof.getRegisteredDate());
		ps.setString(3, prof.getContactNumber());
		actions.add("getContactNumber : " + prof.getContactNumber());
		ps.setString(4, prof.getFullName());
		actions.add("getFullName : " + prof.getFullName());
		ps.setString(5, prof.getAddress());
		actions.add("getAddress : " + prof.getAddress());
		ps.setString(6, prof.getAddedBy());
		actions.add("getAddedBy : " + prof.getAddedBy());
		ps.setString(7, prof.getPicturePath());
		actions.add("getPicturePath : " + prof.getPicturePath());
		ps.setInt(8, prof.getIsActive());
		actions.add("executing for saving...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error inserting data to clientprofile : " + s.getMessage());
		}
		
		LogUserActions.logUserActions(actions);
		return prof;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO clientprofile ("
				+ "clientId,"
				+ "registeredDate,"
				+ "contactNumber,"
				+ "fullName,"
				+ "address,"
				+ "addedBy,"
				+ "picpath,"
				+ "isactiveclient)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		actions.add("inserting data into table clientprofile");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setClientId(Long.valueOf(id));
			actions.add("clientId: 1");
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestClientId()+1;
			ps.setLong(1, id);
			setClientId(Long.valueOf(id));
			actions.add("clientId: " + id);
		}
		
		ps.setString(2, getRegisteredDate());
		actions.add("getRegisteredDate : " + getRegisteredDate());
		ps.setString(3, getContactNumber());
		actions.add("getContactNumber : " + getContactNumber());
		ps.setString(4, getFullName());
		actions.add("getFullName : " + getFullName());
		ps.setString(5, getAddress());
		actions.add("getAddress : " + getAddress());
		ps.setString(6, getAddedBy());
		actions.add("getAddedBy : " + getAddedBy());
		ps.setString(7, getPicturePath());
		actions.add("getPicturePath : " + getPicturePath());
		ps.setInt(8, getIsActive());
		actions.add("executing for saving...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error inserting data to clientprofile : " + s.getMessage());
		}
		
		LogUserActions.logUserActions(actions);
		
	}
	
	public static ClientProfile updateData(ClientProfile prof){
		String sql = "UPDATE clientprofile SET "
				+ "contactNumber=?,"
				+ "fullName=?,"
				+ "address=?,"
				+ "addedBy=?,"
				+ "picpath=?"
				+ " WHERE clientId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, prof.getContactNumber());
		actions.add("getContactNumber : " + prof.getContactNumber());
		ps.setString(2, prof.getFullName());
		actions.add("getFullName : " + prof.getFullName());
		ps.setString(3, prof.getAddress());
		actions.add("getAddress : " + prof.getAddress());
		ps.setString(4, prof.getAddedBy());
		actions.add("getAddedBy : " + prof.getAddedBy());
		ps.setString(5, prof.getPicturePath());
		actions.add("getPicturePath : " + prof.getPicturePath());
		ps.setLong(6, prof.getClientId());
		actions.add("getClientId : " + prof.getClientId());
		actions.add("executing for saving...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error inserting data to clientprofile : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return prof;
	}
	
	public void updateData(){
		String sql = "UPDATE clientprofile SET "
				+ "contactNumber=?,"
				+ "fullName=?,"
				+ "address=?,"
				+ "addedBy=?,"
				+ "picpath=?"
				+ " WHERE clientId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, getContactNumber());
		actions.add("getContactNumber : " + getContactNumber());
		ps.setString(2, getFullName());
		actions.add("getFullName : " + getFullName());
		ps.setString(3, getAddress());
		actions.add("getAddress : " + getAddress());
		ps.setString(4, getAddedBy());
		actions.add("getAddedBy : " + getAddedBy());
		ps.setString(5, getPicturePath());
		actions.add("getPicturePath : " + getPicturePath());
		ps.setLong(6, getClientId());
		actions.add("getClientId : " + getClientId());
		actions.add("executing for saving...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error inserting data to clientprofile : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public static long getLatestClientId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT clientId FROM clientprofile  ORDER BY clientId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("clientId");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static Long getClientInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestClientId();	
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
		ps = conn.prepareStatement("SELECT clientId FROM clientprofile WHERE clientId=?");
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
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){}
		
	}
	
	public void delete(){
		String sql = "UPDATE clientprofile set isactiveclient=0 WHERE clientId=" + getClientId();
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.executeUpdate();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){}
		
	}
	
	public Long getClientId() {
		return clientId;
	}
	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}
	public String getRegisteredDate() {
		return registeredDate;
	}
	public void setRegisteredDate(String registeredDate) {
		this.registeredDate = registeredDate;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	
	public static void main(String[] args) {
		
		
		String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1";
		String[] params = new String[0];
		
		for(ClientProfile pr : ClientProfile.retrieveClientProfile(sql, params)){
			System.out.println(pr.getFullName());
		}
		
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getAmntCollectible() {
		return amntCollectible;
	}

	public void setAmntCollectible(String amntCollectible) {
		this.amntCollectible = amntCollectible;
	}

	public int getIsActive() {
		return isActive;
	}

	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	
	
	
	
	
	
	
	
}
