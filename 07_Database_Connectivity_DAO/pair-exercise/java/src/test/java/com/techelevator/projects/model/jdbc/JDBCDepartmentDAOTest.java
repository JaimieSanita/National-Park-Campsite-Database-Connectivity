package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.SQLException;
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
		String sqlInsertDepartment = "INSERT INTO department (department_id, name) VALUES (?, 'Best SQL Team')";

		// Construct our template object
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		// insert the department
		jdbcTemplate.update(sqlInsertDepartment); // <- notice we pass in the country code constant as a
																// parameter)

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
	

	

}
