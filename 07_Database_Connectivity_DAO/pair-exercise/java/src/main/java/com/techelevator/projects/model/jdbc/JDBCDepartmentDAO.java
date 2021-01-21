package com.techelevator.projects.model.jdbc;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.DepartmentDAO;
import com.techelevator.projects.model.Project;

public class JDBCDepartmentDAO implements DepartmentDAO {
	
	private JdbcTemplate jdbcTemplate;

	public JDBCDepartmentDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<Department> getAllDepartments() {
		List<Department> getAllDepartments = new ArrayList<Department>();
		String query = "SELECT department_id, name FROM department";
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query);
		
		while(results.next()) {
			Department allDepartments = mapRowToDepartment(results);
			getAllDepartments.add(allDepartments);
		}
		
		return getAllDepartments;
	}

	@Override
	public List<Department> searchDepartmentsByName(String nameSearch) {
		List<Department> departmentsByName = new ArrayList<Department>();
		String query = "SELECT department_id, name FROM department WHERE name = ?";
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query, nameSearch);
		
		while(results.next()) {
			Department department = mapRowToDepartment(results);
			departmentsByName.add(department);		}
		return departmentsByName;
	}

	@Override
	public void saveDepartment(Department updatedDepartment) {
		
		String query = "UPDATE department SET name = ? WHERE department_id = ? ";
		this.jdbcTemplate.update(query, updatedDepartment.getName(), updatedDepartment.getId());
		
	}

	@Override
	public Department createDepartment(Department newDepartment) {
		String newDepartmentReturning = "INSERT INTO department (name) VALUES(?) RETURNING department_id, name";
		SqlRowSet results = jdbcTemplate.queryForRowSet(newDepartmentReturning, newDepartment.getName());
		results.next();
		newDepartment.setId(results.getLong("department_id")); //returns a department
		return newDepartment;
	}

	@Override
	public Department getDepartmentById(Long id) {
		String query = "SELECT department_id, name FROM department WHERE department_id = ?";
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query, id);
		
		results.next();
			Department department = new Department();
			department.setId(results.getLong("department_id"));
			
		
		return department;
	}
	
	
	
	private Department mapRowToDepartment(SqlRowSet results) {
		Department department = new Department();
		department.setId(results.getLong("department_id"));
		department.setName(results.getString("name"));
		
		
		return department;
}
}
