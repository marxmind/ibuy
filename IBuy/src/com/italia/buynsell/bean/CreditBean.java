package com.italia.buynsell.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
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
import com.italia.buynsell.controller.Credit;
import com.italia.buynsell.controller.GenericPrint;
import com.italia.buynsell.controller.GenericReportMain;
import com.italia.buynsell.controller.ReadApplicationDetails;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.Application;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;

@Named("creditBean")
@ViewScoped
public class CreditBean implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 35456769431L;
		private String description;
		private String amount;
		private String datein;
		private String addedBy;
		private String userNotification;
		private Long id;
		private String totalCashIn;
		private List<Credit> data = new ArrayList<>();
		private Map<Long, Credit> creditData = Collections.synchronizedMap(new HashMap<Long, Credit>());
		private Credit credit;
		
		private String searchParam;
		private Date calendarFrom;
		private Date calendarTo;
		
		public void loadSearch(){
			loadAddedCash("search");
		}
		
		public String getTotalCashIn() {
			return totalCashIn;
		}
		public void setTotalCashIn(String totalCashIn) {
			this.totalCashIn = totalCashIn;
		}
		
		
		public List<Credit> getData() {
			Map<Long, Credit> cred = new TreeMap(Collections.reverseOrder());
			cred.putAll(creditData);
			data = new ArrayList<>();
			int i=1;
			for(Credit in :  cred.values()){ //creditData.values()){
				in.setCnt(i++);
				data.add(in);
			}
			
			return data;
		}
		public void setData(List<Credit> data) {
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
				loadAll(search);
			}else{
				loadAddedCash("init");
			}
		}
		
		private void loadAll(String search){
			
			String sql = "SELECT *  FROM credit WHERE description like '%" + search.replace("--", "") + "%'";
			String[] params = new String[0]; 
			creditData = Collections.synchronizedMap(new HashMap<Long, Credit>());
			Double amnt = 0.0;
			Double cnt = 0.0;
			for(Credit in : Credit.retrieveCredit(sql, params)){
				try{amnt = Double.valueOf(in.getAmountIn().replace(",", ""));}catch(Exception e){amnt = 0.0;}
				creditData.put(in.getId(), in);
				cnt +=amnt;
			}
			
			setTotalCashIn(formatAmount(cnt+""));
		}
		
		private void loadAddedCash(String type){
			creditData = Collections.synchronizedMap(new HashMap<Long, Credit>());
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection conn = null;
			setCurrentDate(getCurrentDate()==null? null : getCurrentDate().equalsIgnoreCase("")? null : getCurrentDate().isEmpty()? null : getCurrentDate());
			setDescription(getDescription()==null? null : getDescription().equalsIgnoreCase("")? null : getDescription().isEmpty()? null : getDescription());
			setAmount(getAmount()==null? null : getAmount().equalsIgnoreCase("")? null : getAmount().isEmpty()? null : getAmount());
			try{
			conn = DataConnectDAO.getConnection();
			if("init".equalsIgnoreCase(type)){
				ps = conn.prepareStatement("SELECT *  FROM credit WHERE dateIn=? ORDER BY timestamp ASC");
				ps.setString(1, getCurrentDate());
			}else if("search".equalsIgnoreCase(type)){
				
				int cnt=1;
				String sql="SELECT *  FROM credit WHERE ";
				
				sql = sql + " (dateIn>=? AND dateIn<=?) ";
				if(getSearchParam()!=null){
					sql += " AND description like '%" + getSearchParam() + "%'";
				}
				ps = conn.prepareStatement(sql);
				ps.setString(cnt++, DateUtils.convertDate(getCalendarFrom(), "yyyy-MM-dd"));
				ps.setString(cnt++, DateUtils.convertDate(getCalendarTo(), "yyyy-MM-dd"));
				
				/*int cnt=1;
				String sql="SELECT *  FROM credit WHERE ";
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
				}
				
				if(getDescription()==null && getAmount()==null && getCurrentDate()==null){
					ps = conn.prepareStatement("SELECT *  FROM credit WHERE dateIn=? ORDER BY timestamp ASC");
					ps.setString(1, getCurrentDate());
				}else{
					
					ps = conn.prepareStatement(sql +" ORDER BY timestamp ASC");
					
					if(getAmount()!=null){
						ps.setString(cnt, getAmount());
						cnt+=1;
					}
					if(getCurrentDate()!=null){
						if(getCurrentDate().contains(":")){
							int cntSplit = getCurrentDate().indexOf(":");
							ps.setString(cnt, getCurrentDate().split(":", cntSplit)[0]);
							ps.setString(cnt+1, getCurrentDate().split(":", cntSplit)[1]);
						}else{
							ps.setString(cnt, getCurrentDate());
						}
						
					}
					
					
					
				}*/
				
			}
			
			
			
			System.out.println("Is exist sql: " + ps.toString());
			rs = ps.executeQuery();
			data = new ArrayList<>();
			Double amnt = 0.0;
			Double cnt = 0.0;
			long i=1;
			while(rs.next()){
				Credit in = new Credit();
				in.setId(rs.getLong("creditId"));
				in.setDescription(rs.getString("description"));
				try{amnt = Double.valueOf(rs.getString("amountIn").replace(",", ""));}catch(Exception e){amnt = 0.0;}
				in.setAmountIn(rs.getString("amountIn"));
				in.setDateIn(rs.getString("dateIn"));
				in.setAddedBy(rs.getString("processedBy"));
				//data.add(in);
				creditData.put(in.getId(), in);
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
		
		
		
		
		private String formatAmount(String amount){
			try{
			double money = Double.valueOf(amount);
			NumberFormat format = NumberFormat.getCurrencyInstance();
			amount = format.format(money).replace("$", "");
			amount = amount.replace("Php", "");
			amount = amount.replace("₱", "");
			}catch(Exception e){
				
			}
			return amount;
		}
		
		public void deleteRow(Credit in){
			data.remove(in);
			
			
			String sql = "DELETE FROM credit WHERE creditId=?";
			String[] params = new String[1];
			params[0] = in.getId()+"";
			Credit.delete(sql, params);
			
			/*PreparedStatement ps = null;
			Connection conn = null;
			try{
			conn = DataConnectDAO.getConnection();
			ps = conn.prepareStatement("DELETE FROM credit WHERE creditId=?");
			ps.setLong(1,in.getId());
			System.out.println("Is exist sql: " + ps.toString());
			ps.executeUpdate();
			ps.close();
			DataConnectDAO.close(conn);
			}catch(Exception e){
				e.printStackTrace();
			}*/
			loadAddedCash("init");
			Application.addMessage(2, "Success", "Successfully deleted");
		}
		
		private String processBy(){
			try{
				HttpSession session = SessionBean.getSession();
				String proc_by = session.getAttribute("username").toString();
				return proc_by;
				}catch(Exception e){
					return "error";
				}
		}
		public void save(){
			setCurrentDate(getCurrentDate()==null? null : getCurrentDate().equalsIgnoreCase("")? null : getCurrentDate().isEmpty()? null : getCurrentDate());
			setDescription(getDescription()==null? null : getDescription().equalsIgnoreCase("")? null : getDescription().isEmpty()? null : getDescription());
			setAmount(getAmount()==null? null : getAmount().equalsIgnoreCase("")? null : getAmount().isEmpty()? null : getAmount());
			/*System.out.println("id: " + getLatestId());
			System.out.println("Description: "+ getDescription());
			String id = getCashInInfo( getId()==null? 0+"" : getId()+"");
			
			List data = new ArrayList();*/
			
			setDatein(getCurrentDate());
			setAddedBy(processBy());
			
			
			String amnt = "";
			amnt = Currency.removeCurrencySymbol(getAmount(), "");
			amnt = Currency.removeComma(amnt);
			setAmount(amnt);
			
			Credit credit = new Credit();
			String receiptNumber = Credit.getLatestCreditId()+"";
			if(getCredit()!=null){
				credit = getCredit();
			}else{
				credit.setDateIn(getDatein());
			}
			credit.setDescription(getDescription());
			credit.setAmountIn(formatAmount(getAmount()));
			credit.setAddedBy(getAddedBy());
			credit.save();
			
			String str = "MATT-AGRI BUY N SELL\n";
			str += "Sitio Lugan, Pob. Lake Sebu, So. Cot.\n";
			str += "Date Purchased: "+ DateUtils.getCurrentDateMMDDYYYYTIME() +" \n";
			str += "Purchased: " + getDescription() +"\n";
			str += "Amount Php: " + Currency.formatAmount(getAmount()) +"\n";
			str += "Cashier: " + processBy();
			str += "\n***THANK YOU***";
			str +=" \n \n \n \n \n \n";
			//str +="1B 70 00 C8 FA";
			saveSaleToFileReceipt(str, receiptNumber);
			cashdrawerOpen();
			printReceipt(receiptNumber);
			cutPaper();
			loadAddedCash("init");
			clearFields();
			Application.addMessage(1, "Success", "Successfully saved");
			
		}
		
		public void printReceipt(String receiptNo){
			
			ReadApplicationDetails rd = new ReadApplicationDetails();
			
			//check log directory
			String receiptLocation = rd.getSalesreceipt();
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
		
		public void clearFields(){
			System.out.println("Clear method called..");
			setCurrentDate(DateUtils.getCurrentDateYYYYMMDD());
			setDescription(null);
			setAmount(null);
			setId(null);
			setCredit(null);
		}
		
		public String clickItem(Credit d){
			setId(d.getId());
			setDescription(d.getDescription());
			setAmount(d.getAmountIn());
			setCurrentDate(d.getDateIn());
			setCredit(d);
			return null;
		}
	
	public String search(){
		loadAddedCash("search");
		return null;
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
				System.out.println("First data");
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
	public void setCurrentDate(String currentDate){
		this.currentDate = currentDate;
	}
	public String getCurrentDate(){//MMMM d, yyyy
		/*DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");//new SimpleDateFormat("MM/dd/yyyy");//new SimpleDateFormat("yyyy/MM/dd hh:mm: a");
		Date date = new Date();
		String _date = dateFormat.format(date);*/
		if(currentDate==null){
			currentDate = DateUtils.getCurrentYYYYMMDD();
		}
		return currentDate;
	}

	private void insertData(List data, String type){
		String sql = "INSERT INTO credit ("
				+ "creditId,"
				+ "description,"
				+ "amountIn,"
				+ "processedBy,"
				+ "dateIn,"
				+ "countAdded) " 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		if("1".equalsIgnoreCase(type)){
			ps.setInt(1, 1);
		}else if("3".equalsIgnoreCase(type)){
			ps.setInt(1, Integer.valueOf(data.get(5).toString()));
		}
		ps.setString(2, data.get(0).toString());
		ps.setString(3, data.get(1).toString());
		ps.setString(4, data.get(2).toString());
		ps.setString(5, data.get(3).toString());
		ps.setString(6, data.get(4).toString());
		
		System.out.println("SQL ADD : " + ps.toString());
		ps.execute();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	private void updateData(List data){
		String sql = "UPDATE credit SET "
				+ "description=?,"
				+ "amountIn=?,"
				+ "processedBy=?"
				+ " WHERE creditId=?";

		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		ps.setString(1, data.get(0).toString());
		ps.setString(2, data.get(1).toString());
		ps.setString(3, data.get(2).toString());
		//ps.setString(4, data.get(3).toString());
		ps.setLong(4, getId());
		System.out.println("SQL UPDATE : " + ps.toString());
		ps.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}

		private long getLatestId(){
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
			ps = conn.prepareStatement("SELECT creditId FROM credit WHERE creditId=?");
			ps.setString(1, id);
			System.out.println("Is exist sql: " + ps.toString());
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
			ps = conn.prepareStatement("SELECT countAdded  FROM credit WHERE dateIn=? ORDER BY countAdded DESC LIMIT 1");
			ps.setString(1, getCurrentDate());
			System.out.println("Is exist sql: " + ps.toString());
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
		private final String sep = File.separator;
		private final String REPORT_PATH = "C:" + sep + "BuyNSell" + sep + "reports" + sep + "generated" + sep;
		private final String REPORT_NAME ="GenericPrint";
		private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
		public String printReport(){
			try{
				//compile the report
				ArrayList<GenericPrint> print = new ArrayList<>();
				
				for(Credit d : creditData.values()){
					GenericPrint p = new GenericPrint();
					p.setFld1(d.getCnt()+"");
					p.setFld2(d.getDescription());
					p.setFld3(d.getAmountIn());
					p.setFld4(d.getDateIn());
					//p.setFld5(d.getDatepaying());
					//p.setFld6(d.getIsPaid());
					p.setFld5(d.getAddedBy());
					print.add(p);
				}
				HashMap params = new HashMap<>();
				params.put("PARAM_SUBTITLE", "CREDIT REPORT");
				params.put("PARAM_TOTAL", "Total Credit: Php" +getTotalCashIn());
				params.put("PARAM_1", "NO");
				params.put("PARAM_2", "DESCRIPTION");
				params.put("PARAM_3", "AMOUNT");
				params.put("PARAM_4", "DATE PAID");
				//params.put("PARAM_5", "PAYMENT DATE");
				//params.put("PARAM_6", "IS PAID");
				params.put("PARAM_5", "ADDED BY");
				
				
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
		public Credit getCredit() {
			return credit;
		}
		public void setCredit(Credit credit) {
			this.credit = credit;
		}
		
		public static void saveSaleToFileReceipt(String receiptInfo, String receiptNumber){
			
			ReadApplicationDetails rd = new ReadApplicationDetails();
			
			//check log directory
			String receiptLocation = rd.getSalesreceipt();
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
		public String getSearchParam() {
			return searchParam;
		}
		public void setSearchParam(String searchParam) {
			this.searchParam = searchParam;
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




















