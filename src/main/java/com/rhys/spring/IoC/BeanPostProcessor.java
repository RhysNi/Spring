package com.rhys.spring.IoC;

/**
 * <p>
 * <b>初始化之前和初始化之后要执行的方法</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/3/27 9:43
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public interface BeanPostProcessor {

    /**
     * <p>
     * <b>初始化之前执行</b>
     * </p >
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/27
     * @param bean     <span style="color:#e38b6b;">bean实例</span>
     * @param beanName <span style="color:#e38b6b;">bean名称</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }


    /**
     * <p>
     * <b>初始化之后执行</b>
     * </p >
     *
     * @param bean     <span style="color:#e38b6b;">bean实例</span>
     * @param beanName <span style="color:#e38b6b;">bean名称</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/27
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }
}
