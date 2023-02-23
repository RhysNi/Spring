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

![image-20230216232054068](https://article.biliimg.com/bfs/article/9b0e12de3cf00da6eef52114913703018a8eee58.png)

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
>   - DCL单例（`Double Check` +`synchronized` ）

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

### IoC增强功能

#### Bean别名

> Bean除了标识唯一的名称外，还可以有很多个别名，别名具备以下几个特点
>
> - 