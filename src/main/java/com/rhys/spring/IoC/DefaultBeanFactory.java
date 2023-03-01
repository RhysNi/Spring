package com.rhys.spring.IoC;

import com.rhys.spring.IoC.exception.AliasRegistryException;
import com.rhys.spring.IoC.exception.BeanDefinitionRegistryException;
import com.rhys.spring.IoC.exception.PrimaryException;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.lang.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/16 11:19 PM
 */
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private Map<String, Object> singletonBeanMap = new ConcurrentHashMap<>(256);
    private Map<String, String[]> aliasMap = new ConcurrentHashMap<>(256);

    private Map<Class<?>, Set<String>> typeMap = new ConcurrentHashMap<>(256);

    /**
     * 注册BeanDefinition
     *
     * @param beanName       bean名称
     * @param beanDefinition bean定义
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public void registryBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionRegistryException {
        //入参判空
        Objects.requireNonNull(beanName, "beanName不能为空");
        Objects.requireNonNull(beanDefinition, "beanDefinition不能为空");

        //校验bean合法性  beanDefinition named a is invalid
        if (!beanDefinition.validate()) {
            throw new BeanDefinitionRegistryException("beanDefinition named " + beanName + " is invalid !");
        }

        //校验beanName是否已存在，重复则抛异常
        if (this.containsBeanDefinition(beanName)) {
            throw new BeanDefinitionRegistryException(beanName + " Already exists ! " + this.getBeanDefinition(beanName));
        }

        //存储成功注册的bean以及beanDefinition
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    /**
     * 获取BeanDefinition
     *
     * @param beanName bean名称
     * @return com.rhys.spring.beans.BeanDefinition
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        //从beanDefinitionMap中获取注册成功的bean定义
        return this.beanDefinitionMap.get(beanName);
    }

    /**
     * 判断是否已经存在
     *
     * @param beanName bean名称
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean containsBeanDefinition(String beanName) {
        //对比beanDefinitionMap中是否存在相同key
        return this.beanDefinitionMap.containsKey(beanName);
    }


    /**
     * 获取Bean实例
     *
     * @param beanName bean名称
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/16
     */
    @Override
    public Object getBean(String beanName) throws Exception {
        return this.doGetBean(beanName);
    }

    /**
     * 根据beanName获取Type
     *
     * @param beanName
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public Class<?> getType(String beanName) throws Exception {
        //根据beanName到beanDefinitionMap中获取对应的BeanDefinition
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        //获取到具体的Type
        Class<?> beanClass = beanDefinition.getBeanClass();
        if (beanClass != null) {
            //factoryMethodName不为空则通过静态工厂方法构造对象,否则通过构造方法创建实例（beanClass本身就是Type）
            if (StringUtils.isNotBlank(beanDefinition.getFactoryMethodName())) {
                //反射得到Method,最终回去Method返回类型
                beanClass = beanClass.getDeclaredMethod(beanDefinition.getFactoryMethodName(), null).getReturnType();
            }
        } else {
            //beanClass为空，需要获取到工厂Bean的beanClass
            beanClass = this.getType(beanDefinition.getFactoryBeanName());
            //再通过反射得到Method,最终得到工厂方法的返回类型
            beanClass = beanClass.getDeclaredMethod(beanDefinition.getFactoryMethodName(), null).getReturnType();
        }
        return beanClass;
    }

    /**
     * 根据Type获取bean实例
     *
     * @param c
     * @return T
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public <T> T getBean(Class<T> c) throws Exception {

        Set<String> beanNames = this.typeMap.get(c);
        if (beanNames != null) {
            //如果有只有一个直接获取Bean实例返回，否则遍历找出Primary
            if (beanNames.size() == 1) {
                return (T) this.getBean(beanNames.iterator().next());
            } else {
                BeanDefinition beanDefinition;
                String primaryName = null;
                StringBuilder beanNameStr = new StringBuilder();
                // 获得所有对应的BeanDefinition
                for (String beanName : beanNames) {
                    beanDefinition = this.getBeanDefinition(beanName);
                    if (beanDefinition != null && beanDefinition.isPrimary()) {
                        //存在多个Primary的Bean实例抛异常
                        if (primaryName != null) {
                            throw new PrimaryException("Bean of type 【" + c + "】 has more than one Primary !");
                        } else {
                            primaryName = beanName;
                        }
                    }
                    beanNameStr.append(" " + beanName);
                }

                //有primary则返回，没有则抛异常
                if (primaryName != null) {
                    return (T) this.getBean(primaryName);
                } else {
                    throw new PrimaryException("Multiple beans of type【" + c + "】exist but no Primary is found !");
                }
            }
        }
        return null;
    }

    /**
     * 根据Type获取bean实例
     *
     * @param c
     * @return java.util.Map<java.lang.String, T>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public <T> Map<String, T> getBeanOfType(Class<T> c) throws Exception {
        Set<String> beanNames = this.typeMap.get(c);
        if (beanNames != null) {
            HashMap<String, T> map = new HashMap<>();
            for (String beanName : beanNames) {
                map.put(beanName, (T) this.getBean(beanName));
            }
            return map;
        }
        return null;
    }

    /**
     * 根据Type获取bean集合
     *
     * @param c
     * @return java.util.List<T>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    @Override
    public <T> List<T> getBeanListOfType(Class<T> c) throws Exception {
        Set<String> beanNames = this.typeMap.get(c);
        if (beanNames != null) {
            List<T> beanList = new ArrayList<>();
            for (String beanName : beanNames) {
                beanList.add((T) this.getBean(beanName));
            }
            return beanList;
        }
        return null;
    }

    private Object doGetBean(String beanName) throws Exception {
        //校验beanName
        Objects.requireNonNull(beanName, "beanName can not be empty !");

        //从单例Bean所存储的Map中根据beanName获取一下这个Bean实例，获取到直接返回
        Object beanInstance = singletonBeanMap.get(beanName);
        if (beanInstance != null) {
            return beanInstance;
        }

        //没获取到则根据BeanDefinition创建Bean实例并且存放到Map中
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Objects.requireNonNull(beanDefinition, "beanDefinition named " + beanName + " is invalid !");

        if (beanDefinition.isSingleton()) {
            synchronized (this.singletonBeanMap) {
                //双重检查，再次根据beanName从单例Map中尝试获取Bean实例，如果还是没有获取到再开始创建
                beanInstance = this.singletonBeanMap.get(beanName);
                if (beanInstance == null) {
                    //创建实例
                    beanInstance = createInstance(beanDefinition);
                    //存到singletonBeanMap中
                    singletonBeanMap.put(beanName, beanInstance);
                }
            }
        } else {
            //如果不要求为单例则直接创建,不用往单例BeanMap中存，所以不关心是否存在
            beanInstance = createInstance(beanDefinition);
        }
        return beanInstance;
    }


    /**
     * <p>
     * <b>创建实例</b>
     * </p >
     *
     * @param beanDefinition <span style="color:#e38b6b;">bean定义</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/2/22
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">N倪倪</a>
     */
    private Object createInstance(BeanDefinition beanDefinition) throws Exception {
        //获取bean定义对应的类(类即类型)
        Class<?> beanClass = beanDefinition.getBeanClass();
        Object beanInstance = null;

        //根据beanClass中能获取到的信息来创建实例,beanClass为空则直接通过工厂bean方式创建实例
        if (beanClass != null) {
            //获取并判断工厂成员方法名是否为空，为空则根据构造方法创建实例，否则直接根据工厂静态方法创建实例
            if (StringUtils.isBlank(beanDefinition.getFactoryMethodName())) {
                beanInstance = this.createInstanceByConstructor(beanDefinition);
            } else {
                beanInstance = this.createInstanceByStaticFactoryMethod(beanDefinition);
            }
        } else {
            beanInstance = this.createInstanceByFactoryBean(beanDefinition);
        }

        //创建完实例执行初始化方法
        this.init(beanDefinition, beanInstance);

        return beanInstance;
    }

    /**
     * 工厂bean方式创建实例
     *
     * @param beanDefinition
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/20
     */
    private Object createInstanceByFactoryBean(BeanDefinition beanDefinition) throws Exception {
        //根据工厂bean名称创建实例
        Object bean = this.doGetBean(beanDefinition.getFactoryBeanName());
        //再通过工厂bean的类获取工厂Bean成员方法名称创建最终的Bean
        Method method = bean.getClass().getMethod(beanDefinition.getFactoryBeanName(), null);
        return method.invoke(bean, null);
    }

    /**
     * 静态工厂方法
     *
     * @param beanDefinition
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/20
     */
    private Object createInstanceByStaticFactoryMethod(BeanDefinition beanDefinition) throws Exception {
        Class<?> beanClass = beanDefinition.getBeanClass();
        Method method = beanClass.getMethod(beanDefinition.getFactoryMethodName(), null);
        return method.invoke(beanClass, null);
    }

    /**
     * 构造函数创建实例
     *
     * @param beanDefinition
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/20
     */
    private Object createInstanceByConstructor(BeanDefinition beanDefinition) throws Exception {
        //除了InstantiationException和IllegalAccessException异常外，为了避免相关的程序使用不当可能会存在某些危险的操作从而引发安全问题，这里需要捕获一下SecurityException
        try {
            return beanDefinition.getBeanClass().newInstance();
        } catch (SecurityException exception) {
            logger.error("create instance error beanDefinition:{}, exception:{}", beanDefinition, exception);
            throw exception;
        }
    }


    /**
     * 初始化方法
     *
     * @param beanDefinition
     * @param instance
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/17
     */
    private void init(BeanDefinition beanDefinition, Object instance) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (StringUtils.isNotBlank(beanDefinition.getInitMethodName())) {
            //获取所传入实例的初始化方法名称进行调用
            Method method = instance.getClass().getMethod(beanDefinition.getInitMethodName(), null);
            method.invoke(instance, null);
        }
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() {
        //执行单例的销毁方法
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition beanDefinition = entry.getValue();

            if (beanDefinition.isSingleton() && StringUtils.isNotBlank(beanDefinition.getDestroyMethodName())) {
                Object instance = this.singletonBeanMap.get(beanName);
                try {
                    //获取bean的销毁方法名通过invoke执行
                    Method method = instance.getClass().getMethod(beanDefinition.getDestroyMethodName(), null);
                    method.invoke(instance, null);
                } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                    logger.error("bean destruction method named [" + beanName + "] failed to execute ! exception:{}", e);
                }
            }
        }
    }

    /**
     * 别名注册
     *
     * @param beanName
     * @param alias
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    @Override
    public void registerAlias(String beanName, String alias) throws Exception {
        Objects.requireNonNull(beanName, "beanName cannot be empty !");
        Objects.requireNonNull(alias, "alias cannot be empty !");

        //跟已有的所有别名进行对比，校验唯一性，存在重复的直接抛异常
        if (this.checkAliasExists(alias)) {
            throw new AliasRegistryException("alias: [" + alias + "] already exists !");
        }

        //bean没有起过别名则直接添加，否则获取已定义的别名数组进行操作
        if (!aliasMap.containsKey(beanName)) {
            //不存在重复的别名直接添加
            String[] aliasArray = {alias};
            aliasMap.put(beanName, aliasArray);
        } else {
            String[] aliasArray = aliasMap.get(beanName);

            //将原数组所有数据拷贝至新数组中并且把新注册的别名添加到新数组最后
            String[] newAliasArray = Arrays.copyOf(aliasArray, aliasArray.length + 1);
            newAliasArray[aliasArray.length] = alias;

            //重置该key的值
            aliasMap.replace(beanName, newAliasArray);
        }
    }

    /**
     * 是否为别名
     *
     * @param name
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    @Override
    public boolean isAlias(String name) {
        Objects.requireNonNull(name, "name cannot be empty !");

        //如果key存在该名称，说明是别名的别名，一定存在
        if (aliasMap.containsKey(name)) {
            return true;
        }
        return checkAliasExists(name);
    }

    /**
     * 获取所有别名
     *
     * @param name
     * @return java.lang.String[]
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    @Override
    public String[] getAliases(String name) {
        return aliasMap.get(name);
    }

    /**
     * 获取原名
     *
     * @param name
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    @Override
    public String getOriginalName(String name) {
        Objects.requireNonNull(name, "alias cannot be empty !");

        //遍历aliasMap 找包含alias的key返回
        String beanName = null;

        for (Map.Entry<String, String[]> entry : this.aliasMap.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();

            for (String alias : value) {
                if (alias.equals(name)) {
                    beanName = key;
                    break;
                }
            }
        }
        return beanName;
    }

    /**
     * 别名注销
     *
     * @param alias
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    @Override
    public void removeAlias(String alias) {
        Objects.requireNonNull(alias, "alias cannot be empty !");

        for (Map.Entry<String, String[]> entry : this.aliasMap.entrySet()) {
            String key = entry.getKey();
            List<String> list = Arrays.asList(entry.getValue());
            for (String str : list) {
                if (str.equals(alias)) {
                    list.remove(str);
                    //检查是否有别名的别名，一同删除
                    if (aliasMap.containsKey(str)) {
                        aliasMap.remove(str);
                    }
                }
            }
            String[] newArray = (String[]) list.toArray();

            //重置该key的值
            aliasMap.replace(key, newArray);
        }
    }

    /**
     * 校验唯一性
     *
     * @param alias
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/28
     */
    private boolean checkAliasExists(String alias) {
        for (Map.Entry<String, String[]> entry : aliasMap.entrySet()) {
            String[] value = entry.getValue();
            for (String str : value) {
                if (str.equals(alias)) {
                    return true;
                }
            }
        }
        return false;
    }
}
