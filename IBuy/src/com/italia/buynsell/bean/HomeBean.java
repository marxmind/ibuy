package com.italia.buynsell.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
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

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.controller.CashIn;
import com.italia.buynsell.controller.ClientProfile;
import com.italia.buynsell.controller.ClientTransactions;
import com.italia.buynsell.controller.CornConditions;
import com.italia.buynsell.controller.Credit;
import com.italia.buynsell.controller.Debit;
import com.italia.buynsell.controller.Expenses;
import com.italia.buynsell.controller.Purchased;
import com.italia.buynsell.controller.PurchasingCorn;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.ei.EICashOut;
import com.italia.buynsell.ei.EICashPay;
import com.italia.buynsell.ei.EICashReturn;
import com.italia.buynsell.reports.ReportCompiler;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.Fields;
import com.italia.buynsell.utils.Numbers;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;


@Named("homeBean")
@ViewScoped
public class HomeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 54696436476861L;
	private final String sep = File.separator;
	private final String REPORT_PATH = "C:" + sep + "BuyNSell" + sep + "reports" + sep + "generated" + sep;
	private final String REPORT_NAME = "Summary";
	private String todaysFund;
	private String todaysFundDis;
	private boolean checkSummary = true;
	
	public String getTodaysFundDis() {
		return todaysFundDis;
	}
	public void setTodaysFundDis(String todaysFundDis) {
		this.todaysFundDis = todaysFundDis;
	}
		
	private String cornPriceWhite ="0.00";
	private String corngrandTotalWhite ="0.00";
	private float cornkiloWhite=0;
	private float corndiscountWhite=0;
	private String corndriverWhite="0.00";
	
	private String cornPriceYellow ="0.00";
	private String corngrandTotalYellow ="0.00";
	private float cornkiloYellow=0;
	private float corndiscountYellow=0;
	private String corndriverYellow="0.00";
	
	private String totalWYCorn="0.00";
	
	private Map<Long, PurchasingCorn> allCorn = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
	
	private String remAmountCash="0.00";
	private String rentedAmounts;
	private String mortgageAmounts;
	private String loanAmounts;
	private String trackingAmounts;
	private String totalclientcashpaidthesamedate;
	private String totalCashadvance;
	
	private String shellerAmounts;
	private String haulingAmounts;
	
	
	private String depositPaid;
	private String depositUnpaid;
	private String depositReturn;
	
	private String purchased="0.00";
	private String expenses="0.00";
	private String credit="0.00";
	private String debit="0.00";
	
	private String borrowed;
	private String sales;
	
	private String basedInvestment;
	private String expensesAmnt;
	private String incomes;
	private String boughtItems;
	private String cashOnHand;
	
	private List<Debit> data = new ArrayList<>();
	Map<Long, Debit> debitData = Collections.synchronizedMap(new HashMap<Long, Debit>());
	
	private Map<Long, PurchasingCorn> summaryTotal = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
	private List<PurchasingCorn> summary = new ArrayList<PurchasingCorn>();
	
	private List<Fields> funds = new ArrayList<Fields>();
	
	private Date calendarFrom;
	private Date calendarTo;
	
	private String summaryInfo;
	
	public List<Fields> getFunds() {
		
		return funds;
	}
	public void setFunds(List<Fields> funds) {
		this.funds = funds;
	}

	private String sumdetailed;
	private List sumdetailedList = new ArrayList<>();
	
	private String amountReceivable;
	
	public String getAmountReceivable() {
		return amountReceivable;
	}
	public void setAmountReceivable(String amountReceivable) {
		this.amountReceivable = amountReceivable;
	}
	public String getSumdetailed() {
		if(sumdetailed==null || sumdetailed.isEmpty()){
			sumdetailed = "0";
		}
		return sumdetailed;
	}
	public void setSumdetailed(String sumdetailed) {
		this.sumdetailed = sumdetailed;
	}
	public List getSumdetailedList() {
		
		sumdetailedList = new ArrayList<>();
		sumdetailedList.add(new SelectItem("0","SUMMARY"));
		sumdetailedList.add(new SelectItem("1","DETAILED"));
		
		return sumdetailedList;
	}
	public void setSumdetailedList(List sumdetailedList) {
		this.sumdetailedList = sumdetailedList;
	}
	public List<Debit> getData() {
		//loadAddedCash();
		data = new ArrayList<>();
		int i=1;
		for(Debit d : debitData.values()){
			d.setCnt(i++);
			data.add(d);
			//System.out.println(" debit " + d.getDescription());
		}
		
		return data;
	}
	public void setData(List<Debit> data) {
		this.data = data;
	}
	
	
	public String getPurchased() {
		return purchased;
	}

	public void setPurchased(String purchased) {
		this.purchased = purchased;
	}

	public String getExpenses() {
		return expenses;
	}

	public void setExpenses(String expenses) {
		this.expenses = expenses;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getDebit() {
		return debit;
	}

	public void setDebit(String debit) {
		this.debit = debit;
	}
	
	public String getRemAmountCash() {
		return remAmountCash;
	}

	public void setRemAmountCash(String remAmountCash) {
		this.remAmountCash = remAmountCash;
	}

	public String getCashin() {
		return cashin;
	}

	public void setCashin(String cashin) {
		this.cashin = cashin;
	}

	private String cashin;
	
	
	public String getCorngrandTotalWhite() {
		return corngrandTotalWhite;
	}

	public void setCorngrandTotalWhite(String corngrandTotalWhite) {
		this.corngrandTotalWhite = corngrandTotalWhite;
	}

	public float getCornkiloWhite() {
		return cornkiloWhite;
	}

	public void setCornkiloWhite(float cornkiloWhite) {
		this.cornkiloWhite = cornkiloWhite;
	}

	public float getCorndiscountWhite() {
		return corndiscountWhite;
	}

	public void setCorndiscountWhite(float corndiscountWhite) {
		this.corndiscountWhite = corndiscountWhite;
	}

	public String getCornDriverWhite() {
		return corndriverWhite;
	}

	public void setCornDriverWhite(String corndriverWhite) {
		this.corndriverWhite = corndriverWhite;
	}

	public String getCorngrandTotalYellow() {
		return corngrandTotalYellow;
	}

	public void setCorngrandTotalYellow(String corngrandTotalYellow) {
		this.corngrandTotalYellow = corngrandTotalYellow;
	}

	public float getCornkiloYellow() {
		return cornkiloYellow;
	}

	public void setCornkiloYellow(float cornkiloYellow) {
		this.cornkiloYellow = cornkiloYellow;
	}

	public float getCorndiscountYellow() {
		return corndiscountYellow;
	}

	public void setCorndiscountYellow(float corndiscountYellow) {
		this.corndiscountYellow = corndiscountYellow;
	}

	public String getCornDriverYellow() {
		return corndriverYellow;
	}

	public void setCornDriverYellow(String corndriverYellow) {
		this.corndriverYellow = corndriverYellow;
	}

	
	
	public String getTotalWYCorn() {
		return totalWYCorn;
	}

	public void setTotalWYCorn(String totalWYCorn) {
		this.totalWYCorn = totalWYCorn;
	}

	public String getCornPriceYellow() {
		return cornPriceYellow;
	}

	public void setCornPriceYellow(String cornPriceYellow) {
		this.cornPriceYellow = cornPriceYellow;
	}

	public String getCornPriceWhite() {
		return cornPriceWhite;
	}

	public void setCornPriceWhite(String cornPriceWhite) {
		this.cornPriceWhite = cornPriceWhite;
	}
	
	public String getTodaysFund() {
		return todaysFund;
	}

	public void setTodaysFund(String todaysFund) {
		this.todaysFund = todaysFund;
	}

	private void loadAddedCash(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		boolean isDateRange=false;
		if(getCurrentDate().contains(":")){
			int cnt = getCurrentDate().indexOf(":");
			
			ps = conn.prepareStatement("SELECT *  FROM cashin WHERE dateIn>=? AND dateIn<=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate().split(":", cnt)[0]);
			ps.setString(2, getCurrentDate().split(":", cnt)[1]);
			isDateRange=true;
		}else{
			ps = conn.prepareStatement("SELECT *  FROM cashin WHERE dateIn=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate());
		}
		rs = ps.executeQuery();
		Double amnt = 0.0;
		Double cnt = 0.0;
		String str = "", tmp ="";
		int i=1;
		funds = new ArrayList<Fields>();
		//System.out.println("XXXXX ZZZZ XXX " + ps.toString());
		while(rs.next()){
			int cat = rs.getInt("category");
			
			if(cat==2 && isDateRange){
			Fields f = new Fields();
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
			tmp = "Php " + rs.getString("amountIn") + "(" + rs.getString("description") +") ";
			f.setF1(""+i);
			f.setF2(rs.getString("description"));
			f.setF3(""+rs.getString("amountIn"));
			f.setF4(rs.getString("dateIn"));
			if(i==1){
				str += tmp;
			}else{
				str += "+ " +tmp;
			}
			cnt +=amnt;
			i++;
			funds.add(f);
			}
			
			if(!isDateRange){
				Fields f = new Fields();
				try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
				tmp = "Php " + rs.getString("amountIn") + "(" + rs.getString("description") +") ";
				f.setF1(""+i);
				f.setF2(rs.getString("description"));
				f.setF3(""+rs.getString("amountIn"));
				f.setF4(rs.getString("dateIn"));
				if(i==1){
					str += " = "+tmp;
				}else{
					str += " + "+ tmp;
				}
				cnt +=amnt;
				i++;
				funds.add(f);
			}
			
			
		}
		cashin = cnt+"";
		todaysFund =  " Php " + formatAmount(cnt+"") + str;
		todaysFundDis = formatAmount(cnt+"");
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void loadPurchased(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		if(getCurrentDate().contains(":")){
			int cnt = getCurrentDate().indexOf(":");
			ps = conn.prepareStatement("SELECT *  FROM purchased WHERE dateIn>=? AND dateIn<=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate().split(":", cnt)[0]);
			ps.setString(2, getCurrentDate().split(":", cnt)[1]);
		}else{	
			ps = conn.prepareStatement("SELECT *  FROM purchased WHERE dateIn=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate());
		}
		rs = ps.executeQuery();
		Double amnt = 0.0;
		Double cnt = 0.0;
		String str = "", tmp ="";
		int i=1;
		while(rs.next()){
			
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
			cnt +=amnt;
			i++;
		}
		
		setPurchased(formatAmount(cnt+""));
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void loadExpenses(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		if(getCurrentDate().contains(":")){
			int cnt = getCurrentDate().indexOf(":");
			ps = conn.prepareStatement("SELECT *  FROM expenses WHERE dateIn>=? AND dateIn<=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate().split(":", cnt)[0]);
			ps.setString(2, getCurrentDate().split(":", cnt)[1]);
		}else{	
			ps = conn.prepareStatement("SELECT *  FROM expenses WHERE dateIn=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate());
		}
		rs = ps.executeQuery();
		Double amnt = 0.0;
		Double cnt = 0.0;
		String str = "", tmp ="";
		int i=1;
		while(rs.next()){
			
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
			cnt +=amnt;
			i++;
		}
		
		setExpenses(formatAmount(cnt+""));
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void loadCredit(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		if(getCurrentDate().contains(":")){
			int cnt = getCurrentDate().indexOf(":");
			ps = conn.prepareStatement("SELECT *  FROM credit WHERE dateIn>=? AND dateIn<=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate().split(":", cnt)[0]);
			ps.setString(2, getCurrentDate().split(":", cnt)[1]);
		}else{	
			ps = conn.prepareStatement("SELECT *  FROM credit WHERE dateIn=? ORDER BY countAdded");
			ps.setString(1, getCurrentDate());
		}
		rs = ps.executeQuery();
		Double amnt = 0.0;
		Double cnt = 0.0;
		String str = "", tmp ="";
		int i=1;
		while(rs.next()){
			
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
			cnt +=amnt;
			i++;
		}
		
		setCredit(formatAmount(cnt+""));
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void loadDebit(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		if(getCurrentDate().contains(":")){
			int cnt = getCurrentDate().indexOf(":");
			ps = conn.prepareStatement("SELECT *  FROM debit WHERE isPaid!=2 AND dateIn>=? AND dateIn<=? ORDER BY countAdded");//isPaid=0 AND
			ps.setString(1, getCurrentDate().split(":", cnt)[0]);
			ps.setString(2, getCurrentDate().split(":", cnt)[1]);
			//System.out.println("======================DEBIT: " + ps.toString());
		}else{
			ps = conn.prepareStatement("SELECT *  FROM debit WHERE isPaid!=2 AND dateIn=? ORDER BY countAdded"); //isPaid=0 AND
			ps.setString(1, getCurrentDate());
		}
		rs = ps.executeQuery();
		Double amnt = 0.0;
		Double cnt = 0.0;
		String str = "", tmp ="";
		int i=1;
		while(rs.next()){
			
			try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
			cnt +=amnt;
			i++;
		}
		
		setBorrowed(formatAmount(cnt+""));
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
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
	
	public String getCurrentDate(){//MMMM d, yyyy
		if(date==null){
			date = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
			//date +=":"+ DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		}
		return date;
	}
	private String date;
	public void setCurrentDate(String date){
		this.date = date;
	}
	
	
	
	@PostConstruct
	public void init(){
		
		System.out.println("loading....");
		/*String search = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("search");
		String isSummary = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("idSummaryCheck");
		System.out.println("search : " + search + " sumamry " + isSummary);
		
		if(isSummary==null){
			setCheckSummary(false);
			setSumdetailed("1"); //unchecked = detailed
		}else{
			setSumdetailed("0"); //checked = summary
		}
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String url = request.getRequestURL().toString();
		String uri = request.getRequestURI();
		
		System.out.println("init get url : " + url + " uri : " + uri);
		
		if(search!=null && !search.isEmpty()){
				setCurrentDate(search.replace("--", ""));
		}*/
		/*String date = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		date +=":"+ DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		setCurrentDate(date);*/
		System.out.println("check date: " + getCurrentDate());
		setSumdetailed("0");
		CashFunds();
		cornsAll();
		PurchasedItems();
		ExpensesItems();
		SalesItems();
		//eiTrans();
		clientTrans();
		BorrowedMoney();
		loadReceivable();
		calculateSummary();
		//Original data
		/*loadAddedCash();
		loadPurchased();
		loadExpenses();
		loadCredit();
		loadDebit();
		loadReceivable();
		cornsAll();
		eiTrans();
		clientTrans();*/
		supplyingSummaryNotes();
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public String greetings() {
		String[] gt = {"Hi" +" " + processBy(),"Good day"+" " + processBy(), processBy() +", "+"you are awesome!", "Nice day"+ " " + processBy(), "Hey, "+processBy()+", you look cool today."};
		
		return gt[(int) (Math.random() * gt.length)];
	}
	
	private void supplyingSummaryNotes() {
		String text = "<p><strong><h2>"+ greetings() + "</h2></strong></p>";
		String dateFrom = DateUtils.convertDate(getCalendarFrom(),"yyy-MM-dd");
		String dateNameFrom = DateUtils.getMonthName(Integer.valueOf(dateFrom.split("-")[1])) + " " + dateFrom.split("-")[2] + ", " +dateFrom.split("-")[0];
		String dateTo = DateUtils.convertDate(getCalendarTo(),"yyy-MM-dd");
		String dateNameTo = DateUtils.getMonthName(Integer.valueOf(dateTo.split("-")[1])) + " " + dateTo.split("-")[2] + ", " +dateTo.split("-")[0];
		String dateSelected = "";
		if(dateFrom.split("-")[1].equalsIgnoreCase(dateTo.split("-")[1])) {
			if(dateFrom.split("-")[2].equalsIgnoreCase(dateTo.split("-")[2])) {
				dateSelected = DateUtils.getMonthName(Integer.valueOf(dateFrom.split("-")[1])) + " " + dateTo.split("-")[2] + ", " +dateTo.split("-")[0];
			}else {
				dateSelected = DateUtils.getMonthName(Integer.valueOf(dateFrom.split("-")[1])) + " " + dateFrom.split("-")[2] + "-" + dateTo.split("-")[2] + ", " +dateTo.split("-")[0];
			}
		}else {
			dateSelected=dateNameFrom + " - " + dateNameTo;
		}
		
		text += "<br/><p>Please see summary details for the month of <strong>"+dateSelected+"</strong></p><br/>";
		
		if(getFunds()!=null && getFunds().size()>0) {
			text += "<p><h3><strong>Funds:Php" + getTodaysFundDis()+"</strong></h3></p>";
			/*text +="<ul>";
			for(Fields f : getFunds()) {
				text +="<li>"+ f.getF2() +" ---> "+ f.getF3() +"</li>";	
			}
			text +="</ul>";*/
		}
		
		text += "<p><h3><strong>Total Purchased Corn: Php" + getTotalWYCorn()+"</strong></h3></p>";
		text +="<ul>";
		text +="<li>WHITE Corn: Php"+ getCornPriceWhite() +"</li>";
		text +="<li>YELLOW Corn: Php"+ getCornPriceYellow() +"</li>";
		text +="</ul>";
		
		if(getSummary()!=null && getSummary().size()>0) {
			
			text += "<br/><p><strong>Corn Purchased Conditions</strong></p><br/>";
			
			text +="<ul>";
			for(PurchasingCorn corn : getSummary()) {
				text +="<li>"+ corn.getConditions() +" ("+ corn.getKilo()+") = <strong>Php" + Currency.formatAmount(corn.getTotalAmount().doubleValue()) +"</strong></li>";	
			}
			text +="</ul>";
		}
		
		text += "<br/><p><h3><strong>Money Rotation Transactions</strong></h3></p><br/>";
		text +="<ul>";
		text +="<li>Investment: Php"+ getBasedInvestment() +"</li>";
		text +="<li>Expenses: Php"+ getExpensesAmnt() +"</li>";
		text +="<li>Incomes: Php"+ getIncomes() +"</li>";
		text +="<li>Acquired Cost: Php"+ getBoughtItems() +"</li>";
		text +="<li>ABPSD*: Php"+ getTotalclientcashpaidthesamedate() +"</li>";
		text +="</ul>";
		text +="<p style=\"color: red\">Note: *ABPSD - Amount Borrowed and Paid on the Same Date</p>";
		
		text += "<br/><p><h3><strong>Summary Transactions</strong></h3></p><br/>";
		text +="<ul>";
		text +="<li>Cash On Hand: Php"+ getCashOnHand() +"</li>";
		
		text +="<li>Amount Receivable From Debit: <strong>Php"+ getAmountReceivable()+"</strong>";
		text +="<ul>";
		for(Debit d : getData()) {
			String dateCA = DateUtils.getMonthName(Integer.valueOf(d.getDateIn().split("-")[1])) + " " + d.getDateIn().split("-")[2] + ", " +d.getDateIn().split("-")[0];
			text +="<li>Loan Date as of "+ dateCA+" for the description of "+ d.getDescription() +" amounting to Php"+ d.getAmountIn() +"</li>";
		}
		text +="</ul>";
		text += "</li>";
		
		text += receivableFromClient();
		
		text +="</ul>";
		
		
		setSummaryInfo(text);
	}
	
	private String receivableFromClient() {
		String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 ORDER BY fullName";
		String[] params = new String[0];
		String text="";
		Double amnt = 0d;
		List<ClientProfile> cl = Collections.synchronizedList(new ArrayList<ClientProfile>()); 
		List<ClientProfile> pList = ClientProfile.retrieveClientProfile(sql, params); 
		double collamnt = 0;
		if(pList.size()==1){
			ClientProfile p = pList.get(0);
			collamnt = collectibleAmnt(pList.get(0));
			if(collamnt>0) {
				amnt += collamnt;
				p.setAmntCollectible(Currency.formatAmount(collamnt));
				cl.add(p);
			}
		}else {
			for(ClientProfile p : pList){
				collamnt = collectibleAmnt(p);
				if(collamnt>0) {
					amnt += collamnt;
					p.setAmntCollectible(Currency.formatAmount(collamnt));
					cl.add(p);
				}
			}
		}
		
		if(cl!=null && cl.size()>0) {
			text += text +="<li>Amount Receivable From Client Profile: <strong>Php"+ Currency.formatAmount(amnt)+"</strong>";
			text +="<ul>";
			for(ClientProfile p : cl) {
				text += "<li>" + p.getFullName() + " total of <strong>Php" +Currency.formatAmount(p.getAmntCollectible())+"</strong></li>";
			}
			text +="</ul>";
			text +="<li>";
		}
		return text;
	}
	
	private double collectibleAmnt(ClientProfile clientProfile){
		double amntTrans = 0d;
		try{
		String sql = "SELECT transamount FROM client_trans WHERE clientId=? AND status=1 AND paidDate is null";
		String[] params = new String[1];
		params[0] = clientProfile.getClientId()+"";
		
		for(ClientTransactions clientAmount : ClientTransactions.retrieveClientTransacts(sql, params)){
			amntTrans += Double.valueOf(clientAmount.getTransamount().replace(",", ""));
		}
		
		
		}catch(Exception e){}
		return amntTrans;
	}
	
	private void calculateSummary(){
		
		
		//Cash on hand
		double todaysfund = 0d;
		double cashreturn = 0d;
		double cashpaid = 0d;
		todaysfund = Currency.amountDouble(getTodaysFundDis());
		cashreturn = Currency.amountDouble(getTotalamountRet());
		cashpaid = Currency.amountDouble(getTotalamountpayment());
		
		//Expenses
		double cashadvance = 0d;
		double expensesitems = 0d;
		double loan = 0d;
		double mortgage = 0d;
		double depReturn = 0d;
		double eiamountout = 0d;
		double borrowed = 0d;
		cashadvance = Currency.amountDouble(getTotalCashadvance());
		expensesitems = Currency.amountDouble(getExpenses());
		loan = Currency.amountDouble(getLoanAmounts());
		mortgage = Currency.amountDouble(getMortgageAmounts());
		depReturn = Currency.amountDouble(getDepositReturn());
		eiamountout = Currency.amountDouble(getTotalamountOut());
		borrowed = Currency.amountDouble(getBorrowed());
		
		
		//Incomes
		double salesitems = 0d;
		double depitemspaid = 0d;
		salesitems = Currency.amountDouble(getSales());
		depitemspaid = Currency.amountDouble(getDepositPaid());
		
		//amount receivable 
		double renteditems = 0d;
		double trackingitems = 0d; 
		double shelleritems = 0d;
		double haulingitems = 0d;
		double amntdebitreceivable = 0d;
		renteditems = Currency.amountDouble(getRentedAmounts());
		trackingitems = Currency.amountDouble(getTrackingAmounts());
		shelleritems = Currency.amountDouble(getShellerAmounts());
		haulingitems = Currency.amountDouble(getHaulingAmounts());
		amntdebitreceivable = Currency.amountDouble(getAmountReceivable());
		
		//acquired amount
		double cornpurchased = 0d;
		double purchaseditems = 0d;
		cornpurchased = Currency.amountDouble(getTotalWYCorn());
		purchaseditems = Currency.amountDouble(getPurchased());
		
		
		//cash on hand
		double basedinvestment =0d;
		basedinvestment = todaysfund + cashreturn + cashpaid;
		
		//expenses
		double totalexpenses = 0d;
		totalexpenses = cashadvance + expensesitems + loan + mortgage + depReturn + eiamountout + borrowed;
		
		//incomes
		double totalincomes = 0d;
		totalincomes = salesitems + depitemspaid;
		
		
		//acquisition cost
		double totalacquisition = 0d;
		totalacquisition = cornpurchased + purchaseditems;
		
		double credit = 0d;
		credit = basedinvestment + totalincomes;
		
		//total amount receivable
		double totalamountreceivable = 0d;
		totalamountreceivable = renteditems + trackingitems + shelleritems + haulingitems + amntdebitreceivable ;
		
		
		//client transact and paid on the same date;
		double clientpaidsamedate = 0d;
		clientpaidsamedate = Currency.amountDouble(getTotalclientcashpaidthesamedate());
		
		double debit = 0d;
		debit = totalexpenses + totalacquisition + clientpaidsamedate;
		
		//remaining amount cash
		double cashonhand = 0d;
		cashonhand = credit - debit;
		
		setBasedInvestment(Currency.formatAmount(basedinvestment+""));
		setExpensesAmnt(Currency.formatAmount(totalexpenses+""));
		setIncomes(Currency.formatAmount(totalincomes+""));
		setBoughtItems(Currency.formatAmount(totalacquisition+""));
		setCredit(Currency.formatAmount(credit+""));
		setDebit(Currency.formatAmount(debit+""));
		setAmountReceivable(Currency.formatAmount(totalamountreceivable+""));
		setCashOnHand(Currency.formatAmount(cashonhand+""));
	}
	
	public String search(){
		String date_ = getCurrentDate();
		setCurrentDate(date_);
		//String date_ = getCurrentDate();
		System.out.println("Search : " + date_);
		/*value="#{homeBean.currentDate}"*/
		CashFunds();
		cornsAll();
		PurchasedItems();
		ExpensesItems();
		SalesItems();
		eiTrans();
		clientTrans();
		BorrowedMoney();
		loadReceivable();
		calculateSummary();
		setCurrentDate(date_);
		System.out.println("set Search : " + getCurrentDate());
		return null;
	}
	
	public void runSearch(){
		String dateFrom = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		String dateTo = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		String date_ = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		System.out.println("Filter Type: " + getSumdetailed());
		if(!dateFrom.equalsIgnoreCase(dateTo)){
			date_ = dateFrom + ":" + dateTo;
		}
		setCurrentDate(date_);
		CashFunds();
		cornsAll();
		PurchasedItems();
		ExpensesItems();
		SalesItems();
		eiTrans();
		clientTrans();
		BorrowedMoney();
		loadReceivable();
		calculateSummary();
		setCurrentDate(date_);
		
		supplyingSummaryNotes();
	}
	
	private void CashFunds(){
		String sql = "SELECT * FROM cashin WHERE ";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		boolean isDateRange=false;
		
		if(getCurrentDate()!=null && !getCurrentDate().isEmpty()){
			if(getCurrentDate().contains(":")){
				sql += " dateIn>=? AND dateIn<=?";
				params = new String[2];
				params[0] = getCurrentDate().split(":")[0];
				params[1] = getCurrentDate().split(":")[1];
				isDateRange = true;
			}else{
				sql += " dateIn=?";
				params[0] = getCurrentDate();
				
			}
		}
		
		funds = new ArrayList<Fields>();
		int cnt = 1;
		double amnt = 0d,tmpamnt=0d;
		String finalDesc="";;
		for(CashIn cash : CashIn.retrieveCashIn(sql, params)){
			Fields f = new Fields();
			
			int cat = cash.getCategory();
			String tmpDesc = "";
			if(cat==2 && isDateRange){
				tmpDesc = cash.getDescription();
				f.setF2(tmpDesc);
				try{
					amnt = Double.valueOf(cash.getAmountIn().replace(",", ""));
					tmpamnt +=amnt;
				}catch(Exception e){}
				f.setF3(Currency.formatAmount(amnt+""));
				f.setF4(cash.getDateIn());
				funds.add(f);
				
				if(cnt==1){
					finalDesc = "Php" + amnt + " ("+ tmpDesc +")";
				}else{
					finalDesc += ", Php" + amnt + " ("+ tmpDesc +")";
				}
				f.setF1(""+cnt++);
			}
			
			//if no date range has been selected
			if(!isDateRange){
				tmpDesc = cash.getDescription();
				f.setF2(tmpDesc);
				try{
					amnt = Double.valueOf(cash.getAmountIn().replace(",", ""));
					tmpamnt +=amnt;
				}catch(Exception e){}
				f.setF3(Currency.formatAmount(amnt+""));
				f.setF4(cash.getDateIn());
				
				
				if(cnt==1){
					finalDesc = "Php" + amnt + " ("+ tmpDesc +")";
				}else{
					finalDesc += ", Php" + amnt + " ("+ tmpDesc +")";
				}
				f.setF1(""+cnt++);
				funds.add(f);
			}
			
			
		}
		setTodaysFund(Currency.formatAmount(tmpamnt) +" = " + finalDesc);
		setTodaysFundDis(Currency.formatAmount(tmpamnt+""));
		System.out.println("Today fund: " + getTodaysFund());
	}
	
	private void PurchasedItems(){
		String sql = "SELECT * FROM purchased WHERE ";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		
		if(getCurrentDate()!=null && !getCurrentDate().isEmpty()){
			if(getCurrentDate().contains(":")){
				sql += " dateIn>=? AND dateIn<=?";
				params = new String[2];
				params[0] = getCurrentDate().split(":")[0];
				params[1] = getCurrentDate().split(":")[1];
			}else{
				sql += " dateIn=?";
				params[0] = getCurrentDate();
			}
		}
		double amnt = 0d;
		for(Purchased p : Purchased.retrievePurchased(sql, params)){
			try{amnt += Double.valueOf(p.getAmountIn().replace(",", ""));}catch(Exception e){} 
		}
		setPurchased(Currency.formatAmount(amnt+""));
	}
	
	private void ExpensesItems(){
		String sql = "SELECT * FROM expenses WHERE ";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		
		if(getCurrentDate()!=null && !getCurrentDate().isEmpty()){
			if(getCurrentDate().contains(":")){
				sql += " dateIn>=? AND dateIn<=?";
				params = new String[2];
				params[0] = getCurrentDate().split(":")[0];
				params[1] = getCurrentDate().split(":")[1];
			}else{
				sql += " dateIn=?";
				params[0] = getCurrentDate();
			}
		}
		double amnt = 0d;
		for(Expenses ex : Expenses.retrieveExpenses(sql, params)){
			try{amnt += Double.valueOf(ex.getAmountIn().replace(",", ""));}catch(Exception e){} 
		}
		setExpenses(Currency.formatAmount(amnt+""));
	}
	
	private void SalesItems(){
		String sql = "SELECT * FROM credit WHERE ";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		
		if(getCurrentDate()!=null && !getCurrentDate().isEmpty()){
			if(getCurrentDate().contains(":")){
				sql += " dateIn>=? AND dateIn<=?";
				params = new String[2];
				params[0] = getCurrentDate().split(":")[0];
				params[1] = getCurrentDate().split(":")[1];
			}else{
				sql += " dateIn=?";
				params[0] = getCurrentDate();
			}
		}
		double amnt = 0d;
		/*for(Expenses ex : Expenses.retrieveExpenses(sql, params)){
			try{amnt += Double.valueOf(ex.getAmountIn().replace(",", ""));}catch(Exception e){} 
		}*/
		for(Credit cr : Credit.retrieveCredit(sql, params)){
			try{amnt += Double.valueOf(cr.getAmountIn().replace(",", ""));}catch(Exception e){} 
		}
		System.out.println("Log sales: " + amnt);
		setSales(Currency.formatAmount(amnt+""));
	}
	
	private void BorrowedMoney(){
		String sql = "SELECT * FROM debit WHERE isPaid!=2 AND ";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		
		if(getCurrentDate()!=null && !getCurrentDate().isEmpty()){
			if(getCurrentDate().contains(":")){
				sql += " dateIn>=? AND dateIn<=?";
				params = new String[2];
				params[0] = getCurrentDate().split(":")[0];
				params[1] = getCurrentDate().split(":")[1];
			}else{
				sql += " dateIn=?";
				params[0] = getCurrentDate();
			}
		}
		double amnt = 0d;
		for(Debit d : Debit.retrieveDebit(sql, params)){
			try{amnt += Double.valueOf(d.getAmountIn().replace(",", ""));}catch(Exception e){} 
		}
		setBorrowed(Currency.formatAmount(amnt+""));
	}
	
	/*private void loadCornPurchasedDetails(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		allCorn = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
		String sql= "";
		
		
		
		try{
		conn = DataConnectDAO.getConnection();
		sql="SELECT *  FROM purchasingcorn WHERE datein=? ORDER BY timestamp DESC";
		ps = conn.prepareStatement(sql);
		ps.setString(1, getCurrentDate());
		
		
		System.out.println("Is exist sql: " + ps.toString());
		rs = ps.executeQuery();
		
		supplyData(rs);
		
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
				//distinct date
				Map<String, PurchasingCorn> det = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
				if(allCorn!=null){
					for(PurchasingCorn p : allCorn.values()){
						if(!det.containsKey(p.getDateIn())){
							det.put(p.getDateIn(), p);
						}
						if(det==null || det.isEmpty()){
							det = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
							det.put(p.getDateIn(), p);
						}
					}
				}
				System.out.println("total date " + det.size());
				
				//distinct corn conditions
				Map<String, PurchasingCorn> kond = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
				if(allCorn!=null){
					for(PurchasingCorn p : allCorn.values()){
						if(!kond.containsKey(p.getConditions())){
							kond.put(p.getConditions(), p);
						}
						if(kond==null || kond.isEmpty()){
							kond = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
							kond.put(p.getConditions(), p);
						}
					}
				}
		
				
				//calculating summary
				if(allCorn!=null){
					//summaryTotal = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
					int totalKiloWhite=0,grandTotalWhite=0,driverWhite=0,discountWhite=0;
					int totalKiloYellow=0,grandTotalYellow=0,driverYellow=0,discountYellow=0;
					Long id =0l;
					for(String date : det.keySet()){
						
						System.out.println("DATE IN : " + date + " count " + id);
						
						String colors[] = {"YELLOW","WHITE"};
						
							for(String color : colors){
								
								for(String c : kond.keySet()){
									
									String fldColor="";
									int driver= 0, kilo=0, discount=0,amount=0, totalAmount=0;
									boolean isTrue=false;
									for(PurchasingCorn px : allCorn.values()){
										
									String col = px.getCorncolor();
									String cn = px.getConditions();
									String dte = px.getDateIn();
											
											if(date.equalsIgnoreCase(dte) && color.equalsIgnoreCase(col) && c.equalsIgnoreCase(cn)){
												fldColor = col;
												driver += Integer.valueOf(px.getDriver()+"");
												kilo += Integer.valueOf(px.getKilo()+"");
												discount +=Integer.valueOf(px.getDiscount()+"");
												amount += Integer.valueOf(px.getAmount()+"");
												totalAmount += Integer.valueOf(px.getTotalAmount()+"");
												isTrue=true;
											}
										
									}
									
									if(isTrue){
									isTrue=false;
									if("WHITE".equalsIgnoreCase(fldColor)){
										totalKiloWhite +=kilo;
										driverWhite +=driver;
										discountWhite +=discount;
										grandTotalWhite +=totalAmount;
									}else if("YELLOW".equalsIgnoreCase(fldColor)){
										totalKiloYellow +=kilo;
										driverYellow +=driver;
										discountYellow +=discount;
										grandTotalYellow +=totalAmount;
									}
									}
									
								}
								
								
							}
						
					}
					
					//calculating the Price of corn
					//WHITE
					if(grandTotalWhite==0 && totalKiloWhite==0){
						setCornPriceWhite(0.0f);
					}else{
						float price = Float.valueOf(grandTotalWhite+"") / Float.valueOf(totalKiloWhite+"");
						setCornPriceWhite(price);
					}
					if(driverWhite==0){setCornDriverWhite(formatAmount("0"));}else{setCornDriverWhite(formatAmount(driverWhite+""));}
					if(discountWhite==0){setCorndiscountWhite(0.0f);}else{setCorndiscountWhite(discountWhite);}
					if(grandTotalWhite==0){setCorngrandTotalWhite(formatAmount("0"));}else{setCorngrandTotalWhite(formatAmount(grandTotalWhite+""));}
					if(totalKiloWhite==0){setCornkiloWhite(0.0f);}else{setCornkiloWhite(totalKiloWhite);}
					
					//YELLOW
					if(grandTotalYellow==0 && totalKiloYellow==0){
						setCornPriceYellow(0.0f);
					}else{
						float price = Float.valueOf(grandTotalYellow+"") / Float.valueOf(totalKiloYellow+"");
						setCornPriceYellow(price);
					}
					if(driverYellow==0){setCornDriverYellow(formatAmount("0"));}else{setCornDriverYellow(formatAmount(driverYellow+""));}
					if(discountYellow==0){setCorndiscountYellow(0.0f);}else{setCorndiscountYellow(discountYellow);}
					if(grandTotalYellow==0){setCorngrandTotalYellow(formatAmount("0"));}else{setCorngrandTotalYellow(formatAmount(grandTotalYellow+""));}
					if(totalKiloYellow==0){setCornkiloYellow(0.0f);}else{setCornkiloYellow(totalKiloYellow);}
					
					//BOTH
					long total=0;
					float grandtotal=0;
					if(grandTotalWhite==0 && grandTotalYellow==0){
						setTotalWYCorn(formatAmount("0"));
					}else{
						
						total=grandTotalWhite+grandTotalYellow;
						
						setTotalWYCorn(formatAmount(total+""));
					}
					
					grandtotal = Float.valueOf(cashin) - Float.valueOf(total+"");
					
					//private String expenses="0.00";
					//private String credit="0.00";
					//private String debit="0.00";
					float remtotal = (grandtotal + Float.valueOf(credit.replace(",", ""))) - (Float.valueOf(purchased.replace(",", "")) +  Float.valueOf(expenses.replace(",", "")) + Float.valueOf(debit.replace(",", ""))) ;
					System.out.println("Remaining amount: " + remtotal);
					setRemAmountCash(formatAmount(remtotal+""));
					
				}
				
		
	}*/
	private void supplyData(ResultSet rs){
		try{
			while(rs.next()){
				PurchasingCorn p = new PurchasingCorn();
				String date =rs.getString("datein");
				String colors = rs.getString("corncolor").equalsIgnoreCase("1")? "YELLOW" : "WHITE";
				String cond = con(rs.getString("conditions"));
				long id = rs.getLong("chasedid");
				p.setChasedId(id);
				p.setCorncolor(colors);
				p.setConditions(cond);
				p.setDateIn(date);
				p.setKilo(Numbers.formatDouble(rs.getDouble("kilo")));
				p.setDiscount(Numbers.formatDouble(rs.getDouble("discount")));
				p.setAmount(rs.getBigDecimal("amount"));
				p.setDriver(rs.getBigDecimal("driver"));
				p.setTotalAmount(rs.getBigDecimal("totalamount"));
				p.setProcBy(rs.getString("procby"));
				allCorn.put(id, p);
			}
		}catch(SQLException e){}
	}

	private String con(String str){
		switch(str){
		case "0" : { return "ALL";}
		case "1" : { return CornConditions.WCD_NATIVE.getName();}
		case "2" : { return CornConditions.WHITE_WET.getName();}
		case "3" : { return CornConditions.WHITE_BILOG.getName();}
		case "4" : { return CornConditions.YELLOW_DRY.getName();}
		case "5" : { return CornConditions.YELLOW_BASA.getName();}
		case "6" : { return CornConditions.YELLOW_BILOG.getName();}
		case "7" : { return CornConditions.WHITE_SEMI_BASA.getName();}
		case "8" : { return CornConditions.YELLOW_SEMI_BASA.getName();}
		}
		return "";
	}
	
	private void compileReport(){
		try{
		System.out.println("CheckReport path: " + REPORT_PATH);
		//HashMap paramMap = new HashMap();
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		//String reportLocation = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		//System.out.println("Check report path: " + reportLocation);
		
		
		ArrayList<PurchasingCorn> rpts = new ArrayList<PurchasingCorn>();
		if(summaryTotal!=null && summaryTotal.size()>0){
			for(PurchasingCorn r : summaryTotal.values()){
	  			rpts.add(r);
			}
		}
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rpts);
		
		HashMap params = new HashMap();
		params.put("PARAM_CASHIN",getTodaysFund());
		
		/*params.put("PARAM_W_PURCHASED","Php "+getCorngrandTotalWhite());
		params.put("PARAM_W_CURRENT_PRICE","Php "+getCornPriceWhite());
		params.put("PARAM_W_TOTAL_KILO",""+getCornkiloWhite());
		params.put("PARAM_W_TOTAL_DISCOUNT",""+getCorndiscountWhite());
		params.put("PARAM_W_TOTAL_COMMISION","Php "+getCornDriverWhite());

		params.put("PARAM_Y_PURCHASED","Php "+getCorngrandTotalYellow());
		params.put("PARAM_Y_CURRENT_PRICE","Php "+getCornPriceYellow());
		params.put("PARAM_Y_TOTAL_KILO",""+getCornkiloYellow());
		params.put("PARAM_Y_TOTAL_DISCOUNT",""+getCorndiscountYellow());
		params.put("PARAM_Y_TOTAL_COMMISION","Php "+getCornDriverYellow());
*/
		
		params.put("PARAM_CASHONHAND","Php "+getCashOnHand());
		params.put("PARAM_AMOUNT_RECEIVABLE","Php "+getAmountReceivable());
		
		params.put("PARAM_CREDIT","Php "+getCredit());
		params.put("PARAM_DEBIT","Php "+getDebit());
		
		params.put("PARAM_INVESTMENT","Php "+getBasedInvestment());
		params.put("PARAM_EXPENSES","Php "+getExpensesAmnt());
		params.put("PARAM_INCOMES","Php "+getIncomes());
		params.put("PARAM_ACQUIRED_COST","Php "+getBoughtItems());
		params.put("PARAM_ABPSD","Php "+getTotalclientcashpaidthesamedate());
		
		params.put("PARAM_TOTAL_WY_PURCHASED","Php "+getTotalWYCorn());
		
		/*params.put("PARAM_PURCHASED","Php "+getPurchased());
		params.put("PARAM_RENTEDITEM","Php "+getRentedAmounts());
		params.put("PARAM_LOAN","Php "+getMortgageAmounts());*/
		
		params.put("PARAM_OUT","Php "+getTotalamountOut());
		params.put("PARAM_RET","Php "+getTotalamountRet());
		params.put("PARAM_PAY","Php "+getTotalamountpayment());
		
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		
		Date date = new Date();
		if(getCurrentDate()==null){
			params.put("PARAM_DATE","As of "+dateFormat.format(date));
		}else{
			
			if(getCurrentDate().contains(":")){
				params.put("PARAM_DATE"," FROM "+getCurrentDate().split(":")[0] +" TO "+getCurrentDate().split(":")[1]);
			}else{
				params.put("PARAM_DATE","As of "+getCurrentDate());
			}
			
		}
		
		dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm: a");
		date = new Date();
		params.put("PARAM_PRINTDATE","PRINTED DATE: "+dateFormat.format(date));
		HttpSession session = SessionBean.getSession();
		String proc_by = session.getAttribute("username").toString();
		if(proc_by!=null){
			params.put("PARAM_PROCCESSEDBY","PROCCESSED BY: "+proc_by);
		}else{
			params.put("PARAM_PROCCESSEDBY","");
		}
		
		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, params, beanColl);
		JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		
		//JasperPrint print = compiler.report(reportLocation, params);
		//File pdf = null;
		//try{
		//pdf = new File(REPORT_PATH+REPORT_NAME+".pdf");
		///pdf.createNewFile();
		//JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		//System.out.println("pdf successfully created...");
		//}catch(Exception e){
			//e.printStackTrace();
		//}
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	public void printRpt(){
		try{
		//compile the report	
		compileReport();
			
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
		
	}
	
	public String printReport(){
		try{
		//compile the report	
		compileReport();
			
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
	
	//Debit data
	private void loadReceivable(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		try{
		debitData = Collections.synchronizedMap(new HashMap<Long, Debit>());	
		conn = DataConnectDAO.getConnection();
		
		
		ps = conn.prepareStatement("SELECT *  FROM debit WHERE isPaid=0 ORDER BY timestamp");
		
		
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
			//in.setAddedBy(rs.getString("processedBy"));
			try{in.setDatepaying(rs.getString("datepaying"));}catch(Exception e){in.setDatepaying("");}
			//try{in.setIsPaid(rs.getString("isPaid").equalsIgnoreCase("1")? "YES" : "NO");}catch(Exception e){in.setIsPaid("NO");}
			//try{in.setDatecounting(rs.getInt("datecounting"));}catch(Exception e){in.setDatecounting(0);}
			debitData.put(in.getId(), in);
			//data.add(in);
			cnt +=amnt;
		}
		//setDebit(formatAmount(cnt+""));
		setAmountReceivable(formatAmount(cnt+""));
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public List<PurchasingCorn> getSummary() {
		
		summary = new ArrayList<PurchasingCorn>();
		if(summaryTotal!=null){
			int counter=1;
			for(PurchasingCorn p : summaryTotal.values()){
				p.setCounter(counter++);
				summary.add(p);
			}
			
			
		}
		
		
		return summary;
	}

	public void setSummary(List<PurchasingCorn> summary) {
		this.summary = summary;
	}
	private void cornsAll(){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		allCorn = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
		//String filltered = getFiltered()!=null? (getFiltered().equalsIgnoreCase("1")? "DETAILED" :  (getFiltered().equalsIgnoreCase("2")? "SUMMARY" : "ALL") ) : "ALL";
		
		String sql= "";
		
		
		
		try{
		conn = DataConnectDAO.getConnection();
		
		
		/*if("init".equalsIgnoreCase(type)){
			sql="SELECT *  FROM purchasingcorn WHERE datein=? ORDER BY timestamp DESC";
			ps = conn.prepareStatement(sql);
			ps.setString(1, getCurrentDate());
		}else if("search".equalsIgnoreCase(type)){*/
			
			
			
			if(getCurrentDate().contains(":")){
				int cnt = getCurrentDate().indexOf(":");
				
					sql="SELECT *  FROM purchasingcorn WHERE datein>=? AND datein<=? ORDER BY timestamp DESC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getCurrentDate().split(":", cnt)[0]);
					ps.setString(2, getCurrentDate().split(":", cnt)[1]);
				
			}else{
				
				
					sql="SELECT *  FROM purchasingcorn WHERE datein=? ORDER BY timestamp DESC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getCurrentDate());
				
			}
			
			
			
		/*}else{
			sql="SELECT *  FROM purchasingcorn WHERE datein=? ORDER BY timestamp DESC";
			ps = conn.prepareStatement(sql);
			ps.setString(1, getCurrentDate());
		}*/
		
		//System.out.println("Is exist sql: " + ps.toString());
		rs = ps.executeQuery();
		
		supplyData(rs);
		
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//distinct date
		Map<String, PurchasingCorn> det = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
		if(allCorn!=null){
			for(PurchasingCorn p : allCorn.values()){
				if(!det.containsKey(p.getDateIn())){
					det.put(p.getDateIn(), p);
				}
				if(det==null || det.isEmpty()){
					det = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
					det.put(p.getDateIn(), p);
				}
			}
		}
		//System.out.println("total date " + det.size());
		
		//distinct corn conditions
		Map<String, PurchasingCorn> kond = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
		if(allCorn!=null){
			for(PurchasingCorn p : allCorn.values()){
				if(!kond.containsKey(p.getConditions())){
					kond.put(p.getConditions(), p);
				}
				if(kond==null || kond.isEmpty()){
					kond = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
					kond.put(p.getConditions(), p);
				}
			}
		}
		//System.out.println("total cond " + kond.size());
		
		//temp storing of data
		//Map<String, PurchasingCorn> stage1 = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
		//Map<String, PurchasingCorn> stage2 = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
		//Map<Long, PurchasingCorn> stage3 = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
		
		
		
		
		
		if(allCorn!=null){
			summaryTotal = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
			int totalKiloWhite=0,grandTotalWhite=0;
			int totalKiloYellow=0,grandTotalYellow=0;
			Long id =0l;
			setSumdetailed(getSumdetailed()==null? null : getSumdetailed().equalsIgnoreCase("")? null : getSumdetailed().isEmpty()? null : getSumdetailed());
			
			System.out.println("Selected : " + getSumdetailed());
			
			if(getSumdetailed()==null || "1".equalsIgnoreCase(getSumdetailed())){
			
			for(String date : det.keySet()){
				
				//System.out.println("DATE IN : " + date + " count " + id);
				
				String colors[] = {"YELLOW","WHITE"};
				
					for(String color : colors){
						
						for(String c : kond.keySet()){
							
							String fldColor="",fldDate="",fldCond="";
							float driver= 0, kilo=0, discount=0,amount=0, totalAmount=0,netTotal=0;
							boolean isTrue=false;
							for(PurchasingCorn px : allCorn.values()){
								
							String col = px.getCorncolor();
							String cn = px.getConditions();
							String dte = px.getDateIn();
									
									if(date.equalsIgnoreCase(dte) && color.equalsIgnoreCase(col) && c.equalsIgnoreCase(cn)){
										fldColor = col;
										fldDate = dte;
										fldCond = cn;
										driver += Float.valueOf(px.getDriver()+"");
										kilo += Float.valueOf(px.getKilo()+"");
										discount +=Float.valueOf(px.getDiscount()+"");
										amount += Float.valueOf(px.getAmount()+"");
										totalAmount += Float.valueOf(px.getTotalAmount()+"");
										isTrue=true;
									}
								
							}
							
							if(isTrue){
							PurchasingCorn ppp = new PurchasingCorn();
							ppp.setDateIn(fldDate);
							ppp.setCorncolor(fldColor);
							ppp.setConditions(fldCond);
							ppp.setDriver(new BigDecimal(driver));
							ppp.setKilo(Numbers.formatDouble(kilo));
							ppp.setDiscount(Numbers.formatDouble(discount));
							ppp.setAmount(new BigDecimal(amount));
							ppp.setTotalAmount(new BigDecimal(totalAmount));
							netTotal = totalAmount/kilo;
							//System.out.println("Net Price: " + netTotal);
							ppp.setNetPrice(new BigDecimal(netTotal));
							summaryTotal.put(id++, ppp);
							isTrue=false;
							if("WHITE".equalsIgnoreCase(fldColor)){
								totalKiloWhite +=kilo;
								grandTotalWhite +=totalAmount;
							}else if("YELLOW".equalsIgnoreCase(fldColor)){
								totalKiloYellow +=kilo;
								grandTotalYellow +=totalAmount;
							}
							}
							
						}
						
						
					}
				
			}
			
			}else if("0".equalsIgnoreCase(getSumdetailed())){
				
				String colors[] = {"YELLOW","WHITE"};
				
				for(String color : colors){
					
					for(String c : kond.keySet()){
						
						String fldColor="",fldDate="",fldCond="";
						float driver= 0, kilo=0, discount=0,amount=0, totalAmount=0,netTotal=0;
						boolean isTrue=false;
						for(PurchasingCorn px : allCorn.values()){
							
						String col = px.getCorncolor();
						String cn = px.getConditions();
								
								if(color.equalsIgnoreCase(col) && c.equalsIgnoreCase(cn)){
									fldColor = col;
									fldCond = cn;
									driver += Float.valueOf(px.getDriver()+"");
									kilo += Float.valueOf(px.getKilo()+"");
									discount +=Float.valueOf(px.getDiscount()+"");
									amount += Float.valueOf(px.getAmount()+"");
									totalAmount += Float.valueOf(px.getTotalAmount()+"");
									isTrue=true;
								}
							
						}
						
						if(isTrue){
						PurchasingCorn ppp = new PurchasingCorn();
						ppp.setDateIn(getCurrentDate());
						ppp.setCorncolor(fldColor);
						ppp.setConditions(fldCond);
						ppp.setDriver(new BigDecimal(driver));
						ppp.setKilo(Numbers.formatDouble(kilo));
						ppp.setDiscount(Numbers.formatDouble(discount));
						ppp.setAmount(new BigDecimal(amount));
						ppp.setTotalAmount(new BigDecimal(totalAmount));
						netTotal = totalAmount/kilo;
						ppp.setNetPrice(new BigDecimal(netTotal));
						summaryTotal.put(id++, ppp);
						isTrue=false;
						if("WHITE".equalsIgnoreCase(fldColor)){
							totalKiloWhite +=kilo;
							grandTotalWhite +=totalAmount;
						}else if("YELLOW".equalsIgnoreCase(fldColor)){
							totalKiloYellow +=kilo;
							grandTotalYellow +=totalAmount;
						}
						}
						
					}
					
					
				}
				
			}
			
			//calculating the Price of corn
			if(grandTotalWhite==0 && totalKiloWhite==0){
				setCornPriceWhite("0.00");
			}else{
				float price = Float.valueOf(grandTotalWhite+"");// / Float.valueOf(totalKiloWhite+"");
				setCornPriceWhite(formatAmount(price+""));
			}
			if(grandTotalYellow==0 && totalKiloYellow==0){
				setCornPriceYellow("0.00");
			}else{
				float price = Float.valueOf(grandTotalYellow+"");// / Float.valueOf(totalKiloYellow+"");
				setCornPriceYellow(formatAmount(price+""));
			}
			if(grandTotalWhite==0 && grandTotalYellow==0){
				setTotalWYCorn(formatAmount("0"));
			}else{
				int total = grandTotalWhite + grandTotalYellow;
				setTotalWYCorn(formatAmount(total+""));
			}
			
			
			//BOTH
			long total=0;
			//float grandtotal=0;
			if(grandTotalWhite==0 && grandTotalYellow==0){
				setTotalWYCorn(formatAmount("0"));
			}else{
				
				total=grandTotalWhite+grandTotalYellow;
				
				setTotalWYCorn(formatAmount(total+""));
			}
			
			//grandtotal = Float.valueOf(cashin) - Float.valueOf(total+"");
			
			//private String expenses="0.00";
			//private String credit="0.00";
			//private String debit="0.00";
			
				//credit = Currency.removeCurrencySymbol(credit, "");
				//purchased = Currency.removeCurrencySymbol(purchased, "");
				//expenses = Currency.removeCurrencySymbol(expenses, "");
				//debit = Currency.removeCurrencySymbol(debit, "");
				
			//float remtotal = (grandtotal + Float.valueOf(credit.replace(",", ""))) - (Float.valueOf(purchased.replace(",", "")) +  Float.valueOf(expenses.replace(",", "")) + Float.valueOf(debit.replace(",", ""))) ;
			//System.out.println("Remaining amount: " + remtotal);
			//setRemAmountCash(formatAmount(remtotal+""));
			
			
		}
		
	}
	
	List<EICashPay> payList = new ArrayList<>();
	List<EICashOut> outList = new ArrayList<>();
	List<EICashReturn> returnList = new ArrayList<>();
	@Deprecated
	private void eiTrans(){
		
		String date = getCurrentDate();
		
		String sql = "SELECT * FROM ei_cash_payment WHERE  status=? AND datePay=?";
		String[] params = new String[2];
		
		if(date.contains(":")){
			sql = "SELECT * FROM ei_cash_payment WHERE  status=? AND datePay>=? AND datePay<=?";
			params = new String[3];
			params[0] = "2";
			params[1] = date.split(":")[0];
			params[2] = date.split(":")[1];
		}else{
			params[0] = "2";
			if(date==null){
				params[1] = DateUtils.getCurrentYYYYMMDD();
			}else{
				params[1] = date;
			}
		}
		
		double pamnt = 0d;
		for(EICashPay p : EICashPay.retrieveCashPay(sql, params)){
			pamnt += Double.valueOf(p.getAmountPay().replace(",", ""));
		}
		
		sql = "SELECT * FROM ei_cash_out WHERE (status=? OR status=?) AND dateOut=?";
		params = new String[3];
		
		if(date.contains(":")){
			sql = "SELECT * FROM ei_cash_out WHERE (status=? OR status=?)  AND dateOut>=? AND dateOut<=?";
			params = new String[4];
			params[0] = "1";
			params[1] = "5";
			params[2] = date.split(":")[0];
			params[3] = date.split(":")[1];
		}else{
			params[0] = "1";
			params[1] = "5";
			if(date==null){
				params[2] = DateUtils.getCurrentYYYYMMDD();
			}else{
				params[2] = date;
			}
		
		}
		
		double oamnt = 0d;
		for(EICashOut o : EICashOut.retrieveCashOut(sql, params)){
			oamnt += Double.valueOf(o.getAmountOut().replace(",", ""));
		}
		
		sql = "SELECT * FROM ei_cash_return WHERE status!=? AND dateReturn=?";
		params = new String[2];
		
		if(date.contains(":")){
			sql = "SELECT * FROM ei_cash_return WHERE status!=? AND dateReturn>=? AND dateReturn<=?";
			params = new String[3];
			params[0] = "9";
			params[1] = date.split(":")[0];
			params[2] = date.split(":")[1];
		}else{	
			params[0] = "9";
			if(date==null){
				params[1] = DateUtils.getCurrentYYYYMMDD();
			}else{
				params[1] = date;
			}
		
		}
		
		double ramnt = 0d;
		for(EICashReturn r : EICashReturn.retrieveCashReturn(sql, params)){
			ramnt += Double.valueOf(r.getAmountReturn().replace(",", ""));
		}
		
		double calc = 0d,topay=0d;
		
		//try{calc = (Double.valueOf(remAmountCash.replace(",", "")) + ramnt + pamnt) - oamnt;}catch(Exception e){}
		
		topay = oamnt - ramnt;
		
		setTotalamountOut(formatAmount(oamnt+""));
		setTotalamountRet(formatAmount(ramnt+""));
		setTotalamountpayment(formatAmount(pamnt+""));
		setTotalamounttopay(formatAmount(topay+""));
		//setRemAmountCash(formatAmount(calc + ""));
		
	}
	
	private void clientTrans(){
		
		String date = getCurrentDate();
		
		String sql = "SELECT * FROM client_trans WHERE transDate=? AND status=2 AND (transtype=1 OR transtype=3 OR transtype=4)";
		String[] params = new String[1];
		params[0] = date;
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE (transDate>=? AND transDate<=?) AND status=2 AND (transtype=1 OR transtype=3 OR transtype=4)";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate out cash borrowed and paid on the same date
		Double outcash = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			outcash += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setTotalclientcashpaidthesamedate(Currency.formatAmount(outcash+""));
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=1";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status=1 AND transtype=1";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate unpaid cash advance
		Double advanceamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			advanceamnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setTotalCashadvance(Currency.formatAmount(advanceamnt+""));
		
		sql = "SELECT * FROM client_trans WHERE paidDate=? AND status=2";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE paidDate>=? AND paidDate<=? AND status=2";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate paid loans
		Double pamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			pamnt += Double.valueOf(t.getPaidamount().replace(",", ""));
		}
		
		double calc = 0d, todayFund=0d,cashA=0d;
		//try{todayFund=Double.valueOf(getTodaysFundDis().replace(",", ""));}catch(Exception e){}
		//todayFund += pamnt;
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=2";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status=1 AND transtype=2";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate rent items
		Double ramnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			ramnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setRentedAmounts(formatAmount(ramnt+""));
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=3";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status=1 AND transtype=3";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate loan items
		Double lamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			lamnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setLoanAmounts(formatAmount(lamnt+""));
		
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=4";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status=1 AND transtype=4";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate mortgage items
		Double mamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			mamnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setMortgageAmounts(formatAmount(mamnt+""));
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=5";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status=1 AND transtype=5";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate tracking
		Double tamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			tamnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setTrackingAmounts(formatAmount(tamnt+""));
		
		/////////////////////////////////////DEPOSIT///////////////////////////////////////////////////////////////
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=6";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status=1 AND transtype=6";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate deposit unpaid
		Double duamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			duamnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setDepositUnpaid(formatAmount(duamnt+""));
		
		sql = "SELECT * FROM client_trans WHERE paidDate=? AND status=2 AND transtype=6";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE paidDate>=? AND paidDate<=? AND status=2 AND transtype=6";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate deposit paid
		Double dpamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			dpamnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setDepositPaid(formatAmount(dpamnt+""));
		
		sql = "SELECT * FROM client_trans WHERE paidDate=? AND status=3 AND transtype=6";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE paidDate>=? AND paidDate<=? AND status=3 AND transtype=6";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate deposit return
		Double dramnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			dramnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setDepositReturn(formatAmount(dramnt+""));
		
		
/////////////////////////////////////END DEPOSIT///////////////////////////////////////////////////////////////
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=7";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status!=3 AND transtype=7";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate sheller
		Double samnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			samnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setShellerAmounts(formatAmount(samnt+""));
		
		sql = "SELECT * FROM client_trans WHERE transDate=? AND status=1 AND transtype=8";
		params = new String[1];
		params[0] = date;
		
		if(date.contains(":")){
			sql = "SELECT * FROM client_trans WHERE transDate>=? AND transDate<=? AND status!=3 AND transtype=8";
			params = new String[2];
			params[0] = date.split(":")[0];
			params[1] = date.split(":")[1];
		}
		
		//calculate hauling
		Double hamnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			hamnt += Double.valueOf(t.getTransamount().replace(",", ""));
		}
		setHaulingAmounts(formatAmount(hamnt+""));
		
		/*try{calc = (Double.valueOf(remAmountCash.replace(",", "")) - (advanceamnt + lamnt + mamnt) - outcash);}catch(Exception e){}
		
		try{cashA = Double.valueOf(getDebit().replace(",", ""));}catch(Exception e){}
		cashA +=advanceamnt;
		
		//setTodaysFundDis(formatAmount(todayFund+""));
		setRemAmountCash(formatAmount(calc + ""));
		setDebit(formatAmount(cashA+""));
		double rcvamnt = Double.valueOf(getAmountReceivable().replace(",", ""));
		rcvamnt = rcvamnt + lamnt + mamnt + advanceamnt + hamnt + tamnt + samnt;
		setAmountReceivable(formatAmount(rcvamnt+""));*/
		
	}
	
	private String totalamountOut;
	private String totalamountRet;
	private String totalamountpayment;
	private String totalamounttopay;
	public String getTotalamountOut() {
		return totalamountOut;
	}
	public void setTotalamountOut(String totalamountOut) {
		this.totalamountOut = totalamountOut;
	}
	public String getTotalamountRet() {
		return totalamountRet;
	}
	public void setTotalamountRet(String totalamountRet) {
		this.totalamountRet = totalamountRet;
	}
	public String getTotalamountpayment() {
		return totalamountpayment;
	}
	public void setTotalamountpayment(String totalamountpayment) {
		this.totalamountpayment = totalamountpayment;
	}
	public String getTotalamounttopay() {
		return totalamounttopay;
	}
	public void setTotalamounttopay(String totalamounttopay) {
		this.totalamounttopay = totalamounttopay;
	}
	public String getRentedAmounts() {
		return rentedAmounts;
	}
	public void setRentedAmounts(String rentedAmounts) {
		this.rentedAmounts = rentedAmounts;
	}
	public String getMortgageAmounts() {
		return mortgageAmounts;
	}
	public void setMortgageAmounts(String mortgageAmounts) {
		this.mortgageAmounts = mortgageAmounts;
	}
	public String getLoanAmounts() {
		return loanAmounts;
	}
	public String getTrackingAmounts() {
		return trackingAmounts;
	}
	
	public String getShellerAmounts() {
		return shellerAmounts;
	}
	public String getHaulingAmounts() {
		return haulingAmounts;
	}
	public void setLoanAmounts(String loanAmounts) {
		this.loanAmounts = loanAmounts;
	}
	public void setTrackingAmounts(String trackingAmounts) {
		this.trackingAmounts = trackingAmounts;
	}
	
	public void setShellerAmounts(String shellerAmounts) {
		this.shellerAmounts = shellerAmounts;
	}
	public void setHaulingAmounts(String haulingAmounts) {
		this.haulingAmounts = haulingAmounts;
	}
	public String getDepositPaid() {
		return depositPaid;
	}
	public String getDepositUnpaid() {
		return depositUnpaid;
	}
	public String getDepositReturn() {
		return depositReturn;
	}
	public void setDepositPaid(String depositPaid) {
		this.depositPaid = depositPaid;
	}
	public void setDepositUnpaid(String depositUnpaid) {
		this.depositUnpaid = depositUnpaid;
	}
	public void setDepositReturn(String depositReturn) {
		this.depositReturn = depositReturn;
	}
	public String getBorrowed() {
		return borrowed;
	}
	public String getSales() {
		return sales;
	}
	public void setBorrowed(String borrowed) {
		this.borrowed = borrowed;
	}
	public void setSales(String sales) {
		this.sales = sales;
	}
	public String getCashOnHand() {
		return cashOnHand;
	}
	public String getExpensesAmnt() {
		return expensesAmnt;
	}
	public String getIncomes() {
		return incomes;
	}
	public String getBoughtItems() {
		return boughtItems;
	}
	public void setCashOnHand(String cashOnHand) {
		this.cashOnHand = cashOnHand;
	}
	public void setExpensesAmnt(String expensesAmnt) {
		this.expensesAmnt = expensesAmnt;
	}
	public void setIncomes(String incomes) {
		this.incomes = incomes;
	}
	public void setBoughtItems(String boughtItems) {
		this.boughtItems = boughtItems;
	}
	public String getTotalclientcashpaidthesamedate() {
		return totalclientcashpaidthesamedate;
	}
	public void setTotalclientcashpaidthesamedate(String totalclientcashpaidthesamedate) {
		this.totalclientcashpaidthesamedate = totalclientcashpaidthesamedate;
	}
	public String getTotalCashadvance() {
		return totalCashadvance;
	}
	public void setTotalCashadvance(String totalCashadvance) {
		this.totalCashadvance = totalCashadvance;
	}
	public String getBasedInvestment() {
		return basedInvestment;
	}
	public void setBasedInvestment(String basedInvestment) {
		this.basedInvestment = basedInvestment;
	}
	public boolean isCheckSummary() {
		return checkSummary;
	}
	public void setCheckSummary(boolean checkSummary) {
		this.checkSummary = checkSummary;
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
	public String getSummaryInfo() {
		return summaryInfo;
	}
	public void setSummaryInfo(String summaryInfo) {
		this.summaryInfo = summaryInfo;
	}
}
