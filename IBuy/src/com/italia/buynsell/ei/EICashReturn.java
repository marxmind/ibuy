package com.italia.buynsell.ei;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.LogUserActions;

public class EICashReturn {

	private Long idRet;
	private String description;
	private String amountReturn;
	private String dateReturn;
	private String status;
	private String addedBy;
	private String timestamp;
	private EICashPay cashPay;
	
	public EICashReturn(){}
	
	public EICashReturn(
			long idRet,
			String description,
			String amountReturn,
			String dateReturn,
			String status,
			String addedBy,
			String timestamp
			){
		this.idRet = idRet;
		this.description = description;
		this.amountReturn = amountReturn;
		this.dateReturn = dateReturn;
		this.status = status;
		this.addedBy = addedBy;
		this.timestamp = timestamp;
	}
	
	public static void save(EICashReturn cashReturn){
		if(cashReturn!=null){
			List<String> actions = new ArrayList<>();
			actions.add("checking data to be save.");
			long id = EICashReturn.getEIRetInfo(cashReturn.getIdRet()==null? EICashReturn.getLatestRetId()+1 : cashReturn.getIdRet());
			
			if(id==1){
				actions.add("insert data");
				LogUserActions.logUserActions(actions);
				EICashReturn.insertData(cashReturn, "1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				EICashReturn.updateData(cashReturn);
			}else if(id==3){
				actions.add("added new data");
				LogUserActions.logUserActions(actions);
				EICashReturn.insertData(cashReturn, "3");
			}
			
		}
	}
	
	public void save(){
			
			long id = getEIRetInfo(getIdRet()==null? getLatestRetId()+1 : getIdRet());
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
	
	public static List<EICashReturn> retrieveCashReturn(String sql, String[] params){
		List<EICashReturn> cashList = new ArrayList<>();
		
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
			EICashReturn cash = new EICashReturn();
			
			cash.setIdRet(rs.getLong("idRet"));
			cash.setDescription(rs.getString("description"));
			cash.setAmountReturn(rs.getString("amountReturn"));
			cash.setDateReturn(rs.getString("dateReturn"));
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
	
	public static EICashReturn insertData(EICashReturn  cashReturn, String type){
		String sql = "INSERT INTO ei_cash_return ("
				+ "idRet,"
				+ "description,"
				+ "amountReturn,"
				+ "addedBy,"
				+ "dateReturn,"
				+ "status)" 
				+ "idPay)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_return table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			cashReturn.setIdRet(Long.valueOf(id));
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestRetId()+1;
			ps.setLong(1, id);
			cashReturn.setIdRet(Long.valueOf(id));
			actions.add("id : " + id);
		}
		
		ps.setString(2, cashReturn.getDescription());
		ps.setString(3, cashReturn.getAmountReturn());
		ps.setString(4, cashReturn.getAddedBy());
		ps.setString(5, cashReturn.getDateReturn());
		ps.setInt(6, cashReturn.getStatus()==null? 1 : cashReturn.getStatus().equalsIgnoreCase("")? 3 : 3);
		ps.setLong(7, cashReturn.getCashPay()==null? 0 : cashReturn.getCashPay().getIdPay()==null? 0 : cashReturn.getCashPay().getIdPay());
		
		
		actions.add("Description : " + cashReturn.getDescription());
		actions.add("AmountPay : " + cashReturn.getAmountReturn());
		actions.add("AddedBy : " + cashReturn.getAddedBy());
		actions.add("DatePay : " + cashReturn.getDateReturn());
		actions.add("status : " + cashReturn.getStatus()==null? "1" : cashReturn.getStatus().equalsIgnoreCase("")? "3" : "3");
		if(cashReturn.getCashPay()==null){
			actions.add("IdPay : null");
		}else{
			actions.add("IdPay : " + cashReturn.getCashPay()==null? "0" : cashReturn.getCashPay().getIdPay()==null? "0" : cashReturn.getCashPay().getIdPay()+"");
		}	
		actions.add("Executing...");
		ps.execute();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully saved.");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return cashReturn;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO ei_cash_return ("
				+ "idRet,"
				+ "description,"
				+ "amountReturn,"
				+ "addedBy,"
				+ "dateReturn,"
				+ "status,"
				+ "idPay)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_return table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setIdRet(Long.valueOf(id));
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestRetId()+1;
			ps.setLong(1, id);
			setIdRet(Long.valueOf(id));
			actions.add("id : " + id);
		}
		
		ps.setString(2, getDescription());
		ps.setString(3, getAmountReturn());
		ps.setString(4, getAddedBy());
		ps.setString(5, getDateReturn());
		ps.setInt(6, getStatus()==null? 1 : getStatus().equalsIgnoreCase("")? 3 : 3);
		ps.setLong(7, getCashPay()==null? 0 : getCashPay().getIdPay()==null? 0 : getCashPay().getIdPay());
		
		actions.add("Description : " + getDescription());
		actions.add("AmountPay : " + getAmountReturn());
		actions.add("AddedBy : " + getAddedBy());
		actions.add("DatePay : " + getDateReturn());
		actions.add("status : " + getStatus()==null? "1" : getStatus().equalsIgnoreCase("")? "3" : "3");
		if(getCashPay()==null){
			actions.add("IdPay : null");
		}else{
			actions.add("IdPay : " + getCashPay()==null? "0" : getCashPay().getIdPay()==null? "0" : getCashPay().getIdPay()+"");
		}	
		actions.add("Executing...");
		ps.execute();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully saved.");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
	}
	
	public static EICashReturn updateData(EICashReturn cashReturn){
		String sql = "UPDATE ei_cash_return SET "
				+ "description=?,"
				+ "amountReturn=?,"
				+ "addedBy=?,"
				+ "dateReturn=?,"
				+ "status=?,"
				+ "idPay=?"
				+ " WHERE idRet=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_return table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, cashReturn.getDescription());
		ps.setString(2, cashReturn.getAmountReturn());
		ps.setString(3, cashReturn.getAddedBy());
		ps.setString(4, cashReturn.getDateReturn());
		ps.setString(5, cashReturn.getStatus());
		ps.setLong(6, cashReturn.getCashPay()==null? 0 : cashReturn.getCashPay().getIdPay()==null? 0 : cashReturn.getCashPay().getIdPay());
		ps.setLong(7, cashReturn.getIdRet());

		actions.add("Description : " + cashReturn.getDescription());
		actions.add("AmountPay : " + cashReturn.getAmountReturn());
		actions.add("AddedBy : " + cashReturn.getAddedBy());
		actions.add("DatePay : " + cashReturn.getDateReturn());
		actions.add("status : " + cashReturn.getStatus());
		if(cashReturn.getCashPay()==null){
			actions.add("IdPay : null");
		}else{	
			actions.add("IdPay : " + cashReturn.getCashPay()==null? "0" : cashReturn.getCashPay().getIdPay()==null? "0" : cashReturn.getCashPay().getIdPay()+"");
		}
		actions.add("retId : " + cashReturn.getIdRet());
		actions.add("Executing...");
		ps.execute();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in update : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return cashReturn;
	}
	
	public void updateData(){
		String sql = "UPDATE ei_cash_return SET "
				+ "description=?,"
				+ "amountReturn=?,"
				+ "addedBy=?,"
				+ "dateReturn=?,"
				+ "status=?,"
				+ "idPay=?"
				+ " WHERE idRet=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_return table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, getDescription());
		ps.setString(2, getAmountReturn());
		ps.setString(3, getAddedBy());
		ps.setString(4, getDateReturn());
		ps.setString(5, getStatus());
		ps.setLong(6, getCashPay()==null? 0 : getCashPay().getIdPay()==null? 0 : getCashPay().getIdPay());
		ps.setLong(7, getIdRet());
		
		actions.add("Description : " + getDescription());
		actions.add("AmountPay : " + getAmountReturn());
		actions.add("AddedBy : " + getAddedBy());
		actions.add("DatePay : " + getDateReturn());
		actions.add("status : " + getStatus());
		if(getCashPay()==null){
			actions.add("IdPay : null");
		}else{
			actions.add("IdPay : " + getCashPay()==null? "0" : getCashPay().getIdPay()==null? "0" : getCashPay().getIdPay()+"");
		}	
		actions.add("retId : " + getIdRet());
		actions.add("Executing...");
		
		ps.execute();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in update : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
	}
	
	public static EICashReturn deleteData(EICashReturn cashReturn){
		String sql = "UPDATE ei_cash_return SET "
				+ "status=?"
				+ " WHERE idRet=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("deletion data on ei_cash_return table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, "9");
		ps.setLong(2, cashReturn.getIdRet());
		
		actions.add("status : 9");
		actions.add("id : " + cashReturn.getIdRet());
		actions.add("Executing...");
		ps.execute();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully removed.");
		}catch(SQLException s){
			actions.add("Error in deletion : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return cashReturn;
	}
	
	public static long getLatestRetId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT idRet FROM ei_cash_return  ORDER BY idRet DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("idRet");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getEIRetInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestRetId();	
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
		ps = conn.prepareStatement("SELECT idRet FROM ei_cash_return WHERE idRet=?");
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
	
	public Long getIdRet() {
		return idRet;
	}
	public void setIdRet(Long idRet) {
		this.idRet = idRet;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmountReturn() {
		return amountReturn;
	}
	public void setAmountReturn(String amountReturn) {
		this.amountReturn = amountReturn;
	}
	public String getDateReturn() {
		return dateReturn;
	}
	public void setDateReturn(String dateReturn) {
		this.dateReturn = dateReturn;
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

public EICashPay getCashPay() {
		return cashPay;
	}

	public void setCashPay(EICashPay cashPay) {
		this.cashPay = cashPay;
	}

public static void main(String[] args) {
		
		EICashReturn out = new EICashReturn();
		/*out.setIdRet(2L);
		out.setDescription("update testing");
		out.setAmountReturn("200");
		out.setDateReturn("08-18-2016");
		out.setStatus("1");
		out.setAddedBy("mark");
		out.save();*/
		
		String sql = "SELECT * FROM ei_cash_return";
		String params[] = new String[0];
		//params[0] = "1";
		for(EICashReturn o : out.retrieveCashReturn(sql, params)){
			System.out.println(o.getDescription() + "===");
		}
		
	}
	
}
