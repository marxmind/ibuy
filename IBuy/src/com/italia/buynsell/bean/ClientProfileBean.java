package com.italia.buynsell.bean;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.inject.Named;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CaptureEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

import com.italia.buynsell.controller.CashIn;
import com.italia.buynsell.controller.ClientDocs;
import com.italia.buynsell.controller.ClientProfile;
import com.italia.buynsell.controller.ClientTransactions;
import com.italia.buynsell.controller.Employees;
import com.italia.buynsell.controller.Expenses;
import com.italia.buynsell.controller.PurchasingCorn;
import com.italia.buynsell.controller.ReadApplicationDetails;
import com.italia.buynsell.controller.StatusTrans;
import com.italia.buynsell.enm.TransStatus;
import com.italia.buynsell.utils.Application;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.LogUserActions;
import com.italia.buynsell.utils.StringUtils;

@Named("clientBean")
@ViewScoped
public class ClientProfileBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 14646873365L;
	private Long clientId;
	private String registeredDate;
	private String contactNumber;
	private String fullName;
	private String address;
	private String addedBy;
	private ClientProfile clientProfile;
	private List<ClientProfile> clientList = new ArrayList<>();
	private final String sep = File.separator;
	private final String PRIMARY_DRIVE = System.getenv("SystemDrive");
	private final String IMAGE_PATH = PRIMARY_DRIVE + sep + "BuyNSell" + sep + "images" + sep ;
	private String photoId="tmp";
	private List<String> shots = new ArrayList<>();
	
	private final String DOC_PATH = "C:" + sep + "BuyNSell" + sep + "documents" + sep ;
	private String totalCollectible;
	
	
	
	//Documents tab
	private ClientDocs clientDocs;
	private String documentDate;
	private String documentName;
	private String documentPath;
	private String documentDescription;
	private InputStream docImage;
	private BufferedImage docImageTemp;
	private List<ClientDocs> docList = new ArrayList<>();
	private String docFileExt;
	
	//transaction/history tab
	private List<ClientTransactions> transList = new ArrayList<>();
	private List<ClientTransactions> transhisList = new ArrayList<>();
	private ClientTransactions clientTransactions;
	private String statusId;
	private List statusList;
	private String transactTypeId;
	private List transactTypeList;
	private String clientRateId;
	private List clientRateList;
	private String description;
	private String transDate;
	private String transamount;
	private String interestrate;
	private String interestamount;
	private String paidamount;
	private String paidDate;
	private String notes;
	private ClientProfile profile;
	private Employees employee;
	private boolean checkIsPaid;
	private File tmpFileLoad;
	private String totalTrans;
	private String totalTranshis;
	private Date transDueDate;
	
	private String searchClientParam;
	private Date calendarFrom;
	private Date calendarTo;
	private List<ClientTransactions> clientTrans;
	private String totalNowTransactions;
	private boolean paidOnly;
	
	private String transactTypeId2;
	private List transactTypeList2;
	private boolean summaryOnly;
	
	private String tabName;
	
	public boolean isCheckIsPaid() {
		if("1".equalsIgnoreCase(getStatusId())){
			checkIsPaid = true;
		}
		return checkIsPaid;
	}

	public void setCheckIsPaid(boolean checkIsPaid) {
		this.checkIsPaid = checkIsPaid;
	}


	private String searchString;
	
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

	public ClientProfile getClientProfile() {
		return clientProfile;
	}

	public void setClientProfile(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
	}
	
	public void clientLoadTrans() {
		
		if(isSummaryOnly()) {
			clientLoadTransSummary();
		}else {
		
		clientTrans = Collections.synchronizedList(new ArrayList<ClientTransactions>());
		
		String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND fullName like '%"+ getSearchClientParam().replace("--", "") +"%' ";
		String[] params = new String[0];
		
		double amount = 0d;
		if(getSearchClientParam()!=null && !getSearchClientParam().isEmpty()) {
			for(ClientProfile pro : ClientProfile.retrieveClientProfile(sql, params)) {
				sql = "SELECT * FROM client_trans WHERE clientId=?";
				params = new String[3];
				if(isPaidOnly()) {
					sql += " AND (paidDate>=? AND paidDate<=?) AND status!=1";
				}else {
					sql += " AND (transDate>=? AND transDate<=?) AND status=1";
				}
				params[0] = pro.getClientId()+"";
				params[1] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
				params[2] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
				
				if(getTransactTypeId()!=null && !getTransactTypeId().isEmpty()) {
					sql += " AND transtype="+getTransactTypeId();
				}
				
				
				for(ClientTransactions client : ClientTransactions.retrieveClientTransacts(sql, params)) {
					client.setClientProfile(pro);
					client.setTranstype(TransStatus.statusCodeToMeaning("transtype", client.getTranstype()));
					clientTrans.add(client);
					try{amount += Double.valueOf(client.getTransamount().replace(",", ""));}catch(NullPointerException e) {}
				}
				
			}
			
		}else {
			sql = "SELECT * FROM client_trans WHERE ";
			params = new String[2];
			if(isPaidOnly()) {
				sql += " (paidDate>=? AND paidDate<=?) AND status!=1";
			}else {
				sql += " (transDate>=? AND transDate<=?) AND status=1";
			}
			params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
			params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
			if(getTransactTypeId()!=null && !getTransactTypeId().isEmpty()) {
				sql += " AND transtype="+getTransactTypeId();
			}
			
			for(ClientTransactions cl :  ClientTransactions.retrieveClientTransacts(sql, params)) {
				sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND clientId=" + cl.getClientProfile().getClientId();
				try{cl.setClientProfile(ClientProfile.retrieveClientProfile(sql, new String[0]).get(0));}catch(Exception e) {}
				cl.setTranstype(TransStatus.statusCodeToMeaning("transtype", cl.getTranstype()));
				clientTrans.add(cl);
				try{amount += Double.valueOf(cl.getTransamount().replace(",", ""));}catch(NullPointerException e) {}
			}
			
			
		}
		if(isPaidOnly()) {
			setTotalNowTransactions("Total Paid Php: "+Currency.formatAmount(amount));
		}else {
			setTotalNowTransactions("Total Unpaid Php: "+Currency.formatAmount(amount));
		}
		Collections.reverse(clientTrans);
		}
	}
	
	public void clientLoadTransSummary() {
		
		if(isSummaryOnly()) {
		
		clientTrans = Collections.synchronizedList(new ArrayList<ClientTransactions>());
		
		String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND fullName like '%"+ getSearchClientParam().replace("--", "") +"%' ";
		String[] params = new String[0];
		
		Map<String, ClientTransactions> mapTrans = Collections.synchronizedMap(new LinkedHashMap<String, ClientTransactions>());
		
		double amount = 0d;
		if(getSearchClientParam()!=null && !getSearchClientParam().isEmpty()) {
			for(ClientProfile pro : ClientProfile.retrieveClientProfile(sql, params)) {
				sql = "SELECT * FROM client_trans WHERE clientId=?";
				params = new String[3];
				if(isPaidOnly()) {
					sql += " AND (paidDate>=? AND paidDate<=?) AND status!=1";
				}else {
					sql += " AND (transDate>=? AND transDate<=?) AND status=1";
				}
				params[0] = pro.getClientId()+"";
				params[1] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
				params[2] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
				
				if(getTransactTypeId()!=null && !getTransactTypeId().isEmpty()) {
					sql += " AND transtype="+getTransactTypeId();
				}
				
				for(ClientTransactions client : ClientTransactions.retrieveClientTransacts(sql, params)) {
					String type = client.getTranstype() + "-" + pro.getFullName();
					double total = 0d;
					double paidA = 0d;
					client.setClientProfile(pro);
					try{total = Double.valueOf(client.getTransamount().replace(",", ""));}catch(Exception e) {}
					try{paidA = Double.valueOf(client.getPaidamount().replace(",", ""));}catch(Exception e) {}
					String desc = client.getDescription();
					if(mapTrans!=null && mapTrans.size()>0) {
						if(mapTrans.containsKey(type)) {
							try{total += Double.valueOf(mapTrans.get(type).getTransamount().replace(",", ""));}catch(Exception e) {}
							try{paidA += Double.valueOf(mapTrans.get(type).getPaidamount().replace(",", ""));}catch(Exception e) {}
							desc = mapTrans.get(type).getDescription() + ", " + desc;
							mapTrans.get(type).setTransamount(total+"");
							mapTrans.get(type).setPaidamount(paidA+"");
							mapTrans.get(type).setDescription(desc);
						}else {
							client.setTransDate(params[0] + "-" + params[1]);
							mapTrans.put(type, client);
						}
					}else {
						client.setTransDate(params[0] + "-" + params[1]);
						mapTrans.put(type, client);
					}
				}
			}
			
			for(String type : mapTrans.keySet()) {
				System.out.println("type keyset>>> " + type);
				ClientTransactions client = mapTrans.get(type);
				client.setTranstype(TransStatus.statusCodeToMeaning("transtype", client.getTranstype()));
				client.setTimeStamp("***NO DATE***");
				String desc = client.getDescription();
				try{desc = client.getDescription().substring(0,15) + "...";}catch(Exception e) {}
				client.setDescription(desc);
				try{client.setTransamount(Currency.formatAmount(client.getTransamount()));}catch(Exception e) {}
				try{client.setPaidamount(Currency.formatAmount(client.getPaidamount()));}catch(Exception e) {}
				clientTrans.add(client);
				try{amount += Double.valueOf(client.getTransamount().replace(",", ""));}catch(NullPointerException e) {}
			}
			
		}else {
			sql = "SELECT * FROM client_trans WHERE ";
			params = new String[2];
			if(isPaidOnly()) {
				sql += " (paidDate>=? AND paidDate<=?) AND status!=1";
			}else {
				sql += " (transDate>=? AND transDate<=?) AND status=1";
			}
			params[0] = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
			params[1] = DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
			if(getTransactTypeId()!=null && !getTransactTypeId().isEmpty()) {
				sql += " AND transtype="+getTransactTypeId();
			}
			
			for(ClientTransactions cl :  ClientTransactions.retrieveClientTransacts(sql, params)) {
				sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND clientId=" + cl.getClientProfile().getClientId();
				try{cl.setClientProfile(ClientProfile.retrieveClientProfile(sql, new String[0]).get(0));}catch(Exception e) {}
				cl.setTranstype(TransStatus.statusCodeToMeaning("transtype", cl.getTranstype()));
				clientTrans.add(cl);
				try{amount += Double.valueOf(cl.getTransamount().replace(",", ""));}catch(NullPointerException e) {}
			}
			
			
		}
		if(isPaidOnly()) {
			setTotalNowTransactions("Total Paid Php: "+Currency.formatAmount(amount));
		}else {
			setTotalNowTransactions("Total Unpaid Php: "+Currency.formatAmount(amount));
		}
		Collections.reverse(clientTrans);
		}else {
			clientLoadTrans();
		}
	}
	
	@PostConstruct
	public void init(){
		System.out.println("ClientProfile init...");
		
		try{
			String name = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("editProfile");
			System.out.println("Check pass name: " + name);
			if(name!=null && !name.isEmpty() && !"null".equalsIgnoreCase(name)){
				setSearchString(name);
			}
			}catch(Exception e){}
		
		loadSearch();
	}
	
	public List transStatus(String type,String id){
		String sql = "SELECT * FROM status_trans WHERE use_table='client_trans' ";
		String params[] = new String[0];
		List tlist = new ArrayList<>();
		if("0".equalsIgnoreCase(id)){
			sql += " AND status_type=?";
			params = new String[1];
			params[0] = type;
			
			//System.out.println("SQL trans : " + sql);
			for(StatusTrans t : StatusTrans.retrieveStatusTrans(sql, params)){
				tlist.add(new SelectItem(t.getStatusNumber(),t.getStatusName()));
			}
			
		}else{
			sql += " AND status_type=? AND status_number=?";
			params = new String[2];
			params[0] = type;
			params[1] = id;
			//System.out.println("SQL trans : " + sql);
			for(StatusTrans t : StatusTrans.retrieveStatusTrans(sql, params)){
				tlist.add(t.getStatusName());
				
			}
		}
		
		
		return tlist;
	}
	
	
	public List<ClientTransactions> getTranshisList() {
		return transhisList;
	}

	public void setTranshisList(List<ClientTransactions> transhisList) {
		this.transhisList = transhisList;
	}
	
	public void deleteRow(ClientProfile clientProfile){
		
			if(!isExist(clientProfile)){
				List<String> actions = new ArrayList<>();
				clientProfile.delete();
				/*String sql = "DELETE FROM clientprofile WHERE clientId=?";
				String[] params = new String[1];
				params[0] = clientProfile.getClientId()+"";
				ClientProfile.delete(sql, params);
				actions.add("["+ processBy() +"]");
				actions.add("deleting id " + params[0] +" in clientprofile");*/
				File f = new File(clientProfile.getPicturePath());
				if(f.exists()){actions.add("deleting image " + f.getName());f.delete();}
				actions.add("Successfully deleted...");
				LogUserActions.logUserActions(actions);
				init();
				Application.addMessage(1, "Success", "Successfully deleted");
			}
		
	}
	
	@Deprecated
	public void deleteRow(ClientProfile clientProfile, boolean isConfirm){
		if(isConfirm){
			if(!isExist(clientProfile)){
				List<String> actions = new ArrayList<>();
				String sql = "DELETE FROM clientprofile WHERE clientId=?";
				String[] params = new String[1];
				params[0] = clientProfile.getClientId()+"";
				ClientProfile.delete(sql, params);
				actions.add("["+ processBy() +"]");
				actions.add("deleting id " + params[0] +" in clientprofile");
				File f = new File(clientProfile.getPicturePath());
				if(f.exists()){actions.add("deleting image " + f.getName());f.delete();}
				actions.add("Successfully deleted...");
				LogUserActions.logUserActions(actions);
				init();
			}
		}
	}
	
	private boolean isExist(ClientProfile clientProfile){
		
		String sql = "SELECT * FROM client_trans WHERE clientId=? limit 2";
		String[] params = new String[1];
		params[0] = clientProfile.getClientId()+"";
		System.out.println("Delete sql : " + sql);
		int size = ClientTransactions.retrieveClientTransacts(sql, params).size();
		System.out.println("isExist size=" + size);
		if(size>0){
			System.out.println("subject for not deletion... client id = " + clientProfile.getClientId());	
			return true;
		}
		
		
		return false;
	}
	
	public void deleteRowTrans(ClientTransactions trans, boolean isConfirm){
		if(isConfirm){
		List<String> actions = new ArrayList<>();	
		String sql = "DELETE FROM client_trans WHERE transId=?";
		String[] params = new String[1];
		params[0] = trans.getTransId()+"";
		ClientTransactions.delete(sql, params);
		actions.add("["+ processBy() +"]");
		actions.add("deleting id " + params[0] +" in client_trans");
		actions.add("Successfully deleted...");
		LogUserActions.logUserActions(actions);
		transInit();
		}
	}
	
	public void clickItem(ClientProfile clientProfile){
		setTabName("Client Information");
		setClientProfile(clientProfile);
		setRegisteredDate(clientProfile.getRegisteredDate());
		setContactNumber(clientProfile.getContactNumber());
		setFullName(clientProfile.getFullName());
		setAddress(clientProfile.getAddress());
		setPhotoId(clientProfile.getPicturePath());
		transInit();
		docinit();
	}
	
	public void onChange(TabChangeEvent event) {
		System.out.println("Tab>> " + event.getTab().getTitle());
		setTabName(event.getTab().getTitle());
	}
	
	public void buttonAction(String buttonType) {
		if("new".equalsIgnoreCase(buttonType)) {
				clearFields();
				setTabName("Client Information");
		}else if("clear".equalsIgnoreCase(buttonType)) {
			
			if("Client Information".equalsIgnoreCase(getTabName())) {
				clearFields();
			}else if("Documents".equalsIgnoreCase(getTabName())) {
				clearDocs();
			}else if("Transactions".equalsIgnoreCase(getTabName())) {
				clearTrans();
			}
			
		}else if("save".equalsIgnoreCase(buttonType)) {
			PrimeFaces pm = PrimeFaces.current();
			if("Client Information".equalsIgnoreCase(getTabName())) {
				save();
				pm.executeScript("hideWizard()");
			}else if("Documents".equalsIgnoreCase(getTabName())) {
				saveDocs();
			}else if("Transactions".equalsIgnoreCase(getTabName())) {
				saveTrans();
			}
			
			
		}
	}
	
	public void clickItemTransact(ClientTransactions trans){
		setClientTransactions(trans);
		setTransDate(trans.getTransDate());
		setTransactTypeId(TransStatus.statusCode(trans.getTranstype()));
		setStatusId(TransStatus.statusCode(trans.getStatus()));
		setClientRateId(TransStatus.statusCode(trans.getClientrate()));
		setDescription(trans.getDescription());
		setTransamount(trans.getTransamount());
		setInterestrate(trans.getInterestrate());
		setInterestamount(trans.getInterestamount());
		setPaidamount(trans.getPaidamount());
		setPaidDate(trans.getPaidDate());
		setNotes(trans.getNotes());
		setTransDueDate(DateUtils.convertDateString(trans.getDueDate(),"yyyy-MM-dd"));
	}
	
	public void collectData(){
		System.out.println("contact: "+getContactNumber());
		System.out.println("name: "+getFullName());
	}
	
	public void  save(){
		boolean isOk = true;
		ClientProfile p = new ClientProfile();
		List<String> actions = new ArrayList<>();
		
		if(getFullName()==null || getFullName().isEmpty()) {
			isOk = false;
			Application.addMessage(1, "Error", "Please provide Customer Name");
		}
		
		if(getAddress()==null || getAddress().isEmpty()) {
			isOk = false;
			Application.addMessage(1, "Error", "Please provide Address");
		}
		
		if(getContactNumber()==null || getContactNumber().isEmpty()) {
			setContactNumber("0");
		}
		
		if(isOk) {
			
			actions.add("==========ClientProfile saving=========");
			actions.add("["+ processBy() +"] saving......");
			if(getClientProfile()!=null){
				p = getClientProfile();
				actions.add("setClientId : " + p.getClientId());
			}else{
				p.setRegisteredDate(getRegisteredDate());
				p.setIsActive(1);
				actions.add("setRegisteredDate : " + p.getRegisteredDate());
			}
			
			p.setContactNumber(StringUtils.removeSpecialChar(getContactNumber().trim()));
			actions.add("setContactNumber : " + p.getContactNumber());
			p.setFullName(StringUtils.removeSpecialChar(getFullName().trim()).toUpperCase());
			actions.add("setFullName : " + p.getFullName());
			p.setAddress(StringUtils.removeSpecialChar(getAddress()).toUpperCase().trim());
			actions.add("setAddress : " + p.getAddress());
			p.setAddedBy(processBy());
			actions.add("setAddedBy : " + p.getAddedBy());
			
			try{
				//p.setPicturePath(IMAGE_PATH + StringUtils.removeSpecialChar(getFullName()) + ".jpg");
				p.setPicturePath(getPhotoId());
			actions.add("setPicturePath : " + p.getPicturePath());
			}catch(Exception e){actions.add("setPicturePath error : " + e.getMessage());}
			
			if(p.getClientId()==null){
				writeImageToFile(getTmpFile());
				actions.add("Client id is null write tmpFile : ");
			}
			actions.add("prepare saving....");
			LogUserActions.logUserActions(actions);
			p.save();
			clearFields();
			init();
			Application.addMessage(1, "Success", "Successfully saved");
		}
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public void saveTrans(){
		
		ClientTransactions trans = new ClientTransactions();
		boolean isOk = true;
		List<String> actions = new ArrayList<>();
		
		if(getTransactTypeId()==null|| getTransactTypeId().isEmpty()){
			Application.addMessage(3, "Error", "Please provide type");
			isOk = false;
		}
		if(getStatusId()==null || getStatusId().isEmpty()){
			Application.addMessage(3, "Error", "Please provide status");
			isOk = false;
		}
		if(getDescription()==null || getDescription().isEmpty()){
			Application.addMessage(3, "Error", "Please provide description");
			isOk = false;
		}
		if(getTransamount()==null || getTransamount().isEmpty()){
			Application.addMessage(3, "Error", "Please provide amount");
			isOk = false;
		}
		if(getInterestrate()==null || getInterestrate().isEmpty()){
			Application.addMessage(3, "Error", "Please provide interest rate");
			isOk = false;
		}
		if(getInterestamount()==null || getInterestamount().isEmpty()){
			Application.addMessage(3, "Error", "Please provide interest rate amount");
			isOk = false;
		}
		
		
		
		actions.add("==========ClientProfile saveTransactions=========");
		actions.add("["+ processBy() +"] saving....");
		if(getClientTransactions()!=null){
			trans = getClientTransactions();
			
			if(getStatusId().equalsIgnoreCase(TransStatus.PAID.getCode()) || getStatusId().equalsIgnoreCase(TransStatus.RETURN.getCode()) ){
				
				if(getNotes()==null || getNotes().isEmpty()){
					Application.addMessage(3, "Error", "Please provide Notes");
					isOk = false;
				}
				
				if(getClientRateId()==null || getClientRateId().isEmpty()){
					Application.addMessage(3, "Error", "Please provide client rate");
					isOk = false;
				}
				
				trans.setPaidDate(DateUtils.getCurrentYYYYMMDD());
				trans.setPaidamount(getPaidamount());
				trans.setClientrate(getClientRateId());
				trans.setNotes(getNotes());
				actions.add("setTransId : " + trans.getTransId());
				actions.add("setPaidDate : " + trans.getPaidDate());
				actions.add("setPaidamount : " + trans.getPaidamount());
				actions.add("setClientrate : " + trans.getClientrate());
				actions.add("setNotes : " + trans.getNotes());
			}
			
		}else{
			trans.setTransDate(getTransDate());
			trans.setClientrate("0");
			actions.add("setTransDate : " + trans.getTransDate());
			actions.add("setClientrate : 0");
		}
		
		if(isOk){
		
		trans.setTranstype(getTransactTypeId());
		trans.setStatus(getStatusId());
		trans.setDescription(getDescription());
		try{trans.setTransamount(getTransamount().replace(",", ""));}catch(Exception e){}
		trans.setInterestrate(getInterestrate());
		trans.setInterestamount(getInterestamount());
		trans.setClientProfile(getClientProfile());
		trans.setAddedBy(processBy());
		
		trans.setDueDate(DateUtils.convertDate(getTransDueDate(), "yyyy-MM-dd"));
		
		actions.add("setTranstype : " + trans.getTranstype());
		actions.add("setStatus : " + trans.getStatus());
		actions.add("setDescription : " + trans.getDescription());
		actions.add("setTransamount : " + trans.getTransamount());
		actions.add("setInterestrate : " + trans.getInterestrate());
		actions.add("setInterestamount : " + trans.getInterestamount());
		actions.add("setClientProfile : " + trans.getClientProfile().getClientId());
		actions.add("setAddedBy : " + trans.getAddedBy());
		actions.add("setTranstype : " + trans.getDueDate());
		
		//use for cashin
		if(getStatusId().equalsIgnoreCase(TransStatus.PAID.getCode())){ 
			recordToCashIn(trans);
		}else if(getStatusId().equalsIgnoreCase(TransStatus.RETURN.getCode())){
			recordToExpenses(trans);
		}
		
		LogUserActions.logUserActions(actions);
		trans = ClientTransactions.save(trans);
		
		Application.addMessage(1, "Success", "Successfully saved.");
		String str = "MATT-AGRI BUY N SELL\n";
		str += "Sitio Lugan, Pob. Lake Sebu, So. Cot.\n";
		
		str += "\nName: "+ trans.getClientProfile().getFullName() +"\n";
		
		if(trans.getClientProfile()!=null){
			if(trans.getClientProfile().getContactNumber()!=null && !trans.getClientProfile().getContactNumber().isEmpty()){
				str += "Guarantor: "+ trans.getClientProfile().getContactNumber() +"\n\n";
			}
		}else{
			str += "\n";
		}
		
		if(TransStatus.PAID.getCode().equalsIgnoreCase(trans.getStatus())){ 
			str += "Transaction Date : "+ trans.getTransDate() +" \n";
			str += "Due Date : " + trans.getDueDate() +"\n";
			str += "Paid Date : "+ trans.getPaidDate() +" \n";
			str += "Description : " + trans.getDescription() +"\n";
			str += "Interest Rate : " + trans.getInterestrate() +"%\n";
			str += "Interest Amount : " + Currency.formatAmount(trans.getInterestamount()) +"\n";
			str += "Paid Amount : " + Currency.formatAmount(trans.getPaidamount()) +"\n";
			str += "Remarks : " + trans.getNotes() +"\n";
		}else{
			str += "Transaction Date : "+ trans.getTransDate() +" \n";
			str += "Description : " + trans.getDescription() +"\n";
			str += "Amount Php: " + Currency.formatAmount(trans.getTransamount()) +"\n";
			str += "Due Date : " + trans.getDueDate() +"\n";
		}
		
		str += "Cashier: " + processBy();
		str += "\n***THANK YOU***";
		str +=" \n \n \n \n \n \n";
		String rptNumber = trans.getTransId()+"";
		saveSaleToFileReceipt(str, rptNumber);
		cashdrawerOpen();
		printReceipt(rptNumber);
		cutPaper();
		clearTrans();
		transInit();
		}
		
	}
	
	public static void saveSaleToFileReceipt(String receiptInfo, String receiptNumber){
		
		ReadApplicationDetails rd = new ReadApplicationDetails();
		
		//check log directory
		String receiptLocation = rd.getClientTransReciept();
		String receiptFile = receiptLocation + receiptNumber + ".txt";
        File logdirectory = new File(receiptLocation);
        if(!logdirectory.isDirectory()){
        	logdirectory.mkdir();
        }
		
        //BufferedWriter writer = null;
        PrintWriter writer = null; 
        try{
			File file = new File(receiptFile);
			writer = new PrintWriter(new FileWriter(file));
			for(String str : receiptInfo.toString().split("\n")){
				writer.println(str);
			}
			
			writer.flush();
			writer.close();
			
		}catch(IOException e){
			
		}finally{
			
		}
        
	}
	
	public void printReceipt(String receiptNo){
		
		ReadApplicationDetails rd = new ReadApplicationDetails();
		
		//check log directory
		String receiptLocation = rd.getClientTransReciept();
		String receiptFile = receiptLocation + receiptNo + ".txt";
		
					
		//PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		//DocPrintJob job = service.createPrintJob();
		
		try{
			
		PrintService[] printServices;
		String printerName =  rd.getPrinterName(); //"EPSON TM-U220 Receipt";

		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(printerName, null));
		printServices = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);
			
			
		DocPrintJob job = printServices[0].createPrintJob();					

		FileInputStream textStream = new FileInputStream(receiptFile);
		
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(textStream, flavor, null);
		PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
		attrs.add(new Copies(1));
		job.print(doc, attrs);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void cutPaper() {
		ReadApplicationDetails rd = new ReadApplicationDetails();
		//byte[] open = {27, 112, 48, 55, 121};
		byte[] cutter = {29, 86,49};
		String printer = rd.getPrinterName();//"EPSON TM-U220 Receipt";
		PrintServiceAttributeSet printserviceattributeset = new HashPrintServiceAttributeSet();
		printserviceattributeset.add(new PrinterName(printer,null));
		PrintService[] printservice = PrintServiceLookup.lookupPrintServices(null, printserviceattributeset);
		if(printservice.length!=1){
		System.out.println("Printer not found");
		}
		try {
		PrintService pservice = printservice[0];
		DocPrintJob job = pservice.createPrintJob();
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(cutter,flavor,null);
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		
		job.print(doc, aset);
		}catch(ArrayIndexOutOfBoundsException aio) {
			System.out.println(aio.getMessage());
		} catch (PrintException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public static void cashdrawerOpen() {
		ReadApplicationDetails rd = new ReadApplicationDetails();
		byte[] open = {27, 112, 48, 55, 121};
		// byte[] cutter = {29, 86,49};
		String printer = rd.getPrinterName();//"EPSON TM-U220 Receipt";
		PrintServiceAttributeSet printserviceattributeset = new HashPrintServiceAttributeSet();
		printserviceattributeset.add(new PrinterName(printer,null));
		PrintService[] printservice = PrintServiceLookup.lookupPrintServices(null, printserviceattributeset);
		if(printservice.length!=1){
		System.out.println("Printer not found");
		}
		try {
		PrintService pservice = printservice[0];
		DocPrintJob job = pservice.createPrintJob();
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(open,flavor,null);
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		
		job.print(doc, aset);
		}catch(ArrayIndexOutOfBoundsException aio) {
			System.out.println(aio.getMessage());
		} catch (PrintException ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	private void recordToExpenses(ClientTransactions trans){
		List<String> actions = new ArrayList<>();
		Expenses ex = new Expenses();
		ex.setDescription("Return to the client for the deposit amount");
		ex.setAmountIn(trans.getPaidamount());
		ex.setAddedBy(processBy());
		ex.setClientTransactions(trans);
		ex.setDateIn(DateUtils.getCurrentYYYYMMDD());
		actions.add("Record to expenses");
		actions.add("["+processBy() +"]");
		actions.add("setDescription : " + ex.getDescription());
		actions.add("setAmountIn : " + ex.getAmountIn());
		actions.add("setAddedBy : " + ex.getAddedBy());
		actions.add("setClientTransactions : " + ex.getClientTransactions().getTransId());
		LogUserActions.logUserActions(actions);
		ex.save();
	}
	
	private void recordToCashIn(ClientTransactions trans){
		List<String> actions = new ArrayList<>();
		CashIn in = new CashIn();
		in.setDateIn(DateUtils.getCurrentYYYYMMDD());
		in.setDescription("Paid by " + getClientProfile().getFullName() + " from Client Profile.");
		in.setAmountIn(trans.getPaidamount());
		in.setCategory(2); // added cash
		in.setClientTransactions(trans);
		actions.add("Record to cashin");
		actions.add("["+processBy() +"]");
		actions.add("setDateIn : " + in.getDateIn());
		actions.add("setDescription : " + in.getDescription());
		actions.add("setAmountIn : " + in.getAmountIn());
		actions.add("setCategory : " + in.getCategory());
		actions.add("setClientTransactions : " + in.getClientTransactions().getTransId());
		LogUserActions.logUserActions(actions);
		in.save();
		
	}
	
	public void clearTrans(){
		setTransDate(null);
		setClientTransactions(null);
		setTransactTypeId(null);
		setStatusId(null);
		setDescription(null);
		setTransamount(null);
		setInterestrate(null);
		setInterestamount(null);
		setPaidamount(null);
		setPaidDate(null);
		setClientRateId(null);
		setNotes(null);
	}
	
	public void transInit(){
		transList = new ArrayList<>();
		String sql = "SELECT * FROM client_trans WHERE status=1 AND clientId=?";
		String[] params = new String[1];
		params[0] = getClientProfile().getClientId()+"";
		Double amnt = 0d;
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			try{
				amnt += Double.valueOf(t.getTransamount().replace(",", ""));
				t.setTransamount(Currency.formatAmount(t.getTransamount()));}catch(Exception e){}
			try{t.setTranstype(TransStatus.statusCodeToMeaning("transtype", t.getTranstype()));}catch(Exception e){}
			try{t.setStatus(TransStatus.statusCodeToMeaning("status", t.getStatus()));}catch(Exception e){}
			try{t.setClientrate(TransStatus.statusCodeToMeaning("clientrate", t.getClientrate()));}catch(Exception e){}
			try{t.setInterestamount(Currency.formatAmount(t.getInterestamount()));}catch(Exception e){}
			transList.add(t);
		}
		setTotalTrans(Currency.formatAmount(amnt+""));
		
		Double amnthis = 0d;
		Collections.reverse(transList);
		transhisList = new ArrayList<>();
		sql = "SELECT * FROM client_trans WHERE (status=2 OR status=3) AND clientId=?";
		params = new String[1];
		params[0] = getClientProfile().getClientId()+"";
		for(ClientTransactions t : ClientTransactions.retrieveClientTransacts(sql, params)){
			try{
				amnthis += Double.valueOf(t.getTransamount().replace(",", ""));
				t.setTransamount(Currency.formatAmount(t.getTransamount()));}catch(Exception e){}
			try{t.setTranstype(TransStatus.statusCodeToMeaning("transtype", t.getTranstype()));}catch(Exception e){}
			try{t.setStatus(TransStatus.statusCodeToMeaning("status", t.getStatus()));}catch(Exception e){}
			try{t.setClientrate(TransStatus.statusCodeToMeaning("clientrate", t.getClientrate()));}catch(Exception e){}
			try{t.setInterestamount(Currency.formatAmount(t.getInterestamount()));}catch(Exception e){}
			transhisList.add(t);
		}
		sql = "SELECT * FROM purchasingcorn WHERE clientId=?";
		params = new String[1];
		params[0] = getClientProfile().getClientId()+"";
		for(PurchasingCorn corns : PurchasingCorn.retrievePurchasingCorn(sql, params)){
			ClientTransactions t = new ClientTransactions();
			t.setTransDate(corns.getDateIn());
			t.setTranstype("SELLING CORN");
			t.setStatus("PAID");
			t.setTransamount(Currency.formatAmount(corns.getTotalAmount()+""));
			t.setDescription(PurchasingCorn.conditionName(corns.getConditions()) + " kilo= " + corns.getKilo() + " discount= " + corns.getDiscount());
			t.setInterestrate("0");
			t.setInterestamount(Currency.formatAmount("0"));
			t.setPaidDate(corns.getDateIn());
			t.setPaidamount(Currency.formatAmount(corns.getTotalAmount()+""));
			t.setClientrate("N/A");
			t.setNotes("Customer's selling corn");
			transhisList.add(t);
		}
		
		setTotalTranshis(Currency.formatAmount(amnthis+""));
		Collections.reverse(transhisList);
	}
	
	public String calculateInterestAmnt(){
		Double amnt = 0d,intamnt=0d, tmp = 0d;
		double paidAmnt = 0d;
		try{
			if(getTransamount()==null) setTransamount("0");
			if(getTransamount().isEmpty()) setTransamount("0");
			amnt = Double.valueOf(getTransamount().replace(",", ""));}catch(NumberFormatException e){}
		try{
			if(getInterestrate()==null) setInterestrate("0");
			if(getInterestrate().isEmpty()) setInterestrate("0");
			intamnt = Double.valueOf(getInterestrate().replace(",", ""));}catch(NumberFormatException e){}
		intamnt = intamnt/100;
		tmp = amnt * intamnt;
		paidAmnt =amnt+ tmp;
		setInterestamount(Currency.formatAmount(tmp+""));
		setPaidamount(Currency.formatAmount(paidAmnt+""));
		return "calc";
	}
	
	public String calculatePaidAmnt(){
		System.out.println("status id : " + getStatusId());
		System.out.println("Trans id : " + getTransactTypeId());
		if(getStatusId().equalsIgnoreCase(TransStatus.PAID.getCode())){
			Double amnt = 0d,intamnt=0d, tmp = 0d;
			try{amnt = Double.valueOf(getTransamount().replace(",", ""));}catch(NumberFormatException e){}
			try{intamnt = Double.valueOf(getInterestamount().replace(",", ""));}catch(NumberFormatException e){}
			tmp = amnt + intamnt;
			setPaidamount(Currency.formatAmount(tmp+""));
			setPaidDate(DateUtils.getCurrentYYYYMMDD());
			setCheckIsPaid(false);
		}else if(getStatusId().equalsIgnoreCase(TransStatus.RETURN.getCode())){
			
			if(getTransactTypeId().equalsIgnoreCase(TransStatus.DEPOSIT.getCode())){
				setTransactTypeId(TransStatus.DEPOSIT.getCode());
				setStatusId(TransStatus.RETURN.getCode());
				setClientRateId(TransStatus.ON_TIME_PAYER.getCode());
				setInterestrate("0");
				setInterestamount("0");
				setPaidDate(DateUtils.getCurrentYYYYMMDD());
				setCheckIsPaid(false);
				setDescription("Return amount to client");
				setNotes("no penalty");
				setPaidamount(getTransamount());
			}else{
				setPaidamount(null);
				setPaidDate(null);
				setClientRateId(null);
				setNotes(null);
				setCheckIsPaid(true);
			}
		}else{
			setPaidamount(null);
			setPaidDate(null);
			setClientRateId(null);
			setNotes(null);
			setCheckIsPaid(true);
		}
		
		return "status";
	}
	
	public List<ClientTransactions> getTransList() {
		return transList;
	}

	public void setTransList(List<ClientTransactions> transList) {
		this.transList = transList;
	}

	public ClientTransactions getClientTransactions() {
		return clientTransactions;
	}

	public void setClientTransactions(ClientTransactions clientTransactions) {
		this.clientTransactions = clientTransactions;
	}

	public String getStatusId() {
		if(statusId==null){
			statusId = "1";
		}
		return statusId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public List getStatusList() {
		
		statusList=transStatus("status","0");
		
		return statusList;
	}

	public void setStatusList(List statusList) {
		this.statusList = statusList;
	}

	public String getTransactTypeId() {
		return transactTypeId;
	}

	public void setTransactTypeId(String transactTypeId) {
		this.transactTypeId = transactTypeId;
	}

	public List getTransactTypeList() {
		
		transactTypeList=transStatus("transtype","0");
		
		return transactTypeList;
	}

	public void setTransactTypeList(List transactTypeList) {
		this.transactTypeList = transactTypeList;
	}

	public String getClientRateId() {
		return clientRateId;
	}

	public void setClientRateId(String clientRateId) {
		this.clientRateId = clientRateId;
	}

	public List getClientRateList() {
		
		clientRateList=transStatus("clientrate","0");
		
		return clientRateList;
	}

	public void setClientRateList(List clientRateList) {
		this.clientRateList = clientRateList;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTransDate() {
		
		if(transDate==null){
			transDate = DateUtils.getCurrentYYYYMMDD();
		}
		
		return transDate;
	}

	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}

	public String getTransamount() {
		return transamount;
	}

	public void setTransamount(String transamount) {
		this.transamount = transamount;
	}

	public String getInterestrate() {
		return interestrate;
	}

	public void setInterestrate(String interestrate) {
		this.interestrate = interestrate;
	}

	public String getInterestamount() {
		return interestamount;
	}

	public void setInterestamount(String interestamount) {
		this.interestamount = interestamount;
	}

	public String getPaidamount() {
		return paidamount;
	}

	public void setPaidamount(String paidamount) {
		this.paidamount = paidamount;
	}

	public String getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(String paidDate) {
		this.paidDate = paidDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public ClientProfile getProfile() {
		return profile;
	}

	public void setProfile(ClientProfile profile) {
		this.profile = profile;
	}

	public Employees getEmployee() {
		return employee;
	}

	public void setEmployee(Employees employee) {
		this.employee = employee;
	}

	public DefaultStreamedContent getContent() {
		return content;
	}

	public void setContent(DefaultStreamedContent content) {
		this.content = content;
	}

	public void exit(){
		clearFields();
		clearTrans();
		clearDocs();
		//init();
	}
	
	public void clearFields(){
		List<String> actions = new ArrayList<>();
		//setUsersProfileImage(null);
		shots = new ArrayList<>();
		setPhotoId("tmp.jpg");
		actions.add("Clearing fields...");
		setRegisteredDate(null);
		actions.add("setRegisteredDate");
		setContactNumber(null);
		actions.add("setContactNumber");
		setFullName(null);
		actions.add("setFullName");
		setAddress(null);
		actions.add("setAddress");
		setClientProfile(null);
		actions.add("setClientProfile");
		actions.add("done clearing fields...");
		LogUserActions.logUserActions(actions);
	}
	
	
	public void print(){
		
	}
	
	public void loadSearch(){
		
		/*String search = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("paramsearch");
		System.out.println("This is my search : " + search);
		setSearchString(search);*/
		
		if(getTotalCollectible()==null) setTotalCollectible("0.00");
		if(getTotalCollectible().isEmpty()) setTotalCollectible("0.00");
		String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 ";
		String[] params = new String[0];
		clientList = new ArrayList<>();
		if(getSearchString()!=null && !getSearchString().isEmpty()){
			sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND fullName like '%"+ getSearchString().replace("--", "") +"%'";
			sql += " OR contactNumber like '%"+ getSearchString().replace("--", "") +"%'";
		}else{
			sql += " ORDER BY clientId DESC limit 10";
		}
		
		Double amnt = 0d;
		String collamnt = "0.00";
		List<ClientProfile> pList = ClientProfile.retrieveClientProfile(sql, params); 
		
		if(pList.size()==1){
			collamnt = collectibleAmnt(pList.get(0));
			collamnt = collamnt.replace("₱", "");
			amnt += Double.valueOf(collamnt.replace(",", ""));
			pList.get(0).setAmntCollectible(collamnt);
			clientList.add(pList.get(0));
			clickItem(pList.get(0));
			
			PrimeFaces pm = PrimeFaces.current();
			setTabName("Client Information");
			pm.executeScript("addNew();PF('tabSelection').select(0);");
		}else{
			exit();
			for(ClientProfile p : pList){
				collamnt = collectibleAmnt(p);
				collamnt = collamnt.replace("₱", "");
				collamnt = collamnt.replace("?", "");
				amnt += Double.valueOf(collamnt.replace(",", ""));
				p.setAmntCollectible(collamnt);
				clientList.add(p);
			}
			//Collections.reverse(clientList);
		}
		
		setTotalCollectible(Currency.formatAmount(amnt+""));
		
	}
	
	private String collectibleAmnt(ClientProfile clientProfile){
		String amnt = "0.00";
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
	
	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getRegisteredDate() {
		if(registeredDate==null){
			registeredDate = DateUtils.getCurrentYYYYMMDD();
		}
		return registeredDate;
	}

	public void setRegisteredDate(String registeredDate) {
		this.registeredDate = registeredDate;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getAddedBy() {
		return addedBy;
	}

	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	
	
	private StreamedContent profImage;
	public StreamedContent getProfImage() {
		return profImage;
	}

	public void setProfImage(StreamedContent profImage) {
		this.profImage = profImage;
	}
	
	private StreamedContent getDefaultImg(){
		StreamedContent image = null;
		try{
		FacesContext context = FacesContext.getCurrentInstance();		
		//if(context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE){
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedImage img = ImageIO.read(context.getExternalContext()
				.getResourceAsStream("/resources/img/default.png"));
		int w = img.getWidth(null);
		int h = img.getHeight(null);

		// image is scaled two times at run time
		int scale = 2;

		BufferedImage bi = new BufferedImage(w * scale, h * scale,
				BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();

		g.drawImage(img, 10, 10, w * scale, h * scale, null);

		ImageIO.write(bi, "png", bos);
		image = DefaultStreamedContent.builder()
	    		.contentType("image/png")
	    		.name("default.png")
	    		.stream(()-> this.getClass().getResourceAsStream(IMAGE_PATH + "default.png"))
	    		.build();
				//new DefaultStreamedContent(new ByteArrayInputStream(
				//bos.toByteArray()), "image/png");
		
		}catch(IOException e){}
		
		return image;
	}
	
	//private StreamedContent content;
	/*public void setUsersProfileImage(DefaultStreamedContent content){
		this.content = content;
	}*/
	
	/*public StreamedContent getUsersProfileImage() throws IOException{
		
		System.out.println("image start");
		System.out.println("Fullname: " + getFullName());
		if(getFullName()==null){
			//return new DefaultStreamedContent();
			return getDefaultImg(); 
		}else{
				
			 	File file = new File(IMAGE_PATH,  getFullName().toUpperCase()+".jpg");
			 	
			 	if(file.exists()){
			 		setTmpFileLoad(file);//incase of changing name this file will be use
			 		return loadPicture(file);
			 	}else{
			 		
			 		if(getClientProfile()!=null){
			 			if(getClientProfile().getPicturePath()!=null){
			 				file = new File(getClientProfile().getPicturePath());
			 				if(file.exists()){
			 					return loadPicture(file);
			 				}else{
			 					return getDefaultImg();
			 				}
			 				
			 			}
			 		}else{
			 			return getDefaultImg();
			 		}	
			 	}
				
	            
		}	
		
		
		
		return getDefaultImg();
		
	}*/
	private StreamedContent loadPicture(File file){
		StreamedContent image = null;
		
		try{
		System.out.println("Image: " + file.getPath() + " " + file.getName());
        BufferedInputStream in =  new BufferedInputStream(new FileInputStream(file),DEFAULT_BUFFER_SIZE); //new BufferedInputStream(ImageBean.class.getClassLoader().getResourceAsStream("test/test.png"));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        int val = -1;
          
        try
        {
        	
            while((val = in.read()) != -1){
                out.write(val);
                //System.out.println("writing.... " + val);
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        byte[] bytes = out.toByteArray();
        //System.out.println("Bytes -> " + bytes.length);
         return new DefaultStreamedContent(); //new DefaultStreamedContent(new ByteArrayInputStream(bytes), "image/jpeg", getFullName().toUpperCase()+".jpg");
		
		}catch(Exception e){}
         
		return image;
	}
	
	
	private BufferedImage tmpFile;
	
	
	public BufferedImage getTmpFile() {
		return tmpFile;
	}

	public void setTmpFile(BufferedImage tmpFile) {
		this.tmpFile = tmpFile;
	}

	/*private void writeImageToFile(BufferedImage image){
		try{
		
			File fileImg = new File(IMAGE_PATH +  "tmp.jpg");
			if(!getFullName().isEmpty()){
				fileImg = new File(IMAGE_PATH +  getFullName().toUpperCase() +".jpg");
			}
			
			if(image==null){// backup writing if the first attempt is not successsfull
				fileImg = new File(IMAGE_PATH +  "tmp.jpg");
				 image = ImageIO.read(fileImg);
				 fileImg = new File(IMAGE_PATH +  getFullName().toUpperCase() +".jpg");
			}
		 
		System.out.println("writing images.....");
		ImageIO.write(image, "jpg", fileImg);
		}catch(IOException e){}
	}*/
	
	private void writeImageToFile(BufferedImage image){
		try{
			photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
			shots.add(photoId);
			File fileImg = new File(IMAGE_PATH + photoId + ".jpg");
			
			if(image==null){
				fileImg = new File(IMAGE_PATH + "tmp.jpg");
				 image = ImageIO.read(fileImg);
			}
			
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
			String contextImageLoc = "resources" + File.separator + "images" + File.separator;
			String pathToSave = externalContext.getRealPath("") + contextImageLoc;
           // File file = new File(driveImage);
            try{
            	ImageIO.write(image, "jpg", fileImg);
    			Files.copy(fileImg.toPath(), (new File(pathToSave + fileImg.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			System.out.println("writing images....." + pathToSave);
    			}catch(IOException e){}
		
		}catch(IOException e){}
	}
	
	/*public void fileUploadListener(FileUploadEvent event) {

        try {
            BufferedImage image = ImageIO
                    .read(event.getFile().getInputstream());
            if (image != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                //user.setProfileJpegImage(outputStream.toByteArray());
                byte[] bytes = outputStream.toByteArray();
	            System.out.println("Bytes -> " + bytes.length);
	            content = new DefaultStreamedContent(new ByteArrayInputStream(bytes));
	            setTmpFile(image);
	            writeImageToFile(image);
            } else {
                throw new IOException("FAILED TO CONVERT PICTURE");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }*/
	
	public void fileUploadListener(FileUploadEvent event) {

        try {
            BufferedImage image = ImageIO
                    .read(event.getFile().getInputStream());
            if (image != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                //user.setProfileJpegImage(outputStream.toByteArray());
                byte[] bytes = outputStream.toByteArray();
	            System.out.println("Bytes -> " + bytes.length);
	            //content = new DefaultStreamedContent(new ByteArrayInputStream(bytes));
	            //setTmpFile(image);
	            writeImageToFile(image);
            } else {
                throw new IOException("FAILED TO CONVERT PICTURE");
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
	
	public void docUploadListener(FileUploadEvent event) {
		System.out.println("docUploadListener....");
        try {
        	InputStream input = event.getFile().getInputStream();
        if(input!=null){
        	//save the stream in setter for saving purpuses later
        	setDocFileExt(FilenameUtils.getExtension(event.getFile().getFileName()));
        	//System.out.println(event.getFile().getFileName() + " File extension : " + getDocFileExt());
        	setDocumentPath(event.getFile().getFileName());
        	setDocImage(input);
        } else {
            throw new IOException("FAILED TO CONVERT DOCUMENT");
        }
        	
            /*BufferedImage image = ImageIO
                    .read(event.getFile().getInputstream());
            if (image != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(image, "jpg", outputStream);
                //user.setProfileJpegImage(outputStream.toByteArray());
                byte[] bytes = outputStream.toByteArray();
	            System.out.println("Bytes -> " + bytes.length);
	            //content = new DefaultStreamedContent(new ByteArrayInputStream(bytes));
	            //setTmpFile(image);
	            //writeImageToFile(image);
	            setDocumentPath(event.getFile().getFileName());
	            setDocImage(image);
            } else {
                throw new IOException("FAILED TO CONVERT DOCUMENT");
            }*/
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
	
	public String saveDocs(){
		if(getDocumentName()==null) return "null";
		if(getDocumentName().isEmpty()) return "null";
		if(getDocumentDescription()==null) return "null";
		if(getDocumentDescription().isEmpty()) return "null";
		if(getDocumentPath()==null) return "null";
		if(getDocumentPath().isEmpty()) return "null";
		
		ClientDocs doc = new ClientDocs();
		List<String> actions = new ArrayList<>();
		actions.add("==========ClientProfile Save documents=========");
		actions.add("["+ processBy() +"] saving....");
		if(getClientDocs()!=null){
			doc = getClientDocs();
			File f = new File(getDocumentPath());
			String ext = FilenameUtils.getExtension(f.getName());
			if(f.exists()){
				String name = getDocumentName() + "." + ext;
				if(!name.equalsIgnoreCase(f.getName())){
					File fnew = new File(DOC_PATH + name);
					f.renameTo(fnew);
					doc.setDocumentPath(DOC_PATH + getDocumentName() + "." + ext);
				}
			}
			actions.add("setDocumentId : " + doc.getDocumentId());
		}else{
			doc.setDocumentDate(getDocumentDate());
			doc.setClientProfile(getClientProfile());
			writeDocToFile(getDocImage(), getDocumentName()+ "." + getDocFileExt());
			doc.setDocumentPath(DOC_PATH + getDocumentName() + "." + getDocFileExt());
			actions.add("setDocumentDate : " + doc.getDocumentDate());
			actions.add("setClientProfile : " + doc.getClientProfile().getClientId());
			actions.add("setDocumentPath : " + doc.getDocumentPath());
		}
		doc.setDocumentName(getDocumentName());
		doc.setDescription(getDocumentDescription());
		doc.setAddedBy(processBy());
		
		actions.add("setDocumentName : " + doc.getDocumentName());
		actions.add("setDescription : " + doc.getDescription());
		actions.add("setAddedBy : " + doc.getAddedBy());
		LogUserActions.logUserActions(actions);
		doc.save();
		
		docinit();
		clearDocs();
		return "save";
	}
	
	public void deleteRowDocs(ClientDocs doc){
		
			String sql = "DELETE FROM client_documents WHERE docId=?";
			String[] params = new String[1];
			params[0] = doc.getDocumentId()+"";
			ClientDocs.delete(sql, params);
			List<String> actions = new ArrayList<>();
			actions.add("["+ processBy() +"]");
			actions.add("deleting id " + params[0] +" in client_trans");
			
			File file = new File(doc.getDocumentPath());
			if(file.exists()){
				actions.add("deleting document " + file.getName());
				file.delete();
			}
			actions.add("Successfully deleted...");
			docinit();
			clearDocs();
			Application.addMessage(1, "Success", "Successfully deleted");
	}
	
	public void clickItemDoc(ClientDocs doc){
		setClientDocs(doc);
		setDocumentName(doc.getDocumentName());
		setDocumentDescription(doc.getDescription());
		setDocumentPath(doc.getDocumentPath());
	}
	
	public void docinit(){
		docList = new ArrayList<>();
		String sql = "SELECT * FROM client_documents WHERE clientId=?";
		String[] params = new String[1];
		params[0] = getClientProfile().getClientId()+"";
		docList = ClientDocs.retrieveClientDocs(sql, params);
		Collections.reverse(docList);
	}
	
	public void openDoc(){
		try{
		File file = new File(getDocumentPath());	
		Desktop desktop = Desktop.getDesktop();
		desktop.open(file);
		/*Process proc = Runtime.getRuntime().exec(getDocumentPath());
		proc.waitFor();
		System.out.println("check if alive : "+proc.isAlive());*/
		
		}catch(Exception e){
			System.out.println("Error running " + getDocumentPath());
			e.printStackTrace();
		}
	}
	
	public void clearDocs(){
		setDocumentDate(null);
		setClientDocs(null);
		setDocumentName(null);
		setDocumentDescription(null);
		setDocumentPath(null);
	}
	
	private void writeDocToFile(InputStream image, String filename){
		try{
			
		System.out.println("writing... writeDocToFile : " + filename);
		File fileImg = new File(DOC_PATH +  filename);
		System.out.println("writing pdf images.....");
		//ImageIO.write(image, "jpg", fileImg);
		Path file = fileImg.toPath();
		Files.copy(image, file, StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException e){}
	}
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private DefaultStreamedContent content;
	public StreamedContent getClientPicture(){
		 if(content == null)
	        {
			 
			 try{
			 
			 	File file = new File(IMAGE_PATH,  "corn.jpg");
			 	System.out.println("Image: " + file.getPath() + " " + file.getName());
	            /* use your database call here */
	            BufferedInputStream in =  new BufferedInputStream(new FileInputStream(file),DEFAULT_BUFFER_SIZE); //new BufferedInputStream(ImageBean.class.getClassLoader().getResourceAsStream("test/test.png"));
	            ByteArrayOutputStream out = new ByteArrayOutputStream();
	            
	            int val = -1;
	            /* this is a simple test method to double check values from the stream */
	            try
	            {
	            	
	                while((val = in.read()) != -1){
	                    out.write(val);
	                    //System.out.println("writing.... " + val);
	                }
	            }
	            catch(IOException e)
	            {
	                e.printStackTrace();
	            }

	            byte[] bytes = out.toByteArray();
	            System.out.println("Bytes -> " + bytes.length);
	            content = DefaultStreamedContent.builder()
	    	    		.contentType("image/jpeg")
	    	    		.name("corn.jpg")
	    	    		.stream(()-> this.getClass().getResourceAsStream(IMAGE_PATH + "corn.jpg"))
	    	    		.build();

	            		//new DefaultStreamedContent(new ByteArrayInputStream(bytes), "image/jpeg", "corn.jpg");
	            return content;
			 }catch(Exception e){e.printStackTrace();}
	            
	        }

	        return content;
	}

	
	public void transactSave(boolean isConfirm){
		if(isConfirm){
			
		}
	}
	
	/**
	 * Deleting old picture/s for the selected customer
	 */
	private void deletingImages(){
		ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
		
       // String driveImage =  IMAGE_PATH + photoId + ".jpg";
        //String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator + "photocam" + File.separator;
        String deleteImg = externalContext.getRealPath("") + "resources" + File.separator + "images" + File.separator;// + photoId + ".jpg";
        //removing if existing
        try{
        
       getShots().remove(getPhotoId());	
       for(String name : shots){ 	
        	if(!getPhotoId().equalsIgnoreCase(name)){
	        File img = new File(IMAGE_PATH + name + ".jpg");
	        img.delete();
	       
	        img = new File(deleteImg + name + ".jpg");
	        img.delete();
        	}
       	}
        }catch(Exception e){}
	}
	
	public void deleteTmpImages(){
		
		if(getShots()!=null && getShots().size()>0){
			deletingImages();
		}
	}
	
	private String getRandomImageName() {
        int i = (int) (Math.random() * 10000000);
         
        return String.valueOf(i);
    }
	
	public void oncapture(CaptureEvent captureEvent) {
        //photoId = getRandomImageName();
		
		photoId = getRandomImageName() + DateUtils.getCurrentDateMMDDYYYYTIMEPlain();
		shots.add(photoId);
    	//filename ="cam";
        System.out.println("Set picture name " + photoId);
        byte[] data = captureEvent.getData();
 
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        
        String driveImage =  IMAGE_PATH + photoId + ".jpg";
        String contextImageLoc = File.separator + "resources" + File.separator + "images" + File.separator;
        try{
        
        }catch(Exception e){}
        
        FileImageOutputStream imageOutput;
        try {
            imageOutput = new FileImageOutputStream(new File(driveImage));
            imageOutput.write(data, 0, data.length);
            imageOutput.close();    
            
            
            String pathToSave = externalContext.getRealPath("") + contextImageLoc;
            File file = new File(driveImage);
            try{
    			Files.copy(file.toPath(), (new File(pathToSave + file.getName())).toPath(),
    			        StandardCopyOption.REPLACE_EXISTING);
    			}catch(IOException e){}
            //capturedImagePathName = contextImageLoc + photoId + ".jpg";
           // System.out.println("capture path " + capturedImagePathName.replace("\\", "/"));
           // setCapturedImagePathName(capturedImagePathName.replace("\\", "/"));
        }
        catch(IOException e) {
            throw new FacesException("Error in writing captured image.", e);
        }
    }
	
	public void selectedPhoto(String photoId){
		setPhotoId(photoId);
	}
	
	public List<String> getShots() {
		return shots;
	}

	public void setShots(List<String> shots) {
		this.shots = shots;
	}
	
	public void transactPrint(){
		
	}

	public File getTmpFileLoad() {
		return tmpFileLoad;
	}

	public void setTmpFileLoad(File tmpFileLoad) {
		this.tmpFileLoad = tmpFileLoad;
	}

	public ClientDocs getClientDocs() {
		return clientDocs;
	}

	public String getDocumentDate() {
		if(documentDate==null) {documentDate=DateUtils.getCurrentYYYYMMDD();}
		return documentDate;
	}

	public String getDocumentName() {
		return documentName;
	}

	public String getDocumentPath() {
		return documentPath;
	}

	public String getDocumentDescription() {
		return documentDescription;
	}

	public InputStream getDocImage() {
		return docImage;
	}

	public BufferedImage getDocImageTemp() {
		return docImageTemp;
	}

	public List<ClientDocs> getDocList() {
		return docList;
	}

	public void setClientDocs(ClientDocs clientDocs) {
		this.clientDocs = clientDocs;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public void setDocumentPath(String documentPath) {
		this.documentPath = documentPath;
	}

	public void setDocumentDescription(String documentDescription) {
		this.documentDescription = documentDescription;
	}

	public void setDocImage(InputStream docImage) {
		this.docImage = docImage;
	}

	public void setDocImageTemp(BufferedImage docImageTemp) {
		this.docImageTemp = docImageTemp;
	}

	public void setDocList(List<ClientDocs> docList) {
		this.docList = docList;
	}

	public String getTotalTrans() {
		return totalTrans;
	}

	public void setTotalTrans(String totalTrans) {
		this.totalTrans = totalTrans;
	}

	public String getTotalTranshis() {
		return totalTranshis;
	}

	public void setTotalTranshis(String totalTranshis) {
		this.totalTranshis = totalTranshis;
	}

	public String getDocFileExt() {
		return docFileExt;
	}

	public void setDocFileExt(String docFileExt) {
		this.docFileExt = docFileExt;
	}

	public String getTotalCollectible() {
		return totalCollectible;
	}

	public void setTotalCollectible(String totalCollectible) {
		this.totalCollectible = totalCollectible;
	}

	public String getPhotoId() {
		return photoId;
	}

	public void setPhotoId(String photoId) {
		this.photoId = photoId;
	}

	public Date getTransDueDate() {
		if(transDueDate==null){
			transDueDate = DateUtils.getDateToday();
		}
		return transDueDate;
	}

	public void setTransDueDate(Date transDueDate) {
		this.transDueDate = transDueDate;
	}

	public String getSearchClientParam() {
		return searchClientParam;
	}

	public void setSearchClientParam(String searchClientParam) {
		this.searchClientParam = searchClientParam;
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

	public List<ClientTransactions> getClientTrans() {
		return clientTrans;
	}

	public void setClientTrans(List<ClientTransactions> clientTrans) {
		this.clientTrans = clientTrans;
	}

	public String getTotalNowTransactions() {
		return totalNowTransactions;
	}

	public void setTotalNowTransactions(String totalNowTransactions) {
		this.totalNowTransactions = totalNowTransactions;
	}

	public boolean isPaidOnly() {
		return paidOnly;
	}

	public void setPaidOnly(boolean paidOnly) {
		this.paidOnly = paidOnly;
	}

	public String getTransactTypeId2() {
		return transactTypeId2;
	}

	public void setTransactTypeId2(String transactTypeId2) {
		this.transactTypeId2 = transactTypeId2;
	}

	public List getTransactTypeList2() {
		
		transactTypeList2=transStatus("transtype","0");
		
		return transactTypeList2;
	}

	public void setTransactTypeList2(List transactTypeList2) {
		this.transactTypeList2 = transactTypeList2;
	}

	public boolean isSummaryOnly() {
		return summaryOnly;
	}

	public void setSummaryOnly(boolean summaryOnly) {
		this.summaryOnly = summaryOnly;
	}

	public String getTabName() {
		if(tabName==null) {
			tabName = "Client Information";
		}
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	
}










