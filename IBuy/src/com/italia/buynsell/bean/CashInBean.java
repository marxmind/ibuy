package com.italia.buynsell.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.controller.CashIn;
import com.italia.buynsell.controller.Debit;
import com.italia.buynsell.controller.GenericPrint;
import com.italia.buynsell.controller.GenericReportMain;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.Application;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;

@ManagedBean (name="cashInBean", eager=true)
@ViewScoped
public class CashInBean {

	private String description;
	private String amount;
	private String datein;
	private String addedBy;
	private String userNotification;
	private Long id;
	private String totalCashIn;
	private List<CashIn> data = new ArrayList<>();
	private Map<Long, CashIn> cashinData = Collections.synchronizedMap(new HashMap<Long, CashIn>());
	private CashIn cashIn;
	
	private String category;
	private List categoryList = new ArrayList<>();
	
	
	private String searchParam;
	private Date calendarFrom;
	private Date calendarTo;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List getCategoryList() {
		
		categoryList = new ArrayList<>();
		categoryList.add(new SelectItem("1","REM CASH"));
		categoryList.add(new SelectItem("2","ADDED CASH"));
		
		return categoryList;
	}
	public void setCategoryList(List categoryList) {
		this.categoryList = categoryList;
	}
	
	
	public String getTotalCashIn() {
		
		/*PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT *  FROM cashin WHERE dateIn=? ORDER BY countAdded");
		ps.setString(1, getCurrentDate());
		System.out.println("Is exist sql: " + ps.toString());
		rs = ps.executeQuery();
		data = new ArrayList<>();
		Double amnt = 0.0;
		Double cnt = 0.0;
		while(rs.next()){
			amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));
			cnt +=amnt;
		}
		totalCashIn = formatAmount(cnt+"");
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}*/
		
		return totalCashIn;
	}
	public void setTotalCashIn(String totalCashIn) {
		this.totalCashIn = totalCashIn;
	}
	
	
	public List<CashIn> getData() {
		//loadAddedCash();
		Map<Long, CashIn> cash = new TreeMap(Collections.reverseOrder());
		cash.putAll(cashinData);
		
		data = new ArrayList<>();
		int i=1;
		for(CashIn in :  cash.values()){  //cashinData.values()){
			in.setCnt(i++);
			data.add(in);
		}
		
		return data;
	}
	public void setData(List<CashIn> data) {
		this.data = data;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserNotification() {
		return userNotification;
	}
	public void setUserNotification(String userNotification) {
		this.userNotification = userNotification;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getDatein() {
		return datein;
	}
	public void setDatein(String datein) {
		this.datein = datein;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	
	@PostConstruct
	public void init(){
		
			loadAddedCash("init");
	}	
	
	public void loadSearch(){
		String sql = "SELECT * FROM cashin WHERE (dateIn>=? AND dateIn<=?) ";
		String[] params = new String[2];
		
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		
		if(getSearchParam()!=null && !getSearchParam().isEmpty()){
			sql += " AND description like '%" + getSearchParam().replace("--", "") + "%' ";
		}
		
		sql +=" ORDER BY timestamp ASC";
		
		cashinData = Collections.synchronizedMap(new HashMap<Long, CashIn>());
		
		Double amnt = 0.0;
		Double cnt = 0.0;
		for(CashIn in : CashIn.retrieveCashIn(sql, params)){
			try{amnt = Double.valueOf(in.getAmountIn().replace(",", ""));}catch(NumberFormatException e){}
			String cat = in.getCategory()==1? "REM CASH" : "ADDED CASH";
			in.setCategoryName(cat);
			cashinData.put(in.getId(), in);
			cnt +=amnt;
		}
		setTotalCashIn(formatAmount(cnt+""));
		
	}
	
	
	private void loadAddedCash(String type){
		//setCurrentDate(getCurrentDate()==null? null : getCurrentDate().equalsIgnoreCase("")? null : getCurrentDate().isEmpty()? null : getCurrentDate());
		//setDescription(getDescription()==null? null : getDescription().equalsIgnoreCase("")? null : getDescription().isEmpty()? null : getDescription());
		//setAmount(getAmount()==null? null : getAmount().equalsIgnoreCase("")? null : getAmount().isEmpty()? null : getAmount());
		//setCategory(getCategory()==null? null : getCategory().equalsIgnoreCase("")? null : getCategory().isEmpty()? null:getCategory());
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		cashinData = Collections.synchronizedMap(new HashMap<Long, CashIn>());	
		conn = DataConnectDAO.getConnection();
		
			ps = conn.prepareStatement("SELECT *  FROM cashin WHERE dateIn=? ORDER BY timestamp ASC");
			ps.setString(1, DateUtils.getCurrentYYYYMMDD());
		
		rs = ps.executeQuery();
		data = new ArrayList<>();
		Double amnt = 0.0;
		Double cnt = 0.0;
		//long i=1;
		while(rs.next()){
			CashIn in = new CashIn();
			in.setId(rs.getLong("cashinId"));
			in.setDescription(rs.getString("description"));
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){ amnt=0.0;}
			in.setAmountIn(rs.getString("amountIn"));
			in.setDateIn(rs.getString("dateIn"));
			in.setAddedBy(rs.getString("processedBy"));
			in.setCategory(rs.getInt("category"));
			in.setCategoryName(rs.getInt("category")==1? "REM CASH" : "ADDED CASH");
			try{in.setTimestamp(rs.getString("timestamp").replace(".0", ""));}catch(NullPointerException e) {}
			//data.add(in);
			cashinData.put(in.getId(), in);
			cnt +=amnt;
		}
		setTotalCashIn(formatAmount(cnt+""));
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void load(){
		
		loadAddedCash("search");
	}
	
	public String search(){
		loadAddedCash("search");
		return null;
	}
	private String formatAmount(String amount){
		try{
		double money = Double.valueOf(amount);
		NumberFormat format = NumberFormat.getCurrencyInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		}catch(Exception e){
			
		}
		return amount;
	}
	
	public void deleteRow(CashIn in){
		data.remove(in);
		String sql = "DELETE FROM cashin WHERE cashinId=?";
		String[] params = new String[1];
		params[0] = in.getId()+"";
		CashIn.delete(sql, params);
		clearFields();
		loadAddedCash("init");
		Application.addMessage(1, "Success", "Successfully deleted");
	}
	
	public void save(){
		boolean isOk = true;
		if(getCurrentDate()==null){isOk=false;}
		if(getDescription()==null || getDescription().isEmpty()){ isOk=false; Application.addMessage(3, "Error", "Please provide Description");}
		if(getAmount()==null || getAmount().isEmpty()) { isOk=false; Application.addMessage(3, "Error", "Please provide Amount");}
		if(getCategory()==null || getCategory().isEmpty()) { isOk=false; Application.addMessage(3, "Error", "Please provide Category");}
		
		if(isOk){
		setDatein(DateUtils.convertDate(getCurrentDate(), "yyyy-MM-dd"));
		
		try{
		HttpSession session = SessionBean.getSession();
		String proc_by = session.getAttribute("username").toString();
		setAddedBy(proc_by);
		}catch(Exception e){
			
		}
		
		String amnt = "";
		amnt = Currency.removeCurrencySymbol(getAmount(), "");
		amnt = Currency.removeComma(amnt);
		setAmount(amnt);
		
		CashIn in = new CashIn();
		if(getCashIn()!=null){
			in = getCashIn();
		}else{
			in.setDateIn(getDatein());
		}
		in.setDescription(getDescription());
		in.setAmountIn(formatAmount(getAmount()));
		in.setAddedBy(getAddedBy());
		in.setCategory(Integer.valueOf(getCategory()));
		in.save();
		
		clearFields();
		loadAddedCash("init");
		Application.addMessage(1, "Success", "Successfully saved");
		}else{
			Application.addMessage(3, "Error", "Error saving check your values");
		}
		
	}
	
	public void clearFields(){
		System.out.println("Clear method called..");
		setCurrentDate(DateUtils.convertDateString(DateUtils.getCurrentDateYYYYMMDD(),"yyyy-MM-dd"));
		setDescription(null);
		setAmount(null);
		setId(null);
		setCashIn(null);
	}
	public String clickItem(CashIn d){
		setCashIn(d);
		setId(d.getId());
		setDescription(d.getDescription());
		setAmount(d.getAmountIn().replace(",", ""));
		setCategory(d.getCategory()+"");
		setCurrentDate(DateUtils.convertDateString(d.getDateIn(), "yyyy-MM-dd"));
		return null;
	}
private String getCashInInfo(String id){
		System.out.println("method getCashInInfo : " + id);
		boolean isNotNull=false;
		String result ="0";
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestId();	
		if(val==0){
			isNotNull=true;
			result= "1"; // add first data
			System.out.println("First data");
		}
		
		//check if check_no is existing in table
		if(!isNotNull){
			isNotNull = isCheckNoExist(id);
			if(isNotNull){
				result = "2"; // update existing data
				System.out.println("update data");
			}else{
				result = "3"; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
private Date currentDate;
public void setCurrentDate(Date currentDate){
	this.currentDate = currentDate;
}
public Date getCurrentDate(){
	if(currentDate==null){
		currentDate = DateUtils.getDateToday();
	}
	return currentDate;
}

private void insertData(List data, String type){
	String sql = "INSERT INTO cashin ("
			+ "cashinId,"
			+ "description,"
			+ "amountIn,"
			+ "processedBy,"
			+ "dateIn,"
			+ "countAdded,"
			+ "category) " 
			+ "values(?,?,?,?,?,?,?)";
	
	PreparedStatement ps = null;
	ResultSet rs = null;
	Connection conn = null;
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	if("1".equalsIgnoreCase(type)){
		ps.setInt(1, 1);
	}else if("3".equalsIgnoreCase(type)){
		ps.setInt(1, Integer.valueOf(data.get(5).toString()));
	}
	ps.setString(2, data.get(0).toString());
	ps.setString(3, data.get(1).toString());
	ps.setString(4, data.get(2).toString());
	ps.setString(5, data.get(3).toString());
	ps.setString(6, data.get(4).toString());
	ps.setString(7, getCategory());
	System.out.println("SQL ADD : " + ps.toString());
	ps.execute();
	}catch(SQLException e){
		e.printStackTrace();
	}
}
private void updateData(List data){
	String sql = "UPDATE cashin SET "
			+ "description=?,"
			+ "amountIn=?,"
			+ "processedBy=?,"
			+ "dateIn=?,"
			+ "category=?"
			+ " WHERE cashinId=?";

	
	PreparedStatement ps = null;
	ResultSet rs = null;
	Connection conn = null;
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	ps.setString(1, data.get(0).toString());
	ps.setString(2, data.get(1).toString());
	ps.setString(3, data.get(2).toString());
	ps.setString(4, data.get(3).toString());
	ps.setString(5, getCategory());
	ps.setString(6, data.get(4).toString());
	System.out.println("SQL UPDATE : " + ps.toString());
	ps.executeUpdate();
	}catch(SQLException e){
		e.printStackTrace();
	}
}

	private long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT cashinId FROM cashin ORDER BY cashinId DESC LIMIT 1";	
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
		
		//id = id==0 ? 1 : id+1;
		
		return id;
	}
	private boolean isCheckNoExist(String id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT cashinId FROM cashin WHERE cashinId=?");
		ps.setString(1, id);
		System.out.println("Is exist sql: " + ps.toString());
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
	private int getAddedCountAmount(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int result = 0;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT countAdded  FROM cashin WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
		ps.setString(1, DateUtils.convertDate(getCurrentDate(),"yyyy-MM-dd"));
		System.out.println("Is exist sql: " + ps.toString());
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
	private final String sep = File.separator;
	private final String REPORT_PATH = "C:" + sep + "BuyNSell" + sep + "reports" + sep + "generated" + sep;
	private final String REPORT_NAME ="GenericPrint";
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	public String printReport(){
		try{
			//compile the report
			ArrayList<GenericPrint> print = new ArrayList<>();
			
			for(CashIn d : cashinData.values()){
				GenericPrint p = new GenericPrint();
				p.setFld1(d.getCnt()+"");
				p.setFld2(d.getDescription());
				p.setFld3(d.getAmountIn());
				p.setFld4(d.getDateIn());
				//p.setFld5(d.getDatepaying());
				//p.setFld6(d.getIsPaid());
				p.setFld5(d.getAddedBy());
				print.add(p);
			}
			HashMap params = new HashMap<>();
			params.put("PARAM_SUBTITLE", "CASH IN REPORT");
			params.put("PARAM_TOTAL", "Total Cash in: Php" +getTotalCashIn());
			params.put("PARAM_1", "NO");
			params.put("PARAM_2", "DESCRIPTION");
			params.put("PARAM_3", "AMOUNT");
			params.put("PARAM_4", "CASH IN DATE");
			//params.put("PARAM_5", "PAYMENT DATE");
			//params.put("PARAM_6", "IS PAID");
			params.put("PARAM_5", "ADDED BY");
			
			
			GenericReportMain.compileReport(print,params,REPORT_NAME,REPORT_PATH);
				
			FacesContext faces = FacesContext.getCurrentInstance();
			ExternalContext context = faces.getExternalContext();
			HttpServletResponse response = (HttpServletResponse)context.getResponse();
			
			 File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
		     BufferedInputStream input = null;
		     BufferedOutputStream output = null;
			
			
		     try{
		    	 
		    	 // Open file.
		            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);

		            // Init servlet response.
		            response.reset();
		            response.setHeader("Content-Type", "application/pdf");
		            response.setHeader("Content-Length", String.valueOf(file.length()));
		            response.setHeader("Content-Disposition", "inline; filename=\"" + REPORT_NAME + ".pdf" + "\"");
		            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

		            // Write file contents to response.
		            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		            int length;
		            while ((length = input.read(buffer)) > 0) {
		                output.write(buffer, 0, length);
		            }

		            // Finalize task.
		            output.flush();
		    	 
		     }finally{
		    	// Gently close streams.
		            close(output);
		            close(input);
		     }
		     
		     // Inform JSF that it doesn't need to handle response.
		        // This is very important, otherwise you will get the following exception in the logs:
		        // java.lang.IllegalStateException: Cannot forward after response has been committed.
		        faces.responseComplete();
		     
		     
			}catch(Exception ioe){
				ioe.printStackTrace();
			}
		return "print";
	}
	private void close(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException e) {
                // Do your thing with the exception. Print it, log it or mail it. It may be useful to 
                // know that this will generally only be thrown when the client aborted the download.
                e.printStackTrace();
            }
        }
    }
	public CashIn getCashIn() {
		return cashIn;
	}
	public void setCashIn(CashIn cashIn) {
		this.cashIn = cashIn;
	}
	public String getSearchParam() {
		return searchParam;
	}
	public void setSearchParam(String searchParam) {
		this.searchParam = searchParam;
	}
	public Date getCalendarFrom() {
		if(calendarFrom==null){
			calendarFrom = DateUtils.getDateToday();
		}
		return calendarFrom;
	}

	public void setCalendarFrom(Date calendarFrom) {
		this.calendarFrom = calendarFrom;
	}

	public Date getCalendarTo() {
		if(calendarTo==null){
			calendarTo = DateUtils.getDateToday();
		}
		return calendarTo;
	}

	public void setCalendarTo(Date calendarTo) {
		this.calendarTo = calendarTo;
	}
}

















