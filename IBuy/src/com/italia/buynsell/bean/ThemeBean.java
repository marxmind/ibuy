package com.italia.buynsell.bean;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;
/**
 * 
 * @author mark italia
 * @since 04/09/2017
 * @version 1.0
 *
 */
@Named("themeBean")
@ApplicationScoped
public class ThemeBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 147868854437557L;

	public String getApplicationTheme(){
		String theme = "saga";
		try {
		System.out.println("Applying theme...");
		/*ReadApplicationDetails db = new ReadApplicationDetails();
		theme = db.getThemeStyle();*/
		
			HttpSession session = SessionBean.getSession();
			theme = session.getAttribute("theme").toString();
		
		System.out.println("Theme " + theme + " has been applied...");}catch(Exception e){}
		return theme;
	}
	
}
