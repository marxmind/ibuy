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

public class Debit {
	private Long id;
	private String description;
	private String amountIn;
	private String dateIn;
	private String addedBy;
	private int countAdded;
	private String datepaying;
	private String isPaid;
	private int datecounting;
	private int cnt;
	private String withInterest;
	private String amntPenalty;
	private String lendName;
	private int debittypeId;
	private String othername;
	private long employeeid;
	private String debitTypeName;
	
	public Debit(){}
	public Debit(
			Long id,
			String description,
			String amountIn,
			String dateIn,
			String addedBy,
			int countAdded,
			String datepaying,
			String isPaid,
			int datecounting,
			String withInterest,
			String amntPenalty,
			int debittypeId,
			String othername,
			long employeeid
			){
		this.id = id;
		this.description = description;
		this.amountIn = amountIn;
		this.dateIn = dateIn;
		this.addedBy = addedBy;
		this.countAdded = countAdded;
		this.datepaying = datepaying;
		this.isPaid = isPaid;
		this.datecounting = datecounting;
		this.withInterest = withInterest;
		this.amntPenalty = amntPenalty;
		this.debittypeId = debittypeId;
		this.othername = othername;
		this.employeeid = employeeid;
	}
	
	
	public static void save(Debit debit){
		if(debit!=null){
			long id = Debit.getDebitInfo(debit.getId()==null? getLatestDebitId()+1 : debit.getId());
			List<String> actions = new ArrayList<>();
			actions.add("checking data to be save.");
			if(id==1){
				actions.add("insert data");
				LogUserActions.logUserActions(actions);
				Debit.insertData(debit, "1");
			}else if(id==2){
				actions.add("update data");
				LogUserActions.logUserActions(actions);
				Debit.updateData(debit);
			}else if(id==3){
				actions.add("added new data");
				LogUserActions.logUserActions(actions);
				Debit.insertData(debit, "3");
			}
		}
	}
	public void save(){
			long id = Debit.getDebitInfo(getId()==null? getLatestDebitId()+1 : getId());
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
	
	public static List<Debit> retrieveDebit(String sql, String[] params){
		
		List<Debit> debitList = new ArrayList<>();
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
		//System.out.println("SQL payslip : " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			Debit debit = new Debit();
			try{debit.setId(rs.getLong("debitId"));}catch(Exception e){}
			try{debit.setDescription(rs.getString("description"));}catch(Exception e){}
			try{debit.setAmountIn(rs.getString("amountIn"));}catch(Exception e){}
			try{debit.setAddedBy(rs.getString("processedBy"));}catch(Exception e){}
			try{debit.setDateIn(rs.getString("dateIn"));}catch(Exception e){}
			try{debit.setCountAdded(rs.getInt("countAdded"));}catch(Exception e){}
			try{debit.setDatepaying(rs.getString("datepaying"));}catch(Exception e){}
			try{debit.setIsPaid(rs.getString("isPaid"));}catch(Exception e){}
			try{debit.setDatecounting(rs.getInt("datecounting"));}catch(Exception e){}
			try{debit.setWithInterest(rs.getString("withinterest"));}catch(Exception e){}
			try{debit.setAmntPenalty(rs.getString("amntpenalty"));}catch(Exception e){}
			try{debit.setDebittypeId(rs.getInt("debittypeId"));}catch(Exception e){}
			try{debit.setOthername(rs.getString("othername"));}catch(Exception e){}
			try{debit.setEmployeeid(rs.getLong("employeeid"));}catch(Exception e){}
			debitList.add(debit);
		}
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sq){}
		
		return debitList;
	}
	
	public static Debit insertData(Debit debit, String type){
		String sql = "INSERT INTO debit ("
				+ "debitId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "datepaying,"
				+ "isPaid,"
				+ "datecounting,"
				+ "withinterest,"
				+ "amntpenalty,"
				+ "debittypeId,"
				+ "othername,"
				+ "employeeid) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on debit table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			debit.setId(id);
			actions.add("id : " + id);
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestDebitId()+1;
			ps.setLong(1, id);
			debit.setId(id);
			actions.add("id : " + id);
		}
		int cntDebit = getDebitCountAmount()+1;
		ps.setString(2, debit.getDescription());
		ps.setString(3, debit.getAmountIn());
		ps.setString(4, processBy());
		debit.setAddedBy(processBy());
		ps.setString(5, debit.getDateIn());
		ps.setInt(6,cntDebit);
		ps.setString(7, debit.getDatepaying());
		ps.setString(8, debit.getIsPaid());
		ps.setInt(9, debit.getDatecounting());
		ps.setString(10, debit.getWithInterest());
		ps.setString(11, debit.getAmntPenalty());
		ps.setInt(12, debit.getDebittypeId());
		ps.setString(13, debit.getOthername());
		ps.setLong(14, debit.getEmployeeid());
		
		actions.add("Description : "+ debit.getDescription());
		actions.add("AmountIn :" + debit.getAmountIn());
		actions.add("processBy : "+ processBy());
		actions.add("DateIn : " + debit.getDateIn());
		actions.add("cntDebit : " + cntDebit);
		actions.add("DatePaying : " + debit.getDatepaying());
		actions.add("IsPaid : "+ debit.getIsPaid());
		actions.add("DateCounting : "+ debit.getDatecounting());
		actions.add("With Interest : "+ debit.getWithInterest());
		actions.add("Amount Penalty : "+ debit.getAmntPenalty());
		actions.add("DebitTypeId : "+ debit.getDebittypeId());
		actions.add("Other Name : " +  debit.getOthername());
		actions.add("Employee Id : " + debit.getEmployeeid());
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
		return debit;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO debit ("
				+ "debitId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "datepaying,"
				+ "isPaid,"
				+ "datecounting,"
				+ "withinterest,"
				+ "amntpenalty,"
				+ "debittypeId,"
				+ "othername,"
				+ "employeeid) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data on debit table");
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
			id=getLatestDebitId()+1;
			ps.setLong(1, id);
			setId(id);
			actions.add("id : " + id);
		}
		int cntDebit = getDebitCountAmount()+1;
		ps.setString(2, getDescription());
		ps.setString(3, getAmountIn());
		ps.setString(4, processBy());
		setAddedBy(processBy());
		ps.setString(5, getDateIn());
		ps.setInt(6,cntDebit);
		ps.setString(7, getDatepaying());
		ps.setString(8, getIsPaid());
		ps.setInt(9, getDatecounting());
		ps.setString(10, getWithInterest());
		ps.setString(11, getAmntPenalty());
		ps.setInt(12, getDebittypeId());
		ps.setString(13, getOthername());
		ps.setLong(14, getEmployeeid());
		
		actions.add("Description : "+ getDescription());
		actions.add("AmountIn :" + getAmountIn());
		actions.add("processBy : "+ processBy());
		actions.add("DateIn : " + getDateIn());
		actions.add("cntDebit : " + cntDebit);
		actions.add("DatePaying : " + getDatepaying());
		actions.add("IsPaid : "+ getIsPaid());
		actions.add("DateCounting : "+ getDatecounting());
		actions.add("With Interest : "+ getWithInterest());
		actions.add("Amount Penalty : "+ getAmntPenalty());
		actions.add("DebitTypeId : "+ getDebittypeId());
		actions.add("Other Name : " +  getOthername());
		actions.add("Employee Id : " + getEmployeeid());
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
	
	public static int getDebitCountAmount(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int result = 0;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT countAdded  FROM debit WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
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
	
	public static Debit updateData(Debit debit){
		String sql = "UPDATE debit SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?,"
				+ "datepaying=?,"
				+ "isPaid=?,"
				+ "datecounting=?,"
				+ "withinterest=?,"
				+ "amntpenalty=?,"
				+ "debittypeId=?,"
				+ "othername=?,"
				+ "employeeid=?"
				+ " WHERE debitId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data on debit table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, debit.getDescription());
		ps.setString(2, debit.getAmountIn());
		ps.setString(3, processBy());
		debit.setAddedBy(processBy());
		ps.setString(4, debit.getDateIn());
		ps.setString(5, debit.getDatepaying());
		ps.setString(6, debit.getIsPaid());
		ps.setInt(7, debit.getDatecounting());
		ps.setString(8, debit.getWithInterest());
		ps.setString(9, debit.getAmntPenalty());
		ps.setInt(10, debit.getDebittypeId());
		ps.setString(11, debit.getOthername());
		ps.setLong(12, debit.getEmployeeid());
		ps.setLong(13, debit.getId());
		
		actions.add("Description : "+ debit.getDescription());
		actions.add("AmountIn :" + debit.getAmountIn());
		actions.add("processBy : "+ processBy());
		actions.add("DateIn : " + debit.getDateIn());
		actions.add("DatePaying : " + debit.getDatepaying());
		actions.add("IsPaid : "+ debit.getIsPaid());
		actions.add("DateCounting : "+ debit.getDatecounting());
		actions.add("With Interest : "+ debit.getWithInterest());
		actions.add("Amount Penalty : "+ debit.getAmntPenalty());
		actions.add("DebitTypeId : "+ debit.getDebittypeId());
		actions.add("Other Name : " +  debit.getOthername());
		actions.add("Employee Id : " + debit.getEmployeeid());
		actions.add("Id : " + debit.getId());
		actions.add("Executing...");
		ps.executeUpdate();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been updated.");
		}catch(SQLException s){
			actions.add("Error in update : " + s.getMessage());
		}
		LogUserActions.logUserActions(actions);
		return debit;
	}
	
	public void updateData(){
		String sql = "UPDATE debit SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "dateIn=?,"
				+ "datepaying=?,"
				+ "isPaid=?,"
				+ "datecounting=?,"
				+ "withinterest=?,"
				+ "amntpenalty=?,"
				+ "debittypeId=?,"
				+ "othername=?,"
				+ "employeeid=?"
				+ " WHERE debitId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("update data on debit table");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, getDescription());
		ps.setString(2, getAmountIn());
		ps.setString(3, processBy());
		setAddedBy(processBy());
		ps.setString(4, getDateIn());
		ps.setString(5, getDatepaying());
		ps.setString(6, getIsPaid());
		ps.setInt(7, getDatecounting());
		ps.setString(8, getWithInterest());
		ps.setString(9, getAmntPenalty());
		ps.setInt(10, getDebittypeId());
		ps.setString(11, getOthername());
		ps.setLong(12, getEmployeeid());
		ps.setLong(13, getId());
		
		actions.add("Description : "+ getDescription());
		actions.add("AmountIn :" + getAmountIn());
		actions.add("processBy : "+ processBy());
		actions.add("DateIn : " + getDateIn());
		actions.add("DatePaying : " + getDatepaying());
		actions.add("IsPaid : "+ getIsPaid());
		actions.add("DateCounting : "+ getDatecounting());
		actions.add("With Interest : "+ getWithInterest());
		actions.add("Amount Penalty : "+ getAmntPenalty());
		actions.add("DebitTypeId : "+ getDebittypeId());
		actions.add("Other Name : " +  getOthername());
		actions.add("Employee Id : " + getEmployeeid());
		actions.add("Executing...");
		actions.add("Id : " + getId());
		actions.add("Executing...");
		ps.executeUpdate();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been updated.");
		}catch(SQLException s){
			actions.add("Error in update : " + s.getMessage());
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
	public static long getLatestDebitId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT debitId FROM debit  ORDER BY debitId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("debitId");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static Long getDebitInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestDebitId();	
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
		ps = conn.prepareStatement("SELECT debitId FROM debit WHERE debitId=?");
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
	
	/**
	 * 
	 * @param debit = provide id and description
	 * @param permanent = if true data will remove in the database
	 */
	
	public static void delete(Debit debit, boolean permanent){
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		String sql = "UPDATE debit set isPaid=2, description=? WHERE debitId=?";
		actions.add("deletion data on debit table");
		if(permanent){
			sql = "DELETE FROM debit WHERE debitId=?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1,debit.getId());
			actions.add("Permanent");
			actions.add("SQL : "+ sql);
			actions.add("id : " + debit.getId());
		}else{
			actions.add("SQL : " + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, "(DELETED)" + debit.getDescription());
			ps.setLong(2,debit.getId());
			actions.add("Description : (DELETED) " + debit.getDescription());
			actions.add("id : " + debit.getId());
		}
		actions.add("Executing....");
		ps.executeUpdate();
		actions.add("Clsoing....");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully removed.");
		}catch(Exception e){
			actions.add("Error in deletion : " + e.getMessage());
		}
		LogUserActions.logUserActions(actions);
	}
	
	/**
	 * 
	 * @param debit = provide id and description
	 * @param permanent = if true data will remove in the database
	 */
	public void delete(boolean permanent){
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		try{
		conn = DataConnectDAO.getConnection();
		String sql = "UPDATE debit set isPaid=2, description=? WHERE debitId=?";
		actions.add("deletion data on debit table");
		if(permanent){
			sql = "DELETE FROM debit WHERE debitId=?";
			ps = conn.prepareStatement(sql);
			ps.setLong(1,getId());
			actions.add("SQL : "+ sql);
			actions.add("Id : " + getId());
		}else{
			actions.add("SQL : " + sql);
			ps = conn.prepareStatement(sql);
			ps.setString(1, "(DELETED)" + getDescription());
			ps.setLong(2,getId());
			actions.add("Description : (DELETED)" + getDescription());
			actions.add("Id : " + getId());
		}
		actions.add("Executing...");
		ps.executeUpdate();
		actions.add("Closing...");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully removed.");
		}catch(Exception e){
			actions.add("Error in deletion : " + e.getMessage());
		}
		LogUserActions.logUserActions(actions);
	}
	
	public static void delete(String sql, String[] params){
	
	Connection conn = null;
	PreparedStatement ps = null;
	List<String> actions = new ArrayList<>();
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	actions.add("deletion data on debit table");
	if(params!=null && params.length>0){
		
		for(int i=0; i<params.length; i++){
			actions.add(params[i]);
			ps.setString(i+1, params[i]);
		}
		
	}
	actions.add("Executing...");
	ps.executeUpdate();
	actions.add("Closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("Data has been successfully removed.");
	}catch(SQLException s){
		actions.add("Error in deletion : " + s.getMessage());
	}
	LogUserActions.logUserActions(actions);
}
	
	public String getDebitTypeName() {
		return debitTypeName;
	}
	public void setDebitTypeName(String debitTypeName) {
		this.debitTypeName = debitTypeName;
	}
	public int getDebittypeId() {
		return debittypeId;
	}
	public void setDebittypeId(int debittypeId) {
		this.debittypeId = debittypeId;
	}
	public String getOthername() {
		return othername;
	}
	public void setOthername(String othername) {
		this.othername = othername;
	}
	public long getEmployeeid() {
		return employeeid;
	}
	public void setEmployeeid(long employeeid) {
		this.employeeid = employeeid;
	}
	public String getLendName() {
		return lendName;
	}
	public void setLendName(String lendName) {
		this.lendName = lendName;
	}
	public String getAmntPenalty() {
		return amntPenalty;
	}
	public void setAmntPenalty(String amntPenalty) {
		this.amntPenalty = amntPenalty;
	}
	
	public String getWithInterest() {
		return withInterest;
	}
	public void setWithInterest(String withInterest) {
		this.withInterest = withInterest;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public String getDatepaying() {
		return datepaying;
	}
	public void setDatepaying(String datepaying) {
		this.datepaying = datepaying;
	}
	public String getIsPaid() {
		return isPaid;
	}
	public void setIsPaid(String isPaid) {
		this.isPaid = isPaid;
	}
	public int getDatecounting() {
		return datecounting;
	}
	public void setDatecounting(int datecounting) {
		this.datecounting = datecounting;
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
		String sql = "SELECT * FROM debit";
		String[] params = new String[0]; 
		/*for(Debit d : Debit.retrieveDebit(sql, params)){
			System.out.println(d.getId());
		}*/
		Debit debit = new Debit();
		//debit.setDescription("try");
		//debit.save(debit);
		//debit.setId(Long.valueOf("228"));
		debit.setDateIn(DateUtils.getCurrentYYYYMMDD());
		debit.setDescription("try edited again tots dis isa pa");
		//debit.updateData(debit);
		//debit.updateData();
		debit.save();
		//debit.delete(true);
		//Debit.delete(debit, true);
	}
	
}

