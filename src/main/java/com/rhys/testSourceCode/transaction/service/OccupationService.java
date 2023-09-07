package com.rhys.testSourceCode.transaction.service;

import com.rhys.testSourceCode.transaction.dao.OccupationDao;
import com.rhys.testSourceCode.transaction.entity.Occupation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:34 AM
 */
@Service
public class OccupationService {
    private static final Log log = LogFactory.getLog(OccupationService.class);


    @Autowired
    private OccupationDao occupationDao;

    public void save(Occupation occupation) {
        occupationDao.save(occupation);
        log.info("职业分配成功~");
    }
}
