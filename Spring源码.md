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

### BeanFactory实现

#### 实现BeanDefinitionRegistry

> 实现Bean定义信息的注册

```java

```

#### 实现BeanFactory的getBean方法

#### 实现init方法

#### 实现单例作用域

#### 实现容器关闭时单例的销毁操作