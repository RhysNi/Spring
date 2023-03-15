package com.rhys.spring.aop.advice;

import java.lang.reflect.Method;

/**
 * <p>
 * <b>异常通知接口</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/3/14 16:33
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public interface ThrowsAdvice extends Advice{

    /**
     * <p>
     * <b>提供异常处理增强</b>
     * </p >
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/14
     * @param method <span style="color:#e38b6b;">被增强的方法</span>
     * @param args <span style="color:#e38b6b;">方法参数</span>
     * @param target <span style="color:#e38b6b;">方法所属对象</span>
     * @param e <span style="color:#e38b6b;">捕获的异常</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Throwable <span style="color:#ffcb6b;">异常类</span>
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    void afterThrowing(Method method, Object[] args, Object target,Exception e) throws Throwable;
}
