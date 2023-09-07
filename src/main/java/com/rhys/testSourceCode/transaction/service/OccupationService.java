package com.rhys.testSourceCode.transaction.service;

import com.rhys.testSourceCode.transaction.dao.OccupationDao;
import com.rhys.testSourceCode.transaction.entity.Occupation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:34 AM
 */
@Service
public class OccupationService {

    @Autowired
    private OccupationDao occupationDao;

    public void save(Occupation occupation) {
        occupationDao.save(occupation);
    }
}
