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
package com.rhys.testSourceCode.ioc.xml;

public class BeanE {

	public void doSomething() {
		System.out.println("-----" + this + " doSomething ");
	}
}
```

```java
package com.rhys.testSourceCode.ioc.xml;

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
	
	<bean id="beanE" class="com.rhys.testSourceCode.ioc.xml.BeanE" />
	
	<bean id="beanF" class="com.rhys.testSourceCode.ioc.xml.BeanF" ></bean>
	
	<context:annotation-config/>
	
	<context:component-scan base-package="com.rhys.testSourceCode" ></context:component-scan>
</beans>
```

#### 读取配置文件获取对应Bean

```java
package com.rhys.testSourceCode.ioc.xml;

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
package com.rhys.testSourceCode.ioc.annotation;

import com.rhys.testSourceCode.ioc.xml.BeanE;
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
package com.rhys.testSourceCode.ioc.annotation;

import com.rhys.testSourceCode.ioc.xml.BeanF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

@Component
@ImportResource("classpath:com/rhys/testSourceCode/ioc/xml/application.xml")
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
> - `@ComponentScan("com.rhys.testSourceCode.ioc")`定义扫描路径

```java
package com.rhys.testSourceCode.ioc.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.rhys.testSourceCode.ioc")
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
package com.rhys.testSourceCode.ioc.annotation;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AnnotationMain {

	public static void main(String[] args) {
		ApplicationContext context = new AnnotationConfigApplicationContext("com.rhys.testSourceCode.ioc");
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

##### 阶段总结

> 由于`BeanFactoryPostProcessor`接口中没有提供`BeanDefinition`注册的能力，因此拓展了一个`BeanDefinitionRegistryPostProcessor`接口作为`BeanFactoryPostProcessor`的子接口，同时对外提供了一个`postProcessBeanDefinitionRegistry`方法，带入一个`BeanDefinitionRegistry`，也就是拓展了一个注册的功能，负责将一些特定的`BeanDefinition`通过`BeanDefinitionRegistry`注册到`BeanFactory`容器中
>
> 那么具体的注册逻辑其实就是以下`invokeBeanDefinitionRegistryPostProcessors`中为入口的，这个方法在`invokeBeanFactoryPostProcessors`被多次调用，分别注册不同类型的后置处理器

```java
public static void invokeBeanFactoryPostProcessors( ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {
    //...省略部分代码
	sortPostProcessors(currentRegistryProcessors, beanFactory);
    registryProcessors.addAll(currentRegistryProcessors);
    invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
    currentRegistryProcessors.clear();
    //...省略部分代码
}
```

> `invokeBeanDefinitionRegistryPostProcessors`方法中会遍历当前需要注册的所有后置处理器并且逐个调用对应的`postProcessBeanDefinitionRegistry`方法

```java
private static void invokeBeanDefinitionRegistryPostProcessors(
    Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {

    for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
        postProcessor.postProcessBeanDefinitionRegistry(registry);
    }
}
```

> 这里面的`postProcessBeanDefinitionRegistry`方法就调用到`ConfigurationClassPostProcessor`类中的`processConfigBeanDefinitions`方法，
>
> 因为我们说`BeanDefinitionRegistryPostProcessor`接口主要就是拓展了`注册BeanDefinition`的能力，因此肯定要有一个具体的实现类来做具体的逻辑处理,那么`ConfigurationClassPostProcessor`就是`BeanDefinitionRegistryPostProcessor`接口的默认实现类。相关的注册操作就在`ConfigurationClassPostProcessor`类的`processConfigBeanDefinitions`方法中体现

```java
@Override
public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
    int registryId = System.identityHashCode(registry);
    if (this.registriesPostProcessed.contains(registryId)) {
        throw new IllegalStateException(
            "postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
    }
    if (this.factoriesPostProcessed.contains(registryId)) {
        throw new IllegalStateException(
            "postProcessBeanFactory already called on this post-processor against " + registry);
    }
    this.registriesPostProcessed.add(registryId);

    processConfigBeanDefinitions(registry);
}
```

> `processConfigBeanDefinitions`具体逻辑如下，主要完成了`@Configuration`注解和`@Bean`注解修饰的类的解析（会判断是精简版配置类Bean.class注解修饰的还是完整版配置类Configuration.class注解修饰的）以及解析`@Order`注解和 `@Import`注解修饰的类

```java
/**
  * Build and validate a configuration model based on the registry of
  * {@link Configuration} classes.
  */
public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
    List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
    String[] candidateNames = registry.getBeanDefinitionNames();

    for (String beanName : candidateNames) {
        BeanDefinition beanDef = registry.getBeanDefinition(beanName);
        if (ConfigurationClassUtils.isFullConfigurationClass(beanDef) ||
            ConfigurationClassUtils.isLiteConfigurationClass(beanDef)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
            }
        }
        else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)) {
            configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
        }
    }

    // Return immediately if no @Configuration classes were found
    // 没有找到@Configuration修饰的类则立即返回
    if (configCandidates.isEmpty()) {
        return;
    }

    // Sort by previously determined @Order value, if applicable
    // 根据@Order值对BeanDefinition进行排序
    configCandidates.sort((bd1, bd2) -> {
        int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
        int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
        return Integer.compare(i1, i2);
    });

    // Detect any custom bean name generation strategy supplied through the enclosing application context
    // 自定义Bean名称生成器
    SingletonBeanRegistry sbr = null;
    if (registry instanceof SingletonBeanRegistry) {
        sbr = (SingletonBeanRegistry) registry;
        if (!this.localBeanNameGeneratorSet) {
            BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
            if (generator != null) {
                this.componentScanBeanNameGenerator = generator;
                this.importBeanNameGenerator = generator;
            }
        }
    }

    if (this.environment == null) {
        this.environment = new StandardEnvironment();
    }

    // Parse each @Configuration class
    // 解析每个@Configuration修饰的类
    ConfigurationClassParser parser = new ConfigurationClassParser(
        this.metadataReaderFactory, this.problemReporter, this.environment,
        this.resourceLoader, this.componentScanBeanNameGenerator, registry);

    Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
    Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
    do {
        parser.parse(candidates);
        parser.validate();

        Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
        // 将包含alreadyParsed中的所有元素清除，仅保留`@Congifuration\@Bean\@Order\@Import`注解修饰类的相关解析信息
        configClasses.removeAll(alreadyParsed);

        // Read the model and create bean definitions based on its content
        // 读取模型并根据其内容创建Bean定义
        if (this.reader == null) {
            this.reader = new ConfigurationClassBeanDefinitionReader(
                registry, this.sourceExtractor, this.resourceLoader, this.environment,
                this.importBeanNameGenerator, parser.getImportRegistry());
        }
        this.reader.loadBeanDefinitions(configClasses);
        alreadyParsed.addAll(configClasses);
	
        candidates.clear();
        if (registry.getBeanDefinitionCount() > candidateNames.length) {
            String[] newCandidateNames = registry.getBeanDefinitionNames();
            Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
            Set<String> alreadyParsedClasses = new HashSet<>();
            for (ConfigurationClass configurationClass : alreadyParsed) {
                alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
            }
            for (String candidateName : newCandidateNames) {
                if (!oldCandidateNames.contains(candidateName)) {
                    BeanDefinition bd = registry.getBeanDefinition(candidateName);
                    if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, this.metadataReaderFactory) &&
                        !alreadyParsedClasses.contains(bd.getBeanClassName())) {
                        candidates.add(new BeanDefinitionHolder(bd, candidateName));
                    }
                }
            }
            candidateNames = newCandidateNames;
        }
    }
    while (!candidates.isEmpty());

    // Register the ImportRegistry as a bean in order to support ImportAware @Configuration classes
    // 将 ImportRegistry 注册为 Bean 以支持 ImportAware @Configuration类
    if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
        sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
    }

    if (this.metadataReaderFactory instanceof CachingMetadataReaderFactory) {
        // Clear cache in externally provided MetadataReaderFactory; this is a no-op
        // for a shared cache since it'll be cleared by the ApplicationContext.
        
        // 清除外部提供的MetadataReaderFactory中的缓存；这是非操作型的共享缓存，因为它将由ApplicationContext清除。
        ((CachingMetadataReaderFactory) this.metadataReaderFactory).clearCache();
    }
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
com.rhys.testSourceCode.ioc.annotation.BeanH@70be0a2b doH
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
com.rhys.testSourceCode.ioc.annotation.BeanH@c8e4bb0 doH
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

> `preInstantiateSingletons`这个方法其实就是我们常说的`单例Bean的提前暴露`操作
>
> - 首先我们知道`Bean`对象的实例化是Bean工厂基于`BeanDefinition`来实现的
> - 上面<a href="#obtainFreshBeanFactory">obtainFreshBeanFactory</a>方法中我们已经知道`BeanFactory`的默认实现就是`DefaultListableBeanFactory`，然后我们有关`BeanDefiniton`相关信息也是存储在`DefaultListableBeanFactory`的`beanDefinitionMap`容器中

![image-20230730233811563](https://article.biliimg.com/bfs/article/8149a31ee14f19403b604ec31999d915862e8b49.png)

##### Bean实例的创建过程

###### 单例创建方式

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
  	// 通过同步锁的方式保证单例的创建
    synchronized (this.singletonObjects) {
        // 从缓存中根据beanName获取对象 这个缓存其实是在逻辑最后的addSingleton()方法中进行Bean实例缓存操作的
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
              	//创建成功后会往`singletonObjects`容器中保存对应的bean实例，第二次再进来直接从缓存获取就不会再进行二次创建了，保证了单例对象    	 								唯一性
                addSingleton(beanName, singletonObject);
            }
        }
        return singletonObject;
    }
}
```

> **Bean实例创建调用链路**
>
> - `AbstractApplicationContext.refresh` -> `AbstractApplicationContext.invokeBeanFactoryPostProcessors()`
> - `AbstractApplicationContext.invokeBeanFactoryPostProcessors()` -> `AbstractAutowireCapableBeanFactory.doCreateBean()`
>
> - `AbstractAutowireCapableBeanFactory.doCreateBean()` -> `AbstractBeanFactory.getBean()`
>
> - `AbstractBeanFactory.getBean() `-> `AbstractBeanFactory.doGetBean()`
> - `AbstractBeanFactory.doGetBean()` -> `DefaultSingletonBeanRegistry.getSingleton()`
> - `DefaultSingletonBeanRegistry.getSingleton()` -> `ObjectFactory<?> singletonFactory.getObject()`->`lambda$createBean()`
> - `lambda$createBean()` -> `AbstractAutowireCapableBeanFactory.createBean()`
> - `AbstractAutowireCapableBeanFactory.createBean()` -> `AbstractAutowireCapableBeanFactory.doCreateBean()`
> - `AbstractAutowireCapableBeanFactory.doCreateBean()` -> `AbstractAutowireCapableBeanFactory.createBeanInstance()`

![image-20230802004115009](https://article.biliimg.com/bfs/article/044222be9f046b595f01a7f67ee2fe0e2f622cfa.png)

> 最终调用到的`AbstractAutowireCapableBeanFactory.createBeanInstance()`就是真实的Bean实例创建逻辑了
>
> <a href="#createBeanInstance">点击跳转到相关源码解析，由于涉及构造注入，所以相关源码放到了DI模块</a>  

> 阅读完跳转链接中的相关源码当我们DEBUG以下代码就会发现，我要创建的`BeanH`实例其实最终初始化就是通过`Constructor.newInstance`方法，利用反射初始化出来的

```java
@Component
public class BeanH {
    public void doH() {
        System.out.println(this + " doH");
    }
}


@Configuration
@ComponentScan("com.rhys")
public class BPFTestMain {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(BPFTestMain.class);
        // System.out.println("---------------------------------------------------------------------------");
        BeanH beanH = (BeanH) applicationContext.getBean("beanH");
    }
}
```

###### 单例对象的销毁

> 我们对测试类进行修改如下

```java
@Component
public class BeanH {
  	public void doH() {
        System.out.println(this + " doH");
    }
  
    @PreDestroy
    public void destory() {
        System.out.println("销毁对象：" + this);
    }
}

@Configuration
@ComponentScan("com.rhys")
public class BPFTestMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(BPFTestMain.class);
        // System.out.println("---------------------------------------------------------------------------");
        BeanH beanH = (BeanH) applicationContext.getBean("beanH");

        // 主动触发对应事件发布和监听执行
        applicationContext.start();
        applicationContext.stop();
        applicationContext.close();
    }
}
```

> 可以看到我们对应的销毁方法成功执行了，这边触发了两次是因为我们上边实现了自定义的BFP扩展中有相同的代码

```shell
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanDefinitionRegistry()
执行了 - RhysBeanDefinitionRegistryPostProcessor3.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor2.postProcessBeanFactory()
执行了 - RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory()
---------------------------------------------------------------------------
RhysBeanDefinitionRegistryPostProcessor1.postProcessBeanFactory.beanFactory.getBean
com.rhys.testSourceCode.ioc.annotation.BeanH@77e9807f doH
RhysAppListener-接受到了应用事件： org.springframework.context.event.ContextRefreshedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@1d56ce6a, started on Wed Aug 02 04:44:50 CST 2023]
com.rhys.testSourceCode.ioc.annotation.BeanH@222114ba doH
RhysAppListener-接受到了应用事件： org.springframework.context.event.ContextClosedEvent[source=org.springframework.context.annotation.AnnotationConfigApplicationContext@1d56ce6a, started on Wed Aug 02 04:44:50 CST 2023]
销毁对象：com.rhys.testSourceCode.ioc.annotation.BeanH@3b07a0d6
销毁对象：com.rhys.testSourceCode.ioc.annotation.BeanH@222114ba
```

> 执行了`applicationContext.close()`方法后
>
> - `AbstractApplicationContext.doClose()`
> - 

```java
@Override
public void close() {
  synchronized (this.startupShutdownMonitor) {
    doClose();
    // If we registered a JVM shutdown hook, we don't need it anymore now:
    // We've already explicitly closed the context.
    if (this.shutdownHook != null) {
      try {
        Runtime.getRuntime().removeShutdownHook(this.shutdownHook);
      }
      catch (IllegalStateException ex) {
        // ignore - VM is already shutting down
      }
    }
  }
}
```

```java
protected void doClose() {
  // Check whether an actual close attempt is necessary...
  if (this.active.get() && this.closed.compareAndSet(false, true)) {
    if (logger.isDebugEnabled()) {
      logger.debug("Closing " + this);
    }

    LiveBeansView.unregisterApplicationContext(this);

    try {
      // Publish shutdown event.
      // 发布上下文关闭事件
      publishEvent(new ContextClosedEvent(this));
    }
    catch (Throwable ex) {
      logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
    }

    // Stop all Lifecycle beans, to avoid delays during individual destruction.
    // 停止所有生命周期bean，以避免单个销毁过程中的延迟。
    if (this.lifecycleProcessor != null) {
      try {
        this.lifecycleProcessor.onClose();
      }
      catch (Throwable ex) {
        logger.warn("Exception thrown from LifecycleProcessor on context close", ex);
      }
    }

    // Destroy all cached singletons in the context's BeanFactory.
    // 在上下文的BeanFactory中销毁所有缓存的单例
    destroyBeans();

    // Close the state of this context itself.
    // 关闭Bean工厂
    closeBeanFactory();

    // Let subclasses do some final clean-up if they wish...
    // 让子类做一些最后的清理
    onClose();

    // Reset local application listeners to pre-refresh state.
    if (this.earlyApplicationListeners != null) {
      this.applicationListeners.clear();
      this.applicationListeners.addAll(this.earlyApplicationListeners);
    }

    // Switch to inactive.
    this.active.set(false);
  }
}
```

> 这里还有一个`registerShutdownHook()`钩子函数，`在`springBoot`启动的时候有一个步骤`refreshContext`中则会调用到了，通过这个方法新建一个线程来回调`doClose()`方法，对相关单例对象进行注销

```java
@Override
public void registerShutdownHook() {
  if (this.shutdownHook == null) {
    // No shutdown hook registered yet.
    this.shutdownHook = new Thread() {
      @Override
      public void run() {
        synchronized (startupShutdownMonitor) {
          doClose();
        }
      }
    };
    Runtime.getRuntime().addShutdownHook(this.shutdownHook);
  }
}
```

###### <a id ="protoTypeCreation">原型创建方式</a>

> 原型对象不需要Bean工厂提供销毁方式，当根不可达的时候,GC就会自动进行清理了，所以不用进行手动销毁。因为原型对象创建效率高，可能一次性创建相当多的对象，如果对这些对象进行管理就不能被GC掉，需要等到手动销毁，那这个期间极有可能造成大量资源浪费，造成内存泄漏

```java
else if (mbd.isPrototype()) {
  // It's a prototype -> create a new instance.
  Object prototypeInstance = null;
  try {
    beforePrototypeCreation(beanName);
    prototypeInstance = createBean(beanName, mbd, args);
  }
  finally {
    afterPrototypeCreation(beanName);
  }
  bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd);
}
```

> `beforePrototypeCreation`源码
>
> 这里的`ThreadLocal<Object> prototypesCurrentlyInCreation = new NamedThreadLocal<>("Prototype beans currently in creation");`就是使用`ThreadLocal`来保证并发下各线程逻辑互不影响

```java
protected void beforePrototypeCreation(String beanName) {
  	//这里面主要是对不同情况下的`prototypesCurrentlyInCreation`添加操作
		Object curVal = this.prototypesCurrentlyInCreation.get();
		if (curVal == null) {
			this.prototypesCurrentlyInCreation.set(beanName);
		}
		else if (curVal instanceof String) {
			Set<String> beanNameSet = new HashSet<>(2);
			beanNameSet.add((String) curVal);
			beanNameSet.add(beanName);
			this.prototypesCurrentlyInCreation.set(beanNameSet);
		}
		else {
			Set<String> beanNameSet = (Set<String>) curVal;
			beanNameSet.add(beanName);
		}
	}
```

> `afterPrototypeCreation`源码

```java
	protected void afterPrototypeCreation(String beanName) {
    //这里面主要是对不同情况下的`prototypesCurrentlyInCreation`移除操作
		Object curVal = this.prototypesCurrentlyInCreation.get();
		if (curVal instanceof String) {
			this.prototypesCurrentlyInCreation.remove();
		}
		else if (curVal instanceof Set) {
			Set<String> beanNameSet = (Set<String>) curVal;
			beanNameSet.remove(beanName);
			if (beanNameSet.isEmpty()) {
				this.prototypesCurrentlyInCreation.remove();
			}
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

## DI源码分析

### 什么是DI

> - DI即为`依赖注入`，基于IoC所得到的Bean对象相关属性的赋值
>
> - 对象之间的依赖由容器在运行期间决定，即容器动态的将某个依赖注入到对象之中
>
> **依赖注入的本质是`给有参构造方法赋值`、`给属性赋值`**

### 参数值|属性值有哪些

> `直接值`和`Bean依赖`
>
> 现有TestA类和TestB类 如下
>
> - `TestA`类中`a`、`b`、`c`这类的属性赋值就属于直接值
> - 然而`testB`属性对应的是`TestB`对象，这个对象的值（Bean实例）肯定是来自于`IoC`中,所以这就属于Bean依赖

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/8 12:21 AM
 */
public class TestA {
    private String a;
    private int b;
    private char c;
    private TestB testB;


    public TestA(String a, int b, char c, TestB testB) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.testB = testB;
    }
}

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/8 12:21 AM
 */
public class TestB {
  //...
}
```

### 构造注入

#### 多个构造方法的选择

> 由于Spring生成Bean实例的时候`默认调用无参构造方法创建实例`，我们验证一下，新建一个测试用的类做为Bean，其中还有两个属性，给这个类声明两个构造方法，但是没提供无参构造

```java
/**
 * <p>
 * <b>测试DI</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/8/3 19:13
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
@Component
public class BeanR {

    private BeanY beanY;
    private BeanS beanS;


    public BeanR(BeanY beanY) {
        this.beanY = beanY;
    }

    public BeanR(BeanY beanY, BeanS beanS) {
        this.beanY = beanY;
        this.beanS = beanS;
    }
}


@Component
public class BeanY {
    private void  doBeanY(){
        System.out.println("sout doBeanY");
    }
}


@Component
public class BeanS {
    private void doBeanS() {
        System.out.println("sout doBeanS");
    }
}
```

> 当我们启动的时候则会报错如下：没有找到默认的构造方法，也就是无参构造
>
> - `java.lang.NoSuchMethodException: com.rhys.testSourceCode.ioc.base.BeanR.<init>()`

![image-20230807231444528](https://article.biliimg.com/bfs/article/fa0318d99851bb62ddda5c91efa4fbd270b1317d.png)

> 那我们稍作优化，指定一个构造注入，测试类如下
>
> - 在其中一个现有的构造中添加`@Autowired`注解，优先使用此构造去创建实例

```java
@Component
public class BeanR {

    private BeanY beanY;
    private BeanS beanS;

    @Autowired
    public BeanR(BeanY beanY) {
        this.beanY = beanY;
    }


    public BeanR(BeanY beanY, BeanS beanS) {
        this.beanY = beanY;
        this.beanS = beanS;
    }
}
```

> 再次运行测试结果如下

![image-20230807231551190](https://article.biliimg.com/bfs/article/77daf45fe6095d6cb1aa4091d80a3eb401f5da32.png)

> 这里注意，虽然我们存在两个有参构造，但是我们只指定其中一个有参构造，如果我们同时指定两个构造，那在最终选择构造器的时候就会报错了
>
> **同时指定两个构造**

```java
@Component
public class BeanR {

    private BeanY beanY;
    private BeanS beanS;

    @Autowired
    public BeanR(BeanY beanY) {
        this.beanY = beanY;
    }


    @Autowired
    public BeanR(BeanY beanY, BeanS beanS) {
        this.beanY = beanY;
        this.beanS = beanS;
    }
}
```

> 执行结果报错如下：
>
> 
>
> Exception in thread "main" org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'beanR': Invalid autowire-marked constructor: 
>
> `public com.rhys.testSourceCode.ioc.base.BeanR(BeanY,BeanS)`
>
> Found constructor with 'required' Autowired annotation already: 
>
> `public com.rhys.testSourceCode.ioc.base.BeanR(BeanY)`
>
> 
>
> 代表`public BeanR(BeanY beanY, BeanS beanS) {}`这个构造上的`自动装配标记`是无效的,因为已经存在一个`带有'required' Autowired注解的构造函数`，也就是识别到了咱们指定了多个构造函数，不知道用哪个去进行实例创建了，类似于`@Primary`注解，只能指定一个优先级

![image-20230807231654449](https://article.biliimg.com/bfs/article/3ee584fab14b917c3784e74124dfebbf97a74fd6.png)

> <a id="createBeanInstance">构造注入锚点</a>
>
> 这里其实就涉及到了相关自动装配，多构造选择等逻辑处理了
>
> 主要调用链路如下

![image-20230802041543140](https://article.biliimg.com/bfs/article/04f32f109e1aa6dce0a53245f64c47697d1467f7.png)

```java
protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
  // Make sure bean class is actually resolved at this point.
  // 根据BeanDefinition和beanName解析得到对应的beanClass
  Class<?> beanClass = resolveBeanClass(mbd, beanName);

  // 确保class不为空，并且访问权限是public
  if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
    throw new BeanCreationException(mbd.getResourceDescription(), beanName,
                                    "Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
  }
  // 获取用于创建 Bean 实例的回调
  Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
  if (instanceSupplier != null) {
    // 如果BeanDefinition成功获得对应的回调，具备创建实例的能力，则从给定的Bean实例回调获取对应Bean实例
    return obtainFromSupplier(instanceSupplier, beanName);
  }

  // 如果工厂方法名称不为空，则使用工厂方法实例化Bean。
  // 内部逻辑中会有一个 `isStatic` 标志，如果BeanDefinition指定了一个类，而不是工厂Bean，或者使用依赖关系注入配置的工厂对象本身上的实例变量，		 	  则该方法可能是静态的，也就是我们所说的 `工厂静态方法` 则会将 `isStatic` 标志设置为 `true`，并且在后续逻辑中对每个方法的``进行			   	        `Modifier.isStatic()`判定，就是判断方法有没有`STATIC`修饰符
  if (mbd.getFactoryMethodName() != null) {
    return instantiateUsingFactoryMethod(beanName, mbd, args);
  }
	
  // 一个类可能有多个构造器，所以Spring得根据参数个数、类型确定需要调用的构造器
  // 在使用构造器创建实例后，Spring会将解析过后确定下来的构造器或工厂方法保存在缓存中，避免再次创建相同bean时再次解析 
    
  // Shortcut when re-creating the same bean...
  // 防重复创建标记
  boolean resolved = false;
    
  // 是否需要自动装配
  boolean autowireNecessary = false;
 
  // 无参情况下，处理`resolved`标志和`autowireNecessary`标志
  if (args == null) {
    synchronized (mbd.constructorArgumentLock) {
      // 因为一个类可能有多个构造函数，所以需要根据配置文件中配置的参数或传入的参数来确定最终调用的构造函数
      // 因为判断过程会比较，所以spring会将解析、确定好的构造函数缓存到BeanDefinition中的resolvedConstructorOrFactoryMethod（用于缓存解析的构造			 函数或工厂方法）字段中
      // 在下次创建相同时直接从RootBeanDefinition中的属性resolvedConstructorOrFactoryMethod缓存的值获取，避免再次解析
      if (mbd.resolvedConstructorOrFactoryMethod != null) {
        // resolvedConstructorOrFactoryMethod 用于缓存解析的构造函数或工厂方法，不为空直接将`resolved`标识为`true`
        resolved = true;
        // 获取对应缓存的自动装配标记
        autowireNecessary = mbd.constructorArgumentsResolved;
      }
    }
  }
    
  // 有构造参数的或者工厂方法
  if (resolved) {
    if (autowireNecessary) {
      // 满足以上条件则代表存在构造函数缓存并且已解析构造函数参数，则根据构造注入
      return autowireConstructor(beanName, mbd, null, null);
    }
    else {
      // 否则就使用默认构造函数（无参构造）实例化对应Bean
      return instantiateBean(beanName, mbd);
    }
  }

  // Candidate constructors for autowiring?
  // 检查所有注册的 SmartInstantiationAwareBeanPostProcessor 获取对应 bean 的候选构造函数。
  // 从bean后置处理器中为自动装配寻找构造方法, 有且仅有一个有参构造或者有且仅有@Autowired注解构造
  Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
  if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR ||
      mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
    
    // 这边mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR 涉及到自动装配的几种方式
    // 在 AutowireCapableBeanFactory 中定义了这样几个枚举，分别如下释义
    // int AUTOWIRE_NO = 0;  表示没有外部定义的自动装配的常量,但是这种方式下仍然会应用到BeanFactoryAware等注解驱动注入
    // int AUTOWIRE_BY_NAME = 1; 根据名称自动装配bean属性(应用于所有bean属性设置器)
    // int AUTOWIRE_BY_TYPE = 2; 根据类型自动装配bean属性(应用于所有bean属性设置器)的常量	
	// int AUTOWIRE_CONSTRUCTOR = 3; 自动生成可满足的最贪婪的构造函数(涉及解析适当的构造函数)
    // int AUTOWIRE_AUTODETECT = 4; 指示通过对bean类的自省确定适当的自动装配策略。不过从Spring 3.0开始已经弃用: 如果正在使用混合自动装配																			策略，需要选择基于注解的自动装配，以便更清晰地划分自动装配需求
		
    // 满足（构造函数不为空||自动装配模式为AUTOWIRE_CONSTRUCTOR||该BeanDfinition中定义了构造函数参数值||参数列表不为空）任何一个条件即可根    		   据有参构造注入
    return autowireConstructor(beanName, mbd, ctors, args);
  }

  // Preferred constructors for default construction?
  // 获取用于默认构造的首选构造函数。如有必要，构造函数参数将自动装配
  ctors = mbd.getPreferredConstructors();
  if (ctors != null) {
    // 如果用于默认构造的首选构造函数不为空则根据有参构造注入
    return autowireConstructor(beanName, mbd, ctors, null);
  }

  // No special handling: simply use no-arg constructor.
  // 无需特殊处理：只需使用 no-arg 构造函数(无参构造)即可。
  return instantiateBean(beanName, mbd);
}
```

> 根据以上源码，再来看一下`autowireConstructor`方法的重要实现

```java
protected BeanWrapper autowireConstructor(
  String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {

  return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
}
```

> 进入构造解析器的`ConstructorResolver.autowireConstructor`方法

```java
public BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd,
			@Nullable Constructor<?>[] chosenCtors, @Nullable Object[] explicitArgs) {
		// 创建一个新的Bean实例
		BeanWrapperImpl bw = new BeanWrapperImpl();
  	// 初始化Bean工厂里的Bean包装器
		this.beanFactory.initBeanWrapper(bw);

		Constructor<?> constructorToUse = null;
		ArgumentsHolder argsHolderToUse = null;
		Object[] argsToUse = null;

		if (explicitArgs != null) {
			argsToUse = explicitArgs;
		}
		else {
			Object[] argsToResolve = null;
			synchronized (mbd.constructorArgumentLock) {
        // 获取缓存中的构造函数
				constructorToUse = (Constructor<?>) mbd.resolvedConstructorOrFactoryMethod;
        // 缓存中的构造函数不为空并且构造函数参数标记为已解析
				if (constructorToUse != null && mbd.constructorArgumentsResolved) {
					// Found a cached constructor...
          // 获取缓存中的完全解析的构造函数参数
					argsToUse = mbd.resolvedConstructorArguments;
					if (argsToUse == null) {
            // 完全解析的构造函数参数为空，再从缓存中获取部分准备的构造函数参数
						argsToResolve = mbd.preparedConstructorArguments;
					}
				}
			}
			if (argsToResolve != null) {
        // 缓存中部分准备的构造函数参数不为空则开始解析存储在对应BeanDefinition中准备好的参数
				argsToUse = resolvePreparedArguments(beanName, mbd, bw, constructorToUse, argsToResolve, true);
			}
		}
		
		if (constructorToUse == null || argsToUse == null) {
			// Take specified constructors, if any.
      // // 如果以上缓存中的构造函数为空或者完全解析的构造函数参数为空，则采用指定的构造函数
			Constructor<?>[] candidates = chosenCtors;
			if (candidates == null) {
				Class<?> beanClass = mbd.getBeanClass();
				try {
          // 如果允许访问非公共构造函数和方法（默认为true）
          // 则获取此class对象表示的类声明的所有构造函数。包括公共、受保护、默认(包)访问和私有构造函数
          // 否则获取此class对象表示的类的所有公共构造函数
					candidates = (mbd.isNonPublicAccessAllowed() ?
							beanClass.getDeclaredConstructors() : beanClass.getConstructors());
				}
				catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,
							"Resolution of declared constructors on bean Class [" + beanClass.getName() +
							"] from ClassLoader [" + beanClass.getClassLoader() + "] failed", ex);
				}
			}
			
      // 使用无参构造初始化Bean实例
			if (candidates.length == 1 && explicitArgs == null && !mbd.hasConstructorArgumentValues()) {
				Constructor<?> uniqueCandidate = candidates[0];
				if (uniqueCandidate.getParameterCount() == 0) {
					synchronized (mbd.constructorArgumentLock) {
						mbd.resolvedConstructorOrFactoryMethod = uniqueCandidate;
						mbd.constructorArgumentsResolved = true;
						mbd.resolvedConstructorArguments = EMPTY_ARGS;
					}
					bw.setBeanInstance(instantiate(beanName, mbd, uniqueCandidate, EMPTY_ARGS));
					return bw;
				}
			}

			// Need to resolve the constructor.
      // 解析是否需要自动装配
			boolean autowiring = (chosenCtors != null ||
					mbd.getResolvedAutowireMode() == AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
			ConstructorArgumentValues resolvedValues = null;

      // 有参构造相关处理逻辑 如果explicitArgs参数列表不为空 则获取参数列表长度，否则根据BeanDefinition去获取解析对应Bean实例构造函数参数列				 表得到参数个数，用于其他构造函数参数列表的比较
			int minNrOfArgs;
			if (explicitArgs != null) {
				minNrOfArgs = explicitArgs.length;
			}
			else {
        // 获取对应Bean的构造函数参数值
				ConstructorArgumentValues cargs = mbd.getConstructorArgumentValues();
				resolvedValues = new ConstructorArgumentValues();
        // 解析参数列表得到参数个数，用于其他构造函数参数列表的比较,同时将参数值解析出来缓存到`resolvedValues`Map中
				minNrOfArgs = resolveConstructorArguments(beanName, mbd, bw, cargs, resolvedValues);
			}
			
      // 根据参数列表长度（参数数量）进行排序
			AutowireUtils.sortConstructors(candidates);
      
      // 最小类型差值权重 默认Integer最大值
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Set<Constructor<?>> ambiguousConstructors = null;
			LinkedList<UnsatisfiedDependencyException> causes = null;
			
      // 遍历所有构造函数 匹配参数列表
			for (Constructor<?> candidate : candidates) {
        // 获取到所有的参数类型
				Class<?>[] paramTypes = candidate.getParameterTypes();
			
				if (constructorToUse != null && argsToUse != null && argsToUse.length > paramTypes.length) {
					// Already found greedy constructor that can be satisfied ->
					// do not look any further, there are only less greedy constructors left.
					break;
				}
				if (paramTypes.length < minNrOfArgs) {
					continue;
				}
				
        // 初始化一个用于保存参数组合的私有内部类
				ArgumentsHolder argsHolder;
				if (resolvedValues != null) {
					try {
            // resolvedValues中参数值不为空则
            // 检查@ConstructorProperties注解修饰的value长度，如果跟参数数量不一致直接报错，反之获取对应的参数名称列表
						String[] paramNames = ConstructorPropertiesChecker.evaluate(candidate, paramTypes.length);
						if (paramNames == null) {
              // 从Bean工厂获取参数名称查找器
							ParameterNameDiscoverer pnd = this.beanFactory.getParameterNameDiscoverer();
							if (pnd != null) {
                // 使用参数名称查找器获取对应构造函数的参数名
								paramNames = pnd.getParameterNames(candidate);
							}
						}
            // 创建一个参数数组以调用构造函数或工厂方法，给定解析的构造函数参数值。
						argsHolder = createArgumentArray(beanName, mbd, resolvedValues, bw, paramTypes, paramNames,
								getUserDeclaredConstructor(candidate), autowiring, candidates.length == 1);
					}
					catch (UnsatisfiedDependencyException ex) {
						if (logger.isTraceEnabled()) {
							logger.trace("Ignoring constructor [" + candidate + "] of bean '" + beanName + "': " + ex);
						}
						// Swallow and try next constructor.
						if (causes == null) {
							causes = new LinkedList<>();
						}
						causes.add(ex);
						continue;
					}
				}
				else {
					// Explicit arguments given -> arguments length must match exactly.
          // 给出的显式参数和参数长度必须完全匹配，否则直接使用explicitArgs作为参数列表新建一个参数组合
					if (paramTypes.length != explicitArgs.length) {
						continue;
					}
					argsHolder = new ArgumentsHolder(explicitArgs);
				}
				// 是否在宽松模式下还是在严格模式下解析构造函数
        // 宽松模式下解析构造函数：如果找到有效参数，确定类型差异权重,对比转换后的参数和原始参数。如果原始参数权重更高就用原始
        // 📢📢📢个人理解📢📢📢（原始权重 - 1024 优先于相等原始权重的转换权重 ：如果原始权重-1024刚好等于转换权重，则优先使用原始权重） 
        
        // 严格模式下解析构造函数: 通过反射判断是否可无障碍赋值决定返回的权重，三种情况，不一一列举了
				int typeDiffWeight = (mbd.isLenientConstructorResolution() ?
						argsHolder.getTypeDifferenceWeight(paramTypes) : argsHolder.getAssignabilityWeight(paramTypes));
				// Choose this constructor if it represents the closest match.
        // 判断类型差异权重是否小于Integer.MAX_VALUE
				if (typeDiffWeight < minTypeDiffWeight) {
          // 小于就使用当前构造函数
					constructorToUse = candidate;
          // 使用当前参数组合
					argsHolderToUse = argsHolder;
          // 使用当前参数组合中的参数列表
					argsToUse = argsHolder.arguments;
          // 平衡权重
					minTypeDiffWeight = typeDiffWeight;
          // 模糊构造函数集合初始化为空
					ambiguousConstructors = null;
				}
        // 如果权重平衡并且缓存中获取到的构造函数不为空
				else if (constructorToUse != null && typeDiffWeight == minTypeDiffWeight) {
          // 当ambiguousConstructors为空时初始化一个LinkedHashSet用来收集缓存中解析出来的构造函数和当前的构造函数
					if (ambiguousConstructors == null) {
						ambiguousConstructors = new LinkedHashSet<>();
						ambiguousConstructors.add(constructorToUse);
					}
					ambiguousConstructors.add(candidate);
				}
			}

			if (constructorToUse == null) {
				if (causes != null) {
					UnsatisfiedDependencyException ex = causes.removeLast();
					for (Exception cause : causes) {
						this.beanFactory.onSuppressedException(cause);
					}
					throw ex;
				}
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Could not resolve matching constructor " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities)");
			}
			else if (ambiguousConstructors != null && !mbd.isLenientConstructorResolution()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName,
						"Ambiguous constructor matches found in bean '" + beanName + "' " +
						"(hint: specify index/type/name arguments for simple parameters to avoid type ambiguities): " +
						ambiguousConstructors);
			}

			if (explicitArgs == null && argsHolderToUse != null) {
				argsHolderToUse.storeCache(mbd, constructorToUse);
			}
		}
		// argsToUse为空则报错未解析的构造函数参数
		Assert.state(argsToUse != null, "Unresolved constructor arguments");
  	//初始化对应Bean实例并返回
		bw.setBeanInstance(instantiate(beanName, mbd, constructorToUse, argsToUse));
		return bw;
	}
```

> 阅读完以上源码逻辑，大概了解了构造函数的选择其实是由权重对比得出应该使用什么构造函数以及参数列表的，
>
> 那么就剩下`instantiate(beanName, mbd, constructorToUse, argsToUse)`初始化实例的逻辑没有了解了，这里面用到了实例化策略，也就是策略模式

```java
private Object instantiate(String beanName, RootBeanDefinition mbd, Constructor<?> constructorToUse, Object[] argsToUse) {
  try {
    // 获取用于创建 Bean 实例的实例化策略
    InstantiationStrategy strategy = this.beanFactory.getInstantiationStrategy();
    // 获取系统安全管理器 默认为 RuntimePermission("setIO")
    if (System.getSecurityManager() != null) {
      // 使用访问控制器的特权操作进行实例创建，保证该操作是使用`调用方的保护域拥有的权限`与`指定AccessControlContext表示的域拥有的权限`交集执					 行的(权限控制，如果安装了安全管理器，并且指定的 AccessControlContext 不是由系统代码创建的，并且调用方的 ProtectionDomain 尚未被授				 予“createAccessControlContext”SecurityPermission，则执行该操作时没有权限)
      return AccessController.doPrivileged((PrivilegedAction<Object>) () ->
                                           strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, 						                                         argsToUse),
                                           this.beanFactory.getAccessControlContext());
    }
    else {
      return strategy.instantiate(mbd, beanName, this.beanFactory, constructorToUse, argsToUse);
    }
  }
  catch (Throwable ex) {
    throw new BeanCreationException(mbd.getResourceDescription(), beanName,"Bean instantiation via constructor failed", ex);
  }
}
```

> `SimpleInstantiationStrategy.instantiate`源码

```java
@Override
public Object instantiate(RootBeanDefinition bd, @Nullable String beanName, BeanFactory owner,
                          final Constructor<?> ctor, Object... args) {
	// 判断是否存在方法覆盖
  if (!bd.hasMethodOverrides()) {
    // 权限控制
    if (System.getSecurityManager() != null) {
      // use own privileged to change accessibility (when security is on)
      AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
        // 将对应构造的访问标志设置为true,在反射时禁止Java语言访问检查
        ReflectionUtils.makeAccessible(ctor);
        return null;
      });
    }
    return BeanUtils.instantiateClass(ctor, args);
  }
  else {
    return instantiateWithMethodInjection(bd, beanName, owner, ctor, args);
  }
}
```

> `BeanUtils.instantiateClass`源码

```java
public static <T> T instantiateClass(Constructor<T> ctor, Object... args) throws BeanInstantiationException {
  Assert.notNull(ctor, "Constructor must not be null");
  try {
    
		// 进行 ctor.setAccessible(true)操作;
    // 这里主要关注 ctor.newInstance(args)
    ReflectionUtils.makeAccessible(ctor);
    return (KotlinDetector.isKotlinReflectPresent() && KotlinDetector.isKotlinType(ctor.getDeclaringClass()) ?
            KotlinDelegate.instantiateClass(ctor, args) : ctor.newInstance(args));
  }
  catch (InstantiationException ex) {
    throw new BeanInstantiationException(ctor, "Is it an abstract class?", ex);
  }
  catch (IllegalAccessException ex) {
    throw new BeanInstantiationException(ctor, "Is the constructor accessible?", ex);
  }
  catch (IllegalArgumentException ex) {
    throw new BeanInstantiationException(ctor, "Illegal arguments for constructor", ex);
  }
  catch (InvocationTargetException ex) {
    throw new BeanInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
  }
}
```

> `Constructor.newInstance`源码
>
> 所有的Bean实例最终都是通过反射进行创建的

```java
@CallerSensitive
public T newInstance(Object ... initargs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
{
  if (!override) {
    if (!Reflection.quickCheckMemberAccess(clazz, modifiers)) {
      Class<?> caller = Reflection.getCallerClass();
      checkAccess(caller, clazz, null, modifiers);
    }
  }
  if ((clazz.getModifiers() & Modifier.ENUM) != 0)
    throw new IllegalArgumentException("Cannot reflectively create enum objects");
  ConstructorAccessor ca = constructorAccessor;   // read volatile
  if (ca == null) {
    ca = acquireConstructorAccessor();
  }
  @SuppressWarnings("unchecked")
  T inst = (T) ca.newInstance(initargs);
  return inst;
}
```

> 最终其实就是根据构造器调用了`native Object newInstance0(Constructor<?> var0, Object[] var1)`方法，去调用C++对应的逻辑了，这里就不继续往下挖了

```java
class NativeConstructorAccessorImpl extends ConstructorAccessorImpl {
    private final Constructor<?> c;
    private DelegatingConstructorAccessorImpl parent;
    private int numInvocations;

    NativeConstructorAccessorImpl(Constructor<?> var1) {
        this.c = var1;
    }

    public Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
        if (++this.numInvocations > ReflectionFactory.inflationThreshold() && !ReflectUtil.isVMAnonymousClass(this.c.getDeclaringClass())) {
            ConstructorAccessorImpl var2 = (ConstructorAccessorImpl)(new MethodAccessorGenerator()).generateConstructor(this.c.getDeclaringClass(), this.c.getParameterTypes(), this.c.getExceptionTypes(), this.c.getModifiers());
            this.parent.setDelegate(var2);
        }

        return newInstance0(this.c, var1);
    }

    void setParent(DelegatingConstructorAccessorImpl var1) {
        this.parent = var1;
    }

    private static native Object newInstance0(Constructor<?> var0, Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}
```

#### 循环依赖

> 如图：`TestA`创建需要依赖`TestB`,`TestB`创建需要依赖`TestC`,而`TestC`创建又需要依赖`TestA`，这样相互依赖最终没法完整创建导致失败

![image-20230313035616533](https://article.biliimg.com/bfs/article/bbabef0663b1eace18e2fc40d95860eac727bc7c.png)

> - **构造注入中是无法解决循环依赖问题的**
> - **只能检测是否存在循环依赖然后抛出异常**

```JAVA
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/8 2:10 AM
 */

@Component
public class BeanA {
    private BeanB beanB;

    @Autowired
    public BeanA(BeanB beanB) {
        this.beanB = beanB;
    }
}

@Component
class BeanB {

    private BeanC beanC;

    @Autowired
    public BeanB(BeanC beanC) {
        this.beanC = beanC;
    }
}

@Component
class BeanC {
    private BeanB beanB;

    @Autowired
    public BeanC(BeanB beanB) {
        this.beanB = beanB;
    }
}
```

> 获取 beanA

```java
/**
 * <p>
 * <b>功能描述</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/8/7 10:36
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public class ConstructorTestMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.rhys.testSourceCode.ioc.base");
        applicationContext.getBean(BeanA.class);
    }
}
```

> 运行后报错如下, 存在无法解析的循环引用，因此我么才知道，在构造注入中，spring是没有办法帮我们处理循环依赖问题的，只能对循环依赖进行检测报错

![image-20230808021839003](https://article.biliimg.com/bfs/article/88e3163320cd4f1b3649eb7075caf48cfcd0bf4c.png)

> 下面我们就来看一下源码中是如何对循环依赖进行检测的

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
  Assert.notNull(beanName, "Bean name must not be null");
  synchronized (this.singletonObjects) {
    Object singletonObject = this.singletonObjects.get(beanName);
    if (singletonObject == null) {
   		// ... 省略部分源码
      beforeSingletonCreation(beanName);
      boolean newSingleton = false;
      boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
      if (recordSuppressedExceptions) {
        this.suppressedExceptions = new LinkedHashSet<>();
      }
      try {
        singletonObject = singletonFactory.getObject();
        newSingleton = true;
      }
   		// ... 省略部分源码
    }
    return singletonObject;
  }
}
```

> 这里的`beforeSingletonCreation`方法，我们跟进去看一下
>
> 📢：这里其实涉及到一个小知识点
>
> - `singletonsCurrentlyInCreation`为什么不是放在`ThreadLocal`中
>
> 其实是因为这个检查机制是处在`getSingleton`单例Bean的创建中，在创建开始的时候就已经存在两把`synchronized`锁，所以保证了只有一个线程能进来进行操作，但是在<a href="#protoTypeCreation">原型模式的bean实例化</a>中，相关属性就是放在`ThreadLocal`中了。

```java
protected void beforeSingletonCreation(String beanName) {
  // 这里使用了两个容器
 	// 用来存储当前正在创建检查排除项中存在的Bean名称
  // 这个容器的set擦做目前跟踪下来只在`ConfigurationClassEnhancer.resolveBeanReference`类中用到了,也就是涉及到@Bean方法的反射过程
  // Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
  
  // 用来存储当前正在创建的Bean名称
  // Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));
  if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.add(beanName)) {
    throw new BeanCurrentlyInCreationException(beanName);
  }
}
```

> 也就是说当我们正在创建的`Bean`不在`当前正在创建检查排除项inCreationCheckExclusions`中，就说明不需要排除检查，并且同时不能是`当前正在创建的Bean`,而我们这里的程序在进行第一轮创建到beanC之后，发现依赖了beanA，又去创建beanA,而beanA又依赖了beanB,固然先进行beanB的创建，但是发现beanB已经处于正在创建中了，因此判定为循环依赖

![image-20230808024825372](https://article.biliimg.com/bfs/article/9b69a7c402366edf8297d497a97e97aa6e828b9f.png)

> `afterSingletonCreation`源码

```java
protected void afterSingletonCreation(String beanName) {
  // 这里面主要是实bean创建后对`singletonsCurrentlyInCreation`中对应beanNaem的移除操作
  if (!this.inCreationCheckExclusions.contains(beanName) && !this.singletonsCurrentlyInCreation.remove(beanName)) {
    throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
  }
}
```

### 属性注入

> 同样的，我们新建一套测试类

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/8 3:39 AM
 */
@Component
public class BeanQ {

    @Autowired
    private BeanT beanT;

    public BeanQ() {
    }
}

@Component
class BeanT {

    @Autowired
    private BeanQ beanQ;

    public BeanT() {
    }
}
```

> 这样的循环依赖在属性住如下会发生什么？

```java
public class ConstructorTestMain {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.rhys.testSourceCode.ioc.base");
        applicationContext.getBean(BeanQ.class);
    }
}
```

> 运行结果,竟然成功了，那说明spring在属性注入流程中给我们做了循环依赖处理

![image-20230808034617174](https://article.biliimg.com/bfs/article/93f24aa1e6b6da726feaabf3262f2c5e38667e4a.png)

> 属性注入相关源码其实就是在我们常说到的`populateBean`方法中

```java
protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
    if (bw == null) {
    if (mbd.hasPropertyValues()) {
     // 如果beanWrapper为空的情况下，mbd中有需要设置的属性，直接抛出异常
      throw new BeanCreationException(
        mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
    }
    else {
      // Skip property population phase for null instance.
      // mbd中没有可填充的属性，直接跳过
      return;
    }
  }

  // Give any InstantiationAwareBeanPostProcessors the opportunity to modify the
  // state of the bean before properties are set. This can be used, for example,
  // to support styles of field injection.
    
  // 给所有实现了InstantiationAwareBeanPostProcessors的子类有机会在设置属性之前去修改bean的状态，可以被用来支持字段形式注入
  boolean continueWithPropertyPopulation = true;
  
  // `synthetic`是否为true(默认是false,取反则为true)，一般是只有AOP相关的pointCut配置或者Advice配置才会将`synthetic`设置为true
  if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
  	// 如果mdb不是'syntheic',且工厂拥有InstantiationAwareBeanPostProcessor，遍历工厂中的BeanPostProcessor
    for (BeanPostProcessor bp : getBeanPostProcessors()) {
      if (bp instanceof InstantiationAwareBeanPostProcessor) {
     	//如果bp是InstantiationAwareBeanPostProcessor接口，则实例化ibp
        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
        // postProcessAfterInstantiation：一般用于设置属性
        if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
          continueWithPropertyPopulation = false;
          break;
        }
      }
    }
  }

  if (!continueWithPropertyPopulation) {
    return;
  }
  //PropertyValues：包含以一个或多个PropertyValue对象的容器，通常包括针对特定目标Bean的一次更新
  //如果mdb有PropertyValues就获取其PropertyValues
  PropertyValues pvs = (mbd.hasPropertyValues() ? mbd.g etPropertyValues() : null);

  // 获取 mbd 的 自动装配模式
  // 这个自动装配模式在上面源码中提到过，这里再次列举
  // int AUTOWIRE_NO = 0;  表示没有外部定义的自动装配的常量,但是这种方式下仍然会应用到BeanFactoryAware等注解驱动注入
  // int AUTOWIRE_BY_NAME = 1; 根据名称自动装配bean属性(应用于所有bean属性设置器)
  // int AUTOWIRE_BY_TYPE = 2; 根据类型自动装配bean属性(应用于所有bean属性设置器)的常量	
  // int AUTOWIRE_CONSTRUCTOR = 3; 自动生成可满足的最贪婪的构造函数(涉及解析适当的构造函数)
  // int AUTOWIRE_AUTODETECT = 4; 指示通过对bean类的自省确定适当的自动装配策略。不过从Spring 3.0开始已经弃用: 如果正在使用混合自动装配																			策略，需要选择基于注解的自动装配，以便更清晰地划分自动装配需求
   
  if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME || mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
    // 当`AutowireMode`为`根据名称自动装配bean属性` || `根据类型自动装配bean属性`
    // MutablePropertyValues：PropertyValues接口的默认实现。允许对属性进行简单操作，并提供构造函数来支持从映射进行深度复制和构造
    MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
    // Add property values based on autowire by name if applicable.
    // 根据autotowire的名称添加属性值
    if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME) {
      // 通过bw的PropertyDescriptor属性名，查找出对应的Bean对象，将其添加到newPvs中
      autowireByName(beanName, mbd, bw, newPvs);
    }
    // Add property values based on autowire by type if applicable.
    // 根据自动装配的类型添加属性值
    if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
      // 通过bw的PropertyDescriptor属性类型，查找出对应的Bean对象，将其添加到newPvs中
      autowireByType(beanName, mbd, bw, newPvs);
    }
    // 让pvs重新引用newPvs,newPvs此时已经包含了pvs的属性值以及通过AUTOWIRE_BY_NAME，AUTOWIRE_BY_TYPE自动装配所得到的属性值
    pvs = newPvs;
  }

  // 判断工厂是否拥有InstiationAwareBeanPostProcessor处理器
  boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
  // 判断是否需要依赖检查
  boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);
	
  // 经过筛选的PropertyDesciptor数组,存放着排除忽略的依赖项或忽略项上的定义的属性
  PropertyDescriptor[] filteredPds = null;
    
  // 如果工厂拥有InstiationAwareBeanPostProcessor处理器
  // 那么处理对应的流程，主要是对几个注解的赋值工作包含的两个关键子类是 CommonAnnoationBeanPostProcessor,AutowiredAnnotationBeanPostProcessor
  if (hasInstAwareBpps) {
    if (pvs == null) {
      // PropertyValue对象容器为空，则从mbd中获取对应的属性值
      pvs = mbd.getPropertyValues();
    }
    // 遍历BeanPostProcessors
    for (BeanPostProcessor bp : getBeanPostProcessors()) {
      if (bp instanceof InstantiationAwareBeanPostProcessor) {
        // bp匹配为InstantiationAwareBeanPostProcessor接口实例
        InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
        // 在工厂将给定的属性值应用到给定Bean之前，对它们进行后置处理，得到对应的属性值不需要任何属性扫描符，该回调方法在未来的版本会被删掉，取而代之的是 			postProcessPropertyValues 回调方法
        PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
        if (pvsToUse == null) {
          if (filteredPds == null) {
            // pvs为空并且filteredPds为空的情况下，从给定的BeanWrapper中提取一组过滤的属性描述符,排除在忽略的依赖项接口上定义的忽略依赖项类型或属性。
            // mbd.allowCaching:是否允许缓存，默认允许,缓存除了可以提高效率以外，还可以保证在并发的情况下，返回的PropertyDesciptor[]永远都相同
            filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
          }
          // postProcessPropertyValues:一般进行检查是否所有依赖项都满足，例如基于`Require` 注释在 bean属性 setter， 替换要应用的属性值，通常是通			    过基于原始的PropertyValues创建一个新的MutablePropertyValue实例， 添加或删除特定的值,返回的PropertyValues 将应用于bw包装的bean实例              的实际属性值（添加PropertyValues实例到pvs 或者 设置为null以跳过属性填充）回到ipd的postProcessPropertyValues方法
          pvsToUse = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
          // 如果pvsToUse为null，将终止该方法以跳过属性填充
          if (pvsToUse == null) {
            return;
          }
        }
        // 让pvs引用pvsToUse
        pvs = pvsToUse;
      }
    }
  }
  // 需要依赖检查
  if (needsDepCheck) {
    if (filteredPds == null) {
      // 当filteredPds为null，则从bw提取一组经过筛选的PropertyDesciptor,排除忽略的依赖项或忽略项上的定义的属性
      filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
    }
    // 进行依赖检查，主要检查pd的setter方法需要赋值时,pvs中有没有满足其pd的需求的属性值可供其赋值
    checkDependencies(beanName, mbd, filteredPds, pvs);
  }
  // 如果pvs不为null 
  if (pvs != null) {
    // 应用给定的属性值，解决任何在这个bean工厂运行时其他bean的引用。必须使用深拷贝，所以我们不要永久修改此属性
    applyPropertyValues(beanName, mbd, bw, pvs);
  }
}
```

#### 循环依赖解决

##### 提前暴露

> 循环依赖核心源码如下`AbstractAutowireCapableBeanFactory.doCreateBean()`源码

```java
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
    throws BeanCreationException {

    // ...省略部分源码

    // Eagerly cache singletons to be able to resolve circular references
    // even when triggered by lifecycle interfaces like BeanFactoryAware.
    // earlySingletonExposure: 是否需要提前暴露
    // 想要提前暴露的话需要满足三个条件（是单例Bean && 支持循环依赖 && 缓存当前正在创建Bean名称到isSingletonCurrentlyInCreation容器成功，避免重复创	   建）
    boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
                                      isSingletonCurrentlyInCreation(beanName));
    if (earlySingletonExposure) {
        if (logger.isTraceEnabled()) {
            logger.trace("Eagerly caching bean '" + beanName +
                         "' to allow for resolving potential circular references");
        }
        // 进行提前暴露逻辑处理
        addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
    }

    // ...省略部分源码
}
```

> `addSingletonFactory`提前暴露源码

```java
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
    Assert.notNull(singletonFactory, "Singleton factory must not be null");
    // singletonObjects: 用来存储所有单例对象信息的容器
    synchronized (this.singletonObjects) {
        // 当前要创建的这个bean不存于singletonObjects容器（一级缓存）中，代表没有创建过
        if (!this.singletonObjects.containsKey(beanName)) {
            // 缓存`singletonFactory` 这是一个Lambda表达式，就是一个回调函数，执行这个回调将会掉调用											          `AbstractAutowireCapableBeanFactory.getEarlyBeanReference()`方法
            this.singletonFactories.put(beanName, singletonFactory);
            // 将其从早期单例对象的缓存中清除
            this.earlySingletonObjects.remove(beanName);
            // 将其添加到`registeredSingletons`容器中，代表这是一组已注册的单例
            this.registeredSingletons.add(beanName);
        }
    }
}
```

##### 三级缓存

> `spring`中做这样一个三级缓存主要就是因为，当我们Bean实例化完成之后，涉及到`BeanPostProcessor`后置处理（AOP织入相关处理）
>
> - 当我们 `A->B->A`这种循环依赖的时候，咱们A创建完会再AOP的时候生成一个`增强的 ProxyA`对象
> - 那再B进行创建的时候，检测到依赖A，这时候就有问题了，我们A已经被AOP增强了，并且生成了新的 `ProxyA`代理对象，如果还是依赖A，那增强就失效了
> - 因此B其实要依赖增强后的`ProxyA`,所以要用到三级缓存来将增强的代理对象暴露出来

![三级缓存](https://article.biliimg.com/bfs/article/37afc146218ca0b55bd7611fc5e639371f1e5313.png)

```java
protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args)
    throws BeanCreationException {

    // ...省略部分源码
		
		if (earlySingletonExposure) {
      // 循环依赖引用
			Object earlySingletonReference = getSingleton(beanName, false);
			if (earlySingletonReference != null) {
				if (exposedObject == bean) {
					exposedObject = earlySingletonReference;
				}
				else if (!this.allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
					String[] dependentBeans = getDependentBeans(beanName);
					Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
					for (String dependentBean : dependentBeans) {
						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
							actualDependentBeans.add(dependentBean);
						}
					}
					if (!actualDependentBeans.isEmpty()) {
						throw new BeanCurrentlyInCreationException(beanName,
								"Bean with name '" + beanName + "' has been injected into other beans [" +
								StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
								"] in its raw version as part of a circular reference, but has eventually been " +
								"wrapped. This means that said other beans do not use the final version of the " +
								"bean. This is often the result of over-eager type matching - consider using " +
								"'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
					}
				}
			}
		}

    // ...省略部分源码
}
```

> `getEarlyBeanReference`源码

```java
protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
  Object exposedObject = bean;
  if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
    for (BeanPostProcessor bp : getBeanPostProcessors()) {
      if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
        SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
        // 通过BeanPostProcessor对半成品BeanQ做代理，生成半成品的BeanQ代理对象，让BeanT依赖于此代理对象
        exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
      }
    }
  }
  return exposedObject;
}
```

> `getSingleton`源码

```java
@Nullable
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
  Object singletonObject = this.singletonObjects.get(beanName);
  if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
    synchronized (this.singletonObjects) {
      singletonObject = this.earlySingletonObjects.get(beanName);
      if (singletonObject == null && allowEarlyReference) {
        ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
        if (singletonFactory != null) {
          // 获取半成品的对象BeanQ
          singletonObject = singletonFactory.getObject();
          this.earlySingletonObjects.put(beanName, singletonObject);
          this.singletonFactories.remove(beanName);
        }
      }
    }
  }
  return singletonObject;
}
```

## AOP源码解析

### AOP基础概念

> **面向切面编程，在不改变类的代码的情况下，对类方法进行功能的增强**

![image-20230314141613895](https://article.biliimg.com/bfs/article/247d3456da760e98a70d8e5314d3f351b21e375d.png)

#### 程序执行流程

> - 在我们OOP中有一个待执行的正常的流程有`testA()、testB()、testC()`几个方法
> - `Advice`就是我们需要增强的通知内容，对`testA`增强还是对`testB()`增强
> - 具体想增强什么方法，是通过`PonintCut`根据`JoinPoints连接点`来定位到具体的方法的

![image-20230315161934124](https://article.biliimg.com/bfs/article/bf6120dee68fe43761303a633592070dd6863272.png)

### Advice

#### 面向接口编程

> **Advice通知：进行功能增强**

#### Advice的特点

##### 用户性

> 由用户提供增强的逻辑代码

##### 变化性

> 不同的增强需求会有不同的逻辑

##### 多重性

> 同一个切入点可以有多重增强

##### 可选时机

> 可选择在方法执行前、后、异常时进行功能的增强

#### Advice结构

![image-20230811010435064](https://article.biliimg.com/bfs/article/76e77adf522cfcbaab319a14b391c278ed08f6e9.png)

##### Interceptor

> 环绕通知:主要实现有方法拦截器与构造拦截器

###### MethodInterceptor

> 方法拦截器

###### Constructorlnterceptor

> 构造方法拦截器

##### BeforeAdvice

> 前置通知:在方法执行前进行增强

###### MethodBeforeAdvice

###### MethodBeforeAdvicelnterceptor

##### AfterAdvice

> 最终通知:在方法执行后进行增强

###### ThrowsAdvice

> 异常通知

###### ThrowsAdvicelnterceptor

###### AfterReturningAdvice

> 后置通知

###### AfterReturningAdviceterceptor

##### DynamicIntroductionAdvice

> 允许拦截器实现其他接口，并通过使用该拦截器的代理提供。这是一个基本的AOP概念，称为引入
>
> 是一种比较特殊的增提类型，它不是在目标方法周围织入增强，而是为目标创建新的方法和属性，所以它的`连接点`是`类级别`的而`非方法级别`的。
> 通过引介增强我们可以为目标类添加一个接口的实现即原来目标类未实现某个接口,那么通过引介增强可以为目标类创建实现某接口的代理。

###### 案例解析

```java
public class RhysIntroductionTest {
    public static void main(String[] args) {

        //创建新的代理工厂。将代理给定目标实现的对应接口。
        ProxyFactory proxyFactory = new ProxyFactory(new RAop());

        //设置是否直接代理目标类，而不仅仅是代理特定接口。默认值为“false”。
        proxyFactory.setProxyTargetClass(true);

        //为给定的通知创建一个默认的拦截器作为通知者
        DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(new 		  RhysIntroductionInterceptor(),RhysEnhancedAop.class);
        //为代理工厂绑定通知者
        proxyFactory.addAdvisor(advisor);

        //获取代理对象
        Object aop = proxyFactory.getProxy();

        //强转为实现了RhysAop接口的目标类
        RhysAop rhysAop  = (RhysAop) aop;
        rhysAop.call("代理得到了 RhysAop 并执行了 call 方法");

        //强转为实现了RhysEnhancedAop接口的目标类
        RhysEnhancedAop rhysEnhancedAop = (RhysEnhancedAop) aop;
        rhysEnhancedAop.callEnhance("代理得到了 RhysEnhancedAop 并执行了 callEnhance 方法");
    }


}

/**
 * 代理接口
 * @author Rhys.Ni
 * @date 2023/8/17
 */
interface RhysAop {
    void call(String msg);
}

/**
 * 引入增强的接口
 * @author Rhys.Ni
 * @date 2023/8/17
 */
interface RhysEnhancedAop {
    void callEnhance(String msg);
}

/**
 * 代理目标实现类
 * @author Rhys.Ni
 * @date 2023/8/17
 */
class RAop implements RhysAop {

    @Override
    public void call(String msg) {
        System.out.println("execute RAop.call : " + msg);
    }
}


/**
 * 引介拦截器实现需要增强的接口
 * DelegatingIntroductionInterceptor ：方便实现 IntroductionInterceptor 接口。子类只需要扩展这个类并实现要自己引入的接口。在这种情况下，	*	委托是子类实例本身。或者，单独的委托可以实现该接口，并通过委托 bean 属性进行设置。
 * @author Rhys.Ni
 * @date 2023/8/17
 */
class RhysIntroductionInterceptor extends DelegatingIntroductionInterceptor implements RhysEnhancedAop {
    @Override
    public void callEnhance(String msg) {
        System.out.println("execute RhysIntroductionInterceptor.call : " + msg);
    }
}
```

> 执行结果显示
>
> - 咱们上面案例中得到了一个代理对象
> - 这个代理对象既实现了对原有目标对象的增强
> - 又引入了新的接口和新的方法

```shell
execute RAop.call : 代理得到了 RhysAop 并执行了 call 方法
execute RhysIntroductionInterceptor.call : 代理得到了 RhysEnhancedAop 并执行了 callEnhance 方法
```

##### AbstractAspectJAdvice

> 包含 AspectJ 方面或 AspectJ 注释的相关处理

![image-20230817035519705](https://article.biliimg.com/bfs/article/c65ae1156f7f7185f040a74bb2e7ed3224335763.png)

###### AspectJMethodBeforeAdvice

> 在方法之前包装一个 AspectJ

```java
public class AspectJMethodBeforeAdvice extends AbstractAspectJAdvice implements MethodBeforeAdvice, Serializable {

	public AspectJMethodBeforeAdvice(Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {
		super(aspectJBeforeAdviceMethod, pointcut, aif);
	}


	@Override
	public void before(Method method, Object[] args, @Nullable Object target) throws Throwable {
		invokeAdviceMethod(getJoinPointMatch(), null, null);
	}

	@Override
	public boolean isBeforeAdvice() {
		return true;
	}

	@Override
	public boolean isAfterAdvice() {
		return false;
	}
}
```

###### AspectJAroundAdvice

> 包装了一个AspectJ通知方法。暴露一个ProceedingJoinPoint

```java
public class AspectJAroundAdvice extends AbstractAspectJAdvice implements MethodInterceptor, Serializable {

   public AspectJAroundAdvice(
         Method aspectJAroundAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

      super(aspectJAroundAdviceMethod, pointcut, aif);
   }


   @Override
   public boolean isBeforeAdvice() {
      return false;
   }

   @Override
   public boolean isAfterAdvice() {
      return false;
   }

   @Override
   protected boolean supportsProceedingJoinPoint() {
      return true;
   }

   @Override
   public Object invoke(MethodInvocation mi) throws Throwable {
      if (!(mi instanceof ProxyMethodInvocation)) {
         throw new IllegalStateException("MethodInvocation is not a Spring ProxyMethodInvocation: " + mi);
      }
      ProxyMethodInvocation pmi = (ProxyMethodInvocation) mi;
      ProceedingJoinPoint pjp = lazyGetProceedingJoinPoint(pmi);
      JoinPointMatch jpm = getJoinPointMatch(pmi);
      return invokeAdviceMethod(pjp, jpm, null, null);
   }

   /**
    * Return the ProceedingJoinPoint for the current invocation,
    * instantiating it lazily if it hasn't been bound to the thread already.
    * @param rmi the current Spring AOP ReflectiveMethodInvocation,
    * which we'll use for attribute binding
    * @return the ProceedingJoinPoint to make available to advice methods
    */
   protected ProceedingJoinPoint lazyGetProceedingJoinPoint(ProxyMethodInvocation rmi) {
      return new MethodInvocationProceedingJoinPoint(rmi);
   }

}
```

###### AspectJAfterReturningAdvice

> 包装AspectJ后置返回通知方法

```java
public class AspectJAfterReturningAdvice extends AbstractAspectJAdvice
      implements AfterReturningAdvice, AfterAdvice, Serializable {

   public AspectJAfterReturningAdvice(
         Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

      super(aspectJBeforeAdviceMethod, pointcut, aif);
   }


   @Override
   public boolean isBeforeAdvice() {
      return false;
   }

   @Override
   public boolean isAfterAdvice() {
      return true;
   }

   @Override
   public void setReturningName(String name) {
      setReturningNameNoCheck(name);
   }

   @Override
   public void afterReturning(@Nullable Object returnValue, Method method, Object[] args, @Nullable Object target) throws Throwable {
      if (shouldInvokeOnReturnValueOf(method, returnValue)) {
         invokeAdviceMethod(getJoinPointMatch(), returnValue, null);
      }
   }


   /**
    * Following AspectJ semantics, if a returning clause was specified, then the
    * advice is only invoked if the returned value is an instance of the given
    * returning type and generic type parameters, if any, match the assignment
    * rules. If the returning type is Object, the advice is *always* invoked.
    * @param returnValue the return value of the target method
    * @return whether to invoke the advice method for the given return value
    */
   private boolean shouldInvokeOnReturnValueOf(Method method, @Nullable Object returnValue) {
      Class<?> type = getDiscoveredReturningType();
      Type genericType = getDiscoveredReturningGenericType();
      // If we aren't dealing with a raw type, check if generic parameters are assignable.
      return (matchesReturnValue(type, method, returnValue) &&
            (genericType == null || genericType == type ||
                  TypeUtils.isAssignable(genericType, method.getGenericReturnType())));
   }

   /**
    * Following AspectJ semantics, if a return value is null (or return type is void),
    * then the return type of target method should be used to determine whether advice
    * is invoked or not. Also, even if the return type is void, if the type of argument
    * declared in the advice method is Object, then the advice must still get invoked.
    * @param type the type of argument declared in advice method
    * @param method the advice method
    * @param returnValue the return value of the target method
    * @return whether to invoke the advice method for the given return value and type
    */
   private boolean matchesReturnValue(Class<?> type, Method method, @Nullable Object returnValue) {
      if (returnValue != null) {
         return ClassUtils.isAssignableValue(type, returnValue);
      }
      else if (Object.class == type && void.class == method.getReturnType()) {
         return true;
      }
      else {
         return ClassUtils.isAssignable(type, method.getReturnType());
      }
   }

}
```

###### AspectJAfterThrowingAdvice

> 包装AspectJ异常排除后通知方法

```java
public class AspectJAfterThrowingAdvice extends AbstractAspectJAdvice
      implements MethodInterceptor, AfterAdvice, Serializable {

   public AspectJAfterThrowingAdvice(
         Method aspectJBeforeAdviceMethod, AspectJExpressionPointcut pointcut, AspectInstanceFactory aif) {

      super(aspectJBeforeAdviceMethod, pointcut, aif);
   }


   @Override
   public boolean isBeforeAdvice() {
      return false;
   }

   @Override
   public boolean isAfterAdvice() {
      return true;
   }

   @Override
   public void setThrowingName(String name) {
      setThrowingNameNoCheck(name);
   }

   @Override
   public Object invoke(MethodInvocation mi) throws Throwable {
      try {
         return mi.proceed();
      }
      catch (Throwable ex) {
         if (shouldInvokeOnThrowing(ex)) {
            invokeAdviceMethod(getJoinPointMatch(), null, ex);
         }
         throw ex;
      }
   }

   /**
    * In AspectJ semantics, after throwing advice that specifies a throwing clause
    * is only invoked if the thrown exception is a subtype of the given throwing type.
    */
   private boolean shouldInvokeOnThrowing(Throwable ex) {
      return getDiscoveredThrowingType().isAssignableFrom(ex.getClass());
   }

}
```

###### AspectJAfterAdvice

> 包装AspectJ最终通知方法。

```java
public class AspectJAfterAdvice extends AbstractAspectJAdvice implements MethodInterceptor, AfterAdvice, Serializable {

   public AspectJAfterAdvice(Method aspectJBeforeAdviceMethod,AspectJExpressionPointcut pointcut,AspectInstanceFactory aif) {
      super(aspectJBeforeAdviceMethod, pointcut, aif);
   }

   @Override
   public Object invoke(MethodInvocation mi) throws Throwable {
      try {
        // ReflectiveMethodInvocation实现类中的动态匹配逻辑
         return mi.proceed();
      }
      finally {
         invokeAdviceMethod(getJoinPointMatch(), null, null);
      }
   }

   @Override
   public boolean isBeforeAdvice() {
      return false;
   }

   @Override
   public boolean isAfterAdvice() {
      return true;
   }

}
```

### Pointcut

```java
public interface Pointcut {

	/**
	 * Return the ClassFilter for this pointcut.
	 * @return the ClassFilter (never {@code null})
	 */
  // 返回此切入点的类筛选器
	ClassFilter getClassFilter();

	/**
	 * Return the MethodMatcher for this pointcut.
	 * @return the MethodMatcher (never {@code null})
	 */
  // 返回此切入点的方法匹配器
	MethodMatcher getMethodMatcher();


	/**
	 * Canonical Pointcut instance that always matches.
	 */
  // 始终匹配的规范切入点实例 TruePointcut
	Pointcut TRUE = TruePointcut.INSTANCE;

}
```

#### 实现方式

![image-20230814225650583](https://article.biliimg.com/bfs/article/8b1afdc3a5cb292264a6a7efd53646963cd10d00.png)

##### AspectJExpressionPointcut

> 使用 AspectJ weaver 来评估切入点表达式。切入点表达式值是一个 AspectJ 表达式，仅支持方法执行切入点

##### JdkRegexpMethodPointcut

> 基于 java.util.regex 包的正则表达式切入点

### Advisor

> 为用户提供更简单的`Advisor(通知者)`组合`Advice`和`Pointcut`
>
> - 当用户使用`AspectJ`表达式来指定切入点事就用`AspectJPointCutAdvisor`这个实现
> - 只需要配置好该类的Bean，指定AdviceBeanName和expression即可

```java
public interface Advisor {

	/**
	 * Common placeholder for an empty {@code Advice} to be returned from
	 * {@link #getAdvice()} if no proper advice has been configured (yet).
	 * @since 5.0
	 */
  // 如果尚未配置正确的通知，则从 getAdvice（） 返回空通知的通用占位符
	Advice EMPTY_ADVICE = new Advice() {};


	/**
	 * Return the advice part of this aspect. An advice may be an
	 * interceptor, a before advice, a throws advice, etc.
	 * @return the advice that should apply if the pointcut matches
	 * @see org.aopalliance.intercept.MethodInterceptor
	 * @see BeforeAdvice
	 * @see ThrowsAdvice
	 * @see AfterReturningAdvice
	 */
  // 返回这方面的通知。可以是环绕通知、方法前置通知、方法后置通知等
	Advice getAdvice();

	/**
	 * Return whether this advice is associated with a particular instance
	 * (for example, creating a mixin) or shared with all instances of
	 * the advised class obtained from the same Spring bean factory.
	 * <p><b>Note that this method is not currently used by the framework.</b>
	 * Typical Advisor implementations always return {@code true}.
	 * Use singleton/prototype bean definitions or appropriate programmatic
	 * proxy creation to ensure that Advisors have the correct lifecycle model.
	 * @return whether this advice is associated with a particular target instance
	 */
  // 判断此通知是否与特定实例相关联还是与从同一Bean工厂获得的被通知类的对应共享实例
	boolean isPerInstance();

}
```

#### 具体实现

##### AspectJPointcutAdvisor

> 将AbstractAspectJAdvice适配PointcutAdvisor接口

### Weaving

> **织入：不改变原类的代码实现增强**，
>
> - 负责将用户提供的`Advice通知`增强到`Pointcuts的指定方法中`，将切入点所对应的方法（Bean对象）与切入点关联起来
> - 创建Bean的时候，在Bean执行初始化后通过代理进行增强
> - 需要对Bean类及方法挨个匹配用户配置的切面，如果匹配到切面则需要增强

#### 织入设计

> 根据AOP的使用流程
>
> - 用户负责配置切面
> - 织入就在初始化后判断判断Bean是否需要增强
> - 如果需要增强则通过代理进行增强，最后返回`代理对象实例`
> - 不需要增强的话则直接返回`原始对象实例`

![image-20230322013928793](https://article.biliimg.com/bfs/article/38f934325210ec4acff266dadc60d8823db91fe0.png)

#### 织入实现

> 首先我们先定义好需要增强的目标类

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/15 1:48 AM
 */
@Component
public class BeanN {
    public void execMethod(String val) {
        System.out.println("BeanN.execMethod: val:" + val);
    }
    public String serviceMethod(String name) {
        System.out.println("BeanN.serviceMethod name:" + name);
        return name;
    }
    public String serviceMTest(String name) {
        System.out.println("BeanN.serviceMTest name:" + name);
        if (!"serviceMTest".equals(name)) {
            throw new IllegalStateException("异常增强测试信息： name is not equals serviceMTest, name:" + name);
        }
        return name;
    }
}
```

> 我们定义一个切面类了解一下织入的实现

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/15 12:32 AM
 */
@Aspect
@Component
@EnableAspectJAutoProxy
public class AnnotationAspectJAdvice {

    /**
     * 定义exec前缀方法切点
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Pointcut("execution(* com.rhys.testSourceCode.aop.beans .*.exec*(..))")
    public void execMethodsPoint() {
    }

    /**
     * 定义service前缀方法切点
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Pointcut("execution(* com.rhys.testSourceCode.aop.beans.*.service*(..))")
    public void servicesPoint() {
    }

    /**
     * 定义一个方法前置增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Before("execMethodsPoint() && args(val,..)")
    public void before(String val) {
        System.out.println("增强了 AnnotationAspectJAdvice.before方法，val=" + val);
    }

    /**
     * 定义一个环绕增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @Around("servicesPoint() && args(name,..)")
    public Object around(ProceedingJoinPoint pjp, String name) throws Throwable {
        System.out.println("增强了 AnnotationAspectJAdvice.around方法，name=" + name);
        System.out.println("增强了 AnnotationAspectJAdvice.around方法，环绕前-" + pjp);
        Object ret = pjp.proceed();
        System.out.println("增强了 AnnotationAspectJAdvice.around方法，环绕后-" + pjp);
        return ret;
    }

    /**
     * 定义一个方法后置增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @AfterReturning(pointcut = "servicesPoint()", returning = "val")
    public void afterReturning(Object val) {
        System.out.println("增强了 AnnotationAspectJAdvice.afterReturning方法，val=" + val);
    }

    /**
     * 定义一个异常通知增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @AfterThrowing(pointcut = "servicesPoint()", throwing = "e")
    public void afterThrowing(JoinPoint jp, Exception e) {
        System.out.println("增强了 AnnotationAspectJAdvice.afterThrowing方法，joinPoint-" + jp);
        System.out.println("增强了 AnnotationAspectJAdvice.afterThrowing方法，e: " + e);
    }

    /**
     * 定义一个最终通知增强
     *
     * @return void
     * @author Rhys.Ni
     * @date 2023/8/18
     */
    @After("execMethodsPoint()")
    public void after(JoinPoint jp) {
        System.out.println("增强了 AnnotationAspectJAdvice.after方法，joinPoint-" + jp);
    }
}
```

> 最后在测试类中验证我们所定义的切面是否能起到作用

```java
/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/8/15 1:55 AM
 */
@Configuration
@ComponentScan(basePackages = "com.rhys.testSourceCode.aop")
public class AnnotationAopTest {
  public static void main(String[] args) {
    ApplicationContext applicationContext = new AnnotationConfigApplicationContext(AnnotationAopTest.class);
    BeanN beanN = applicationContext.getBean(BeanN.class);
    beanN.execMethod("testVal");
    System.out.println("=================================================================================");
    beanN.serviceMethod("serviceMethod");
    System.out.println("=================================================================================");
    beanN.serviceMTest("serviceMTest");
  }
}
```

> 执行结果
>
> - 执行`exec`为前缀的方法时，切面会在执行方法前做一个增强操作，在方法执行后进行了最终通知增强操作
> - 执行`service`为前缀的方法时，由于我们案例中存在两个相同前缀的方法，因此匹配到了两次增强操作，唯一的区别就是在`serviceMTest`方法中模拟了一个`异常增强`操作效果

![image-20230818011029140](https://article.biliimg.com/bfs/article/3dd6f3d1b36e918e9ce94fb3e8d5b4807a362409.png)

##### @EnableAspectJAutoProxy

> `EnableAspectJAutoProxy注解`最主要的点就是会通过`@Import`将`AspectJAutoProxyRegistrar`注入到容器中，那么我们需要使用代理增强处理，就必须添加@EnableAspectJAutoProxy才生效

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(AspectJAutoProxyRegistrar.class)
public @interface EnableAspectJAutoProxy {

	/**
	 * Indicate whether subclass-based (CGLIB) proxies are to be created as opposed
	 * to standard Java interface-based proxies. The default is {@code false}.
	 */
	boolean proxyTargetClass() default false;

	/**
	 * Indicate that the proxy should be exposed by the AOP framework as a {@code ThreadLocal}
	 * for retrieval via the {@link org.springframework.aop.framework.AopContext} class.
	 * Off by default, i.e. no guarantees that {@code AopContext} access will work.
	 * @since 4.3.1
	 */
	boolean exposeProxy() default false;

}
```

##### AspectJAutoProxyRegistrar

> 根据给定的@EnableAspectJAutoProxy注解，根据需要针对当前 BeanDefinitionRegistry 注册 `AnnotationAwareAspectJAutoProxyCreator`

```java
class AspectJAutoProxyRegistrar implements ImportBeanDefinitionRegistrar {

	/**
	 * Register, escalate, and configure the AspectJ auto proxy creator based on the value
	 * of the @{@link EnableAspectJAutoProxy#proxyTargetClass()} attribute on the importing
	 * {@code @Configuration} class.
	 */
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		// 将 AnnotationAwareAspectJAutoProxyCreator 注册到容器
		AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);

		AnnotationAttributes enableAspectJAutoProxy =
				AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
		if (enableAspectJAutoProxy != null) {
			if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
			}
			if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
				AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
			}
		}
	}
}
```

###### AnnotationAwareAspectJAutoProxyCreator

> - `AspectJAwareAdvisorAutoProxyCreator`的子类，处理所有当前应用程序上下文中的AspectJ相关注释以及相关通知者（advisors）
> - 任何AspectJ注解的类都会被自动识别，如果Spring AOP是基于代理的模型能够应用它们，那么它们的通知就会被应用。同时涵盖了方法执行连接点
> - 如果使用了`<aop:include>`元素，只有名称与include模式匹配的`@AspectJ bean`才会被视为定义用于Spring自动代理

![AnnotationAwareAspectJAutoProxyCreator](https://article.biliimg.com/bfs/article/7d4d9aaafecdfe950939ec84585d966cf5d94992.png)

> 从结构上看，最上层实现了`BeanPostProcessor`接口，那么也就是说其实`AnnotationAwareAspectJAutoProxyCreator`这个类本质上就是一个Bean后置处理器,我们直接找到`BeanPostProcessor`子接口`InstantiationAwareBeanPostProcessor.postProcessBeforeInstantiation`方法，`postProcessBeforeInstantiation`是`InstantiationAwareBeanPostProcessor`

![image-20230818160655058](https://article.biliimg.com/bfs/article/1f6b57fea0febdd9c32e3a6f8f370e9e6ad59284.png)

> 的具体抽象实现类`AbstractAutoProxyCreator`，其中有关`postProcessBeforeInstantiation`方法具体的实现如下

```java
public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
  Object cacheKey = getCacheKey(beanClass, beanName);

  if (!StringUtils.hasLength(beanName) || !this.targetSourcedBeans.contains(beanName)) {
    if (this.advisedBeans.containsKey(cacheKey)) {
      return null;
    }
    if (isInfrastructureClass(beanClass) || shouldSkip(beanClass, beanName)) {
      this.advisedBeans.put(cacheKey, Boolean.FALSE);
      return null;
    }
  }

  // Create proxy here if we have a custom TargetSource.
  // Suppresses unnecessary default instantiation of the target bean:
  // The TargetSource will handle target instances in a custom fashion.
  TargetSource targetSource = getCustomTargetSource(beanClass, beanName);
  if (targetSource != null) {
    // 如果我们有一个自定义的TargetSource
    if (StringUtils.hasLength(beanName)) {
      // 添加到targetSourcedBeans容器
      this.targetSourcedBeans.add(beanName);
    }
    //在这里创建代理，从而抑制目标bean不必要的默认实例化:TargetSource将以自定义方式处理目标实例
    Object[] specificInterceptors = getAdvicesAndAdvisorsForBean(beanClass, beanName, targetSource);
    Object proxy = createProxy(beanClass, beanName, specificInterceptors, targetSource);
    this.proxyTypes.put(cacheKey, proxy.getClass());
    return proxy;
  }

  return null;
}

```

###### Bean与BeanPostPorcessor的串联

> 说到这个，我们又要提到一下前面分析过的`AbstractAutowireCapableBeanFactory.createBean`逻辑了，在进行`doCreateBean`之前还有一个步骤是`应用实例化后处理器，解析是否存在指定 Bean 的实例化前快捷方式`

```java
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {
    // ... 省略其他源码
    
    @Nullable
	protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
		Object bean = null;
		if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
			// Make sure bean class is actually resolved at this point.
			if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
				Class<?> targetType = determineTargetType(beanName, mbd);
				if (targetType != null) {
                    // 将InstantiationAwareBeanPostProcessors通过类和名称应用到指定的bean定义，调用它们的postProcessBeforeInstantiation方						  法。任何返回的对象都将被用作bean，而不是实际实例化目标bean。从后处理器返回的值将导致目标bean被实例化。
					bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
					if (bean != null) {
						bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
					}
				}
			}
			mbd.beforeInstantiationResolved = (bean != null);
		}
		return bean;
	}
    
    // ... 省略其他源码
}
```



> 查找当前 Bean 工厂中所有符合条件的Advisor Bean，忽略 FactoryBeans 并排除当前正在创建的 Bean。

```java
public List<Advisor> findAdvisorBeans() {
  // Determine list of advisor bean names, if not cached already.
  String[] advisorNames = this.cachedAdvisorBeanNames;
  if (advisorNames == null) {
    // Do not initialize FactoryBeans here: We need to leave all regular beans
    // uninitialized to let the auto-proxy creator apply to them!
    advisorNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
      this.beanFactory, Advisor.class, true, false);
    this.cachedAdvisorBeanNames = advisorNames;
  }
  if (advisorNames.length == 0) {
    return new ArrayList<>();
  }

  List<Advisor> advisors = new ArrayList<>();
  for (String name : advisorNames) {
    if (isEligibleBean(name)) {
      if (this.beanFactory.isCurrentlyInCreation(name)) {
        if (logger.isTraceEnabled()) {
          logger.trace("Skipping currently created advisor '" + name + "'");
        }
      }
      else {
        try {
          advisors.add(this.beanFactory.getBean(name, Advisor.class));
        }
        catch (BeanCreationException ex) {
          Throwable rootCause = ex.getMostSpecificCause();
          if (rootCause instanceof BeanCurrentlyInCreationException) {
            BeanCreationException bce = (BeanCreationException) rootCause;
            String bceBeanName = bce.getBeanName();
            if (bceBeanName != null && this.beanFactory.isCurrentlyInCreation(bceBeanName)) {
              if (logger.isTraceEnabled()) {
                logger.trace("Skipping advisor '" + name +
                             "' with dependency on currently created bean: " + ex.getMessage());
              }
              // Ignore: indicates a reference back to the bean we're trying to advise.
              // We want to find advisors other than the currently created bean itself.
              continue;
            }
          }
          throw ex;
        }
      }
    }
  }
  return advisors;
}
```

