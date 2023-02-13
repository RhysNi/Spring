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

## IoC实现

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

### BeanDefinition增强功能

> - 在现有基础上增加单例对象的初始化和销毁功能等
> - 同时创建一个通用实现类`GenericBeanDefinition`

![image-20230214023112763](https://article.biliimg.com/bfs/article/9c97a8fd005a97b5d3114146afcb206d25f978cb.png)

```java
package com.rhys.spring.beans;

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

### Bean的注册

> 清楚了`BeanDefinition`与`BeanFactory`后，那么他们之间有什么关联呢？

> 实现Bean定义信息的注册

```java

```

#### 实现BeanFactory的getBean方法

#### 实现init方法

#### 实现单例作用域

#### 实现容器关闭时单例的销毁操作