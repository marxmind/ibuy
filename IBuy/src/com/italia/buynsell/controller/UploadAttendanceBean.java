package com.italia.buynsell.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;

import com.italia.buynsell.utils.Application;
import com.italia.buynsell.utils.DateUtils;
import com.italia.buynsell.utils.ExcelUtils;
import com.italia.buynsell.utils.Numbers;

/**
 * 
 * @author mark italia
 * @version 1.0
 * @since 09/19/2018
 *
 */

@ManagedBean(name="attendBean", eager=true)
@ViewScoped
public class UploadAttendanceBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 143483743L;
	private  String EXCEL_PATH = System.getenv("SystemDrive") + File.separator + "BuyNSell" + File.separator + "uploadedattendance" +File.separator;
	private List<AttendanceFile> attFiles = Collections.synchronizedList(new ArrayList<AttendanceFile>());		
	List<Attendance> attendances = Collections.synchronizedList(new ArrayList<Attendance>());	
	private String searchName;
	//Map<Long, List<Attendance>> attendanceData = Collections.synchronizedMap(new HashMap<Long, List<Attendance>>());
	private List<Attendance> timeSelected =  Collections.synchronizedList(new ArrayList<Attendance>());
	//private List<Attendance> attendancesData = Collections.synchronizedList(new ArrayList<Attendance>());
	private List<Employees> employees = Collections.synchronizedList(new ArrayList<Employees>());
	private String searchEmployee;
	
	private List<EmployeeCA> cashAs = Collections.synchronizedList(new ArrayList<EmployeeCA>());
	private String searchEmployeeCa;
	
	private List employeeCa = Collections.synchronizedList(new ArrayList<>());
	private int empCaId;
	
	private Date dateCa;
	private double amountCa;
	private EmployeeCA employeeCaData;
	
	private static final double HOURS_PER_DAY = 8.5;
	
	public void loadCAs() {
		cashAs = Collections.synchronizedList(new ArrayList<EmployeeCA>());
		String sql = " AND ca.caisactive=1 AND ca.capayable!=0 ";
		String[] params = new String[0];
		
		if(getSearchEmployeeCa()!=null && !getSearchEmployeeCa().isEmpty()) {
			sql += " AND em.emname like '%"+ getSearchEmployeeCa().replace("--", "")+"%'";
		}
		int index=0;
		for(EmployeeCA ca : EmployeeCA.retrieve(sql, params)) {
			ca.setIndex(index++);
			cashAs.add(ca);
		}
		
		if(employeeCa.size()==0) {
			loadCAEmployees();
		}
	}
	
	public void loadCAEmployees() {
		employeeCa = Collections.synchronizedList(new ArrayList<>());
		for(Employees em : Employees.retrieve("", new String[0])) {
			employeeCa.add(new SelectItem(em.getId(), em.getFullName()));
		}
	}
	
	public void clickCA(EmployeeCA ca) {
		setEmployeeCaData(ca);
		setEmpCaId(ca.getEmployees().getId());
		setDateCa(DateUtils.convertDateString(ca.getCashDate(), "yyyy-MM-dd"));
		setAmountCa(ca.getAmount());
	}
	
	public void saveCA() {
		EmployeeCA ca = new EmployeeCA();
		boolean isOk = true;
		
		if(getEmpCaId()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide Employee Name");
		}
		
		if(getAmountCa()==0) {
			isOk = false;
			Application.addMessage(3, "Error", "Please provide amount");
		}
		
		if(isOk) {
			if(getEmployeeCaData()!=null) {
				ca = getEmployeeCaData();
			}else {
				ca.setIsActive(1);
			}
			
			ca.setCashDate(DateUtils.convertDate(getDateCa(), "yyyy-MM-dd"));
			ca.setAmount(getAmountCa());
			ca.setPayable(getAmountCa());
			Employees em = new Employees();
			em.setId(getEmpCaId());
			ca.setEmployees(em);
			
			ca.save();
			clearCA();
			loadCAs();
			Application.addMessage(1, "Success", "Successfully saved");
		}
		
	}
	
	public void clearCA() {
		setEmployeeCaData(null);
		setEmpCaId(0);
		setDateCa(null);
		setAmountCa(0);
	}
	
	public void uploadData(FileUploadEvent event){
		
		 try {
			 InputStream stream = event.getFile().getInputstream();
			 //String ext = FilenameUtils.getExtension(event.getFile().getFileName());
			 String file = event.getFile().getFileName();
			 
			 if(writeDocToFile(event)){
				 Application.addMessage(1, "Success", "Data has been successfully uploaded");
				 //initAttendanceFile();
			 }else{
				 Application.addMessage(3, "Error", "Error uploading the data " + file);
			 }
			 
	     } catch (Exception e) {
	     
	     }
		
	}
	
	
	public void initAttendanceFile() {
		try {
			File file = new File(EXCEL_PATH);
			String[] fileList = file.list();
			int id=1;
			attFiles = Collections.synchronizedList(new ArrayList<AttendanceFile>());		
			for(String fileName : fileList){
				AttendanceFile atFile = new AttendanceFile();
				String ext = fileName.split("\\.")[1];
				atFile.setId(id);
				atFile.setFileName(fileName);
				attFiles.add(atFile);
			}
			Collections.reverse(attFiles);
		}catch(Exception e) {}
	}
	
	@PostConstruct
	public void init() {
		attendances = Collections.synchronizedList(new ArrayList<Attendance>());
		//attendancesData = Collections.synchronizedList(new ArrayList<Attendance>());
		int index = 0;
		//String sql = " AND att.ispaid=0 AND (att.attdate>=? AND att.attdate<=?)";
		String sql = " AND att.ispaid=0 ";
		String[] params = new String[0];
		//params[0] = DateUtils.getCurrentYear() + "-" + (DateUtils.getCurrentMonth()>9? DateUtils.getCurrentMonth() : "0"+DateUtils.getCurrentMonth()) + "-01";
		//params[1] = DateUtils.getCurrentDateYYYYMMDD();
		
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			sql += " AND em.emname like '%"+ getSearchName().replace("--", "") +"%'";
		}
		
		sql += " ORDER BY att.attdate";
		for(Attendance att : Attendance.retrieve(sql, params)) {
			att.setIndex(index++);
			attendances.add(att);
			//attendancesData.add(att);
		}
	}
	
	@Deprecated
	public void clickItem(AttendanceFile attFile) {
		File file = new File(EXCEL_PATH + attFile.getFileName());
		attendances = Collections.synchronizedList(new ArrayList<Attendance>());
		//attendancesData = Collections.synchronizedList(new ArrayList<Attendance>());
		Map<Long, List<Attendance>> attendance = ExcelUtils.loadFile(file,3);
		//setAttendanceData(attendance);
		int index = 0;
		for(long id : attendance.keySet()) {
			for(Attendance att : attendance.get(id)) {
				att.setIndex(index++);
				att = fillInSavedTimeSheet(att);
				//System.out.println(att.getId()+"\t"+att.getName()+"\t"+att.getDateWork()+"\t"+att.getTimeInAM()+"\t"+att.getTimeOutAM()+"\t"+att.getTimeInPM()+"\t"+att.getTimeOutPM()+"\t"+att.getTotalTime());
				attendances.add(att);
				//attendancesData.add(att);
			}
		}
		
	}
	
	private void readingExcelFile(String fileName) {
		File file = new File(EXCEL_PATH + fileName);
		attendances = Collections.synchronizedList(new ArrayList<Attendance>());
		//attendancesData = Collections.synchronizedList(new ArrayList<Attendance>());
		Map<Long, List<Attendance>> attendance = ExcelUtils.loadFile(file,3);
		//setAttendanceData(attendance);
		int index = 0;
		for(long id : attendance.keySet()) {
			for(Attendance att : attendance.get(id)) {
				att.setIndex(index++);
				att = fillInSavedTimeSheet(att);
				if(att!=null) {
					attendances.add(att);
				}
			}
		}
	}
	
	public void calculateTimeSheetAndSave() {
		if(getTimeSelected()!=null && getTimeSelected().size()>0) {
			
			for(Attendance att : getTimeSelected()) {
				
				double amIn=6.67;
				double amInM=0;
				double amOut=11.67;
				double amOutM=0;
				double pmIn=13;
				double pmInM=0;
				double pmOut=16.5;
				double pmOutM=0;
				double total = 0d;
				boolean isAMAttended = false;
				boolean isPMAttended = false;
				if(att.getTimeInAM()!=null && !att.getTimeInAM().isEmpty()) {
					amIn= Double.valueOf(att.getTimeInAM().split(":")[0]);
					amInM= Double.valueOf(att.getTimeInAM().split(":")[1]);
					isAMAttended = true;
					amInM = amInM/60;
					amIn +=amInM;
					amIn = Numbers.formatDouble(amIn);
					if(amIn<6.67) {
						amIn=6.67;
					}
				}
				
				if(att.getTimeOutAM()!=null && !att.getTimeOutAM().isEmpty()) {
					amOut= Double.valueOf(att.getTimeOutAM().split(":")[0]);
					amOutM= Double.valueOf(att.getTimeOutAM().split(":")[1]);
					amOutM = amOutM/60;
					amOut += amOutM;
					amOut = Numbers.formatDouble(amOut);
					if(amOut>11.67) {
						amOut=11.67;
					}
				}
				
				if(att.getTimeInPM()!=null && !att.getTimeInPM().isEmpty()) {
					pmIn= Double.valueOf(att.getTimeInPM().split(":")[0]);
					pmInM= Double.valueOf(att.getTimeInPM().split(":")[1]);
					isPMAttended = true;
					pmInM = pmInM/60;
					pmIn +=pmInM;
					pmIn = Numbers.formatDouble(pmIn);
					if(pmIn<13) {
						pmIn=13;
					}
				}
				
				if(att.getTimeOutPM()!=null && !att.getTimeOutPM().isEmpty()) {
					pmOut= Double.valueOf(att.getTimeOutPM().split(":")[0]);
					pmOutM= Double.valueOf(att.getTimeOutPM().split(":")[1]);
					pmOutM = pmOutM/60;
					pmOut += pmOutM;
					pmOut = Numbers.formatDouble(pmOut);
					if(pmOut>16.5) {
						pmOut=16.5;
					}
				}
				
				if(isAMAttended)
					total = amOut - amIn;
				if(isPMAttended)
					total += pmOut - pmIn;
				
				//no overtime = maximum hours is 8.5
				if(total>8.5) {
					total = 8.5;
				}
				
				if(total<0) {
					total = 0;
				}
				
				att.setTotalTime(Numbers.formatDouble(total));
				double salaryPerHour = att.getEmployees().getDailySalary() / HOURS_PER_DAY;
				double dailySalary = salaryPerHour * total;
				att.setTotalAmount(Numbers.formatDouble(dailySalary));
				
				att.save();
			}
			Application.addMessage(1, "Success", "Successfully updated");
			init();
			setTimeSelected(null);
		}
		
	}
	
	private Attendance fillInSavedTimeSheet(Attendance newdata) {
		
		Attendance oldAtt = Attendance.findAndLoadTimeSheet(newdata.getEmployees().getId(), newdata.getDateWork());
		
		
		if(oldAtt!=null) {
			if(oldAtt.getIsPaid()==0) {
				boolean isTosave = false;
				oldAtt.setIndex(newdata.getIndex());
				
				oldAtt.setName(newdata.getName());
				
				if(newdata.getTimeInAM()!=null && !newdata.getTimeInAM().isEmpty()) { 
					if(oldAtt.getTimeInAM()==null || oldAtt.getTimeInAM().isEmpty()) {
						oldAtt.setTimeInAM(newdata.getTimeInAM());
						isTosave = true;
					}
				}
				if(newdata.getTimeOutAM()!=null && !newdata.getTimeOutAM().isEmpty()) {
					if(oldAtt.getTimeOutAM()==null || oldAtt.getTimeOutAM().isEmpty()) {
						oldAtt.setTimeOutAM(newdata.getTimeOutAM());
						isTosave = true;
					}
				}
				if(newdata.getTimeInPM()!=null && !newdata.getTimeInPM().isEmpty()) { 
					if(oldAtt.getTimeInPM()==null || oldAtt.getTimeInPM().isEmpty()) {
						oldAtt.setTimeInPM(newdata.getTimeInPM());
						isTosave = true;
					}
				}
				if(newdata.getTimeOutPM()!=null && !newdata.getTimeOutPM().isEmpty()) { 
					if(oldAtt.getTimeOutPM()==null || oldAtt.getTimeOutPM().isEmpty()) {
						oldAtt.setTimeOutPM(newdata.getTimeOutPM());
						isTosave = true;
					}
				}
				
				if(isTosave) {
					
					double timeTotal = calculateTime(oldAtt);
					oldAtt.setTotalTime(timeTotal);
					
					oldAtt = Attendance.save(oldAtt);
				}
			}else {
				//timesheet already completed
				return null;
			}
		}else {
			//oldAtt = newdata;
			newdata.setIsActive(1);
			oldAtt = Attendance.save(newdata);
		}
		
		return oldAtt;
	}
	
	private double calculateTime(Attendance att) {
		double amIn=6.67;
		double amInM=0;
		double amOut=11.67;
		double amOutM=0;
		double pmIn=13;
		double pmInM=0;
		double pmOut=16.5;
		double pmOutM=0;
		double total = 0d;
		boolean isAMAttended = false;
		boolean isPMAttended = false;
		if(att.getTimeInAM()!=null && !att.getTimeInAM().isEmpty()) {
			amIn= Double.valueOf(att.getTimeInAM().split(":")[0]);
			amInM= Double.valueOf(att.getTimeInAM().split(":")[1]);
			isAMAttended = true;
			amInM = amInM/60;
			amIn +=amInM;
			amIn = Numbers.formatDouble(amIn);
			if(amIn<6.67) {
				amIn=6.67;
			}
		}
		
		if(att.getTimeOutAM()!=null && !att.getTimeOutAM().isEmpty()) {
			amOut= Double.valueOf(att.getTimeOutAM().split(":")[0]);
			amOutM= Double.valueOf(att.getTimeOutAM().split(":")[1]);
			amOutM = amOutM/60;
			amOut += amOutM;
			amOut = Numbers.formatDouble(amOut);
			if(amOut>11.67) {
				amOut=11.67;
			}
		}
		
		if(att.getTimeInPM()!=null && !att.getTimeInPM().isEmpty()) {
			pmIn= Double.valueOf(att.getTimeInPM().split(":")[0]);
			pmInM= Double.valueOf(att.getTimeInPM().split(":")[1]);
			isPMAttended = true;
			pmInM = pmInM/60;
			pmIn +=pmInM;
			pmIn = Numbers.formatDouble(pmIn);
			if(pmIn<13) {
				pmIn=13;
			}
		}
		
		if(att.getTimeOutPM()!=null && !att.getTimeOutPM().isEmpty()) {
			pmOut= Double.valueOf(att.getTimeOutPM().split(":")[0]);
			pmOutM= Double.valueOf(att.getTimeOutPM().split(":")[1]);
			pmOutM = pmOutM/60;
			pmOut += pmOutM;
			pmOut = Numbers.formatDouble(pmOut);
			if(pmOut>16.5) {
				pmOut=16.5;
			}
		}
		
		if(isAMAttended)
			total = amOut - amIn;
		if(isPMAttended)
			total += pmOut - pmIn;
		
		//no overtime = maximum hours is 8.5
		if(total>8.5) {
			total = 8.5;
		}
		
		if(total<0) {
			total = 0;
		}
		return Numbers.formatDouble(total);
	}
	
	public void deleteExcelFile(AttendanceFile attFile) {
		try {
			File file = new File(EXCEL_PATH + attFile.getFileName());
			file.delete();
			Application.addMessage(1, "Success", "Data has been successfully deleted");
			initAttendanceFile();
			attendances = Collections.synchronizedList(new ArrayList<Attendance>());
			//setAttendanceData(null);
		}catch(Exception e) {}
	}
	
	@Deprecated
	public void searchingName() {
		/*List<Attendance> attendancesTmp = Collections.synchronizedList(new ArrayList<Attendance>());
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
			attendancesTmp = attendances;
			attendances = Collections.synchronizedList(new ArrayList<Attendance>());
		}
		int index=0;
		for(Attendance att : attendancesTmp) {
			if(getSearchName()!=null && !getSearchName().isEmpty()) {
				if(att.getName().contains(getSearchName())) {
					att.setIndex(index++);
					attendances.add(att);
				}
			}
		}*/
		/*attendances = Collections.synchronizedList(new ArrayList<Attendance>());
		int index = 0;
		String sql = " AND att.ispaid=0 ";
		String[] params = new String[0];
		
		if(getSearchName()!=null && !getSearchName().isEmpty()) {
				sql += " AND em.emname like '%"+ getSearchName().replace("--", "") +"%'";
		}
		
		for(Attendance att : Attendance.retrieve(sql, params)) {
			att.setIndex(index++);
			attendances.add(att);
		}*/
		
	}
	
	private boolean writeDocToFile(FileUploadEvent event){
		try{
		InputStream stream = event.getFile().getInputstream();
		String fileExt = event.getFile().getFileName().split("\\.")[1];
		String filename = "attendance-" + DateUtils.getCurrentDateMMDDYYYYTIMEPlain() + "."+fileExt.toLowerCase();
		
		System.out.println("writing... writeDocToFile : " + filename);
		File fileDoc = new File(EXCEL_PATH +  filename);
		Path file = fileDoc.toPath();
		Files.copy(stream, file, StandardCopyOption.REPLACE_EXISTING);
		//return loadToDatabase(fileDoc);
		readingExcelFile(filename);
		return true;
		}catch(IOException e){return false;}
		
	}
	
		 
	public void updateEmployeeTime() {
		if(getTimeSelected()!=null) {
			int cnt = 0;
			for(Attendance att : getTimeSelected()) {
				System.out.println(att.getName() +"\t" + att.getDateWork() + "\t" + att.getTimeInAM());
				att.update(att.getEmployees().getId(), att.getDateWork(), att);
				cnt++;
			}
			Application.addMessage(1, "Success", "You have successfully save ("+cnt+") timeshet");
		}
	}
	
	public void loadEmployee() {
		employees = Collections.synchronizedList(new ArrayList<Employees>());
		String sql = " ";
		String[] params = new String[0];
		if(getSearchEmployee()!=null && !getSearchEmployee().isEmpty()) {
			sql = " AND emname like '%"+ getSearchEmployee().replace("--", "") +"%'";
		}
		int index=0;
		for(Employees em : Employees.retrieve(sql, params)) {
			em.setIndex(index++);
			employees.add(em);
		}
	}
	
	public void onCellEditEmployee(CellEditEvent event) {
		
		double amount = (double)event.getNewValue();
		int index = event.getRowIndex();
		Employees em = getEmployees().get(index);
		em.setDailySalary(amount);
		em.save();
		Application.addMessage(1, "Success", "Data has been updated.");
	}
	
	public void onCellEdit(CellEditEvent event) {
		
	}
	
	public List<AttendanceFile> getAttFiles() {
		return attFiles;
	}

	public void setAttFiles(List<AttendanceFile> attFiles) {
		this.attFiles = attFiles;
	}

	public List<Attendance> getAttendances() {
		return attendances;
	}

	public void setAttendances(List<Attendance> attendances) {
		this.attendances = attendances;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	/*public Map<Long, List<Attendance>> getAttendanceData() {
		return attendanceData;
	}

	public void setAttendanceData(Map<Long, List<Attendance>> attendanceData) {
		this.attendanceData = attendanceData;
	}*/

	public List<Attendance> getTimeSelected() {
		return timeSelected;
	}

	public void setTimeSelected(List<Attendance> timeSelected) {
		this.timeSelected = timeSelected;
	}

	/*public List<Attendance> getAttendancesData() {
		return attendancesData;
	}

	public void setAttendancesData(List<Attendance> attendancesData) {
		this.attendancesData = attendancesData;
	}*/

	public List<Employees> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employees> employees) {
		this.employees = employees;
	}

	public String getSearchEmployee() {
		return searchEmployee;
	}

	public void setSearchEmployee(String searchEmployee) {
		this.searchEmployee = searchEmployee;
	}


	public List<EmployeeCA> getCashAs() {
		return cashAs;
	}


	public void setCashAs(List<EmployeeCA> cashAs) {
		this.cashAs = cashAs;
	}


	public String getSearchEmployeeCa() {
		return searchEmployeeCa;
	}


	public void setSearchEmployeeCa(String searchEmployeeCa) {
		this.searchEmployeeCa = searchEmployeeCa;
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

	public Date getDateCa() {
		if(dateCa==null) {
			dateCa = DateUtils.getDateToday();
		}
		return dateCa;
	}

	public void setDateCa(Date dateCa) {
		this.dateCa = dateCa;
	}

	public double getAmountCa() {
		return amountCa;
	}

	public void setAmountCa(double amountCa) {
		this.amountCa = amountCa;
	}

	public EmployeeCA getEmployeeCaData() {
		return employeeCaData;
	}

	public void setEmployeeCaData(EmployeeCA employeeCaData) {
		this.employeeCaData = employeeCaData;
	}
	
	public static void main(String[] args) {
		
		Attendance att = new Attendance();
		att.setTimeInAM("6:40");
		att.setTimeOutAM("11:40");
		att.setTimeInPM("13:00");
		att.setTimeOutPM("16:30");
		
		double amIn=6.67;
		double amInM=0;
		double amOut=11.67;
		double amOutM=0;
		double pmIn=13;
		double pmInM=0;
		double pmOut=16.5;
		double pmOutM=0;
		double total = 0d;
		boolean isAMAttended = false;
		boolean isPMAttended = false;
		if(att.getTimeInAM()!=null && !att.getTimeInAM().isEmpty()) {
			amIn= Double.valueOf(att.getTimeInAM().split(":")[0]);
			amInM= Double.valueOf(att.getTimeInAM().split(":")[1]);
			isAMAttended = true;
			amInM = amInM/60;
			amIn +=amInM;
			amIn = Numbers.formatDouble(amIn);
		}
		
		if(att.getTimeOutAM()!=null && !att.getTimeOutAM().isEmpty()) {
			amOut= Double.valueOf(att.getTimeOutAM().split(":")[0]);
			amOutM= Double.valueOf(att.getTimeOutAM().split(":")[1]);
			amOutM = amOutM/60;
			amOut += amOutM;
			amOut = Numbers.formatDouble(amOut);
		}
		
		if(att.getTimeInPM()!=null && !att.getTimeInPM().isEmpty()) {
			pmIn= Double.valueOf(att.getTimeInPM().split(":")[0]);
			pmInM= Double.valueOf(att.getTimeInPM().split(":")[1]);
			isPMAttended = true;
			pmInM = pmInM/60;
			pmIn +=pmInM;
			pmIn = Numbers.formatDouble(pmIn);
		}
		
		if(att.getTimeOutPM()!=null && !att.getTimeOutPM().isEmpty()) {
			pmOut= Double.valueOf(att.getTimeOutPM().split(":")[0]);
			pmOutM= Double.valueOf(att.getTimeOutPM().split(":")[1]);
			pmOutM = pmOutM/60;
			pmOut += pmOutM;
			pmOut = Numbers.formatDouble(pmOut);
		}
		
		if(isAMAttended)
			total = amOut - amIn;
		
		System.out.println("Am Total: " + total);
		
		if(isPMAttended) {
			total += pmOut - pmIn;
			System.out.println("pm out " + pmOut +" pmIn "+ pmIn);
		}	
		
		
		
		//no overtime = maximum hours is 8.5
		if(total>8.5) {
			total = 8.5;
		}
		
		System.out.println("total hous: " + total);
		
	}
	
}
