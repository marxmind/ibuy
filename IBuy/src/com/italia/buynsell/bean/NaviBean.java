package com.italia.buynsell.bean;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("naviBean")
@ViewScoped
public class NaviBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4365457671L;
	public String home(){
		
		HomeBean bean = new HomeBean();
		bean.init();
		
		return "main";
	}
	public String cashIn(){
		return "cashin";
	}
	public String whiteCorn(){
		return "whitecorn";
	}
	public String yellowCorn(){
		return "yellowcorn";
	}
	public String purchased(){
		return "purchased";
	}
	public String expenses(){
		return "expenses";
	}
	public String credit(){
		return "credit";
	}
	public String debit(){
		return "debit";
	}
	public String priceList(){
		return "pricelist";
	}
	public String eitrans(){
		return "eitrans";
	}
	public String clientprofile(){
		return "client";
	}
	
}
