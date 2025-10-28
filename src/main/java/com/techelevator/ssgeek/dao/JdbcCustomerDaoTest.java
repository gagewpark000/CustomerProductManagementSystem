package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcCustomerDaoTest {
    @BeforeEach
    public void setup() {
        if (dataSource == null) {
            dataSource = new SingleConnectionDataSource("jdbc:postgresql://localhost:5432/SSGeek", "postgres", "postgres1", true);
            dataSource.setAutoCommit(false);
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        dao = new JdbcCustomerDao(jdbcTemplate);
    }

    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();

    }

    private JdbcCustomerDao dao;
    private static SingleConnectionDataSource dataSource;

    private static final Customer CUSTOMER_1 = new Customer(1, "Taylor Galtone", "59 Bultman Street", "Apt 1708", "Cedar Rapids", "IA", "52401");


    @Test
    public void getCustomerById_with_valid_id_returns_correct_customer() {
        Customer result = dao.getCustomerById(1);
        assertNotNull(result);
        assertCustomersMatch(CUSTOMER_1, result);
    }

    @Test
    public void getCustomerById_with_invalid_id_returns_null_customer() {
        Customer result = dao.getCustomerById(-1);
        assertNull(result);
    }

    @Test
    public void getCustomers_returns_valid_customers() {
        List<Customer> result = dao.getCustomers();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertCustomersMatch(CUSTOMER_1, result.get(0));
    }


    @Test
    public void createCustomer_creates_customer() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Michael Jordan");
        newCustomer.setStreetAddress1("4341234 Road");
        newCustomer.setStreetAddress2("8409809 State Route");
        newCustomer.setCity("Believeland");
        newCustomer.setState("VA");
        newCustomer.setZipCode("12345");

        Customer result = dao.createCustomer(newCustomer);
        assertNotNull(result);
        assertTrue(result.getCustomerId() > 0);
    }

    @Test
    public void updateCustomer_updates_customer() {
        Customer customer = new Customer();
        customer.setName("Michael Jordan");
        customer.setStreetAddress1("4341234 Road");
        customer.setStreetAddress2("8409809 State Route");
        customer.setCity("Believeland");
        customer.setState("VA");
        customer.setZipCode("12345");

        Customer createdCustomer = dao.createCustomer(customer);
        assertNotNull(createdCustomer);
        int customerId = createdCustomer.getCustomerId();

        createdCustomer.setName("Jordan Michael");
        createdCustomer.setStreetAddress1("437541234 Road");
        createdCustomer.setStreetAddress2("8409 State Route");
        createdCustomer.setCity("Dreamland");
        createdCustomer.setState("WV");
        createdCustomer.setZipCode("125");

        dao.updateCustomer(createdCustomer);

        Customer updated = dao.getCustomerById(customerId);
        assertNotNull(updated);
        assertCustomersMatch(createdCustomer, updated);

    }

    private void assertCustomersMatch(Customer expected, Customer actual) {
        assertEquals(expected.getCustomerId(), actual.getCustomerId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getStreetAddress1(), actual.getStreetAddress1());
        assertEquals(expected.getStreetAddress2(), actual.getStreetAddress2());
        assertEquals(expected.getCity(), actual.getCity());
        assertEquals(expected.getState(), actual.getState());
        assertEquals(expected.getZipCode(), actual.getZipCode().trim());

    }
}
