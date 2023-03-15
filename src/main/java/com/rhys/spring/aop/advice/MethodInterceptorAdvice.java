package com.rhys.spring.aop.advice;

import java.lang.reflect.Method;

/**
 * <p>
 * <b>环绕通知接口</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/3/14 16:23
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public interface MethodInterceptorAdvice extends Advice {

    /**
     * <p>
     * <b>对方法进行环绕（前置、后置）增强、异常处理增强，方法实现中需调用目标方法</b>
     * </p >
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/14
     * @param method <span style="color:#e38b6b;">被增强的方法</span>
     * @param args <span style="color:#e38b6b;">方法参数</span>
     * @param target <span style="color:#e38b6b;">方法所属对象</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    Object invoke(Method method, Object[] args, Object target) throws Throwable;
}
