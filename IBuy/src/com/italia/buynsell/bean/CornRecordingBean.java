package com.italia.buynsell.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.italia.buynsell.controller.ClientProfile;
import com.italia.buynsell.controller.CornConditions;
import com.italia.buynsell.controller.PurchasingCorn;
import com.italia.buynsell.controller.ReadApplicationDetails;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.reports.ReportCompiler;
import com.italia.buynsell.utils.Application;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.LogUserActions;
import com.italia.buynsell.utils.Numbers;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

//@ManagedBean(name="cornBean", eager=true)
//@SessionScoped
@Named("cornBean")
@SessionScoped
public class CornRecordingBean implements Serializable{
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 6576987565475471L;
	private String condition;
	private String condSearch;
	private List conditions = new ArrayList<>();
	private List conditionsSearch = new ArrayList<>();
	private String conditionLabel;
	private String date;
	 
	private String searchDate;
	private List filteredSearch = new ArrayList<>();
	private String filtered;
	private List cornColors = new ArrayList<>();
	private String cornColor;
	private List cornColorsSearch = new ArrayList<>();
	private String cornColorSearch;
	
	private String customerName;
	
	private double discount = 0.0;
	private double kilo = 0.0;
	private BigDecimal driver = new BigDecimal("0.00");
	private BigDecimal totalPrice = new BigDecimal("0.00");
	private BigDecimal amount = new BigDecimal("0.00");
	
	private List<PurchasingCorn> purchCorn = new ArrayList<PurchasingCorn>();
	private Map<String, Map> t1 = Collections.synchronizedMap(new HashMap<String, Map>());
	private Map<String, Map> t2 = Collections.synchronizedMap(new HashMap<String, Map>());
	private Map<String, PurchasingCorn> t3 = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
	
	//Map<String, Map<String, Map<String, PurchasingCorn>>> dataSearch = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,PurchasingCorn>>>());
	//Map<String, Map<String, Map<String, PurchasingCorn>>> dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,PurchasingCorn>>());
	//Map<String, PurchasingCorn> dataCondtions = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
	
	Map<String, Map<String, Map<String, Map<Long, PurchasingCorn>>>> dataSearch = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
	Map<String, Map<String, Map<Long, PurchasingCorn>>> dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
	Map<String, Map<Long, PurchasingCorn>> dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
	Map<Long, PurchasingCorn> dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
	Map<String, Map<String, Map<String, Map<Long, PurchasingCorn>>>> datahold = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
	Map<String, Map<String, Map<String, Map<Long, PurchasingCorn>>>> datatemp = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
	
	private Map<Long, PurchasingCorn> allCorn = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
	
	private Map<Long, PurchasingCorn> summaryTotal = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
	
	private String cornPriceWhite ="0.00";
	private String cornPriceYellow ="0.00";
	private String totalWYCorn="0.00";
	
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	private List<PurchasingCorn> reports = new ArrayList<>();
	private final String sep = File.separator;
	private final String REPORT_PATH = "C:" + sep + "BuyNSell" + sep + "reports" + sep + "generated" + sep;
	private final String REPORT_NAME = "CornDetails";		
	private Long id;
	
	private String deductedData;
	private double deductedTotal;
	private double finalAmount;
	
	private String kiloDetails;
	
	private Date calendarFrom;
	private Date calendarTo;
	private StreamedContent tempPdfFile;
	
	private ClientProfile clientName;
	
	public String getKiloDetails() {
		return kiloDetails;
	}

	public void setKiloDetails(String kiloDetails) {
		this.kiloDetails = kiloDetails;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String deleteRow(PurchasingCorn p){
		
		if(DateUtils.getCurrentYYYYMMDD().equalsIgnoreCase(p.getDateIn())){
		try{allCorn.remove(p.getChasedId());}catch(Exception e){}
		
		Connection conn = null;
		PreparedStatement prep = null;
		String sql = "";
		List<String> actions = new ArrayList<>();
		actions.add("deleting data in purchasingcorn");
		try{
		sql="DELETE FROM purchasingcorn WHERE chasedid=?";
		actions.add(sql);
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);
		prep.setLong(1, p.getChasedId());
		actions.add("ChasedId : " + p.getChasedId());
		actions.add("Execute update...");
		prep.executeUpdate();
		actions.add("closing...");
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			actions.add("Error updating : " + e.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
		String col = getCornColorSearch()!=null ? (getCornColorSearch().equalsIgnoreCase("1")? "YELLOW" : (getCornColorSearch().equalsIgnoreCase("2")? "WHITE" : getCornColorSearch().equalsIgnoreCase("0")? "ALL" : "ALL") ) : "ALL"; 
		String date = getSearchDate()!=null? getSearchDate() : getCurrentDate();
		String cond = getCondSearch()==null? "ALL" : getCondSearch().isEmpty()? "ALL" : con(getCondSearch());
		String filltered = getFiltered()!=null? (getFiltered().equalsIgnoreCase("1")? "DETAILED" :  (getFiltered().equalsIgnoreCase("2")? "SUMMARY" : "ALL") ) : "ALL";
		List data = new ArrayList<>();
		
		data.add(date);
		data.add(col);
		data.add(cond);
		data.add(filltered);
		//retrievePurchase(data, "search");
		cornsAll("search");
		Application.addMessage(1, "Success", "Successfully deleted");
		}
		return "delete";
	}
	
	@Deprecated
	public String deleteRow(PurchasingCorn p, boolean isConfirm){
		
		if(isConfirm && (DateUtils.getCurrentYYYYMMDD().equalsIgnoreCase(p.getDateIn()))){
		try{allCorn.remove(p.getChasedId());}catch(Exception e){}
		
		Connection conn = null;
		PreparedStatement prep = null;
		String sql = "";
		List<String> actions = new ArrayList<>();
		actions.add("deleting data in purchasingcorn");
		try{
		sql="DELETE FROM purchasingcorn WHERE chasedid=?";
		actions.add(sql);
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);
		prep.setLong(1, p.getChasedId());
		actions.add("ChasedId : " + p.getChasedId());
		actions.add("Execute update...");
		prep.executeUpdate();
		actions.add("closing...");
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			actions.add("Error updating : " + e.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
		String col = getCornColorSearch()!=null ? (getCornColorSearch().equalsIgnoreCase("1")? "YELLOW" : (getCornColorSearch().equalsIgnoreCase("2")? "WHITE" : getCornColorSearch().equalsIgnoreCase("0")? "ALL" : "ALL") ) : "ALL"; 
		String date = getSearchDate()!=null? getSearchDate() : getCurrentDate();
		String cond = getCondSearch()==null? "ALL" : getCondSearch().isEmpty()? "ALL" : con(getCondSearch());
		String filltered = getFiltered()!=null? (getFiltered().equalsIgnoreCase("1")? "DETAILED" :  (getFiltered().equalsIgnoreCase("2")? "SUMMARY" : "ALL") ) : "ALL";
		List data = new ArrayList<>();
		
		data.add(date);
		data.add(col);
		data.add(cond);
		data.add(filltered);
		//retrievePurchase(data, "search");
		cornsAll("search");
		}
		return "delete";
	}
	
	@PostConstruct
	public void init(){
		List data = new ArrayList<>();
		data.add(getDate());
		//retrievePurchase(data,"init");
		cornsAll("init");
	}
	
	public List<PurchasingCorn> getPurchCorn() {
		
	purchCorn = new ArrayList<PurchasingCorn>();
	int cnt = 1;	
	/*for(Map<String, Map<String, Map<Long, PurchasingCorn>>> a : datahold.values()){
			
			for(Map<String, Map<Long, PurchasingCorn>> b : a.values()){
				
				for(Map<Long, PurchasingCorn> c : b.values()){
					
					for(PurchasingCorn e : c.values()){
						e.setCounter(cnt++);
						purchCorn.add(e);
						
					}
					
				}
				
			}
			
		}*/
		if(allCorn==null) return purchCorn;
		Map<Long, PurchasingCorn> desc = new TreeMap(Collections.reverseOrder());
		desc.putAll(allCorn);
		
		for(PurchasingCorn p : desc.values()){
			p.setCounter(cnt++);
			p.setTimeStamp(p.getTimeStamp().replace(".0", ""));
			purchCorn.add(p);
		}
		
		return purchCorn;
	}

	public void setPurchCorn(List<PurchasingCorn> purchCorn) {
		this.purchCorn = purchCorn;
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
	private String recon(String str){
		
		if(CornConditions.WCD_NATIVE.getName().equalsIgnoreCase(str)){return "1";
		}else if(CornConditions.WHITE_WET.getName().equalsIgnoreCase(str) ){ return "2";
		}else if(CornConditions.WHITE_BILOG.getName().equalsIgnoreCase(str)){ return "3";
		}else if(CornConditions.YELLOW_DRY.getName().equalsIgnoreCase(str)){ return "4";
		}else if(CornConditions.YELLOW_BASA.getName().equalsIgnoreCase(str)){ return "5";
		}else if(CornConditions.YELLOW_BILOG.getName().equalsIgnoreCase(str)) { return "6";
		}else if(CornConditions.WHITE_SEMI_BASA.getName().equalsIgnoreCase(str)) { return "7";
		}else if(CornConditions.YELLOW_SEMI_BASA.getName().equalsIgnoreCase(str)){ return "8";}
		return "";
	}
	
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		System.out.println("Set amount: " + amount);
		this.amount = amount;
		this.calculate();
	}
	public BigDecimal getDriver() {
		return driver;
	}
	public void setDriver(BigDecimal driver) {
		System.out.println("set drvier : " + driver);
		this.driver = driver;
		this.calculate();
	}

	private void calculate(){
		
		if(driver!=null && amount==null){
			totalPrice = driver;
		}
		if(amount!=null && driver==null ){
			totalPrice = amount;
		}
		
		if(driver!=null && amount!=null){
			totalPrice = driver.add(amount);
		}
	
	String str = totalPrice.toPlainString();
	str = str.replace("Php", "");
	str = str.replace("$", "");
	totalPrice = new BigDecimal(str);
	System.out.println("Total price: " + totalPrice);
	calculateNet();	
	}
	
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}
	public List getCornColorsSearch() {
		
		cornColorsSearch = new ArrayList<>();
		cornColorsSearch.add(new SelectItem("0","ALL"));
		cornColorsSearch.add(new SelectItem("1","YELLOW"));
		cornColorsSearch.add(new SelectItem("2","WHITE"));
		
		return cornColorsSearch;
	}
	public void setCornColorsSearch(List cornColorsSearch) {
		this.cornColorsSearch = cornColorsSearch;
	}
	public String getCornColorSearch() {
		return cornColorSearch;
	}
	public void setCornColorSearch(String cornColorSearch) {
		this.cornColorSearch = cornColorSearch;
	}
	
	public List getCornColors() {
		
		cornColors = new ArrayList<>();
		cornColors.add(new SelectItem("1","YELLOW"));
		cornColors.add(new SelectItem("2","WHITE"));
		
		
		return cornColors;
	}
	public void setCornColors(List cornColors) {
		this.cornColors = cornColors;
	}
	public String getCornColor() {
		if(cornColor==null || cornColor.isEmpty()) {
			cornColor = "2";
		}
		return cornColor;
	}
	public void setCornColor(String cornColor) {
		this.cornColor = cornColor;
	}
	
	public String getFiltered() {
		return filtered;
	}
	public void setFiltered(String filtered) {
		this.filtered = filtered;
	}
	public List getFilteredSearch() {
		
		filteredSearch = new ArrayList<>();
		filteredSearch.add(new SelectItem("1","DETAILED"));
		filteredSearch.add(new SelectItem("2","SUMMARY"));
		
		return filteredSearch;
	}
	public void setFilteredSearch(List filteredSearch) {
		this.filteredSearch = filteredSearch;
	}
	public String getSearchDate() {
		
		if(searchDate==null)
			searchDate = getCurrentDate();
		
		return searchDate;
	}
	public void setSearchDate(String searchDate) {
		this.searchDate = searchDate;
	}
	public String save(){
	
	String receiptNumber = 	getLatestId()+"";
	boolean isOk = true;
	
	if(getCornColor()==null || getCornColor().isEmpty()){
		isOk = false;
		Application.addMessage(3, "Error", "Please provide Corn Color");
	}
	
	if(getCondition()==null || getCondition().isEmpty()){
		isOk = false;
		Application.addMessage(3, "Error", "Please provide corn condition");
	}
	
	if(getKilo()<=0){
		isOk = false;
		Application.addMessage(3, "Error", "Please provide kilo");
	}
	
	if(getAmount().intValue()<=0){
		isOk = false;
		Application.addMessage(3, "Error", "Please provide amount");
	}
	
	if(getClientName()==null) {
		isOk = false;
		Application.addMessage(3, "Error", "Please provide client name");
	}
	
	if(isOk){
	
			List data = new ArrayList<>();	
			data.add(receiptNumber);	
			data.add(getCornColor());
			data.add(getCondition());
			data.add(getDate());
			data.add(getKilo());
			data.add(getDiscount());
			data.add(getAmount());
			data.add(getDriver());
			data.add(getTotalPrice());
			
			HttpSession session = SessionBean.getSession();
			String proc_by = session.getAttribute("username").toString();
			if(proc_by==null){
				session.invalidate();
				return "logout";
			}	
			data.add(proc_by);
			
			if(getId()==null){
				insertData(data);
			}else{
				updateData(data);
				receiptNumber = getId()+"";
			}
			List data1 = new ArrayList<>();
			data1.add(getCurrentDate());
			
			String colors[] = {"N/A","YELLOW","WHITE"};
			
			String str = "MATT-AGRI BUY N SELL\n";
			str += "Sitio Lugan, Pob. Lake Sebu, So. Cot\n";
			str += "Date : "+ DateUtils.getCurrentDateMMDDYYYYTIME() +" \n";
			if(getClientName()!=null){
				 str +="Customer: " + getClientName().getFullName()+"\n"; 
			}
			str += "Corn : " + colors[Integer.valueOf(getCornColor())] +"\n";
			str += "Condition : " + con(getCondition()) +"\n";
			str += "Kilo : " + getKilo() +"\n";
			str += "Discount : " + getDiscount() +"\n";
			//str += "Total Price Php: " + Currency.formatAmount(getTotalPrice()+"") +"\n";
			str += "Total Php: " + Currency.formatAmount(getAmount()+"") +"\n";
			//str += "Driver Php: " + Currency.formatAmount(getDriver()+"") +"\n";
			//str += "Cashier: " + proc_by+"\n";
			//str +=" \n \n \n \n \n";
			str +=" \n";
			//saveSaleToFileReceipt(str, "FINAL-"+receiptNumber);
			
			String cornData = str;
			
			if(getKiloDetails()!=null && !getKiloDetails().isEmpty()){
				String kilod = "";
				kilod += "=======KILO DETAILS=======\n";
				kilod += getKiloDetails() + "\n \n";
				saveSaleToFileReceipt(kilod, "KILO-"+receiptNumber);
				cornData += kilod;
			}
			
			str = new String();
			double finalAmount = getAmount().doubleValue() -  getDeductedTotal();
			if(getDeductedTotal()>0){
			setFinalAmount(finalAmount);
			
			str +="=======DEDUCTION DETAILS======\n";
			str = str + getDeductedData();
			str +="\n";
			str +="Deducted: " + Currency.formatAmount(getDeductedTotal()+"") +"\n";
			str +="Net: " + Currency.formatAmount(finalAmount+"") +"\n \n";
			saveSaleToFileReceipt(str, "DEDUCTED-"+receiptNumber);
			str += "Cashier: " + proc_by+"\n";
			}else{
				str += "Cashier: " + proc_by+"\n";
			}
			str += "\n***THANK YOU***";
			str +=" \n \n \n \n \n";
			
			cornData = cornData + str;
			
			//str +=cornData + " \n \n \n \n \n";
			saveSaleToFileReceipt(cornData, receiptNumber);
			cashdrawerOpen();
			printReceipt(receiptNumber);//sending for printing
			cutPaper();
			
			cornsAll("init");
			clearFields();
			Application.addMessage(1, "Success", "Successfully saved.");
			
			PrimeFaces pm = PrimeFaces.current();
			pm.executeScript("hideWizard();");
			
		}
		return "SAVE";	
	}
	
	public void calculateNet(){
		double finalAmount = getAmount().doubleValue() -  getDeductedTotal();
		setFinalAmount(finalAmount);
	}
	
public static String viewReceipt(String receiptNumber){
		
	ReadApplicationDetails rd = new ReadApplicationDetails();
	
	//check log directory
	String receiptLocation = rd.getCornrpt();
	String receiptFile = receiptLocation + receiptNumber + ".txt";
	
		/*String receiptLocation = ReadConfig.value(Ipos.RECEIPTS_LOG);
		String receiptFile = receiptLocation + receiptNumber + ".txt";*/
	
		try{
		BufferedReader br = new BufferedReader(new FileReader(receiptFile));
		
		 	String line = null;
	        // Read from the original file and write to the new
		 	StringBuffer str = new StringBuffer();
		 	//str.append("\t****REPRINT****\n");
	        while ((line = br.readLine()) != null) {
	        	str.append(line + "\n");
	        }
	        br.close();
	        return str.toString();
		}catch(Exception e){}
		
		return null;
	}
	
public static String viewReceiptKilo(String receiptNumber){
		
		ReadApplicationDetails rd = new ReadApplicationDetails();
		
		//check log directory
		String receiptLocation = rd.getCornrpt();
		String receiptFile = receiptLocation + receiptNumber + ".txt";
		
			/*String receiptLocation = ReadConfig.value(Ipos.RECEIPTS_LOG);
			String receiptFile = receiptLocation + receiptNumber + ".txt";*/
		
			try{
			BufferedReader br = new BufferedReader(new FileReader(receiptFile));
			
			 	String line = null;
		        // Read from the original file and write to the new
			 	StringBuffer str = new StringBuffer();
			 	//str.append("\t****REPRINT****\n");
		        while ((line = br.readLine()) != null) {
		        	if("=======KILO DETAILS=======".equalsIgnoreCase(line)){
		        		//do nothing
		        	}else{
		        		str.append(line + "\n");
		        	}
		        }
		        br.close();
		        return str.toString();
			}catch(Exception e){}
			
			return null;
		}

public String viewDeducted(String receiptNumber){
		
		ReadApplicationDetails rd = new ReadApplicationDetails();
		
		//check log directory
		String receiptLocation = rd.getCornrpt();
		String receiptFile = receiptLocation + receiptNumber + ".txt";
		
			/*String receiptLocation = ReadConfig.value(Ipos.RECEIPTS_LOG);
			String receiptFile = receiptLocation + receiptNumber + ".txt";*/
		
			try{
			BufferedReader br = new BufferedReader(new FileReader(receiptFile));
			
			 	String line = null;
		        // Read from the original file and write to the new
			 	StringBuffer str = new StringBuffer();
			 	//str.append("\t****REPRINT****\n");
			 	
		        while ((line = br.readLine()) != null) {
		        	System.out.println("line:"+line);
		        	boolean isOk = true;
		        	try{
		        		double total = Double.valueOf(line.replace("Deducted: ", "").replace(",", ""));
		        		setDeductedTotal(total);
		        		isOk = false;
		        	}catch(Exception e){}
		        	
		        	try{
		        		double net = Double.valueOf(line.replace("Net: ", "").replace(",", ""));
		        		setFinalAmount(net);
		        		isOk = false;
		        	}catch(Exception e){}
		        	
		        	if("=======DEDUCTION DETAILS======".contains(line)){
		        		//do nothing
		        	}else{
		        		if(isOk){
		        			str.append(line + "\n");
		        		}
		        	}
		        }
		        setDeductedData(str.toString());
		        br.close();
		        return str.toString();
			}catch(Exception e){}
			
			return null;
		}

	public static void saveSaleToFileReceipt(String receiptInfo, String receiptNumber){
		
		ReadApplicationDetails rd = new ReadApplicationDetails();
		
		//check log directory
		String receiptLocation = rd.getCornrpt();
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
		String receiptLocation = rd.getCornrpt();
		String receiptFile = receiptLocation + receiptNo + ".txt";
		
		try{
		PrintService[] printServices;
		PrintService printService;
		String printerName =  rd.getPrinterName(); //"EPSON TM-U220 Receipt";

		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
		printServiceAttributeSet.add(new PrinterName(printerName, null));
		printServices = PrintServiceLookup.lookupPrintServices(null, printServiceAttributeSet);
		
		
		DocPrintJob job = printServices[0].createPrintJob();
		
		
		//PrintService service = PrintServiceLookup.lookupDefaultPrintService();
		//DocPrintJob job = service.createPrintJob();
		
			
			
		//String FILE_NAME = "C:\\ipos\\receipts\\000000000000026.txt";
		FileInputStream textStream = new FileInputStream(receiptFile);
		
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		Doc doc = new SimpleDoc(textStream, flavor, null);
		PrintRequestAttributeSet attrs = new HashPrintRequestAttributeSet();
		attrs.add(new Copies(1));
		job.print(doc, attrs);
		System.out.println("Printing...");
		}catch(Exception e){
			e.printStackTrace();
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
		} catch (PrintException ex) {
			System.out.println(ex.getMessage());
		}catch(NullPointerException ne) {
			System.out.println(ne.getMessage());
		}catch(ArrayIndexOutOfBoundsException ae) {
			System.out.println(ae.getMessage());
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
		} catch (PrintException ex) {
			System.out.println(ex.getMessage());
		} catch(ArrayIndexOutOfBoundsException ai) {
			System.out.println(ai.getMessage());
		}
	}
	
	public void clearFields(){
		setClientName(null);
		setKiloDetails(null);
		setDeductedData(null);
		setDeductedTotal(0);
		setFinalAmount(0);
		setCornColor(null);
		setCondition(null);
		setKilo(0.0);
		setDiscount(0.0);
		setAmount(new BigDecimal("0.00"));
		setDriver(new BigDecimal("0.00"));
		setTotalPrice(new BigDecimal("0.00"));
		setId(null);
		setCustomerName(null);
	}
	
	public String clickItem(PurchasingCorn d){
		
		if(d==null) return "error";
		System.out.println("before interpret");
		System.out.println("corn color : " + d.getCorncolor());
		System.out.println("codiditon : " + d.getConditions());
		
		setCornColor(d.getCorncolor().equalsIgnoreCase("YELLOW")? "1" : "2");
		setCondition(recon(d.getConditions()));
		
		System.out.println("after interpret");
		System.out.println("corn color : " + getCornColor());
		System.out.println("codiditon : " + getCondition());
		
		
		setKilo(d.getKilo());
		setDiscount(d.getDiscount());
		setAmount(d.getAmount());
		setDriver(d.getDriver());
		setTotalPrice(d.getTotalAmount());
		setId(d.getChasedId());
		setDate(d.getDateIn());
		if(d.getClientProfile()!=null){
			setClientName(d.getClientProfile());
			setCustomerName(d.getClientProfile().getFullName());
		}
		setDeductedData("");
		viewDeducted("DEDUCTED-"+d.getChasedId()+"");
		try{setKiloDetails(viewReceiptKilo("KILO-"+d.getChasedId()+""));}catch(Exception e){}
		return "click";
	}
	
	private void updateData(List data){
		String sql = "UPDATE purchasingcorn SET "
				+ "corncolor=?,"
				+ "conditions=?,"
				+ "datein=?,"
				+ "kilo=?,"
				+ "discount=?,"
				+ "amount=?,"
				+ "driver=?,"
				+ "totalamount=?,"
				+ "procby=?, clientId=? "
				+ " WHERE chasedid=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("updating data to purchasingcorn");
		actions.add("SQL : " + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, data.get(1).toString());
		ps.setString(2, data.get(2).toString());
		ps.setString(3, data.get(3).toString());
		ps.setString(4, data.get(4).toString());
		ps.setString(5, data.get(5).toString());
		ps.setString(6, data.get(6).toString());
		ps.setString(7, data.get(7).toString());
		ps.setString(8, data.get(8).toString());
		ps.setString(9, data.get(9).toString());
		String clientId = getClientName().getClientId()+"";
		ps.setString(10, clientId);
		ps.setLong(11, getId());
		for(int i=1; i<data.size();i++){
			actions.add(data.get(i).toString());
		}
		actions.add("clientId : " + clientId);
		actions.add(getId()+"");
		actions.add("execute update...");
		ps.executeUpdate();
		actions.add("closing....");
		ps.close();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully updated.");
		}catch(SQLException e){
			actions.add("Error updating : " + e.getMessage());
		}
		LogUserActions.logUserActions(actions);
		
		
	}
	
	
	private void searchAll(List data, String type){
		
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT * FROM purchasingcorn ORDER BY chasedid DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			//id = rs.getLong("chasedid");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void cornsAll(String type){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		allCorn = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
		String filltered = getFiltered()!=null? (getFiltered().equalsIgnoreCase("1")? "DETAILED" :  (getFiltered().equalsIgnoreCase("2")? "SUMMARY" : "ALL") ) : "ALL";
		
		String sql= "";
		
		try{
		conn = DataConnectDAO.getConnection();
		
		
		if("init".equalsIgnoreCase(type)){
			sql="SELECT *  FROM purchasingcorn WHERE datein=? ORDER BY timestamp ASC";
			ps = conn.prepareStatement(sql);
			ps.setString(1, getCurrentDate());
		}else if("search".equalsIgnoreCase(type)){
			
			String date = DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd");
			date +=":"+ DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd");
			setSearchDate(date);
			String col = getCornColorSearch()==null ? "ALL" : (getCornColorSearch().isEmpty()? "ALL" : (getCornColorSearch().equalsIgnoreCase("0")? "ALL" : getCornColorSearch()) ) ; 
			String cond = getCondSearch()==null? "ALL" : getCondSearch().isEmpty()? "ALL" : (getCondSearch().equalsIgnoreCase("0")? "ALL" : getCondSearch());
			
			if(getSearchDate()==null){
				setSearchDate(getCurrentDate());
			}
			
			if(getSearchDate().contains(":")){
				int cnt = getSearchDate().indexOf(":");
				//sql="SELECT *  FROM purchasingcorn WHERE datein>=? AND datein<=? ORDER BY timestamp DESC";
				//ps.setString(1, getSearchDate().split(":", cnt)[0]);
				//ps.setString(2, getSearchDate().split(":", cnt)[1]);
				
				
				if("ALL".equalsIgnoreCase(col) && "ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE datein>=? AND datein<=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate().split(":", cnt)[0]);
					ps.setString(2, getSearchDate().split(":", cnt)[1]);
				}else if(!"ALL".equalsIgnoreCase(col) && "ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE (datein>=? AND datein<=?) AND corncolor=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate().split(":", cnt)[0]);
					ps.setString(2, getSearchDate().split(":", cnt)[1]);
					ps.setString(3, col);
				}else if("ALL".equalsIgnoreCase(col) && !"ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE (datein>=? AND datein<=?) AND conditions=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate().split(":", cnt)[0]);
					ps.setString(2, getSearchDate().split(":", cnt)[1]);
					ps.setString(3, cond);
				}else if(!"ALL".equalsIgnoreCase(col) && !"ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE (datein>=? AND datein<=?) AND corncolor=? AND conditions=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate().split(":", cnt)[0]);
					ps.setString(2, getSearchDate().split(":", cnt)[1]);
					ps.setString(3, col);
					ps.setString(4, cond);
				}
				
			}else{
				
				if("ALL".equalsIgnoreCase(col) && "ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE datein=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate());
				}else if(!"ALL".equalsIgnoreCase(col) && "ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE datein=? AND corncolor=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate());
					ps.setString(2, col);
				}else if("ALL".equalsIgnoreCase(col) && !"ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE datein=? AND conditions=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate());
					ps.setString(2, cond);
				}else if(!"ALL".equalsIgnoreCase(col) && !"ALL".equalsIgnoreCase(cond)){
					sql="SELECT *  FROM purchasingcorn WHERE datein=? AND corncolor=? AND conditions=? ORDER BY timestamp ASC";
					ps = conn.prepareStatement(sql);
					ps.setString(1, getSearchDate());
					ps.setString(2, col);
					ps.setString(3, cond);
				}
			}
			
			
			
		}else{
			sql="SELECT *  FROM purchasingcorn WHERE datein=? ORDER BY timestamp ASC";
			ps = conn.prepareStatement(sql);
			ps.setString(1, getCurrentDate());
		}
		
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
		System.out.println("total cond " + kond.size());
		
		//temp storing of data
		Map<String, PurchasingCorn> stage1 = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
		Map<String, PurchasingCorn> stage2 = Collections.synchronizedMap(new HashMap<String, PurchasingCorn>());
		Map<Long, PurchasingCorn> stage3 = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
		
		//do this if filteration is selected
		if(!"ALL".equalsIgnoreCase(filltered)){
			if(allCorn!=null){
				
			}
		}else{
			if(allCorn!=null){
				
				
				//stage 1 collect all the same date
				for(String date : det.keySet()){
					for(PurchasingCorn p : allCorn.values()){
						if(date.equalsIgnoreCase(p.getDateIn())){
							stage1.put(date, p);
						}
					}
				}
				
				//stage 2 collect all the same corn color
				for(int i=1;i<=2;i++ ){
					
				}
			
			
			//arrange the data base on date colors and conditions of corn
			}
		}
		
		
		//calculating summary
		if(allCorn!=null){
			summaryTotal = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
			int totalKiloWhite=0,grandTotalWhite=0;
			int totalKiloYellow=0,grandTotalYellow=0;
			Long id =0l;
			for(String date : det.keySet()){
				
				System.out.println("DATE IN : " + date + " count " + id);
				
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
							double net = Numbers.formatDouble(Double.valueOf(netTotal));
							ppp.setNetPrice(new BigDecimal(net+""));
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
			
		}
		
	}
	
	private List<PurchasingCorn> summary = new ArrayList<PurchasingCorn>();
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
				p.setKilo(rs.getDouble("kilo"));
				p.setDiscount(rs.getDouble("discount"));
				p.setAmount(rs.getBigDecimal("amount"));
				p.setDriver(rs.getBigDecimal("driver"));
				p.setTotalAmount(rs.getBigDecimal("totalamount"));
				p.setProcBy(rs.getString("procby"));
				p.setTimeStamp(rs.getString("timestamp").replace("0.", ""));
				
				ClientProfile clientProfile = new ClientProfile();
				try{Long clientId = rs.getLong("clientId");
				String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND clientId=?";
				String[] params = new String[1];
				params[0] = clientId+"";
				clientProfile = ClientProfile.retrieveClientProfile(sql, params).get(0);
				System.out.println("clientProfile : "+ clientProfile);
				if(clientProfile==null){
					clientProfile.setClientId(0l);
					clientProfile.setFullName("UNKNOWN CUSTOMER");
				}else{
					p.setClientProfile(clientProfile);
				}
				}catch(Exception e){
					clientProfile.setClientId(0l);
					clientProfile.setFullName("UNKNOWN CUSTOMER"); 
				}
				allCorn.put(id, p);
			}
		}catch(SQLException e){}
	}
	
	private long getLatestId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT chasedid FROM purchasingcorn ORDER BY chasedid DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("chasedid");
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
	
	private void insertData(List data){
		String sql = "INSERT INTO purchasingcorn ("
				+ "chasedid,"
				+ "corncolor,"
				+ "conditions,"
				+ "datein,"
				+ "kilo,"
				+ "discount,"
				+ "amount,"
				+ "driver,"
				+ "totalamount,"
				+ "procby,clientId) " 
				+ "values(?,?,?,?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		List<String> actions = new ArrayList<>();
		actions.add("Inserting data to purchasingcorn");
		actions.add("SQL :" + sql);
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setLong(1, Long.valueOf(data.get(0).toString()));
		ps.setString(2, data.get(1).toString());
		ps.setString(3, data.get(2).toString());
		ps.setString(4, data.get(3).toString());
		ps.setString(5, data.get(4).toString());
		ps.setString(6, data.get(5).toString());
		ps.setString(7, data.get(6).toString());
		ps.setString(8, data.get(7).toString());
		ps.setString(9, data.get(8).toString());
		ps.setString(10, data.get(9).toString());
		
		for(int i=0; i<data.size();i++){
			actions.add(data.get(i).toString());
		}
		String clientId = getClientName().getClientId()+"";
		ps.setString(11, clientId);
		actions.add("clientId: " + clientId);
		actions.add("execute update...");
		ps.execute();
		DataConnectDAO.close(conn);
		actions.add("Data has been successfully saved..");
		}catch(SQLException e){
			actions.add("Error saving : " + e.getMessage());
		}
		LogUserActions.logUserActions(actions);
	}
	
	
	public String search(){
		
		/*List data = new ArrayList<>();
		
		String col = getCornColorSearch()!=null ? (getCornColorSearch().equalsIgnoreCase("1")? "YELLOW" : (getCornColorSearch().equalsIgnoreCase("2")? "WHITE" : getCornColorSearch().equalsIgnoreCase("0")? "ALL" : "ALL") ) : "ALL"; 
		String date = getSearchDate()!=null? getSearchDate() : getCurrentDate();
		String cond = getCondSearch()==null? "ALL" : getCondSearch().isEmpty()? "ALL" : con(getCondSearch());
		String filltered = getFiltered()!=null? (getFiltered().equalsIgnoreCase("1")? "DETAILED" :  (getFiltered().equalsIgnoreCase("2")? "SUMMARY" : "ALL") ) : "ALL";
		
		
		data.add(date);
		data.add(col);
		data.add(cond);
		data.add(filltered);
		
		System.out.println("Search param date : " + date + " color : " + col + " condition : " + cond + " filtered : " + filltered);
		
		retrievePurchase(data, "search");*/
		cornsAll("search");
		
		return null;	
	}
	public String getCondSearch() {
		return condSearch;
	}
	public void setCondSearch(String condSearch) {
		this.condSearch = condSearch;
	}
	
	public String getCurrentDate(){//MMMM d, yyyy
		//DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		//Date date = new Date();
		String _date = DateUtils.getCurrentYYYYMMDD();
		return _date;
	}
	
	public String getDate() {
		
		if(date==null)
			date = getCurrentDate();
		
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public double getKilo() {
		return kilo;
	}
	public void setKilo(double kilo) {
		this.kilo = kilo;
	}
	public double getDiscount() {
		return discount;
	}
	public void setDiscount(double discount) {
		this.discount = discount;
	}
	
	
	public String getCondition() {
		if(condition==null || condition.isEmpty()) {
			condition = "1";
		}
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public void reloadConditions() {
		getConditions();
	}
	
	public List getConditions() {
		System.out.println("getConditions()>>> before");
		conditions = new ArrayList<>();
		
		if(cornColor!=null && "1".equalsIgnoreCase(cornColor)) {
			conditions.add(new SelectItem(CornConditions.YELLOW_DRY.getCode(),CornConditions.YELLOW_DRY.getName()));
			conditions.add(new SelectItem(CornConditions.YELLOW_BASA.getCode(),CornConditions.YELLOW_BASA.getName()));
			conditions.add(new SelectItem(CornConditions.YELLOW_BILOG.getCode(),CornConditions.YELLOW_BILOG.getName()));
			conditions.add(new SelectItem(CornConditions.YELLOW_SEMI_BASA.getCode(),CornConditions.YELLOW_SEMI_BASA.getName()));
		}else if(cornColor!=null && "2".equalsIgnoreCase(cornColor)) {
			conditions.add(new SelectItem(CornConditions.WCD_NATIVE.getCode(),CornConditions.WCD_NATIVE.getName()));
			conditions.add(new SelectItem(CornConditions.WHITE_WET.getCode(),CornConditions.WHITE_WET.getName()));
			conditions.add(new SelectItem(CornConditions.WHITE_BILOG.getCode(),CornConditions.WHITE_BILOG.getName()));
			conditions.add(new SelectItem(CornConditions.WHITE_SEMI_BASA.getCode(),CornConditions.WHITE_SEMI_BASA.getName()));
		}else {
			conditions.add(new SelectItem(CornConditions.WCD_NATIVE.getCode(),CornConditions.WCD_NATIVE.getName()));
		}
		
		System.out.println("getConditions()>>> after count " + conditions.size());
		
		return conditions;
	}
	public List getConditionsSearch() {
		
		conditionsSearch = new ArrayList<>();
		conditionsSearch.add(new SelectItem("0","ALL"));
		conditionsSearch.add(new SelectItem(CornConditions.WCD_NATIVE.getCode(),CornConditions.WCD_NATIVE.getName()));
		conditionsSearch.add(new SelectItem(CornConditions.WHITE_WET.getCode(),CornConditions.WHITE_WET.getName()));
		conditionsSearch.add(new SelectItem(CornConditions.WHITE_BILOG.getCode(),CornConditions.WHITE_BILOG.getName()));
		conditionsSearch.add(new SelectItem(CornConditions.YELLOW_DRY.getCode(),CornConditions.YELLOW_DRY.getName()));
		conditionsSearch.add(new SelectItem(CornConditions.YELLOW_BASA.getCode(),CornConditions.YELLOW_BASA.getName()));
		conditionsSearch.add(new SelectItem(CornConditions.YELLOW_BILOG.getCode(),CornConditions.YELLOW_BILOG.getName()));
		conditionsSearch.add(new SelectItem(CornConditions.WHITE_SEMI_BASA.getCode(),CornConditions.WHITE_SEMI_BASA.getName()));
		conditionsSearch.add(new SelectItem(CornConditions.YELLOW_SEMI_BASA.getCode(),CornConditions.YELLOW_SEMI_BASA.getName()));
		
		return conditionsSearch;
	}
	public void setConditionsSearch(List conditionsSearch) {
		this.conditionsSearch = conditionsSearch;
	}
	
	public void setConditions(List conditions) {
		this.conditions = conditions;
	}
	public String getConditionLabel() {
		
		if(conditionLabel==null){
			conditionLabel = "Please select conditions...";
		}
		
		return conditionLabel;
	}
	public void setConditionLabel(String conditionLabel) {
		this.conditionLabel = conditionLabel;
	}
	
	private void retrievePurchase(List data,String type){
		
		if("init".equalsIgnoreCase(type)){
		
		if(dataSearch==null){
			dataSearch = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
			dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
			dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
			dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		
		String sql= "SELECT *  FROM purchasingcorn WHERE datein>=? AND datein <=?";
		
		
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, data.get(0).toString());
		ps.setString(2, data.get(0).toString());
		
		
		
		System.out.println("Is exist sql: " + ps.toString());
		rs = ps.executeQuery();
		
		datahold = dbLoad(rs);
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		}else if("search".equalsIgnoreCase(type)){
			
			datatemp = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
			String _date = data.get(0).toString();
			String _color = data.get(1).toString();
			String _cond = data.get(2).toString();
			String _filtered = data.get(3).toString();
			boolean isRangeDate = _date.contains(":");
			Map<String, String> date = Collections.synchronizedMap(new HashMap<String, String>());			
					if(isRangeDate){
							int cnt = _date.indexOf(":");
							
							PreparedStatement ps = null;
							PreparedStatement ps1 = null;
							ResultSet rs = null;
							ResultSet rs1 = null;
							Connection conn = null;
							
							String sql= "SELECT *  FROM purchasingcorn WHERE datein>=? AND datein <=?";
							
							
							
							try{
							conn = DataConnectDAO.getConnection();
							ps = conn.prepareStatement("SELECT distinct datein  FROM purchasingcorn WHERE datein>=? AND datein <=?");
							ps1 = conn.prepareStatement(sql);
							ps.setString(1, _date.split(":",cnt)[0]);
							ps.setString(2, _date.split(":",cnt)[1]);
							ps1.setString(1, _date.split(":",cnt)[0]);
							ps1.setString(2, _date.split(":",cnt)[1]);
							System.out.println("Search date " + ps.toString());
							rs = ps.executeQuery();
							rs1 = ps1.executeQuery();
							dataSearch = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
							dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
							dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
							dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
							datahold = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
							datatemp = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
							System.out.println("Preparing to load.... dataSearch " + dataSearch.size());
							date=dateSearch(rs);
							dataSearch = dbLoad(rs1);
							System.out.println("done loading......dataSearch " + dataSearch.size());
							
							rs.close();
							rs1.close();
							ps.close();
							ps1.close();
							DataConnectDAO.close(conn);
							}catch(SQLException e){e.printStackTrace();}
							
							
							for(String _dt : date.values() ){
								map(dataSearch, _dt, _color, _cond, _filtered,isRangeDate);
							}
							
							
					}else{
						_date = checkDate(_date);
						map(dataSearch, _date, _color, _cond, _filtered,isRangeDate);
				
					}//if
				
				
				datahold = datatemp; // transfer all the found data
			}
			
		}
	private String checkDate(String date){
		System.out.println("Check date : " + date);
		//if(!date.equalsIgnoreCase(getCurrentDate())){
				PreparedStatement ps = null;
				ResultSet rs = null;
				Connection conn = null;
				dataSearch = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
				dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
				dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
				dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
			
				try{
					String sql= "SELECT *  FROM purchasingcorn WHERE datein=?";
					conn = DataConnectDAO.getConnection();
					ps = conn.prepareStatement(sql);
					ps.setString(1, date);
					System.out.println("Search date " + ps.toString());
					rs = ps.executeQuery();
					
					dataSearch = dbLoad(rs);
					
					rs.close();
					ps.close();
					DataConnectDAO.close(conn);
					}catch(SQLException e){e.printStackTrace();}
			//}
		return date;
	}
	private Map<String, String> dateSearch(ResultSet rs){
		Map<String, String> date = Collections.synchronizedMap(new HashMap<String, String>());
		try{
		while(rs.next()){
			String d = rs.getString("datein");
			System.out.println("before Adding date to map.... " + d);
			if(!date.containsKey(d)){
				date.put(d, d);
				System.out.println("Adding date to map.... " + d);
			}else if(date==null){
				date = Collections.synchronizedMap(new HashMap<String, String>());
				date.put(d, d);
				System.out.println("NULL : Adding date to map.... " + d);
			}
			
			
		}
		}catch(SQLException e){}
		return date;
	}
	private Map<String, Map<String, Map<String, Map<Long, PurchasingCorn>>>> dbLoad(ResultSet rs){
		try{
		int cnt = 1;
		dataSearch = Collections.synchronizedMap(new HashMap<String, Map<String,Map<String,Map<Long,PurchasingCorn>>>>());
		
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
			p.setKilo(rs.getDouble("kilo"));
			p.setDiscount(rs.getDouble("discount"));
			p.setAmount(rs.getBigDecimal("amount"));
			p.setDriver(rs.getBigDecimal("driver"));
			p.setTotalAmount(rs.getBigDecimal("totalamount"));
			p.setProcBy(rs.getString("procby"));
			p.setCounter(cnt);
			
			System.out.println("Counting data " + cnt++ +" date : " + date + " colors : " + colors + " conditions : " + cond);
			
			if(dataSearch.containsKey(date)){
				System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 2 ");
				dataColors = dataSearch.get(date);
				
				if(dataColors.containsKey(colors)){
					System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 3 ");
					dataCondtions = dataColors.get(colors);
					
					if(dataCondtions.containsKey(cond)){
						System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 5 ");
						dataPurch = dataCondtions.get(cond);
						
						if(dataPurch.containsKey(id)){
							System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 7 ");
							//dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
							dataPurch.put(id, p);
							dataCondtions.put(cond, dataPurch);
							dataColors.put(colors, dataCondtions);
							dataSearch.put(date, dataColors);
						}else{
							System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 8 ");
							dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
							dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
							dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
							dataPurch.put(id, p);
							dataCondtions.put(cond, dataPurch);
							dataColors.put(colors, dataCondtions);
							dataSearch.put(date, dataColors);
						}
						
					}else{
						System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 6 ");
						dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
						dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
						dataPurch.put(id, p);
						dataCondtions.put(cond, dataPurch);
						dataColors.put(colors, dataCondtions);
						dataSearch.put(date, dataColors);
					}
					
					
				}else{
					System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 4 ");
					dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
					dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
					dataPurch.put(id, p);
					dataCondtions.put(cond, dataPurch);
					dataColors.put(colors, dataCondtions);
					dataSearch.put(date, dataColors);
				}
				
			}else{
				dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
				dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
				dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
				System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 1 ");
				dataPurch.put(id, p);
				dataCondtions.put(cond, dataPurch);
				dataColors.put(colors, dataCondtions);
				dataSearch.put(date, dataColors);
			}
			
		}
		//rs.close();
		}catch(Exception e){}
		System.out.println(" data load >>>>>>>>>>>>>>>>>>>>>>>>>>>>>> count dataSearch passing " + dataSearch.size());
		return dataSearch;
	}
	
	private void map(Map<String, Map<String, Map<String, Map<Long, PurchasingCorn>>>> dataSearch, String _date, String _color, String _cond, String _filtered, boolean isRangeDate){
		
		for(Map<String, Map<String, Map<Long, PurchasingCorn>>> a : dataSearch.values()){
			//if(a.containsKey(_date)){
				for(Map<String, Map<Long, PurchasingCorn>> b : a.values()){
					
					//if(b.containsKey(_color)){
						
					for(Map<Long, PurchasingCorn> c : b.values()){
						//if(c.containsKey(_cond)){
						
							for(PurchasingCorn p : c.values()){
								
								System.out.println("))))))))))))))))))) Date : " + p.getDateIn() + " Search date : " + _date);
								
								//specific search
								if(_color.equalsIgnoreCase(p.getCorncolor()) && _cond.equalsIgnoreCase(p.getConditions())){
									
									RangeDate(p, _filtered, _date,isRangeDate);
									
								}
								
								//All
								if("ALL".equalsIgnoreCase(_color) && "ALL".equalsIgnoreCase(_cond)){
									
									RangeDate(p, _filtered, _date,isRangeDate);
									
									System.out.println("Conditon 1 color : " + _color + " cond : " + _color);
								}
								
								//All Colors but specific conditions
								if("ALL".equalsIgnoreCase(_color) && !"ALL".equalsIgnoreCase(_cond)){
									if(_cond.equalsIgnoreCase(p.getConditions())){
										
										RangeDate(p, _filtered, _date,isRangeDate);
										
										System.out.println("Conditon 2 color : " + _color + " cond : " + _color);
									}
								}
								
								//specific color but not specific condition
								if(!"ALL".equalsIgnoreCase(_color) && "ALL".equalsIgnoreCase(_cond)){
									if(_color.equalsIgnoreCase(p.getCorncolor())){
										
										RangeDate(p, _filtered, _date,isRangeDate);
										
										System.out.println("Conditon 3 color : " + _color + " cond : " + _color);
									}
								}
								
								//specific color and specific condition
								if(!"ALL".equalsIgnoreCase(_color) && !"ALL".equalsIgnoreCase(_cond)){
									if(_color.equalsIgnoreCase(p.getCorncolor()) && _cond.equalsIgnoreCase(p.getConditions()) ){
										
										RangeDate(p, _filtered, _date,isRangeDate);
										
										System.out.println("Conditon 4 color : " + _color + " cond : " + _color);
									}
								}
								
								
							
							}
							
						//}//if condition
					 }
						
					//}//if color
				}
					
			//}//if date
			
			}//for
	}
	
	private void RangeDate(PurchasingCorn p, String _filtered, String date, boolean isRangeDate){
		
		
		loopData(p,_filtered);
		
	}
	
	private void loopData(PurchasingCorn p, String _filtered){
		
		/*dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
		dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
		dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());*/		
		
		String date = p.getDateIn();
		String colors = p.getCorncolor();
		String cond = p.getConditions();
		long id = p.getChasedId();
		
		if(datatemp.containsKey(date)){
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 2 ");
			dataColors = datatemp.get(date);
			
			if(dataColors.containsKey(colors)){
				
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 3 ");
				
				dataCondtions = dataColors.get(colors);
				
				if(dataCondtions.containsKey(cond)){
					
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 5 ");
					
					dataPurch = dataCondtions.get(cond);
					
					if(dataPurch.containsKey(id)){
						
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 7 ");
						
						//dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
						dataPurch.put(id, p);
						dataCondtions.put(cond, dataPurch);
						dataColors.put(colors, dataCondtions);
						datatemp.put(date, dataColors);
					}else{
						
						System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 8 ");
						
						//dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
						//dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
						//dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
						dataPurch.put(id, p);
						dataCondtions.put(cond, dataPurch);
						dataColors.put(colors, dataCondtions);
						datatemp.put(date, dataColors);
					}
					
				}else{
					
					System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 6 ");
					
					dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
					dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
					dataPurch.put(id, p);
					dataCondtions.put(cond, dataPurch);
					dataColors.put(colors, dataCondtions);
					datatemp.put(date, dataColors);
				}
				
				
			}else{
				
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 4 ");
				
				dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
				dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
				dataPurch.put(id, p);
				dataCondtions.put(cond, dataPurch);
				dataColors.put(colors, dataCondtions);
				datatemp.put(date, dataColors);
			}
			
		}else{
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 1 ");
			
			dataColors = Collections.synchronizedMap(new HashMap<String, Map<String,Map<Long,PurchasingCorn>>>());
			dataCondtions = Collections.synchronizedMap(new HashMap<String, Map<Long,PurchasingCorn>>());
			dataPurch = Collections.synchronizedMap(new HashMap<Long, PurchasingCorn>());
			
			dataPurch.put(id, p);
			dataCondtions.put(cond, dataPurch);
			dataColors.put(colors, dataCondtions);
			datatemp.put(date, dataColors);
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
	public static void main(String[] args) {
		
		
		/*WhiteCornBean xx = new WhiteCornBean();
		List data = new ArrayList<>();
		data.add("03-24-2016");
		xx.retrievePurchase(data, "init");
		System.out.println("Date : \t\t Color : Conditions :  Kilo :  Disc: \t Amount : ");
		
		
		for(Map<String, Map<String, Map<Long, PurchasingCorn>>> a : xx.dataSearch.values()){
			
			for(Map<String, Map<Long, PurchasingCorn>> b : a.values()){
				
				for(Map<Long, PurchasingCorn> c : b.values()){
					
					for(PurchasingCorn e : c.values()){
						
						System.out.println(e.getDateIn() + "\t" + e.getCorncolor() + "\t" + e.getConditions() + "\t" + e.getKilo() + "\t" + e.getDiscount() + "\t" + e.getAmount());
						
					}
					
				}
				
			}
			
		}*/
		
		
		/*String date = "2-24-2016:03-25-2016";
		boolean isColon = date.contains(":");
		int cnt = date.indexOf(":");
		System.out.println(cnt);
		System.out.println(date.split(":",cnt)[0]);
		System.out.println(date.split(":",cnt)[1]);*/
		
		/*double discount = 0.0;
		discount = 1.6*1.2;
		System.out.println(discount);*/
		
		/*double value = 87.69999694824219;
		DecimalFormat df = new DecimalFormat("####0.00");
		System.out.println("Value: " + df.format(value));*/
		
	}
	public void printReport(){
		try{
			
		compileReport();	
			
		 File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
		 FacesContext faces = FacesContext.getCurrentInstance();
		 ExternalContext context = faces.getExternalContext();
		 HttpServletResponse response = (HttpServletResponse)context.getResponse();
			
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
	private void compileReport(){
		try{
			ReportCompiler compiler = new ReportCompiler();
			String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
	  		ArrayList<PurchasingCorn> rpts = new ArrayList<PurchasingCorn>();
	  		float kilow=0f, disw=0f, driverw=0f,totalw=0f;
	  		float kiloy=0f, disy=0f, drivery=0f,totaly=0f;
	  		
	  		Map<Long, PurchasingCorn> desc = new TreeMap(Collections.reverseOrder());
			desc.putAll(allCorn);
	  		
	  		for(PurchasingCorn r : desc.values()){
	  			rpts.add(r);
	  			
	  			if("WHITE".equalsIgnoreCase(r.getCorncolor())){
	  				kilow += Float.valueOf(r.getKilo()+"");
	  				disw += Float.valueOf(r.getDiscount()+"");
	  				driverw += Float.valueOf(r.getDriver()+"");
	  				totalw += Float.valueOf(r.getTotalAmount()+"");
	  			}else if("YELLOW".equalsIgnoreCase(r.getCorncolor())){
	  				kiloy += Float.valueOf(r.getKilo()+"");
	  				disy += Float.valueOf(r.getDiscount()+"");
	  				drivery += Float.valueOf(r.getDriver()+"");
	  				totaly += Float.valueOf(r.getTotalAmount()+"");
	  			}
	  			
	  		}
	  		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(rpts);
	  		HashMap param = new HashMap();
	  		
	  		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");//new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
			Date date = new Date();
			
	  		param.put("PARAM_DATE", dateFormat.format(date));
	  		dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm: a");
			date = new Date();
	  		param.put("PARAM_PRINTEDDATE","PRINTED DATE: "+dateFormat.format(date));
	  		
	  		HttpSession session = SessionBean.getSession();
			String proc_by = session.getAttribute("username").toString();
	  		if(proc_by!=null){
	  			param.put("PARAM_PROCCESSEDBY","PROCCESSED BY: " + proc_by);
	  		}else{
	  			param.put("PARAM_PROCCESSEDBY","");
	  		}
	  		
	  		
	  		param.put("PARAM_W_PURCHASED","Php " + formatAmount(totalw+""));
	  		param.put("PARAM_W_CURRENT_PRICE","Php "+ getCornPriceWhite());
	  		param.put("PARAM_W_TOTAL_KILO",""+ kilow);
	  		param.put("PARAM_W_TOTAL_DISCOUNT",""+disw);
	  		param.put("PARAM_W_TOTAL_COMMISION","Php " + formatAmount(driverw+""));

	  		param.put("PARAM_Y_PURCHASED","Php " + formatAmount(totaly+""));
	  		param.put("PARAM_Y_CURRENT_PRICE","Php "+getCornPriceYellow());
	  		param.put("PARAM_Y_TOTAL_KILO",""+ kiloy);
	  		param.put("PARAM_Y_TOTAL_DISCOUNT",""+disy);
	  		param.put("PARAM_Y_TOTAL_COMMISION","Php " + drivery);
	  		
	  		param.put("PARAM_TOTAL_WY_PURCHASED","Php "+getTotalWYCorn());
	  		
	  		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, param, beanColl);
	  	    JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		
			System.out.println("pdf successfully created...");
			}catch(Exception e){
				e.printStackTrace();
			}
	}

	public List<String> autoClientName(String query){
		String sql = "SELECT fullName FROM clientprofile WHERE isactiveclient=1 AND fullName like '%" + query + "%'";
		String[] params = new String[0];
		List<String> result = new ArrayList<>();
		for(ClientProfile d : ClientProfile.retrieveClientProfile(sql, params)){
			result.add(d.getFullName());
			System.out.println("Name>> " + d.getFullName());
		}	
		return result;
	}
	
	public void clickClient() {
		System.out.println("Click Client Name : " + getClientName());
		setClientName(getClientName());
	}
	
	public List<ClientProfile> autoClientSuggestion(String query){
		String sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND fullName like '%" + query + "%' LIMIT 5)";
		String[] params = new String[0];
		
		if(query!=null && !query.isEmpty()) {
			sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 AND fullName like '%" + query + "%' LIMIT 5";
		}else {
			sql = "SELECT * FROM clientprofile WHERE isactiveclient=1 ORDER BY clientId DESC LIMIT 5";
		}
		
		List<ClientProfile> cus = Collections.synchronizedList(new ArrayList<ClientProfile>());
		ClientProfile pr = new ClientProfile();
		pr.setClientId(0l);
		pr.setFullName("Results >>");
		pr.setIsActive(1);
		cus.add(pr);
		for(ClientProfile p : ClientProfile.retrieveClientProfile(sql, params)) {
			cus.add(p);
		}
		
		return  cus;
	}
	
	/*
	 * name to client id
	 */
	private String selectedClientName(String name){
		String sql = "SELECT clientId FROM clientprofile WHERE isactiveclient=1 AND fullName=?";
		String[] params = new String[1];
		params[0] = name.replace("--", "");
		try{name = ClientProfile.retrieveClientProfile(sql, params).get(0).getClientId().toString();}catch(Exception e){name="0";}
		return name;
	}
	
	/*
	 * name to client id
	 */
	private String clientName(String clientId){
		String sql = "SELECT fullName FROM clientprofile WHERE isactiveclient=1 AND clientId=?";
		String[] params = new String[1];
		params[0] = clientId.replace("--", "");
		try{clientId = ClientProfile.retrieveClientProfile(sql, params).get(0).getClientId().toString();}catch(Exception e){clientId="";}
		return clientId;
	}
	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getDeductedData() {
		return deductedData;
	}

	public void setDeductedData(String deductedData) {
		this.deductedData = deductedData;
	}

	public double getFinalAmount() {
		return finalAmount;
	}

	public void setFinalAmount(double finalAmount) {
		this.finalAmount = finalAmount;
	}

	public double getDeductedTotal() {
		return deductedTotal;
	}

	public void setDeductedTotal(double deductedTotal) {
		this.deductedTotal = deductedTotal;
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
	
	public void printPdf() {
		try {
		compileReport();	
			
		String pdfName = REPORT_NAME +".pdf";
	    File pdfFile = new File(REPORT_PATH + REPORT_NAME +".pdf");
	    /*tempPdfFile = DefaultStreamedContent.builder()
	    		.contentType("application/pdf")
	    		.name(pdfName)
	    		.stream(()-> this.getClass().getResourceAsStream(REPORT_PATH + REPORT_NAME +".pdf"))
	    		.build();
	    		//new DefaultStreamedContent(new FileInputStream(pdfFile), "application/pdf", pdfName);
  	    */
	    
	    tempPdfFile = DefaultStreamedContent.builder()
				 .contentType("application/pdf")
				 .name(pdfName)
				 .stream(()-> {
					try {
						return new FileInputStream(pdfFile);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return null;
					}
				})
				 .build();
	    
	    
  	    PrimeFaces pm = PrimeFaces.current();
  	    pm.executeScript("showPdf();hideButton();");
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public String generateRandomIdForNotCaching() {
		return java.util.UUID.randomUUID().toString();
	}
	
	public StreamedContent getTempPdfFile() throws IOException {
		File file = new File(REPORT_PATH, REPORT_NAME + ".pdf");
		if(tempPdfFile==null) {
			String pdfName = "CornDetails.pdf";
			String pdf = REPORT_PATH + REPORT_NAME + ".pdf";
			//pdf += pdfName;
			System.out.println("pdf file >>>> " + pdf);
			
			
		    File pdfFile = new File(pdf);
		    /*
		    return DefaultStreamedContent.builder()
		    		.contentType("application/pdf")
		    		.name(pdfName)
		    		.stream(()-> this.getClass().getResourceAsStream(REPORT_PATH + REPORT_NAME +".pdf"))
		    		.build();//new DefaultStreamedContent(new FileInputStream(pdfFile), "application/pdf", pdfName);
		    */
		    
		    return DefaultStreamedContent.builder()
					 .contentType("application/pdf")
					 .name(pdfName)
					 .stream(()-> {
						try {
							return new FileInputStream(pdfFile);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return null;
						}
					})
					 .build();
		    
		    
		}else {
			return tempPdfFile;
		}
	  }
	public void setTempPdfFile(StreamedContent tempPdfFile) {
		this.tempPdfFile = tempPdfFile;
	}

	public ClientProfile getClientName() {
		return clientName;
	}

	public void setClientName(ClientProfile clientName) {
		this.clientName = clientName;
	}
	
}
