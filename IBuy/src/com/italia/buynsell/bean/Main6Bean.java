package com.italia.buynsell.bean;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import com.italia.buynsell.controller.ClientProfile;
import com.italia.buynsell.controller.ClientTransactions;
import com.italia.buynsell.controller.PurchasingCorn;
import com.italia.buynsell.enm.TransStatus;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;

@Named("main6Bean")
@ViewScoped
public class Main6Bean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6676885454651L;
	
	private String searchString;
	private String totalCollectible;
	private List<ClientTransactions> clientTrans;
	private List<ClientProfile> clientList = new ArrayList<ClientProfile>();
	private final String sep = File.separator;
	private final String PRIMARY_DRIVE = System.getenv("SystemDrive");
	private final String IMAGE_PATH = PRIMARY_DRIVE + sep + "BuyNSell" + sep + "images" + sep ;
	private String notes;
	@PostConstruct
	public void init() {
		
		System.out.println("running on Main6Bean....");
		
		if(getTotalCollectible()==null) setTotalCollectible("0.00");
		if(getTotalCollectible().isEmpty()) setTotalCollectible("0.00");
		String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 ";
		String[] params = new String[0];
		clientList = new ArrayList<>();
		if(getSearchString()!=null && !getSearchString().isEmpty()){
			sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND fullName like '%"+ getSearchString().replace("--", "") +"%'";
			sql += " OR contactNumber like '%"+ getSearchString().replace("--", "") +"%'";
		}else{
			sql += " ORDER BY clientId DESC limit 4";
		}
		
		Double amnt = 0d;
		String collamnt = "0.00";
		List<ClientProfile> pList = ClientProfile.retrieveClientProfile(sql, params); 
		
		if(pList.size()==1){
			collamnt = collectibleAmnt(pList.get(0));
			amnt += Double.valueOf(Currency.removeCurrencySymbol(collamnt, "")); //collamnt.replace(",", ""));
			pList.get(0).setAmntCollectible(collamnt);
			clientList.add(pList.get(0));
			writeImageToFile(pList.get(0).getPicturePath());
			loadDatails(pList.get(0));
		}else{
			for(ClientProfile p : pList){
				collamnt = collectibleAmnt(p);
				amnt += Double.valueOf(Currency.removeCurrencySymbol(collamnt, ""));// collamnt.replace(",", ""));
				p.setAmntCollectible(collamnt);
				clientList.add(p);
			}
			String note = "";
				if(getSearchString()==null || getSearchString().isEmpty()) {
					note = "<p>Today is " + DateUtils.convertDateToMonthDayYear(DateUtils.getCurrentDateYYYYMMDD()) + "</>";
				}else {
					note = "<p>Search results("+ pList.size() +") for the keyword <strong>"+ getSearchString() +"</strong></p>";
				}
			setNotes(note);
		}
		if(getSearchString()==null || getSearchString().isEmpty()) {
			PrimeFaces pm = PrimeFaces.current();
			pm.executeScript("showDashboard()");
		}
		setTotalCollectible(Currency.formatAmount(amnt+""));
	}
	
	private void writeImageToFile(String photoid){
		
			File fileImg = new File(IMAGE_PATH + photoid + ".jpg");
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String contextImageLoc = "resources" + File.separator + "images" + File.separator;
			String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            System.out.println("Path: " + pathToSave);
            try{
    			Files.copy(fileImg.toPath(), (new File(pathToSave + fileImg.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            
		
	}
	
	
	public void loadDatails(ClientProfile p) {
		String note = "<p>Please see below details</p>";
		note += "<p>Name: <strong>" + p.getFullName()+"</strong></p>";
		note += "<p>Address: <strong>" + p.getAddress()+"</strong></p>";
		note += "<p>Guarantor/Contact No: <strong>" + p.getContactNumber()+"</strong></p>";
		note += "<p>Current Borrowed Money: <strong>Php" + Currency.formatAmount(p.getAmntCollectible())+"</strong></p>";
		
		//currenct payable amount
		String sql = "SELECT * FROM client_trans WHERE status=1 AND clientId=?";
		String[] params = new String[1];
		params[0] = p.getClientId()+"";
		Double amnt = 0d;
		note += "<br/>";
		note += "<p>Transaction:</p>";
		String transNote = "";
		transNote += "<ul>";
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			amnt += Double.valueOf(t.getTransamount().replace(",", ""));
			transNote += "<li>"+TransStatus.statusCodeToMeaning("transtype", t.getTranstype());
			transNote +="<ul>";
			transNote += "<li>" + t.getTransDate() + " - " + TransStatus.statusCodeToMeaning("status", t.getStatus()) +" ("+ Currency.formatAmount(t.getTransamount()) +")</li>";
			transNote +="</ul>";
			transNote +="</li>";
		}
		transNote += "</ul>";
		if(amnt>0) {
			note += "<p>Total of <strong>Php"+Currency.formatAmount(amnt) +"</strong> for the following details.<p>";
			note += transNote;
		}else {
			note += "<p><strong>No borrowed amount</strong></p>";
		}
		//paid customer transaction
		sql = "SELECT * FROM client_trans WHERE (status=2 OR status=3) AND clientId=? ORDER BY transId DESC LIMIT 10";
		params = new String[1];
		params[0] = p.getClientId()+"";
		transNote = "";
		amnt = 0d;
		note += "<br/><p>History Transaction:</p>";
		transNote += "<ul>";
		int cnt = 0;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			amnt += Double.valueOf(t.getTransamount().replace(",", ""));
			transNote += "<li>"+TransStatus.statusCodeToMeaning("transtype", t.getTranstype());
			transNote +="<ul>";
			transNote += "<li> Paid on " + t.getPaidDate() + " - " + TransStatus.statusCodeToMeaning("status", t.getStatus()) +" ("+ Currency.formatAmount(t.getTransamount()) +")</li>";
			transNote +="</ul>";
			transNote +="</li>";
			cnt++;
		}
		transNote += "</ul>";
		if(amnt>0) {
			note += "<p>Total of <strong>Php"+Currency.formatAmount(amnt) +"</strong> for the following details.<p>";
			note += transNote;
			if(cnt>9) {
				note +="<p>Please note that this person has more than ten transactions. The system limit only in 10. For more information for this person transaction go to client profile for full details</p><br/>";
			}
		}else {
			note += "<p><strong>No history yet</strong></p>";
		}
		
		sql = "SELECT * FROM purchasingcorn WHERE clientId=? ORDER BY chasedid DESC LIMIT 10";
		params = new String[1];
		params[0] = p.getClientId()+"";
		
		transNote = "";
		note += "<br/><p>History for selling a corn:</p>";
		transNote += "<ul>";
		boolean hasCorn = false;
		cnt = 0;
		for(PurchasingCorn corns : PurchasingCorn.retrievePurchasingCorn(sql, params)){
			transNote += "<li>" + corns.getDateIn();
			transNote += "<ul>";
			transNote += "<li>" + corns.getDateIn() + "</li>";
			transNote += "<li>" + PurchasingCorn.conditionName(corns.getConditions()) + " kilo= " + corns.getKilo() + " discount= " + corns.getDiscount() + "</li>";
			transNote += "<li>" + Currency.formatAmount(corns.getTotalAmount()+"") + "</li>";
			transNote += "</ul>";
			transNote += "</li>";
			hasCorn = true;
			cnt++;
		}
		transNote += "</ul>";
		if(hasCorn) {
			note += "<p>Below are the details of corn purchased</p>";
			note += transNote;
			if(cnt>9) {
				note +="<p>Please note that this person has more than ten transactions. The system limit only in 10. For more information for this person transaction go to client profile for full details</p><br/>";
			}
		}else {
			note += "<p>No purchased corn yet</p>";
		}
		
		setNotes(note);
	}
	
	private String collectibleAmnt(ClientProfile clientProfile){
		String amnt = null;
		try{
		String sql = "SELECT transamount as transamount FROM client_trans WHERE clientId=? AND status=1";
		String[] params = new String[1];
		params[0] = clientProfile.getClientId()+"";
		double amntTrans = 0d;
		for(ClientTransactions clientAmount : ClientTransactions.retrieveClientTransacts(sql, params)){
			amntTrans += Double.valueOf(clientAmount.getTransamount().replace(",", ""));
		}
		//try{amnt = Currency.formatAmount(ClientTransactions.retrieveClientTransacts(sql, params).get(0).getTransamount());}catch(Exception e){}
		try{amnt = Currency.formatAmount(amntTrans+"");}catch(Exception e){}
		if(amnt==null){
			amnt="0.00";
		}
		if(amnt.isEmpty()){
			amnt="0.00";
		}
		
		}catch(Exception e){}
		return amnt;
	}
	
	
	
	
	
	public List<ClientTransactions> getClientTrans() {
		return clientTrans;
	}
	public void setClientTrans(List<ClientTransactions> clientTrans) {
		this.clientTrans = clientTrans;
	}
	public String getTotalCollectible() {
		return totalCollectible;
	}

	public void setTotalCollectible(String totalCollectible) {
		this.totalCollectible = totalCollectible;
	}



	public String getSearchString() {
		return searchString;
	}



	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}



	public List<ClientProfile> getClientList() {
		return clientList;
	}



	public void setClientList(List<ClientProfile> clientList) {
		this.clientList = clientList;
	}

	public String getNotes() {
		if(notes==null) {
			notes = "<p><h1>Hi There, welcome to Buy and Sell Software, your portal for Information</h1></p>";
		}
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

}
