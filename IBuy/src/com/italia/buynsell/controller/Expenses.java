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

public class Expenses {

	private Long id;
	private String description;
	private String amountIn;
	private String dateIn;
	private String addedBy;
	private int countAdded;
	private int cnt;
	private ClientProfile clientProfile;
	private ClientTransactions clientTransactions;
	
	public Expenses(){}
	
	public Expenses(
			Long id,
			String description,
			String amountIn,
			String dateIn,
			String addedBy,
			int countAdded,
			int cnt,
			ClientTransactions clientTransactions
			){
		this.id = id;
		this.description = description;
		this.amountIn = amountIn;
		this.dateIn = dateIn;
		this.addedBy = addedBy;
		this.countAdded = countAdded;
		this.cnt = cnt;
		this.clientTransactions = clientTransactions;
	}
	
	
	public static void save(Expenses ex){
		if(ex!=null){
			List<String> actions = new ArrayList<>();
			actions.add("check data for saving....");
			long id = Expenses.getExpensesInfo(ex.getId()==null? Expenses.getLatestExpenseId()+1 : ex.getId());
			
			if(id==1){
				actions.add("new data");
				LogUserActions.logUserActions(actions);
				Expenses.insertData(ex, "1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				Expenses.updateData(ex);
			}else if(id==3){
				actions.add("insert new added data");
				LogUserActions.logUserActions(actions);
				Expenses.insertData(ex, "3");
			}
			
		}
	}
	
	public void save(){
		List<String> actions = new ArrayList<>();
		actions.add("check data for saving....");
			long id = getExpensesInfo(getId()==null? getLatestExpenseId()+1 : getId());
			if(id==1){
				actions.add("new data");
				LogUserActions.logUserActions(actions);
				insertData("1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				updateData();
			}else if(id==3){
				actions.add("insert new added data");
				LogUserActions.logUserActions(actions);
				insertData("3");
			}
			
		
	}
	
	
	public static List<Expenses> retrieveExpenses(String sql, String[] params){
		List<Expenses> exList = new ArrayList<>();
		
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
			Expenses ex = new Expenses();
			
			try{ex.setId(rs.getLong("exeId"));}catch(Exception e){}
			try{ex.setDescription(rs.getString("description"));}catch(Exception e){}
			try{ex.setAmountIn(rs.getString("amountIn"));}catch(Exception e){}
			try{ex.setAddedBy(rs.getString("processedBy"));}catch(Exception e){}
			try{ex.setDateIn(rs.getString("dateIn"));}catch(Exception e){}
			try{
			ClientTransactions clientTransactions = new ClientTransactions();
			clientTransactions.setTransId(rs.getLong("transid"));
			ex.setClientTransactions(clientTransactions);
			}catch(Exception e){}
			
			exList.add(ex);
			
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return exList;
	}
	
	public static Expenses insertData(Expenses ex, String type){
		String sql = "INSERT INTO expenses ("
				+ "exeId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "transid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("inserting new data to expenses...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			ex.setId(Long.valueOf(id));
			actions.add("Id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestExpenseId()+1;
			ps.setLong(1, id);
			ex.setId(Long.valueOf(id));
			actions.add("Id : " + id);
		}
		
		
		ps.setString(2, ex.getDescription());
		actions.add("Description : " + ex.getDescription());
		ps.setString(3, ex.getAmountIn());
		actions.add("AmountIn : " + ex.getAmountIn());
		ps.setString(4, ex.getAddedBy());
		actions.add("AddedBy : " + ex.getAddedBy());
		ps.setString(5, ex.getDateIn());
		actions.add("DateIn : " + ex.getDateIn());
		ps.setInt(6, ex.getCountAdded());
		actions.add("CountAdded : " + ex.getCountAdded());
		if(ex.getClientTransactions()==null){
			ps.setString(7, null);
			actions.add("ClientTransactions :  null ");
		}else{
			ps.setLong(7, ex.getClientTransactions().getTransId());
			actions.add("ClientTransactions : " + ex.getClientTransactions().getTransId());
		}
		
		actions.add("Execute insertion...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully inserted...");
		}catch(SQLException s){
			actions.add("Error in inserting : "+ s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return ex;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO expenses ("
				+ "exeId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "transid)" 
				+ "values(?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("inserting new data to expenses...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setId(Long.valueOf(id));
			actions.add("Id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestExpenseId()+1;
			ps.setLong(1, id);
			setId(Long.valueOf(id));
			actions.add("Id : " + id);
		}
		
		
		ps.setString(2, getDescription());
		actions.add("Description : " + getDescription());
		ps.setString(3, getAmountIn());
		actions.add("AmountIn : " + getAmountIn());
		ps.setString(4, getAddedBy());
		actions.add("AddedBy : " + getAddedBy());
		ps.setString(5, getDateIn());
		actions.add("DateIn : " + getDateIn());
		ps.setInt(6, getCountAdded());
		actions.add("CountAdded : " + getCountAdded());
		if(getClientTransactions()==null){
			ps.setString(7, null);
			actions.add("ClientTransactions :  null ");
		}else{
			ps.setLong(7, getClientTransactions().getTransId());
			actions.add("ClientTransactions : " + getClientTransactions().getTransId());
		}
		
		actions.add("Execute insertion...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully inserted...");
		}catch(SQLException s){
			actions.add("Error in inserting : "+ s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
	}
	
	public static Expenses updateData(Expenses ex){
		String sql = "UPDATE expenses SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?"
				+ " WHERE exeId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("updating data to expenses...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, ex.getDescription());
		actions.add("Description : " + ex.getDescription());
		ps.setString(2, ex.getAmountIn());
		actions.add("AmountIn : " + ex.getAmountIn());
		ps.setString(3, ex.getAddedBy());
		actions.add("AddedBy : " + ex.getAddedBy());
		ps.setString(4, ex.getDateIn());
		actions.add("DateIn : " + ex.getDateIn());
		ps.setLong(5, ex.getId());
		actions.add("Id : " + ex.getId());
		actions.add("executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in updating : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return ex;
	}
	
	public void updateData(){
		String sql = "UPDATE expenses SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?"
				+ " WHERE exeId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("updating data to expenses...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, getDescription());
		actions.add("Description : " + getDescription());
		ps.setString(2, getAmountIn());
		actions.add("AmountIn : " + getAmountIn());
		ps.setString(3, getAddedBy());
		actions.add("AddedBy : " + getAddedBy());
		ps.setString(4, getDateIn());
		actions.add("DateIn : " + getDateIn());
		ps.setLong(5, getId());
		actions.add("Id : " + getId());
		actions.add("executing...");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in updating : " + s.getMessage());
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
	
	public static long getLatestExpenseId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT exeId FROM expenses  ORDER BY exeId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("exeId");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static Long getExpensesInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestExpenseId();	
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
		ps = conn.prepareStatement("SELECT exeId FROM expenses WHERE exeId=?");
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

	public ClientProfile getClientProfile() {
		return clientProfile;
	}

	public ClientTransactions getClientTransactions() {
		return clientTransactions;
	}

	public void setClientProfile(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
	}

	public void setClientTransactions(ClientTransactions clientTransactions) {
		this.clientTransactions = clientTransactions;
	}
	
	public static void main(String[] args) {
		
		Expenses ex = new Expenses();
		ex.setId(422l);
		ex.setDateIn(DateUtils.getCurrentYYYYMMDD());
		ex.setAmountIn("100.00");
		ex.setAddedBy("mark");
		ex.setDescription("testing again update update");
		ex.setCountAdded(1);
		ClientTransactions t = new ClientTransactions();
		t.setTransId(1l);
		ex.setClientTransactions(t);
		ex.save();
		
	}
	
}

