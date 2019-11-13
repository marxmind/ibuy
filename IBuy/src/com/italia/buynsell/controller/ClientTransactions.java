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
import com.italia.buynsell.enm.TransStatus;
import com.italia.buynsell.utils.DateUtils;

public class ClientTransactions {

	private Long transId;
	private String status;
	private String transtype;
	private String clientrate;
	private String description;
	private String transDate;
	private String transamount;
	private String interestrate;
	private String interestamount;
	private String paidamount;
	private String paidDate;
	private String notes;
	private String addedBy;
	private ClientProfile clientProfile;
	private Employee employee;
	private String timeStamp;
	
	private String dueDate;
	
	public ClientTransactions(){}
	
	
	public ClientTransactions(
			 Long transId,
			 String status,
			 String transtype,
			 String clientrate,
			 String description,
			 String transDate,
			 String transamount,
			 String interestrate,
			 String interestamount,
			 String paidamount,
			 String paidDate,
			 String notes,
			 String addedBy,
			 ClientProfile clientProfile,
			 Employee employee
			){
		
		this.transId = transId;
		this.status = status;
		this.transtype = transtype;
		this.clientrate = clientrate;
		this.description = description;
		this.transDate = transDate;
		this.transamount = transamount;
		this.interestrate = interestrate;
		this.interestamount = interestamount;
		this.paidamount = paidamount;
		this.paidDate = paidDate;
		this.notes = notes;
		this.addedBy = addedBy;
		this.clientProfile = clientProfile;
		this.employee = employee;
	}
	
	
	public static ClientTransactions save(ClientTransactions trans){
		if(trans!=null){
			
			long id = ClientTransactions.getTransInfo(trans.getTransId()==null? ClientTransactions.getLatestTransId()+1 : trans.getTransId());
			
			if(id==1){
				trans = ClientTransactions.insertData(trans, "1");
			}else if(id==2){
				trans = ClientTransactions.updateData(trans);
			}else if(id==3){
				trans =ClientTransactions.insertData(trans, "3");
			}
			
		}
		return trans;
	}
	
	public void save(){
		long id = getTransInfo(getTransId()==null? getLatestTransId()+1 : getTransId());
			
			if(id==1){
				insertData("1");
			}else if(id==2){
				updateData();
			}else if(id==3){
				insertData("3");
			}
	}
	
	public static List<ClientTransactions> retrieveClientTransacts(String sql, String[] params){
		List<ClientTransactions> transList = new ArrayList<>();
		
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
		System.out.println("SQL ClientProfileTransactions: "+ ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			ClientTransactions trans = new ClientTransactions();
			
			try{trans.setTransId(rs.getLong("transId"));}catch(Exception e){}
			try{trans.setStatus(rs.getString("status"));}catch(Exception e){}
			try{trans.setTranstype(rs.getString("transtype"));}catch(Exception e){}
			try{trans.setClientrate(rs.getString("clientrate"));}catch(Exception e){}
			try{trans.setDescription(rs.getString("description"));}catch(Exception e){}
			try{trans.setTransDate(rs.getString("transDate"));}catch(Exception e){}
			try{trans.setTransamount(rs.getString("transamount"));}catch(Exception e){}
			try{trans.setInterestrate(rs.getString("interestrate"));}catch(Exception e){}
			try{trans.setInterestamount(rs.getString("interestamount"));}catch(Exception e){}
			try{trans.setPaidamount(rs.getString("paidamount"));}catch(Exception e){}
			try{trans.setPaidDate(rs.getString("paidDate"));}catch(Exception e){}
			try{trans.setNotes(rs.getString("notes"));}catch(Exception e){}
			try{trans.setAddedBy(rs.getString("addedBy"));}catch(Exception e){}
			try{ClientProfile clientProfile = new ClientProfile();
			clientProfile.setClientId(rs.getLong("clientId"));
			trans.setClientProfile(clientProfile);}catch(Exception e){}
			try{Employee employee = new Employee();
			employee.setEmpId(rs.getLong("empId"));
			trans.setEmployee(employee);}catch(Exception e){}
			try{trans.setDueDate(rs.getString("duedate"));}catch(Exception e){}
			try{trans.setTimeStamp(rs.getString("timestamp").replace(".0", ""));}catch(Exception e){}
			transList.add(trans);
			
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return transList;
	}
	
	
	public static ClientTransactions insertData(ClientTransactions trans, String type){
		String sql = "INSERT INTO client_trans ("
				+ "transId,"
				+ "status,"
				+ "transtype,"
				+ "clientrate,"
				+ "description,"
				+ "transDate,"
				+ "transamount,"
				+ "interestrate,"
				+ "interestamount,"
				+ "paidamount,"
				+ "paidDate,"
				+ "notes,"
				+ "addedBy,"
				+ "clientId,"
				+ "empId,"
				+ "duedate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			trans.setTransId(Long.valueOf(id));
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestTransId()+1;
			ps.setLong(1, id);
			trans.setTransId(Long.valueOf(id));
		}
		
		ps.setInt(2, Integer.valueOf(trans.getStatus()));
		ps.setInt(3, Integer.valueOf(trans.getTranstype()));
		ps.setInt(4, Integer.valueOf(trans.getClientrate()));
		ps.setString(5, trans.getDescription());
		ps.setString(6, trans.getTransDate());
		ps.setString(7, trans.getTransamount());
		ps.setString(8, trans.getInterestrate());
		ps.setString(9, trans.getInterestamount());
		ps.setString(10, trans.getPaidamount());
		ps.setString(11, trans.getPaidDate());
		ps.setString(12, trans.getNotes());
		ps.setString(13, trans.getAddedBy());
		ps.setLong(14, trans.getClientProfile()==null? 0 : trans.getClientProfile().getClientId()==null? 0 : trans.getClientProfile().getClientId());
		ps.setLong(15, trans.getEmployee()==null? 0 : trans.getEmployee().getEmpId()==null? 0 : trans.getEmployee().getEmpId());
		ps.setString(16, trans.getDueDate());
		
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
		return trans;
	}
	
	
	
	public void insertData(String type){
		String sql = "INSERT INTO client_trans ("
				+ "transId,"
				+ "status,"
				+ "transtype,"
				+ "clientrate,"
				+ "description,"
				+ "transDate,"
				+ "transamount,"
				+ "interestrate,"
				+ "interestamount,"
				+ "paidamount,"
				+ "paidDate,"
				+ "notes,"
				+ "addedBy,"
				+ "clientId,"
				+ "empId,"
				+ "duedate)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setTransId(Long.valueOf(id));
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestTransId()+1;
			ps.setLong(1, id);
			setTransId(Long.valueOf(id));
		}
		
		ps.setInt(2, Integer.valueOf(getStatus()));
		ps.setInt(3, Integer.valueOf(getTranstype()));
		ps.setInt(4, Integer.valueOf(getClientrate()));
		ps.setString(5, getDescription());
		ps.setString(6, getTransDate());
		ps.setString(7, getTransamount());
		ps.setString(8, getInterestrate());
		ps.setString(9, getInterestamount());
		ps.setString(10, getPaidamount());
		ps.setString(11, getPaidDate());
		ps.setString(12, getNotes());
		ps.setString(13, getAddedBy());
		ps.setLong(14, getClientProfile()==null? 0 : getClientProfile().getClientId()==null? 0 : getClientProfile().getClientId());
		ps.setLong(15, getEmployee()==null? 0 : getEmployee().getEmpId()==null? 0 : getEmployee().getEmpId());
		ps.setString(16, getDueDate());
		
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
	}
	
	public static ClientTransactions updateData(ClientTransactions trans){
		String sql = "UPDATE client_trans SET "
				+ "status=?,"
				+ "transtype=?,"
				+ "clientrate=?,"
				+ "description=?,"
				+ "transDate=?,"
				+ "transamount=?,"
				+ "interestrate=?,"
				+ "interestamount=?,"
				+ "paidamount=?,"
				+ "paidDate=?,"
				+ "notes=?,"
				+ "addedBy=?,"
				+ "duedate=?"
				+ " WHERE transId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setInt(1, Integer.valueOf(trans.getStatus()));
		ps.setInt(2, Integer.valueOf(trans.getTranstype()));
		ps.setInt(3, Integer.valueOf(trans.getClientrate()));
		ps.setString(4, trans.getDescription());
		ps.setString(5, trans.getTransDate());
		ps.setString(6, trans.getTransamount());
		ps.setString(7, trans.getInterestrate());
		ps.setString(8, trans.getInterestamount());
		ps.setString(9, trans.getPaidamount());
		ps.setString(10, trans.getPaidDate());
		ps.setString(11, trans.getNotes());
		ps.setString(12, trans.getAddedBy());
		ps.setString(13, trans.getDueDate());
		ps.setLong(14, trans.getTransId());
		
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
		return trans;
	}
	
	public void updateData(){
		String sql = "UPDATE client_trans SET "
				+ "status=?,"
				+ "transtype=?,"
				+ "clientrate=?,"
				+ "description=?,"
				+ "transDate=?,"
				+ "transamount=?,"
				+ "interestrate=?,"
				+ "interestamount=?,"
				+ "paidamount=?,"
				+ "paidDate=?,"
				+ "notes=?,"
				+ "addedBy=?,"
				+ "duedate=?"
				+ " WHERE transId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setInt(1, Integer.valueOf(getStatus()));
		ps.setInt(2, Integer.valueOf(getTranstype()));
		ps.setInt(3, Integer.valueOf(getClientrate()));
		ps.setString(4, getDescription());
		ps.setString(5, getTransDate());
		ps.setString(6, getTransamount());
		ps.setString(7, getInterestrate());
		ps.setString(8, getInterestamount());
		ps.setString(9, getPaidamount());
		ps.setString(10, getPaidDate());
		ps.setString(11, getNotes());
		ps.setString(12, getAddedBy());
		ps.setString(13, getDueDate());
		ps.setLong(14, getTransId());
		
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public static long getLatestTransId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT transId FROM client_trans  ORDER BY transId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("transId");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static Long getTransInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestTransId();	
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
		ps = conn.prepareStatement("SELECT transId FROM client_trans WHERE transId=?");
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
	
	public Long getTransId() {
		return transId;
	}
	public void setTransId(Long transId) {
		this.transId = transId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTranstype() {
		return transtype;
	}
	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}
	public String getClientrate() {
		return clientrate;
	}
	public void setClientrate(String clientrate) {
		this.clientrate = clientrate;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getTransamount() {
		return transamount;
	}
	public void setTransamount(String transamount) {
		this.transamount = transamount;
	}
	public String getInterestrate() {
		return interestrate;
	}
	public void setInterestrate(String interestrate) {
		this.interestrate = interestrate;
	}
	public String getInterestamount() {
		return interestamount;
	}
	public void setInterestamount(String interestamount) {
		this.interestamount = interestamount;
	}
	public String getPaidamount() {
		return paidamount;
	}
	public void setPaidamount(String paidamount) {
		this.paidamount = paidamount;
	}
	public String getPaidDate() {
		return paidDate;
	}
	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public ClientProfile getClientProfile() {
		return clientProfile;
	}
	public void setClientProfile(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public String getDueDate() {
		return dueDate;
	}


	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}


	public String getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}


	public static void main(String[] args) {
		
		/*ClientTransactions t = new ClientTransactions();
		t.setTransId(2l);
		t.setStatus("1");
		t.setTranstype("1");
		t.setClientrate("1");
		t.setDescription("first");
		t.setTransDate(DateUtils.getCurrentDateMMDDYYYY());
		t.setTransamount("100");
		t.setInterestrate("0.10");
		t.setInterestamount("50");
		t.setPaidamount("150");
		t.setPaidDate(DateUtils.getCurrentDateMMDDYYYY());
		t.setNotes("Notes");
		t.setAddedBy("markus 23");
		ClientProfile c = new ClientProfile();
		c.setClientId(0l);
		t.setClientProfile(c);
		Employee e = new Employee();
		e.setEmpId(0l);
		t.setEmployee(e);
		t.save();*/
		
		
		System.out.println(TransStatus.statusCodeToMeaning("clientrate", "1"));
		System.out.println(TransStatus.statusNameToCode("status", "PAID"));
		
	}
	
	
	
}
