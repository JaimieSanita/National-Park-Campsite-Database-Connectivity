package com.techelevator.projects.model.jdbc;

import java.sql.Date;
import java.util.ArrayList;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Department;
import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.EmployeeDAO;

public class JDBCEmployeeDAO implements EmployeeDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCEmployeeDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Employee> getAllEmployees() {
		//create employee object
		List<Employee> listOfAllEmployees = new ArrayList<Employee>();
		//sql query
		String query = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date " +
					    "FROM employee ";
		//sqlrowset
		SqlRowSet results = jdbcTemplate.queryForRowSet(query);
		//while(results)
		while(results.next()) {
		//convert row to object
		Employee employee = mapRowToEmployee(results);
		listOfAllEmployees.add(employee);
		}
		return listOfAllEmployees;
	}

	@Override
	public List<Employee> searchEmployeesByName(String firstNameSearch, String lastNameSearch) {
		List<Employee> employeesByName = new ArrayList<Employee>();
		
		//query
		String query = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date " +
						"FROM employee " + 
						"WHERE first_name = ? AND last_name = ?";
		//execute query
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query, firstNameSearch, lastNameSearch);
		//while loop
		while(results.next()) {
			Employee employeeNew = mapRowToEmployee(results);
			//convert a row into w2
			employeesByName.add(employeeNew);
		}
		return employeesByName;
		
	}

	@Override
	public List<Employee> getEmployeesByDepartmentId(long id) {
		
		List<Employee> employeesByDepartment = new ArrayList<Employee>();
		
		String query = "SELECT employee_id, department_id, first_name, last_name, birth_date, gender, hire_date " +
					    "FROM employee " +
					    "WHERE department_id = ?";
		
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query, id);
		
		while(results.next()) {
			Employee employeeNewest = mapRowToEmployee(results);
			employeesByDepartment.add(employeeNewest);
			
		}
		return employeesByDepartment;
	}

	@Override
	public List<Employee> getEmployeesWithoutProjects() {
		List<Employee> employeesWithoutProjects = new ArrayList<Employee>();
		String query ="SELECT * " + 
				"FROM employee AS e " + 
				"JOIN project_employee AS pe ON e.employee_id = pe.employee_id " + 
				"WHERE pe.project_id IS NULL";
		
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query);
		
		while(results.next()) {
			Employee boredEmployee = mapRowToEmployee(results);
			employeesWithoutProjects.add(boredEmployee);
		}
		return employeesWithoutProjects;
	}

	@Override
	public List<Employee> getEmployeesByProjectId(Long projectId) {
		List<Employee> employeesByProjectId = new ArrayList<Employee>();
		String query = "SELECT * " + 
				"FROM employee AS e " + 
				"JOIN project_employee AS pe ON e.employee_id = pe.employee_id " + 
				"WHERE pe.project_id = ?";
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query, projectId);
		
		while(results.next()) {
			Employee employee = mapRowToEmployee(results);
			employeesByProjectId.add(employee);
		}
		return employeesByProjectId;
	}


	@Override
	public void changeEmployeeDepartment(Long employeeId, Long departmentId) {
		String query = "UPDATE INTO employee " +
					    "SET department_id = ? " +
						"WHERE employee_id = ?";
		this.jdbcTemplate.update(query, departmentId, employeeId);
	}
	
	private Employee mapRowToEmployee(SqlRowSet results) {
		Employee employee = new Employee();
		employee.setId(results.getLong("employee_id"));
		employee.setDepartmentId(results.getLong("department_id"));
		employee.setFirstName(results.getString("first_name")); 
		employee.setLastName(results.getString("last_name")); 
		Date birthDate = results.getDate("birth_date");
		if(birthDate != null) {
			employee.setBirthDay(birthDate.toLocalDate());
		}
		employee.setGender(results.getString("gender").charAt(0)); //takes first character
		Date hireDate = results.getDate("hire_date");
		if(hireDate != null) {
			employee.setHireDate(hireDate.toLocalDate());
		}
		
		
		return employee;
	}
	


}
