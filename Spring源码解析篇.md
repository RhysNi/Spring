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

## IoC初始化过程

### refresh方法

```java
@Override
	public void refresh() throws BeansException, IllegalStateException {
		synchronized (this.startupShutdownMonitor) {
			// Prepare this context for refreshing.
      // 准备此上下文以进行刷新
			prepareRefresh();

			// Tell the subclass to refresh the internal bean factory.
      //让子类实现刷新内部持有BeanFactory
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();

			// Prepare the bean factory for use in this context.
      // 对beanFactory做一些准备工作：注册一些context回调、bean等
			prepareBeanFactory(beanFactory);

			try {
				// Allows post-processing of the bean factory in context subclasses.
        // 调用留给子类来提供实现逻辑的 对BeanFactory进行处理的钩子方法
				postProcessBeanFactory(beanFactory);

				// Invoke factory processors registered as beans in the context.
        // 执行context中注册的 BeanFactoryPostProcessor bean
				invokeBeanFactoryPostProcessors(beanFactory);

				// Register bean processors that intercept bean creation.
        // 注册BeanPostProcessor: 获得用户注册的BeanPostProcessor实例，注册到BeanFactory上
				registerBeanPostProcessors(beanFactory);

				// Initialize message source for this context.
        // 初始化国际化资源
				initMessageSource();

				// Initialize event multicaster for this context.
        // 初始化应用程序事件广播器
				initApplicationEventMulticaster();

				// Initialize other special beans in specific context subclasses.
        // 初始化由子类来提供实现逻辑的钩子函数
				onRefresh();

				// Check for listener beans and register them.
        // 注册ApplicationListener: 获取用户注册的ApplicationListener Bean实例，注册到广播器上
        registerListeners();

				// Instantiate all remaining (non-lazy-init) singletons.
        // 完成剩余的单例Bean的实例化
				finishBeanFactoryInitialization(beanFactory);

				// Last step: publish corresponding event.
        // 发布对应的应用事件
				finishRefresh();
			}

			catch (BeansException ex) {
				if (logger.isWarnEnabled()) {
					logger.warn("Exception encountered during context initialization - " +
							"cancelling refresh attempt: " + ex);
				}

				// Destroy already created singletons to avoid dangling resources.
				destroyBeans();

				// Reset 'active' flag.
				cancelRefresh(ex);

				// Propagate exception to caller.
				throw ex;
			}

			finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				resetCommonCaches();
			}
		}
	}
```

#### prepareRefresh

> 准备此上下文以进行刷新，设置其启动日期和活动标志，以及执行所有环境参数的初始化。

```java
protected void prepareRefresh() {
  // Switch to active.
  // 设置相关的状态
  this.startupDate = System.currentTimeMillis();
  this.closed.set(false);
  this.active.set(true);

  if (logger.isDebugEnabled()) {
    if (logger.isTraceEnabled()) {
      logger.trace("Refreshing " + this);
    }
    else {
      logger.debug("Refreshing " + getDisplayName());
    }
  }

  // Initialize any placeholder property sources in the context environment.
  // 初始化上下文环境中的任何占位符属性源
  initPropertySources();

  // Validate that all properties marked as required are resolvable:
  // 验证所有标记为required的属性都是可解析的:
  // see ConfigurablePropertyResolver#setRequiredProperties
  getEnvironment().validateRequiredProperties();

  // Store pre-refresh ApplicationListeners...
  // 存储预刷新ApplicationListeners
  if (this.earlyApplicationListeners == null) {
    this.earlyApplicationListeners = new LinkedHashSet<>(this.applicationListeners);
  }
  else {
    // Reset local application listeners to pre-refresh state.
    // 将本地应用程序侦听器重置为预刷新状态
    this.applicationListeners.clear();
    this.applicationListeners.addAll(this.earlyApplicationListeners);
  }

  // Allow for the collection of early ApplicationEvents,
  // to be published once the multicaster is available...
  // 允许收集早期的ApplicationEvents，一旦广播器可用就发布…
  this.earlyApplicationEvents = new LinkedHashSet<>();
}
```

#### <a id= "obtainFreshBeanFactory">obtainFreshBeanFactory </a>

> 告诉子类刷新内部 Bean 工厂
>
> - 主要负责完成Bean工厂的刷新
> - 如果是基于配置文件的方式来定义的Bean，则会完成对应XML文件的加载解析，BeanDefinition对象的创建，以及通过BeanDefinitijonRegistry将BeanDefinition和BeanFactory关联起来
> - 如果是`@Compoent`注解修饰的类也会一并处理并添加到`BeanFactory`中
> - 🔈有一点要注意的是：虽然处理了`@Compoent`注解修饰的类，但是并没有处理`@Configuration`修饰的类 

```java
@Override
public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
    // 刷新容器
    refreshBeanFactory();
    return getBeanFactory();
}
```

> 这边的`getBeanFactory`其实调用到了当前抽象类的默认子实现类`GenericApplicationContext`类中的`getBeanFactory`方法，而`getBeanFactory`方法的默认`IOC容器(BeanFactory实现)`就是`DefaultListableBeanFactory`

![image-20230730232526640](https://article.biliimg.com/bfs/article/ca5aaec32c0a3f04688490f8e4606a8a8ce91211.png)

```java
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

	private final DefaultListableBeanFactory beanFactory;

	@Nullable
	private ResourceLoader resourceLoader;

	private boolean customClassLoader = false;

	private final AtomicBoolean refreshed = new AtomicBoolean();


	/**
	 * Create a new GenericApplicationContext.
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext() {
		this.beanFactory = new DefaultListableBeanFactory();
	}

		/**
	 * Return the single internal BeanFactory held by this context
	 * (as ConfigurableListableBeanFactory).
	 */
	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		return this.beanFactory;
	}
}
```

#### prepareBeanFactory

>  配置工厂的标准上下文特征，例如上下文的类加载器和后处理器。

```java
protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    // Tell the internal bean factory to use the context's class loader etc.
    // 设置beanFactory的classloader为当前context的classloader
    beanFactory.setBeanClassLoader(getClassLoader());
    // 设置beanfactory的表达式语言处理器
    beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
    // 为beanFactory增加一个默认的propertyEditor，这个主要是对bean的属性等设置管理的一个工具类
    beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));

    // Configure the bean factory with context callbacks.
    // 添加beanPostProcessor,ApplicationContextAwareProcessor此类用来完成某些Aware对象的注入
    beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
    // 设置要忽略自动装配的接口，因为这些接口的实现是由容器通过set方法进行注入的，所以在使用autowire进行注入的时候需要将这些接口进行忽略
    beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
    beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
    beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
    beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
    beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);

    // BeanFactory interface not registered as resolvable type in a plain factory.
    // MessageSource registered (and found for autowiring) as a bean.
    // 设置几个自动装配的特殊规则,当进行IoC初始化的时候如果有多个实现，那么就使用指定的对象进行注入
    beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
    beanFactory.registerResolvableDependency(ResourceLoader.class, this);
    beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
    beanFactory.registerResolvableDependency(ApplicationContext.class, this);

    // Register early post-processor for detecting inner beans as ApplicationListeners.
    // 注册BeanPostProcessor
    beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));

    // Detect a LoadTimeWeaver and prepare for weaving, if found.
    // 增加对AspectJ的支持，在java中织入分为三种方式，分为编译器织入，类加载器织入，运行期织入，编译器织入是指在java编译器，采用特殊的编译器，
    // 将切面织入到java类中，而类加载期织入则指通过特殊的类加载器，在类字节码加载到JVM时，织入切面，运行期织入则是采用cglib和jdk进行切面的织入,
    // AspectJ提供了两种织入方式: 
    // 		第一种：通过特殊编译器，在编译器，将aspectj语言编写的切面类织入到java类中
    // 		第二种：类加载期织入，就是下面的load time weaving
    if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
        // Set a temporary ClassLoader for type matching.
        // 为类型匹配设置临时类加载器
        beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
    }

    // Register default environment beans.
    // 注册默认的系统环境bean到一级缓存中
    if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
        beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
    }
    if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
        beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
    }
    if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
        beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
    }
}
```

#### invokeBeanFactoryPostProcessors

> 是BeanFactory的后置处理方法。核心是会完成注册的BeanFactoryPostProcessor接口和BeanDefinitionRegistryPostProcessor的相关逻辑
>
> - 由于`BeanFactoryPostProcessor`接口中没有提供`BeanDefinition`注册的能力，因此拓展了一个`BeanDefinitionRegistryPostProcessor`接口作为`BeanFactoryPostProcessor`的子接口，同时对外提供了一个`postProcessBeanDefinitionRegistry`方法，带入一个`BeanDefinitionRegistry`，也就是拓展了一个注册的功能，负责将一些特定的`BeanDefinition`通过`BeanDefinitionRegistry`注册到`BeanFactory`容器中
> - 那么具体的注册逻辑其实就是调用到`ConfigurationClassPostProcessor`类中的`processConfigBeanDefinitions`方法，这里面主要就是处理`@Configuration`修饰的类，包括

```java
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

    // Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime
    // (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
    if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
        beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
        beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
    }
}
```

> PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());

```java
public static void invokeBeanFactoryPostProcessors( ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

    // Invoke BeanDefinitionRegistryPostProcessors first, if any.
    // 无论是什么情况，优先执行BeanDefinitionRegistryPostProcessors,将已经执行过的BFPP存储在processedBeans中，防止重复执行
    Set<String> processedBeans = new HashSet<>();
	
    // 判断beanFactory是否为BeanDefinitionRegistry类型，因为这里的beanFactory是obtainFreshBeanFactory方法中getBeanFactory得到的ConfigurableListableBeanFactory，而ConfigurableListableBeanFactory是从GenericApplicationContext中getBeanFactory()得到的DefaultListableBeanFactory类，而DefaultListableBeanFactory类又同时实现了BeanDefinitionRegistry，因此结果是true
    if (beanFactory instanceof BeanDefinitionRegistry) {
        //类型强转
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        
        //BeanDefinitionRegistryPostProcessor是BeanFactoryPostProcessor的子集
        //BeanFactoryPostProcessor主要针对的操作对象是BeanFactory
        //BeanDefinitionRegistryPostProcessor主要针对的操作对象是BeanDefinition
        List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
        List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
		
        // 遍历beanFactoryPostProcessors集合，将BeanDefinitionRegistryPostProcessor和BeanFactoryPostProcessor做一个分类
        // 分别存放到对应的registryProcessors集合和regularPostProcessors集合中
        for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
            // 如果是BeanDefinitionRegistryPostProcessor
            if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
                // 直接执行BeanDefinitionRegistryPostProcessor接口中的postProcessBeanDefinitionRegistry方法
                registryProcessor.postProcessBeanDefinitionRegistry(registry);
                // 添加到registryProcessors集合，用于后续执行postProcessBeanFactory方法
                registryProcessors.add(registryProcessor);
            }
            else {
                //如果是普通的BeanFactoryPostProcessor，添加到regularPostProcessors，用于后续执行postProcessBeanFactory方法
                regularPostProcessors.add(postProcessor);
            }
        }

        // Do not initialize FactoryBeans here: We need to leave all regular beans
        // uninitialized to let the bean factory post-processors apply to them!
        // Separate between BeanDefinitionRegistryPostProcessors that implement
        // PriorityOrdered, Ordered, and the rest.
        // 保留本次所有要用到的未初始化BeanDefinitionRegistryPostProcessor
        List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

        // First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
        // 调用所有实现PriorityOrdered接口的BeanDefinitionRegistryPostProcessor实现类
        // 找到所有实现BeanDefinitionRegistryPostProcessor接口bean的BeanName
        String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
        for (String ppName : postProcessorNames) {
            // 检测是否实现了PriorityOrdered接口
            if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                // 获取名字对应的bean实例，添加到currentRegistryProcessors中
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                // 将要被执行的BFPP名称添加到processedBeans，避免后续重复执行
                processedBeans.add(ppName);
            }
        }
        // 按照优先级进行排序
        sortPostProcessors(currentRegistryProcessors, beanFactory);
        // 添加到registryProcessors中，用于最后执行postProcessBeanFactory方法
        registryProcessors.addAll(currentRegistryProcessors);
        // 遍历currentRegistryProcessors，执行postProcessBeanDefinitionRegistry方法
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
        // 执行完成后清空currentRegistryProcessors集合
        currentRegistryProcessors集合.clear();

        // Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
        // 调用所有实现了Ordered类的BeanDefinitionRegistryPostProcessors实现类
        // 找到所有实现BeanDefinitionRegistryPostProcessor接口的BeanName
        postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
        for (String ppName : postProcessorNames) {
            // 校验是否为已经执行过的BFPP，并且是否实现了Ordered接口
            if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
                // 获取名字对应的BFPP bean实例，添加到currentRegistryProcessors中
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                // 将要被执行的BFPP名称添加到processedBeans，避免后续重复执行
                processedBeans.add(ppName);
            }
        }
        // 按照优先级排序
        sortPostProcessors(currentRegistryProcessors, beanFactory);
        // 添加到registryProcessors集合中，用于最后执行postProcessBeanFactory方法
        registryProcessors集合中，用于最后执行.addAll(currentRegistryProcessors);
        // 遍历currentRegistryProcessors，执行postProcessBeanDefinitionRegistry方法
        invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
        // 执行完成后清空currentRegistryProcessors集合
        currentRegistryProcessors.clear();

        // Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
        // 最后调用所有剩下的BeanDefinitionRegistryPostProcessors
        boolean reiterate = true;
        while (reiterate) {
            reiterate = false;
            // 找出所有实现了BeanDefinitionRegistryPostProcessor接口的类并获取到对应的BeanName
            postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
            for (String ppName : postProcessorNames) {
                // 跳过已经执行过的BeanDefinitionRegistryPostProcessor
                if (!processedBeans.contains(ppName)) {
                    // 获取对应BeanName的Bean实例添加到currentRegistryProcessors中
                    currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                    // 将要执行的BFPP名称添加到processedBeans中，防止重复执行
                    processedBeans.add(ppName);
                    reiterate = true;
                }
            }
            // 按照优先级进行排序
            sortPostProcessors(currentRegistryProcessors, beanFactory);
            // 添加到registryProcessors集合中，用于最后执行postProcessBeanFactory方法
            registryProcessors.addAll(currentRegistryProcessors);
            // 遍历currentRegistryProcessors，执行postProcessBeanDefinitionRegistry方法
            invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
            // 执行完成后清空currentRegistryProcessors集合
            currentRegistryProcessors.clear();
        }

        // Now, invoke the postProcessBeanFactory callback of all processors handled so far.
        // 调用所有BeanDefinitionRegistryPostProcessor的postProcessBeanFactory方法
        invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
        // 调用所有BeanFactoryPostProcessor的postProcessBeanFactory方法
        invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
    }

    else {
        // Invoke factory processors registered with the context instance.
        // 如果该beanFactory不归属于BeanDefinitionRegistry类型，那么直接执行postProcessBeanFactory方法
        invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
    }
    
    // 到这里为止，入参beanFactoryPostProcessors和容器中的所有BeanDefinitionRegistryPostProcessor已经全部处理完毕
    // 接下来开始处理容器中所有的BeanFactoryPostProcessor
    // 可能会包含一些只实现了BeanFactoryPostProcessor，并没有实现BeanDefinitionRegistryPostProcessor接口

    // Do not initialize FactoryBeans here: We need to leave all regular beans
    // uninitialized to let the bean factory post-processors apply to them!
    // 找到所有实现BeanFactoryPostProcessor接口的类
    String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

    // Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
    // Ordered, and the rest.
    
    // 用于存放实现了PriorityOrdered接口的BeanFactoryPostProcessor
    List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
    
    // 用于存放实现了Ordered接口的BeanFactoryPostProcessor的beanName
    List<String> orderedPostProcessorNames = new ArrayList<>();
    
    // 用于存放普通BeanFactoryPostProcessor的beanName
    List<String> nonOrderedPostProcessorNames = new ArrayList<>();
    
    // 遍历postProcessorNames
    // 按实现PriorityOrdered接口的、实现Ordered接口的和普通的BeanFactoryPostProcessor做分类
    for (String ppName : postProcessorNames) {
        // 跳过已经执行过的BeanFactoryPostProcessor
        if (processedBeans.contains(ppName)) {
            // skip - already processed in first phase above
        }
        else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
            // 添加实现了PriorityOrdered接口的BeanFactoryPostProcessor到priorityOrderedPostProcessors
            priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
        }
        else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
            // 添加实现了Ordered接口的BeanFactoryPostProcessor到priorityOrderedPostProcessors
            orderedPostProcessorNames.add(ppName);
        }
        else {
            // 添加剩下的普通BeanFactoryPostProcessor的beanName到nonOrderedPostProcessorNames
            nonOrderedPostProcessorNames.add(ppName);
        }
    }

    // First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
    // 对实现了PriorityOrdered接口的BeanFactoryPostProcessor进行排序
    sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
    // 遍历执行所有实现了PriorityOrdered接口的BeanFactoryPostProcessor的postProcessBeanFactory(beanFactory)方法
    invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

    // Next, invoke the BeanFactoryPostProcessors that implement Ordered.
    // 创建存放实现了Ordered接口的BeanFactoryPostProcessor集合
    List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
    // 遍历存放实现了Ordered接口的BeanFactoryPostProcessor的名字
    for (String postProcessorName : orderedPostProcessorNames) {
        // 将实现了Ordered接口的BeanFactoryPostProcessor添加到集合中
        orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
    }
    // 对实现了Ordered接口的BeanFactoryPostProcessor进行排序
    sortPostProcessors(orderedPostProcessors, beanFactory);
    // 遍历执行所有实现了Ordered接口的BeanFactoryPostProcessor的postProcessBeanFactory(beanFactory)方法
    invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

    // Finally, invoke all other BeanFactoryPostProcessors.
    // 创建存放普通的BeanFactoryPostProcessor集合
    List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();、
    // 遍历普通的BeanFactoryPostProcessor的名称
    for (String postProcessorName : nonOrderedPostProcessorNames) {
        // 获取对应名称的BeanFactoryPostProcessor添加到nonOrderedPostProcessors中
        nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
    }
    // 遍历执行所有普通的BeanFactoryPostProcessor的postProcessBeanFactory(beanFactory)方法
    invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

    // Clear cached merged bean definitions since the post-processors might have
    // modified the original metadata, e.g. replacing placeholders in values...
    // 清除元数据缓存（mergeBeanDefinitions、allBeanNamesByType、singletonBeanNameByType）
    // 因为后置处理器可能已经修改了原始元数据，例如，替换值中的占位符
    beanFactory.clearMetadataCache();
}
```

##### 自定义BFP扩展

> 基于前面的源码解析我们可以知道，在`invokeBeanFactoryPostProcessors`这边其实是可以暴露出来给我们一个扩展点，我们可以自定义`PostProcessor`,使其优先级在`ConfigurationClassPostProcessor`之后，因为这样就可以拿到`BeanDefinition`并且对其修改。我么可以通过`实现BeanDefinitionRegistryPostProcessor接口`对`BeanFactoryPostProcessor`进行扩展。

> 首先我们先创建好对应的`三种不同类型的BeanFactoryPostProcessor`，在上面我们源码中出现很多次的分类操作还有印象吧，分别是`只实现了BeanDefinitionRegistryPostProcessor接口的普通BeanFactoryPostProcessor扩展`、`实现了Ordered接口支持排序的BeanFactoryPostProcessor扩展`、`实现了PriorityOrdered接口具备优先级的BeanFactoryPostProcessor扩展`

![image-20230728020339532](https://article.biliimg.com/bfs/article/a45448866b3276c5341e000c7b6de8b14ad742f3.png)

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/7/28 2:01 AM
 */
@Component
public class RhysBeanDefinitionRegistryPostProcessor1 implements BeanDefinitionRegistryPostProcessor {

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()");
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()");
    }
}
```

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/7/28 2:01 AM
 */
@Component
public class RhysBeanDefinitionRegistryPostProcessor2 implements BeanDefinitionRegistryPostProcessor, Ordered {
    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanDefinitionRegistry()");
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanFactory()");
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/7/28 2:01 AM
 */
@Component
public class RhysBeanDefinitionRegistryPostProcessor3 implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanDefinitionRegistry()");
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanFactory()");
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

> 当我们执行以下代码，在`invokeBeanFactoryPostProcessors`的时候则会输出我们在自定义Bean后置处理器中的扩展逻辑

```java
@Configuration
@ComponentScan("com.rhys")
public class BPFTestMain {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BPFTestMain.class);
    }
}
```

> 运行结果

```sh
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()
```

##### 补充知识点

> 如果我们想在自定义的后置处理器中去讲某一个类型的`Bean`添加到IOC中，我们可以在这样做
>
> 首先有这样一个`BeanH`类

```java
public class BeanR {
	public void exc() {
    System.out.println(this + " doR");
	}
}
```

> 我们要在`RhysBeanDefinitionRegistryPostProcessor1`中通过`postProcessBeanDefinitionRegistry`中提供的`BeanDefinitionRegistry`手动将注册到IOC容器，并以"postProcessBeanDefinitionRegistry.beanH"作为beanName

```java
@Component
public class RhysBeanDefinitionRegistryPostProcessor1 implements BeanDefinitionRegistryPostProcessor {

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()");
        RootBeanDefinition beanDefinition = new RootBeanDefinition(BeanH.class);
        registry.registerBeanDefinition("postProcessBeanDefinitionRegistry.beanH", beanDefinition);
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()");
    }
}
```

> 随后我执行以下代码则可以顺利从`IOC`中获取到对应的`BeanH`实例，并且可以调用`BeanH`中的`doH()`方法

```java
@Configuration
@ComponentScan("com.rhys")
public class BPFTestMain {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BPFTestMain.class);
        System.out.println("---------------------------------------------------------------------------");
        BeanH beanH = (BeanH) applicationContext.getBean("postProcessBeanDefinitionRegistry.beanH");
        beanH.doH();
    }
}
```

> 执行结果

```shell
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()
---------------------------------------------------------------------------
com.rhys.testSourceCode.config.annotation.BeanH@70be0a2b doH
```

> 如果我们还想对beanFactory中的某些Bean做处理还可以这样做】

```java
@Component
public class RhysBeanDefinitionRegistryPostProcessor1 implements BeanDefinitionRegistryPostProcessor {

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()");
        RootBeanDefinition beanDefinition = new RootBeanDefinition(BeanH.class);
        registry.registerBeanDefinition("postProcessBeanDefinitionRegistry.beanH", beanDefinition);
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        System.out.println("执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()");

        System.out.println("---------------------------------------------------------------------------");
        System.out.println("RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory.beanFactory.getBean");
        BeanH beanH = (BeanH) beanFactory.getBean("postProcessBeanDefinitionRegistry.beanH");
        beanH.doH();
    }
}
```

> 改写`BPFTestMain`执行类为以下

```java
@Configuration
@ComponentScan("com.rhys")
public class BPFTestMain {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BPFTestMain.class);
    }
}
```

> 执行结果

```shell
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()
---------------------------------------------------------------------------
RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory.beanFactory.getBean
com.rhys.testSourceCode.config.annotation.BeanH@c8e4bb0 doH
```

#### registerBeanPostProcessors

> 实例化并注册所有 BeanPostProcessor bean

```java
public static void registerBeanPostProcessors( ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
		
  //找到所有实现了BeanPostProcessor接口的类
  String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

  // Register BeanPostProcessorChecker that logs an info message when
  // a bean is created during BeanPostProcessor instantiation, i.e. when
  // a bean is not eligible for getting processed by all BeanPostProcessors.
  // 记录下BeanPostProcessor的目标计数，由于下面流程需要添加一个BeanPostProcessorChecker类，因此这边记录数总数需要在
  // postProcessorNames和beanFactoryProcessorCount数总和的基础上再 `+1` 
  int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
  // 添加BeanPostProcessorChecker到BeanPostprocessor集合，BeanPostProcessorChecker主要用于记录信息
  beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));

  // Separate between BeanPostProcessors that implement PriorityOrdered,
  // Ordered, and the rest.
  // 将实现了PriorityOrdered接口的BeanPostProcessor bean分离出来
  
  // 定义存放实现了PriorityOrdered接口的BeanPostProcessor集合
  List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
  // 定义存放spring内部的BeanPostProcessor
  List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
  // 定义存放实现了Ordered接口的BeanPostProcessor的name集合
  List<String> orderedPostProcessorNames = new ArrayList<>();
	// 定义存放普通的BeanPostProcessor的name集合
  List<String> nonOrderedPostProcessorNames = new ArrayList<>();
  
  // 遍历beanFactory中存放的BeanPostProcssorde的名称
  for (String ppName : postProcessorNames) {
    // 判断ppName对应的BPP是否实现了PriorityOrdered接口,实现了则找到对应beanName的BPP bean添加到priorityOrderedPostProcessors中
    if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
      BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
      priorityOrderedPostProcessors.add(pp);
      // 如果pp同时实现了MergedBeanDefinitionPostProcessor接口，则将pp同时添加到internalPostProcessors中
      if (pp instanceof MergedBeanDefinitionPostProcessor) {
        internalPostProcessors.add(pp);
      }
    }
    // ppName对应的BPP没有实现PriorityOrdered接口但是实现了Ordered接口，则将ppName存到orderedPostProcessorNames
    else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
      orderedPostProcessorNames.add(ppName);
    }
    // 如果ppName对应的BPP没有实现PriorityOrdered接口和Ordered接口，则认为是普通的BPP，将PPName存放到普通的BPP名称集合中以供后续使用
    else {
      nonOrderedPostProcessorNames.add(ppName);
    }
  }

  // First, register the BeanPostProcessors that implement PriorityOrdered.
  // 对实现了PriorityOrdered接口的BeanPostProcessor进行排序
  sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
  // 注册实现了PriorityOrdered接口的BeanPostProcessor实例到BeanFactory中
  registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

  // Next, register the BeanPostProcessors that implement Ordered.
  // 创建存放实现了Ordered接口的集合
  List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
  // 遍历实现了Ordered接口的BeanPostProcessor bean的名称
  for (String ppName : orderedPostProcessorNames) {
   	// 获取到对应名称的BeanPostProcessor实例并添加到orderedPostProcessors中
    BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
    orderedPostProcessors.add(pp);
    // 如果该BeanPostProcessor实例同时实现了MergedBeanDefinitionPostProcessor接口，则同时添加到internalPostProcessors中
    if (pp instanceof MergedBeanDefinitionPostProcessor) {
      internalPostProcessors.add(pp);
    }
  }
  
  // 对实现了Ordered接口的BeanPostProcessors进行排序
  sortPostProcessors(orderedPostProcessors, beanFactory);
  // 注册所有实现了Ordered接口的BeanPostProcessors到BeanFactory中
  registerBeanPostProcessors(beanFactory, orderedPostProcessors);

  // Now, register all regular BeanPostProcessors.
  // 创建存放普通的BeanPostProcessor集合
  List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
  // 遍历nonOrderedPostProcessorNames，根据ppName得到对应的BeanPostProcessor添加到nonOrderedPostProcessors集合中
  for (String ppName : nonOrderedPostProcessorNames) {
    BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
    nonOrderedPostProcessors.add(pp);
    
   	//如果ppName对应的BeanPostProcessor同时实现了MergedBeanDefinitionPostProcessor接口则同时添加到internalPostProcessors集合中
    if (pp instanceof MergedBeanDefinitionPostProcessor) {
      internalPostProcessors.add(pp);
    }
  }
  // 注册所有普通的BeanPostProcessor到BeanFactory中
  registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

  // Finally, re-register all internal BeanPostProcessors.
  // 给所有实现了MergedBeanDefinitionPostProcessor接口则的BeanPostProcessors排序
  sortPostProcessors(internalPostProcessors, beanFactory);
  // 注册所有实现了MergedBeanDefinitionPostProcessor接口则的BeanPostProcessors到BeanFactory
  registerBeanPostProcessors(beanFactory, internalPostProcessors);

  // Re-register post-processor for detecting inner beans as ApplicationListeners,
  // moving it to the end of the processor chain (for picking up proxies etc).
  // 注册ApplicationListenerDetector到BeanFactory中
  beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
}
```

#### initMessageSource

> 为上下文初始化message源，即不同语言的消息体，做国际化处理

```java
protected void initMessageSource() {
  ConfigurableListableBeanFactory beanFactory = getBeanFactory();
  if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
    // 得到name为"messageSource"的实例赋值给属性MessageSource messageSource;
    this.messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
    // Make MessageSource aware of parent MessageSource.
    // 如果有父消息源则使用父消息源
    if (this.parent != null && this.messageSource instanceof HierarchicalMessageSource) {
      // 类型强转为HierarchicalMessageSource
      // HierarchicalMessageSource继承了MessageSource接口，提供父消息源的获取能力
      HierarchicalMessageSource hms = (HierarchicalMessageSource) this.messageSource;
      if (hms.getParentMessageSource() == null) {
        // Only set parent context as parent MessageSource if no parent MessageSource
        // registered already.
        // 当父消息源尚未注册时，将父上下文（ApplicationContext）设置为父消息源。
        hms.setParentMessageSource(getInternalParentMessageSource());
      }
    }
    if (logger.isTraceEnabled()) {
      logger.trace("Using MessageSource [" + this.messageSource + "]");
    }
  }
  else {
    // Use empty MessageSource to be able to accept getMessage calls.
    // 如果BeanFactory中不存在对应的消息源实例，将父上下文（ApplicationContext）设置为父消息源
    DelegatingMessageSource dms = new DelegatingMessageSource();
    dms.setParentMessageSource(getInternalParentMessageSource());
    this.messageSource = dms;
    // 将消息源注册到BeanFactory中
    beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, this.messageSource);
    if (logger.isTraceEnabled()) {
      logger.trace("No '" + MESSAGE_SOURCE_BEAN_NAME + "' bean, using [" + this.messageSource + "]");
    }
  }
}
```

#### initApplicationEventMulticaster

> 初始化应用程序事件广播器。如果上下文中未定义任何内容，则使用 SimpleApplicationEventMulticaster。

```java
protected void initApplicationEventMulticaster() {
  // 获取当前bean工厂,一般是DefaultListableBeanFactory
  ConfigurableListableBeanFactory beanFactory = getBeanFactory();
  // BeanFactory存在name为"applicationEventMulticaster"的BeanDefinition
  // 自定义的事件监听多路广播器，必须实现ApplicationEventMulticaster接口
  if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
    // 如果有，则从bean工厂得到这个bean对象
    this.applicationEventMulticaster =
      beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
    if (logger.isTraceEnabled()) {
      logger.trace("Using ApplicationEventMulticaster [" + this.applicationEventMulticaster + "]");
    }
  }
  else {
    // 如果没有，则默认采用SimpleApplicationEventMulticaster
    this.applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
    beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, this.applicationEventMulticaster);
    if (logger.isTraceEnabled()) {
      logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +
                   "[" + this.applicationEventMulticaster.getClass().getSimpleName() + "]");
    }
  }
}
```

#### onRefresh

> 留给子类来初始化其他的bean，官方没有做默认实现,可以看到在代码作用域内打了一行备注翻译过来就是：“对于子类: 默认情况下什么都不做”

![image-20230730234808272](https://article.biliimg.com/bfs/article/dda58e2c121f90664a96af5fd342b52593e17380.png)

```java
	/**
	 * Template method which can be overridden to add context-specific refresh work.
	 * Called on initialization of special beans, before instantiation of singletons.
	 * <p>This implementation is empty.
	 * @throws BeansException in case of errors
	 * @see #refresh()
	 */
	protected void onRefresh() throws BeansException {
		// For subclasses: do nothing by default.
	}
```

#### registerListeners

> 所有注册的bean中查找listener bean,注册到消息广播器中

```java
protected void registerListeners() {
  // Register statically specified listeners first.
  // 遍历应用程序中存在的监听器集合，将对应的监听器添加到监听器的的多路广播器中
  for (ApplicationListener<?> listener : getApplicationListeners()) {
    getApplicationEventMulticaster().addApplicationListener(listener);
  }

  // Do not initialize FactoryBeans here: We need to leave all regular beans
  // uninitialized to let post-processors apply to them!
  // 从容器中获取所有实现了ApplicationListener接口的BeanDefinition的beanName
  String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
  for (String listenerBeanName : listenerBeanNames) {
    getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
  }

  // Publish early application events now that we finally have a multicaster...
  // 此处先发布早期的监听器集合
  Set<ApplicationEvent> earlyEventsToProcess = this.earlyApplicationEvents;
  this.earlyApplicationEvents = null;
  if (earlyEventsToProcess != null) {
    for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
      getApplicationEventMulticaster().multicastEvent(earlyEvent);
    }
  }
}
```

##### 自定义应用监听器

> 我们创建一个自定义的应用监听器，看一下最终是怎么添加到容器中的

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/7/28 4:34 AM
 */
@Component
public class RhysAppListener implements ApplicationListener<ApplicationEvent> {

    /**
     * Handle an application event.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        System.out.println("RhysAppListener-接受到了应用事件： " + event);
    }
}
```

> 当启动应用执行到`registerListeners`方法之后，我们可以得到所有的应用监听器，这里我们只有一个自定义的

![image-20230728044245110](https://article.biliimg.com/bfs/article/96aca60c416eeb7bb4a25d106b69d761885a1ea6.png)

> 最终会调用到`AbstractApplicationEventMulticaster.addApplicationListenerBean`方法，当add完成后，`Set<String> applicationListenerBeans = new LinkedHashSet<>();`容器中就会存在我们这个自定义的应用监听器

![image-20230728044638106](https://article.biliimg.com/bfs/article/16521c23f76a0d6996876c09d9d7724d70f75d6d.png)

> 当应用执行完成后结果如下

```shell
RhysAppListener-接受到了应用事件： org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@49e4cb85, started on Fri Jul 28 04:38:55 CST 2023]
```

#### finishBeanFactoryInitialization

> finishBeanFactoryInitialization初始化剩下的非懒加载的单例

```java
protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
  // Initialize conversion service for this context.
  // 为此上下文初始化转换服务
  if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) &&
      beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
    beanFactory.setConversionService(
      beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
  }

  // Register a default embedded value resolver if no bean post-processor
  // (such as a PropertyPlaceholderConfigurer bean) registered any before:
  // at this point, primarily for resolution in annotation attribute values.
  
  // 如果没有bean后处理器，注册一个默认的内嵌值解析器,(例如PropertyPlaceholderConfigurer bean)之前注册过: 此时，主要用于解析注释属性值。
  if (!beanFactory.hasEmbeddedValueResolver()) {
    beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
  }

  // Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early.
  // 尽早初始化LoadTimeWeaverAware bean，以便尽早注册它们的转换器
  String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
  for (String weaverAwareName : weaverAwareNames) {
    getBean(weaverAwareName);
  }

  // Stop using the temporary ClassLoader for type matching.
  // 停止使用临时ClassLoader进行类型匹配
  beanFactory.setTempClassLoader(null);

  // Allow for caching all bean definition metadata, not expecting further changes.
  // 允许缓存所有bean定义元数据，不期望进一步更改
  beanFactory.freezeConfiguration();

  // Instantiate all remaining (non-lazy-init) singletons.
  // //实例化所有剩余的(非延迟初始化 )单例。
  beanFactory.preInstantiateSingletons();
}
```

##### preInstantiateSingletons

###### 单例Bean的提前暴露

> `preInstantiateSingletons`这个方法其实就是我们常说的`单例Bean的提前暴露`操作
>
> - 首先我们知道`Bean`对象的实例化是Bean工厂基于`BeanDefinition`来实现的
> - 上面<a href="#obtainFreshBeanFactory">obtainFreshBeanFactory</a>方法中我们已经知道`BeanFactory`的默认实现就是`DefaultListableBeanFactory`，然后我们有关`BeanDefiniton`相关信息也是存储在`DefaultListableBeanFactory`的`beanDefinitionMap`容器中

![image-20230730233811563](https://article.biliimg.com/bfs/article/8149a31ee14f19403b604ec31999d915862e8b49.png)

###### Bean实例的创建过程

> 这边我们只需要着重关注`单例Bean创建`的相关流程即可，因此我们通过`Debug`直接进入到`AbstractBeanFactory`抽象类的`doGetBean`方法中的部分逻辑，如下列举所示代码
>
> 主要调用流程如下图中所示：
>
> - 由`AbstractApplicationContext`抽象类中的`refresh`方法为入口
> - 调用掉了`AbstractApplicationContext`抽象类的`preInstantiateSingletons`方法
> - 然后调用了`AbstractBeanFactory`抽象工厂的`getBean`方法
> - 最终该`getBean`调用了此类中的`doGetBean`方法

![image-20230731011158615](https://article.biliimg.com/bfs/article/a5ea7c2d874dfcda82787527c800944f2a89a747.png)

> - 这边判断如果是单例的话就会执行对应的创建逻辑
> - 将`BeanName`和`一个负责创建Bean对象的回调函数`传递给 `getSingleton`方法，具体逻辑在以下`单例对象获取具体实现逻辑`逻辑中体现

```java
// Create bean instance.
if (mbd.isSingleton()) {
  sharedInstance = getSingleton(beanName, () -> {
    try {
      return createBean(beanName, mbd, args);
    }
    catch (BeansException ex) {
      // Explicitly remove instance from singleton cache: It might have been put there
      // eagerly by the creation process, to allow for circular reference resolution.
      // Also remove any beans that received a temporary reference to the bean.
      destroySingleton(beanName);
      throw ex;
    }
  });
  bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
}
```

> 单例对象获取具体实现逻辑

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
    Assert.notNull(beanName, "Bean name must not be null");
    synchronized (this.singletonObjects) {
        // 从缓存中根据beanName获取对象
        Object singletonObject = this.singletonObjects.get(beanName);
        // 缓存中不存在该对象时做相关的异常处理
        if (singletonObject == null) {
            if (this.singletonsCurrentlyInDestruction) {
                throw new BeanCreationNotAllowedException(beanName,
                "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
            }
            // 在创建单例之前会去根据beanName挨个检查一下这个bean是否满足创建条件，不满足条件直接抛出异常
            //（是否是当前正在创建检查中排除的bean，并且是不是当前正在创建的bean）
            beforeSingletonCreation(beanName);

            // 表示是否为新的单例对象
            boolean newSingleton = false;
            boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
            if (recordSuppressedExceptions) {
                this.suppressedExceptions = new LinkedHashSet<>();
            }
            try {
                // 获取单例对象，这边 getObject() 其实调用的就是上面提到的回调函数，函数中逻辑则会执行createBean(beanName, mbd, args)
                singletonObject = singletonFactory.getObject();
                newSingleton = true;
            }
            catch (IllegalStateException ex) {
                // Has the singleton object implicitly appeared in the meantime ->
                // if yes, proceed with it since the exception indicates that state.
                singletonObject = this.singletonObjects.get(beanName);
                if (singletonObject == null) {
                    throw ex;
                }
            }
            catch (BeanCreationException ex) {
                if (recordSuppressedExceptions) {
                    for (Exception suppressedException : this.suppressedExceptions) {
                        ex.addRelatedCause(suppressedException);
                    }
                }
                throw ex;
            }
            finally {
                if (recordSuppressedExceptions) {
                    this.suppressedExceptions = null;
                }
                afterSingletonCreation(beanName);
            }
            if (newSingleton) {
                addSingleton(beanName, singletonObject);
            }
        }
        return singletonObject;
    }
}
```

#### finishRefresh

> finishRefresh完成刷新过程，通知生命周期处理器lifecycleProcessor刷新过程，同时发出ContextRefreshEvent通知别人.

```java
protected void finishRefresh() {
  // Clear context-level resource caches (such as ASM metadata from scanning).
  // 清除上下文级别的资源缓存(如扫描的ASM元数据)
  // 清空在资源加载器中的所有资源缓存
  clearResourceCaches();

  // Initialize lifecycle processor for this context.
  // 为这个上下文初始化生命周期处理器
  // 初始化LifecycleProcessor.如果上下文中找到'lifecycleProcessor'的LifecycleProcessor Bean对象，
  // 则使用DefaultLifecycleProcessor
  initLifecycleProcessor();

  // Propagate refresh to lifecycle processor first.
  // 首先将刷新传播到生命周期处理器
  // 上下文刷新的通知，例如自动启动的组件
  getLifecycleProcessor().onRefresh();

  // Publish the final event.
  // 发布最终事件
  // 新建ContextRefreshedEvent事件对象，将其发布到所有监听器。
  publishEvent(new ContextRefreshedEvent(this));

  // Participate in LiveBeansView MBean, if active.
  // 参与LiveBeansView MBean，如果是活动的
  // LiveBeansView:Sping用于支持JMX 服务的类
  // 注册当前上下文到LiveBeansView，以支持JMX服务
  LiveBeansView.registerApplicationContext(this);
}
```
