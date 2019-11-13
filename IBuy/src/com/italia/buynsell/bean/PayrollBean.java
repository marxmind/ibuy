package com.italia.buynsell.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import com.italia.buynsell.controller.Attendance;
import com.italia.buynsell.controller.EmployeeCA;
import com.italia.buynsell.controller.Employees;
import com.italia.buynsell.utils.Application;
import com.italia.buynsell.utils.Currency;
import com.italia.buynsell.utils.Numbers;

/**
 * 
 * @author Mark Italia
 * @version1.0
 * @since 9/22/2018
 *
 */
@ManagedBean(name="payBean", eager=true)
@ViewScoped
public class PayrollBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 56766765641L;
	
	private List employeeCa = Collections.synchronizedList(new ArrayList<>());
	private int empCaId;
	private Map<Integer, Employees> employeeData = Collections.synchronizedMap(new HashMap<Integer, Employees>());
	
	private String paymentNotes;
	private double payableCA;
	
	private List<Attendance> attendanceData = Collections.synchronizedList(new ArrayList<>());
	private List<EmployeeCA> employeCAData = Collections.synchronizedList(new ArrayList<>());
	
	public void clearAll() {
		setPaymentNotes("Please select name of the employee first");
		attendanceData = Collections.synchronizedList(new ArrayList<>());
		employeCAData = Collections.synchronizedList(new ArrayList<>());
		setPayableCA(0);
		setEmpCaId(0);
	}
	
	public void loadCAEmployees() {
		
		clearAll();
		
		employeeData = Collections.synchronizedMap(new HashMap<Integer, Employees>());
		employeeCa = Collections.synchronizedList(new ArrayList<>());
		for(Employees em : Employees.retrieve("", new String[0])) {
			employeeCa.add(new SelectItem(em.getId(), em.getFullName()));
			employeeData.put(em.getId(), em);
		}
	}
	
	public void calculatePayroll() {
		
		attendanceData = Collections.synchronizedList(new ArrayList<>());
		employeCAData = Collections.synchronizedList(new ArrayList<>());
		
		if(employeeData!=null && employeeData.containsKey(getEmpCaId())) {
			String note = "<h3><strong>"+greetings()+"</strong></h3><br/>";
			
			note +="<h3><strong>Name: "+getEmployeeData().get(getEmpCaId()).getFullName().toUpperCase()+"</strong></h3>";
			note +="<h3><strong>Salary Rate: Php"+Currency.formatAmount(getEmployeeData().get(getEmpCaId()).getDailySalary())+"</strong></h3><br/>";
			
			note += "<p>Please see details for " + getEmployeeData().get(getEmpCaId()).getFullName() +"'s DTR(Daily Timesheet) and Cash Advances</p><br/>";
			note +="<p><strong>Timesheet</strong></p>";
			String sql = " AND att.attisactive=1 AND att.ispaid=0 AND em.emid=" + getEmpCaId();
			String[] params = new String[0];
			double totalCa = 0d;
			double totalPayable=0d;
			double totalHour=0d;
			List<Attendance> atts = Attendance.retrieve(sql, params);
			boolean hasDTR=true;
			boolean hasCA= true;
			if(getEmployeeData().get(getEmpCaId()).getDailySalary()!=0) {
			
				if(atts!=null && atts.size()>0) {
					note += "<p>I have collected ("+ atts.size()+") attendance.</p>";
					note +="<ul>";
					for(Attendance att : atts) {
						note += "<li><strong>"+att.getDateWork() + "</strong> total hours is <strong>" + att.getTotalTime() + "</strong> and the calculated payable amount is <strong>Php" + Currency.formatAmount(att.getTotalAmount())+"</strong></li>";
						totalPayable += att.getTotalAmount();
						attendanceData.add(att);
						totalHour +=att.getTotalTime();
					}
					note +="</ul>";
					note +="<br/>";
					note +="<p><strong>Total Hours:"+Numbers.formatDouble(totalHour)+"</strong></p>";
					note +="<p><strong>Total Payable Php:"+Currency.formatAmount(totalPayable)+"</strong></p>";
				}else {
					note += "<p>No Attendance recorded or you have not yet downloaded the attendance/s " + processBy()+".</p>";
					hasDTR = false;
				}
			
			}else {
				String[] noSaly = {"Ouch!, It seems that I can't find the daily rate record of " + getEmployeeData().get(getEmpCaId()).getFullName(),
						processBy()+", I think you need to check the record of this employee. I can't locate the salary rate.",
						"Something went wrong " + processBy() + ". I cant retrieve the salary rate. And I'm afraid to continue calculating the timesheet."};
				
				note += noSaly[(int) (Math.random() * noSaly.length)];
				hasDTR = false;
			}
			
			note +="<br/>";
			note +="<p><strong>Cash Advance</strong></p>";
			
			sql = " AND ca.caisactive=1 AND ca.capayable!=0 AND em.emid=" + getEmpCaId();
			params = new String[0];
			List<EmployeeCA> cas = EmployeeCA.retrieve(sql, params);
			if(cas!=null && cas.size()>0) {
				note += "<p>I have retrieve ("+ cas.size()+") Cash advance/s.</p>";
				note +="<ul>";
				for(EmployeeCA ca : cas) {
					note += "<li><strong>"+ca.getCashDate() + "</strong> amounting to <strong>Php" + Currency.formatAmount(ca.getPayable()) +"<strong></li>";
					totalCa += ca.getPayable();
					employeCAData.add(ca);
				}
				note +="</ul>";
			}else {
				note += "<p>No Cash Advances had been recorded or you have not yet recorded " + processBy() + " the CA of " + getEmployeeData().get(getEmpCaId()).getFullName() + "</p>";
				hasCA = false;
			}
			
			if(hasDTR) {
				note +="<br/>";
				note +="<p>Here are the total payable to <strong>" + getEmployeeData().get(getEmpCaId()).getFullName() + "</strong></p>";
				note +="<p><strong>Gross: Php"+Currency.formatAmount(totalPayable)+"</strong></p>";
				if(cas!=null & cas.size()>0) {
					if(getPayableCA()>0 && getPayableCA()<totalCa) {
						double remainingCA = 0d;
						remainingCA = totalCa - getPayableCA();
						note += "<p>Since you have provided <strong>Php"+Currency.formatAmount(getPayableCA())+"</strong> the only CA to be paid by <strong>" + getEmployeeData().get(getEmpCaId()).getFullName()+"</strong>. This person has a remaining balance of <strong>Php" + Currency.formatAmount(remainingCA)+"</strong></p>";
						totalCa = getPayableCA();
					}else {
						note +="<p>I have deducted the Cash advance amounting to Php "+Currency.formatAmount(totalCa)+" </p>";
						setPayableCA(totalCa);
					}
				}
				totalPayable -= totalCa;
				note +="<p><strong>Net: Php"+Currency.formatAmount(totalPayable)+"</strong></p>";
				
				note +="<br/>";
				note += "<p><strong>"+processBy()+"</strong>, If you are ok with the above information please click process it means you are agreed to pay the salary calculated for <strong>"+getEmployeeData().get(getEmpCaId()).getFullName()+"</strong></p>";
			}else {
				if(hasCA) {
					note += "</br><p>" + processBy() + ", you can collect only CA of this employee which is <strong>Php"+Currency.formatAmount(totalCa)+"</strong></p>";
				}
			}
			
			setPaymentNotes(note);
		}else {
			setPaymentNotes(processBy()+", No payroll to be calculated. Please select name of the employee in order for you to continue.");
		}
	}
	
	public void processPayroll() {
		if(getEmployeeData()!=null) {
			
			if(getAttendanceData()!=null && getAttendanceData().size()>0) {
				for(Attendance att : getAttendanceData()) {
					att.setIsPaid(1);
					att.save();
				}
			}
			
			double payableCa = getPayableCA();
			
			if(getEmployeCAData()!=null && getEmployeCAData().size()>0) {
			for(EmployeeCA ca : getEmployeCAData()) {
				double cashOut = ca.getAmount();
				if(payableCa>cashOut) {
					payableCa -= cashOut;
					ca.setPayable(0);
					ca.save();
				}else if(payableCa>0 && payableCa<cashOut){
					ca.setPayable(payableCa);
					payableCa = 0;
					ca.save();
				}else if(payableCa==cashOut){
					ca.setPayable(0);
					payableCa = 0;
					ca.save();
				}
			}		
				
			}
			calculatePayroll();
			Application.addMessage(1, "Success", "Payroll has been successfully processed.");
		}
	}
	
	public String greetings() {
		String[] gt = {"Hi" +" " + processBy(),"Good day"+" " + processBy(), processBy() +", "+"you are awesome!", "Nice day"+ " " + processBy(), "Hey, "+processBy()+", you look cool today."};
		
		return gt[(int) (Math.random() * gt.length)];
	}
	
	private static String processBy(){
		String proc_by = "error";
		try{
			HttpSession session = SessionBean.getSession();
			proc_by = session.getAttribute("username").toString();
		}catch(Exception e){}
		return proc_by;
	}
	
	public List getEmployeeCa() {
		return employeeCa;
	}
	public void setEmployeeCa(List employeeCa) {
		this.employeeCa = employeeCa;
	}
	public int getEmpCaId() {
		return empCaId;
	}
	public void setEmpCaId(int empCaId) {
		this.empCaId = empCaId;
	}

	public String getPaymentNotes() {
		return paymentNotes;
	}

	public void setPaymentNotes(String paymentNotes) {
		this.paymentNotes = paymentNotes;
	}

	public Map<Integer, Employees> getEmployeeData() {
		return employeeData;
	}

	public void setEmployeeData(Map<Integer, Employees> employeeData) {
		this.employeeData = employeeData;
	}

	public double getPayableCA() {
		return payableCA;
	}

	public void setPayableCA(double payableCA) {
		this.payableCA = payableCA;
	}

	public List<Attendance> getAttendanceData() {
		return attendanceData;
	}

	public void setAttendanceData(List<Attendance> attendanceData) {
		this.attendanceData = attendanceData;
	}

	public List<EmployeeCA> getEmployeCAData() {
		return employeCAData;
	}

	public void setEmployeCAData(List<EmployeeCA> employeCAData) {
		this.employeCAData = employeCAData;
	}

}
