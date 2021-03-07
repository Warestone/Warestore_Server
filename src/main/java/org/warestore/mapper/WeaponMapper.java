package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.Weapon;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WeaponMapper implements RowMapper<Weapon> {
    @Override
    public Weapon mapRow(ResultSet resultSet, int i) throws SQLException {
        Weapon weapon = new Weapon();
        weapon.setId(resultSet.getInt("id"));
        weapon.setPrice(resultSet.getDouble("value"));
        weapon.setName(resultSet.getString("name"));
        resultSet.next();
        weapon.setDescription(resultSet.getString("value"));
        resultSet.next();
        weapon.setQuantity(resultSet.getInt("value"));
        resultSet.next();
        weapon.setCaliber(resultSet.getString("value"));
        return weapon;
    }
}
