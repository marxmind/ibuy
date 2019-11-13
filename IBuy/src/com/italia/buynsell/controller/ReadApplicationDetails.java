package com.italia.buynsell.controller;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadApplicationDetails {
	
	public ReadApplicationDetails(){
		readConf();
	}
	
	private final String sep = File.separator;
	private final String PRIMARY_DRIVE = System.getenv("SystemDrive");
	private final String PATH = PRIMARY_DRIVE + sep + "BuyNSell" + sep + "conf" + sep;
	private final String FILE_NAME = "application.xml";
	
	private String databaseName;
	private String driver;
	private String addressPort;
	private String SSL;
	private String userName;
	private String password;
	private String applicationExp;
	private String applicationVersion;
	private String copyright;
	private String author;
	private String supportEamil;
	private String supportNo;
	private String includeIpay;
	private String documentsPath;
	private String imagesPath;
	private String logPath;
	private String includeLog;
	private String salesreceipt;
	private String cornrpt;
	private String printerName;
	private String expenses;
	private String purchased;
	private String themeStyle;
	private String clientTransReciept;
	private String chatLog;
	
	public String getIncludeIpay() {
		return includeIpay;
	}
	public void setIncludeIpay(String includeIpay) {
		this.includeIpay = includeIpay;
	}
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public String getAddressPort() {
		return addressPort;
	}
	public void setAddressPort(String addressPort) {
		this.addressPort = addressPort;
	}
	public String getSSL() {
		return SSL;
	}
	public void setSSL(String sSL) {
		SSL = sSL;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getApplicationExp() {
		return applicationExp;
	}
	public void setApplicationExp(String applicationExp) {
		this.applicationExp = applicationExp;
	}
	public String getApplicationVersion() {
		return applicationVersion;
	}
	public void setApplicationVersion(String applicationVersion) {
		this.applicationVersion = applicationVersion;
	}
	public String getCopyright() {
		return copyright;
	}
	public void setCopyright(String copyright) {
		this.copyright = copyright;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getSupportEamil() {
		return supportEamil;
	}
	public void setSupportEamil(String supportEamil) {
		this.supportEamil = supportEamil;
	}
	public String getSupportNo() {
		return supportNo;
	}
	public void setSupportNo(String supportNo) {
		this.supportNo = supportNo;
	}
	
	
	public void readConf(){
		try{
			File xmlFile = new File(PATH+FILE_NAME);
			
			if(xmlFile.exists()){
				
				DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
				Document doc = documentBuilder.parse(xmlFile);
				
				/////////////normalize
				doc.getDocumentElement().normalize();
				//System.out.println("Reading conf......");
				
				NodeList ls = doc.getElementsByTagName("databaseDetails");
				for(int i=0; i<ls.getLength(); i++){
					Node n = ls.item(i);
					//System.out.println("Current Node: "+ n.getNodeName());
					
					if(n.getNodeType() == Node.ELEMENT_NODE){
						Element e = (Element)n;
						//System.out.println("Update field id : " + e.getAttribute("id"));
						setDatabaseName(e.getElementsByTagName("databaseName").item(0).getTextContent());
						setDriver(e.getElementsByTagName("driver").item(0).getTextContent());
						setAddressPort(e.getElementsByTagName("addressPort").item(0).getTextContent());
						setSSL(e.getElementsByTagName("SSL").item(0).getTextContent());
						setUserName(e.getElementsByTagName("username").item(0).getTextContent());
						setPassword(e.getElementsByTagName("password").item(0).getTextContent());
						setApplicationExp(e.getElementsByTagName("applicationExp").item(0).getTextContent());
						setApplicationVersion(e.getElementsByTagName("applicationVersion").item(0).getTextContent());
						setCopyright(e.getElementsByTagName("copyright").item(0).getTextContent());
						setAuthor(e.getElementsByTagName("author").item(0).getTextContent());
						setSupportEamil(e.getElementsByTagName("supportEamil").item(0).getTextContent());
						setSupportNo(e.getElementsByTagName("supportNo").item(0).getTextContent());
						setIncludeIpay(e.getElementsByTagName("includeIPay").item(0).getTextContent());
						setDocumentsPath(e.getElementsByTagName("documentsPath").item(0).getTextContent());
						setImagesPath(e.getElementsByTagName("imagesPath").item(0).getTextContent());
						setIncludeLog(e.getElementsByTagName("includeLog").item(0).getTextContent());
						setLogPath(e.getElementsByTagName("logPath").item(0).getTextContent());
						setSalesreceipt(e.getElementsByTagName("salesreceipt").item(0).getTextContent());
						setCornrpt(e.getElementsByTagName("cornrpt").item(0).getTextContent());
						setPrinterName(e.getElementsByTagName("printername").item(0).getTextContent());
						setExpenses(e.getElementsByTagName("expenses").item(0).getTextContent());
						setPurchased(e.getElementsByTagName("purchased").item(0).getTextContent());
						setThemeStyle(e.getElementsByTagName("themeStyle").item(0).getTextContent());
						setClientTransReciept(e.getElementsByTagName("clientReceipt").item(0).getTextContent());
						setChatLog(e.getElementsByTagName("chat").item(0).getTextContent());
					}
					
				}
				//System.out.println("completed reading conf......");
			}else{
				System.out.println("File is not exist...");
			}
			
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		ReadApplicationDetails s = new ReadApplicationDetails();
		System.out.println(s.getImagesPath());
		
		System.out.println("Default OS drive : " + System.getenv("SystemDrive"));
		
	}
	public String getDocumentsPath() {
		return documentsPath;
	}
	public String getImagesPath() {
		return imagesPath;
	}
	public String getLogPath() {
		return logPath;
	}
	public void setDocumentsPath(String documentsPath) {
		this.documentsPath = documentsPath;
	}
	public void setImagesPath(String imagesPath) {
		this.imagesPath = imagesPath;
	}
	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}
	public String getIncludeLog() {
		return includeLog;
	}
	public void setIncludeLog(String includeLog) {
		this.includeLog = includeLog;
	}
	public String getSalesreceipt() {
		return salesreceipt;
	}
	public void setSalesreceipt(String salesreceipt) {
		this.salesreceipt = salesreceipt;
	}
	public String getCornrpt() {
		return cornrpt;
	}
	public void setCornrpt(String cornrpt) {
		this.cornrpt = cornrpt;
	}
	public String getPrinterName() {
		return printerName;
	}
	public void setPrinterName(String printerName) {
		this.printerName = printerName;
	}
	public String getExpenses() {
		return expenses;
	}
	public void setExpenses(String expenses) {
		this.expenses = expenses;
	}
	public String getPurchased() {
		return purchased;
	}
	public void setPurchased(String purchased) {
		this.purchased = purchased;
	}
	public String getThemeStyle() {
		return themeStyle;
	}
	public void setThemeStyle(String themeStyle) {
		this.themeStyle = themeStyle;
	}
	public String getClientTransReciept() {
		return clientTransReciept;
	}
	public void setClientTransReciept(String clientTransReciept) {
		this.clientTransReciept = clientTransReciept;
	}
	public String getChatLog() {
		return chatLog;
	}
	public void setChatLog(String chatLog) {
		this.chatLog = chatLog;
	}
	
	
}



















