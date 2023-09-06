package com.rhys.testSourceCode.transaction.service;

import com.rhys.testSourceCode.transaction.dao.OccupationDao;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:34 AM
 */
@Service
public class OccupationService {

    @Resource
    private OccupationDao occupationDao;


}
