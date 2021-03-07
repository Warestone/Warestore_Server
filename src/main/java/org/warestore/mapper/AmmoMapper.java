package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.Ammo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AmmoMapper implements RowMapper<Ammo>{
    @Override
    public Ammo mapRow(ResultSet resultSet, int i) throws SQLException {
        Ammo ammo = new Ammo();
        ammo.setId(resultSet.getInt("id"));
        ammo.setPrice(resultSet.getDouble("value"));
        ammo.setName(resultSet.getString("name"));
        resultSet.next();
        ammo.setDescription(resultSet.getString("value"));
        resultSet.next();
        ammo.setQuantity(resultSet.getInt("value"));
        resultSet.next();
        ammo.setCaliber(resultSet.getString("value"));
        resultSet.next();
        ammo.setRounds(resultSet.getInt("value"));
        return ammo;
    }
}
