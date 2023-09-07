package com.rhys.testSourceCode.transaction.dao;

import com.rhys.testSourceCode.transaction.entity.Occupation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:34 AM
 */
@Component
public class OccupationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void save(Occupation occupation) {
        jdbcTemplate.update("INSERT INTO tab_occupation (uName, occupation) VALUES(?,?)", occupation.getUserName(), occupation.getOccuation());
    }
}
