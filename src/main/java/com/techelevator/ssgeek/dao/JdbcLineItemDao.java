package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.LineItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcLineItemDao implements LineItemDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcLineItemDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public JdbcLineItemDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<LineItem> getLineItemsBySaleId(int saleId){
        List<LineItem> lineItems = new ArrayList<>();
        String sql = "select line_item.line_item_id, line_item.sale_id, line_item.product_id, line_item.quantity,product.name as product_name, (line_item.quantity * product.price) as extended_price from line_item join product on line_item.product_id = product.product_id where sale_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, saleId);
        while(results.next()){
            LineItem lineItem = new LineItem();
            lineItem.setLineItemId(results.getInt("line_item_id"));
            lineItem.setSaleId(results.getInt("sale_id"));
            lineItem.setProductId(results.getInt("product_id"));
            lineItem.setQuantity(results.getInt("quantity"));
            lineItem.setProductName(results.getString("product_name"));
            lineItem.setPrice(results.getBigDecimal("extended_price"));
            lineItems.add(lineItem);
        }
        return lineItems;
    }

}
