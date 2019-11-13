package com.italia.buynsell.controller;

public enum CornConditions {

	WCD_NATIVE("1","WCD(NATIVE ONLY)"),
	WHITE_WET("2","WHITE WET"),
	WHITE_BILOG("3","WHITE BILOG"),
	YELLOW_DRY("4","YELLOW DRY"),
	YELLOW_BASA("5","YELLOW BASA"),
	YELLOW_BILOG("6","YELLOW BILOG"),
	WHITE_SEMI_BASA("7","WHITE SEMI BASA"),
	YELLOW_SEMI_BASA("8","YELLOW SEMI BASA");
	
	private String name;
	private String code;
	private CornConditions(String code,String name){
		this.name = name;
		this.code = code;
	}
	
	public static String typeName(String id){
		for(CornConditions type : CornConditions.values()){
			if(id.equalsIgnoreCase(type.getCode())){
				return type.getName();
			}
		}
		return CornConditions.WCD_NATIVE.getName();
	}
	public static String typeId(String name){
		for(CornConditions type : CornConditions.values()){
			if(name.equalsIgnoreCase(type.getName())){
				return type.getName();
			}
		}
		return CornConditions.WCD_NATIVE.getName();
	}
	
	public String getCode(){
		return code;
	}
	public String getName(){
		return name;
	}
	
}
