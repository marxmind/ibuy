package com.italia.buynsell.ipay;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.controller.CashIn;
import com.italia.buynsell.controller.Debit;
import com.italia.buynsell.controller.ReadApplicationDetails;
import com.italia.buynsell.utils.DateUtils;

@Deprecated
public class IPayTables {
	
	/**
	 * 
	 * @param type save or delete
	 * @param tableName provide table name
	 * @param data
	 * @param sqlStatements
	 * @param parameters
	 * @return
	 */
	/*public static IPayFields process(String type,String tableName, String[] data,String sqlStatements, String[] parameters){
		IPayFields payFields = new IPayFields();
		ReadApplicationDetails db = new ReadApplicationDetails();
		
		if("yes".equalsIgnoreCase(db.getIncludeIpay())){
			if("save".equalsIgnoreCase(type)){
				payFields = save(tableName,data);
			}else if("delete".equalsIgnoreCase(type)){
				delete(tableName, data);
			}
		}else{
			return payFields;
		}
		
		return payFields;
	}
	
	private static IPayFields save(String tableName, String[] data){
		IPayFields payFields = new IPayFields();
		if("lendmoney".equalsIgnoreCase(tableName)){
			payFields = lendmoneyTable(data);
		}
		
		return payFields;
	}
	
	private static IPayFields lendmoneyTable(String[] data){
		IPayFields payFields = new IPayFields();
		String sql = "SELECT * FROM lendmoney WHERE empid=? AND debitId=?";
		String params[] = new String[2];
		params[0] = data[0];
		params[1] = data[1];
		List<LendMoney> lends = LendMoney.retrieveLendMoney(sql, params);
		LendMoney lend = new LendMoney();
		int cnt=0;
		if(lends.size()>0){
			lend = lends.get(0);
			cnt=1;
		}else{
			lend = new LendMoney();
			Employee employee = new Employee();
			employee.setEmpId(Long.valueOf(data[0]));
			lend.setEmployee(employee);
			lend.setDebitId(Long.valueOf(data[1]));
			cnt=2;
		}
		lend.setDateIn(data[2]);
		lend.setDescription(data[3]);
		lend.setIsPaid(false);
		lend.setAmountIn(new BigDecimal(data[4]));
		
		if(cnt==1){payFields = collectData(LendMoney.insertData(lend, "3"));} //update data
		if(cnt==2){payFields = collectData(LendMoney.insertData(lend, "1"));} //new data
		
		return payFields;
	}
	
	private static IPayFields collectData(Object obj){
		IPayFields fld = new IPayFields();
		String[] parameters = new String[1];
		if(obj instanceof LendMoney){
			LendMoney lend = (LendMoney)obj;
			parameters[0] = lend.getLendId()+"";
			fld =  retrieveData("lendmoney", "SELECT * FROM lendmoney WHERE lendId=?", parameters).get(0);
		}
		return fld;
	}
	
	*//** 
	 * @param sqlStatements SQL statement
	 * @param parameters Prepared statement
	 * @return true if successfully processed
	 *//*
	private static boolean delete(String tableName, String[] data){
		boolean isSuccess=false;
		try{
			if("lendmoney".equalsIgnoreCase(tableName)){
				String sql = "SELECT * FROM lendmoney WHERE debitId=? ";
				String params[] = new String[1];
						params[0] = data[0];
				List<LendMoney> lends = LendMoney.retrieveLendMoney(sql, params);
				if(lends.size()>0){
					LendMoney lend = lends.get(0);
					lend.setDescription("deleted");
					LendMoney.updateData(lend);
					isSuccess = true;
				}
			}
		}catch(Exception ex){
			isSuccess = false;	
		}
		return isSuccess;
	}
	
	*//**
	 * 
	 * @param tableName
	 * @param sqlStatements
	 * @param parameters
	 * @return list of data
	 *//*
	public static List<IPayFields> retrieveData(String tableName, String sqlStatements, String[] parameters){
		List<IPayFields> fields = new ArrayList<>();
		ReadApplicationDetails db = new ReadApplicationDetails();
		
		if("yes".equalsIgnoreCase(db.getIncludeIpay())){
		
			if("lendmoney".equalsIgnoreCase(tableName)){
				for(LendMoney money : LendMoney.retrieveLendMoney(sqlStatements, parameters)){
					IPayFields pay = new IPayFields();
					
					pay.setF1(money.getLendId()+"");
					pay.setF2(money.getEmployee().getEmpId()+"");
					pay.setF3(money.getDescription());
					pay.setF4(money.getAmountIn()+"");
					pay.setF5(money.getProcessedBy());
					pay.setF6(money.getDateIn());
					pay.setF7(money.getDatePaid());
					pay.setF8(money.getIsPaid()==true? "true" : "false");
					pay.setF9(money.getDebitId()+"");
					pay.setF10(money.getTimestamp()+"");
				}
			}
			
		}
		
		return fields;
	}*/
	
}
