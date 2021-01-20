package com.techelevator.projects.model.jdbc;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.techelevator.projects.model.Employee;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		List<Project> allActiveProjects = new ArrayList<Project>(); //wth<>
		String query = "SELECT project_id, name, from_date, to_date " +
						"FROM project " +
						"WHERE from_date IS NOT NULL AND to_date IS NULL";
		SqlRowSet results = this.jdbcTemplate.queryForRowSet(query);
		while(results.next()) {
			Project project = mapRowToProject(results);
			allActiveProjects.add(project);
		}
		return allActiveProjects;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String query = "DELETE FROM project_employee WHERE project_id = ? AND employee_id = ?";
		 this.jdbcTemplate.update(query, projectId, employeeId);

	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String query = "INSERT INTO project_employee (project_id, employee_id) VALUES (?, ?)";
		this.jdbcTemplate.update(query, projectId, employeeId);
	}
	
	private Project mapRowToProject(SqlRowSet results) {
		Project project = new Project();
		project.setId(results.getLong("project_id"));
		project.setName(results.getString("name"));
	
		Date startDate = results.getDate("from_date");
		if(startDate != null) {
			project.setStartDate(startDate.toLocalDate());
		}
		
		Date endDate = results.getDate("to_date");
		if(endDate != null) {
			project.setEndDate(endDate.toLocalDate());
		}
		
		return project;
		
		
	}

}
