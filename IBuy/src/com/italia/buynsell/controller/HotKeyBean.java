package com.italia.buynsell.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
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
import javax.print.attribute.standard.PrinterName;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.bean.SessionBean;
import com.italia.buynsell.dao.LoginDAO;
import com.italia.buynsell.utils.Application;

/**
 * 
 * @author Mark Italia
 * @version 1.0
 * @since 12/31/2018
 *
 */

@ManagedBean(name="hotkeyBean", eager=true)
@ViewScoped
public class HotKeyBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 777756656551L;
	
	
	private String uname;
	private String secret;
	
	public void validateUserNamePassword(){
		boolean valid = LoginDAO.validate(getUname(), getSecret());
		System.out.println("Valid: " + valid);
		if(valid){
			HttpSession session = SessionBean.getSession();
			session.setAttribute("username", getUname());
			
			System.out.println("Opening drawer");
			cashdrawerOpen();
			
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(
							FacesMessage.SEVERITY_WARN, 
							"Incorrect username and password", 
							"Please enter correct username and password"
							)
					);
			System.out.println("Wrong credentials");
			Application.addMessage(3, "Invalid Credentials", "Please provide correct credentials");
		}
		
	}
	
	public void cashdrawerOpen() {
		System.out.println("Opening cash drawer");
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
		PrintService pservice = printservice[0];
		DocPrintJob job = pservice.createPrintJob();
		DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		Doc doc = new SimpleDoc(open,flavor,null);
		PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
		try {
		job.print(doc, aset);
		} catch (PrintException ex) {
		System.out.println(ex.getMessage());
		}
	}
	
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
}
