package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.dao.CustomerDao;
import com.techelevator.ssgeek.exception.DaoException;
import com.techelevator.ssgeek.model.Customer;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcCustomerDao implements CustomerDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcCustomerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public JdbcCustomerDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }



    @Override
    public Customer getCustomerById(int customerId) {

        String sql = "select customer_id, name, street_address1, street_address2, city, state, zip_code from customer where customer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, customerId);
        if (results.next()) {
            return mapRowToCustomer(results);
        } else {
            return null;
        }
    }

    @Override
    public List<Customer> getCustomers() {


        List<Customer> customers = new ArrayList<>();
        String sql = "select customer_id, name, street_address1, street_address2, city, state, zip_code from customer order by customer_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);

        while (results.next()) {
            customers.add(mapRowToCustomer(results));

        }

        return customers;
    }

    @Override
    public Customer createCustomer(Customer newCustomer) {
        try {
            String sql = "insert into customer (name, street_address1, street_address2, city, state, zip_code) values (?, ?, ?, ?, ?, ?) returning customer_id";
            Integer createdId = jdbcTemplate.queryForObject(sql, Integer.class, newCustomer.getName(), newCustomer.getStreetAddress1(), newCustomer.getStreetAddress2(), newCustomer.getCity(), newCustomer.getState(), newCustomer.getZipCode());
            return getCustomerById(createdId);
        } catch (
                DataAccessException ex) {
            throw new DaoException("Failed to create customer: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Customer updateCustomer(Customer updatedCustomer) {
        try {
            String sql = "update customer set name = ?, street_address1 = ?, street_address2 = ?, city = ?, state = ?, zip_code = ? where customer_id = ?";
            jdbcTemplate.update(sql, updatedCustomer.getName(), updatedCustomer.getStreetAddress1(), updatedCustomer.getStreetAddress2(), updatedCustomer.getCity(), updatedCustomer.getState(), updatedCustomer.getZipCode(), updatedCustomer.getCustomerId());
            return getCustomerById(updatedCustomer.getCustomerId());
        } catch (DataAccessException ex) {
            throw new DaoException("Failed to update customer: " + ex.getMessage(), ex);
        }
    }

    private Customer mapRowToCustomer(SqlRowSet results) {
        Customer customer = new Customer();
        customer.setCustomerId(results.getInt("customer_id"));
        customer.setName(results.getString("name"));
        customer.setStreetAddress1(results.getString("street_address1"));
        customer.setStreetAddress2(results.getString("street_address2"));
        customer.setCity(results.getString("city"));
        customer.setState(results.getString("state"));
        customer.setZipCode(results.getString("zip_code"));

        return customer;
    }
}


