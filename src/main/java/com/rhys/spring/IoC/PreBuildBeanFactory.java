package com.rhys.spring.IoC;

import java.util.Map;

/**
 * <p>
 * <b>提前创建单例对象</b>
 * </p >
 *
 * @author : 倪世栋
 * @version : v1.0
 * @date : 2023/2/22 16:36
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public class PreBuildBeanFactory extends DefaultBeanFactory {

    /**
     * <p>
     * <b>提前实例化单例</b>
     * </p >
     *
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/2/22
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    public void instantiateSingleton() throws Exception {
        synchronized (this.beanDefinitionMap) {
            for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
                String beanName = entry.getKey();
                BeanDefinition beanDefinition = entry.getValue();
                //判断是否为单例
                if (beanDefinition.isSingleton()) {
                    //执行doGetBean进行单例对象的创建以及存储
                    this.getBean(beanName);
                }
            }
        }
    }
}
