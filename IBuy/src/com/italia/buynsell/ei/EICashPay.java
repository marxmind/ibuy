package com.italia.buynsell.ei;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.LogUserActions;

public class EICashPay {

	private long idPay;
	private String description;
	private String amountPay;
	private String datePay;
	private String status;
	private String addedBy;
	private String timestamp;
	
	public EICashPay(){}
	
	public EICashPay(
			Long idPay,
			String description,
			String amountPay,
			String datePay,
			String status,
			String addedBy,
			String timestamp
			){
		this.idPay = idPay;
		this.description = description;
		this.amountPay = amountPay;
		this.datePay = datePay;
		this.status = status;
		this.addedBy = addedBy;
		this.timestamp = timestamp;
	}
	
	public static void save(EICashPay cashPay){
		if(cashPay!=null){
			List<String> actions = new ArrayList<>();
			actions.add("checking data to be save.");
			long id = EICashPay.getEIPayInfo(cashPay.getIdPay()==null? EICashPay.getLatestPayId()+1 : cashPay.getIdPay());
			
			if(id==1){
				actions.add("insert data");
				LogUserActions.logUserActions(actions);
				EICashPay.insertData(cashPay, "1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				EICashPay.updateData(cashPay);
			}else if(id==3){
				actions.add("added new data");
				LogUserActions.logUserActions(actions);
				EICashPay.insertData(cashPay, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getEIPayInfo(getIdPay()==null? EICashPay.getLatestPayId()+1 : getIdPay());
			List<String> actions = new ArrayList<>();
			actions.add("checking data to be save.");
			if(id==1){
				actions.add("insert data");
				LogUserActions.logUserActions(actions);
				insertData("1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				updateData();
			}else if(id==3){
				actions.add("added new data");
				LogUserActions.logUserActions(actions);
				insertData("3");
			}
	}
	
	public static List<EICashPay> retrieveCashPay(String sql, String[] params){
		List<EICashPay> cashList = new ArrayList<>();
		
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
			EICashPay cash = new EICashPay();
			
			cash.setIdPay(rs.getLong("idPay"));
			cash.setDescription(rs.getString("description"));
			cash.setAmountPay(rs.getString("amountPay"));
			cash.setDatePay(rs.getString("datePay"));
			cash.setAddedBy(rs.getString("addedBy"));
			cash.setStatus(rs.getString("status"));
			cashList.add(cash);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return cashList;
	}
	
	public static EICashPay insertData(EICashPay  cashPay, String type){
		String sql = "INSERT INTO ei_cash_payment ("
				+ "idPay,"
				+ "description,"
				+ "amountPay,"
				+ "addedBy,"
				+ "datePay,"
				+ "status)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_payment table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			cashPay.setIdPay(Long.valueOf(id));
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestPayId()+1;
			ps.setLong(1, id);
			cashPay.setIdPay(Long.valueOf(id));
			actions.add("id : " + id);
		}
		
		ps.setString(2, cashPay.getDescription());
		ps.setString(3, cashPay.getAmountPay());
		ps.setString(4, cashPay.getAddedBy());
		ps.setString(5, cashPay.getDatePay());
		ps.setInt(6, cashPay.getStatus()==null? 0 : cashPay.getStatus().equalsIgnoreCase("")? 0 : cashPay.getStatus().equalsIgnoreCase("PAID")? 2 : 0);
		
		actions.add("Description : " + cashPay.getDescription());
		actions.add("AmountPay : " + cashPay.getAmountPay());
		actions.add("AddedBy : " + cashPay.getAddedBy());
		actions.add("DatePay : " + cashPay.getDatePay());
		actions.add("status : " + cashPay.getStatus()==null? "0" : cashPay.getStatus().equalsIgnoreCase("")? "0" : cashPay.getStatus().equalsIgnoreCase("PAID")? "2" : "0");
		actions.add("Executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully saved.");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
		return cashPay;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO ei_cash_payment ("
				+ "idPay,"
				+ "description,"
				+ "amountPay,"
				+ "addedBy,"
				+ "datePay,"
				+ "status)" 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_payment table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setIdPay(Long.valueOf(id));
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestPayId()+1;
			ps.setLong(1, id);
			setIdPay(Long.valueOf(id));
			actions.add("id : " + id);
		}
		
		ps.setString(2, getDescription());
		ps.setString(3, getAmountPay());
		ps.setString(4, getAddedBy());
		ps.setString(5, getDatePay());
		ps.setInt(6, getStatus()==null? 0 : getStatus().equalsIgnoreCase("")? 0 : getStatus().equalsIgnoreCase("PAID")? 2 : 0);
		
		actions.add("Description : " + getDescription());
		actions.add("AmountPay : " + getAmountPay());
		actions.add("AddedBy : " + getAddedBy());
		actions.add("DatePay : " + getDatePay());
		actions.add("status : " + getStatus()==null? "0" : getStatus().equalsIgnoreCase("")? "0" : getStatus().equalsIgnoreCase("PAID")? "2" : "0");
		actions.add("Executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully saved.");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
	}
	
	public static EICashPay updateData(EICashPay cashPay){
		String sql = "UPDATE ei_cash_payment SET "
				+ "description=?,"
				+ "amountPay=?,"
				+ "addedBy=?,"
				+ "datePay=?,"
				+ "status=?"
				+ " WHERE idPay=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data on ei_cash_payment table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, cashPay.getDescription());
		ps.setString(2, cashPay.getAmountPay());
		ps.setString(3, cashPay.getAddedBy());
		ps.setString(4, cashPay.getDatePay());
		ps.setString(5, cashPay.getStatus());
		ps.setLong(6, cashPay.getIdPay());
		
		actions.add("Description : " + cashPay.getDescription());
		actions.add("AmountPay : " + cashPay.getAmountPay());
		actions.add("AddedBy : " + cashPay.getAddedBy());
		actions.add("DatePay : " + cashPay.getDatePay());
		actions.add("status : " + cashPay.getStatus()==null? "0" : cashPay.getStatus().equalsIgnoreCase("")? "0" : cashPay.getStatus().equalsIgnoreCase("PAID")? "2" : "0");
		actions.add("IdPay : " + cashPay.getIdPay());
		actions.add("Executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in update : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return cashPay;
	}
	
	public void updateData(){
		String sql = "UPDATE ei_cash_payment SET "
				+ "description=?,"
				+ "amountPay=?,"
				+ "addedBy=?,"
				+ "datePay=?,"
				+ "status=?"
				+ " WHERE idPay=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data on ei_cash_payment table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, getDescription());
		ps.setString(2, getAmountPay());
		ps.setString(3, getAddedBy());
		ps.setString(4, getDatePay());
		ps.setString(5, getStatus());
		ps.setLong(6, getIdPay());
		
		actions.add("Description : " + getDescription());
		actions.add("AmountPay : " + getAmountPay());
		actions.add("AddedBy : " + getAddedBy());
		actions.add("DatePay : " + getDatePay());
		actions.add("status : " + getStatus()==null? "0" : getStatus().equalsIgnoreCase("")? "0" : getStatus().equalsIgnoreCase("PAID")? "2" : "0");
		actions.add("IdPay : " + getIdPay());
		actions.add("Executing...");
		
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in update : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
	}
	
	public static EICashPay deleteData(EICashPay cashPay){
		String sql = "UPDATE ei_cash_payment SET "
				+ "status=?"
				+ " WHERE idPay=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("deletion of data on ei_cash_payment table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, "9");
		ps.setLong(2, cashPay.getIdPay());
		
		actions.add("status : 9");
		actions.add("id : " + cashPay.getIdPay());
		actions.add("executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully removed.");
		}catch(SQLException s){
			actions.add("Error in deletion : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return cashPay;
	}
	
	public static long getLatestPayId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT idPay FROM ei_cash_payment  ORDER BY idPay DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("idPay");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getEIPayInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestPayId();	
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
		ps = conn.prepareStatement("SELECT idPay FROM ei_cash_payment WHERE idPay=?");
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
	
	public Long getIdPay() {
		return idPay;
	}
	public void setIdPay(Long idPay) {
		this.idPay = idPay;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmountPay() {
		return amountPay;
	}
	public void setAmountPay(String amountPay) {
		this.amountPay = amountPay;
	}
	public String getDatePay() {
		return datePay;
	}
	public void setDatePay(String datePay) {
		this.datePay = datePay;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	public static void main(String[] args) {
		
		EICashPay out = new EICashPay();
		//out.setIdPay(2L);
		out.setDescription("update testing");
		out.setAmountPay("300");
		out.setDatePay("08-19-2016");
		out.setStatus("2");
		out.setAddedBy("mark");
		out.save();
		
		/*String sql = "SELECT * FROM ei_cash_payment";
		String params[] = new String[0];
		//params[0] = "1";
		for(EICashPay o : out.retrieveCashPay(sql, params)){
			System.out.println(o.getDescription() + "===");
		}*/
		
	}
	
}
