package com.techelevator.projects.model.jdbc;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.Employee;

public class JDBCProjectTest {

	private static SingleConnectionDataSource dataSource;

	// CONSTANTS
	private static final Long PROJECT_ID = (long) 123456789;
	private static final Long PROJECT_ID2 = (long) 123456788;

	private static final String PROJECT_NAME = "Dummy Project";
	private static final String PROJECT_NAME2 = "Dumbest Project";

	private static final Date PROJECT_FROM_DATE = Date.valueOf("2015-11-23");
	private static final Date PROJECT_FROM_DATE2 = Date.valueOf("2010-03-06");

	private static final Date PROJECT_TO_DATE = Date.valueOf("2020-01-05");
	private static final Date PROJECT_TO_DATE2 = null;

	private static final Long EMPLOYEE_ID = (long) 234567891;
	private static final Long EMPLOYEE_ID2 = (long) 234567892;

	private static final String EMPLOYEE_FIRST_NAME = "Emerson";
	private static final String EMPLOYEE_FIRST_NAME2 = "Nora";

	private static final String EMPLOYEE_LAST_NAME = "Sanita";
	private static final String EMPLOYEE_LAST_NAME2 = "Matos";

	private static final LocalDate EMPLOYEE_BIRTHDATE = LocalDate.of(2018, 07, 18);
	private static final LocalDate EMPLOYEE_BIRTHDATE2 = LocalDate.of(2020, 03, 22);

	private static final char EMPLOYEE_GENDER = 'M';
	private static final char EMPLOYEE_GENDER2 = 'F';

	private static final LocalDate EMPLOYEE_HIRE_DATE = LocalDate.of(2018, 8, 18);
	private static final LocalDate EMPLOYEE_HIRE_DATE2 = LocalDate.of(2020, 05, 17);

	private JDBCProjectDAO dao;
	private JDBCEmployeeDAO daoEmployee;

	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}

	@Before
	public void setup() {

		// DELETE ALL TABLES clean database
		String sqlDeleteAllTables = "DELETE FROM project_employee;" + "DELETE FROM employee;"
				+ "DELETE FROM department;" + "DELETE FROM project;";
		// INSERT fake projects into project table
		String sqlInsertProject = "INSERT INTO project (project_id, name, from_date, to_date) VALUES (?, ?, ?, ?)";

		// INSERT fake employee into employee table
		String sqlInsertEmployee = "INSERT INTO employee (employee_id, first_name, last_name, birth_date, gender, hire_date ) "
				+ "VALUES (?,?,?,?,?,?)";
		// INSERT fake join
		// String sqlInsertJoin = "INSERT INTO project_employee (project_id,
		// employee_id) VALUES(?, ?)";

		// Construct our template object
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		// delete all tables
		jdbcTemplate.update(sqlDeleteAllTables);

		// insert project1
		jdbcTemplate.update(sqlInsertProject, PROJECT_ID, PROJECT_NAME, PROJECT_FROM_DATE, PROJECT_TO_DATE);
		// insert project2
		jdbcTemplate.update(sqlInsertProject, PROJECT_ID2, PROJECT_NAME2, PROJECT_FROM_DATE2, PROJECT_TO_DATE2);

		// insert employee1
		jdbcTemplate.update(sqlInsertEmployee, EMPLOYEE_ID, EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME, EMPLOYEE_BIRTHDATE,
				EMPLOYEE_GENDER, EMPLOYEE_HIRE_DATE);

		// insert employee2
		jdbcTemplate.update(sqlInsertEmployee, EMPLOYEE_ID2, EMPLOYEE_FIRST_NAME2, EMPLOYEE_LAST_NAME2,
				EMPLOYEE_BIRTHDATE2, EMPLOYEE_GENDER2, EMPLOYEE_HIRE_DATE2);

		this.dao = new JDBCProjectDAO(dataSource);
		this.daoEmployee = new JDBCEmployeeDAO(dataSource);
	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void returns_all_projects() {
		List<Project> actualList = this.dao.getAllActiveProjects();

		Assert.assertNotNull("The list should return all active projects", actualList);
		Assert.assertEquals("There should only be one active project.", 1, actualList.size());
	}

	@Test
	public void removes_employee_from_project() {
		dao.addEmployeeToProject(PROJECT_ID, EMPLOYEE_ID);
		dao.addEmployeeToProject(PROJECT_ID, EMPLOYEE_ID2);
		dao.removeEmployeeFromProject(PROJECT_ID, EMPLOYEE_ID);
		List<Employee> actual = this.daoEmployee.getEmployeesByProjectId(PROJECT_ID);
		Assert.assertEquals("One employee should be removed from project.", 1, actual.size());
	}

	@Test
	public void adds_employee_to_project() {

		dao.addEmployeeToProject(PROJECT_ID, EMPLOYEE_ID);
		dao.addEmployeeToProject(PROJECT_ID, EMPLOYEE_ID2);

		List<Employee> actual = this.daoEmployee.getEmployeesByProjectId(PROJECT_ID);

		Assert.assertNotNull("The list include employees.", actual);
		Assert.assertEquals("Two employees should have been added.", 2, actual.size());
	}

}
