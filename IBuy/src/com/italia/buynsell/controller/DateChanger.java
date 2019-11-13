package com.italia.buynsell.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.italia.buynsell.ei.EICashOut;
import com.italia.buynsell.ei.EICashPay;
import com.italia.buynsell.ei.EICashReturn;

public class DateChanger {

	
	public static void main(String[] args) {
		DateChanger.runDateChanger();
		
	}
	
	public static void runDateChanger(){
		ReadApplicationDetails r = new ReadApplicationDetails();
        String logpath = r.getLogPath();
        
		String sql = "SELECT * FROM cashin";
		String mm="",dd="",yy="", date="";
		BufferedWriter writer = null;
		try{
			File file = new File(logpath+"cash.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(CashIn in : CashIn.retrieveCashIn(sql, new String[0])){
			date = in.getDateIn();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE cashin set datein='" + yy +"-" + mm + "-" + dd +"'  WHERE cashinId=" + in.getId() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "SELECT *  FROM purchasingcorn";
		try{
			File file = new File(logpath+"purchasingcorn.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(PurchasingCorn corn : PurchasingCorn.retrievePurchasingCorn(sql, new String[0])){
			date = corn.getDateIn();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE purchasingcorn set datein='" + yy +"-" + mm + "-" + dd +"'  WHERE chasedid=" + corn.getChasedId() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "SELECT * FROM purchased";
		try{
			File file = new File(logpath+"purchased.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(Purchased pur : Purchased.retrievePurchased(sql, new String[0])){
			date = pur.getDateIn();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE purchased set dateIn='" + yy +"-" + mm + "-" + dd +"'  WHERE pureId=" + pur.getId() +";\n");
		}
		
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "SELECT * FROM expenses";
		try{
			File file = new File(logpath+"expenses.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(Expenses ex : Expenses.retrieveExpenses(sql, new String[0])){
			date = ex.getDateIn();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE expenses set dateIn='" + yy +"-" + mm + "-" + dd +"'  WHERE exeId=" + ex.getId() +";\n");
		}
		
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "SELECT * FROM credit";
		try{
			File file = new File(logpath+"credit.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(Credit cr : Credit.retrieveCredit(sql, new String[0])){
			date = cr.getDateIn();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE credit set dateIn='" + yy +"-" + mm + "-" + dd +"'  WHERE creditId=" + cr.getId() +";\n");
		}
		
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "SELECT * FROM debit";
		try{
			File file = new File(logpath+"credit.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(Debit d : Debit.retrieveDebit(sql, new String[0])){
			date = d.getDateIn();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE debit set dateIn='" + yy +"-" + mm + "-" + dd +"'  WHERE debitId=" + d.getId() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "select * from clientprofile";
		try{
			File file = new File(logpath+"clientprofile.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(ClientProfile d : ClientProfile.retrieveClientProfile(sql, new String[0])){
			date = d.getRegisteredDate();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE clientprofile set registeredDate='" + yy +"-" + mm + "-" + dd +"'  WHERE clientId=" + d.getClientId() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "select * from client_trans";
		try{
			File file = new File(logpath+"client_trans.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(ClientTransactions d : ClientTransactions.retrieveClientTransacts(sql, new String[0])){
			date = d.getTransDate();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE client_trans set transDate='" + yy +"-" + mm + "-" + dd +"'  WHERE transId=" + d.getTransId() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "select * from ei_cash_out";
		try{
			File file = new File(logpath+"ei_cash_out.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(EICashOut d : EICashOut.retrieveCashOut(sql, new String[0])){
			date = d.getDateOut();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE ei_cash_out set dateOut='" + yy +"-" + mm + "-" + dd +"'  WHERE idOut=" + d.getIdOut() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "select * from ei_cash_payment";
		try{
			File file = new File(logpath+"ei_cash_payment.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(EICashPay d : EICashPay.retrieveCashPay(sql, new String[0])){
			date = d.getDatePay();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE ei_cash_payment set datePay='" + yy +"-" + mm + "-" + dd +"'  WHERE idPay=" + d.getIdPay() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
		sql = "select * from ei_cash_return";
		try{
			File file = new File(logpath+"ei_cash_return.sql");
			writer = new BufferedWriter(new FileWriter(file));
		for(EICashReturn d : EICashReturn.retrieveCashReturn(sql, new String[0])){
			date = d.getDateReturn();
			mm = date.split("-")[0];
			dd = date.split("-")[1];
			yy = date.split("-")[2];
			
			writer.write("UPDATE ei_cash_return set dateReturn='" + yy +"-" + mm + "-" + dd +"'  WHERE idRet=" + d.getIdRet() +";\n");
		}
		}catch(IOException e){
			
		}finally{
			try{
				if(writer!=null){
					writer.close();
				}
			}catch(IOException e){}
		}
		
	}
	
}
