package com.rhys.spring.context;

import com.rhys.spring.DI.BeanReference;
import com.rhys.spring.DI.PropertyValue;
import com.rhys.spring.IoC.BeanDefinitionRegistry;
import com.rhys.spring.IoC.GenericBeanDefinition;
import com.rhys.spring.IoC.exception.BeanDefinitionRegistryException;
import com.rhys.spring.context.annotation.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/4/19 3:32 AM
 */
public class ClassPathBeanDefinitionScanner {

    // private static final Logger log = LoggerFactory.getLogger(ClassPathBeanDefinitionScanner.class);
    private BeanDefinitionRegistry beanDefinitionRegistry;

    private int classPathAbsLength = new File(ClassPathBeanDefinitionScanner.class.getResource("/").getPath()).getAbsolutePath().length();


    public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry beanDefinitionRegistry) {
        this.beanDefinitionRegistry = beanDefinitionRegistry;
    }


    /**
     * <p>
     * <b>扫描路径下class文件</b>
     * </p >
     *
     * @param basePackages <span style="color:#e38b6b;">字段描述</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/23
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    public void scan(String... basePackages) {
        if (basePackages != null && basePackages.length > 0) {
            for (String basePackage : basePackages) {
                //递归扫描包目录下.class文件
                //组合包路径+class文件名 得到全限定类名
                //使用类加载器获取对应类名的Class对象
                //解析Class上的注解，获得Bean定义信息，注册Bean定义

                //递归扫描包目录下.class文件
                Set<File> files = this.doScan(basePackage);
                //得到Class对象，解析注解注册BeanDefinition
                this.readAndRegistryBeanDefinition(files);
            }
        }
    }

    /**
     * <p>
     * <b>解析类上注解</b>
     * </p >
     *
     * @param files <span style="color:#e38b6b;">字段描述</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/23
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    private void readAndRegistryBeanDefinition(Set<File> files) {
        for (File file : files) {
            String className = getClassNameFromFile(file);
            try {
                //加载类
                Class<?> clazz = this.getClass().getClassLoader().loadClass(className);
                Component component = clazz.getAnnotation(Component.class);
                //标注了@Component注解
                if (component != null) {
                    //获取Component设置的beanName
                    String beanName = component.value();
                    //根据类名生成beanName
                    if (StringUtils.isBlank(beanName)) {
                        beanName = this.generateBeanName(clazz);
                    }

                    //配置BeanDefinition
                    GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

                    //设置BeanClass
                    beanDefinition.setBeanClass(clazz);

                    //设置Scope
                    Scope scope = clazz.getAnnotation(Scope.class);
                    if (scope != null) {
                        beanDefinition.setScope(scope.value());
                    }

                    //设置primary
                    Primary primary = clazz.getAnnotation(Primary.class);
                    if (primary != null) {
                        beanDefinition.setPrimary(true);
                    }

                    //处理构造方法
                    this.handleConstructor(clazz, beanDefinition);

                    //处理方法上注解
                    this.handleMethod(clazz, beanDefinition, beanName);

                    //处理属性依赖
                    this.handlePropertyDI(clazz, beanDefinition);

                    //注册BeanDefinition
                    this.beanDefinitionRegistry.registryBeanDefinition(beanName, beanDefinition);
                }

            } catch (Exception e) {
                // log.error("Read And Registry BeanDefinition Exception:{}{}", e.getMessage(), e.getStackTrace());
            }
        }
    }

    /**
     * <p>
     * <b>处理属性依赖</b>
     * </p >
     *
     * @param clazz          <span style="color:#e38b6b;">字段描述</span>
     * @param beanDefinition <span style="color:#e38b6b;">字段描述</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/21
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    private void handlePropertyDI(Class<?> clazz, GenericBeanDefinition beanDefinition) {
        List<PropertyValue> propertyValues = new ArrayList<>();
        beanDefinition.setPropertyValues(propertyValues);

        //在类属性上找@Autowired注解，找到了则优先处理Qualifier注解
        //标注了@Qualifier则直接将qualifier设置的value作为beanName,否则直接将属性类型作为引用类型
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                BeanReference beanReference = null;
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    beanReference = new BeanReference(qualifier.value());
                } else {
                    beanReference = new BeanReference(field.getType());
                }

                propertyValues.add(new PropertyValue(field.getName(), beanReference));
            }
        }
    }

    /**
     * <p>
     * <b>处理方法上注解</b>
     * </p >
     *
     * @param clazz          <span style="color:#e38b6b;">字段描述</span>
     * @param beanDefinition <span style="color:#e38b6b;">字段描述</span>
     * @param beanName       <span style="color:#e38b6b;">字段描述</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/21
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    private void handleMethod(Class<?> clazz, GenericBeanDefinition beanDefinition, String beanName) throws BeanDefinitionRegistryException {
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(PostConstruct.class)) {
                beanDefinition.setInitMethodName(method.getName());
            } else if (method.isAnnotationPresent(PreDestroy.class)) {
                beanDefinition.setDestroyMethodName(method.getName());
            } else if (method.isAnnotationPresent(Bean.class)) {
                //处理工厂方法
                this.handleFactoryMethod(method, clazz, beanName);
            }
        }
    }

    /**
     * <p>
     * <b>处理工厂方法</b>
     * </p >
     *
     * @param method   <span style="color:#e38b6b;">字段描述</span>
     * @param clazz    <span style="color:#e38b6b;">字段描述</span>
     * @param beanName <span style="color:#e38b6b;">字段描述</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/21
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    private void handleFactoryMethod(Method method, Class<?> clazz, String beanName) throws BeanDefinitionRegistryException {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        //处理静态工厂

        //静态工厂直接将class作为bean类型，反之则为成员工厂方法，需要指定beanName
        if (Modifier.isStatic(method.getModifiers())) {
            beanDefinition.setBeanClass(clazz);
        } else {
            beanDefinition.setFactoryBeanName(beanName);
        }
        beanDefinition.setFactoryMethod(method);
        beanDefinition.setFactoryMethodName(method.getName());

        //处理@Scope注解
        Scope scope = method.getAnnotation(Scope.class);
        if (scope != null) {
            beanDefinition.setScope(scope.value());
        }

        //处理@Primary注解
        Primary primary = method.getAnnotation(Primary.class);
        if (primary != null) {
            beanDefinition.setPrimary(true);
        }

        //处理@Bean注解
        Bean bean = method.getAnnotation(Bean.class);
        String beanNameStr = bean.name();
        if (StringUtils.isBlank(beanNameStr)) {
            beanNameStr = method.getName();
        }

        //处理初始化方法和销毁方法
        String initMethod = bean.initMethod();
        if (StringUtils.isNotBlank(initMethod)) {
            beanDefinition.setInitMethodName(initMethod);
        }

        String destroyMethod = bean.destroyMethod();
        if (StringUtils.isNotBlank(destroyMethod)) {
            beanDefinition.setDestroyMethodName(destroyMethod);
        }

        //处理参数依赖
        beanDefinition.setConstructorArgumentValues(this.handleMethodParameters(method.getParameters()));

        //注册BeanDefinition
        this.beanDefinitionRegistry.registryBeanDefinition(beanNameStr, beanDefinition);
    }

    /**
     * <p>
     * <b>处理构造方法，在构造方法上找@Autowired注解，如有，将这个构造方法设置到BeanDefinition</b>
     * </p >
     *
     * @param clazz          <span style="color:#e38b6b;">处理类</span>
     * @param beanDefinition <span style="color:#e38b6b;">Bean定义</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/21
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    private void handleConstructor(Class<?> clazz, GenericBeanDefinition beanDefinition) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (ArrayUtils.isNotEmpty(constructors)) {
            for (Constructor<?> constructor : constructors) {
                if (constructor.getAnnotation(Autowired.class) != null) {
                    beanDefinition.setConstructor(constructor);
                    //构造参数依赖处理
                    beanDefinition.setConstructorArgumentValues(this.handleMethodParameters(constructor.getParameters()));
                    break;
                }
            }
        }
    }

    /**
     * <p>
     * <b>构造参数依赖处理,遍历获取参数上的注解，及创建构造参数依赖</b>
     * </p >
     *
     * @param parameters <span style="color:#e38b6b;">参数列表</span>
     * @return <span style="color:#ffcb6b;"> java.util.List<?></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/21
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    private List<?> handleMethodParameters(Parameter[] parameters) {
        List<Object> args = new ArrayList<>();
        for (Parameter parameter : parameters) {

            //找@Value注解
            Value valueAnnotation = parameter.getAnnotation(Value.class);
            if (valueAnnotation != null) {
                args.add(valueAnnotation.value());
                continue;
            }

            //找@Qualifier注解,不为空则使用value的值为beanName，否则直接使用参数的类型去查找对应的Bean
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                args.add(new BeanReference(qualifier.value()));
            } else {
                args.add(new BeanReference(parameter.getType()));
            }
        }
        return args;
    }

    /**
     * <p>
     * <b>根据类名生成beanName</b>
     * </p >
     *
     * @param clazz <span style="color:#e38b6b;">类</span>
     * @return <span style="color:#ffcb6b;"></span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/4/21
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
     */
    private String generateBeanName(Class<?> clazz) {
        String clazzName = clazz.getName();
        return clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);
    }

    /**
     * 根据文件路径截取类名
     *
     * @param file
     * @return java.lang.String
     * @author Rhys.Ni
     * @date 2023/4/19
     */
    private String getClassNameFromFile(File file) {
        //获取绝对路径
        String absolutePath = file.getAbsolutePath();
        String name = absolutePath.substring(classPathAbsLength + 1, absolutePath.indexOf("."));
        return StringUtils.replace(name, File.separator, ".");
    }

    private Set<File> doScan(String basePackage) {
        //扫描包下的类，将包名转为路径名
        String basePackagePath = "/" + StringUtils.replace(basePackage, ".", "/");
        //得到对应包目录
        File dir = new File(this.getClass().getResource(basePackagePath).getPath());
        //缓存找到的class文件
        Set<File> fileSet = new HashSet<>();
        //检索class文件
        this.retrieveClassFiles(dir, fileSet);

        return fileSet;
    }

    /**
     * 检索Class文件
     *
     * @param dir
     * @param fileSet
     * @return void
     * @author Rhys.Ni
     * @date 2023/4/19
     */
    private void retrieveClassFiles(File dir, Set<File> fileSet) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory() && file.canRead()) {
                retrieveClassFiles(file, fileSet);
            }

            if (file.getName().endsWith(".class")) {
                fileSet.add(file);
            }
        }
    }
}
