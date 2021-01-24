package com.techelevator.projects.model.jdbc;

import static org.junit.Assert.assertEquals;

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
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.techelevator.projects.model.Employee;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JDBCEmployeeDAOTest {

	private static SingleConnectionDataSource dataSource;

	// CONSTANTS
	private static final Long PROJECT_ID = (long) 123456789;
	private static final Long PROJECT_ID2 = (long) 123456788;
	private static final Long PROJECT_ID3 = (long) 0;

	private static final String PROJECT_NAME = "Dummy Project";
	private static final String PROJECT_NAME2 = "Dumbest Project";
	private static final String PROJECT_NAME3 = "Dumbiest Project";

	private static final Date PROJECT_FROM_DATE = Date.valueOf("2015-11-23");
	private static final Date PROJECT_FROM_DATE2 = Date.valueOf("2010-03-06");

	private static final Date PROJECT_TO_DATE = Date.valueOf("2020-01-05");
	private static final Date PROJECT_TO_DATE2 = null;

	private static final Long EMPLOYEE_ID = (long) 234567891;
	private static final Long EMPLOYEE_ID2 = (long) 234567892;
	private static final Long EMPLOYEE_ID3 = (long) 234567893;

	private static final Long DEPARTMENT_ID = (long) 456789123;
	private static final Long DEPARTMENT_ID2 = (long) 456789124;
	private static final Long DEPARTMENT_ID3 = (long) 456789125;

	private static final String DEPARTMENT_NAME = "Dummy Department";
	private static final String DEPARTMENT_NAME2 = "Dumbest Department";
	private static final String DEPARTMENT_NAME3 = "Dumbiest Department";

	private static final String EMPLOYEE_FIRST_NAME = "Emerson";
	private static final String EMPLOYEE_FIRST_NAME2 = "Nora";
	private static final String EMPLOYEE_FIRST_NAME3 = "Sharon";

	private static final String EMPLOYEE_LAST_NAME = "Sanita";
	private static final String EMPLOYEE_LAST_NAME2 = "Matos";
	private static final String EMPLOYEE_LAST_NAME3 = "Stone";

	private static final LocalDate EMPLOYEE_BIRTHDATE = LocalDate.of(2018, 07, 18);
	private static final LocalDate EMPLOYEE_BIRTHDATE2 = LocalDate.of(2020, 03, 22);
	private static final LocalDate EMPLOYEE_BIRTHDATE3 = LocalDate.of(2016, 11, 02);

	private static final char EMPLOYEE_GENDER = 'M';
	private static final char EMPLOYEE_GENDER2 = 'F';
	private static final char EMPLOYEE_GENDER3 = 'F';

	private static final LocalDate EMPLOYEE_HIRE_DATE = LocalDate.of(2018, 8, 18);
	private static final LocalDate EMPLOYEE_HIRE_DATE2 = LocalDate.of(2020, 05, 17);
	private static final LocalDate EMPLOYEE_HIRE_DATE3 = LocalDate.of(2019, 9, 25);

	private JDBCEmployeeDAO dao;

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

		// INSERT fake department into department table
		String sqlInsertDepartment = "INSERT INTO department (department_id, name) VALUES (?, ?)";
		// INSERT fake employee into employee table
		String sqlInsertEmployee = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, gender, hire_date ) "
				+ "VALUES (?,?,?,?,?,?,?)";
		// INSERT fake join
		String sqlInsertJoin = "INSERT INTO project_employee (project_id, employee_id) VALUES(?, ?)";

		// Construct our template object
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		// delete all tables
		jdbcTemplate.update(sqlDeleteAllTables);

		// insert project1
		jdbcTemplate.update(sqlInsertProject, PROJECT_ID, PROJECT_NAME, PROJECT_FROM_DATE, PROJECT_TO_DATE);
		// insert project2
		jdbcTemplate.update(sqlInsertProject, PROJECT_ID2, PROJECT_NAME2, PROJECT_FROM_DATE2, PROJECT_TO_DATE2);
		// insert project3=0
		jdbcTemplate.update(sqlInsertProject, PROJECT_ID3, PROJECT_NAME3, PROJECT_FROM_DATE2, PROJECT_TO_DATE2);

		// insert department1
		jdbcTemplate.update(sqlInsertDepartment, DEPARTMENT_ID, DEPARTMENT_NAME);
		// insert department2
		jdbcTemplate.update(sqlInsertDepartment, DEPARTMENT_ID2, DEPARTMENT_NAME2);
		// insert department3
		jdbcTemplate.update(sqlInsertDepartment, DEPARTMENT_ID3, DEPARTMENT_NAME3);
		// insert employee1
		jdbcTemplate.update(sqlInsertEmployee, EMPLOYEE_ID, DEPARTMENT_ID, EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME,
				EMPLOYEE_BIRTHDATE, EMPLOYEE_GENDER, EMPLOYEE_HIRE_DATE);

		// insert employee2
		jdbcTemplate.update(sqlInsertEmployee, EMPLOYEE_ID2, DEPARTMENT_ID2, EMPLOYEE_FIRST_NAME2, EMPLOYEE_LAST_NAME2,
				EMPLOYEE_BIRTHDATE2, EMPLOYEE_GENDER2, EMPLOYEE_HIRE_DATE2);

		// insert employee3
		jdbcTemplate.update(sqlInsertEmployee, EMPLOYEE_ID3, DEPARTMENT_ID2, EMPLOYEE_FIRST_NAME3, EMPLOYEE_LAST_NAME3,
				EMPLOYEE_BIRTHDATE3, EMPLOYEE_GENDER3, EMPLOYEE_HIRE_DATE3);

		// insert fake join
		jdbcTemplate.update(sqlInsertJoin, PROJECT_ID, EMPLOYEE_ID);
		jdbcTemplate.update(sqlInsertJoin, PROJECT_ID, EMPLOYEE_ID2);
		jdbcTemplate.update(sqlInsertJoin, PROJECT_ID3, EMPLOYEE_ID3);

		this.dao = new JDBCEmployeeDAO(dataSource);
	}

	@After
	public void rollback() throws SQLException {
		dataSource.getConnection().rollback();
	}

	@Test
	public void returns_all_employees() {

		List<Employee> actualList = this.dao.getAllEmployees();

		Assert.assertNotNull("All employees should be displayed.", actualList);
		Assert.assertEquals("There should only be two employees.", 3, actualList.size());

	}

	@Test
	public void returns_employee_by_name() {
		List<Employee> actual = this.dao.searchEmployeesByName(EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME);

		Employee expectedEmployee = getEmployee(EMPLOYEE_ID, DEPARTMENT_ID, EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME,
				EMPLOYEE_BIRTHDATE, EMPLOYEE_GENDER, EMPLOYEE_HIRE_DATE);
		Employee actualEmployee = actual.get(0);

		Assert.assertNotNull("An employee should be returned.", actual);
		Assert.assertEquals(1, actual.size());
		assertEmployeesAreEqual(expectedEmployee, actualEmployee);
	}

	@Test
	public void returns_employees_by_department_id() {

		List<Employee> actual = this.dao.getEmployeesByDepartmentId(DEPARTMENT_ID2);
		Employee actualEmployee = actual.get(0);

		Employee expectedEmployee = getEmployee(EMPLOYEE_ID2, DEPARTMENT_ID2, EMPLOYEE_FIRST_NAME2, EMPLOYEE_LAST_NAME2,
				EMPLOYEE_BIRTHDATE2, EMPLOYEE_GENDER2, EMPLOYEE_HIRE_DATE2);

		Assert.assertNotNull("An employee should be returned by department id.", actual);
		Assert.assertEquals("There should only be one employee returned.", 2, actual.size());
		assertEmployeesAreEqual(expectedEmployee, actualEmployee);
	}

	@Test
	public void returns_employees_without_projects() {

		List<Employee> actual = this.dao.getEmployeesWithoutProjects();
		Employee actualEmployee = actual.get(0);

		Employee expectedEmployee = getEmployee(EMPLOYEE_ID3, DEPARTMENT_ID2, EMPLOYEE_FIRST_NAME3, EMPLOYEE_LAST_NAME3,
				EMPLOYEE_BIRTHDATE3, EMPLOYEE_GENDER3, EMPLOYEE_HIRE_DATE3);

		assertEmployeesAreEqual(expectedEmployee, actualEmployee);

	}

	@Test
	public void employee_can_change_departments() {

		this.dao.changeEmployeeDepartment(EMPLOYEE_ID3, DEPARTMENT_ID3);

		List<Employee> actual = this.dao.getEmployeesByDepartmentId(DEPARTMENT_ID3);
		Employee actualEmployee = actual.get(0);

		Employee expectedEmployee = getEmployee(EMPLOYEE_ID3, DEPARTMENT_ID3, EMPLOYEE_FIRST_NAME3, EMPLOYEE_LAST_NAME3,
				EMPLOYEE_BIRTHDATE3, EMPLOYEE_GENDER3, EMPLOYEE_HIRE_DATE3);

		assertEmployeesAreEqual(expectedEmployee, actualEmployee);
	}

	@Test
	public void returns_employee_by_project_id() {
		List<Employee> actual = this.dao.getEmployeesByProjectId(PROJECT_ID);
		Employee actualEmployee = actual.get(0);
		Employee secondActual = actual.get(1);
		Employee expectedEmployee = getEmployee(EMPLOYEE_ID, DEPARTMENT_ID, EMPLOYEE_FIRST_NAME, EMPLOYEE_LAST_NAME,
				EMPLOYEE_BIRTHDATE, EMPLOYEE_GENDER, EMPLOYEE_HIRE_DATE);
		Employee secondExpected = getEmployee(EMPLOYEE_ID2, DEPARTMENT_ID2, EMPLOYEE_FIRST_NAME2, EMPLOYEE_LAST_NAME2,
				EMPLOYEE_BIRTHDATE2, EMPLOYEE_GENDER2, EMPLOYEE_HIRE_DATE2);

		Assert.assertNotNull("Each project is assigned to an employee.", actual);
		Assert.assertEquals("The project should have two employees assigned.", 2, actual.size());
		assertEmployeesAreEqual(expectedEmployee, actualEmployee);
		assertEmployeesAreEqual(secondExpected, secondActual);
	}

	private Employee getEmployee(Long employeeId, Long departmentId, String firstName, String lastName,
			LocalDate birthDay, char gender, LocalDate hireDate) {
		Employee employee = new Employee();
		employee.setId(employeeId);
		employee.setDepartmentId(departmentId);
		employee.setFirstName(firstName);
		employee.setLastName(lastName);
		employee.setBirthDay(birthDay);
		employee.setGender(gender);
		employee.setHireDate(hireDate);
		return employee;

	}

	private void assertEmployeesAreEqual(Employee expected, Employee actual) {
		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getDepartmentId(), actual.getDepartmentId());
		assertEquals(expected.getFirstName(), actual.getFirstName());
		assertEquals(expected.getLastName(), actual.getLastName());
		assertEquals(expected.getBirthDay(), actual.getBirthDay());
		assertEquals(expected.getGender(), actual.getGender());
		assertEquals(expected.getHireDate(), actual.getHireDate());
	}

}
