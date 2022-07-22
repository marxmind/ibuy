package com.italia.buynsell.utils;

import java.text.NumberFormat;

public class Currency {
	
	public static void main(String[] args) {
		String[] symbols = {"Php","php","PHP","$"};
		String value="$123.00";
		for(String symbol : symbols){
			value = value.replace(symbol, "");
		}
		System.out.println("value>> " + value);
	}
	
	public static String removeCurrencySymbol(String value, String replaceChr){
		String[] symbols = {"Php","php","PHP","$","\u20B1",",","₱","?"};
		if(value==null) return "0.00";
		for(String symbol : symbols){
			value = value.replace(symbol, replaceChr);
		}
		
		return value;
	}
	
	public static String removeComma(String value){
		value = value.replace(",", "");
		return value;
	}
	
	public static String formatAmount(double amount){
		return formatAmount(amount+"");
	}
	
	public static String formatAmount(String amount){
		if(amount==null) return "0";
		if(amount.isEmpty()) return "0";
		try{
		String[] symbols = {"Php","php","PHP","$",",","\u20B1","₱","?"};
		for(String symbol : symbols){
			amount = amount.replace(symbol, "");
		}
		double money = Double.valueOf(amount);
		
		NumberFormat format = NumberFormat.getCurrencyInstance();
		amount = format.format(money).replace("$", "");
		amount = amount.replace("Php", "");
		amount = amount.replace("₱", "");
		amount = amount.replace("?", "");
		}catch(Exception e){
			
		}
		return amount;
	}
	
	public static double amountDouble(String amount){
		if(amount == null) return 0d;
		if(amount.isEmpty()) return 0d;
		double amnt = 0d;
		amount = amount.replace(",", "");
		amnt = Double.valueOf(amount);
		return amnt;
	}
}
