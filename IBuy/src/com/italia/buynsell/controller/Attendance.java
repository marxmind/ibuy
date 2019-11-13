package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.LogUserActions;

public class Attendance {

	private long id;
	private String name;
	private String dateWork;
	private String timeInAM;
	private String timeOutAM;
	private String timeInPM;
	private String timeOutPM;
	private double totalTime;
	private double totalAmount;
	private String remarks;
	private int isActive;
	private Employees employees;
	private int isPaid;
	
	private int index;
	
	
public static List<Attendance> retrieve(String sqlAdd, String[] params){
		
		List<Attendance> ems = new ArrayList<Attendance>();
		
		String tableAtt = "att";
		String tableEm = "em";
		String sql = "SELECT * FROM attendance "+ tableAtt +", employee "+ tableEm +" WHERE  " 
		+ tableAtt + ".attisactive=1 AND " +
		tableAtt +".emid=" + tableEm +".emid ";
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
		System.out.println("Attendance>> SQL " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			
			Attendance att = new Attendance();
			try{att.setId(rs.getLong("attid"));}catch(NullPointerException e) {}
			try{att.setDateWork(rs.getString("attdate"));}catch(NullPointerException e) {}
			try{att.setTimeInAM(rs.getString("timeinam"));}catch(NullPointerException e) {}
			try{att.setTimeOutAM(rs.getString("timeoutam"));}catch(NullPointerException e) {}
			try{att.setTimeInPM(rs.getString("timeinpm"));}catch(NullPointerException e) {}
			try{att.setTimeOutPM(rs.getString("timeoutpm"));}catch(NullPointerException e) {}
			try{att.setTotalTime(rs.getDouble("totalhour"));}catch(NullPointerException e) {}
			try{att.setTotalAmount(rs.getDouble("amountpayable"));}catch(NullPointerException e) {}
			try{att.setRemarks(rs.getString("remarks"));}catch(NullPointerException e) {}
			try{att.setIsActive(rs.getInt("attisactive"));}catch(NullPointerException e) {}
			try{att.setIsPaid(rs.getInt("ispaid"));}catch(NullPointerException e) {}
			
			Employees em = new Employees();
			try{em.setId(rs.getInt("emid"));}catch(NullPointerException e) {}
			try{em.setRegistered(rs.getString("regdate"));}catch(NullPointerException e) {}
			try{em.setFullName(rs.getString("emname"));}catch(NullPointerException e) {}
			try{em.setDailySalary(rs.getDouble("dailysalary"));}catch(NullPointerException e) {}
			try{em.setIsActive(rs.getInt("emisactive"));}catch(NullPointerException e) {}
			
			try{att.setName(em.getFullName());}catch(NullPointerException e) {}
			att.setEmployees(em);
			ems.add(att);
			
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		return ems;
	}
	
public static Attendance save(Attendance prof){
	if(prof!=null){
		List<String> actions = new ArrayList<>();
		long id = Attendance.getInfo(prof.getId()==0? Attendance.getLatestId()+1 : prof.getId());
		actions.add("checking for new added data");
		if(id==1){
			actions.add("insert new Data ");
			LogUserActions.logUserActions(actions);
			prof = Attendance.insertData(prof, "1");
		}else if(id==2){
			actions.add("update Data ");
			LogUserActions.logUserActions(actions);
			prof = Attendance.updateData(prof);
		}else if(id==3){
			actions.add("added new Data ");
			LogUserActions.logUserActions(actions);
			prof = Attendance.insertData(prof, "3");
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
	
	public static Attendance findAndLoadTimeSheet(int employeeId, String dateWork) {
		Attendance att = null;
		String sql = "SELECT * FROM attendance WHERE attisactive=1 AND emid=" + employeeId + " AND attdate='"+dateWork+"'";
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		System.out.println("SQL findAndLoadTimeSheet " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			att = new Attendance();
			try{att.setId(rs.getLong("attid"));}catch(NullPointerException e) {}
			try{att.setDateWork(rs.getString("attdate"));}catch(NullPointerException e) {}
			try{att.setTimeInAM(rs.getString("timeinam"));}catch(NullPointerException e) {}
			try{att.setTimeOutAM(rs.getString("timeoutam"));}catch(NullPointerException e) {}
			try{att.setTimeInPM(rs.getString("timeinpm"));}catch(NullPointerException e) {}
			try{att.setTimeOutPM(rs.getString("timeoutpm"));}catch(NullPointerException e) {}
			try{att.setTotalTime(rs.getDouble("totalhour"));}catch(NullPointerException e) {}
			try{att.setTotalAmount(rs.getDouble("amountpayable"));}catch(NullPointerException e) {}
			try{att.setRemarks(rs.getString("remarks"));}catch(NullPointerException e) {}
			try{att.setIsActive(rs.getInt("attisactive"));}catch(NullPointerException e) {}
			try{att.setIsPaid(rs.getInt("ispaid"));}catch(NullPointerException e) {}
			
			Employees em = new Employees();
			try{em.setId(rs.getInt("emid"));}catch(NullPointerException e) {}
			att.setEmployees(em);
			
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		return att;
	}
	
	public static void update(int employeeId, String dateWork, Attendance newdata) {
		Attendance att = new Attendance();
		String sql = "SELECT * FROM attendance WHERE ispaid=0 AND attisactive=1 AND emid=" + employeeId + " AND attdate='"+dateWork+"'";
		boolean isFound = false;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		System.out.println("SQL look update " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			isFound = true;
			System.out.println("Found.... ");
			try{att.setId(rs.getLong("attid"));}catch(NullPointerException e) {}
			try{att.setDateWork(rs.getString("attdate"));}catch(NullPointerException e) {}
			try{att.setTimeInAM(rs.getString("timeinam"));}catch(NullPointerException e) {}
			try{att.setTimeOutAM(rs.getString("timeoutam"));}catch(NullPointerException e) {}
			try{att.setTimeInPM(rs.getString("timeinpm"));}catch(NullPointerException e) {}
			try{att.setTimeOutPM(rs.getString("timeoutpm"));}catch(NullPointerException e) {}
			try{att.setTotalTime(rs.getDouble("totalhour"));}catch(NullPointerException e) {}
			try{att.setTotalAmount(rs.getDouble("amountpayable"));}catch(NullPointerException e) {}
			try{att.setRemarks(rs.getString("remarks"));}catch(NullPointerException e) {}
			try{att.setIsActive(rs.getInt("attisactive"));}catch(NullPointerException e) {}
			try{att.setIsPaid(rs.getInt("ispaid"));}catch(NullPointerException e) {}
			
			Employees em = new Employees();
			try{em.setId(rs.getInt("emid"));}catch(NullPointerException e) {}
			att.setEmployees(em);
			
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		if(isFound) {
			System.out.println("saving found data");
			Attendance upatt = att;
			
			if(newdata.getDateWork()!=null && !newdata.getDateWork().isEmpty()) { 
				if(!att.getDateWork().equalsIgnoreCase(newdata.getDateWork().trim())) {
					upatt.setDateWork(newdata.getDateWork());
				}
			}
			if(newdata.getTimeInAM()!=null && !newdata.getTimeInAM().isEmpty()) { 
				if(!att.getTimeInAM().equalsIgnoreCase(newdata.getTimeInAM().trim())) {
					upatt.setTimeInAM(newdata.getTimeInAM());
				}
			}
			if(newdata.getTimeOutAM()!=null && !newdata.getTimeOutAM().isEmpty()) {
				if(!att.getTimeOutAM().equalsIgnoreCase(newdata.getTimeOutAM().trim())) {
					upatt.setTimeOutAM(newdata.getTimeOutAM());
				}
			}
			if(newdata.getTimeInPM()!=null && !newdata.getTimeInPM().isEmpty()) { 
				if(!att.getTimeInPM().equalsIgnoreCase(newdata.getTimeInPM().trim())) {
					upatt.setTimeInPM(newdata.getTimeInPM());
				}
			}
			if(newdata.getTimeOutPM()!=null && !newdata.getTimeOutPM().isEmpty()) { 
				if(!att.getTimeOutPM().equalsIgnoreCase(newdata.getTimeOutPM().trim())) {
					upatt.setTimeOutPM(newdata.getTimeOutPM());
				}
			}
			if(newdata.getTotalTime()>0) {
				if(att.getTotalTime()!=newdata.getTotalTime()) {
					upatt.setTotalTime(newdata.getTotalTime());
				}
			}
			if(newdata.getTotalAmount()>0) {
				if(att.getTotalAmount()!=newdata.getTotalAmount()) {
					upatt.setTotalAmount(newdata.getTotalAmount());
				}
			}
			if(newdata.getRemarks()!=null && !newdata.getRemarks().isEmpty()) {
				if(att.getRemarks()!=null && !att.getRemarks().equalsIgnoreCase(newdata.getRemarks().trim())) {
					upatt.setRemarks(newdata.getRemarks());
				}else if(att.getRemarks()==null) {
					upatt.setRemarks(newdata.getRemarks());
				}
			}
			
			upatt.save();
		}else {
			System.out.println("saving new data");
			newdata.setId(0);
			newdata.setIsActive(1);
			newdata.setIsPaid(0);
			newdata.save();
		}
		
	}
	
	public static Attendance insertData(Attendance em, String type){
		String sql = "INSERT INTO attendance ("
				+ "attid,"
				+ "attdate,"
				+ "timeinam,"
				+ "timeoutam,"
				+ "timeinpm,"
				+ "timeoutpm,"
				+ "totalhour,"
				+ "amountpayable,"
				+ "remarks,"
				+ "attisactive,"
				+ "emid,"
				+ "ispaid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		actions.add("inserting data into table attendance");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			em.setId(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			em.setId(id);
		}
		
		ps.setString(cnt++, em.getDateWork());
		ps.setString(cnt++, em.getTimeInAM());
		ps.setString(cnt++, em.getTimeOutAM());
		ps.setString(cnt++, em.getTimeInPM());
		ps.setString(cnt++, em.getTimeOutPM());
		ps.setDouble(cnt++, em.getTotalTime());
		ps.setDouble(cnt++, em.getTotalAmount());
		ps.setString(cnt++, em.getRemarks());
		ps.setInt(cnt++, em.getIsActive());
		ps.setInt(cnt++, em.getEmployees().getId());
		ps.setInt(cnt++, em.getIsPaid());
		
		
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error inserting data to attendance : " + s.getMessage());
		}
		
		LogUserActions.logUserActions(actions);
		return em;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO attendance ("
				+ "attid,"
				+ "attdate,"
				+ "timeinam,"
				+ "timeoutam,"
				+ "timeinpm,"
				+ "timeoutpm,"
				+ "totalhour,"
				+ "amountpayable,"
				+ "remarks,"
				+ "attisactive,"
				+ "emid,"
				+ "ispaid)" 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		int cnt =1;
		actions.add("inserting data into table attendance");
		if("1".equalsIgnoreCase(type)){
			ps.setLong(cnt++, id);
			setId(id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestId()+1;
			ps.setLong(cnt++, id);
			setId(id);
		}
		
		ps.setString(cnt++, getDateWork());
		ps.setString(cnt++, getTimeInAM());
		ps.setString(cnt++, getTimeOutAM());
		ps.setString(cnt++, getTimeInPM());
		ps.setString(cnt++, getTimeOutPM());
		ps.setDouble(cnt++, getTotalTime());
		ps.setDouble(cnt++, getTotalAmount());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getIsActive());
		ps.setInt(cnt++, getEmployees().getId());
		ps.setInt(cnt++, getIsPaid());
		
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error inserting data to attendance : " + s.getMessage());
		}
		
		LogUserActions.logUserActions(actions);
		
	}
	
	public static Attendance updateData(Attendance em){
		String sql = "UPDATE attendance SET "
				+ "attdate=?,"
				+ "timeinam=?,"
				+ "timeoutam=?,"
				+ "timeinpm=?,"
				+ "timeoutpm=?,"
				+ "totalhour=?,"
				+ "amountpayable=?,"
				+ "remarks=?,"
				+ "emid=?,"
				+ "ispaid=?" 
				+ " WHERE attid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt =1;
		actions.add("inserting data into table attendance");
		
		ps.setString(cnt++, em.getDateWork());
		ps.setString(cnt++, em.getTimeInAM());
		ps.setString(cnt++, em.getTimeOutAM());
		ps.setString(cnt++, em.getTimeInPM());
		ps.setString(cnt++, em.getTimeOutPM());
		ps.setDouble(cnt++, em.getTotalTime());
		ps.setDouble(cnt++, em.getTotalAmount());
		ps.setString(cnt++, em.getRemarks());
		ps.setInt(cnt++, em.getEmployees().getId());
		ps.setInt(cnt++, em.getIsPaid());
		ps.setLong(cnt++, em.getId());
		
		ps.executeUpdate();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error updating data to attendance : " + s.getMessage());
		}
		
		LogUserActions.logUserActions(actions);
		return em;
	}
	
	public void updateData(){
		String sql = "UPDATE attendance SET "
				+ "attdate=?,"
				+ "timeinam=?,"
				+ "timeoutam=?,"
				+ "timeinpm=?,"
				+ "timeoutpm=?,"
				+ "totalhour=?,"
				+ "amountpayable=?,"
				+ "remarks=?,"
				+ "emid=?,"
				+ "ispaid=?" 
				+ " WHERE attid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		int cnt =1;
		actions.add("inserting data into table attendance");
		
		ps.setString(cnt++, getDateWork());
		ps.setString(cnt++, getTimeInAM());
		ps.setString(cnt++, getTimeOutAM());
		ps.setString(cnt++, getTimeInPM());
		ps.setString(cnt++, getTimeOutPM());
		ps.setDouble(cnt++, getTotalTime());
		ps.setDouble(cnt++, getTotalAmount());
		ps.setString(cnt++, getRemarks());
		ps.setInt(cnt++, getEmployees().getId());
		ps.setInt(cnt++, getIsPaid());
		ps.setLong(cnt++, getId());
		
		ps.executeUpdate();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully saved...");
		}catch(SQLException s){
			actions.add("error updating data to attendance : " + s.getMessage());
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
		sql="SELECT attid FROM attendance  ORDER BY attid DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("attid");
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
		ps = conn.prepareStatement("SELECT attid FROM attendance WHERE attid=?");
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
		String sql = "UPDATE attendance set attisactive=0 WHERE attid=" + getId();
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDateWork() {
		return dateWork;
	}
	public void setDateWork(String dateWork) {
		this.dateWork = dateWork;
	}
	public String getTimeInAM() {
		return timeInAM;
	}
	public void setTimeInAM(String timeInAM) {
		this.timeInAM = timeInAM;
	}
	public String getTimeOutAM() {
		return timeOutAM;
	}
	public void setTimeOutAM(String timeOutAM) {
		this.timeOutAM = timeOutAM;
	}
	public String getTimeInPM() {
		return timeInPM;
	}
	public void setTimeInPM(String timeInPM) {
		this.timeInPM = timeInPM;
	}
	public String getTimeOutPM() {
		return timeOutPM;
	}
	public void setTimeOutPM(String timeOutPM) {
		this.timeOutPM = timeOutPM;
	}
	public double getTotalTime() {
		return totalTime;
	}
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public int getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(int isPaid) {
		this.isPaid = isPaid;
	}
	
}
