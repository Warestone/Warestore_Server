package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.Target;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TargetMapper implements RowMapper<Target> {
    @Override
    public Target mapRow(ResultSet resultSet, int i) throws SQLException {
        Target target = new Target();
        target.setId(resultSet.getInt("id"));
        target.setName(resultSet.getString("name"));
        target.setDescription(resultSet.getString("value"));
        resultSet.next();
        target.setPrice(resultSet.getDouble("value"));
        resultSet.next();
        target.setQuantity(resultSet.getInt("value"));
        resultSet.next();
        target.setSize(resultSet.getString("value"));
        return target;
    }
}
