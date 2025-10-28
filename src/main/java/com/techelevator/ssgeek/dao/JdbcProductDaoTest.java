package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import com.techelevator.ssgeek.model.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcProductDaoTest {
    @BeforeEach
    public void setup() {
        if (dataSource == null) {
            dataSource = new SingleConnectionDataSource("jdbc:postgresql://localhost:5432/SSGeek", "postgres", "postgres1", true);
            dataSource.setAutoCommit(false);
        }
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        dao = new JdbcProductDao(jdbcTemplate);
    }

    @AfterEach
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();

    }

    private JdbcProductDao dao;
    private static SingleConnectionDataSource dataSource;

    private static final Product PRODUCT_1 = new Product(1, "Coffee Mug", "Staying up late to take in the wonders of the solar system can make a geek a little sluggish in the morning. This awesome mug is just what you need to perk up in the morning with your caffeinatened beverage of choice!", BigDecimal.valueOf(9.99), "ssg_mug.png");

    private static final Product PRODUCT_2 = new Product(5, "Midnight Planetarium Watch", "The planets evolve at their real speeds of orbit, while a graceful star indicates your lucky day. A poetic invitation to immortalize a special date.", new BigDecimal("221999.99"), null);

    @Test
    public void getProductById_with_valid_id_returns_correct_product() {
        Product result = dao.getProductById(1);
        assertNotNull(result);
        assertProductsMatch(PRODUCT_1, result);
    }

    @Test
    public void getProductById_with_invalid_id_returns_null_product() {
        Product result = dao.getProductById(-1);
        assertNull(result);
    }

    @Test
    public void getProducts_returns_all_products() {
        List<Product> result = dao.getProducts();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertProductsMatch(PRODUCT_1, result.get(0));
    }

    @Test
    public void getProductsWithNoSales_returns_products_with_no_sales() {
        Product noSalesProduct = new Product();
        noSalesProduct.setName("No Sales Product");
        noSalesProduct.setDescription("No Sales Description");
        noSalesProduct.setPrice(BigDecimal.valueOf(1.00));
        noSalesProduct.setImageName("No Sales Image");

        Product createdProduct = dao.createProduct(noSalesProduct);
        List<Product> products = dao.getProductsWithNoSales();

        assertNotNull(products);
        assertTrue(products.contains(createdProduct));
    }

    @Test
    public void getProductsWithNoSales_does_not_return_products_with_sales() {
        List<Product> products = dao.getProductsWithNoSales();
        assertNotNull(products);
        assertFalse(products.contains(PRODUCT_2));

    }

    @Test
    public void createProduct_creates_product() {
        Product newProduct = new Product();
        newProduct.setName("Stuffed Animal");
        newProduct.setDescription("Fluffy");
        newProduct.setPrice(BigDecimal.valueOf(10.99));
        newProduct.setImageName("fluffy_stuffed_animal.png");


        Product result = dao.createProduct(newProduct);
        assertNotNull(result);
        assertTrue(result.getProductId() > 0);
    }

    @Test
    public void updateProduct_updates_product() {
        Product product = new Product();
        product.setName("Stuffed Animal");
        product.setDescription("Fluffy");
        product.setPrice(BigDecimal.valueOf(10.99));
        product.setImageName("fluffy_stuffed_animal.png");

        Product createdProduct = dao.createProduct(product);
        assertNotNull(createdProduct);
        int productId = createdProduct.getProductId();

        createdProduct.setName("Dog Plush");
        createdProduct.setDescription("Fuzzy");
        createdProduct.setPrice(BigDecimal.valueOf(10.00));
        createdProduct.setImageName("fuzzy_dog_plush.png");

        dao.updateProduct(createdProduct);

        Product updated = dao.getProductById(productId);
        assertNotNull(updated);
        assertProductsMatch(createdProduct, updated);
    }

    private void assertProductsMatch(Product expected, Product actual) {
        assertEquals(expected.getProductId(), actual.getProductId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertTrue(expected.getPrice().compareTo(actual.getPrice()) == 0);
        assertEquals(expected.getImageName(), actual.getImageName());

    }

}
