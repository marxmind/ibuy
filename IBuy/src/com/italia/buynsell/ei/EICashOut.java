package com.italia.buynsell.ei;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.LogUserActions;

public class EICashOut {

	private Long idOut;
	private String description;
	private String amountOut;
	private String dateOut;
	private String status;
	private String addedBy;
	private String timestamp;
	private EICashPay eiCashPay;
	
	public EICashOut(){}
	
	public EICashOut(
			long idOut,
			String description,
			String amountOut,
			String dateOut,
			String status,
			String addedBy,
			String timestamp,
			EICashPay eiCashPay
			){
		this.idOut = idOut;
		this.description = description;
		this.amountOut = amountOut;
		this.dateOut = dateOut;
		this.status = status;
		this.addedBy = addedBy;
		this.timestamp = timestamp;
		this.eiCashPay = eiCashPay;
	}
	
	public static void save(EICashOut eiCashOut){
		if(eiCashOut!=null){
			List<String> actions = new ArrayList<>();
			actions.add("checking data to be save.");
			long id = EICashOut.getEIOutInfo(eiCashOut.getIdOut()==null? EICashOut.getLatestOutId()+1 : eiCashOut.getIdOut());
			
			if(id==1){
				actions.add("insert data");
				LogUserActions.logUserActions(actions);
				EICashOut.insertData(eiCashOut, "1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				EICashOut.updateData(eiCashOut);
			}else if(id==3){
				actions.add("added new data");
				LogUserActions.logUserActions(actions);
				EICashOut.insertData(eiCashOut, "3");
			}
			
		}
	}
	
	public void save(){
			long id = getEIOutInfo(getIdOut()==null? EICashOut.getLatestOutId()+1 : getIdOut());
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
	
	public static List<EICashOut> retrieveCashOut(String sql, String[] params){
		List<EICashOut> cashList = new ArrayList<>();
		
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
			EICashOut cash = new EICashOut();
			
			cash.setIdOut(rs.getLong("idOut"));
			cash.setDescription(rs.getString("description"));
			cash.setAmountOut(rs.getString("amountOut"));
			cash.setDateOut(rs.getString("dateOut"));
			cash.setAddedBy(rs.getString("addedBy"));
			cash.setStatus(rs.getString("status"));
			EICashPay eiCashPay = new EICashPay();
			eiCashPay.setIdPay(rs.getLong("idPay"));
			cash.setEiCashPay(eiCashPay);
			cashList.add(cash);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return cashList;
	}
	
	public static EICashOut insertData(EICashOut eOut, String type){
		String sql = "INSERT INTO ei_cash_out ("
				+ "idOut,"
				+ "description,"
				+ "amountOut,"
				+ "addedBy,"
				+ "dateOut,"
				+ "status,"
				+ "idPay)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_out table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			eOut.setIdOut(Long.valueOf(id));
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestOutId()+1;
			ps.setLong(1, id);
			eOut.setIdOut(Long.valueOf(id));
			actions.add("id : " + id);
		}
		
		ps.setString(2, eOut.getDescription());
		ps.setString(3, eOut.getAmountOut());
		ps.setString(4, eOut.getAddedBy());
		ps.setString(5, eOut.getDateOut());
		ps.setInt(6, eOut.getStatus()==null? 1 : eOut.getStatus().equalsIgnoreCase("")? 1 : 1);
		ps.setLong(7, eOut.eiCashPay==null? 0 : eOut.eiCashPay.getIdPay());
		
		actions.add("Description : " + eOut.getDescription());
		actions.add("AmountOut : " + eOut.getAmountOut());
		actions.add("AddedBy : " + eOut.getAddedBy());
		actions.add("DateOut : " + eOut.getDateOut());
		actions.add("Status : " + eOut.getStatus()==null? "1" : eOut.getStatus().equalsIgnoreCase("")? "1" : "1");
		if(eOut.eiCashPay==null){
			actions.add("IdPay : null");
		}else{	
			actions.add("IdPay : " + eOut.eiCashPay==null? "0" : eOut.eiCashPay.getIdPay()+"");
		}
		actions.add("executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully saved.");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return eOut;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO ei_cash_out ("
				+ "idOut,"
				+ "description,"
				+ "amountOut,"
				+ "addedBy,"
				+ "dateOut,"
				+ "status,"
				+ "idPay)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on ei_cash_out table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setIdOut(Long.valueOf(id));
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestOutId()+1;
			ps.setLong(1, id);
			setIdOut(Long.valueOf(id));
			actions.add("id : " + id);
		}
		
		ps.setString(2, getDescription());
		ps.setString(3, getAmountOut());
		ps.setString(4, getAddedBy());
		ps.setString(5, getDateOut());
		ps.setInt(6, getStatus()==null? 1 : getStatus().equalsIgnoreCase("")? 1 : 1);
		ps.setLong(7, eiCashPay==null? 0 : eiCashPay.getIdPay());
		
		actions.add("Description : " + getDescription());
		actions.add("AmountOut : " + getAmountOut());
		actions.add("AddedBy : " + getAddedBy());
		actions.add("DateOut : " + getDateOut());
		actions.add("Status : " + getStatus()==null? "1" : getStatus().equalsIgnoreCase("")? "1" : "1");
		if(eiCashPay==null){
			actions.add("IdPay : null");
		}else{
			actions.add("IdPay : " + eiCashPay==null? "0" : eiCashPay.getIdPay()==null? "0" : eiCashPay.getIdPay()+"");
		}
		actions.add("executing...");
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
	
	public static EICashOut updateData(EICashOut eOut){
		String sql = "UPDATE ei_cash_out SET "
				+ "description=?,"
				+ "amountOut=?,"
				+ "addedBy=?,"
				+ "dateOut=?,"
				+ "status=?,"
				+ "idPay=?"
				+ " WHERE idOut=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data on ei_cash_out table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, eOut.getDescription());
		ps.setString(2, eOut.getAmountOut());
		ps.setString(3, eOut.getAddedBy());
		ps.setString(4, eOut.getDateOut());
		ps.setString(5, eOut.getStatus());
		ps.setLong(6, eOut.getEiCashPay()==null? 0 : eOut.getEiCashPay().getIdPay()==null? 0 : eOut.getEiCashPay().getIdPay());
		ps.setLong(7, eOut.getIdOut());
		
		actions.add("Description : " + eOut.getDescription());
		actions.add("AmountOut : " + eOut.getAmountOut());
		actions.add("AddedBy : " + eOut.getAddedBy());
		actions.add("DateOut : " + eOut.getDateOut());
		actions.add("Status : " + eOut.getStatus()==null? "1" : eOut.getStatus().equalsIgnoreCase("")? "1" : "1");
		actions.add("IdPay : " + eOut.eiCashPay==null? "0" : eOut.eiCashPay.getIdPay()+"");
		actions.add("id : " + eOut.getIdOut());
		actions.add("executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
		return eOut;
	}
	
	public void updateData(){
		String sql = "UPDATE ei_cash_out SET "
				+ "description=?,"
				+ "amountOut=?,"
				+ "addedBy=?,"
				+ "dateOut=?,"
				+ "status=?,"
				+ "idPay=?"
				+ " WHERE idOut=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data on ei_cash_out table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, getDescription());
		ps.setString(2, getAmountOut());
		ps.setString(3, getAddedBy());
		ps.setString(4, getDateOut());
		ps.setString(5, getStatus());
		ps.setLong(6, getEiCashPay()==null? 0 : getEiCashPay().getIdPay()==null? 0 : getEiCashPay().getIdPay());
		ps.setLong(7, getIdOut());
		
		actions.add("Description : " + getDescription());
		actions.add("AmountOut : " + getAmountOut());
		actions.add("AddedBy : " + getAddedBy());
		actions.add("DateOut : " + getDateOut());
		actions.add("Status : " + getStatus()==null? "1" : getStatus().equalsIgnoreCase("")? "1" : "1");
		actions.add("IdPay : " + eiCashPay==null? "0" : eiCashPay.getIdPay()+"");
		actions.add("id : " + getIdOut());
		
		actions.add("executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
	}
	
	public static EICashOut deleteData(EICashOut eOut){
		String sql = "UPDATE ei_cash_out SET "
				+ "status=?"
				+ " WHERE idOut=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("deletion of data on ei_cash_out table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, "9");
		ps.setLong(2, eOut.getIdOut());
		
		actions.add("Status : 9");
		actions.add("id : " + eOut.getIdOut());
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
		return eOut;
	}
	
	public static long getLatestOutId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT idOut FROM ei_cash_out  ORDER BY idOut DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("idOut");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getEIOutInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestOutId();	
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
		ps = conn.prepareStatement("SELECT idOut FROM ei_cash_out WHERE idOut=?");
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
	
	public Long getIdOut() {
		return idOut;
	}
	public void setIdOut(Long idOut) {
		this.idOut = idOut;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmountOut() {
		return amountOut;
	}
	public void setAmountOut(String amountOut) {
		this.amountOut = amountOut;
	}
	public String getDateOut() {
		return dateOut;
	}
	public void setDateOut(String dateOut) {
		this.dateOut = dateOut;
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
	public EICashPay getEiCashPay() {
		return eiCashPay;
	}
	public void setEiCashPay(EICashPay eiCashPay) {
		this.eiCashPay = eiCashPay;
	}
	
	public static void main(String[] args) {
		
		EICashOut out = new EICashOut();
		/*out.setIdOut(3L);
		out.setDescription("update testing");
		out.setAmountOut("600");
		out.setDateOut("08-18-2016");
		out.setStatus("1");
		out.setAddedBy("mark");
		out.save();*/
		String sql = "SELECT * FROM ei_cash_out";
		String params[] = new String[0];
		//params[0] = "1";
		for(EICashOut o : out.retrieveCashOut(sql, params)){
			System.out.println(o.getDescription());
		}
		
	}
	
}
