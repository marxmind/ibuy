package com.italia.buynsell.controller;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ReadDatabaseIPayDetails {
		
		public ReadDatabaseIPayDetails(){
			readConf();
		}
		
		private final String sep = File.separator;
		private final String PATH = "C:" + sep + "IPay" + sep + "conf" + sep;
		private final String FILE_NAME = "ipaydb.xml";
		
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
					System.out.println("Get field name: "+ doc.getDocumentElement().getNodeName());
					
					NodeList ls = doc.getElementsByTagName("databaseDetails");
					for(int i=0; i<ls.getLength(); i++){
						Node n = ls.item(i);
						System.out.println("Current Node: "+ n.getNodeName());
						
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
						}
						
						
					}
					
				}else{
					System.out.println("File is not exist...");
				}
				
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		public static void main(String[] args) {
			
			ReadApplicationDetails s = new ReadApplicationDetails();
			
			
		}
		




















	
}
