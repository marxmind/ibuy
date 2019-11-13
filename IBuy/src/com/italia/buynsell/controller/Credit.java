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
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.LogUserActions;

public class Credit {
	
	
	private Long id;
	private String description;
	private String amountIn;
	private String dateIn;
	private String addedBy;
	private int countAdded;
	private int cnt; 
	private Debit debit;
	
	public Credit(){}
	
	public Credit(
			Long id,
			String description,
			String amountIn,
			String dateIn,
			String addedBy,
			int countAdded,
			int cnt, 
			Debit debit
			){
		this.id = id;
		this.description = description;
		this.amountIn = amountIn;
		this.dateIn = dateIn;
		this.addedBy = addedBy;
		this.countAdded = countAdded;
		this.cnt = cnt;
		this.debit = debit;
	}
	
	public static void save(Credit credit){
		if(credit!=null){
			List<String> actions = new ArrayList<>();
			actions.add("checking data to be save.");
			long id = getCreditInfo(credit.getId()==null? getLatestCreditId()+1 : credit.getId());
			if(id==1){
				actions.add("inserting new data");
				LogUserActions.logUserActions(actions);
				Credit.insertData(credit, "1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				Credit.updateData(credit);
			}else if(id==3){
				actions.add("adding new data");
				LogUserActions.logUserActions(actions);
				Credit.insertData(credit, "3");
			}
		}
	}
	public void save(){
		List<String> actions = new ArrayList<>();
		actions.add("checking data to be save.");
			long id = getCreditInfo(getId()==null? getLatestCreditId()+1 : getId());
			if(id==1){
				actions.add("inserting new data");
				LogUserActions.logUserActions(actions);
				insertData("1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				updateData();
			}else if(id==3){
				actions.add("adding new data");
				LogUserActions.logUserActions(actions);
				insertData("3");
			}
	}
	
	public static List<Credit> retrieveCredit(String sql, String[] params){
		
		List<Credit> creditList = new ArrayList<>();
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
			Credit credit = new Credit();
			try{credit.setId(rs.getLong("creditId"));}catch(NullPointerException e){}
			try{credit.setDescription(rs.getString("description"));}catch(NullPointerException e){}
			try{credit.setAmountIn(rs.getString("amountIn"));}catch(NullPointerException e){}
			try{credit.setAddedBy(rs.getString("processedBy"));}catch(NullPointerException e){}
			try{credit.setDateIn(rs.getString("dateIn"));}catch(NullPointerException e){}
			try{credit.setCountAdded(rs.getInt("countAdded"));}catch(NullPointerException e){}
			Debit debit = new Debit();
			try{debit.setId(rs.getLong("debitId"));}catch(NullPointerException e){}
			try{credit.setDebit(debit);}catch(NullPointerException e){}
			creditList.add(credit);
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		return creditList;
	}
	
	
	public static Credit insertData(Credit credit, String type){
		String sql = "INSERT INTO credit ("
				+ "creditId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "debitId) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("inserting data to credit table.");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			credit.setId(id);
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestCreditId()+1;
			ps.setLong(1, id);
			credit.setId(id);
			actions.add("id : " + id);
		}
		ps.setString(2, credit.getDescription());
		ps.setString(3, credit.getAmountIn());
		ps.setString(4, processBy());
		credit.setAddedBy(processBy());
		ps.setString(5, credit.getDateIn());
		int cnt = getCreditCountAmount()+1;
		ps.setInt(6,cnt);
		
		actions.add("Description : " + credit.getDescription());
		actions.add("AmountIn : " + credit.getAmountIn());
		actions.add("processBy : " + processBy());
		actions.add("DateIn : " + credit.getDateIn());
		actions.add("CreditCountAmount : " + cnt);
		
		if(credit.getDebit()==null){
			ps.setString(7, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(7, credit.getDebit().getId());
			actions.add("Debit : " + credit.getDebit().getId());
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
		return credit;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO credit ("
				+ "creditId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "debitId) " 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("inserting data to credit table.");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setId(id);
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestCreditId()+1;
			ps.setLong(1, id);
			setId(id);
			actions.add("id : " + id);
		}
		ps.setString(2, getDescription());
		ps.setString(3, getAmountIn());
		ps.setString(4, processBy());
		setAddedBy(processBy());
		ps.setString(5, getDateIn());
		int cnt = getCreditCountAmount()+1;
		ps.setInt(6,cnt);
		
		actions.add("Description : " + getDescription());
		actions.add("AmountIn : " + getAmountIn());
		actions.add("processBy : " + processBy());
		actions.add("DateIn : " + getDateIn());
		actions.add("CreditCountAmount : " + cnt);
		if(getDebit()==null){
			ps.setString(7, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(7, getDebit().getId());
			actions.add("Debit : " + getDebit().getId());
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
	
	public static Credit updateData(Credit credit){
		String sql = "UPDATE credit SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?,"
				+ "debitId=?"
				+ " WHERE creditId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data to credit table.");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, credit.getDescription());
		ps.setString(2, credit.getAmountIn());
		ps.setString(3, processBy());
		credit.setAddedBy(processBy());
		ps.setString(4, credit.getDateIn());
		
		actions.add("Description : " + credit.getDescription());
		actions.add("AmountIn : " + credit.getAmountIn());
		actions.add("processBy : " + processBy());
		actions.add("DateIn : " + credit.getDateIn());
		
		if(credit.getDebit()==null){
			ps.setString(5, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(5, credit.getDebit().getId());
			actions.add("Debit : " + credit.getDebit().getId());
		}
		ps.setLong(6, credit.getId());
		actions.add("id : " + credit.getId());
		actions.add("executing...");
		ps.executeUpdate();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfuly updated.");
		}catch(SQLException s){
			actions.add("Error in updating : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return credit;
	}
	
	public void updateData(){
		String sql = "UPDATE credit SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?,"
				+ "debitId=?"
				+ " WHERE creditId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data to credit table.");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1,getDescription());
		ps.setString(2,getAmountIn());
		ps.setString(3, processBy());
		setAddedBy(processBy());
		ps.setString(4, getDateIn());
		
		actions.add("Description : " + getDescription());
		actions.add("AmountIn : " + getAmountIn());
		actions.add("processBy : " + processBy());
		actions.add("DateIn : " + getDateIn());
		
		if(getDebit()==null){
			ps.setString(5, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(5, getDebit().getId());
			actions.add("Debit : " + getDebit().getId());
		}
		ps.setLong(6, getId());
		actions.add("id : " + getId());
		actions.add("executing...");
		ps.executeUpdate();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfuly updated.");
		}catch(SQLException s){
			actions.add("Error in updating : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
	}
	
	public static int getCreditCountAmount(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int result = 0;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT countAdded  FROM credit WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
		ps.setString(1, DateUtils.getCurrentYYYYMMDD());
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=rs.getInt("countAdded");
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	public static long getLatestCreditId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT creditId FROM credit  ORDER BY creditId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("creditId");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getCreditInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestCreditId();	
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
		ps = conn.prepareStatement("SELECT creditId FROM credit WHERE creditId=?");
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
	
	public Debit getDebit() {
		return debit;
	}

	public void setDebit(Debit debit) {
		this.debit = debit;
	}

	public static void delete(String sql, String[] params){
		List<String> actions = new ArrayList<>();
		actions.add("deletion of data");
		actions.add("SQL : " + sql);
	Connection conn = null;
	PreparedStatement ps = null;
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	
	if(params!=null && params.length>0){
		
		for(int i=0; i<params.length; i++){
			actions.add(params[i]);
			ps.setString(i+1, params[i]);
		}
		
	}
	actions.add("executing...");
	ps.executeUpdate();
	actions.add("closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("Data has been successfully deleted.");
	}catch(SQLException s){
		actions.add("Error in deletion : " + s.getMessage());
	}
	LogUserActions.logUserActions(actions);
	}
	
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmountIn() {
		return amountIn;
	}
	public void setAmountIn(String amountIn) {
		this.amountIn = amountIn;
	}
	public String getDateIn() {
		return dateIn;
	}
	public void setDateIn(String dateIn) {
		this.dateIn = dateIn;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public int getCountAdded() {
		return countAdded;
	}
	public void setCountAdded(int countAdded) {
		this.countAdded = countAdded;
	}
	
	public static void main(String[] args) {
		
		Credit credit = new Credit();
		//credit.setId(Long.valueOf("230"));
		credit.setDescription("test ito ako ulit pa");
		credit.setDateIn(DateUtils.getCurrentYYYYMMDD());
		Credit.save(credit);
		
	}
	
	
}
