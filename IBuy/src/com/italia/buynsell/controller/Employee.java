package com.italia.buynsell.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.italia.buynsell.dao.DataConnectIPayDAO;
/**
 * 
 * @Please see employees class
 *
 */
@Deprecated
public class Employee {

	private Long empId;
	private String firstName;
	private String middleName;
	private String lastName;
	
	public static List<Employee> retrieveEmployees(String sql, String[] params){
		
		List<Employee> employeeList = new ArrayList<>();
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try{
		conn = DataConnectIPayDAO.getConnection();
		ps = conn.prepareStatement(sql);
		
		if(params!=null && params.length>0){
			
			for(int i=0; i<params.length; i++){
				ps.setString(i+1, params[i]);
			}
			
		}
		rs = ps.executeQuery();
		
		while(rs.next()){
			Employee e = new Employee();
			e.setEmpId(rs.getLong("empId"));
			e.setFirstName(rs.getString("firstName"));
			e.setMiddleName(rs.getString("middleName"));
			e.setLastName(rs.getString("lastName"));
			employeeList.add(e);
		}
		
		rs.close();
		ps.close();
		DataConnectIPayDAO.close(conn);
		}catch(SQLException sq){}
		
		return employeeList;
		
	}
	
	
	public Long getEmpId() {
		return empId;
	}

	public void setEmpId(Long empId) {
		this.empId = empId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	
	
	
	
	
}