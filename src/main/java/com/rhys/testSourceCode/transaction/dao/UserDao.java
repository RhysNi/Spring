package com.rhys.testSourceCode.transaction.dao;

import com.rhys.testSourceCode.transaction.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:33 AM
 */
@Component
public class UserDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(User user) {
        jdbcTemplate.update("INSERT INTO tab_user (uid, name, age) VALUES(?,?,?)", user.getUserId(), user.getName(), user.getAge());
    }
}
