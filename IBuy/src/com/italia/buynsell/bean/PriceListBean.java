package com.italia.buynsell.bean;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

import com.italia.buynsell.controller.CornConditions;
import com.italia.buynsell.controller.CornPrice;
import com.italia.buynsell.controller.Corns;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.reports.ReportCompiler;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Named("priceBean")
@ViewScoped
public class PriceListBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 65486584541L;
	private List<CornPrice> cornPrices = new ArrayList<CornPrice>();
	private String volume;
	private String cornName;
	private String cornColor;
	private String variant;
	private String condition;
	private String price;
	private List colors = new ArrayList<>();
	private String colorLabel;
	private List variants = new ArrayList<>();
	private String variantLabel;
	private List conditions = new ArrayList<>();
	private String conditionLabel;
	private List<CornPrice> nativeOnly = new ArrayList<CornPrice>();
	private Map<Long, CornPrice> data = Collections.synchronizedMap(new HashMap<Long, CornPrice>());
	private List<CornPrice> whitewet = new ArrayList<CornPrice>();
	private List<CornPrice> whitebilog = new ArrayList<CornPrice>();
	private List<CornPrice> whiteslightwet = new ArrayList<CornPrice>();
	private List<CornPrice> yellowdry = new ArrayList<CornPrice>();
	private List<CornPrice> yellowwet = new ArrayList<CornPrice>();
	private List<CornPrice> yellowbilog = new ArrayList<CornPrice>();
	private List<CornPrice> yellowslightwet = new ArrayList<CornPrice>();
	private Long priceId;
	private Long cornId;
	
	private final String sep = File.separator;
	private final String REPORT_PATH = "C:" + sep + "BuyNSell" + sep + "reports" + sep + "generated" + sep;
	private final String REPORT_NAME = "priceList";
	private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
	
	public Long getPriceId() {
		return priceId;
	}

	public void setPriceId(Long priceId) {
		this.priceId = priceId;
	}

	public Long getCornId() {
		return cornId;
	}

	public void setCornId(Long cornId) {
		this.cornId = cornId;
	}	
	public List<CornPrice> getYellowslightwet() {
		
		yellowslightwet = new ArrayList<CornPrice>();
		if(data!=null){	
			for(CornPrice p : data.values()){
				if(CornConditions.YELLOW_SEMI_BASA.getCode().equalsIgnoreCase(p.getConditions())){
					yellowslightwet.add(p);
					//System.out.println("Volume: " + p.getVolume());
				}
			}
		}
		
		return yellowslightwet;
	}

	public void setYellowslightwet(List<CornPrice> yellowslightwet) {
		this.yellowslightwet = yellowslightwet;
	}

	public List<CornPrice> getYellowbilog() {
		
		yellowbilog = new ArrayList<CornPrice>();
		if(data!=null){	
			for(CornPrice p : data.values()){
				if(CornConditions.YELLOW_BILOG.getCode().equalsIgnoreCase(p.getConditions())){
					yellowbilog.add(p);
					//System.out.println("Volume: " + p.getVolume());
				}
			}
		}
		
		
		return yellowbilog;
	}

	public void setYellowbilog(List<CornPrice> yellowbilog) {
		this.yellowbilog = yellowbilog;
	}

	public List<CornPrice> getYellowwet() {
		
		yellowwet = new ArrayList<CornPrice>();
		if(data!=null){	
			for(CornPrice p : data.values()){
				if(CornConditions.YELLOW_BASA.getCode().equalsIgnoreCase(p.getConditions())){
					yellowwet.add(p);
					//System.out.println("Volume: " + p.getVolume());
				}
			}
		}
		
		return yellowwet;
	}

	public void setYellowwet(List<CornPrice> yellowwet) {
		this.yellowwet = yellowwet;
	}

	public List<CornPrice> getYellowdry() {
		
		yellowdry = new ArrayList<CornPrice>();
		if(data!=null){	
			for(CornPrice p : data.values()){
				if(CornConditions.YELLOW_DRY.getCode().equalsIgnoreCase(p.getConditions())){
					yellowdry.add(p);
					//System.out.println("Volume: " + p.getVolume());
				}
			}
		}
		
		return yellowdry;
	}

	public void setYellowdry(List<CornPrice> yellowdry) {
		this.yellowdry = yellowdry;
	}

	public List<CornPrice> getWhiteslightwet() {
		
		whiteslightwet = new ArrayList<CornPrice>();
		if(data!=null){	
			for(CornPrice p : data.values()){
				//System.out.println("whiteslightwet : " + CornConditions.WHITE_SEMI_BASA.getCode() +" = "+ p.getConditions());
				if(CornConditions.WHITE_SEMI_BASA.getCode().equalsIgnoreCase(p.getConditions())){
					whiteslightwet.add(p);
					//System.out.println("Volume: " + p.getVolume());
				}
			}
		}
		
		return whiteslightwet;
	}

	public void setWhiteslightwet(List<CornPrice> whiteslightwet) {
		this.whiteslightwet = whiteslightwet;
	}

	public List<CornPrice> getWhitebilog() {
		
		whitebilog = new ArrayList<CornPrice>();
		if(data!=null){	
			for(CornPrice p : data.values()){
				//System.out.println("whitebilog : " + CornConditions.WHITE_BILOG.getCode() +" = "+ p.getConditions());
				if(CornConditions.WHITE_BILOG.getCode().equalsIgnoreCase(p.getConditions())){
					whitebilog.add(p);
					//System.out.println("Volume: " + p.getVolume());
				}
			}
		}
		
		return whitebilog;
	}

	public void setWhitebilog(List<CornPrice> whitebilog) {
		this.whitebilog = whitebilog;
	}

	public List<CornPrice> getWhitewet() {
		
		whitewet = new ArrayList<CornPrice>();
		if(data!=null){	
			for(CornPrice p : data.values()){
				//System.out.println("whitewet : " + CornConditions.WHITE_WET.getCode() +" = "+ p.getConditions());
				if(CornConditions.WHITE_WET.getCode().equalsIgnoreCase(p.getConditions())){
					whitewet.add(p);
					//System.out.println("Volume: " + p.getVolume());
				}
			}
		}
		
		return whitewet;
	}

	public void setWhitewet(List<CornPrice> whitewet) {
		this.whitewet = whitewet;
	}
	
	public List<CornPrice> getNativeOnly() {
	
	nativeOnly = new ArrayList<CornPrice>();	
	if(data!=null){	
		for(CornPrice p : data.values()){
			//System.out.println("Native Only : " + CornConditions.WCD_NATIVE.getCode() +" = "+ p.getConditions());
			if(CornConditions.WCD_NATIVE.getCode().equalsIgnoreCase(p.getConditions())){
				nativeOnly.add(p);
				//System.out.println("Volume: " + p.getVolume());
			}
		}
	}
		return nativeOnly;
	}

	public void setNativeOnly(List<CornPrice> nativeOnly) {
		this.nativeOnly = nativeOnly;
	}

	public Map<Long, CornPrice> getData() {
		return data;
	}

	public void setData(Map<Long, CornPrice> data) {
		this.data = data;
	}

	@PostConstruct
	public void init(){
		loadPrice();
	}
	
	private void loadPrice(){
		
		data = Collections.synchronizedMap(new HashMap<Long, CornPrice>());
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("SELECT *  FROM cornPrice, corns WHERE corns.cornId = cornPrice.priceId ORDER BY cornPrice.price DESC");
		System.out.println("Is exist sql: " + ps.toString());
		rs = ps.executeQuery();
		
		while(rs.next()){
			CornPrice price = new CornPrice();
			price.setName(rs.getString("cornName"));
			price.setPriceId(rs.getLong("priceId"));
			price.setVolume(rs.getString("volume"));
			price.setConditions(rs.getString("conditions"));
			price.setPrice(rs.getString("price"));
			price.setVariant(rs.getString("variant"));
			price.setCornId(rs.getLong("cornId"));
			
			Corns corn = new Corns();
			corn.setCornId(rs.getLong("cornId"));
			corn.setCornName(rs.getString("cornName"));
			corn.setCornType(rs.getString("cornType"));
			corn.setCornColor(rs.getString("cornColor"));
			
			price.setCorns(corn);
			
			data.put(price.getPriceId(), price);
			//System.out.println("Adding to map <"+ price.getPriceId() +", "+ price +">");
		}
		
		//System.out.println("Map size : " + data.size());
		
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	public String deleteRow(CornPrice in){
		
		cornPrices.remove(in);
		data.remove(in.getPriceId());
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement("DELETE FROM cornPrice WHERE priceId=?");
		ps.setLong(1,in.getPriceId());
		ps.executeUpdate();
		ps = conn.prepareStatement("DELETE FROM corns WHERE cornId=?");
		ps.setLong(1,in.getPriceId());
		ps.executeUpdate();
		
		
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "delete";
	}
	
	
	public List getConditions() {
		
		conditions = new ArrayList<>();
		conditions.add(new SelectItem(CornConditions.WCD_NATIVE.getCode(),CornConditions.WCD_NATIVE.getName()));
		conditions.add(new SelectItem(CornConditions.WHITE_WET.getCode(),CornConditions.WHITE_WET.getName()));
		conditions.add(new SelectItem(CornConditions.WHITE_BILOG.getCode(),CornConditions.WHITE_BILOG.getName()));
		conditions.add(new SelectItem(CornConditions.YELLOW_DRY.getCode(),CornConditions.YELLOW_DRY.getName()));
		conditions.add(new SelectItem(CornConditions.YELLOW_BASA.getCode(),CornConditions.YELLOW_BASA.getName()));
		conditions.add(new SelectItem(CornConditions.YELLOW_BILOG.getCode(),CornConditions.YELLOW_BILOG.getName()));
		conditions.add(new SelectItem(CornConditions.WHITE_SEMI_BASA.getCode(),CornConditions.WHITE_SEMI_BASA.getName()));
		conditions.add(new SelectItem(CornConditions.YELLOW_SEMI_BASA.getCode(),CornConditions.YELLOW_SEMI_BASA.getName()));
		
		return conditions;
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

	
	
	public List getVariants() {
		
		variants = new ArrayList<>();
		variants.add(new SelectItem("1", "N/A"));
		variants.add(new SelectItem("1", "SIGE2X"));
		
		return variants;
	}
	public void setVariants(List variants) {
		this.variants = variants;
	}
	public String getVariantLabel() {
		
		if(variantLabel==null){
			variantLabel = "Please select variant...";
		}
		
		return variantLabel;
	}
	public void setVariantLabel(String variantLabel) {
		this.variantLabel = variantLabel;
	}

	
	
	public String save(){
		
		List data = new ArrayList<>();
		setVolume(getVolume().equalsIgnoreCase("")? null : getVolume().isEmpty()? null : getVolume());
		setPrice(getPrice().equalsIgnoreCase("")? null : getPrice().isEmpty()? null : getPrice());
		setCornName(getCornName().equalsIgnoreCase("")? null : getCornName().isEmpty()? null : getCornName());
		data.add(getLatestPriceId());
		data.add(getVolume());
		data.add(getPrice());
		data.add(getVariant());
		data.add(getCondition());
		data.add(getLatestPriceId());
		
		data.add(getLatestPriceId());
		data.add(getCornName());
		data.add(getVariant());
		data.add(getCornColor());
		
		System.out.println("saving.....");
		System.out.println("volume: " + getVolume());
		System.out.println("price: " + getPrice());
		System.out.println("corn Name: " + getCornName());
		
		if(getPriceId()!=null && getCornId()!=null){
			updateData(data);
		}else{
			if(getVolume()!=null && getPrice()!=null && getCornName()!=null){
				insertData(data);
			}
		}
		clearFields();
		loadPrice();
		return "SAVE";
	}
	
	private void clearFields(){
		System.out.println("clearing fields....");
		setPriceId(null);
		setCornId(null);
		setVolume(null);
		setPrice(null);
		setCornName(null);
		setCornColor(null);
		setVariant(null);
		setCondition(null);
	}
	
	private long getLatestPriceId(){
		long id =0;
		Connection conn = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		String sql = "";
		try{
		sql="SELECT priceId FROM cornPrice ORDER BY priceId DESC LIMIT 1";	
		conn = DataConnectDAO.getConnection();
		prep = conn.prepareStatement(sql);	
		rs = prep.executeQuery();
		
		while(rs.next()){
			id = rs.getLong("priceId");
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
		String sql = "INSERT INTO cornPrice ("
				+ "priceId,"
				+ "volume,"
				+ "price,"
				+ "variant,"
				+ "conditions,"
				+ "cornId) " 
				+ "values(?,?,?,?,?,?)";
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		ps.setLong(1, Long.valueOf(data.get(0).toString()));
		ps.setString(2, data.get(1).toString());
		ps.setString(3, data.get(2).toString());
		ps.setString(4, data.get(3).toString());
		ps.setString(5, data.get(4).toString());
		ps.setString(6, data.get(5).toString());
		
		System.out.println("SQL ADD corn price: " + ps.toString());
		ps.execute();
		
		sql = "INSERT INTO corns ("
				+ "cornId,"
				+ "cornName,"
				+ "cornType,"
				+ "cornColor) " 
				+ "values(?,?,?,?)";
		ps = conn.prepareStatement(sql);
		ps.setLong(1, Long.valueOf(data.get(6).toString()));
		ps.setString(2, data.get(7).toString());
		ps.setString(3, data.get(8).toString());
		ps.setString(4, data.get(9).toString());
		
		System.out.println("SQL ADD Corns : " + ps.toString());
		ps.execute();
		
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	private void updateData(List data){
		String sql = "UPDATE cornPrice SET "
				+ "volume=?,"
				+ "price=?,"
				+ "variant=?,"
				+ "conditions=?"
				+ " WHERE priceId=?"; 
		
		PreparedStatement ps = null;
		Connection conn = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		
		ps.setString(1, getVolume());
		ps.setString(2, getPrice());
		ps.setString(3, getVariant());
		ps.setString(4, getCondition());
		ps.setLong(5, getPriceId());
		
		System.out.println("SQL ADD corn price: " + ps.toString());
		ps.executeUpdate();
		
		sql = "UPDATE corns SET"
				+ "cornName=?,"
				+ "cornType=?,"
				+ "cornColor=?" 
				+ " WHERE cornId=?";
		ps = conn.prepareStatement(sql);
		
		ps.setString(1, getCornName());
		ps.setString(2, getVariant());
		ps.setString(3, getCornColor());
		ps.setLong(4, getCornId());
		
		System.out.println("SQL ADD Corns : " + ps.toString());
		ps.executeUpdate();
		
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public List getColors() {
		
		colors = new ArrayList<>();
		colors.add(new SelectItem("1", "Yellow"));
		colors.add(new SelectItem("2", "White"));
		
		return colors;
	}
	public void setColors(List colors) {
		this.colors = colors;
	}
	public String getColorLabel() {
		
		if(colorLabel==null){
			colorLabel = "Please select color...";
		}
		
		return colorLabel;
	}
	public void setColorLabel(String colorLabel) {
		this.colorLabel = colorLabel;
	}
	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getCornName() {
		return cornName;
	}

	public void setCornName(String cornName) {
		this.cornName = cornName;
	}

	public String getCornColor() {
		return cornColor;
	}

	public void setCornColor(String cornColor) {
		this.cornColor = cornColor;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public List<CornPrice> getCornPrices() {
		
		
		return cornPrices;
	}

	public void setCornPrices(List<CornPrice> cornPrices) {
		this.cornPrices = cornPrices;
	}
	
	public String clickItem(CornPrice d){
		setPriceId(d.getPriceId());
		setCornId(d.getCorns().getCornId());
		setVolume(d.getVolume());
		setPrice(d.getPrice());
		setCornName(d.getCorns().getCornName());
		setCornColor(d.getCorns().getCornColor());
		setVariant(d.getVariant());
		setCondition(d.getConditions());
		return "price";
	}
	
	public String printReport(){
		try{
			
		//use for printing
			
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
		return "Print";
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
		
		System.out.println("CheckReport path: " + REPORT_PATH);
		HashMap paramMap = new HashMap();
		ReportCompiler compiler = new ReportCompiler();
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		String reportLocation = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		System.out.println("Check report path: " + reportLocation);
		HashMap params = new HashMap();
		
		int i=1;
		String paramname = "PARAM_";
		for(CornPrice p : nativeOnly){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		i=11;
		for(CornPrice p : whitewet){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		i=21;
		for(CornPrice p : whitebilog){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		i=31;
		for(CornPrice p : whiteslightwet){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		i=41;
		for(CornPrice p : yellowdry){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		i=51;
		for(CornPrice p : yellowwet){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		i=61;
		for(CornPrice p : yellowbilog){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		i=71;
		for(CornPrice p : yellowslightwet){
			params.put(paramname+i, p.getVolume() + " : " + p.getPrice());
			i+=1;
		}
		
		
		
		
		JasperPrint print = compiler.report(reportLocation, params);
		File pdf = null;
		try{
		pdf = new File(REPORT_PATH+REPORT_NAME+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		System.out.println("pdf successfully created...");
		System.out.println("Creating a backup copy....");
		pdf = new File(REPORT_PATH+REPORT_NAME+"_copy"+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		System.out.println("Done creating a backup copy....");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
