package com.rhys.testSourceCode.transaction.service;

import com.rhys.testSourceCode.transaction.dao.UserDao;
import com.rhys.testSourceCode.transaction.entity.Occupation;
import com.rhys.testSourceCode.transaction.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/7 4:33 AM
 */
@Service
public class UserService {
    private static final Log log = LogFactory.getLog(UserService.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private OccupationService occupationService;

    @Transactional(rollbackFor = Exception.class)
    public void save(User user, Occupation occupation) {
        userDao.save(user);
        log.info("用户添加成功~");

        occupationService.save(occupation);
    }
}
