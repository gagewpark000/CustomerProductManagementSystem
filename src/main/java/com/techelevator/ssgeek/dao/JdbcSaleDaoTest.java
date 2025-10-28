package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;
import com.techelevator.ssgeek.model.Sale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcSaleDaoTest {
    @BeforeEach
    public void setup() {
        if (dataSource == null) {
            dataSource = new SingleConnectionDataSource("jdbc:postgresql://localhost:5432/SSGeek", "postgres", "postgres1", true);
            dataSource.setAutoCommit(false);
        }
        jdbcTemplate = new JdbcTemplate(dataSource);

        dao = new JdbcSaleDao(jdbcTemplate);
    }

    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();

    }

    private JdbcSaleDao dao;
    private JdbcTemplate jdbcTemplate;
    private static SingleConnectionDataSource dataSource;

    private static final Sale SALE_1 = new Sale(1, 1, LocalDate.parse("2022-01-01"), LocalDate.parse("2022-01-04"));
    private static final Sale SALE_2 = new Sale(6, 3, LocalDate.parse("2022-06-01"), null);

    @Test
    public void getSaleById_with_valid_id_returns_correct_sale() {
        Sale result = dao.getSaleById(1);
        assertNotNull(result);
        assertSalesMatch(SALE_1, result);
    }

    @Test
    public void getUnshippedSales_returns_sales_that_are_unshipped() {

        Sale unshippedSale = new Sale();
        unshippedSale.setCustomerId(1);
        unshippedSale.setSaleDate(LocalDate.now());
        unshippedSale.setShipDate(null);

        Sale createdSale = dao.createSale(unshippedSale);

        List<Sale> unshippedSales = dao.getUnshippedSales();
        assertNotNull(unshippedSales);
        assertTrue(unshippedSales.contains(createdSale));

    }

    @Test
    public void getSalesByCustomerId() {
        List<Sale> sales = dao.getSalesByCustomerId(1);
        assertNotNull(sales);
        assertTrue(sales.contains(SALE_1));

    }

    @Test
    public void getSalesByProductId() {
        int productId = 1;
        List<Sale> result = dao.getSalesByProductId(productId);
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertSalesMatch(SALE_1, result.get(0));
    }

    @Test
    public void createSale_creates_sale() {
        Sale newSale = new Sale();
        newSale.setCustomerId(1);
        newSale.setSaleDate(LocalDate.parse("2025-04-01"));
        newSale.setShipDate(LocalDate.parse("2025-01-01"));


        Sale result = dao.createSale(newSale);
        assertNotNull(result);
        assertTrue(result.getSaleId() > 0);
    }

    @Test
    public void updateSale_updates_sale() {
        Sale sale = new Sale();
        sale.setCustomerId(1);
        sale.setSaleDate(LocalDate.parse("2025-04-01"));
        sale.setShipDate(LocalDate.parse("2025-01-01"));

        Sale createdSale = dao.createSale(sale);
        assertNotNull(createdSale);
        int saleId = createdSale.getSaleId();

        createdSale.setCustomerId(2);
        createdSale.setSaleDate(LocalDate.parse("2022-04-01"));
        createdSale.setShipDate(LocalDate.parse("2022-01-01"));

        dao.updateSale(createdSale);

        Sale updated = dao.getSaleById(saleId);
        assertNotNull(updated);
        assertSalesMatch(createdSale, updated);
    }

    @Test
    public void deleteSaleById() {
        jdbcTemplate.update(
                "insert into sale (customer_id, sale_date, ship_date) values (?, ?, ?)",
                SALE_1.getCustomerId(),
                SALE_1.getSaleDate(),
                SALE_1.getShipDate());


        int rowsAffected = dao.deleteSaleById(SALE_1.getSaleId());

        assertEquals(1, rowsAffected);

        Sale retrieved = dao.getSaleById(SALE_1.getSaleId());
        assertNull(retrieved);
    }


    private void assertSalesMatch(Sale expected, Sale actual) {
        assertEquals(expected.getSaleId(), actual.getSaleId());
        assertEquals(expected.getCustomerId(), actual.getCustomerId());
        assertEquals(expected.getSaleDate(), actual.getSaleDate());
        assertEquals(expected.getShipDate(), actual.getShipDate());
    }


}
