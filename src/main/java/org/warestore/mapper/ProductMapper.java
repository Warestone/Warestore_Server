package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.Product;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet resultSet, int i) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setPrice(resultSet.getDouble("value"));
        resultSet.next();
        product.setQuantity(resultSet.getInt("value"));
        return product;
    }
}
