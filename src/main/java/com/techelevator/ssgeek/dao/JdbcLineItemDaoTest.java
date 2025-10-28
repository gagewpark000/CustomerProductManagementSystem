package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcLineItemDaoTest {
    @BeforeEach
    public void setup() {
        if (dataSource == null) {
            dataSource = new SingleConnectionDataSource("jdbc:postgresql://localhost:5432/SSGeek", "postgres", "postgres1", true);
            dataSource.setAutoCommit(false);
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        dao = new JdbcLineItemDao(jdbcTemplate);
    }

    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();

    }

    private JdbcLineItemDao dao;
    private static SingleConnectionDataSource dataSource;
    private static final LineItem LINE_ITEM_1 = new LineItem(1, 1, 1, 10, "Coffee Mug", new BigDecimal("99.90"));

    @Test
    public void getLineItemsBySaleId_with_valid_id_returns_correct_lineItem() {
        List<LineItem> results = dao.getLineItemsBySaleId(1);
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertLineItemsMatch(LINE_ITEM_1, results.get(0));
    }

    @Test
    public void getLineItemsBySaleId_with_invalid_sale_id_returns_empty_list() {
        List<LineItem> results = dao.getLineItemsBySaleId(-1);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    private void assertLineItemsMatch(LineItem expected, LineItem actual) {
        assertEquals(expected.getLineItemId(), actual.getLineItemId());
        assertEquals(expected.getSaleId(), actual.getSaleId());
        assertEquals(expected.getProductId(), actual.getProductId());
        assertEquals(expected.getQuantity(), actual.getQuantity());
        assertEquals(expected.getProductName(), actual.getProductName());
        assertEquals(expected.getExtendedPrice(), actual.getExtendedPrice());
    }
}
