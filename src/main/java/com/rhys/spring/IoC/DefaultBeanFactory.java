package com.rhys.spring.IoC;

import com.rhys.spring.DI.BeanReference;
import com.rhys.spring.DI.PropertyValue;
import com.rhys.spring.IoC.exception.AliasRegistryException;
import com.rhys.spring.IoC.exception.BeanDefinitionRegistryException;
import com.rhys.spring.IoC.exception.PrimaryException;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/2/16 11:19 PM
 */
public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry, Closeable {
    private static final Logger logger = LoggerFactory.getLogger(DefaultBeanFactory.class);
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);
    private Map<String, Object> singletonBeanMap = new ConcurrentHashMap<>(256);
    private Map<String, String[]> aliasMap = new ConcurrentHashMap<>(256);
    private Map<Class<?>, Set<String>> typeMap = new ConcurrentHashMap<>(256);
    private ThreadLocal<Set<String>> buildingBeansRecorder = new ThreadLocal<>();
    private ThreadLocal<Map<String, Object>> earlyExposeBuildingBeans = new ThreadLocal<>();

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

    /**
     * 注册typeMap
     *
     * @param
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/3
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
     *
     * @param beanName
     * @param type
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/3
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
     *
     * @param beanName
     * @param type
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/3
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
     *
     * @param beanName
     * @param type
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/3
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

    private Object doGetBean(String beanName) throws Exception {
        //校验beanName
        Objects.requireNonNull(beanName, "beanName can not be empty !");

        //从单例Bean所存储的Map中根据beanName获取一下这个Bean实例，获取到直接返回
        Object beanInstance = singletonBeanMap.get(beanName);
        if (beanInstance != null) {
            return beanInstance;
        }

        //属性依赖时的循环依赖
        //从提前暴露的bean实例缓存中获取bean实例
        beanInstance = this.getFromEarlyExposeBuildingBeans(beanName);
        if (beanInstance != null) {
            return beanInstance;
        }

        //没获取到则根据BeanDefinition创建Bean实例并且存放到Map中
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        Objects.requireNonNull(beanDefinition, "beanDefinition named " + beanName + " is invalid !");

        //检测循环依赖
        Set<String> buildingBeans = buildingBeansRecorder.get();
        if (buildingBeans == null) {
            buildingBeans = new HashSet<>();
            this.buildingBeansRecorder.set(buildingBeans);
        }

        //检测到记录不为空则进行模糊匹配,只要记录中包含本次要创建的Bean则直接抛出异常，认为循环依赖了
        if (buildingBeans.contains(beanName)) {
            throw new Exception(beanName + " bean has cyclic dependencies !");
        }

        //记录中不存在改Bean则添加记录
        buildingBeans.add(beanName);

        if (beanDefinition.isSingleton()) {
            synchronized (this.singletonBeanMap) {
                //双重检查，再次根据beanName从单例Map中尝试获取Bean实例，如果还是没有获取到再开始创建
                beanInstance = this.singletonBeanMap.get(beanName);
                if (beanInstance == null) {
                    //创建实例
                    beanInstance = createInstance(beanName, beanDefinition);
                    //存到singletonBeanMap中
                    singletonBeanMap.put(beanName, beanInstance);
                }
            }
        } else {
            //如果不要求为单例则直接创建,不用往单例BeanMap中存，所以不关心是否存在
            beanInstance = createInstance(beanName, beanDefinition);
        }

        //创建实例完成后移除创建中记录
        buildingBeans.remove(beanName);

        return beanInstance;
    }


    /**
     * <p>
     * <b>创建实例</b>
     * </p >
     *
     * @param beanDefinition <span style="color:#e38b6b;">bean定义</span>
     * @return <span style="color:#ffcb6b;"> java.lang.Object</span>
     * @throws Exception <span style="color:#ffcb6b;">异常类</span>
     * @author <span style="color:#4585ff;">RhysNi</span>
     * @date 2023/2/22
     * @CopyRight: <a href="https://blog.csdn.net/weixin_44977377?type=blog">N倪倪</a>
     */
    private Object createInstance(String beanName, BeanDefinition beanDefinition) throws Exception {
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

        //提前暴露bean实例
        this.doEarlyExposeBuildingBeans(beanName, beanInstance);

        //设置属性依赖
        this.setPropertyDIValues(beanDefinition, beanInstance);

        //创建完成后移除缓存中提前暴露的bean实例
        this.removeEarlyExposeBuildingBeans(beanName);

        //创建完实例执行初始化方法
        this.init(beanDefinition, beanInstance);

        return beanInstance;
    }

    /**
     * 移除提前暴露的bean实例
     *
     * @param beanName
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/14
     */
    private void removeEarlyExposeBuildingBeans(String beanName) {
        earlyExposeBuildingBeans.get().remove(beanName);
    }

    /**
     * 提前暴露bean实例
     *
     * @param beanName
     * @param beanInstance
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/14
     */
    private void doEarlyExposeBuildingBeans(String beanName, Object beanInstance) {
        Map<String, Object> buildingBeansMap = earlyExposeBuildingBeans.get();
        if (buildingBeansMap == null) {
            buildingBeansMap = new HashMap<>();
            earlyExposeBuildingBeans.set(buildingBeansMap);
        }
        buildingBeansMap.put(beanName, beanInstance);
    }

    /**
     * 从缓存中获取
     *
     * @param beanName
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/3/14
     */
    private Object getFromEarlyExposeBuildingBeans(String beanName) {
        Map<String, Object> buildingBeans = earlyExposeBuildingBeans.get();
        return buildingBeans == null ? null : buildingBeans.get(beanName);
    }


    /**
     * 反射设置属性依赖
     *
     * @param beanDefinition bean定义
     * @param beanInstance   bean实例
     * @return void
     * @author Rhys.Ni
     * @date 2023/3/14
     */
    private void setPropertyDIValues(BeanDefinition beanDefinition, Object beanInstance) throws Exception {
        if (!CollectionUtils.isEmpty(beanDefinition.getPropertyValues())) {
            for (PropertyValue propertyValue : beanDefinition.getPropertyValues()) {
                if (!StringUtils.isBlank(propertyValue.getName())) {
                    Class<?> clazz = beanInstance.getClass();
                    //根据属性名获取对应属性
                    Field field = clazz.getDeclaredField(propertyValue.getName());
                    //暴力访问
                    field.setAccessible(true);
                    //处理得到真正的bean引用值给bean实例属性
                    field.set(beanInstance, this.getOneArgumentRealValue(propertyValue.getValue()));
                }
            }
        }
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
        Object[] args = this.getConstructorArgumentValues(beanDefinition);
        //匹配确定具体的工厂方法
        Method method = this.determineFactoryMethod(beanDefinition, args, this.getType(beanDefinition.getFactoryBeanName()));
        //根据工厂方法得到具体的工厂Bean
        Object factoryBean = this.doGetBean(beanDefinition.getFactoryBeanName());
        return method.invoke(factoryBean, null);
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
        Object[] args = this.getConstructorArgumentValues(beanDefinition);
        //beanClass也作为type
        Class<?> beanClass = beanDefinition.getBeanClass();
        Method method = this.determineFactoryMethod(beanDefinition, args, beanClass);
        return method.invoke(beanClass, args);
    }

    /**
     * 确定使用那个工厂方法
     *
     * @param beanDefinition Bean定义
     * @param args           参数列表
     * @param beanClass      Bean类型
     * @return java.lang.reflect.Method
     * @author Rhys.Ni
     * @date 2023/3/13
     */
    private Method determineFactoryMethod(BeanDefinition beanDefinition, Object[] args, Class<?> beanClass) throws Exception {

        String methodName = beanDefinition.getFactoryMethodName();
        //无参则直接使用无参构造方法
        if (args == null) {
            return beanClass.getMethod(methodName, null);
        }

        Method method = null;
        //原型Bean从第二次开始获取Bean实例的时候就可以直接获取第一次缓存的构造方法
        method = beanDefinition.getFactoryMethod();
        if (method != null) {
            return method;
        }

        //判定工厂方法的逻辑同构造方法的判定逻辑,先根据实参类型进行精确匹配查找
        Class[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }

        try {
            method = beanClass.getMethod(methodName, paramTypes);
        } catch (Exception e) {
            //捕获异常并放行，防止异常影响后续遍历匹配
        }

        //精确匹配未找到
        if (method == null) {
            //获得所有方法，遍历，通过方法名、参数数量过滤，再比对形参类型与实参类型
            outerCycle:
            for (Method declaredMethod : beanClass.getDeclaredMethods()) {
                //匹配方法名
                if (!declaredMethod.getName().equals(methodName)) {
                    continue;
                }

                //方法名匹配上根据参数个数进行过滤
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == args.length) {
                    for (int i = 0; i < parameterTypes.length; i++) {
                        //对比每个属性的类型,不匹配则直接对比下一个同名方法的参数
                        if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                            continue outerCycle;
                        }
                    }

                    //方法名，参数列表全部吻合直接结束对比
                    method = declaredMethod;
                    break outerCycle;
                }
            }
        }

        //如果此时method仍为空则直接抛出异常
        if (method == null) {
            throw new Exception("static factory method does not exist in the beanDefinition :" + beanDefinition);
        }

        //第一次找到，如果是原型Bean直接缓存本次找到的工厂方法
        //下次构造实例对象时,直接在BeanDefinition中获取该工厂方法
        if (beanDefinition.isPrototype()) {
            beanDefinition.setFactoryMethod(method);
        }

        return method;
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
            //根据当前类所具有的有参构造方法，找到对应的构造对象
            Object[] args = this.getConstructorArgumentValues(beanDefinition);
            //推断调用哪个构造方法创建实例
            return this.determineConstructor(beanDefinition, args).newInstance(args);
        } catch (SecurityException exception) {
            logger.error("create instance error beanDefinition:{}, exception:{}", beanDefinition, exception);
            throw exception;
        }
    }

    /**
     * 获取合适的构造方法
     *
     * @param beanDefinition bean定义
     * @param args           参数列表
     * @return java.lang.reflect.Constructor<?>
     * @author Rhys.Ni
     * @date 2023/3/8
     */
    private Constructor<?> determineConstructor(BeanDefinition beanDefinition, Object[] args) throws Exception {
        Constructor<?> constructor = null;

        //没有参数就提供无参构造方法
        if (args == null) {
            return beanDefinition.getBeanClass().getConstructor(null);
        }

        //先根据参数类型进行精确匹配
        Class<?>[] paramTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            paramTypes[i] = args[i].getClass();
        }

        try {
            constructor = beanDefinition.getBeanClass().getConstructor(paramTypes);
        } catch (Exception e) {
            //无需处理该异常,让其能顺利进行后续逻辑
        }

        //没有精确匹配到构造方法，直接获取所有构造方法进行遍历
        if (constructor == null) {
            //outerCycle是为循环做的标记，因为这里涉及嵌套循环，如果不对外循环进行标记，那么在内循环中将不好控制continue与break的作用范围
            outerCycle:
            for (Constructor<?> cst : beanDefinition.getBeanClass().getConstructors()) {
                Class<?>[] parameterTypes = cst.getParameterTypes();
                //通过参数个数进行过滤
                if (parameterTypes.length == args.length) {
                    //再对比形参类型与实参类型
                    for (int i = 0; i < parameterTypes.length; i++) {
                        //isAssignableFrom：判断当前的Class对象所表示的类，是不是参数中传递的Class对象所表示的类的父类，超接口，或者是相同的类型。是则返回true，否则返回false
                        if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                            //只要有一个参数类型不匹配则跳过外循环的本轮循环
                            continue outerCycle;
                        }
                    }
                    //只要匹配上也直接结束外循环
                    constructor = cst;
                    break outerCycle;
                }
            }
        }

        //如果此时构造函数任然为空，则说明没有匹配上，直接抛异常
        if (constructor == null) {
            throw new Exception("there is no corresponding constructor for this bean definition:[" + beanDefinition + "]");
        }
        return constructor;
    }

    /**
     * 获取参数列表
     *
     * @param beanDefinition bean定义
     * @return java.lang.Object[]
     * @author Rhys.Ni
     * @date 2023/3/8
     */
    private Object[] getConstructorArgumentValues(BeanDefinition beanDefinition) throws Exception {
        List<?> argumentValues = beanDefinition.getConstructorArgumentValues();
        if (CollectionUtils.isEmpty(argumentValues)) {
            return null;
        }

        Object[] args = new Object[argumentValues.size()];
        for (int i = 0; i < argumentValues.size(); i++) {
            args[i] = getOneArgumentRealValue(argumentValues.get(i));
        }

        return args;
    }

    /**
     * 获取真的参数值
     *
     * @param originalValue 原始值
     * @return java.lang.Object
     * @author Rhys.Ni
     * @date 2023/3/8
     */
    private Object getOneArgumentRealValue(Object originalValue) throws Exception {
        //处理BeanReference,得到真正的Bean实例，从而获取真正的参数值
        Object realValue = null;
        if (originalValue != null) {
            //根据originalValue的类型决定怎么找到真正的
            if (originalValue instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) originalValue;
                //获取bean的两种方式 根据beanName/beanType
                if (StringUtils.isNotBlank(beanReference.getBeanName())) {
                    realValue = this.getBean(beanReference.getBeanName());
                } else if (beanReference.getType() != null) {
                    realValue = this.getBean(beanReference.getType());
                }
            } else if (originalValue instanceof Object[]) {

            } else if (originalValue instanceof Collection) {

            } else if (originalValue instanceof Map) {

            } else {
                realValue = originalValue;
            }
        }
        return realValue;
    }


    /**
     * 初始化方法
     *
     * @param beanDefinition bean定义
     * @param instance       实例
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

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     *
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
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
                    logger.error("bean destruction method named [" + beanName + "] failed to execute ! exception:{}", e);
                }
            }
        }
    }

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


}
