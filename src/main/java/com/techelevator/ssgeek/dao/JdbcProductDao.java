package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.exception.DaoException;
import com.techelevator.ssgeek.model.Product;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDao implements ProductDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcProductDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public JdbcProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    public Product getProductById(int productId) {
        String sql = "select product_id, name, description, price, image_name from product where product_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, productId);

        if (results.next()) {
            return mapRowToProduct(results);

        } else {
            return null;
        }
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "select product_id, name, description, price, image_name from product order by product_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Product product = mapRowToProduct(results);
            products.add(product);

        }

        return products;
    }

    @Override
    public List<Product> getProductsWithNoSales() {
        List<Product> products = new ArrayList<>();
        String sql = "select product.product_id, name, description, price, image_name from product left join line_item on product.product_id = line_item.product_id where line_item.product_id is null";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Product product = mapRowToProduct(results);
            products.add(product);
        }
        return products;
    }

    @Override
    public Product createProduct(Product newProduct) {
        try {
            String sql = "insert into product (name, description, price, image_name) values (?, ?, ?, ?) returning product_id";
            Integer createdId = jdbcTemplate.queryForObject(sql, Integer.class, newProduct.getName(), newProduct.getDescription(), newProduct.getPrice(), newProduct.getImageName());
            return getProductById(createdId);
        } catch (
                DataAccessException ex) {
            throw new DaoException("Failed to create product: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Product updateProduct(Product updatedProduct) {
        try {
            String sql = "update product set name = ?, description = ?, price = ?, image_name = ? where product_id = ?";
            jdbcTemplate.update(sql, updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice(), updatedProduct.getImageName(), updatedProduct.getProductId());
            return getProductById(updatedProduct.getProductId());
        } catch (DataAccessException ex) {
            throw new DaoException("Failed to update product: " + ex.getMessage(), ex);
        }
    }

    @Override
    public int deleteProductById(int productId) {
        try {
            String deletion = "delete from product where product_id = ?";
            return jdbcTemplate.update(deletion, productId);
        } catch (DataAccessException ex) {
            throw new DaoException("Failed to delete Product with ID " + productId, ex);
        }

    }

    private Product mapRowToProduct(SqlRowSet results) {
        Product product = new Product();
        product.setProductId(results.getInt("product_id"));
        product.setName(results.getString("name"));
        product.setDescription(results.getString("description"));
        product.setPrice(results.getBigDecimal("price"));
        product.setImageName(results.getString("image_name"));
        return product;
    }
}








