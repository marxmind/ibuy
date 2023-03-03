package com.italia.buynsell.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import com.italia.buynsell.controller.ApplicationFixes;
import com.italia.buynsell.controller.ApplicationVersionController;
import com.italia.buynsell.controller.Copyright;

import lombok.Data;

/**
 * 
 * @author mark italia
 * @since 09/28/2016
 * @version 1.0
 */
@Named
@ViewScoped
@Data
public class VersionBean implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = 134357657547547L;
	private ApplicationVersionController versionController;
	private ApplicationFixes applicationFixes;
	private List<ApplicationFixes> fixes = new ArrayList<ApplicationFixes>();
	private Copyright copyright;
	
	@PostConstruct
	public void init(){
		//InitDB.getInstance().setPathFileLocation(GlobalVar.APP_DATABASE_CONF); 
		try {
		String sql = "SELECT * FROM app_version_control ORDER BY timestamp DESC LIMIT 1";
		String[] params = new String[0];
		versionController = ApplicationVersionController.retrieve(sql, params).get(0);
		
		sql = "SELECT * FROM copyright ORDER BY id desc limit 1";
		params = new String[0];
		copyright = Copyright.retrieve(sql, params).get(0);
		
		fixes = new ArrayList<ApplicationFixes>();
		sql = "SELECT * FROM buildfixes WHERE buildid=?";
		params = new String[1];
		params[0] = versionController.getBuildid()+"";
		fixes = ApplicationFixes.retrieve(sql, params);
		}catch(Exception e){}
		
	}
	
}
