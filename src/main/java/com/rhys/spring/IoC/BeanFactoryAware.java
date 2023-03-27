package com.rhys.spring.IoC;

/**
 * <p>
 * <b>Bean工厂感知接口</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/3/27 10:34
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public interface BeanFactoryAware extends Aware {

    /**
     * <p>
     * <b>设置Bean工厂</b>
     * </p >
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/3/27
     * @param beanFactory <span style="color:#e38b6b;">字段描述</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    void setBeanFactory(BeanFactory beanFactory);
}
