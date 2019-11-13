package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.DateUtils;

public class ClientDocs {

	private Long documentId;
	private String documentDate;
	private String documentName;
	private String documentPath;
	private String description;
	private String addedBy;
	private ClientProfile clientProfile;
	private Employee employee;
	
	
	public ClientDocs(){}
	
	public ClientDocs( Long documentId,
			 String documentDate,
			 String documentName,
			 String documentPath,
			 String description,
			 String addedBy,
			 ClientProfile clientProfile,
			 Employee employee){
		
		this.documentId = documentId;
		this.documentDate = documentDate;
		this.documentName = documentName;
		this.documentPath = documentPath;
		this.description = description;
		this.addedBy = addedBy;
		this.clientProfile = clientProfile;
		this.employee = employee;
	}
	
	public static List<ClientDocs> retrieveClientDocs(String sql, String[] params){
		List<ClientDocs> docList = new ArrayList<>();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ClientDocs doc = new ClientDocs();
			
			try{doc.setDocumentId(rs.getLong("docId"));}catch(Exception e){}
			try{doc.setDocumentDate(rs.getString("docDate"));}catch(Exception e){}
			try{doc.setDocumentName(rs.getString("docName"));}catch(Exception e){}
			try{doc.setDocumentPath(rs.getString("docPath"));}catch(Exception e){}
			try{doc.setDescription(rs.getString("description"));}catch(Exception e){}
			try{doc.setAddedBy(rs.getString("addedBy"));}catch(Exception e){}
			try{ClientProfile clientProfile = new ClientProfile();
			clientProfile.setClientId(rs.getLong("clientId"));
			doc.setClientProfile(clientProfile);}catch(Exception e){}
			try{Employee employee = new Employee();
			employee.setEmpId(rs.getLong("empId"));
			doc.setEmployee(employee);}catch(Exception e){}
			docList.add(doc);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException sl){}
		
		return docList;
	}
	
	public static void save(ClientDocs doc){
		if(doc!=null){
			
			long id = ClientDocs.getClientDocsInfo(doc.getDocumentId()==null? ClientDocs.getLatestClientDocsId()+1 : doc.getDocumentId());
			
			if(id==1){
				ClientDocs.insertData(doc, "1");
			}else if(id==2){
				ClientDocs.updateData(doc);
			}else if(id==3){
				ClientDocs.insertData(doc, "3");
			}
			
		}
	}
	
	public void save(){
		
			
			long id = getClientDocsInfo(getDocumentId()==null? getLatestClientDocsId()+1 : getDocumentId());
			
			if(id==1){
				insertData("1");
			}else if(id==2){
				updateData();
			}else if(id==3){
				insertData("3");
			}
			
		
	}
	
	public static ClientDocs insertData(ClientDocs doc, String type){
		String sql = "INSERT INTO client_documents ("
				+ "docId,"
				+ "docDate,"
				+ "docName,"
				+ "docPath,"
				+ "description,"
				+ "addedBy,"
				+ "clientId,"
				+ "empId)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			doc.setDocumentId(Long.valueOf(id));
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestClientDocsId()+1;
			ps.setLong(1, id);
			doc.setDocumentId(Long.valueOf(id));
		}
		
		ps.setString(2, doc.getDocumentDate());
		ps.setString(3, doc.getDocumentName());
		ps.setString(4, doc.getDocumentPath());
		ps.setString(5, doc.getDescription());
		ps.setString(6, doc.getAddedBy());
		if(doc.getClientProfile()==null){
			ps.setString(7, null);
		}else{
			ps.setLong(7, doc.getClientProfile().getClientId());
		}
		if(doc.getEmployee()==null){
			ps.setString(8, null);
		}else{
			ps.setLong(8, doc.getEmployee().getEmpId());
		}
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
		return doc;
	}
	
	public void insertData(String type){
		String sql = "INSERT INTO client_documents ("
				+ "docId,"
				+ "docDate,"
				+ "docName,"
				+ "docPath,"
				+ "description,"
				+ "addedBy,"
				+ "clientId,"
				+ "empId)" 
				+ "values(?,?,?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		long id =1;
		if("1".equalsIgnoreCase(type)){
			ps.setLong(1, id);
			setDocumentId(Long.valueOf(id));
		}else if("3".equalsIgnoreCase(type)){
			id=getLatestClientDocsId()+1;
			ps.setLong(1, id);
			setDocumentId(Long.valueOf(id));
		}
		
		ps.setString(2, getDocumentDate());
		ps.setString(3, getDocumentName());
		ps.setString(4, getDocumentPath());
		ps.setString(5, getDescription());
		ps.setString(6, getAddedBy());
		if(getClientProfile()==null){
			ps.setString(7, null);
		}else{
			ps.setLong(7, getClientProfile().getClientId());
		}
		if(getEmployee()==null){
			ps.setString(8, null);
		}else{
			ps.setLong(8, getEmployee().getEmpId());
		}
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
	}
	
	public static ClientDocs updateData(ClientDocs doc){
		String sql = "UPDATE client_documents SET "
				+ "docName=?,"
				+ "docPath=?,"
				+ "description=?,"
				+ "addedBy=?"
				+ " WHERE docId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, doc.getDocumentName());
		ps.setString(2, doc.getDocumentPath());
		ps.setString(3, doc.getDescription());
		ps.setString(4, doc.getAddedBy());
		ps.setLong(5, doc.getDocumentId());
		
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
		}
		
		return doc;
	}
	
	public void updateData(){
		String sql = "UPDATE client_documents SET "
				+ "docName=?,"
				+ "docPath=?,"
				+ "description=?,"
				+ "addedBy=?"
				+ " WHERE docId=?";
		
		PreparedStatement ps = null;
		Connection conn = null;
		
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, getDocumentName());
		ps.setString(2, getDocumentPath());
		ps.setString(3, getDescription());
		ps.setString(4, getAddedBy());
		ps.setLong(5, getDocumentId());
		
		ps.execute();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){
			s.printStackTrace();
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
	
	public static long getLatestClientDocsId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT docId FROM client_documents  ORDER BY docId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("docId");
		}
		
		rs.close();
		prep.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return id;
	}
	public static Long getClientDocsInfo(long id){
		boolean isNotNull=false;
		long result =0;
		//id no data retrieve.
		//application will insert a default no which 1 for the first data
		long val = getLatestClientDocsId();
		if(val==0){
			isNotNull=true;
			result= 1; // add first data
			System.out.println("First data");
		}
		
		//check if empId is existing in table
		if(!isNotNull){
			isNotNull = isIdNoExist(id);
			if(isNotNull){
				result = 2; // update existing data
				System.out.println("update data");
			}else{
				result = 3; // add new data
				System.out.println("add new data");
			}
		}
		
		
		return result;
	}
	public static boolean isIdNoExist(long id){
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		boolean result = false;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT docId FROM client_documents WHERE docId=?");
		ps.setLong(1, id);
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
	
	public static void delete(String sql, String[] params){
		
		Connection conn = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		ps.executeUpdate();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(SQLException s){}
		
	}
	
	public Long getDocumentId() {
		return documentId;
	}
	public String getDocumentDate() {
		return documentDate;
	}
	public String getDocumentName() {
		return documentName;
	}
	public String getDocumentPath() {
		return documentPath;
	}
	public String getDescription() {
		return description;
	}
	public String getAddedBy() {
		return addedBy;
	}
	public ClientProfile getClientProfile() {
		return clientProfile;
	}
	public Employee getEmployee() {
		return employee;
	}
	public void setDocumentId(Long documentId) {
		this.documentId = documentId;
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
	public void setDescription(String description) {
		this.description = description;
	}
	public void setAddedBy(String addedBy) {
		this.addedBy = addedBy;
	}
	public void setClientProfile(ClientProfile clientProfile) {
		this.clientProfile = clientProfile;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public static void main(String[] args) {
		
		ClientDocs doc = new ClientDocs();
		doc.setDocumentId(2l);
		doc.setDocumentDate(DateUtils.getCurrentYYYYMMDD());
		doc.setDocumentName("first");
		doc.setDescription("palit ito na change");
		doc.setDocumentPath("D:\\adasd.pdf");
		doc.setAddedBy("mark");
		doc.save();
		
	}
	
}
