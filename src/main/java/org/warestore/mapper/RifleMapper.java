package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.object.Rifle;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RifleMapper implements RowMapper<Rifle> {
    @Override
    public Rifle mapRow(ResultSet resultSet, int i) throws SQLException {
        Rifle rifle = new Rifle();
        rifle.setId(i+1);
        rifle.setName(resultSet.getString("name"));
        rifle.setDescription(resultSet.getString("description"));
        rifle.setQuantity(resultSet.getInt("quantity"));
        rifle.setPrice(resultSet.getDouble("price"));
        return rifle;
    }
}
