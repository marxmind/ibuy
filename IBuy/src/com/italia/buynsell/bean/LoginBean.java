package com.italia.buynsell.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.dao.LoginDAO;

@ManagedBean(name="loginBean", eager=true)
@SessionScoped
public class LoginBean implements Serializable{

	private static final long serialVersionUID = 1094801825228386363L;
	
	private String name;
	private String password;
	private String errorMessage;
	private String messages;
	private String keyPress;
	
	private List themes;
	private String idThemes="luna-blue";
	private String ui="6";
	public String getCurrentDate(){//MMMM d, yyyy
		DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
		Date date_ = new Date();
		String _date = dateFormat.format(date_);
		return _date;
	}
	
	public String getMessages() {
		DateFormat dateFormat = new SimpleDateFormat("MM-dd");
		Date date_ = new Date();
		String _date = dateFormat.format(date_);
		
		switch(_date){
		case "04-14" : {messages="HAPPY BIRTHDAY BOSS!!! We wish you good health and more blessing to come. Stay humble and kind to us..."; break;}
		}
		
		
		
		return messages;
	}
	public void setMessages(String messages) {
		this.messages = messages;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	//validate login
	public String validateUserNamePassword(){
		
		System.out.println("UserName: " + name + " Password: " + password);
		boolean valid = LoginDAO.validate(name, password);
		System.out.println("Valid: " + valid);
		String result="login";
		if(valid){
			HttpSession session = SessionBean.getSession();
			session.setAttribute("username", name);
			session.setAttribute("theme",getIdThemes());
			result = "dashboard";
			
			if("6".equalsIgnoreCase(getUi())) {
				result = "main6";
			}
			
		}else{
			FacesContext.getCurrentInstance().addMessage(
					null,new FacesMessage(
							FacesMessage.SEVERITY_WARN, 
							"Incorrect username and password", 
							"Please enter correct username and password"
							)
					);
//			/setErrorMessage("Incorrect username and password.");
			setName("");
			setPassword("");
			result= "login";
		}
		System.out.println(getErrorMessage());
		return result;
	}
	//logout event, invalidate session
	public String logout(){
		System.out.println("sessions has been destroyed....");
		HttpSession session = SessionBean.getSession();
		FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove("loginBean");
		setName("");
		setPassword("");
		session.invalidate();
		return "login";
	}
	
	public String getKeyPress() {
		keyPress = "logId";
		return keyPress;
	}



	public void setKeyPress(String keyPress) {
		this.keyPress = keyPress;
	}

	public List getThemes() {
		
		themes = new ArrayList<>();
		
		themes.add(new SelectItem("luna-amber","LUNA AMBER"));
		themes.add(new SelectItem("luna-blue","LUNA BLUE"));
		themes.add(new SelectItem("luna-green","LUNA GREEN"));
		themes.add(new SelectItem("luna-pink","LUNA PINK"));
		themes.add(new SelectItem("nova-colored","NOVA COLORED"));
		themes.add(new SelectItem("nova-dark","NOVA DARK"));
		themes.add(new SelectItem("nova-light","NOVA LIGHT"));
		themes.add(new SelectItem("omega","OMEGA"));
		
		
		return themes;
	}

	public void setThemes(List themes) {
		this.themes = themes;
	}

	public String getIdThemes() {
		return idThemes;
	}

	public void setIdThemes(String idThemes) {
		this.idThemes = idThemes;
	}

	public String getUi() {
		return ui;
	}

	public void setUi(String ui) {
		this.ui = ui;
	}
	
}
