package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import com.italia.buynsell.dao.DataConnectDAO;
import com.italia.buynsell.utils.DateUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 
 * @author mark italia
 * @since 09/28/2016
 * @version 1.0
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ApplicationFixes {

	private Long fixid;
	private String fixdesc;
	private ApplicationVersionController versionController;
	private Timestamp timestamp;
	
	public static List<ApplicationFixes> retrieve(String sql, String[] params){
		List<ApplicationFixes> data = new ArrayList<>();//Collections.synchronizedList(new ArrayList<>());
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		
		rs = ps.executeQuery();
		
		while(rs.next()){
			ApplicationFixes app = new ApplicationFixes();
			try{app.setFixid(rs.getLong("fixid"));}catch(NullPointerException e){}
			try{app.setFixdesc(rs.getString("fixdesc"));}catch(NullPointerException e){}
			try{
				ApplicationVersionController versionController = new ApplicationVersionController();
				versionController.setBuildid(rs.getLong("buildid"));
				app.setVersionController(versionController);}catch(NullPointerException e){}
			try{app.setTimestamp(rs.getTimestamp("timestamp"));}catch(NullPointerException e){}
			data.add(app);
		}
		rs.close();
		ps.close();
		DataConnectDAO.close(conn);
		}catch(Exception e){
			data.add(errorProvider());
		}
		return data;
	}
	
	private static ApplicationFixes errorProvider() {
		ApplicationFixes app = ApplicationFixes.builder()
				.fixid(1l)
				.fixdesc("local fixes")
				.versionController(new ApplicationVersionController().builder().buildid(1l).buildversion("1.0").buildapplieddate(DateUtils.getCurrentDateYYYYMMDD()).build())
				.build();
				return app;
	}
	
}
