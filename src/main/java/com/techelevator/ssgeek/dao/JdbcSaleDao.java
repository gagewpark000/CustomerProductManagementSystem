package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.exception.DaoException;
import com.techelevator.ssgeek.model.Sale;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcSaleDao implements SaleDao {

    private final JdbcTemplate jdbcTemplate;


    public JdbcSaleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    public JdbcSaleDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Sale getSaleById(int saleId) {
        String sql = "select sale_id, customer_id, sale_date, ship_date from sale where sale_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, saleId);
        if (results.next()) {
            return mapRowToSale(results);
        } else {
            return null;
        }
    }

    @Override
    public List<Sale> getUnshippedSales() {
        List<Sale> sales = new ArrayList<>();
        String sql = "select sale_id, customer_id, sale_date, ship_date from sale where ship_date is null";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            sales.add(mapRowToSale(results));
        }
        return sales;
    }

    @Override
    public List<Sale> getSalesByCustomerId(int customerId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "select sale_id, customer_id, sale_date, ship_date from sale where customer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, customerId);
        while (results.next()) {
            sales.add(mapRowToSale(results));
        }
        return sales;
    }

    @Override
    public List<Sale> getSalesByProductId(int productId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "select sale.sale_id, sale.customer_id, sale.sale_date, sale.ship_date from sale join line_item on sale.sale_id = line_item.sale_id where line_item.product_id = ? ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, productId);
        while (results.next()) {
            sales.add(mapRowToSale(results));
        }
        return sales;
    }

    @Override
    public Sale createSale(Sale newSale) {
        try {
            String sql = "insert into sale (customer_id, sale_date, ship_date) values (?, ?, ?) returning sale_id";
            Integer createdId = jdbcTemplate.queryForObject(sql, Integer.class, newSale.getCustomerId(), newSale.getSaleDate(), newSale.getShipDate());
            return getSaleById(createdId);
        } catch (
                DataAccessException ex) {
            throw new DaoException("Failed to create Sale: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Sale updateSale(Sale updatedSale) {
        try {
            String sql = "update sale set customer_id = ?, sale_date = ?, ship_date = ? where sale_id = ?";
            jdbcTemplate.update(sql, updatedSale.getCustomerId(), updatedSale.getSaleDate(), updatedSale.getShipDate(), updatedSale.getSaleId());
            return getSaleById(updatedSale.getSaleId());

        } catch (
                DataAccessException ex) {
            throw new DaoException("Failed to update Sale: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int deleteSaleById(int saleId) {

        try {
            jdbcTemplate.update("delete from line_item where sale_id = ?", saleId);

            int rowsAffected = jdbcTemplate.update("delete from sale where sale_id = ? ", saleId);
            if(rowsAffected == 0){

            }
        } catch (DataAccessException ex) {
            throw new DaoException("Failed to delete sale with ID " + saleId, ex);
        }
        return saleId;
    }

    private Sale mapRowToSale(SqlRowSet results) {
        Sale sale = new Sale();
        sale.setSaleId(results.getInt("sale_id"));
        sale.setCustomerId(results.getInt("customer_id"));
        if(results.getDate("sale_date") != null) {
            sale.setSaleDate(results.getDate("sale_date").toLocalDate());
        }
        if(results.getDate("ship_date") != null){
            sale.setShipDate(results.getDate("ship_date").toLocalDate());
        }
        return sale;
    }
}
