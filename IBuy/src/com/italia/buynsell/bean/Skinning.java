package com.italia.buynsell.bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
/**
 * 
 * @author mark italia
 * @since 11/11/2017
 * @version 1.0
 *
 */
@Named("skin")
@ViewScoped
public class Skinning implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 65456894741L;
	

	private String label;
	private String header;
	private String title;
	private String placeholder;
	private String textInput;
	private String table;
	private String panel;
	private String tabs;
	private String button;
	private String link;
	private String dialog;
	private String pagelayout;
	private String copyrightLabel;
	private String calendarSched;
	private String homeDirImgaes;
	private String homeImgaeProfile;
	private String msg;
	private String toolbar;
	private String pageTitle;
	private String menu;
	private String grid;
	private String select;
	private String calendar;
	private String chart;
	private String checkbox;
	private String searchButton;
	private String sideBar;
	private String emailButton;
	private String emailMenu;
	private String sendToLabel;
	private String accordionPanelMain;
	private String calendarDueDate;
	 
	private static final String sep = File.separator;
	private static final String PRIMARY_DRIVE = System.getenv("SystemDrive");
	private static final String PATH = PRIMARY_DRIVE + sep + "BuyNSell" + sep + "conf" + sep;
	private static final String FILE_NAME = "skin.max";
	private static final String PROPERTY_FILE = PATH + FILE_NAME;
	
	public Skinning(){
		
		Properties prop = new Properties();
		try{
			prop.load(new FileInputStream(PROPERTY_FILE));
			
			setLabel(prop.getProperty("label"));
			setHeader(prop.getProperty("header"));
			setTitle(prop.getProperty("title"));
			setPlaceholder(prop.getProperty("placeholder"));
			setTextInput(prop.getProperty("textInput"));
			setTable(prop.getProperty("table"));
			setPanel(prop.getProperty("panel"));
			setTabs(prop.getProperty("tabs"));
			setButton(prop.getProperty("button"));
			setLink(prop.getProperty("link"));
			setDialog(prop.getProperty("dialog"));
			setPagelayout(prop.getProperty("pagelayout"));
			setCopyrightLabel(prop.getProperty("copyrightLabel"));
			setCalendarSched(prop.getProperty("calendarSched"));
			setHomeDirImgaes(prop.getProperty("homeDirImgaes"));
			setHomeImgaeProfile(prop.getProperty("homeImgaeProfile"));
			setMsg(prop.getProperty("msg"));
			setToolbar(prop.getProperty("toolbar"));
			setPageTitle(prop.getProperty("pageTitle"));
			setMenu(prop.getProperty("menu"));
			setGrid(prop.getProperty("grid"));
			setSelect(prop.getProperty("select"));
			setCalendar(prop.getProperty("calendar"));
			setChart(prop.getProperty("chart"));
			setCheckbox(prop.getProperty("checkbox"));
			setSearchButton(prop.getProperty("searchButton"));
			setSideBar(prop.getProperty("sideBar"));
			setEmailButton(prop.getProperty("emailButton"));
			setSendToLabel(prop.getProperty("sendToLabel"));
			setEmailMenu(prop.getProperty("emailMenu"));
			setAccordionPanelMain(prop.getProperty("accordionPanelMain"));
			setCalendarDueDate(prop.getProperty("calendarDueDate"));
			
		}catch(Exception e){e.printStackTrace();}
		
	}
	
	public static void main(String[] args) {
		Skinning skin = new Skinning();
		System.out.println(skin.getLabel());
	}
	
	@PostConstruct
	public void init(){
		new Skinning();
	}
	
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}
	public String getTextInput() {
		return textInput;
	}
	public void setTextInput(String textInput) {
		this.textInput = textInput;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getPanel() {
		return panel;
	}
	public void setPanel(String panel) {
		this.panel = panel;
	}
	public String getTabs() {
		return tabs;
	}
	public void setTabs(String tabs) {
		this.tabs = tabs;
	}
	public String getButton() {
		return button;
	}
	public void setButton(String button) {
		this.button = button;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getCopyrightLabel() {
		return copyrightLabel;
	}
	public void setCopyrightLabel(String copyrightLabel) {
		this.copyrightLabel = copyrightLabel;
	}
	public String getDialog() {
		return dialog;
	}
	public void setDialog(String dialog) {
		this.dialog = dialog;
	}
	public String getPagelayout() {
		return pagelayout;
	}
	public void setPagelayout(String pagelayout) {
		this.pagelayout = pagelayout;
	}
	public String getCalendarSched() {
		return calendarSched;
	}
	public void setCalendarSched(String calendarSched) {
		this.calendarSched = calendarSched;
	}
	public String getHomeDirImgaes() {
		return homeDirImgaes;
	}
	public void setHomeDirImgaes(String homeDirImgaes) {
		this.homeDirImgaes = homeDirImgaes;
	}
	public String getHomeImgaeProfile() {
		return homeImgaeProfile;
	}
	public void setHomeImgaeProfile(String homeImgaeProfile) {
		this.homeImgaeProfile = homeImgaeProfile;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getToolbar() {
		return toolbar;
	}
	public void setToolbar(String toolbar) {
		this.toolbar = toolbar;
	}
	public String getPageTitle() {
		return pageTitle;
	}
	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}
	public String getMenu() {
		return menu;
	}
	public void setMenu(String menu) {
		this.menu = menu;
	}
	public String getGrid() {
		return grid;
	}
	public void setGrid(String grid) {
		this.grid = grid;
	}
	public String getSelect() {
		return select;
	}
	public void setSelect(String select) {
		this.select = select;
	}
	public String getCalendar() {
		return calendar;
	}
	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}
	public String getChart() {
		return chart;
	}
	public void setChart(String chart) {
		this.chart = chart;
	}
	public String getCheckbox() {
		return checkbox;
	}
	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public String getSearchButton() {
		return searchButton;
	}

	public void setSearchButton(String searchButton) {
		this.searchButton = searchButton;
	}

	public String getSideBar() {
		return sideBar;
	}

	public void setSideBar(String sideBar) {
		this.sideBar = sideBar;
	}

	public String getEmailButton() {
		return emailButton;
	}

	public void setEmailButton(String emailButton) {
		this.emailButton = emailButton;
	}

	public String getEmailMenu() {
		return emailMenu;
	}

	public void setEmailMenu(String emailMenu) {
		this.emailMenu = emailMenu;
	}

	public String getSendToLabel() {
		return sendToLabel;
	}

	public void setSendToLabel(String sendToLabel) {
		this.sendToLabel = sendToLabel;
	}

	public String getAccordionPanelMain() {
		return accordionPanelMain;
	}

	public void setAccordionPanelMain(String accordionPanelMain) {
		this.accordionPanelMain = accordionPanelMain;
	}

	public String getCalendarDueDate() {
		return calendarDueDate;
	}

	public void setCalendarDueDate(String calendarDueDate) {
		this.calendarDueDate = calendarDueDate;
	}
	

}
