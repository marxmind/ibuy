package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.LogUserActions;

public class CashIn {

	private Long id;
	private String description;
	private String amountIn;
	private String dateIn;
	private String addedBy;
	private int countAdded;
	private int cnt;
	private int category;
	private String processedBy;
	private String timestamp;
	private Debit debit;
	private String categoryName;
	private ClientTransactions clientTransactions;
	
	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public CashIn(){}
	
	public CashIn(
			Long id,
			String description,
			String amountIn,
			String dateIn,
			String addedBy,
			int countAdded,
			int cnt,
			int category,
			String processedBy,
			Debit debit,
			ClientTransactions clientTransactions
			){
		this.id = id;
		this.description = description;
		this.amountIn = amountIn;
		this.dateIn = dateIn;
		this.addedBy = addedBy;
		this.countAdded = countAdded;
		this.cnt = cnt;
		this.debit = debit;
		this.addedBy = addedBy;
		this.category = category;
		this.clientTransactions = clientTransactions;
	}
	
	public static void save(CashIn cashIn){
		if(cashIn!=null){
			List<String> actions = new ArrayList<>();
			actions.add("checking for new added data");
			long id = getCashInInfo(cashIn.getId()==null? getLatestCashInId()+1 : cashIn.getId());
			if(id==1){
				actions.add("new added data");
				LogUserActions.logUserActions(actions);
				CashIn.insertData(cashIn, "1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				CashIn.updateData(cashIn);
			}else if(id==3){
				actions.add("adding new added data");
				LogUserActions.logUserActions(actions);
				CashIn.insertData(cashIn, "3");
			}
			
		}
	}
	public void save(){
		List<String> actions = new ArrayList<>();
		actions.add("checking for new added data");
			long id = getCashInInfo(getId()==null? getLatestCashInId()+1 : getId());
			if(id==1){
				actions.add("new added data");
				LogUserActions.logUserActions(actions);
				insertData("1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				updateData();
			}else if(id==3){
				actions.add("adding new added data");
				LogUserActions.logUserActions(actions);
				insertData("3");
			}
	}
	
	public static List<CashIn> retrieveCashIn(String sql, String[] params){
		List<CashIn> cashList = new ArrayList<>();
		
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
		System.out.println("cash in SQL: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			CashIn in = new CashIn();
			
			try{in.setId(rs.getLong("cashinId"));}catch(Exception e){}
			try{in.setDescription(rs.getString("description"));}catch(Exception e){}
			try{in.setAmountIn(rs.getString("amountIn"));}catch(Exception e){}
			try{in.setAddedBy(rs.getString("processedBy"));}catch(Exception e){}
			try{in.setDateIn(rs.getString("dateIn"));}catch(Exception e){}
			try{in.setCategory(rs.getInt("category"));}catch(Exception e){}
			try{in.setTimestamp(rs.getString("timestamp").replace(".0", ""));}catch(NullPointerException e) {}
			Debit debit = new Debit();
			try{debit.setId(rs.getLong("debitId"));}catch(Exception e){}
			try{in.setDebit(debit);}catch(Exception e){}
			try{ClientTransactions clientTransactions = new ClientTransactions();
			clientTransactions.setTransId(rs.getLong("transid"));
			in.setClientTransactions(clientTransactions);}catch(Exception e){}
			System.out.println("Cash loop ... " + in.getAmountIn());
			cashList.add(in);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return cashList;
	}
	
	public static CashIn insertData(CashIn cashIn, String type){
		String sql = "INSERT INTO cashin ("
				+ "cashinId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "category,"
				+ "debitId,"
				+ "transid) " 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting new data to cashin table...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			cashIn.setId(id);
			actions.add("setId : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestCashInId()+1;
			ps.setLong(1, id);
			cashIn.setId(id);
			actions.add("setId : " + id);
		}
		ps.setString(2, cashIn.getDescription());
		actions.add("Description : " + cashIn.getDescription());
		ps.setString(3, cashIn.getAmountIn());
		actions.add("AmountIn : " + cashIn.getAmountIn());
		ps.setString(4, processBy());
		actions.add("processBy : " + processBy());
		cashIn.setAddedBy(processBy());
		ps.setString(5, cashIn.getDateIn());
		actions.add("DateIn : " + cashIn.getDateIn());
		int cnt = getCashInCountAmount()+1;
		ps.setInt(6,cnt);
		actions.add("CashInCountAmount : " + cnt);
		ps.setInt(7, cashIn.getCategory());
		actions.add("Category : " + cashIn.getCategory());
		if(cashIn.getDebit()==null){
			ps.setString(8, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(8, cashIn.getDebit().getId());
			actions.add("Debit : " + cashIn.getDebit().getId());
		}
		if(cashIn.getClientTransactions()==null){
			ps.setString(9, null);
			actions.add("ClientTransactions : null");
		}else{
			ps.setLong(9, cashIn.getClientTransactions().getTransId());
			actions.add("ClientTransactions : " + cashIn.getClientTransactions().getTransId());
		}
		actions.add("executing");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Added data has been successfully added");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return cashIn;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO cashin ("
				+ "cashinId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "category,"
				+ "debitId,"
				+ "transid) " 
				+ "values(?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting new data to cashin table...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setId(id);
			actions.add("setId : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestCashInId()+1;
			ps.setLong(1, id);
			setId(id);
			actions.add("setId : " + id);
		}
		ps.setString(2, getDescription());
		actions.add("Description : " + getDescription());
		ps.setString(3, getAmountIn());
		actions.add("AmountIn : " + getAmountIn());
		ps.setString(4, processBy());
		actions.add("processBy : " + processBy());
		setAddedBy(processBy());
		ps.setString(5, getDateIn());
		actions.add("DateIn : " + getDateIn());
		int cnt = getCashInCountAmount()+1;
		ps.setInt(6,cnt);
		actions.add("CashInCountAmount : " + cnt);
		ps.setInt(7, getCategory());
		actions.add("Category : " + getCategory());
		if(getDebit()==null){
			ps.setString(8, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(8, getDebit().getId());
			actions.add("Debit : " + getDebit().getId());
		}
		if(getClientTransactions()==null){
			ps.setString(9, null);
			actions.add("ClientTransactions : null");
		}else{
			ps.setLong(9, getClientTransactions().getTransId());
			actions.add("ClientTransactions : " + getClientTransactions().getTransId());
		}
		actions.add("executing");
		ps.execute();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Added data has been successfully added");
		}catch(SQLException s){
			actions.add("Error in saving : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
	}
	
	public static CashIn updateData(CashIn cashIn){
		String sql = "UPDATE cashin SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?,"
				+ "category=?,"
				+ "debitId=?"
				+ " WHERE cashinId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("updating data to cashin table...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, cashIn.getDescription());
		actions.add("Description : " + cashIn.getDescription());
		ps.setString(2, cashIn.getAmountIn());
		actions.add("AmountIn : " + cashIn.getAmountIn());
		ps.setString(3, processBy());
		actions.add("processBy : " + processBy());
		cashIn.setAddedBy(processBy());
		ps.setString(4, cashIn.getDateIn());
		actions.add("DateIn : " + cashIn.getDateIn());
		ps.setInt(5,cashIn.getCategory());
		if(cashIn.getDebit()==null){
			ps.setString(6, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(6, cashIn.getDebit().getId());
			actions.add("Debit : " + cashIn.getDebit().getIsPaid());
		}
		ps.setLong(7, cashIn.getId());
		actions.add("Id : " + cashIn.getId());
		actions.add("executing update...");
		ps.executeUpdate();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in updating : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return cashIn;
	}
	public void updateData(){
		String sql = "UPDATE cashin SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?,"
				+ "category=?,"
				+ "debitId=?"
				+ " WHERE cashinId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("updating data to cashin table...");
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, getDescription());
		actions.add("Description : " + getDescription());
		ps.setString(2, getAmountIn());
		actions.add("AmountIn : " + getAmountIn());
		ps.setString(3, processBy());
		actions.add("processBy : " + processBy());
		setAddedBy(processBy());
		ps.setString(4, getDateIn());
		actions.add("DateIn : " + getDateIn());
		ps.setInt(5,getCategory());
		if(getDebit()==null){
			ps.setString(6, null);
			actions.add("Debit : null");
		}else{
			ps.setLong(6, getDebit().getId());
			actions.add("Debit : " + getDebit().getIsPaid());
		}
		ps.setLong(7, getId());
		actions.add("Id : " + getId());
		actions.add("executing update...");
		ps.executeUpdate();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException s){
			actions.add("Error in updating : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
	}
	public static int getCashInCountAmount(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int result = 0;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT countAdded  FROM cashin WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
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
	
	public static long getLatestCashInId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT cashinId FROM cashin  ORDER BY cashinId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("cashinId");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	
	public static Long getCashInInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestCashInId();	
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
		ps = conn.prepareStatement("SELECT cashinId FROM cashin WHERE cashinId=?");
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
		List<String> actions = new ArrayList<>();
		actions.add("["+ processBy() +"]");
		actions.add("deleting data in cashin ...");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				actions.add(params[i]);
				ps.setString(i+1, params[i]);
			}
			
		}
		actions.add("executing deletion...");
		ps.executeUpdate();
		actions.add("closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("data has been successfully removed.");
		}catch(SQLException s){
			actions.add("Error in deletion : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
	}
	
	public static List<CashIn> retriveCashIn(String sql, String[] params){
		
		List<CashIn> cashList = new ArrayList<>();
		
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
			CashIn in = new CashIn();
			in.setId(rs.getLong("cashinId"));
			in.setDescription(rs.getString("description"));
			in.setAmountIn(rs.getString("amountIn"));
			in.setAddedBy(rs.getString("processedBy"));
			in.setProcessedBy(rs.getString("processedBy"));
			in.setDateIn(rs.getString("dateIn"));
			in.setCategory(rs.getInt("category"));
			Debit debit = new Debit();
			debit.setId(rs.getLong("debitId"));
			in.setDebit(debit);
			try{in.setTimestamp(rs.getString("timestamp").replace(".0", ""));}catch(NullPointerException e) {}
			cashList.add(in);
		}
		
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException e){}
		
		return cashList;
		
	}
	
	
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
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


	public String getProcessedBy() {
		return processedBy;
	}


	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	public Debit getDebit() {
		return debit;
	}


	public void setDebit(Debit debit) {
		this.debit = debit;
	}
	
	public static void main(String[] args) {
		
		CashIn cashIn = new CashIn();
		cashIn.setId(Long.valueOf("300"));
		cashIn.setDescription("try ulit");
		
		CashIn.save(cashIn);
		
	}

	public ClientTransactions getClientTransactions() {
		return clientTransactions;
	}

	public void setClientTransactions(ClientTransactions clientTransactions) {
		this.clientTransactions = clientTransactions;
	}
	
}












