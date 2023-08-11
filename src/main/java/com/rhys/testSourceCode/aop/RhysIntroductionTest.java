package com.rhys.testSourceCode.aop;

import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/**
 * <p>
 * <b>功能描述</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/8/11 15:47
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public class RhysIntroductionTest {
    public static void main(String[] args) {

    }


}

/*
代理接口
 */
interface RhysAop {
    void call(String msg);
}

interface RhysEnhancedAop {
    void callEnhance(String msg);
}

class RAop implements RhysAop {

    @Override
    public void call(String msg) {
        System.out.println("execute RAop.call : " + msg);
    }
}


class RhysIntroductionInterceptor extends DelegatingIntroductionInterceptor implements RhysEnhancedAop {
    @Override
    public void callEnhance(String msg) {
        System.out.println("execute RhysIntroductionInterceptor.call : " + msg);
    }
}