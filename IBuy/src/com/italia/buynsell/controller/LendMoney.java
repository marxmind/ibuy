package com.italia.buynsell.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.dao.DataConnectIPayDAO;

public class LendMoney {

	private Long lendId;
	private String description;
	private BigDecimal amountIn;
	private String processedBy;
	private String dateIn;
	private String datePaid;
	private Employee employee;
	private Timestamp timestamp;
	private boolean isPaid;
	private Debit debit;
	
	public Debit getDebit() {
		return debit;
	}

	public void setDebit(Debit debit) {
		this.debit = debit;
	}

	public boolean getIsPaid() {
		return isPaid;
	}

	public void setIsPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}

	public static void save(LendMoney lend){
		if(lend!=null){
			long id = LendMoney.getLendMoneyInfo(lend.getLendId()==null? LendMoney.getLatestLendId()+1 : lend.getLendId());
			
			if(id==1){
				LendMoney.insertData(lend, "1");
			}else if(id==2){
				LendMoney.updateData(lend);
			}else if(id==3){
				LendMoney.insertData(lend, "3");
			}
		}
	}
	
	public static List<LendMoney> retrieveLendMoney(String sql, String[] params){
		List<LendMoney> lendList = new ArrayList<>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectIPayDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			LendMoney lend = new LendMoney();
			lend.setLendId(rs.getLong("lendId"));
			Employee employee = new Employee();
			employee.setEmpId(rs.getLong("empid"));
			lend.setEmployee(employee);
			lend.setDescription(rs.getString("description"));
			lend.setAmountIn(rs.getBigDecimal("amountIn"));
			lend.setProcessedBy(rs.getString("processedBy"));
			lend.setDateIn(rs.getString("dateIn"));
			lend.setDatePaid(rs.getString("datepaid"));
			lend.setIsPaid(rs.getInt("isPaid")==0? false : true);
			lend.setTimestamp(rs.getTimestamp("timestamp"));
			lendList.add(lend);
		}
		rs.close();
		ps.close();
		DataConnectIPayDAO.close(conn);
		}catch(SQLException sl){}
		
		return lendList;
	}
	
	public static LendMoney insertData(LendMoney lend, String type){
		String sql = "INSERT INTO lendmoney ("
				+ "lendId,"
				+ "empid,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "isPaid,"
				+ "debitId)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectIPayDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			lend.setLendId(Long.valueOf(id));
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestLendId()+1;
			ps.setLong(1, id);
			lend.setLendId(Long.valueOf(id));
		}
		ps.setLong(2, lend.getEmployee().getEmpId());
		ps.setString(3, lend.getDescription());
		ps.setBigDecimal(4, lend.getAmountIn());
		ps.setString(5, processBy());
		ps.setString(6, lend.getDateIn());
		ps.setInt(7, 0);
		ps.setLong(8, lend.getDebit().getId());
		System.out.println("SQL ADD : " + ps.toString());
		ps.execute();
		ps.close();
		DataConnectIPayDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
		return lend;
	}
	
	public static LendMoney updateData(LendMoney lend){
		String sql = "UPDATE lendmoney SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "datepaid=?,"
				+ "isPaid=?"
				+ " WHERE lendId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectIPayDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, lend.getDescription());
		ps.setBigDecimal(2, lend.getAmountIn());
		ps.setString(3, processBy());
		lend.setProcessedBy(processBy());
		ps.setString(4, lend.getDatePaid());
		if("deleted".equalsIgnoreCase(lend.getDescription())){
			ps.setInt(5,2);
		}else{
			ps.setInt(5, lend.getIsPaid()==true? 1 : 0);
		}
		ps.setLong(6, lend.getLendId());
		System.out.println("UPDATE EMPLOYEE: " + ps.toString());
		ps.executeUpdate();
		ps.close();
		DataConnectIPayDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		return lend;
	}
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	public static long getLatestLendId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT lendId FROM lendmoney  ORDER BY lendId DESC LIMIT 1";	
		conn = DataConnectIPayDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("lendId");
		}
		
		rs.close();
		prep.close();
		DataConnectIPayDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static Long getLendMoneyInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestLendId();	
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
		conn = DataConnectIPayDAO.getConnection();
		ps = conn.prepareStatement("SELECT lendId FROM lendmoney WHERE lendId=?");
		ps.setLong(1, id);
		rs = ps.executeQuery();
		
		if(rs.next()){
			result=true;
		}
		
		rs.close();
		ps.close();
		DataConnectIPayDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	
	public Long getLendId() {
		return lendId;
	}
	public void setLendId(Long lendId) {
		this.lendId = lendId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public BigDecimal getAmountIn() {
		return amountIn;
	}
	public void setAmountIn(BigDecimal amountIn) {
		this.amountIn = amountIn;
	}
	public String getProcessedBy() {
		return processedBy;
	}
	public void setProcessedBy(String processedBy) {
		this.processedBy = processedBy;
	}
	public String getDateIn() {
		return dateIn;
	}
	public void setDateIn(String dateIn) {
		this.dateIn = dateIn;
	}
	public String getDatePaid() {
		return datePaid;
	}
	public void setDatePaid(String datePaid) {
		this.datePaid = datePaid;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Timestamp getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	
}

