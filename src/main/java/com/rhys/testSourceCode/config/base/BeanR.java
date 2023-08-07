package com.rhys.testSourceCode.config.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * <p>
 * <b>测试DI</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/8/3 19:13
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
@Component
public class BeanR {

    private BeanY beanY;
    private BeanS beanS;

    @Autowired
    public BeanR(BeanY beanY) {
        this.beanY = beanY;
    }


    // @Autowired
    public BeanR(BeanY beanY, BeanS beanS) {
        this.beanY = beanY;
        this.beanS = beanS;
    }
}
