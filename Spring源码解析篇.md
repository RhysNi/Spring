# Spring源码解析篇

## 源码依赖导入

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
    <spring.version>5.2.16.RELEASE</spring.version>
</properties>

<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>${spring.version}</version>
</dependency>
```

## ApplicationContext接口

> 应用的上下文

![ApplicationContext2](https://article.biliimg.com/bfs/article/d973af0d04e699af984fb1c05ce65a5176185d94.png)

> - `BeanFactory`是IoC容器，是工厂对象
> - `HierarchicalBeanFactory`和`ListableBeanFactory`接口是扩展了`BeanFactory`
> - `ApplicationContext`在`BeanFactory`的基础上扩展了一个`ApplicationEventPublisher`接口,具备应用事件发布相关的功能
> - `ResourceLoader`具备资源加载的功能,当我们通过`XML`等方式去做一些配置，`ResourceLoader`就会去加载配置文件
> - `MessageSource`是给国际化提供一些能力

### HierarchicalBeanFactory

![image-20230607000409516](https://article.biliimg.com/bfs/article/2d5db4e7b8bd1016264ee5daf110a08862e8645c.png)

> - 提供了`getParentBeanFactory`获取父容器的能力
> - 提供了`containsLocalBean`判断是否是本地Bean的能力
>
> Spring中有个父子容器的概念就是由这个接口提供的

### ListableBeanFactory

![image-20230607000321524](https://article.biliimg.com/bfs/article/0de675d1025ec7738e53baa2ffe47ac51870645b.png)

> - `BeanFactory`中生成并缓存了很多`Bean`对象，但是并没有提供操作这些容器的能力
> - 而`ListableBeanFactory`正是扩展了这些操作`BeanFactory`中容器的能力
> - 例如`getBeanDefinitionNames`获取所有Bean定义的名称
> - 例如`getBeansOfType`根据类型获取所有Bean等

### EnvironmentCapable

![image-20230607000653726](https://article.biliimg.com/bfs/article/eae1992206e642c4a256548c43e198f488e58858.png)

> 针对于我们的`系统环境参数`、`自定义环境参数`获取都是由这个接口提供的扩展能力去实现的

#### Environment

![image-20230607002032140](https://article.biliimg.com/bfs/article/3da8134ec1bde1fbab730c47674f526f98285517.png)

> 主要提供我们常用的`getActiveProfiles`、`getDefaultProfiles`获取配置文件的 这些行为

### ApplicationEventPublisher

![image-20230607001122795](https://article.biliimg.com/bfs/article/2c7db2f9edaa244cfac84a0115aab836ecb10c77.png)

> 基于观察者模式发布订阅所实现的，主要提供发布事件的行为

### ResourceLoader

![image-20230607001342030](https://article.biliimg.com/bfs/article/ff553f7f1243c82e2502353d162e7432c01ba600.png)

> 资源加载，主要提供`获取类加载器`、`获取资源文件`的行为

#### Resource

> 在Spring中相关的资源都会用`Resource`自定义对象封装，主要提供了一些基础判断，获取资源文件的相关行为	

![image-20230607001454224](https://article.biliimg.com/bfs/article/6a26135ec47d4fc42aa678c60b7f14218058fd0b.png)

### MessageSource

![image-20230607001738114](https://article.biliimg.com/bfs/article/4eaaaee65564d309002b6569e62bcbbe4be97b4b.png)

> 为国际化提供一些不同方式获取Message的行为



## BeanFactory

### ApplicationContext实现类

![image-20230607004123235](https://article.biliimg.com/bfs/article/e2b9c658dac22d7a5c5fe6a75092872b5ae40478.png)

#### ConfigurableApplicationContext

> 作为`ApplicationContext`的子接口，扩展了`Lifecycle`生命周期、`Closeable`销毁能力

##### AbstractApplicationContext

> 这个抽象类将`资源加载`、`环境参数`、`国际化`、`容器操作`、`集合处理`、`事件发布`等能实现的公共功能都在这里面实现了

###### AbstractRefreshableApplicationContext 

> 一些例如`refreshBeanFactory`刷新Bean工厂、`closeBeanFactory`销毁Bean工厂没法在`AbstractApplicationContext`中实现Bean配置的公共处理的就在此类中做具体的处理
>
> - BeanFactory的创建也是在`refreshBeanFactory`方法中实现的
> - 调用`createBeanFactory`方法
> - 然后`new DefaultListableBeanFactory`
> - 最终调用的是`ConfigurableApplicationContext`中的`getBeanFactory`

![image-20230607220035455](https://article.biliimg.com/bfs/article/2940780ce0343cf060045d9e49d9b3047195d356.png)

![image-20230607221357971](https://article.biliimg.com/bfs/article/d51b0b7481ea42f064a6b8897026307cff25ac4a.png)

![image-20230607221315267](https://article.biliimg.com/bfs/article/8bea52e362ccd2ffa879932f92feecc6a7e18081.png)

###### DefaultListableBeanFactory结构

![image-20230607233359448](https://article.biliimg.com/bfs/article/c5d8988e29dcdaa6b6e9d22455ef1b2a69f5c0a4.png)

###### GenericApplicationContext

> 主要做了XML可刷新的具体实现

## BeanDefinition生成的两种方式

### 基于XML方式

#### 定义相关Java类
```java
package com.rhys.testSourceCode.config.xml;

public class BeanE {

	public void doSomething() {
		System.out.println("-----" + this + " doSomething ");
	}
}
```

```java
package com.rhys.testSourceCode.config.xml;

import org.springframework.beans.factory.annotation.Autowired;

public class BeanF {

	@Autowired
	private BeanE be;

	public void do1() {
		System.out.println("----------" + this + " do1");
		this.be.doSomething();
	}
}

```

#### 定义XML文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        http://www.springframework.org/schema/context/spring-context.xsd">
	
	<bean id="beanE" class="com.rhys.testSourceCode.config.xml.BeanE" />
	
	<bean id="beanF" class="com.rhys.testSourceCode.config.xml.BeanF" ></bean>
	
	<context:annotation-config/>
	
	<context:component-scan base-package="com.rhys.testSourceCode" ></context:component-scan>
</beans>
```

#### 读取配置文件获取对应Bean

```java
package com.rhys.testSourceCode.config.xml;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

public class XMLConfigMain {

	public static void main(String[] args) {
		ApplicationContext context = new GenericXmlApplicationContext(
				"classpath:com/rhys/testSourceCode/config/xml/application.xml");
		BeanF bf = context.getBean(BeanF.class);
		bf.do1();
	}
}
```

#### 原理分析

> 既然知道了IOC的本质其实就是`DefaultlistableBeanFactory`，那么主要关注其中的`registerBeanDefinition`以及后续步骤即可
>
> - 首先`GenericXmlApplicationContext`会创建`XmlBeanDefinitionReader`用来解析XML文件标签中的Bean定义信息
> - 在`DefaultBeanDefinitionDocumentReader`中利用`BeanDefinitionParserDelegate`委托器将将解析得到的Bean定义信息转换成BeanDefinition对象并封装成`BeanDefinitionHolder`包含了`beanName`、`BeanDefinition`、`Alias`信息
> - 最终在`DeafultListableBeanFactory`中进行具体的Bean注册操作，从`beanDefinitionMap`获取已注册定义信息,没有且不存在正在创建的Bean则直接注册并缓存到`beanDefinitionMap`

![image-20230612012256411](https://article.biliimg.com/bfs/article/eaee6b675f381df8987bc826ecb33635fa31915c.png)

### 基于注解形式

#### 定义相关Java类

```java
package com.rhys.testSourceCode.config.annotation;

import com.rhys.testSourceCode.config.xml.BeanE;
import org.springframework.beans.factory.annotation.Autowired;

public class BeanH {

	@Autowired
	private BeanE be;

	public void doH() {
		System.out.println("-----------" + this + " doH");
		be.doSomething();
	}
}
```

```java
package com.rhys.testSourceCode.config.annotation;

import com.rhys.testSourceCode.config.xml.BeanF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@Component
@ImportResource("classpath:com/rhys/testSourceCode/config/xml/application.xml")
public class BeanG {
	@Autowired
	private BeanF beanf;

	public void dog() {
		System.out.println("----------------------------------------");
		this.beanf.do1();
	}
}
```

#### 定义配置类

> Sping中使用`@Configuration`注解修饰的类则为配置类
>
> - 在配置类中定义了`getBeanH`方法用来生成相关`BeanH`
> - `@ComponentScan("com.rhys.testSourceCode.config")`定义扫描路径

```java
package com.rhys.testSourceCode.config.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.rhys.testSourceCode.config")
public class JavaBasedMain {

	@Bean
	public BeanH getBeanH() {
		return new BeanH();
	}

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext(JavaBasedMain.class);

		BeanH bh = context.getBean(BeanH.class);
		bh.doH();
	}
}
```

#### 定义测试类

> 定义一个扫描路径

```java
package com.rhys.testSourceCode.config.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationMain {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext("com.rhys.testSourceCode.config");
		BeanG bg = context.getBean(BeanG.class);
		bg.dog();
	}
}
```

#### 原理分析

##### this构造方法

> 负责完成相关的配置处理

<img src="https://article.biliimg.com/bfs/article/f1089e2eddd1698a646c8af0698a5bcfaebc663c.png" alt="image-20230612031845433" style="zoom:200%;" />

###### ClassPathBeanDefinitionScanner

> 创建了一个对应的扫描器

###### AnnotatedBeanDefinitionReader

> - 主要完成核心的`ConfigurationClassPostProcessor`的注入
>
> - `ConfigurationClassPostProcessor` 会完成`@Configuration`注解的相关解析

![image-20230628215734249](https://article.biliimg.com/bfs/article/09e09206d9f953582aec6d9e6fbb16634451a8d4.png)

> - 回去校验是否为配置类
> - 不是则校验是否存在配置类注解,存在配置相关注解则缓存到`configCandidates`List中
> - 最后如果没有找到`@Configuration`注解修饰的配置类则直接返回

![image-20230628223922058](https://article.biliimg.com/bfs/article/432e2e38a8179fd1975476ded5ecccb563eb48af.png)

##### 扫描实现

![ClassPathBeanDefinitionScanner_scan.png](https://article.biliimg.com/bfs/article/4ef109e2e1dfa0bbbc8f9959cff42a402bea0fe3.png)

> 通过声明的`ClassPathBeanDefinitionScanner`扫描器进行扫描

![image-20230628225848352](https://article.biliimg.com/bfs/article/00ce2399265f473ef97c2481ebe0063d9e739cc3.png)

> 会去找到所有的候选组件（Bean）并封装成Bean定义

![image-20230628230417647](https://article.biliimg.com/bfs/article/85a16848fde89a9e2b2c3b8edc38ece74f74f53e.png)

> 最后会检查Bean是否已经存在，不存在则注册相关Bean定义

![image-20230628231141260](https://article.biliimg.com/bfs/article/6c22bf9f1af19ed74c7a3aebfc3e99eec1bf868b.png)

##### @Configuration注解

> @Configuration的解析是在refresh方法中来实现的

![image-20230629000234819](https://article.biliimg.com/bfs/article/08ef0e1b1855131c1779c3cf44c78535ec424b54.png)

![image-20230629000328418](https://article.biliimg.com/bfs/article/746ddbbf3ee7e8b9f0684ab60f3112bfe614c6fd.png)

### 小结

> 通过上面的分析对Bean定义的扫描，解析和注册过程归纳为以下三点：
>
> - AnnotatedBeanDefinitionReader（reader）解析XML，完成xml方法配置的bean定义
> - ClassPathBeanDefinitionScanner（scanner）扫描指定包下的类，找出带有@Component注解的类，注册成Bean定义
> - `ConfigurationClassPostProcessor`处理带有`@Configuration`注解的类，解析它上面的注解，以及类中带有`@Bean` 注解,加入这些Bean的定义

## BeanDefinition结构

> 继承`属性访问器`和`元数据接口`，增加了Bean定义操作，实现了数据和操作解耦

![BeanDefinition](https://article.biliimg.com/bfs/article/1d4d933bf20644c901a0d04593b04d4ac8c9b57d.png)

#### BeanMetadataElement

> 提供了获取数据源的方式，也就是可以知道Bean是来自哪个类

![image-20230629224203447](https://article.biliimg.com/bfs/article/d2eead9f0cc242a4a80c80cf4b6eebcd01684939.png)

##### BeanMetadataAttribute

> 实现了`BeanMetadataElement`接口，增加了属性的名字和值

![image-20230629224525735](https://article.biliimg.com/bfs/article/cc6856c505cc9e7dbeb1b09371d1bdc0838de1ba.png)

![image-20230629224739434](https://article.biliimg.com/bfs/article/f31a428dc2c3d219e889da23505b5cf1f2759bf6.png)

#### AttributeAccessor

> 属性访问器，给Bean定义了增删改查属性的功能

![image-20230629225108740](https://article.biliimg.com/bfs/article/7dd8bf7cd071765fe77607cc5e46a168ae8d03b0.png)

##### AttributeAccessorSupport

> 属性访问抽象实现类,内部定义了1个map来存放属性

![image-20230630001154412](https://article.biliimg.com/bfs/article/c2249b5da383ebdaec7bcc40163e224b5411049e.png)

![image-20230630001315480](https://article.biliimg.com/bfs/article/0a6ec869ea7e7a56d7d9e3df8d95fc193259ca3e.png)

##### BeanMetadataAttributeAccessor

> 元数据属性访问器
>
> 继承AttributeAccessorSupport具备属性访问功能，实现BeanMetadataElement具备获取元数据功能。
>
>  **AbstractBeanDefinition就继承于它，使得同时具有属性访问和元数据访问的功能 **

![BeanMetadataAttributeAccessor](https://article.biliimg.com/bfs/article/018557d8cff6e37b92219caff528e534636d12d6.png)

> 结合AbstractBeanDefinition的类图结构

![AbstractBeanDefinition](https://article.biliimg.com/bfs/article/f3799100deee660f452b9ac36446895201a87955.png)

## BeanDefinition继承体系

### AnnotatedBeanDefinition

> - 增加了2个方法，获取bean所在类的注解元数据和工厂方法元数据，这些数据在进行解析处理的时候需要用到
>
> - 该接口有三个具体实现，分别是`ScannedGenericBeanDefinition`、`AnnotatedGenericBeanDefinition`、`ConfigurationClassBeanDefinition`

![image-20230630002918722](https://article.biliimg.com/bfs/article/b166c5f4d2f01ad78b4a9edd0fabccd42f01a2cf.png)

### AbstractBeanDefinition

> 我们可以称之为BeanDefinition的模板类, 具备了 Bean元数据的获取和属性相关的操作

![AbstractBeanDefinition](https://article.biliimg.com/bfs/article/410085584e0dc56af40abd398747e707ae1d1335.png)

#### 继承结构

![AbstractBeanDefinition](https://article.biliimg.com/bfs/article/f8482d30827bf0d92ef66dfd0b0819dc41ac52ba.png)

##### RootBeanDefinition

> 根bean定义
>
> - 主要用在spring内部的bean定义、把不同类型的bean定义合并成RootBeanDefinition（getMergedLocalBeanDefinition方法）
> - 没有实现BeanDefinition接口的设置获取父bean定义方法，不支持设置父子beanDefinition

##### ConfigurationClassBeanDefinition

> 用作ConfigurationClassPostProcessor解析过程中封装配置类的bean定义

##### GenericBeanDefinition

> 通用Bean的定义

##### ScannedGenericBeanDefinition

> `@ComponentScan`扫描的bean定义使用

##### AnnotatedGenericBeanDefinition

> GenericBeanDefinition 类的扩展，添加了对通过 AnnotatedBeanDefinition 接口公开的注释元数据的支持
