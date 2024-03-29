package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.Order;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderMapper implements RowMapper<Order> {
    @Override
    public Order mapRow(ResultSet resultSet, int i) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("id"));
        order.setNameOrder(resultSet.getString("name"));
        order.setName(resultSet.getString("product_name"));
        order.setUsername(resultSet.getString("username"));
        order.setDate(resultSet.getString("value"));
        resultSet.next();
        order.setPrice(resultSet.getDouble("value"));
        resultSet.next();
        order.setQuantity(resultSet.getInt("value"));
        resultSet.next();
        order.setStatus(resultSet.getString("value"));
        return order;
    }
}
