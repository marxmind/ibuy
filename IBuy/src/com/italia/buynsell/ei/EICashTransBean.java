package com.italia.buynsell.ei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.Fields;
import com.italia.buynsell.utils.Numbers;

@ManagedBean(name="eiBean", eager=true)
@ViewScoped
public class EICashTransBean {

///////////////////////////////////////////////////////////CASH PAY HISTORY START/////////////////////////////////////////////////////////////////////////	
	
private List<EICashPay> hispayList = new ArrayList<>();

private void inithis(){
	String sql = "SELECT * FROM ei_cash_payment WHERE status=?";
	String[] params = new String[1];
	params[0] = "2";
	hispayList = EICashPay.retrieveCashPay(sql, params);
	Collections.reverse(hispayList);
}


public List<EICashPay> getHispayList() {
	return hispayList;
}


public void setHispayList(List<EICashPay> hispayList) {
	this.hispayList = hispayList;
}
///////////////////////////////////////////////////////////CASH PAY HISTORY END/////////////////////////////////////////////////////////////////////////	

///////////////////////////////////////////////////////////CASH PAY START/////////////////////////////////////////////////////////////////////////
private List<EICashOut> outListpay = new ArrayList<>();
private List<EICashReturn> returnListpay = new ArrayList<>();
private List<EICashPay> listpay = new ArrayList<>();
private List<EICashOut> selectedout;
private List<EICashReturn> selectedreturn;

private String totalamountselectedOut;
private String totalamountselectedRet;
private String totalamountpayment;

private List<Fields> amountList = new ArrayList<>();

private String descriptionPay;

public String getDescriptionPay() {
	return descriptionPay;
}


public void setDescriptionPay(String descriptionPay) {
	this.descriptionPay = descriptionPay;
}


private void initpay(){
	outListpay = new ArrayList<>();
	returnListpay = new ArrayList<>();
	
	String sql = "SELECT * FROM ei_cash_out WHERE status=?";
	String[] params = new String[1];
	params[0] = "1";
	outListpay = EICashOut.retrieveCashOut(sql, params);
	
	sql = "SELECT * FROM ei_cash_return WHERE status=?";
	params = new String[1];
	params[0] = "3";
	returnListpay = EICashReturn.retrieveCashReturn(sql, params);
	
	
}

public void savePay(boolean isConfirm) {
	
		if(isConfirm){
		
		
			if(selectedout!=null && selectedreturn!=null){
				
				if(getDescriptionPay()!=null && !getDescriptionPay().isEmpty()){
				
				EICashPay out = new EICashPay();
				out.setStatus("PAID");
				out.setDescription(getDescriptionPay());
				out.setDatePay(DateUtils.getCurrentYYYYMMDD());
				out.setAmountPay(getTotalamountpayment());
				out.setAddedBy(getUserLogin());
				out = EICashPay.insertData(out, "3");
				
				for(EICashOut o : selectedout){
					o.setEiCashPay(out);
					o.setStatus("5"); //completed
					o.save();
				}
				
				for(EICashReturn r : selectedreturn){
					r.setCashPay(out);
					r.setStatus("5"); //completed
					r.save();
				}
				
				addMessage("The information has been successfully saved.");
				init();
				}else{
					addMessage("Please provide description in order to process this information.");
				}
				
			}
		}

	
    
}
	
public String getTotalamountpayment() {
	return totalamountpayment;
}


public void setTotalamountpayment(String totalamountpayment) {
	this.totalamountpayment = totalamountpayment;
}


public List<Fields> getAmountList() {
	System.out.println("Amount..................");
	amountList = new ArrayList<>();
	if(selectedout!=null && selectedreturn!=null){
		
		int out = selectedout.size();
		int ret = selectedreturn.size();
		if(out > ret){	
			for(int i=0; i<out; i++){
				Fields f = new Fields();
				f.setF1(Currency.formatAmount(selectedout.get(i).getAmountOut()));
				try{f.setF2(Currency.formatAmount(selectedreturn.get(i).getAmountReturn()));}catch(Exception e){
					f.setF2("0.00");
				}
				amountList.add(f);
				
			}
			
		}else if(out < ret){
				
			for(int i=0; i<ret; i++){
				Fields f = new Fields();
				try{f.setF1(Currency.formatAmount(selectedout.get(i).getAmountOut()));}catch(Exception e){
					f.setF1("0.00");
				}
				f.setF2(Currency.formatAmount(selectedreturn.get(i).getAmountReturn()));
				amountList.add(f);
				
			}
		
		}else if(out == ret){
			
			for(int i=0; i<ret; i++){
				Fields f = new Fields();
				f.setF1(Currency.formatAmount(selectedout.get(i).getAmountOut()));
				f.setF2(Currency.formatAmount(selectedreturn.get(i).getAmountReturn()));
				amountList.add(f);
				
			}
		}
		
		
		setTotalamountpayment(Currency.formatAmount((Double.valueOf(totalamountselectedOut.replace(",", "")) - Double.valueOf(totalamountselectedRet.replace(",", ""))) + ""));
		
	}
	
	
	return amountList;
}


public void setAmountList(List<Fields> amountList) {
	this.amountList = amountList;
}


public String getTotalamountselectedOut() {
	return totalamountselectedOut;
}


public void setTotalamountselectedOut(String totalamountselectedOut) {
	this.totalamountselectedOut = totalamountselectedOut;
}


public String getTotalamountselectedRet() {
	return totalamountselectedRet;
}


public void setTotalamountselectedRet(String totalamountselectedRet) {
	this.totalamountselectedRet = totalamountselectedRet;
}


public List<EICashOut> getSelectedout() {
	double amnt = 0d;
	if(selectedout!=null){
		for(EICashOut out : selectedout){
			amnt +=  Double.valueOf(out.getAmountOut());
		}
		setTotalamountselectedOut(Currency.formatAmount(amnt+""));
	}
	return selectedout;
}


public void setSelectedout(List<EICashOut> selectedout) {
	this.selectedout = selectedout;
}


public List<EICashReturn> getSelectedreturn() {
	double amnt = 0d;
	if(selectedreturn!=null){
		for(EICashReturn out : selectedreturn){
			amnt += Double.valueOf(out.getAmountReturn());
		}
		setTotalamountselectedRet(Currency.formatAmount(amnt+""));
	}
	return selectedreturn;
}


public void setSelectedreturn(List<EICashReturn> selectedreturn) {
	this.selectedreturn = selectedreturn;
}


public List<EICashOut> getOutListpay() {
	return outListpay;
}

public void setOutListpay(List<EICashOut> outListpay) {
	this.outListpay = outListpay;
}

public List<EICashReturn> getReturnListpay() {
	return returnListpay;
}

public void setReturnListpay(List<EICashReturn> returnListpay) {
	this.returnListpay = returnListpay;
}

public List<EICashPay> getListpay() {
	return listpay;
}

public void setListpay(List<EICashPay> listpay) {
	this.listpay = listpay;
}
/////////////////////////////////////////////////////////CASH PAY END/////////////////////////////////////////////////////////////////////////	

	//////////////////////////////////////////////////////////CASH RETURN START///////////////////////////////////////////////////////////////////////
	private String totalAmountRet;
	private Long idRet;
	private String descriptionRet;
	private String amountRet;
	private String dateRet;
	private String statusRet;
	private String addedByRet;
	private String timestampRet;
	private EICashPay eiCashPayRet;
	private List<EICashReturn> cashretList = new ArrayList<EICashReturn>();
	private EICashReturn cashReturn;

	
	public EICashReturn getCashReturn() {
		return cashReturn;
	}

	public void setCashReturn(EICashReturn cashReturn) {
		this.cashReturn = cashReturn;
	}

	public String getTotalAmountRet() {
		return totalAmountRet;
	}

	public void setTotalAmountRet(String totalAmountRet) {
		this.totalAmountRet = totalAmountRet;
	}

	public Long getIdRet() {
		return idRet;
	}

	public void setIdRet(Long idRet) {
		this.idRet = idRet;
	}

	public String getDescriptionRet() {
		return descriptionRet;
	}

	public void setDescriptionRet(String descriptionRet) {
		this.descriptionRet = descriptionRet;
	}

	public String getAmountRet() {
		return amountRet;
	}

	public void setAmountRet(String amountRet) {
		this.amountRet = amountRet;
	}

	public String getDateRet() {
		if(dateRet==null){dateRet=DateUtils.getCurrentYYYYMMDD();}
		return dateRet;
	}

	public void setDateRet(String dateRet) {
		this.dateRet = dateRet;
	}

	public String getStatusRet() {
		if(statusRet==null){statusRet="RECEIVED";}
		return statusRet;
	}

	public void setStatusRet(String statusRet) {
		this.statusRet = statusRet;
	}

	public String getAddedByRet() {
		return addedByRet;
	}

	public void setAddedByRet(String addedByRet) {
		this.addedByRet = addedByRet;
	}

	public String getTimestampRet() {
		return timestampRet;
	}

	public void setTimestampRet(String timestampRet) {
		this.timestampRet = timestampRet;
	}

	public EICashPay getEiCashPayRet() {
		return eiCashPayRet;
	}

	public void setEiCashPayRet(EICashPay eiCashPayRet) {
		this.eiCashPayRet = eiCashPayRet;
	}

	public List<EICashReturn> getCashretList() {
		return cashretList;
	}

	public void setCashretList(List<EICashReturn> cashretList) {
		this.cashretList = cashretList;
	}
	
	public void loadDeletedRet(ActionEvent actionEvent){
		String sql = "SELECT * FROM ei_cash_return WHERE dateReturn=? AND status=?";
		String params[] = new String[2];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		params[1] = "9";
		actionsRetStatus(sql, params);
	}
	
	public void loadReceivedRet(ActionEvent actionEvent){
		String sql = "SELECT * FROM ei_cash_return WHERE dateReturn=? AND status=?";
		String params[] = new String[2];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		params[1] = "3";
		actionsRetStatus(sql, params);
	}
	
	public void loadAllRet(ActionEvent actionEvent){
		String sql = "SELECT * FROM ei_cash_return WHERE dateReturn=?";
		String params[] = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		actionsRetStatus(sql, params);
	}
	
	public void updateRet(ActionEvent actionEvent) {
        addMessage("Data updated");
    }
	
	private void actionsRetStatus(String sql, String[] params){
		cashretList = new ArrayList<EICashReturn>();
		Double amnt = 0d;
		Double tmpAmnt = 0d;
		for(EICashReturn out : EICashReturn.retrieveCashReturn(sql, params)){
			
			String status = out.getStatus().equalsIgnoreCase("3")? "RECEIVED" :
					out.getStatus().equalsIgnoreCase("9")? "DELETED" : "ERROR";
			out.setStatus(status);
			
			amnt = Double.valueOf(out.getAmountReturn());
			
			out.setAmountReturn(Currency.formatAmount(out.getAmountReturn()));
			cashretList.add(out);
			
			tmpAmnt += amnt;
			
			
		}
		setTotalAmountRet(Currency.formatAmount(Numbers.formatDouble(tmpAmnt)+""));
		Collections.reverse(cashretList);
		//addMessage("Please wait while loading the data... ");
	}
	
	private void initRet(){
		String sql = "SELECT * FROM ei_cash_return WHERE dateReturn=? AND status!=?";
		String params[] = new String[2];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		params[1] = "5";
		actionsRetStatus(sql, params);
	}
	
	public void clearRet(){
		setDescriptionRet(null);
		setAmountRet(null);
		setCashReturn(null);
	}
	
	public void deleteRowRet(EICashReturn out, boolean isConfirm){
		System.out.println("id of deletetion: " + out.getIdRet());
		if(isConfirm){
			EICashReturn.deleteData(out);
			initRet();
		}
	}
	
	public void clickItemRet(EICashReturn out){
		System.out.println("click item id: " + out.getIdRet());
		setCashReturn(out);
		setIdRet(out.getIdRet());
		setDateRet(out.getDateReturn());
		setStatusRet(out.getStatus());
		setDescriptionRet(out.getDescription());
		setAmountRet(out.getAmountReturn());
	}
	
	public void saveRet(ActionEvent actionEvent) {
		String msgs = "";
		int i=0;
		if(getDescriptionRet()==null){
			msgs = "Description";
			i++;
		}else if(getDescriptionRet().isEmpty()){
			msgs = "Description";
			i++;
		}
		
		if(getAmountRet()==null){
			if(i==1){
				msgs += ",Amount";
				i++;
			}else{
				msgs = "Amount";
				i++;
			}
		}else if(getAmountRet().isEmpty()){
			if(i==1){
				msgs += ",Amount";
				i++;
			}else{
				msgs = "Amount";
				i++;
			}
		}
		
		if(i>0){
			addMessage("Please provide information for " + msgs + ".");
		}else{
			
			EICashReturn out = new EICashReturn();
			if(getCashReturn()!=null) out = getCashReturn();
			String status = "";
			status = getStatusRet().equalsIgnoreCase("RECEIVED")? "3" : getStatusOut().equalsIgnoreCase("DELETED")? "9" : "0";
			out.setDateReturn(getDateRet());
			out.setDescription(getDescriptionRet());
			out.setStatus(status);
			
			String amnt = "";
			amnt = Currency.removeCurrencySymbol(getAmountRet(), "");
			amnt = Currency.removeComma(amnt);
			
			out.setAmountReturn(amnt);
			out.setAddedBy(getUserLogin());
			
			try{Double.valueOf(amnt);
			out.save();
			clearRet();
			init();
			addMessage("The information has been successfully saved.");
			}catch(NumberFormatException e)
			{
				addMessage("Error in saving please provide only number in amount field.");
			}
		}
		
        
    }
//////////////////////////////////////////////////////////CASH RETURN START///////////////////////////////////////////////////////////////////////	
	
//////////////////////////////////////////////////////////CASH OUT START///////////////////////////////////////////////////////////////////////
	
	private EICashOut eiCashOut;
	
	public EICashOut getEiCashOut() {
		return eiCashOut;
	}

	public void setEiCashOut(EICashOut eiCashOut) {
		this.eiCashOut = eiCashOut;
	}

	private String totalAmount;
	
	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	private Long idOut;
	private String descriptionOut;
	private String amountOut;
	private String dateOut;
	private String statusOut;
	private String addedByOut;
	private String timestampOut;
	private EICashPay eiCashPayOut;
	private List<EICashOut> cashoutList = new ArrayList<EICashOut>();
	
	public void loadDeletedOut(ActionEvent actionEvent){
		String sql = "SELECT * FROM ei_cash_out WHERE dateOut=? AND status=?";
		String params[] = new String[2];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		params[1] = "9";
		actionsOutStatus(sql, params);
	}
	
	public void loadNewOut(ActionEvent actionEvent){
		String sql = "SELECT * FROM ei_cash_out WHERE dateOut=? AND status=?";
		String params[] = new String[2];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		params[1] = "1";
		actionsOutStatus(sql, params);
	}
	
	public void loadPaidOut(ActionEvent actionEvent){
		String sql = "SELECT * FROM ei_cash_out WHERE dateOut=? AND status=?";
		String params[] = new String[2];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		params[1] = "2";
		actionsOutStatus(sql, params);
	}
	
	public void loadAllOut(ActionEvent actionEvent){
		String sql = "SELECT * FROM ei_cash_out WHERE dateOut=?";
		String params[] = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		actionsOutStatus(sql, params);
	}
	
	private void actionsOutStatus(String sql, String[] params){
		cashoutList = new ArrayList<EICashOut>();
		Double amnt = 0d;
		Double tmpAmnt = 0d;
		for(EICashOut out : EICashOut.retrieveCashOut(sql, params)){
			
			String status = out.getStatus().equalsIgnoreCase("1")? "NEW" : 
				out.getStatus().equalsIgnoreCase("2")? "PAID" : 
					out.getStatus().equalsIgnoreCase("9")? "DELETED" : "ERROR";
			out.setStatus(status);
			
			amnt = Double.valueOf(out.getAmountOut());
			
			out.setAmountOut(Currency.formatAmount(out.getAmountOut()));
			cashoutList.add(out);
			
			tmpAmnt += amnt;
			
			
		}
		setTotalAmount(Currency.formatAmount(Numbers.formatDouble(tmpAmnt)+""));
		Collections.reverse(cashoutList);
		//addMessage("Please wait while loading the data... ");
	}
	
	private void initOut(){
		String sql = "SELECT * FROM ei_cash_out WHERE dateOut=? AND status!=?";
		String params[] = new String[2];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		params[1] = "5";
		actionsOutStatus(sql, params);
	}
	
	public void clearOut(){
		setDescriptionOut(null);
		setAmountOut(null);
		setEiCashOut(null);
	}
	
	public void deleteRowOut(EICashOut out, boolean isConfirm){
		System.out.println("id of deletetion: " + out.getIdOut());
		if(isConfirm){
			EICashOut.deleteData(out);
			initOut();
		}
	}
	
	public void clickItemOut(EICashOut out){
		System.out.println("click item id: " + out.getIdOut());
		setEiCashOut(out);
		setIdOut(out.getIdOut());
		setDateOut(out.getDateOut());
		setStatusOut(out.getStatus());
		setDescriptionOut(out.getDescription());
		setAmountOut(out.getAmountOut());
		
	}
	
	public List<EICashOut> getCashoutList() {
		return cashoutList;
	}

	public void setCashoutList(List<EICashOut> cashoutList) {
		this.cashoutList = cashoutList;
	}

	public void saveOut(ActionEvent actionEvent) {
		String msgs = "";
		int i=0;
		if(getDescriptionOut()==null){
			msgs = "Description";
			i++;
		}else if(getDescriptionOut().isEmpty()){
			msgs = "Description";
			i++;
		}
		
		if(getAmountOut()==null){
			if(i==1){
				msgs += ",Amount";
				i++;
			}else{
				msgs = "Amount";
				i++;
			}
		}else if(getAmountOut().isEmpty()){
			if(i==1){
				msgs += ",Amount";
				i++;
			}else{
				msgs = "Amount";
				i++;
			}
		}
		
		if(i>0){
			addMessage("Please provide information for " + msgs + ".");
		}else{
			
			EICashOut out = new EICashOut();
			if(getEiCashOut()!=null) out = getEiCashOut();
			
			String status = "";
			status = getStatusOut().equalsIgnoreCase("NEW")? "1" : getStatusOut().equalsIgnoreCase("PAID")? "2" : "0";
			out.setDateOut(getDateOut());
			out.setDescription(getDescriptionOut());
			out.setStatus(status);
			
			String amnt = "";
			amnt = Currency.removeCurrencySymbol(getAmountOut(), "");
			amnt = Currency.removeComma(amnt);
			
			out.setAmountOut(amnt);
			out.setAddedBy(getUserLogin());
			try{Double.valueOf(amnt);
			out.save();
			clearOut();
			init();
			addMessage("The information has been successfully saved.");
			}catch(NumberFormatException e)
			{
				addMessage("Error in saving please provide only number in amount field.");
			}
		}
		
        
    }
	
	public void updateOut(ActionEvent actionEvent) {
        addMessage("Data updated");
    }
     
    public void deleteOut(ActionEvent actionEvent) {
        addMessage("Data deleted");
    }
	
//////////////////////////////////////////////////////////CASH OUT END///////////////////////////////////////////////////////////////////////
    
	 public void addMessage(String summary) {
	        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  null);
	        FacesContext.getCurrentInstance().addMessage(null, message);
	    }
	
	private String getUserLogin(){
		try{
			HttpSession session = SessionBean.getSession();
			String proc_by = session.getAttribute("username").toString();
			return proc_by;
			}catch(Exception e){
				return "error";
		}
	}
	
	@PostConstruct
	public void init(){
		System.out.println("========init=========");
		initOut();
		initRet();
		initpay();
		inithis();
	}

	public Long getIdOut() {
		return idOut;
	}

	public void setIdOut(Long idOut) {
		this.idOut = idOut;
	}

	public String getDescriptionOut() {
		return descriptionOut;
	}

	public void setDescriptionOut(String descriptionOut) {
		this.descriptionOut = descriptionOut;
	}

	public String getAmountOut() {
		return amountOut;
	}

	public void setAmountOut(String amountOut) {
		this.amountOut = amountOut;
	}

	public String getDateOut() {
		if(dateOut==null){dateOut = DateUtils.getCurrentYYYYMMDD();}
		return dateOut;
	}

	public void setDateOut(String dateOut) {
		this.dateOut = dateOut;
	}

	public String getStatusOut() {
		if(statusOut==null){statusOut="NEW";}
		return statusOut;
	}

	public void setStatusOut(String statusOut) {
		this.statusOut = statusOut;
	}

	public String getAddedByOut() {
		return addedByOut;
	}

	public void setAddedByOut(String addedByOut) {
		this.addedByOut = addedByOut;
	}

	public String getTimestampOut() {
		return timestampOut;
	}

	public void setTimestampOut(String timestampOut) {
		this.timestampOut = timestampOut;
	}

	public EICashPay getEiCashPayOut() {
		return eiCashPayOut;
	}

	public void setEiCashPayOut(EICashPay eiCashPayOut) {
		this.eiCashPayOut = eiCashPayOut;
	}


	
	
}
