package org.warestore.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.warestore.model.User;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int i) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setEmail(resultSet.getString("value"));
        resultSet.next();
        user.setPhoneNumber(resultSet.getString("value"));
        resultSet.next();
        user.setRole(resultSet.getString("value"));
        resultSet.next();
        user.setAddress(resultSet.getString("value"));
        resultSet.next();
        user.setFirstName(resultSet.getString("value"));
        resultSet.next();
        user.setLastName(resultSet.getString("value"));
        resultSet.next();
        user.setPatronymicName(resultSet.getString("value"));
        return user;
    }
}
