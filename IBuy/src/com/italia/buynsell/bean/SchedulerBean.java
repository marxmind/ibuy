package com.italia.buynsell.bean;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.LazyScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.italia.buynsell.controller.ClientProfile;
import com.italia.buynsell.controller.ClientTransactions;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.DateUtils;

@ManagedBean (name="schedBean", eager=true)
@ViewScoped
public class SchedulerBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 546657894523231L;
	
	
	private ScheduleModel eventModel;
	private ScheduleEvent event = new DefaultScheduleEvent();
	
	@PostConstruct
	public void init() {
		eventModel = new DefaultScheduleModel();
	}
	
	public void loadDueDate() {
		System.out.println("loadDueDate()>>>>>");
		eventModel = new DefaultScheduleModel();
		try {
		String sql = "SELECT * FROM client_trans WHERE duedate>=?";
		String[] params = new String[1];
		params[0] = DateUtils.getCurrentYYYYMMDD();
		List<ScheduleEvent> sched =Collections.synchronizedList(new ArrayList<ScheduleEvent>());
		 for(ClientTransactions trans : ClientTransactions.retrieveClientTransacts(sql, params)){
	        	/*ScheduleEvent eve = new DefaultScheduleEvent();
	        	eve = loadDuedate(trans);
	        	eventModel.addEvent(eve);
	        	eve.setId(trans.getTransId()+"");*/
	        	
			 sched.add(loadDuedate(trans));
			 
	        }
		 
		 eventModel = new LazyScheduleModel() {
     		@Override
     		public void loadEvents(Date start, Date end) {
     			for(ScheduleEvent event : sched) {
     				addEvent(event);
     			}
     			
     		}
     	};
     	
		 
		 
		}catch(Exception e) {}
		
	}
	
	private ScheduleEvent loadDuedate(ClientTransactions trans) {
		ScheduleEvent eve = new DefaultScheduleEvent();
			
		try {
			ClientProfile pro = ClientProfile.retrieveClientProfile("SELECT fullName FROM clientprofile WHERE isactiveclient=1 AND clientId="+trans.getClientProfile().getClientId(), new String[0]).get(0);
			String title = pro.getFullName() + " - " + trans.getDescription()+ " - for the borrowed amount of Php" + Currency.formatAmount(trans.getTransamount());
	    	String startDate = trans.getDueDate();
	    	
	    	String[] date = startDate.split("-");
	    	
	    	startDate = date[2] + "-" + DateUtils.getMonthShortName(Integer.valueOf(date[1]))  +"-"+date[0];
	    	
	    	String endDate = startDate;
	    	
	    	String fromDate = startDate+",08:00:00 AM";
	        String toDate = endDate+",05:00:00 PM";
	        
	    	DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
			Date dateFrom = formatter.parse(fromDate);
	        Date dateTo = formatter.parse(toDate);
	        
	        eve = new DefaultScheduleEvent(title, dateFrom, dateTo); 
	        eve.setId(trans.getTransId()+"");
	        
		}catch(Exception e) {}
	        
		return eve;
	}
	
	 public void onEventSelect(SelectEvent selectEvent) {
	        event = (ScheduleEvent) selectEvent.getObject();
	    }
	
	public ScheduleModel getEventModel() {
		return eventModel;
	}
	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}
	public ScheduleEvent getEvent() {
		return event;
	}
	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
