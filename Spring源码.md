# Spring源码

## Inversion of Control

### 什么是IoC

> 原来我们需要自己手动创建对象

```java
Test test = new Test();
```

> 控制反转 -> 获取对象的方式被反转了

![image-20230206220403788](https://article.biliimg.com/bfs/article/598b3491595f3a3696c4966056717d7911b83640.png)

> **IoC容器是工厂模式的实例**
>
> **IoC容器负责来创建类实例对象，需要从IoC容器中get获取。IoC容器我们也称为Bean工厂**
>
> **`bean`：组件,也就是类的对象!**

![image-20230206233921048](https://article.biliimg.com/bfs/article/8c7455b7034df73b9f96f672c4016e9ca99768ca.png)

### IoC的优点

- 代码更加简洁，不需要new对象
- 面向接口编程，使用者与具体类，解耦，易扩展、替换实现者
- 可以方便进行AOP编程

### IoC容器做了什么工作

> **负责创建，管理类实例，向使用者提供实例**

![image-20230206233240548](https://article.biliimg.com/bfs/article/8157754d5c5d0fa0cfb9280fc6864b60071323a0.png)

## 手写IoC实现

### BeanFactory作用

> - 创建-管理`Bean`，同时对外提供`Bean`的实例

### BeanFactory设计 

> - 由于BeanFactory需要对外提供`Bean`实例，所以需要一个`getBean()`方法
>
> - BeanFactory需要知道生产哪个类型的`bean`，所以需要接受`bean`的名称来获取到对应的bean类型，返回对应的实例
> - 由于`bean`是有多个以`key-value`形式记录在Map中，所以返回类型是不确定且不唯一的，我们使用`Object`类型作为返回类型
>   - 这边作为接口防止实现类中抛出异常，咱们手动`throws Exception`，这样无论实现类中抛出什么异常都可以接住了	

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:08 AM
 */
public interface BeanFactory {
    Object getBean(String beanName) throws Exception;
}
```

> 上面定义了Bean工厂对外提供`bean`实例的方法，那么BeanFactory怎么知道该创建哪个对象，又是怎么创建对象的呢？
>
> **首先我们得把Bean的定义信息告诉`BeanFactory`,然后`BeanFactory`根据Bean的定义信息来生成对应的bean实力对象**
>
> - 因此我们需要定义一个模型（`BeanDefinition`）来表示`如何创建Bean实例的信息`
> - 另外，`BeanFactory`也需要提供一个入参来接收`Bean的定义信息`

### BeanDefinition

#### Bean定义的作用

> 告诉`BeanFactory`应该如何创建某个类的`Bean实力`

#### 获取实例的方式有哪些

##### 构造方法

> `BeanDefinition`需要`给BeanFactory`提供`类名`

```java
Person person = new Person();
```

##### 工厂静态方法

> `BeanDefinition`需要`给BeanFactory`提供`工厂类名`、`工厂方法名`

```java
public interface PersonFactory {
    public static Person getPerson(){
      return new Person();
    }
}
```

##### 工厂成员方法

> `BeanDefinition`需要`给BeanFactory`提供`工厂bean名`、`工厂方法名`

```java
public interface PersonFactory {
    public Person getPerson(){
      return new Person();
    }
}
```

> 因此我们需要在`BeanDefinition`接口中具备以下几种功能

![image-20230214004511598](https://article.biliimg.com/bfs/article/6eac69528b22ea29a06f35d9b4b96b3f5a440bb4.png)

```java
public interface BeanDefinition {
    Class<?> getBeanClass();

    String getFactoryBeanName();

    String getFactoryMethodName();
}
```

#### BeanDefinition增强功能

> - 在现有基础上增加单例对象的初始化和销毁功能等
> - 同时创建一个通用实现类`GenericBeanDefinition`

![image-20230214023112763](https://article.biliimg.com/bfs/article/9c97a8fd005a97b5d3114146afcb206d25f978cb.png)

```java
package com.rhys.spring.IoC;

import org.apache.commons.lang.StringUtils;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:09 AM
 */
public interface BeanDefinition {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    /**
     * 对外提供具体的Bean类
     *
     * @param
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    Class<?> getBeanClass();

    /**
     * 对外提供工厂bean名
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getFactoryBeanName();

    /**
     * 对外提供工厂方法名
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getFactoryMethodName();

    /**
     * 初始化方法
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getInitMethodName();

    /**
     * 销毁方法
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getDestroyMethodName();

    /**
     * 作用域
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return java.lang.String
     */
    String getScope();

    /**
     * 是否是单例
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return boolean
     */
    boolean isSingleton();

    /**
     * 是否是原型
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return boolean
     */
    boolean isPrototype();

    /**
     * 是否是主要
     * @author Rhys.Ni
     * @date 2023/2/14
     * @param
     * @return boolean
     */
    boolean isPrimary();


    /**
     * 校验bean定义的合法性
     *
     * @param
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/7
     */
    default boolean validate() {
        //没定义class,工厂bean或工厂方法没指定的均不合法
        if (this.getBeanClass() == null) {
            if (StringUtils.isBlank(this.getFactoryBeanName()) || StringUtils.isBlank(this.getFactoryMethodName())) {
                return false;
            }
        }

        //定义了类，又定义工厂bean认为不合法，这里都不为空则满足以上条件，任意一个为空则校验通过
        return this.getBeanClass() == null || StringUtils.isBlank(this.getFactoryBeanName());
    }
}

```

#### BeanDefinition默认实现

![image-20230301020607759](https://article.biliimg.com/bfs/article/0b6a01b83fe9dbf1e8f51a14a37cbf72aaad5fc5.png)

```java
public class GenericBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;

    private String scope = SCOPE_SINGLETON;

    private String factoryBeanName;

    private String factoryMethodName;

    private String initMethodName;

    private String destoryMethodName;

    private boolean primary;

    /**
     * 对外提供具体的Bean类
     *
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    /**
     * 对外提供工厂bean名
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getFactoryBeanName() {
        return this.factoryBeanName;
    }

    /**
     * 对外提供工厂方法名
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getFactoryMethodName() {
        return this.factoryMethodName;
    }

    /**
     * 初始化方法
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getInitMethodName() {
        return this.initMethodName;
    }

    /**
     * 销毁方法
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getDestroyMethodName() {
        return this.destoryMethodName;
    }

    /**
     * 作用域
     *
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public String getScope() {
        return this.scope;
    }

    /**
     * 是否是单例
     *
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(this.scope);
    }

    /**
     * 是否是原型
     *
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(this.scope);
    }

    /**
     * 是否是主要
     *
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    @Override
    public boolean isPrimary() {
        return this.primary;
    }


    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public void setScope(String scope) {
        if (StringUtils.isNotBlank(scope)) {
            this.scope = scope;
        }
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }

    public void setFactoryMethodName(String factoryMethodName) {
        this.factoryMethodName = factoryMethodName;
    }

    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    public void setDestoryMethodName(String destoryMethodName) {
        this.destoryMethodName = destoryMethodName;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GenericBeanDefinition that = (GenericBeanDefinition) o;

        return new EqualsBuilder()
                .append(primary, that.primary)
                .append(beanClass, that.beanClass)
                .append(scope, that.scope)
                .append(factoryBeanName, that.factoryBeanName)
                .append(factoryMethodName, that.factoryMethodName)
                .append(initMethodName, that.initMethodName)
                .append(destoryMethodName, that.destoryMethodName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(beanClass).append(scope)
                .append(factoryBeanName)
                .append(factoryMethodName)
                .append(initMethodName)
                .append(destoryMethodName)
                .append(primary)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "GenericBeanDefinition{" +
                "beanClass=" + beanClass +
                ", scope='" + scope + '\'' +
                ", factoryBeanName='" + factoryBeanName + '\'' +
                ", factoryMethodName='" + factoryMethodName + '\'' +
                ", initMethodName='" + initMethodName + '\'' +
                ", destoryMethodName='" + destoryMethodName + '\'' +
                ", primary=" + primary +
                '}';
    }
}
```

### BeanDefinitionRegistry

> 清楚了`BeanDefinition`与`BeanFactory`后，那么他们之间有什么关联呢？
>
> - 需要定义一个`BeanDefinitionRegistry`用来实现`BeanDefinition`的注册功能
> - `BeanDefinitionRegistry`需要具备`注册BeanDefinition`和`获取BeanDefinition`能力
> - 为了能区分每个`Bean`的定义信息，还需要给每个`Bean`定义一个唯一名称

![image-20230216112804729](https://article.biliimg.com/bfs/article/3089c16936978dcc654c271f688b5f62954749ee.png)

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:09 AM
 */
public interface BeanDefinitionRegistry {

    /**
     * 注册BeanDefinition
     *
     * @param beanName       - bean名称
     * @param beanDefinition - bean定义
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    void registryBeanDefinition(String beanName, BeanDefinition beanDefinition);

    /**
     * 获取BeanDefinition
     *
     * @param beanName - bean名称
     * @return com.rhys.spring.IoC.BeanDefinition
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    BeanDefinition getBeanDefinition(String beanName);

    /**
     * 判断是否已经存在
     *
     * @param beanName - bean名称
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/14
     */
    boolean containsBeanDefinition(String beanName);
}
```

### DefaultBeanFactory实现

> 综上，目前已经实现的相关功能设计如下：
>
> - 已经设计好了`BeanFactory`、`BeanDefinition`、`BeanDefinitionRegistry`
> - 现在需要实现一个默认的Bean工厂`DefaultBeanFactory`

![image-20230301021118560](https://article.biliimg.com/bfs/article/f243aefcbd373e891781f846450f6f278d9eb473.png)

> 实现`BeanDefinitionRegistry`,这里有以下几个注意点
>
> - 如何存储`BeanDefinition`
>   - 使用`Map<String,Object>`
> - `beanName`重名怎么办
>   - Spring中`默认`是`不可覆盖`，直接抛异常
>   - 可通过参数 `spring.main.allow-bean-definition-overriding: true `来允许覆盖
> - 先实现`registryBeanDefinition`、`getBeanDefinition`、`containsBeanDefinition`
>   - 这里需要先定义一个`beanDefinitionMap`来存放合法的`beanDefinition`
>   - 后面比较以及获取`beanDefinition`都以这个Map中的数据为基准

```java
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

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
}
```

> 实现初始化方法的执行,在下面`getBean`方法中将`创建实例`时会执行所传入的`BeanDefinition`中的`初始化方法`,所以先实现一下

```java
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
```

> 实现`BeanFactory`的`getBean`方法
>
> 这里边要注意`单例的实现`:
>
> - 单例存储在哪里
>   - 与`BeanDefinition`存储方式相同,定义一个私有成员变量`singletonBeanMap`来存储单例数据
> - 怎么保证单例
>   - DCL单例

```java
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
```

> 实现容器关闭是执行单例的销毁操作

```java
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
        logger.error("bean destruction method named [" + beanName + "] failed to execute ! exception:{}",e);
      }
    }
  }
}
```

> - 所谓`单例`必然是只有一个对象，所以需要将单例Bean的实例化提前到初始化阶段，在系统启动的时候就去将单例的对象创建出来，避免在运行时调用`doGetBean`方法判断为单例再去执行创建
> - 因此我们需要实现一个`PreBuildBeanFactory`，将单例对象的创建操作放到这个里面去实现
> - 这种方案缺点就是增加了系统启动时的性能消耗
> - 优点就是减少了系统运行时的性能消耗
> - 因为系统启动的时候是`单线程`,提前创建单例也很好的解决了`getBean`的时候单例的保证,`双重检查`的时候也不会有问题了

![image-20230222170836138](https://article.biliimg.com/bfs/article/1278df1be57738088174eec9d914699bb0cd6e45.png)

```java
public class PreBuildBeanFactory extends DefaultBeanFactory {
    private static final Logger logger = LoggerFactory.getLogger(PreBuildBeanFactory.class);

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
```

### BeanAlias别名实现

> Bean除了标识唯一的名称外，还可以有很多个别名，别名具备以下几个特点
>
> - 可以存在多个别名
> - 可以在有别名的基础上，给别名起别名
> - 别名需要保证唯一
>
> 因此，我们需要一个具备 `别名注册`相关功能的接口如下设计

![image-20230228003858909](https://article.biliimg.com/bfs/article/e4a7f11e3d30a22f26dbdaae6cbdeeb5964e43de.png)

> 别名的别名大概如下

![image-20230301024040172](https://article.biliimg.com/bfs/article/aa9757c476d5d817efeed702718bc1821aea2f2c.png)

```java
package com.rhys.spring.IoC;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/27 1048 PM
 */
public interface AliasRegistry {
    /**
     * 别名注册
     *
     * @param name
     * @param alias
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    void registerAlias(String name, String alias);

    /**
     * 是否为别名
     *
     * @param name
     * @return boolean
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    boolean isAlias(String name);

    /**
     * 获取所有别名
     *
     * @param name
     * @return java.lang.String[]
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    String[] getAliases(String name);

    /**
     * 获取原名
     *
     * @param name
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    String getOriginalName(String name);

    /**
     * 别名注销
     *
     * @param alias
     * @return void
     * @author Rhys.Ni
     * @date 2023/2/27
     */
    void removeAlias(String alias);
}
```

> `DefaultBeanFactory`中具体实现如下

```java
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
```

### BeanType获取Bean实例对象

> 为了解决只能单一的通过`beanName`获取bean对象，中间需要进行强制类型转换，可能会出现异常情况，因此增加根据`Type`获取实例对象，`BeanFactory`修改设计如下
>
> - 提前把`Type`和`Bean`关系找出来缓存到`Map`中
>   - 在`BeanFactory`中新增一个`getType`方法提供`根据beanName获取beanClass类型`的功能
>   - 再到`DefaultBeanFactory`中添加一个`typeMap`容器，Map数据结构设计为`Map<Class<?>, Set<String>>`
>   - 最后通过`buildTypeMap()`方法来构建`Type-Bean`之间的映射关系
> - 因为`Class`可能对应`多个Type`,需要通过`Primary`来处理

![image-20230301031436711](https://article.biliimg.com/bfs/article/75cb17fd1383a1ec9a67f02ae3cf8bb440758315.png)

> `BeanFactory`增加功能方法

```java
package com.rhys.spring.IoC;

import java.util.List;
import java.util.Map;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/7 12:08 AM
 */
public interface BeanFactory {
    /**
     * 获取Bean实例
     *
     * @param beanName bean名称
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/2/16
     */
    Object getBean(String beanName) throws Exception;

    /**
     * 根据beanName获取Type
     *
     * @param beanName
     * @return java.lang.Class<?>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    Class<?> getType(String beanName) throws Exception;

    /**
     * 根据Type获取bean实例
     *
     * @param c
     * @return T
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    <T> T getBean(Class<T> c) throws Exception;

    /**
     * 根据Type获取bean实例
     *
     * @param c
     * @return java.util.Map<java.lang.String, T>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    <T> Map<String, T> getBeanOfType(Class<T> c) throws Exception;

    /**
     * 根据Type获取bean集合
     *
     * @param c
     * @return java.util.List<T>
     * @author Rhys.Ni
     * @date 2023/3/1
     */
    <T> List<T> getBeanListOfType(Class<T> c) throws Exception;
}
```

> `DefaultBeanFactory`中具体实现如下

```java
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


/**
 * 注册typeMap
 * @author Rhys.Ni
 * @date 2023/3/3
 * @param
 * @return void
 */
public void registerTypeMap() throws Exception {
    //获取type - name 映射关系，在所有的Bean定义信息都注册完成后执行
    for (String beanName : this.beanDefinitionMap.keySet()) {
        Class<?> type = this.getType(beanName);
        //映射本类
        this.registerTypeMap(beanName, type);
        //映射父类
        this.registerSuperClassTypeMap(beanName, type);
        //映射接口
        this.registerInterfaceTypeMap(beanName, type);
    }
}

/**
 * 注册typeMap 建立映射关系
 * @author Rhys.Ni
 * @date 2023/3/3
 * @param beanName
 * @param type
 * @return void
 */
private void registerTypeMap(String beanName, Class<?> type) {
    Set<String> beanNames2Type = this.typeMap.get(type);
    if (beanNames2Type != null) {
        //重置beanName - type 映射关系
        beanNames2Type = new HashSet<>();
        this.typeMap.put(type, beanNames2Type);
    }
    beanNames2Type.add(beanName);
}

/**
 * 递归注册父类实现的接口
 * @author Rhys.Ni
 * @date 2023/3/3
 * @param beanName
 * @param type
 * @return void
 */
private void registerSuperClassTypeMap(String beanName, Class<?> type) {
    Class<?> superclass = type.getSuperclass();
    if (superclass != null && !superclass.equals(Objects.class)) {
        this.registerTypeMap(beanName, superclass);
        //递归找父类
        this.registerSuperClassTypeMap(beanName, superclass);
        //找父类实现的接口
        this.registerInterfaceTypeMap(beanName, superclass);
    }
}


/**
 * 递归注册父接口
 * @author Rhys.Ni
 * @date 2023/3/3
 * @param beanName
 * @param type
 * @return void
 */
private void registerInterfaceTypeMap(String beanName, Class<?> type) {
    Class<?>[] interfaces = type.getInterfaces();
    if (interfaces.length > 0) {
        for (Class<?> anInterface : interfaces) {
            this.registerTypeMap(beanName, anInterface);
            //递归找父接口
            this.registerInterfaceTypeMap(beanName, anInterface);
        }
    }
}
```

### 总结

#### IoC核心类图总览

> -  BeanDefinition通过BeanDefinitionRegistry与BeanFactory关联起来
> - BeanFactory可以通过BeanDefinitionRegistry去找到想要的BeanDefinition
> - GenericBeanDefinition是BeanDefinition的默认实现
> - BeanDefinitionRegistry通过AliasRegistry实现了别名的处理（别名判断逻辑后续补充）
> - 最后在DeafultBeanFactory中进行BeanFactory、BeanDefinitionRegistry所有逻辑实现
> - PreBuildBeanFactory实现了单例Bean实例化提前到启动阶段

![image-20230301032645220](https://article.biliimg.com/bfs/article/9574c691f6eadc197717c7fa48260169fc300276.png)
