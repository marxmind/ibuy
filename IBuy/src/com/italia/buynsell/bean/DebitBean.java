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
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.controller.CashIn;
import com.italia.buynsell.controller.Credit;
import com.italia.buynsell.controller.DateChanger;
import com.italia.buynsell.controller.Debit;
import com.italia.buynsell.controller.DebitType;
import com.italia.buynsell.controller.Employee;
import com.italia.buynsell.controller.GenericPrint;
import com.italia.buynsell.controller.GenericReportMain;
import com.italia.buynsell.controller.ReadApplicationDetails;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.ipay.IPayTables;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.LogUserActions;

@ManagedBean (name="debitBean", eager=true)
@ViewScoped
@Deprecated
public class DebitBean {
	private String description;
	private String amount;
	private String datein;
	private String addedBy;
	private String userNotification;
	private Long id;
	private String totalCashIn;
	private List<Debit> data = new ArrayList<>();
	private String isPaid;
	private int datecounting; 
	
	private String datepaying;
	private List isPaidDate = new ArrayList<>();
	
	private List withInterestList = new ArrayList<>();
	private String withInterest;
	private String amntPenalty;
	private List debitTypes = new ArrayList<>(); 
	private String debitType;
	private String employeeId;
	private String employeeOther;
	private List employeeList = new ArrayList<>();
	
	private String tempDescription;
	
	public String getTempDescription() {
		return tempDescription;
	}
	public void setTempDescription(String tempDescription) {
		this.tempDescription = tempDescription;
	}
	public String getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}
	public String getEmployeeOther() {
		return employeeOther;
	}
	public void setEmployeeOther(String employeeOther) {
		this.employeeOther = employeeOther;
	}
	
	public List<String> autoClientName(String query){
		String sql = "SELECT DISTINCT othername from debit WHERE othername like '%" + query + "%'";
		String[] params = new String[0];
		List<String> result = new ArrayList<>();
		for(Debit d : Debit.retrieveDebit(sql, params)){
			result.add(d.getOthername());
		}	
		return result;
	}
	
	public List getEmployeeList() {
		
		employeeList = new ArrayList<>();
		String sql = "SELECT * FROM employeedtls WHERE isActive=1";
		String[] params = new String[0];
		for(Employee e : Employee.retrieveEmployees(sql, params)){
			employeeList.add(new SelectItem(e.getEmpId()+"",e.getFirstName() + " " + e.getMiddleName().substring(0, 1) + ". " + e.getLastName()));
		}
		
		return employeeList;
	}
	public void setEmployeeList(List employeeList) {
		this.employeeList = employeeList;
	}
	public String getDebitType() {
		return debitType;
	}
	public void setDebitType(String debitType) {
		this.debitType = debitType;
	}
	public List getDebitTypes() {
		
		debitTypes = new ArrayList<>();
		String sql = "SELECT * FROM debittype";
		String[] params = new String[0];
		for(DebitType t : DebitType.retrieveDebitType(sql, params)){
			debitTypes.add(new SelectItem(t.getId()+"", t.getDescription()));
		}
		
		return debitTypes;
	}
	public void setDebitTypes(List debitTypes) {
		this.debitTypes = debitTypes;
	}
	public String getAmntPenalty() {
		if(amntPenalty==null){
			amntPenalty = "0";
		}
		return amntPenalty;
	}
	public void setAmntPenalty(String amntPenalty) {
		this.amntPenalty = amntPenalty;
	}
	public List getWithInterestList() {
		
		withInterestList = new ArrayList<>();
		withInterestList.add(new SelectItem("0","NO"));
		withInterestList.add(new SelectItem("1","YES"));
		
		return withInterestList;
	}
	public void setWithInterestList(List withInterestList) {
		this.withInterestList = withInterestList;
	}
	public String getWithInterest() {
		if(withInterest==null){
			withInterest = "0";
		}
		return withInterest;
	}
	public void setWithInterest(String withInterest) {
		this.withInterest = withInterest;
	}
	
	
	Map<Long, Debit> debit = Collections.synchronizedMap(new HashMap<Long, Debit>());
	
	public List getIsPaidDate() {
		
		isPaidDate = new ArrayList<>();
		isPaidDate.add(new SelectItem("0","NO"));
		isPaidDate.add(new SelectItem("1","YES"));
		
		return isPaidDate;
	}
	public void setIsPaidDate(List isPaidDate) {
		this.isPaidDate = isPaidDate;
	}
	
	
	public String getDatepaying() {
		
		if(datepaying==null){
			datepaying = DateUtils.getEndOfMonthDate("MM-dd-yyyy", Locale.TAIWAN);
		}
		
		return datepaying;
	}
	public void setDatepaying(String datepaying) {
		this.datepaying = datepaying;
	}
	public String getIsPaid() {
		if(isPaid==null){
			isPaid = "0";
		}
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
	
	
	public String getTotalCashIn() {
		
		/*PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT *  FROM debit WHERE dateIn=? ORDER BY countAdded");
		ps.setString(1, getCurrentDate());
		System.out.println("Is exist sql: " + ps.toString());
		rs = ps.executeQuery();
		data = new ArrayList<>();
		Double amnt = 0.0;
		Double cnt = 0.0;
		while(rs.next()){
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
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
	
	
	public List<Debit> getData() {
		
		/*Map<Long, Debit> deb = new TreeMap(Collections.reverseOrder());
		deb.putAll(debit);
		data = new ArrayList<>();
		int i=1;
		for(Debit d : deb.values()){
			d.setCnt(i++);
			data.add(d);
		}*/
		
		return data;
	}
	public void setData(List<Debit> data) {
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
		System.out.println("loading....");
		String search = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("search");
		System.out.println("search : " + search);
		if(search!=null){
			setSearchString(search);
		}	
			loadSearch();
		
		DateChanger.runDateChanger();	
			
	}
	
	
	private String searchString;
	
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public void loadSearch(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		debit = Collections.synchronizedMap(new HashMap<Long, Debit>());
		String sql = "SELECT *  FROM debit WHERE isPaid=0 ORDER BY timestamp";
		String[] params= new String[0];
		double amnt = 0d;
		data = new ArrayList<>();
		if(getSearchString()!=null){
			sql = "SELECT *  FROM debit WHERE isPaid=0 AND (description like '%" +getSearchString()+ "%' OR othername like '%"+getSearchString() +"%') ORDER BY timestamp";
		}
			//debit = Collections.synchronizedMap(new HashMap<Long, Debit>());
			
			int cnt = 0;
			List<Debit> debits = Debit.retrieveDebit(sql, params);
			cnt = debits.size();
			for(Debit d : debits){
				d.setCnt(cnt--);
				
				try{amnt += Double.valueOf(d.getAmountIn().replace(",", ""));}catch(Exception e){}
				
				String interest = d.getWithInterest();
				interest = interest==null? "NO" : interest.equalsIgnoreCase("0")? "NO" : interest.equalsIgnoreCase("1")? "YES" : "NO";
				d.setWithInterest(interest);
				
				try{d.setIsPaid(d.getIsPaid().equalsIgnoreCase("1")? "YES" : "NO");}catch(Exception e){d.setIsPaid("NO");}
				
				d.setLendName(d.getOthername()==null? employeName(d.getEmployeeid()<=0? 0 :d.getEmployeeid()) : 
					d.getOthername().equalsIgnoreCase("")? employeName(d.getEmployeeid()<=0? 0 : d.getEmployeeid()) :   d.getOthername());
				
				d.setDebitTypeName(retrieveDebitType(d.getDebittypeId()));
				
				data.add(d);
				debit.put(d.getId(), d);
			}
			
			
		
		setTotalCashIn(formatAmount(amnt+""));
		Collections.reverse(data);
	}
	
	
	private void loadAddedCash(String type){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		//System.out.println("desc:" + getDescription());
		//System.out.println("amount:" + getAmount());
		//System.out.println("paying date:" + getDatepaying());
		//System.out.println("Is Paid:" + getIsPaid());
		
		setCurrentDate(getCurrentDate()==null? null : getCurrentDate().equalsIgnoreCase("")? null : getCurrentDate().isEmpty()? null : getCurrentDate());
		setDescription(getDescription()==null? null : getDescription().equalsIgnoreCase("")? null : getDescription().isEmpty()? null : getDescription());
		setAmount(getAmount()==null? null : getAmount().equalsIgnoreCase("")? null : getAmount().isEmpty()? null : getAmount());
		setDatepaying(getDatepaying()==null? null : getDatepaying().equalsIgnoreCase("")? null : getDatepaying().isEmpty()? null : getDatepaying());
		setIsPaid(getIsPaid()==null? null : getIsPaid().equalsIgnoreCase("")? null : getIsPaid().isEmpty()? null : getIsPaid());
		setWithInterest(getWithInterest()==null? null : getWithInterest().equalsIgnoreCase("")? null : getWithInterest().isEmpty()? null : getWithInterest());
		
		try{
		debit = Collections.synchronizedMap(new HashMap<Long, Debit>());	
		conn = DataConnectDAO.getConnection();
		
		if("init".equalsIgnoreCase(type)){
			ps = conn.prepareStatement("SELECT *  FROM debit WHERE isPaid=0 ORDER BY timestamp");
		}else if("search".equalsIgnoreCase(type)){
			int cnt=1;
			String sql="SELECT *  FROM debit WHERE ";
			boolean isTrue=false;
			if(getDescription()!=null){
				sql = sql + " description like '%" + getDescription() + "%'";
				isTrue=true;
			}
			if(getAmount()!=null){
				if(isTrue){
					sql = sql + " AND amountIn=? ";
				}else{
					sql = sql + " amountIn=? ";
				}
				isTrue=true;
			}
			if(getDatepaying()!=null){
				if(getDatepaying().contains(":")){
					if(isTrue){
						sql = sql + " AND (datepaying>=? AND datepaying<=?) ";
					}else{
						sql = sql + " (datepaying>=? AND datepaying<=?) ";
					}
				}else{
					if(isTrue){
						sql = sql + " AND datepaying=? ";
					}else{
						sql = sql + " datepaying=? ";
					}
				}
				isTrue=true;
			}
			if(getIsPaid()!=null){
				if(isTrue){
					sql = sql + " AND isPaid=? ";
				}else{
					sql = sql + " isPaid=? ";
				}
				isTrue=true;
			}
			if(getCurrentDate()!=null){
				if(getCurrentDate().contains(":")){
					
					if(isTrue){
						sql = sql + " AND (dateIn>=? AND dateIn<=?) ";
					}else{
						sql = sql + " dateIn>=? AND dateIn<=? ";
					}
					
				}else{
					if(isTrue){
						sql = sql + " AND dateIn=? ";
					}else{
						sql = sql + " dateIn=? ";
					}
				}
				isTrue=true;
			}
			
			if(getWithInterest()!=null){
					
					if(isTrue){
						sql = sql + " AND withinterest=? ";
					}else{
						sql = sql + " withinterest=? ";
					}
				
			}
			
			//System.out.println("SQL: " + sql);
			if(getDescription()==null && getAmount()==null && getDatepaying()==null && getIsPaid()==null && getWithInterest()==null){
				ps = conn.prepareStatement("SELECT *  FROM debit WHERE isPaid=0 ORDER BY timestamp");
			}else{
				ps = conn.prepareStatement(sql +" ORDER BY timestamp");
				
				if(getAmount()!=null){
					ps.setString(cnt, getAmount());
					cnt+=1;
				}
				if(getDatepaying()!=null){
					if(getDatepaying().contains(":")){
						int cntSplit = getDatepaying().indexOf(":");
						ps.setString(cnt, getDatepaying().split(":", cntSplit)[0]);
						cnt+=1;
						ps.setString(cnt, getDatepaying().split(":", cntSplit)[1]);
						cnt+=1;
					}else{
						ps.setString(cnt, getDatepaying());
						cnt+=1;
					}
				}
				if(getIsPaid()!=null){
					String paid = getIsPaid()==null? "0" : getIsPaid().isEmpty()? "0" : getIsPaid().equalsIgnoreCase("")? "0" : getIsPaid();
					ps.setString(cnt, paid);
					cnt+=1;
				}
				if(getCurrentDate()!=null){
					if(getCurrentDate().contains(":")){
						int cntSplit = getCurrentDate().indexOf(":");
						ps.setString(cnt, getCurrentDate().split(":", cntSplit)[0]);
						ps.setString(cnt+1, getCurrentDate().split(":", cntSplit)[1]);
						cnt = cnt + 2;
					}else{
						ps.setString(cnt, getCurrentDate());
						cnt+=1;
					}
				}
				if(getWithInterest()!=null){
					String interest = getWithInterest()==null? "0" : getWithInterest().isEmpty()? "0" : getWithInterest().equalsIgnoreCase("")? "0" : getWithInterest();
					ps.setString(cnt, interest);
					cnt+=1;
				}

			}
			
		}
		
		//System.out.println("Is exist sql: " + ps.toString());
		rs = ps.executeQuery();
		//data = new ArrayList<>();
		Double amnt = 0.0;
		Double cnt = 0.0;
		//int i=1;
		while(rs.next()){ 
			Debit in = new Debit();
			in.setId(rs.getLong("debitId"));
			//in.setCnt(i++);
			try{in.setDescription(rs.getString("description"));}catch(Exception e){}
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
			in.setAmountIn(rs.getString("amountIn"));
			in.setDateIn(rs.getString("dateIn"));
			in.setAddedBy(rs.getString("processedBy"));
			try{in.setDatepaying(rs.getString("datepaying"));}catch(Exception e){in.setDatepaying("");}
			try{in.setIsPaid(rs.getString("isPaid").equalsIgnoreCase("1")? "YES" : "NO");}catch(Exception e){in.setIsPaid("NO");}
			try{in.setDatecounting(rs.getInt("datecounting"));}catch(Exception e){in.setDatecounting(0);}
			try{
				String interest = rs.getString("withinterest");
				interest = interest==null? "NO" : interest.equalsIgnoreCase("0")? "NO" : interest.equalsIgnoreCase("1")? "YES" : "NO";
				in.setWithInterest(interest);
			}catch(NullPointerException e){}
			in.setAmntPenalty(rs.getString("amntpenalty"));
			
			try{in.setDebittypeId(rs.getInt("debittypeId"));}catch(Exception e){}
			try{in.setOthername(rs.getString("othername"));}catch(Exception e){}
			try{in.setEmployeeid(rs.getLong("employeeid"));}catch(Exception e){}
			
			in.setLendName(rs.getString("othername")==null? employeName(rs.getLong("employeeid")<=0? 0 : rs.getLong("employeeid")) : 
				rs.getString("othername").equalsIgnoreCase("")? employeName(rs.getLong("employeeid")<=0? 0 : rs.getLong("employeeid")) :   rs.getString("othername"));
			in.setDebitTypeName(retrieveDebitType(rs.getInt("debittypeId")));
			debit.put(in.getId(), in);
			
			//data.add(in);
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
	private String retrieveDebitType(int id){
		String sql = "SELECT * FROM debittype WHERE id=?";
		String[] params = new String[1];
		params[0] = id+"";
		List<DebitType> types = DebitType.retrieveDebitType(sql, params);
		if(types.isEmpty()){
			return "";
		}else{
			return types.get(0).getDescription();
		}
	}
	
	private String employeName(long id){
		String fullName="";
		
		String sql = "SELECT * FROM employeedtls WHERE empId=?";
		String[] params = new String[1];
		params[0] = id+"";
		//System.out.println("SQL : " + sql + " PARAMS = " + params[0]);
		List<Employee> employee = Employee.retrieveEmployees(sql, params);
		if(employee.size()>0){
			Employee e = employee.get(0);
			fullName = e.getFirstName() +" "+ e.getMiddleName()  + " " + e.getLastName(); 
		}
		
		return fullName;
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
	
	public String clickItem(Debit d){
		if(d==null) return null;
		setId(d.getId());
		setDescription(d.getDescription());
		setTempDescription(d.getDescription());
		setAmount(d.getAmountIn());
		setDatepaying(d.getDatepaying());
		setIsPaid(d.getIsPaid().equalsIgnoreCase("YES")? "1" : "0");
		setCurrentDate(d.getDateIn());
		setWithInterest(d.getWithInterest().equalsIgnoreCase("YES")? "1" : "0");
		setAmntPenalty(d.getAmntPenalty());
		setDebitType(d.getDebittypeId()+"");
		setEmployeeId(d.getEmployeeid()+"");
		setEmployeeOther(d.getOthername());
		return null;
	}
	
	public String deleteRow(Debit in){
		if(in==null) return null;
		try{System.out.println("Id Deleted : "+ in.getId() + " isPaid= " + in.getIsPaid());}catch(Exception e){e.printStackTrace(); System.out.println("deleteRow method error: " + e.getMessage());}
		if("NO".equalsIgnoreCase(in.getIsPaid()) && DateUtils.getCurrentYYYYMMDD().equalsIgnoreCase(in.getDateIn()) ){
			
			data.remove(in);
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		//ps = conn.prepareStatement("DELETE FROM debit WHERE debitId=?");
		ps = conn.prepareStatement("UPDATE debit set isPaid=2, description=? WHERE debitId=?");
		ps.setString(1, "(DELETED)" + in.getDescription());
		ps.setLong(2,in.getId());
		//System.out.println("Is exist sql: " + ps.toString());
		ps.executeUpdate();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		String[] value=new String[1];
		value[0] = in.getId()+"";
		saveLendMoney("4", value);
		//loadAddedCash("init");
		loadSearch();
		}
		return null;
	}
	
	private String personName(){
		String desc = "";
		if(getEmployeeId()!=null && !"".equalsIgnoreCase(getEmployeeId()))
			desc = employeName(Long.valueOf(getEmployeeId()));
		if(getEmployeeOther()!=null && !"".equalsIgnoreCase(getEmployeeOther()))
			desc = getEmployeeOther();
		return desc;
	}
	
	public void filDescription(){
		setDescription(retrieveDebitType(Integer.valueOf(getDebitType())) + " " +" " + personName());
	}
	
	public String save(){
		
		setCurrentDate(getCurrentDate()==null? null : getCurrentDate().equalsIgnoreCase("")? null : getCurrentDate().isEmpty()? null : getCurrentDate());
		setDescription(getDescription()==null? null : getDescription().equalsIgnoreCase("")? null : getDescription().isEmpty()? null : getDescription());
		setAmount(getAmount()==null? null : getAmount().equalsIgnoreCase("")? null : getAmount().isEmpty()? null : getAmount());
		setDatepaying(getDatepaying()==null? null : getDatepaying().equalsIgnoreCase("")? null : getDatepaying().isEmpty()? null : getDatepaying());
		setIsPaid(getIsPaid()==null? null : getIsPaid().equalsIgnoreCase("")? null : getIsPaid().isEmpty()? null : getIsPaid());
		setWithInterest(getWithInterest()==null? null : getWithInterest().equalsIgnoreCase("")? null : getWithInterest().isEmpty()? null : getWithInterest());
		
		//System.out.println("id: " + getLatestId());
		//System.out.println("Description: "+ getDescription());
		String id = getCashInInfo( getId()==null? 0+"" : getId()+"");
		
		List data = new ArrayList();
		
		setDatein(getCurrentDate());
		
		try{
		HttpSession session = SessionBean.getSession();
		String proc_by = session.getAttribute("username").toString();
		setAddedBy(proc_by);
		}catch(Exception e){
			return "error";
		}
		
		String amnt = "";
		amnt = Currency.removeCurrencySymbol(getAmount(), "");
		amnt = Currency.removeComma(amnt);
		setAmount(amnt);
		data.add(getDescription());
		data.add(formatAmount(getAmount()));
		data.add(getAddedBy());
		data.add(getDatein());
		boolean isTrue = false;
		int counter=0;
		String note = "";
		if(getCurrentDate()==null){
			note += "Loan Date ";
			isTrue=true;
			counter=counter+1;
		}
		
		if(getDescription()==null){
			if(counter>=1){
				note += ",Description ";
			}else{
				note += "Description";
			}
			isTrue=true;
			counter=counter+1;
		}
		if(getAmount()==null){
			if(counter>=1){
				note += ",Amount In";
			}else{
				note += "Amount In";
			}
			isTrue=true;
			counter=counter+1;
		}
		if(getDatepaying()==null){
			if(counter>=1){
				note += ",Date Paying";
			}else{
				note += "Date Paying";
			}
			
			isTrue=true;
			counter=counter+1;
		}
		if(getIsPaid()==null){
			if(counter>=1){
				note += ",Is Paid";
			}else{
				note += "Is Paid";
			}
			
			isTrue=true;
			counter=counter+1;
		}
		if(getWithInterest()==null){
			if(counter>=1){
				note += ",With Interest";
			}else{
				note += "With Interest";
			}
			
			isTrue=true;
			counter=counter+1;
		}
		
		
		
		if(getDebitType()==null || "".equalsIgnoreCase(getDebitType())){
			
			if(counter>=1){
				note += ",Debit Type";
			}else{
				note += "Debit Type";
			}
			counter=counter+1;
			isTrue = true;
		}
		
		if((getEmployeeId()==null && getEmployeeOther()==null) || ("".equalsIgnoreCase(getEmployeeId()) && "".equalsIgnoreCase(getEmployeeOther()))){
			
			if(counter>=1){
				note += ",Lender Name";
			}else{
				note += "Lender Name";
			}
			counter=counter+1;
			isTrue = true;
		}
		
		
		//System.out.println("counter: " + counter);
		if(counter>1){
			note += " are empty";
		}else{
			note += " is empty";
		}
		
		if((getEmployeeId()!=null && !"".equalsIgnoreCase(getEmployeeId()) ) && (getEmployeeOther()!=null && !"".equalsIgnoreCase(getEmployeeOther()))){
			
			if(counter>=1){
				note += " And please select only one Lender Name";
			}else{
				note = "Please select only one Lender Name";
			}
			counter=counter+1;
			isTrue = true;
		}
		
		if(isTrue){
			
			if(getWithInterest()!=null && getWithInterest().equalsIgnoreCase("2")){
				//setUserNotification(note + "and please provide amount for Amnt Penalty.");
				addMessage(note + "and please provide amount for Amnt Penalty.");
			}else{
				setUserNotification(note);
			}
			return "error";
		}
		
		int cnt = getAddedCountAmount()==0? 1 : getAddedCountAmount()+1; 
		long idDebit=0l;
		if(id.equalsIgnoreCase("1")){
			//insert first data
			idDebit =getLatestId(); 
			data.add(cnt);
			data.add(idDebit);
			insertData(data, "1");
			addMessage("Cash Advance information has been save.");
		}else if(id.equalsIgnoreCase("2")){
			//update data
			idDebit = getId();
			if(getId()!=null){
			data.add(idDebit);
			updateData(data);
			addMessage("Cash Advance information has been updated.");
			}
		}else if(id.equalsIgnoreCase("3")){
			//add new data
			idDebit = getLatestId()+1;
			data.add(cnt);
			data.add(idDebit);
			insertData(data, "3");
			addMessage("Cash Advance information has been save.");
		}
		
		if(getEmployeeId()!=null && !"".equalsIgnoreCase(getEmployeeId())){
			String[] value = new String[2]; 
			value[0] = getEmployeeId();
			value[1] = idDebit+"";
			saveLendMoney(id,value);
		}
		
		clearFields();
		//loadAddedCash("init");
		loadSearch();
		return "save";
	}
	
	private void saveLendMoney(String type, String value[]){
		
		if(!"4".equalsIgnoreCase(type)){
			String data[] = new String[5];
			data[0] = value[0];
			data[1] = value[1];
			data[2] = DateUtils.getCurrentYYYYMMDD();
			
			if(getIsPaid().equalsIgnoreCase("1")){
				data[3] = "pay";
			}else{
				data[3] = "lend";
			}
			
			Double amnt = 0.00;
			try{amnt = Double.valueOf(Currency.removeComma(Currency.removeCurrencySymbol(getAmount(), "")));}catch(NumberFormatException num){}
			data[4] = amnt+"";
			 
			//IPayTables.process("save", "lendmoney", data, new String(), new String[0]);
			
		}else{
			String data[] = new String[1];
			data[0] = value[0];
			//System.out.println("Deleting... " + data[0]);
			//IPayTables.process("delete", "lendmoney", data, new String(), new String[0]);
		}
		
		/**
		 * Type
		 * value = 1 = new data
		 * value = 2 = update data
		 * value = 3 = added data
		 * value = 4 = deleted data
		 */
		
		/*String sql = "SELECT * FROM lendmoney WHERE ";
		int size = 1;
		String[] params = new String[size];
		if(!"4".equalsIgnoreCase(type)){
			size=2;
			sql += "empid=? AND debitId=?";
			params = new String[size];
			params[0] = value[0];
			params[1] = value[1];
		}else{
			params = new String[size];
			sql += "debitId=?";
			params[0] = value[0];
		}
		List<LendMoney> lends = LendMoney.retrieveLendMoney(sql, params);
		
		LendMoney lend = new LendMoney();
		if(lends.size()>0){
			lend = lends.get(0);
		}else{
			Employee e = new Employee();
			e.setEmpId(Long.valueOf(value[0]));
			lend.setEmployee(e);
			Debit debit = new Debit();
			debit.setId(Long.valueOf(value[1]));
			lend.setDebit(debit);
		}
		
		
		lend.setDateIn(getCurrentDate());
		
		if("4".equalsIgnoreCase(type)){
			lend.setDescription("deleted");
		}else{
			if(getIsPaid().equalsIgnoreCase("1")){
				lend.setDescription("pay");
			}else{
				lend.setDescription("lend");
			}
			lend.setIsPaid(false);
			Double amnt = 0.00;
			try{amnt = Double.valueOf(Currency.removeComma(Currency.removeCurrencySymbol(getAmount(), "")));}catch(NumberFormatException num){}
			lend.setAmountIn(new BigDecimal(amnt));
		}
		System.out.println("saving......");
		LendMoney.save(lend);*/
	}
	
	private void clearFields(){
		System.out.println("Clear method called..");
		setDescription(null);
		setAmount(null);
		setDatepaying(null);
		setIsPaid(null);
		setId(null);
		setCurrentDate(null);
		setCurrentDate(getCurrentDate());
		setWithInterest(null);
		setDebitType(null);
		setEmployeeOther(null);
		setEmployeeId(null);
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
			//System.out.println("First data");
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
private String currentDate;
public String getCurrentDate(){//MMMM d, yyyy
	DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");//new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
	Date date = new Date();
	String _date = dateFormat.format(date);
	if(currentDate!=null)
		_date = currentDate;
	return _date;
}
public void setCurrentDate(String currentDate){
	this.currentDate = currentDate;
}

private void insertData(List data, String type){
	String sql = "INSERT INTO debit ("
			+ "debitId,"
			+ "description,"
			+ "amountIn,"
			+ "processedBy,"
			+ "dateIn,"
			+ "countAdded,"
			+ "datepaying,"
			+ "isPaid,"
			+"withinterest,"
			+ "amntpenalty,"
			+ "debittypeId,"
			+ "othername,"
			+ "employeeid) " 
			+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	PreparedStatement ps = null;
	ResultSet rs = null;
	Connection conn = null;
	List<String> actions = new ArrayList<>();
	actions.add("Inserting data on debit table");
	actions.add("SQL : " + sql);
	try{
	conn = DataConnectDAO.getConnection();
	ps = conn.prepareStatement(sql);
	if("1".equalsIgnoreCase(type)){
		ps.setInt(1, 1);
		actions.add("id : 1");
	}else if("3".equalsIgnoreCase(type)){
		ps.setInt(1, Integer.valueOf(data.get(5).toString()));
		actions.add("id : " + data.get(5).toString());
	}
	ps.setString(2, data.get(0).toString());
	ps.setString(3, data.get(1).toString());
	ps.setString(4, data.get(2).toString());
	ps.setString(5, data.get(3).toString());
	ps.setString(6, data.get(4).toString());
	ps.setString(7, getDatepaying());
	ps.setString(8, getIsPaid()==null? "0" : getIsPaid().isEmpty()? "0" : getIsPaid().equalsIgnoreCase("")? "0" : getIsPaid());
	ps.setString(9, getWithInterest()==null? "0" : getWithInterest().equalsIgnoreCase("")? "0" : getWithInterest().isEmpty()? "0" : getWithInterest());
	ps.setString(10, getAmntPenalty());
	ps.setString(11, getDebitType()==null? null : getDebitType().equalsIgnoreCase("")? null : getDebitType());
	ps.setString(12, getEmployeeOther()==null? null : getEmployeeOther().equalsIgnoreCase("")? null : getEmployeeOther());
	ps.setString(13, getEmployeeId()==null? null : getEmployeeId().equalsIgnoreCase("")? null : getEmployeeId());
	
	for(int i=0;i<5;i++){
		actions.add(data.get(i).toString());
	}
	actions.add(getDatepaying());
	actions.add(getIsPaid()==null? "0" : getIsPaid().isEmpty()? "0" : getIsPaid().equalsIgnoreCase("")? "0" : getIsPaid());
	actions.add(getWithInterest()==null? "0" : getWithInterest().equalsIgnoreCase("")? "0" : getWithInterest().isEmpty()? "0" : getWithInterest());
	actions.add(getAmntPenalty());
	actions.add(getDebitType()==null? null : getDebitType().equalsIgnoreCase("")? null : getDebitType());
	actions.add(getEmployeeOther()==null? null : getEmployeeOther().equalsIgnoreCase("")? null : getEmployeeOther());
	actions.add(getEmployeeId()==null? null : getEmployeeId().equalsIgnoreCase("")? null : getEmployeeId());
	actions.add("Executing...");
	ps.execute();
	actions.add("Closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("Data has been successfully saved.");
	}catch(SQLException e){
		actions.add("Error in saving : " + e.getMessage());
	}
	LogUserActions.logUserActions(actions);
}
private void updateData(List data){
	
	List<String> actions = new ArrayList<>();
	actions.add("updating data on debit table");
	
	PreparedStatement ps = null;
	ResultSet rs = null;
	Connection conn = null;
	String sql = "";
	String isPaid = getIsPaid()==null? "0" : getIsPaid().isEmpty()? "0" : getIsPaid().equalsIgnoreCase("")? "0" : getIsPaid();
	String interest = getWithInterest()==null? "0" : getWithInterest().equalsIgnoreCase("")? "0" : getWithInterest().isEmpty()? "0" : getWithInterest();
	ReadApplicationDetails db = new ReadApplicationDetails();
	try{
		
	if(isPaid.equalsIgnoreCase("1") &&  "1".equalsIgnoreCase(getDebitType())){
			
			
			if("yes".equalsIgnoreCase(db.getIncludeIpay())){
				//Ipay application will trigger to settle employee's paying cash advance
				System.out.println("paying employee through IPay application .... ");
				actions.add("paying employee through IPay application .... ");
			}else{
				
				System.out.println("paid employee .... ");
				
				sql = "UPDATE debit SET "
						+ "description=?,"
						+ "amountIn=?,"
						+ "processedBy=?,"
						+ "datepaying=?,"
						+ "isPaid=?,"
						+ "withinterest=?,"
						+ "amntpenalty=?,"
						+ "debittypeId=?,"
						+ "othername =?,"
						+ "employeeid=?"
						+ " WHERE debitId=?";

				
				
				actions.add("SQL : " + sql);
				conn = DataConnectDAO.getConnection();
				ps = conn.prepareStatement(sql);
				ps.setString(1, data.get(0).toString());
				ps.setString(2, data.get(1).toString());
				ps.setString(3, data.get(2).toString());
				
				actions.add(data.get(0).toString());
				actions.add(data.get(1).toString());
				actions.add(data.get(0).toString());
				
				if(isPaid.equalsIgnoreCase("1")){
					DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
					Date date = new Date();
					String _date = dateFormat.format(date);
					ps.setString(4, _date);
					actions.add("date : " + _date);
				}else{
					ps.setString(4, getDatepaying());
					actions.add("datePaying : " + getDatepaying());
				}
				
				ps.setString(5, isPaid);
				ps.setString(6, interest);
				ps.setString(7, getAmntPenalty());
				
				ps.setString(8, getDebitType()==null? null : getDebitType().equalsIgnoreCase("")? null : getDebitType());
				ps.setString(9, getEmployeeOther()==null? null : getEmployeeOther().equalsIgnoreCase("")? null : getEmployeeOther());
				ps.setString(10, getEmployeeId()==null? null : getEmployeeId().equalsIgnoreCase("")? null : getEmployeeId());
				
				ps.setString(11, data.get(4).toString());
				
				actions.add(isPaid);
				actions.add(interest);
				actions.add(getAmntPenalty());
				actions.add(getDebitType()==null? null : getDebitType().equalsIgnoreCase("")? null : getDebitType());
				actions.add(getEmployeeOther()==null? null : getEmployeeOther().equalsIgnoreCase("")? null : getEmployeeOther());
				actions.add(getEmployeeId()==null? null : getEmployeeId().equalsIgnoreCase("")? null : getEmployeeId());
				actions.add(data.get(4).toString());
				actions.add("executing...");
				ps.executeUpdate();
				
			}
			
		
	}else{
	
		
		sql = "UPDATE debit SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?,"
				+ "datepaying=?,"
				+ "isPaid=?,"
				+ "withinterest=?,"
				+ "amntpenalty=?,"
				+ "debittypeId=?,"
				+ "othername =?,"
				+ "employeeid=?"
				+ " WHERE debitId=?";

		
		
		actions.add("SQL : " + sql);
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, data.get(0).toString());
		ps.setString(2, data.get(1).toString());
		ps.setString(3, data.get(2).toString());
		
		actions.add(data.get(0).toString());
		actions.add(data.get(1).toString());
		actions.add(data.get(0).toString());
		
		if(isPaid.equalsIgnoreCase("1")){
			DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			Date date = new Date();
			String _date = dateFormat.format(date);
			ps.setString(4, _date);
			actions.add("date : " + _date);
		}else{
			ps.setString(4, getDatepaying());
			actions.add("datePaying : " + getDatepaying());
		}
		
		ps.setString(5, isPaid);
		ps.setString(6, interest);
		ps.setString(7, getAmntPenalty());
		
		ps.setString(8, getDebitType()==null? null : getDebitType().equalsIgnoreCase("")? null : getDebitType());
		ps.setString(9, getEmployeeOther()==null? null : getEmployeeOther().equalsIgnoreCase("")? null : getEmployeeOther());
		ps.setString(10, getEmployeeId()==null? null : getEmployeeId().equalsIgnoreCase("")? null : getEmployeeId());
		
		ps.setString(11, data.get(4).toString());
		
		actions.add(isPaid);
		actions.add(interest);
		actions.add(getAmntPenalty());
		actions.add(getDebitType()==null? null : getDebitType().equalsIgnoreCase("")? null : getDebitType());
		actions.add(getEmployeeOther()==null? null : getEmployeeOther().equalsIgnoreCase("")? null : getEmployeeOther());
		actions.add(getEmployeeId()==null? null : getEmployeeId().equalsIgnoreCase("")? null : getEmployeeId());
		actions.add(data.get(4).toString());
		actions.add("executing...");
		
		ps.executeUpdate();
		
		
	}
	
	
	
	if(isPaid.equalsIgnoreCase("1") && "0".equalsIgnoreCase(interest)){
		
		if("1".equalsIgnoreCase(getDebitType())){
			
			if("yes".equalsIgnoreCase(db.getIncludeIpay())){
				System.out.println("process to ipay...");
			}else{
				System.out.println("process not ipay...");
				sql = "INSERT INTO cashin ("
						+ "cashinId,"
						+ "description,"
						+ "amountIn,"
						+ "processedBy,"
						+ "dateIn,"
						+ "countAdded,"
						+ "category,"
						+ "debitId) " 
						+ "values(?,?,?,?,?,?,?,?)";
				actions.add("SQL : " + sql);
				conn = DataConnectDAO.getConnection();
				ps = conn.prepareStatement(sql);
				long cashId = getLatestIdCashin()+1;
				ps.setLong(1, cashId);
				ps.setString(2, lenderName() + " paid from Debit. " + data.get(0).toString());
				ps.setString(3, data.get(1).toString());
				ps.setString(4, data.get(2).toString());
				DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
				Date date = new Date();
				String _date = dateFormat.format(date);
				ps.setString(5, _date);
				int cnt = getAddedCountCashin()==0? 1 : getAddedCountCashin()+1;
				ps.setInt(6, cnt);
				ps.setString(7, "2"); //- 2 meaning added cash
				ps.setString(8, data.get(4).toString());
				
				actions.add("cashId : " + cashId);
				actions.add("Description : " + lenderName() + " paid from Debit. " + data.get(0).toString());
				actions.add("Amount In : " + data.get(1).toString());
				actions.add("Process by : " + data.get(2).toString());
				
				actions.add("Date : " + _date);
				actions.add("Count added : " + cnt);
				actions.add("Category : Added Cash");
				actions.add("Debit Id : " + data.get(4).toString());
				actions.add("Executing....");
				ps.execute();
			}
			
			
		}else{
		System.out.println("process not ipay...");
		sql = "INSERT INTO cashin ("
				+ "cashinId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "category,"
				+ "debitId) " 
				+ "values(?,?,?,?,?,?,?,?)";
		actions.add("SQL : " + sql);
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long cashId = getLatestIdCashin()+1;
		ps.setLong(1, cashId);
		ps.setString(2, lenderName() + " paid from Debit. " + data.get(0).toString());
		ps.setString(3, data.get(1).toString());
		ps.setString(4, data.get(2).toString());
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date = new Date();
		String _date = dateFormat.format(date);
		ps.setString(5, _date);
		int cnt = getAddedCountCashin()==0? 1 : getAddedCountCashin()+1;
		ps.setInt(6, cnt);
		ps.setString(7, "2"); //- 2 meaning added cash
		ps.setString(8, data.get(4).toString());
		
		actions.add("cashId : " + cashId);
		actions.add("Description : " + lenderName() + " paid from Debit. " + data.get(0).toString());
		actions.add("Amount In : " + data.get(1).toString());
		actions.add("Process by : " + data.get(2).toString());
		
		actions.add("Date : " + _date);
		actions.add("Count added : " + cnt);
		actions.add("Category : Added Cash");
		actions.add("Debit Id : " + data.get(4).toString());
		actions.add("Executing....");
		
		ps.execute();
		
		}
		
	}else if(isPaid.equalsIgnoreCase("0") && "0".equalsIgnoreCase(interest)){
		String sqls = "DELETE FROM cashin WHERE debitId=?";
		String[] params = new String[1];
		params[0] =  data.get(4).toString();
		actions.add("id : " + params[0]);
		CashIn.delete(sqls, params);
	
	}else if(isPaid.equalsIgnoreCase("1") && "1".equalsIgnoreCase(interest)){
		sql = "INSERT INTO cashin ("
				+ "cashinId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "category,"
				+ "debitId) " 
				+ "values(?,?,?,?,?,?,?,?)";
		try{
		actions.add("SQL : " + sql);	
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long cashId = getLatestIdCashin()+1;
		ps.setLong(1, cashId);
		ps.setString(2, lenderName() + " paid from Debit. " + data.get(0).toString());
		ps.setString(3, data.get(1).toString());
		ps.setString(4, data.get(2).toString());
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date = new Date();
		String _date = dateFormat.format(date);
		ps.setString(5, _date);
		int cnt = getAddedCountCashin()==0? 1 : getAddedCountCashin()+1;
		ps.setInt(6, cnt);
		ps.setString(7, "2");
		ps.setString(8, data.get(4).toString());


		actions.add("cashId : " + cashId);
		actions.add("Description : " + lenderName() + " paid from Debit. " + data.get(0).toString());
		actions.add("Amount In : " + data.get(1).toString());
		actions.add("Process by : " + data.get(2).toString());
		
		actions.add("Date : " + _date);
		actions.add("Count added : " + cnt);
		actions.add("Category : Added Cash");
		actions.add("Debit Id : " + data.get(4).toString());
		actions.add("Executing....");
		
		ps.execute();
		
		String penalty = getAmntPenalty()==null? "0" : getAmntPenalty();
		
		if(!"0".equalsIgnoreCase(penalty)){
		sql = "INSERT INTO credit ("
				+ "creditId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded,"
				+ "debitId) " 
				+ "values(?,?,?,?,?,?,?)";
		actions.add("SQL : " + sql);
		PreparedStatement ps1 = null;
		ps1 = conn.prepareStatement(sql);
		long creditId = getLatestIdCredit();
		ps1.setLong(1, creditId);
		ps1.setString(2, lenderName() + " " + "Penalty paid from Debit. " + data.get(0).toString());
		ps1.setString(3, penalty);
		ps1.setString(4, data.get(2).toString());
		//DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		///Date date = new Date();
		//String _date = dateFormat.format(date);
		ps1.setString(5, _date);
		int cnt1 = getCreditCountAmount()==0? 1 : getCreditCountAmount()+1;
		ps1.setString(6, cnt1+"");
		//System.out.println("Parameter 7 " + data.get(4).toString());
		ps1.setString(7, data.get(4).toString());
		
		actions.add("credit : " + creditId);
		actions.add("Description : " + lenderName() + " Penalty paid from Debit. " + data.get(0).toString());
		actions.add("Penalty : " + penalty);
		actions.add("Process by : " + data.get(2).toString());
		
		actions.add("Date : " + _date);
		actions.add("Count added : " + cnt1);
		actions.add("Debit Id : " + data.get(4).toString());
		actions.add("Executing....");
		
		ps1.execute();
		ps1.close();
		
		}
		
		}catch(SQLException e){
			e.printStackTrace();
		}
	}else if(isPaid.equalsIgnoreCase("0") && "1".equalsIgnoreCase(interest)){
		String sqls = "DELETE FROM cashin WHERE debitId=?";
		String[] params = new String[1];
		params[0] =  data.get(4).toString();
		CashIn.delete(sqls, params);
		
		String penalty = getAmntPenalty()==null? "0" : getAmntPenalty();
		if(!"0".equalsIgnoreCase(penalty)){
			sqls = "DELETE FROM credit WHERE debitId=?";
			params = new String[1];
			params[0] =  data.get(4).toString();
			Credit.delete(sqls, params);
			actions.add("SQL : " + sqls);
			actions.add("id : " + params[0]);
		}
		
	}
	actions.add("Closing...");
	ps.close();
	DataConnectDAO.close(conn);
	actions.add("Data has been successfull updated");
	}catch(SQLException e){
		actions.add("Error in update : " + e.getMessage());
	}
	LogUserActions.logUserActions(actions);
}

private String lenderName(){
	String desc = "";
	if(getEmployeeId()!=null && !"".equalsIgnoreCase(getEmployeeId()))
		desc = employeName(Long.valueOf(getEmployeeId())) + " lended  for " + retrieveDebitType(Integer.valueOf(getDebitType()));
	if(getEmployeeOther()!=null && !"".equalsIgnoreCase(getEmployeeOther()))
		desc = getEmployeeOther() + " lended  for " + retrieveDebitType(Integer.valueOf(getDebitType()));
	return desc;
}

private long getLatestIdCashin(){
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
private long getLatestIdCredit(){
	long id =0;
	Connection conn = null;
	PreparedStatement prep = null;
	ResultSet rs = null;
	String sql = "";
	try{
	sql="SELECT creditId FROM credit ORDER BY creditId DESC LIMIT 1";	
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
	
	id = id==0 ? 1 : id+1;
	
	return id;
}

	private long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT debitId FROM debit ORDER BY debitId DESC LIMIT 1";	
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
		ps = conn.prepareStatement("SELECT debitId FROM debit WHERE debitId=?");
		ps.setString(1, id);
		//System.out.println("Is exist sql: " + ps.toString());
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
		ps = conn.prepareStatement("SELECT countAdded  FROM debit WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
		ps.setString(1, getCurrentDate());
		//System.out.println("Is exist sql: " + ps.toString());
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
	
	private int getAddedCountCashin(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int result = 0;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT countAdded  FROM cashin WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
		ps.setString(1, getCurrentDate());
		//System.out.println("Is exist sql: " + ps.toString());
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
	
	private int getCreditCountAmount(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		int result = 0;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT countAdded  FROM credit WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
		ps.setString(1, getCurrentDate());
		//System.out.println("Is exist sql: " + ps.toString());
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
	
	public String search(){
		loadAddedCash("search");
		return null;
	}
	
	private final String sep = File.separator;
	private final String REPORT_PATH = "C:" + sep + "BuyNSell" + sep + "reports" + sep + "generated" + sep;
	private final String REPORT_NAME ="GenericPrint";
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	public String printReport(){
		try{
			//compile the report
			ArrayList<GenericPrint> print = new ArrayList<>();
			
			Map<Long, Debit> deb = new TreeMap(Collections.reverseOrder());
			deb.putAll(debit);
			
			for(Debit d : deb.values()){
				GenericPrint p = new GenericPrint();
				String addedDetails = "";
					try{addedDetails = d.getOthername()==null? "" : d.getOthername();}catch(Exception e){}
				p.setFld1(d.getCnt()+"");
				p.setFld2(d.getDescription() + " " + addedDetails);
				p.setFld3(d.getAmountIn());
				p.setFld4(d.getDateIn());
				p.setFld5(d.getDatepaying());
				p.setFld6(d.getIsPaid());
				p.setFld7(d.getAddedBy());
				print.add(p);
			}
			HashMap params = new HashMap<>();
			params.put("PARAM_SUBTITLE", "DEBIT REPORT");
			params.put("PARAM_TOTAL", "Total Debit: Php" +getTotalCashIn());
			params.put("PARAM_1", "NO");
			params.put("PARAM_2", "DESCRIPTION");
			params.put("PARAM_3", "AMOUNT");
			params.put("PARAM_4", "LOAN DATE");
			params.put("PARAM_5", "PAYMENT DATE");
			params.put("PARAM_6", "IS PAID");
			params.put("PARAM_7", "ADDED BY");
			
			
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
	public void addMessage(String summary) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}





















