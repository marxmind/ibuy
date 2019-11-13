package com.italia.buynsell.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class Application {

	/**
	 * 
	 * @param severityLevel 1-INFO 2-WARN 3-ERROR 4-FATAL
	 * @param summary
	 * @param detail
	 */
	public static void addMessage(int severityLevel,String summary, String detail) {
		FacesMessage message = null;
		switch(severityLevel){
		case 1:
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 2:
			message = new FacesMessage(FacesMessage.SEVERITY_WARN, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 3:
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;
		case 4:
			message = new FacesMessage(FacesMessage.SEVERITY_FATAL, summary, detail);
			FacesContext.getCurrentInstance().addMessage(null, message);
			break;		
		}
    }
	
}
