package com.italia.buynsell.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.italia.buynsell.controller.Attendance;
import com.italia.buynsell.controller.Employees;

/**
 * 
 * @author Mark Italia
 * @version: 1.0
 * @since 09/18/2018
 *
 *Using library commons-collections4-4.2
 */

public class ExcelUtils {
	
	private static Map<Integer, Employees> employeeData = Collections.synchronizedMap(new HashMap<Integer, Employees>());
	private static final double HOURS_PER_DAY = 8.5;
	
	public static Map<Long, List<Attendance>> loadFile(File file,int sheetNo) {
		System.out.println(file.getName());
		String ext = file.getName().split("\\.")[1];

		if(ExcelExtension.XLS.getName().equalsIgnoreCase(ext)) {
			loadEmployeeInformation();
			readEmployeeXLS(file, 1);
			Map<Long, List<Attendance>> attendance = readXLSFile(file,sheetNo);
			return attendance;
		}else if(ExcelExtension.XLSX.getName().equalsIgnoreCase(ext)) {
			loadEmployeeInformation();
			readEmployeeXLSX(file, 1);
			Map<Long, List<Attendance>> attendance = readXLSXFile(file,sheetNo);
			return attendance;
		}else if(ExcelExtension.TXT.getName().equalsIgnoreCase(ext)) {
			loadEmployeeInformation();
			readEmployeeInText(file);
			Map<Long, List<Attendance>> attendance = readTextFile(file);
			return attendance;
		}
		return null;
	}
	
	private static void loadEmployeeInformation() {
		employeeData = Collections.synchronizedMap(new HashMap<Integer, Employees>());
		for(Employees em : Employees.retrieve("", new String[0])) {
			employeeData.put(em.getId(), em);
		}
	}
	
	private static Map<Long, List<Attendance>> readXLSFile(File file,int sheetNo) {
		try {
			Map<Long, List<Attendance>> attendance = Collections.synchronizedMap(new HashMap<Long, List<Attendance>>());
			List<Attendance> atts = Collections.synchronizedList(new ArrayList<Attendance>());	
			
			FileInputStream fin = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(fin); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheetNo);
			HSSFRow row;
			HSSFCell cell;
			
				Iterator rows = sheet.rowIterator();
				int startRow=1;
			    while (rows.hasNext()){
		            row=(HSSFRow) rows.next();
		            Iterator cells = row.cellIterator();
		            int countRow = 1;
		            Attendance att = new Attendance();
		            while (cells.hasNext()){
		            	
		            	if(countRow<=9 && startRow>4) {
		            	
			                cell=(HSSFCell) cells.next();
			                String value="";
			                if(cell.getCellTypeEnum()==CellType.STRING) {
			                	value = cell.getStringCellValue();
			                }else if(cell.getCellTypeEnum()==CellType.NUMERIC) {
			                	value = cell.getNumericCellValue()+"";
			                }else {
			                	//U Can Handel Boolean, Formula, Errors
			                	//System.out.println("\t");
			                }
			                
			                switch(countRow) {
			                case 1:
			                	//att.setId(Integer.valueOf(value));
			                	Employees em = new Employees();
			                	em.setId(Integer.valueOf(value));
			                	att.setEmployees(em);break;
			                case 2:att.setName(value);break;
			                case 4:att.setDateWork(value.replace("/", "-"));break;
			                case 5:att.setTimeInAM(value);break;
			                case 6:att.setTimeOutAM(value);break;
			                case 7:att.setTimeInPM(value);break;
			                case 8:att.setTimeOutPM(value);
			                	att = provideTimeForEmptyTimeOut(att);
			                	break;
			                case 9:att.setTotalTime(calculateTime(att));
			                	att.setTotalAmount(calculateSalry(att.getEmployees().getId(), att.getTotalTime()));
			                break;	
			                }
		                
		            	}else {
		            		break;
		            	}
		                countRow++; 
		                
		            }
		            //System.out.println();
		            
		            if(startRow>4) {
	                	long id = att.getEmployees().getId();
	                if(attendance!=null) {
	                	if(attendance.containsKey(id)) {
	                		atts = attendance.get(id);
	                		atts.add(att);
	                		attendance.put(id, atts);
	                	}else {
	                		atts = Collections.synchronizedList(new ArrayList<Attendance>());
	                		atts.add(att);
	                		attendance.put(id, atts);
	                	}
	                }else {
	                	atts.add(att);
	                	attendance.put(id, atts);
	                }
	                }
	                
	                startRow++;
		        }
	    
		
			    fin.close();
				
				return attendance;
		}catch(Exception e) {
			
		}
		return null;
	}
	
	private static Attendance provideTimeForEmptyTimeOut(Attendance att) {
		boolean isAMAttended = false;
		boolean isPMAttended = false;
		double todayHour = Double.valueOf(DateUtils.getCurrentHourMinutes().split(":")[0]);
		double todayMinute = Double.valueOf(DateUtils.getCurrentHourMinutes().split(":")[1]);
		String todayDate = DateUtils.getCurrentDateYYYYMMDD();
		
		todayHour +=todayMinute/60;
		
		if(att.getTimeInAM()!=null && !att.getTimeInAM().isEmpty()) {
			isAMAttended = true;
		}
		
		if(att.getTimeOutAM()==null || att.getTimeOutAM().isEmpty()) {
			if(isAMAttended && todayHour>12.5){
				att.setTimeOutAM("11:40");
			}else if(isAMAttended && !todayDate.equalsIgnoreCase(att.getDateWork())) {
				att.setTimeOutAM("11:40");
			}
		}
		
		if(att.getTimeInPM()!=null && !att.getTimeInPM().isEmpty()) {
			isPMAttended = true;
		}
		
		if(att.getTimeOutPM()==null || att.getTimeOutPM().isEmpty()) {
			if(isPMAttended && todayHour>16.5){
				att.setTimeOutPM("16:30");
			}else if(isPMAttended && !todayDate.equalsIgnoreCase(att.getDateWork())) {
				att.setTimeOutAM("16:30");
			}
		}
		
		return att;
	}
	
	private static double calculateSalry(int employeeId, double hours) {
		double salary = 0;
		//System.out.println("calculateSalry: " + employeeId);
		if(getEmployeeData()!=null) {
			salary = getEmployeeData().get(employeeId).getDailySalary()/HOURS_PER_DAY;
			salary *= hours;
			//System.out.println("salary Php: " + salary);
		}
		
		salary = Numbers.formatDouble(salary);
		return salary;
	}
	
	private static Map<Long, List<Attendance>> readXLSXFile(File file,int sheetNo) {
		try {
			
			Map<Long, List<Attendance>> attendance = Collections.synchronizedMap(new HashMap<Long, List<Attendance>>());
			List<Attendance> atts = Collections.synchronizedList(new ArrayList<Attendance>());
			
			FileInputStream fin = new FileInputStream(file);
			XSSFWorkbook wbx = new XSSFWorkbook(fin);
			XSSFSheet sheetx = wbx.getSheetAt(sheetNo);
			XSSFRow rowx;
			XSSFCell cellx;
			
			Iterator rows = sheetx.rowIterator();
			int startRow=1;
		    while (rows.hasNext())
	        {
		    	
	            rowx=(XSSFRow) rows.next();
	            Iterator cells = rowx.cellIterator();
	            int countRow = 1;
	            Attendance att = new Attendance();
	            while (cells.hasNext()) {
	            	if(countRow<=9 && startRow>4) {
	            		
		                cellx=(XSSFCell) cells.next();
		                String value="";
		                if(cellx.getCellTypeEnum()==CellType.STRING) {
		                	value = cellx.getStringCellValue();
		                }else if(cellx.getCellTypeEnum()==CellType.NUMERIC) {
		                	value = cellx.getNumericCellValue()+"";
		                }else {
		                	//U Can Handel Boolean, Formula, Errors
		                }
		                
		                switch(countRow) {
		                case 1:
		                	//att.setId(Integer.valueOf(value));
		                	Employees em = new Employees();
		                	em.setId(Integer.valueOf(value));
		                	att.setEmployees(em);break;
		                case 2:att.setName(value);break;
		                case 4:att.setDateWork(value.replace("/", "-"));break;
		                case 5:att.setTimeInAM(value);break;
		                case 6:att.setTimeOutAM(value);break;
		                case 7:att.setTimeInPM(value);break;
		                case 8:att.setTimeOutPM(value);
			                att = provideTimeForEmptyTimeOut(att);
			                break;
		                case 9:att.setTotalTime(calculateTime(att));
		                	att.setTotalAmount(calculateSalry(att.getEmployees().getId(), att.getTotalTime()));
		                	break;
		                }
	                
	            	}else {
	            		break;
	            	}
	                countRow++; 
	                
	            }
	            
	            
                if(startRow>4) {
                	long id = att.getEmployees().getId();
                if(attendance!=null) {
                	if(attendance.containsKey(id)) {
                		atts = attendance.get(id);
                		atts.add(att);
                		attendance.put(id, atts);
                	}else {
                		atts = Collections.synchronizedList(new ArrayList<Attendance>());
                		atts.add(att);
                		attendance.put(id, atts);
                	}
                }else {
                	atts.add(att);
                	attendance.put(id, atts);
                }
                }
	            //System.out.println();
	            startRow++;
	        }
			
			fin.close();
			
			return attendance;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static double calculateTime(Attendance att) {
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
		String todayDate = DateUtils.getCurrentDateYYYYMMDD();
		
		//if no time out yet for current day
		if(todayDate.equalsIgnoreCase(att.getDateWork())) {
			amOut=0;
			pmOut=0;
		}
		
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
	
	public static void readEmployeeXLS(File file,int sheetNo) {
		try {
			FileInputStream fin = new FileInputStream(file);
			POIFSFileSystem fs = new POIFSFileSystem(fin); 
			HSSFWorkbook wb = new HSSFWorkbook(fs);
			HSSFSheet sheet = wb.getSheetAt(sheetNo);
			HSSFRow row;
			HSSFCell cell;
			List<Employees> ems = Collections.synchronizedList(new ArrayList<Employees>());
			Iterator rows = sheet.rowIterator();
			int startRow=1;
		    while (rows.hasNext()){
	            row=(HSSFRow) rows.next();
	            Iterator cells = row.cellIterator();
	            int countRow = 1;
	            Employees em = new Employees();
	            while (cells.hasNext()){
	            	
	            	if(countRow<=2 && startRow>4) {
	            	
		                cell=(HSSFCell) cells.next();
		                String value="";
		                if(cell.getCellTypeEnum()==CellType.STRING) {
		                	value = cell.getStringCellValue();
		                }else if(cell.getCellTypeEnum()==CellType.NUMERIC) {
		                	value = cell.getNumericCellValue()+"";
		                }else {
		                	//U Can Handel Boolean, Formula, Errors
		                	//System.out.println("\t");
		                }
		                
		                switch(countRow) {
		                case 1:em.setId(Integer.valueOf(value));break;
		                case 2:em.setFullName(value);break;
		                }
	                
	            	}else {
	            		break;
	            	}
	                countRow++; 
	                
	            }
	            if(startRow>4) {
	            	ems.add(em);
	            }
	            //System.out.println();
                startRow++;
	        }
    
		    fin.close();
			
		    
		    for(Employees e : ems) {
		    	if(getEmployeeData()!=null) {
		    		if(!getEmployeeData().containsKey(e.getId())) {
		    			e.setIsActive(1);
			    		e.setDailySalary(0);
			    		e.save();
		    		}
		    	}else {
		    		e.setIsActive(1);
		    		e.setDailySalary(0);
		    		e.save();
		    	}
		    	
		    	
		    }
		    
		}catch(Exception e) {e.printStackTrace();}
	}
	
	public static void readEmployeeXLSX(File file,int sheetNo) {
		try {
			FileInputStream fin = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fin);
			XSSFSheet sheet = wb.getSheetAt(sheetNo);
			XSSFRow row;
			XSSFCell cell;
			List<Employees> ems = Collections.synchronizedList(new ArrayList<Employees>());
			Iterator rows = sheet.rowIterator();
			int startRow=1;
		    while (rows.hasNext()){
	            row=(XSSFRow) rows.next();
	            Iterator cells = row.cellIterator();
	            int countRow = 1;
	            Employees em = new Employees();
	            while (cells.hasNext()){
	            	
	            	if(countRow<=2 && startRow>4) {
	            	
		                cell=(XSSFCell) cells.next();
		                String value="";
		                if(cell.getCellTypeEnum()==CellType.STRING) {
		                	value = cell.getStringCellValue();
		                }else if(cell.getCellTypeEnum()==CellType.NUMERIC) {
		                	value = cell.getNumericCellValue()+"";
		                }else {
		                	//U Can Handel Boolean, Formula, Errors
		                	//System.out.println("\t");
		                }
		                
		                switch(countRow) {
		                case 1:em.setId(Integer.valueOf(value));break;
		                case 2:em.setFullName(value);break;
		                }
	                
	            	}else {
	            		break;
	            	}
	                countRow++; 
	                
	            }
	            if(startRow>4) {
	            	ems.add(em);
	            }
	            //System.out.println();
                startRow++;
	        }
    
		    fin.close();
			
		    
		    for(Employees e : ems) {
		    	if(getEmployeeData()!=null) {
		    		if(!getEmployeeData().containsKey(e.getId())) {
		    			e.setIsActive(1);
			    		e.setDailySalary(0);
			    		e.save();
		    		}
		    	}else {
		    		e.setIsActive(1);
		    		e.setDailySalary(0);
		    		e.save();
		    	}
		    	
		    	
		    }
		    
		}catch(Exception e) {e.printStackTrace();}
	}
	

	public static Map<Integer, Employees> getEmployeeData() {
		return employeeData;
	}

	public static void setEmployeeData(Map<Integer, Employees> employeeData) {
		ExcelUtils.employeeData = employeeData;
	}
	
	public static Map<Long, List<Attendance>> readTextFile(File file) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    
		    Map<Integer, Map<String,Attendance>> attendaceMaps = Collections.synchronizedMap(new HashMap<Integer,Map<String, Attendance>>());
		    Map<String, Attendance> attendance = Collections.synchronizedMap(new HashMap<String,Attendance>());
		    Attendance att = new Attendance();
		    
		    int cnt = 1;
		    while (line != null && !line.isEmpty()) {
		    	if(cnt>=3) {
			    	String[] val = line.split("\t");
			    	int empId = Integer.valueOf(val[2]);
			    	String name = val[3];
			    	String dateTime = val[6];
			    	String[] dt = dateTime.split("  ");	
			    	String date = dt[0].replace("/", "-");
			    	String time = dt[1];
			    	
			    	Employees emp = new Employees();
		    		emp.setId(empId);
		    		emp.setFullName(name);
		    		
			    	
			    	if(attendaceMaps!=null) {
			    		
			    		if(attendaceMaps.containsKey(emp.getId())) {
			    			
			    			if(attendaceMaps.get(emp.getId()).containsKey(date)) {
			    				att=attendaceMaps.get(emp.getId()).get(date);
			    				att = filingTimeSlot(att, time);
					    		attendaceMaps.get(emp.getId()).put(date, att);
			    			}else {
			    				att = new Attendance();
			    				att.setName(name);
					    		att.setEmployees(emp);
			    				att.setDateWork(date);
					    		att.setIsPaid(0);
					    		att.setIsActive(1);
					    		att = filingTimeSlot(att, time);
					    		attendaceMaps.get(emp.getId()).put(date, att);
			    			}
			    			
			    		}else {
			    			att = new Attendance();
			    			attendance = Collections.synchronizedMap(new HashMap<String,Attendance>());
			    			att.setName(name);
				    		att.setEmployees(emp);
			    			att.setDateWork(date);
				    		att.setIsPaid(0);
				    		att.setIsActive(1);
				    		att = filingTimeSlot(att, time);
				    		
				    		attendance.put(date, att);
				    		attendaceMaps.put(emp.getId(), attendance);
			    		}
			    		
			    	}else {
			    		
			    		att = new Attendance();
			    		att.setName(name);
			    		att.setEmployees(emp);
			    		att.setDateWork(date);
			    		att.setIsPaid(0);
			    		att.setIsActive(1);
			    		att = filingTimeSlot(att, time);
			    		
			    		attendance.put(date, att);
			    		attendaceMaps.put(emp.getId(), attendance);
			    	}
			        
		    	}
		    	line = br.readLine();
		        cnt++;
		    }
		    
		    Map<Long, List<Attendance>> atts = Collections.synchronizedMap(new HashMap<Long,List<Attendance>>());
		    List<Attendance> attendances = Collections.synchronizedList(new ArrayList<Attendance>());
		    if(attendaceMaps!=null && attendaceMaps.size()>0) {
		    	for(int empId : attendaceMaps.keySet()) {
		    		 Map<String, Attendance> attx = new TreeMap<String, Attendance>(attendaceMaps.get(empId));
		    		for(String date : attx.keySet()) {
		    			Attendance tm = attendaceMaps.get(empId).get(date);
		    			
		    			tm = provideTimeForEmptyTimeOut(tm);
		    			tm.setTotalTime(calculateTime(tm));
	                	tm.setTotalAmount(calculateSalry(tm.getEmployees().getId(), tm.getTotalTime()));
		    			long id = tm.getEmployees().getId();
		    			
	                	if(atts!=null && atts.size()>0) {
	                		if(atts.containsKey(id)) {
	                			attendances = atts.get(id);
	                			attendances.add(tm);
	                			atts.put(id, attendances);
	                		}else {
	                			attendances = Collections.synchronizedList(new ArrayList<Attendance>());
	                			attendances.add(tm);
		                		atts.put(id, attendances);
	                		}
	                	}else {
	                		attendances.add(tm);
	                		atts.put(id, attendances);
	                	}
	                	
		    		}
		    	}
		    }
		    return atts;
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
		
		
	}
	
	private static Attendance filingTimeSlot(Attendance att, String time) {
		
		double hour = Double.valueOf(time.split(":")[0]);
		double minutes = Double.valueOf(time.split(":")[1]);
		minutes = minutes/60;
		hour = hour + minutes;
		hour = Numbers.formatDouble(hour);
		
		if(hour<=12.5) {
			if(att.getTimeInAM()==null || att.getTimeInAM().isEmpty()) {
				att.setTimeInAM(time.split(":")[0]+":"+time.split(":")[1]);
			}else {
				att.setTimeOutAM(time.split(":")[0]+":"+time.split(":")[1]);
			}
		}else if(hour>12.5) {
			if(att.getTimeInPM()==null || att.getTimeInPM().isEmpty()) {
				att.setTimeInPM(time.split(":")[0]+":"+time.split(":")[1]);
			}else {
				att.setTimeOutPM(time.split(":")[0]+":"+time.split(":")[1]);
			}
		}
		
		return att;
	}
	
	private static void readEmployeeInText(File file) {
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();
		    List<Employees> ems = Collections.synchronizedList(new ArrayList<Employees>());
			int cnt = 1;
		    while (line != null && !line.isEmpty()) {
		    	if(cnt>=3) {
		    		
			    	String[] val = line.split("\t");
			    	int empId = Integer.valueOf(val[2]);
			    	String name = val[3];
			    	String dateTime = val[6];
			    	String[] dt = dateTime.split("  ");	
			    	String date = dt[0].replace("/", "-");
			    	String time = dt[1];
			    	
			    	Employees emp = new Employees();
		    		emp.setId(empId);
		    		emp.setFullName(name);
		    		ems.add(emp);
		    	}
		    	line = br.readLine();
		        cnt++;
		    }	
			
		    for(Employees e : ems) {
		    	if(getEmployeeData()!=null) {
		    		if(!getEmployeeData().containsKey(e.getId())) {
		    			e.setIsActive(1);
			    		e.setDailySalary(0);
			    		e.save();
		    		}
		    	}else {
		    		e.setIsActive(1);
		    		e.setDailySalary(0);
		    		e.save();
		    	}
		    	
		    	
		    }
		    
		}catch(Exception e) {}
		
	}
	
	public static void main(String[] args) {
		try {
		
			//File file = new File("C://bris//xxx.xlsx");
			File file = new File("C://bris//09Static.xls");
			file = new File("C://BuyNSell///GLG_001.txt");
			Map<Long, List<Attendance>> attendance = loadFile(file,3);
			//File file = new File("C://bris//attendance.xlsx");
			//Map<Long, List<Attendance>> attendance = loadFile(file,3);
			//System.out.println("Reading excel file...");
			//System.out.println("reading now...");
			for(long id : attendance.keySet()) {
				//System.out.println("done reading here ");
				for(Attendance att : attendance.get(id)) {
					System.out.println(att.getId()+"\t"+att.getName()+"\t"+att.getDateWork()+"\t"+att.getTimeInAM()+"\t"+att.getTimeOutAM()+"\t"+att.getTimeInPM()+"\t"+att.getTimeOutPM()+"\t"+att.getTotalTime()+"\t"+att.getTotalAmount());
				}
			}
			//System.out.println("done reading");
			/*try(BufferedReader br = new BufferedReader(new FileReader(file))) {
			    StringBuilder sb = new StringBuilder();
			    String line = br.readLine();
			    
			    int cnt = 1;
			    while (line != null) {
			    	if(cnt>=3) {
				    	String[] val = line.split("\t");
				    	int empId = Integer.valueOf(val[2]);
				    	String name = val[3];
				    	String dateTime = val[6];
				    	String[] dt = dateTime.split("  ");	
				    	String date = dt[0].replace("/", "-");
				    	String time = dt[1];
				    	
				    	System.out.println(empId+"\t" +name.trim() +"\t" + date +"\t" + time);
				        
				        sb.append(System.lineSeparator());
				        
			    	}
			    	line = br.readLine();
			        cnt++;
			    }
			    //String everything = sb.toString();
			   // System.out.println(everything);
			}*/
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

enum ExcelExtension{
	
	XLS("xls"),
	XLSX("xlsx"),
	TXT("txt");
	
	private String name;
	
	ExcelExtension(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}