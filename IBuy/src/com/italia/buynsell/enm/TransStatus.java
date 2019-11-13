package com.italia.buynsell.enm;

public enum TransStatus {

	UNPAID("status", "1", "UNPAID"),
	PAID("status", "2", "PAID"),
	RETURN("status", "3", "RETURN"),
	
	CASH_ADVANCE("transtype", "1", "CASH ADVANCE"),
	RENT_ITEMS("transtype", "2", "RENT ITEMS"),
	LOAN("transtype", "3", "LOAN"),
	MORTGAGE("transtype", "4", "MORTGAGE"),
	TRACKING("transtype", "5", "TRACKING"),
	DEPOSIT("transtype", "6", "DEPOSIT"),
	SHELLER("transtype", "7", "SHELLER"),
	HAULING("transtype", "8", "HAULING"),
	
	ON_TIME_PAYER("clientrate", "1", "ON TIME PAYER"),
	DELAYED_PAYER("clientrate", "2", "DELAYED PAYER"),
	PROMISER("clientrate", "3", "PROMISER");
	
	
	private String type;
	private String code;
	private String name;
	
	private TransStatus(String type, String code, String name){
		this.type = type;
		this.code = code;
		this.name = name; 
	}
	
	/**
	 * 
	 * @param type @status @transtype @clientrate
	 * @param id 1,2,3
	 * @return equal value name
	 */
	public static String statusCodeToMeaning(String type, String id){
		
		for(TransStatus a : TransStatus.values()){
			if(type.equalsIgnoreCase(a.getType()) && id.equalsIgnoreCase(a.getCode())){
				return a.getName();
			}
		}
		
		return id;
	}
	
	/**
	 * 
	 * @param type @status @transtype @clientrate
	 * @param name status name
	 * @return equal value code
	 */
	public static String statusNameToCode(String type, String name){
		
		for(TransStatus a : TransStatus.values()){
			if(type.equalsIgnoreCase(a.getType()) && name.equalsIgnoreCase(a.getName())){
				return a.getCode();
			}
		}
		
		return name;
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public static String statusCode(String name){
		
		for(TransStatus a : TransStatus.values()){
			if(name.equalsIgnoreCase(a.getName())){
				return a.getCode();
			}
		}
		
		return name;
	}
	
	public String getType() {
		return type;
	}
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	
}
