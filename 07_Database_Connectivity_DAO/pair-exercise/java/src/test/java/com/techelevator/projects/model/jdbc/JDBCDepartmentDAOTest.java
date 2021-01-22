package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Department;



public class JDBCDepartmentDAOTest {
	
	//may need private static final variable
	
	private static SingleConnectionDataSource dataSource;
	
	//CONSTANTS
	private static final Long DEPARTMENT_ID = (long) 123456789;
	private static final Long DEPARTMENT_ID2 = (long) 123456788;
	private static final String DEPARTMENT_NAME = "Dummy Department";
	private static final String DEPARTMENT_NAME2 = "Dumbest Department";
	
	private JDBCDepartmentDAO dao;
	
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		/*
		 * The following line disables autocommit for connections returned by this
		 * DataSource. This allows us to rollback any changes after each test
		 */
		dataSource.setAutoCommit(false);
	}
	
	@AfterClass
	public static void closeDataSource() throws SQLException {
		dataSource.destroy();
	}
	
	@Before
	public void setup() {
		// INSERT a fake department into the deparment table
		// that way our tests don't have to depend on data already in the database
		// we make a clean insert each time
		String sqlDeleteAllTables = "DELETE FROM project_employee;" + 
				"DELETE FROM employee;" + 
				"DELETE FROM department;" + 
				"DELETE FROM project;";
		
		
		String sqlInsertDepartment = "INSERT INTO department (department_id, name) VALUES (?, ?)";
		

		// Construct our template object
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		jdbcTemplate.update(sqlDeleteAllTables);
		// insert the department
		jdbcTemplate.update(sqlInsertDepartment, DEPARTMENT_ID, DEPARTMENT_NAME); // <- notice we pass in the country code constant as a
		jdbcTemplate.update(sqlInsertDepartment, DEPARTMENT_ID2, DEPARTMENT_NAME2);

		// setup my dao field, so that my tests have a valid DAO to work with
		this.dao = new JDBCDepartmentDAO(dataSource);
	}

	/*
	 * After each test, we rollback any changes that were made to the database so
	 * that everything is clean for the next test
	 */
	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}
	
	@Test
	public void all_departments_are_fetched() {
		//ARRANGE; delete all entries
		
		//ACT
		List<Department> actual = this.dao.getAllDepartments();
		//ASSERT
		Assert.assertNotNull("The list should not be empty.", actual);
		Assert.assertEquals(2, actual.size()); //should get back 1 department from list
	}
	
	@Test
	public void returns_department_by_name() {
		List<Department> actual = this.dao.searchDepartmentsByName(DEPARTMENT_NAME);
		
		
		Department expectedDepartment = new Department();
		expectedDepartment.setId(DEPARTMENT_ID);
		expectedDepartment.setName(DEPARTMENT_NAME);
		
		Assert.assertNotNull(actual);
		Assert.assertEquals(DEPARTMENT_NAME, actual.get(0).getName());
		Department savedDepartment = actual.get(0);
		assertDepartmentsAreEqual(expectedDepartment, savedDepartment);
	}
	
	@Test
	public void saves_department() {
		String newName = "The Dumbest Name Possible";
		Department updatedDepartment = new Department();
		updatedDepartment.setName(newName);
		updatedDepartment.setId(DEPARTMENT_ID);
		
		//act
		dao.saveDepartment(updatedDepartment);
		
		Department actual = dao.getDepartmentById(DEPARTMENT_ID);
		
		assertDepartmentsAreEqual(updatedDepartment, actual);
		
	}
	
	private void assertDepartmentsAreEqual(Department expected, Department actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getName(), actual.getName());
	}
	

	

}
