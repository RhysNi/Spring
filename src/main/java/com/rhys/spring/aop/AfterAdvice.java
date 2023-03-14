package com.rhys.spring.aop;

import java.lang.reflect.Method;

/**
 * <p>
 * <b>最终通知接口</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/3/14 15:57
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public interface AfterAdvice extends Advice {

    /**
     * <p>
     * <b>提供最终增强</b>
     * </p >
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/14
     * @param returnValue <span style="color:#e38b6b;">返回值</span>
     * @param method <span style="color:#e38b6b;">被增强的方法</span>
     * @param args <span style="color:#e38b6b;">方法参数</span>
     * @param target <span style="color:#e38b6b;">方法所属对象</span>
     * @throws Throwable <span style="color:#ffcb6b;">异常类</span>
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    void after(Object returnValue, Method method, Object[] args, Object target) throws Throwable;
}
