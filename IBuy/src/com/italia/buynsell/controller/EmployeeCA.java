package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.LogUserActions;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 09/19/2018
 *
 */
public class EmployeeCA {

	private long id;
	private String cashDate;
	private double amount;
	private double payable;
	private int isActive;
	private Employees employees;
	
	private int index;
	
public static List<EmployeeCA> retrieve(String sqlAdd, String[] params){
		
		List<EmployeeCA> ems = new ArrayList<EmployeeCA>();
		
		String tableCA = "ca";
		String tableEm = "em";
		String sql = "SELECT * FROM employeeca "+ tableCA +", employee "+ tableEm +" WHERE  " 
		+ tableCA + ".caisactive=1 AND " +
		tableCA +".emid=" + tableEm +".emid ";
		sql += sqlAdd;
		
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
			
			EmployeeCA ca = new EmployeeCA();
			try{ca.setId(rs.getLong("caid"));}catch(NullPointerException e) {}
			try{ca.setCashDate(rs.getString("cadate"));}catch(NullPointerException e) {}
			try{ca.setAmount(rs.getDouble("caamount"));}catch(NullPointerException e) {}
			try{ca.setPayable(rs.getDouble("capayable"));}catch(NullPointerException e) {}
			try{ca.setIsActive(rs.getInt("caisactive"));}catch(NullPointerException e) {}
			
			Employees em = new Employees();
			try{em.setId(rs.getInt("emid"));}catch(NullPointerException e) {}
			try{em.setRegistered(rs.getString("regdate"));}catch(NullPointerException e) {}
			try{em.setFullName(rs.getString("emname"));}catch(NullPointerException e) {}
			try{em.setDailySalary(rs.getDouble("dailysalary"));}catch(NullPointerException e) {}
			try{em.setIsActive(rs.getInt("emisactive"));}catch(NullPointerException e) {}
			ca.setEmployees(em);
			
			ems.add(ca);
			
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		return ems;
	}
	
public static EmployeeCA save(EmployeeCA prof){
	if(prof!=null){
		List<String> actions = new ArrayList<>();
		long id = EmployeeCA.getInfo(prof.getId()==0? EmployeeCA.getLatestId()+1 : prof.getId());
		actions.add("checking for new added data");
		if(id==1){
			actions.add("insert new Data ");
			LogUserActions.logUserActions(actions);
			prof = EmployeeCA.insertData(prof, "1");
		}else if(id==2){
			actions.add("update Data ");
			LogUserActions.logUserActions(actions);
			prof = EmployeeCA.updateData(prof);
		}else if(id==3){
			actions.add("added new Data ");
			LogUserActions.logUserActions(actions);
			prof = EmployeeCA.insertData(prof, "3");
		}
		
	}
	return prof;
}

public void save(){
		List<String> actions = new ArrayList<>();
		long id = getInfo(getId()==0? getLatestId()+1 : getId());
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

public static EmployeeCA insertData(EmployeeCA em, String type){
	String sql = "INSERT INTO employeeca ("
			+ "caid,"
			+ "cadate,"
			+ "caamount,"
			+ "capayable,"
			+ "emid,"
			+ "caisactive)" 
			+ " values(?,?,?,?,?,?)";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	long id =1;
	int cnt =1;
	actions.add("inserting data into table employeeca");
	if("1".equalsIgnoreCase(type)){
		ps.setLong(cnt++, id);
		em.setId(id);
	}else if("3".equalsIgnoreCase(type)){
		id=getLatestId()+1;
		ps.setLong(cnt++, id);
		em.setId(id);
	}
	
	ps.setString(cnt++, em.getCashDate());
	ps.setDouble(cnt++, em.getAmount());
	ps.setDouble(cnt++, em.getPayable());
	ps.setInt(cnt++, em.getEmployees().getId());
	ps.setInt(cnt++, em.getIsActive());
	
	ps.execute();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error inserting data to employeeca : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
	return em;
}

public void insertData(String type){
	String sql = "INSERT INTO employeeca ("
			+ "caid,"
			+ "cadate,"
			+ "caamount,"
			+ "capayable,"
			+ "emid,"
			+ "caisactive)" 
			+ " values(?,?,?,?,?,?)";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	long id =1;
	int cnt =1;
	actions.add("inserting data into table employeeca");
	if("1".equalsIgnoreCase(type)){
		ps.setLong(cnt++, id);
		setId(id);
	}else if("3".equalsIgnoreCase(type)){
		id=getLatestId()+1;
		ps.setLong(cnt++, id);
		setId(id);
	}
	
	ps.setString(cnt++, getCashDate());
	ps.setDouble(cnt++, getAmount());
	ps.setDouble(cnt++, getPayable());
	ps.setInt(cnt++, getEmployees().getId());
	ps.setInt(cnt++, getIsActive());
	
	ps.execute();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error inserting data to employeeca : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
	
}

public static EmployeeCA updateData(EmployeeCA em){
	String sql = "UPDATE employeeca SET "
			+ "cadate=?,"
			+ "caamount=?,"
			+ "capayable=?,"
			+ "emid=?" 
			+ " WHERE caid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt =1;
	actions.add("updating data into table employeeca");
	
	
	ps.setString(cnt++, em.getCashDate());
	ps.setDouble(cnt++, em.getAmount());
	ps.setDouble(cnt++, em.getPayable());
	ps.setInt(cnt++, em.getEmployees().getId());
	ps.setLong(cnt++, em.getId());
	
	ps.executeUpdate();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error updating data to employeeca : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
	return em;
}

public void updateData(){
	String sql = "UPDATE employeeca SET "
			+ "cadate=?,"
			+ "caamount=?,"
			+ "capayable=?,"
			+ "emid=?" 
			+ " WHERE caid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt =1;
	actions.add("updating data into table employeeca");
	
	
	ps.setString(cnt++, getCashDate());
	ps.setDouble(cnt++, getAmount());
	ps.setDouble(cnt++, getPayable());
	ps.setInt(cnt++, getEmployees().getId());
	ps.setLong(cnt++, getId());
	
	ps.executeUpdate();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error updating data to employeeca : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
	
}

public static long getLatestId(){
	long id =0;
	Connection conn = null;
	PreparedStatement prep = null;
	ResultSet rs = null;
	String sql = "";
	try{
	sql="SELECT caid FROM employeeca  ORDER BY caid DESC LIMIT 1";	
	conn = DataConnectDAO.getConnection();
	prep = conn.prepareStatement(sql);	
	rs = prep.executeQuery();
	
	while(rs.next()){
		id = rs.getLong("caid");
	}
	
	rs.close();
	prep.close();
	DataConnectDAO.close(conn);
	}catch(Exception e){
		e.printStackTrace();
	}
	
	return id;
}

public static Long getInfo(long id){
	boolean isNotNull=false;
	long result =0;
	//id no data retrieve.
	//application will insert a default no which 1 for the first data
	long val = getLatestId();	
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
	ps = conn.prepareStatement("SELECT caid FROM employeeca WHERE caid=?");
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
	String sql = "UPDATE employeeca set caisactive=0 WHERE caid=" + getId();
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

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getCashDate() {
		return cashDate;
	}
	public void setCashDate(String cashDate) {
		this.cashDate = cashDate;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public double getPayable() {
		return payable;
	}
	public void setPayable(double payable) {
		this.payable = payable;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}
	public Employees getEmployees() {
		return employees;
	}
	public void setEmployees(Employees employees) {
		this.employees = employees;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
