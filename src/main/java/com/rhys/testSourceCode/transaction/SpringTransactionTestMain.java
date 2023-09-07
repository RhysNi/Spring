package com.rhys.testSourceCode.transaction;

import com.rhys.testSourceCode.transaction.entity.Occupation;
import com.rhys.testSourceCode.transaction.entity.User;
import com.rhys.testSourceCode.transaction.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.UUID;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/9/4 12:15 AM
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.rhys.testSourceCode.transaction")
public class SpringTransactionTestMain {
    private static final Log log = LogFactory.getLog(SpringTransactionTestMain.class);

    public static void main(String[] args) {

        try (AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringTransactionTestMain.class);) {
            UserService userService = applicationContext.getBean(UserService.class);
            userService.save(
                    User.builder().userId(UUID.randomUUID().toString().replaceAll("-", "")).name("RhysNi1").age("26").build(),
                    Occupation.builder().userName("RhysNi1").occuation("coder").build()
            );
        }
    }
}
