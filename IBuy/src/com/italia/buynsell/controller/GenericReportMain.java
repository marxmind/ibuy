package com.italia.buynsell.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.reports.ReportCompiler;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class GenericReportMain {
	
	
	public static void compileReport(ArrayList<GenericPrint> print, HashMap params,String REPORT_NAME, String REPORT_PATH){
		try{
		System.out.println("CheckReport path: " + REPORT_PATH);
		//HashMap paramMap = new HashMap();
		
		ReportCompiler compiler = new ReportCompiler();
		String jrxmlFile = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		System.out.println("REPORT_NAME: " +REPORT_NAME + " REPORT_PATH: " + REPORT_PATH);
		//String reportLocation = compiler.compileReport(REPORT_NAME, REPORT_NAME, REPORT_PATH);
		//System.out.println("Check report path: " + reportLocation);
		
		
		/*ArrayList<GenericPrint> rpts = new ArrayList<GenericPrint>();
		for(GenericPrint r : print){
  			rpts.add(r);
		}*/	
		JRBeanCollectionDataSource beanColl = new JRBeanCollectionDataSource(print);
		
		
		//params.put("PARAM_CASHIN",getTodaysFund());
		
		
		
		DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
		Date date = new Date();
		params.put("PARAM_DATE","As of "+dateFormat.format(date));
		
		dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm: a");
		date = new Date();
		params.put("PARAM_PRINTDATE","PRINTED DATE: "+dateFormat.format(date));
		HttpSession session = SessionBean.getSession();
		String proc_by = session.getAttribute("username").toString();
		if(proc_by!=null){
			params.put("PARAM_PROCCESSEDBY","PROCCESSED BY: "+proc_by);
		}else{
			params.put("PARAM_PROCCESSEDBY","");
		}
		
		String jrprint = JasperFillManager.fillReportToFile(jrxmlFile, params, beanColl);
		JasperExportManager.exportReportToPdfFile(jrprint,REPORT_PATH+ REPORT_NAME +".pdf");
		
		
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	
}
