package com.italia.buynsell.reports;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class ReportCompiler {

	public String compileReport(String rptFileJrxml, String rptFileJasper, String rptLocation){
		String jasperFile="";
		System.out.println("Compiling " + rptFileJrxml + " file.....");
		try{
		JasperCompileManager.compileReportToFile(rptLocation + rptFileJrxml + ".jrxml", rptLocation + rptFileJasper + ".jasper");
		System.out.println(rptFileJrxml +" is successfully compiled...");
		jasperFile = rptLocation + rptFileJasper + ".jasper";
		}catch(JRException jre){
			jre.getMessage();
		}
		return jasperFile;
	}
	public JasperPrint report(String reportLocation, HashMap params){
		JasperPrint jasperPrint = null;
		try{
		jasperPrint = JasperFillManager.fillReport (reportLocation, params, new JREmptyDataSource());
		}catch(JRException jre){
			System.out.println("JasperPrint report()");
			jre.getMessage();
		}
		return jasperPrint;
	}
	public JasperPrint report(String jasperReport, HashMap params,JRBeanCollectionDataSource jrBeanColl){
		JasperPrint jasperPrint = null;
		try{
		jasperPrint = JasperFillManager.fillReport(jasperReport, params,jrBeanColl);
		System.out.println("JasperPrint report()");
		}catch(Exception jre){
			
			jre.getMessage();
		}
		return jasperPrint;
	}
	
	public static void main(String[] args) {
		String[] values = new String[]{"123412.01","ONE HUNDRED TWENTY THREE THOUSAND FOUR HUNDRED TWELVE AND ONE CENTS ONLY"};
		Path PATH_APP = Paths.get("");
		String sep = File.separator;
		String REPORT_PATH = "C:" + sep + "reports" + sep;
				//FacesContext.getCurrentInstance().getExternalContext().getRealPath(sep +"resources"+sep+"reports")+sep;
		String REPORT_NAME = "BankChequeReport";
		
		HashMap paramMap = new HashMap();
		
		ReportCompiler compiler = new ReportCompiler();
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		String reportLocation = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		System.out.println("Check report path: " + reportLocation);
		HashMap params = new HashMap();
		params.put("PARAM_AMOUNT", values[0]);
		params.put("PARAM_AMOUNT_INWORDS", values[1]);
		JasperPrint print = compiler.report(reportLocation, params);
		File pdf = null;
		try{
		pdf = new File(REPORT_PATH+REPORT_NAME+".pdf");
		pdf.createNewFile();
		JasperExportManager.exportReportToPdfStream(print, new FileOutputStream(pdf));
		System.out.println("pdf successfully created...");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
}

