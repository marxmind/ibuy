package com.italia.buynsell.bean;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.italia.buynsell.controller.CashIn;
import com.italia.buynsell.controller.ClientProfile;
import com.italia.buynsell.controller.ClientTransactions;
import com.italia.buynsell.controller.Credit;
import com.italia.buynsell.controller.Debit;
import com.italia.buynsell.controller.Expenses;
import com.italia.buynsell.controller.Purchased;
import com.italia.buynsell.controller.PurchasingCorn;
import com.italia.buynsell.controller.Search;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.ei.EICashOut;
import com.italia.buynsell.ei.EICashPay;
import com.italia.buynsell.ei.EICashReturn;
import com.italia.buynsell.enm.TransStatus;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;

@ManagedBean(name="searchBean", eager=true)
@ViewScoped
public class SearchBean implements Serializable{

	List<Search> searchList = new ArrayList<>();
	private String searchString;
	private Date calendarFrom;
	private Date calendarTo;
	
	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@PostConstruct
	public void init(){
		//String search = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("search");
		load(getSearchString());
		
	}
	
	private void load(String search){
		
		searchList = Collections.synchronizedList(new ArrayList<>());
		int cnt = 1;
		cnt = ClientTransacat(cnt, search);
		cnt = ClientTransNotPaid(cnt,search);
		cnt = ClientTransPaid(cnt,search);
		cnt = Cash(cnt, search);
		cnt = CornPurchase(cnt, search);
		cnt = Purchased(cnt, search);
		cnt = Expenses(cnt, search);
		cnt = Sales(cnt, search);
		cnt = DebitCashAdvance(cnt, search);
		cnt = CASH_OUT(cnt, search);
		cnt = CASH_RETURN(cnt, search);
		cnt = CASH_PAY(cnt, search);
		
		//Collections.reverse(searchList);
	}
	
	private int DebitCashAdvance(int cnt, String search){
		
		String sql = "SELECT * FROM debit WHERE isPaid!=2 AND (dateIn>=? AND  dateIn<=?) ";
		String[] params = new String[2];
		
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		
		
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(Debit t : Debit.retrieveDebit(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("Cash Advance");
			if(t.getOthername()!=null){
				find.setF3(t.getOthername());
				find.setF6("Cash Advance");
			}else{
				find.setF3("");
				find.setF6("LEND CASH");
			}
			find.setF4(t.getDescription());
			find.setF5("ADDED CASH");
			
			find.setF7(t.getDateIn());
			find.setF8(Currency.formatAmount(t.getAmountIn().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		return cnt;
	}
	
	private int ClientTransacat(int cnt,String search){
		String sql = "SELECT * FROM client_trans WHERE transDate=? AND (paidDate>=? AND paidDate<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
			

		sql += " ORDER BY timestamp";
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("CLIENT TRANSACTION");
			find.setF3(name(t.getClientProfile().getClientId()));
			find.setF4(t.getDescription());
			find.setF5("TRANSACT");
			find.setF6(TransStatus.statusCodeToMeaning("transtype", t.getTranstype()));
			find.setF7(t.getTransDate());
			find.setF8(Currency.formatAmount(t.getTransamount().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		return cnt;
	}
	
	private int ClientTransNotPaid(int cnt,String search){
		String sql = "SELECT * FROM client_trans WHERE (transDate>=? AND transDate<=?) AND paidDate is null ";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
			

		sql += " ORDER BY timestamp";
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("CLIENT TRANSACTION");
			find.setF3(name(t.getClientProfile().getClientId()));
			find.setF4(t.getDescription());
			find.setF5("UNPAID");
			find.setF6(TransStatus.statusCodeToMeaning("transtype", t.getTranstype()));
			find.setF7(t.getTransDate());
			find.setF8(Currency.formatAmount(t.getTransamount().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		return cnt;
	}
	
	private int ClientTransPaid(int cnt, String search){
		String sql = "SELECT * FROM client_trans WHERE (paidDate>=? AND paidDate<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("CLIENT TRANSACTION");
			find.setF3(name(t.getClientProfile().getClientId()));
			find.setF4(t.getDescription());
			find.setF5(TransStatus.statusCodeToMeaning("status", t.getStatus()));
			find.setF6(TransStatus.statusCodeToMeaning("transtype", t.getTranstype()));
			find.setF7(t.getPaidDate());
			find.setF8(Currency.formatAmount(t.getTransamount().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		return cnt;
	}
	
	private int Cash(int cnt, String search){
		String sql = "SELECT * FROM cashin WHERE (dateIn>=? AND dateIn<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(CashIn t : CashIn.retrieveCashIn(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("FUNDS");
			if(t.getClientTransactions()!=null){
				find.setF3(nametrans(t.getClientTransactions().getTransId()));
				find.setF6("PAID FROM CLIENT");
			}else{
				find.setF3("");
				find.setF6("ADDITIONAL CASH");
			}
			find.setF4(t.getDescription());
			find.setF5("ADDED CASH");
			
			find.setF7(t.getDateIn());
			find.setF8(Currency.formatAmount(t.getAmountIn().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		return cnt;
	}
	
	private int CornPurchase(int cnt, String search){
		
		String sql = "SELECT * FROM purchasingcorn WHERE (datein>=? AND datein<=?) ORDER BY timestamp";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search==null){
			
			for(PurchasingCorn corn : PurchasingCorn.retrievePurchasingCorn(sql, params)){
				Search find = new Search();
				find.setF1(cnt++ + "");
				find.setF2("PURCHASING CORN");
				
				if(corn.getClientProfile().getClientId()>0){
					find.setF3(name(corn.getClientProfile().getClientId()));
					find.setF6("BUYING CORN");
				}else{
					find.setF3("UNKNOWN SELLER");
					find.setF6("BUYING CORN");
				}
				find.setF4(PurchasingCorn.conditionName(corn.getConditions()) + " kilo= " + corn.getKilo() + " discount= " + corn.getDiscount());
				find.setF5("BOUGHT");
				
				find.setF7(corn.getDateIn());
				find.setF8(Currency.formatAmount(corn.getTotalAmount()+""));
				find.setF9(corn.getProcBy());
				searchList.add(find);
			}
			
			
		}
		
		
		return cnt;
	}
	
	private int Expenses(int cnt, String search){
		
		String sql = "SELECT * FROM expenses WHERE (dateIn>=? AND dateIn<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(Expenses t : Expenses.retrieveExpenses(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("EXPENSES");
			if(t.getClientTransactions()!=null){
				find.setF3(nametrans(t.getClientTransactions().getTransId()));
				find.setF6("PAID TO CLIENT");
			}else{
				find.setF3("UNKNOWN NAME");
				find.setF6("EXPENSES");
			}
			find.setF4(t.getDescription());
			find.setF5("EXPENSES");
			
			find.setF7(t.getDateIn());
			find.setF8(Currency.formatAmount(t.getAmountIn().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		
		return cnt;
	}
	
	private int Sales(int cnt, String search){
		
		String sql = "SELECT * FROM credit WHERE (dateIn>=? AND dateIn<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(Credit t : Credit.retrieveCredit(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("SALES");
			
			find.setF3("UNKNOWN NAME");
			find.setF6("SELLING");
			
			find.setF4(t.getDescription());
			find.setF5("SOLD");
			
			find.setF7(t.getDateIn());
			find.setF8(Currency.formatAmount(t.getAmountIn().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		
		return cnt;
	}
	
	private int CASH_OUT(int cnt, String search){
		
		String sql = "SELECT * FROM ei_cash_out WHERE (dateOut>=? AND dateOut<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(EICashOut t : EICashOut.retrieveCashOut(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("EI TRANSACTION");
			
			find.setF3("ELMER ITALIA");
			find.setF6("DEBIT");
			
			find.setF4(t.getDescription());
			find.setF5("CASH OUT");
			
			find.setF7(t.getDateOut());
			find.setF8(Currency.formatAmount(t.getAmountOut().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		
		return cnt;
	}
	
	private int CASH_RETURN(int cnt, String search){
		
		String sql = "SELECT * FROM ei_cash_return WHERE (dateReturnt>=? AND dateReturnt<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(EICashReturn t : EICashReturn.retrieveCashReturn(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("EI TRANSACTION");
			
			find.setF3("ELMER ITALIA");
			find.setF6("CREDIT");
			
			find.setF4(t.getDescription());
			find.setF5("CASH RETURN");
			
			find.setF7(t.getDateReturn());
			find.setF8(Currency.formatAmount(t.getAmountReturn().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		
		return cnt;
	}
	
	private int CASH_PAY(int cnt, String search){
		
		String sql = "SELECT * FROM ei_cash_payment WHERE (datePay>=? AND datePay<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(EICashPay t : EICashPay.retrieveCashPay(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("EI TRANSACTION");
			
			find.setF3("ELMER ITALIA");
			find.setF6("CREDIT");
			
			find.setF4(t.getDescription());
			find.setF5("CASH PAID");
			
			find.setF7(t.getDatePay());
			find.setF8(Currency.formatAmount(t.getAmountPay().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		
		return cnt;
	}
	
	private int Purchased(int cnt, String search){
		
		String sql = "SELECT * FROM purchased WHERE (dateIn>=? AND dateIn<=?)";
		String[] params = new String[2];
		params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
		params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
		if(search!=null){
			sql += " AND description like'%"+ search.replace("--", "") +"%'";
		}
		
		sql += " ORDER BY timestamp";
		for(Purchased t : Purchased.retrievePurchased(sql, params)){
			Search find = new Search();
			find.setF1(cnt++ + "");
			find.setF2("PURCHASING ITEM");
			
			find.setF3("UNKNOWN");
			find.setF6("PURCHASING");
			
			find.setF4(t.getDescription());
			find.setF5("PAID");
			
			find.setF7(t.getDateIn());
			find.setF8(Currency.formatAmount(t.getAmountIn().replace(",", "")));
			find.setF9(t.getAddedBy());
			searchList.add(find);
		}
		
		
		return cnt;
	}
	
	private String name(Long id){
		String name = null;
		String sql = "SELECT fullName FROM clientprofile WHERE isactiveclient=1 AND clientId=? ";
		String[] params = new String[1];
		params[0] = id+"";
		try{name = ClientProfile.retrieveClientProfile(sql, params).get(0).getFullName();}catch(Exception e){}
		if(name==null) name="UNKNOWN NAME";
		return name;
	}
	
	private String nametrans(Long id){
		String name = null;
		Long clientId = 0l;
		String sql = "SELECT clientId FROM client_trans WHERE transId=? ";
		String[] params = new String[1];
		params[0] = id+"";
		try{clientId = ClientTransactions.retrieveClientTransacts(sql, params).get(0).getClientProfile().getClientId();}catch(Exception e){}
		sql = "SELECT clientId FROM client_trans WHERE transId=? ";
		if(clientId!=null){
			name = name(clientId);
		}else{
			name="UNKNOWN NAME";
		}
		return name;
	}
	
	public List<Search> getSearchList() {
		return searchList;
	}

	public void setSearchList(List<Search> searchList) {
		this.searchList = searchList;
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
