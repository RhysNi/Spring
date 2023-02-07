# Spring源码

## Inversion of Control

### 什么是IoC

> 原来我们需要自己手动创建对象

```java
Test test = new Test();
```

> 控制反转 -> 获取对象的方式被反转了

![image-20230206220403788](/Users/Rhys.Ni/Library/Application Support/typora-user-images/image-20230206220403788.png)

> **IoC容器是工厂模式的实例**
>
> **IoC容器负责来创建类实例对象，需要从IoC容器中get获取。IoC容器我们也称为Bean工厂**
>
> **`bean`：组件,也就是类的对象!**

![image-20230206233921048](/Users/Rhys.Ni/Library/Application Support/typora-user-images/image-20230206233921048.png)

### IoC的优点

- 代码更加简洁，不需要new对象
- 面向接口编程，使用者与具体类，解耦，易扩展、替换实现者
- 可以方便进行AOP编程

### IoC容器做了什么工作

> **负责创建，管理类实例，向使用者提供实例**

![image-20230206233240548](/Users/Rhys.Ni/Library/Application Support/typora-user-images/image-20230206233240548.png)

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

### BeanFactory实现

#### 实现BeanDefinitionRegistry

> 实现Bean定义信息的注册

```java

```

#### 实现BeanFactory的getBean方法

#### 实现init方法

#### 实现单例作用域

#### 实现容器关闭时单例的销毁操作