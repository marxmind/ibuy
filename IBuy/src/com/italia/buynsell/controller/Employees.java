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
public class Employees {

	private int id;
	private String registered;
	private String fullName;
	private double dailySalary;
	private int isActive;
	
	private int index;
	
public static List<Employees> retrieve(String sqlAdd, String[] params){
		
		List<Employees> ems = new ArrayList<Employees>();
		
		
		String sql = "SELECT * FROM employee WHERE emisactive=1 ";
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
			
			Employees em = new Employees();
			try{em.setId(rs.getInt("emid"));}catch(NullPointerException e) {}
			try{em.setRegistered(rs.getString("regdate"));}catch(NullPointerException e) {}
			try{em.setFullName(rs.getString("emname"));}catch(NullPointerException e) {}
			try{em.setDailySalary(rs.getDouble("dailysalary"));}catch(NullPointerException e) {}
			try{em.setIsActive(rs.getInt("emisactive"));}catch(NullPointerException e) {}
			ems.add(em);
			
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		return ems;
	}

public static double salaryDailyRate(int id){
	
	String sql = "SELECT * FROM employee WHERE emisactive=1 AND emid=" + id;
	
	Connection conn = null;
	ResultSet rs = null;
	PreparedStatement ps = null;
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	
	rs = ps.executeQuery();
	
	while(rs.next()){
		return rs.getDouble("dailysalary");
	}
	
	rs.close();
	ps.close();
	DataConnectDAO.close(conn);
	}catch(SQLException sq){}
	
	return 0;
}
	
public static Employees save(Employees prof){
	if(prof!=null){
		List<String> actions = new ArrayList<>();
		long id = Employees.getInfo(prof.getId()==0? Employees.getLatestId()+1 : prof.getId());
		actions.add("checking for new added data");
		if(id==1){
			actions.add("insert new Data ");
			LogUserActions.logUserActions(actions);
			prof = Employees.insertData(prof, "1");
		}else if(id==2){
			actions.add("update Data ");
			LogUserActions.logUserActions(actions);
			prof = Employees.updateData(prof);
		}else if(id==3){
			actions.add("added new Data ");
			LogUserActions.logUserActions(actions);
			prof = Employees.insertData(prof, "3");
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

public static Employees insertData(Employees em, String type){
	String sql = "INSERT INTO employee ("
			+ "emid,"
			+ "regdate,"
			+ "emname,"
			+ "dailysalary,"
			+ "emisactive)" 
			+ " values(?,?,?,?,?)";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	int id =1;
	int cnt =1;
	actions.add("inserting data into table employee");
	if("1".equalsIgnoreCase(type)){
		ps.setInt(cnt++, id);
		em.setId(id);
	}else if("3".equalsIgnoreCase(type)){
		id=getLatestId()+1;
		ps.setInt(cnt++, id);
		em.setId(id);
	}
	
	ps.setString(cnt++, em.getRegistered());
	ps.setString(cnt++, em.getFullName());
	ps.setDouble(cnt++, em.getDailySalary());
	ps.setInt(cnt++, em.getIsActive());
	
	ps.execute();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error inserting data to employee : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
	return em;
}

public void insertData(String type){
	String sql = "INSERT INTO employee ("
			+ "emid,"
			+ "regdate,"
			+ "emname,"
			+ "dailysalary,"
			+ "emisactive)" 
			+ " values(?,?,?,?,?)";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	int id =1;
	int cnt =1;
	actions.add("inserting data into table employee");
	if("1".equalsIgnoreCase(type)){
		ps.setInt(cnt++, id);
		setId(id);
	}else if("3".equalsIgnoreCase(type)){
		id=getLatestId()+1;
		ps.setInt(cnt++, id);
		setId(id);
	}
	
	ps.setString(cnt++, getRegistered());
	ps.setString(cnt++, getFullName());
	ps.setDouble(cnt++, getDailySalary());
	ps.setInt(cnt++, getIsActive());
	
	ps.execute();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error inserting data to employee : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
}

public static Employees updateData(Employees em){
	String sql = "UPDATE employee SET "
			+ "regdate=?,"
			+ "emname=?,"
			+ "dailysalary=?"
			+ " WHERE emid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt =1;
	actions.add("updating data into table employee");
	
	ps.setString(cnt++, em.getRegistered());
	ps.setString(cnt++, em.getFullName());
	ps.setDouble(cnt++, em.getDailySalary());
	ps.setInt(cnt++, em.getId());
	
	ps.executeUpdate();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error updating data to employee : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
	return em;
}

public void updateData(){
	String sql = "UPDATE employee SET "
			+ "regdate=?,"
			+ "emname=?,"
			+ "dailysalary=?"
			+ " WHERE emid=?";
	
	PreparedStatement ps = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	
	int cnt =1;
	actions.add("updating data into table employee");
	
	ps.setString(cnt++, getRegistered());
	ps.setString(cnt++, getFullName());
	ps.setDouble(cnt++, getDailySalary());
	ps.setInt(cnt++, getId());
	
	ps.executeUpdate();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("data has been successfully saved...");
	}catch(SQLException s){
		actions.add("error updating data to employee : " + s.getMessage());
	}
	
	LogUserActions.logUserActions(actions);
	
}

public static int getLatestId(){
	int id =0;
	Connection conn = null;
	PreparedStatement prep = null;
	ResultSet rs = null;
	String sql = "";
	try{
	sql="SELECT emid FROM employee  ORDER BY emid DESC LIMIT 1";	
	conn = DataConnectDAO.getConnection();
	prep = conn.prepareStatement(sql);	
	rs = prep.executeQuery();
	
	while(rs.next()){
		id = rs.getInt("emid");
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
	ps = conn.prepareStatement("SELECT emid FROM employee WHERE emid=?");
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
	String sql = "UPDATE employee set emisactive=0 WHERE emid=" + getId();
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

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRegistered() {
		return registered;
	}
	public void setRegistered(String registered) {
		this.registered = registered;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public double getDailySalary() {
		return dailySalary;
	}
	public void setDailySalary(double dailySalary) {
		this.dailySalary = dailySalary;
	}
	public int getIsActive() {
		return isActive;
	}
	public void setIsActive(int isActive) {
		this.isActive = isActive;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}
