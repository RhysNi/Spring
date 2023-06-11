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



## Bean工厂创建

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

## BeanDefinition两种方式

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

> 负责完成相关的配置处理f

<img src="https://article.biliimg.com/bfs/article/f1089e2eddd1698a646c8af0698a5bcfaebc663c.png" alt="image-20230612031845433" style="zoom:200%;" />
