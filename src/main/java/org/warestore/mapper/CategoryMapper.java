package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.object.Category;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryMapper implements RowMapper<Category> {
    @Override
    public Category mapRow(ResultSet resultSet, int i) throws SQLException {
        Category category = new Category();
        category.setId(i+1);
        category.setName(resultSet.getString("name"));
        return category;
    }
}
