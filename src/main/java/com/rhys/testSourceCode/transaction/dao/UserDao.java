package com.rhys.testSourceCode.transaction.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:33 AM
 */
@Component
public class UserDao {

    @Resource
    private JdbcTemplate jdbcTemplate;


}
