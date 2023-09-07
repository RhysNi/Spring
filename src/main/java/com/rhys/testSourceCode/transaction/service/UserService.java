package com.rhys.testSourceCode.transaction.service;

import com.rhys.testSourceCode.transaction.dao.UserDao;
import com.rhys.testSourceCode.transaction.entity.Occupation;
import com.rhys.testSourceCode.transaction.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:33 AM
 */
@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private OccupationService occupationService;

    public void save(User user, Occupation occupation) {
        userDao.save(user);
        occupationService.save(occupation);
    }
}
