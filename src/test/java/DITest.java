import com.rhys.spring.DI.BeanReference;
import com.rhys.spring.IoC.GenericBeanDefinition;
import com.rhys.spring.IoC.PreBuildBeanFactory;
import com.rhys.spring.demo.TestA;
import com.rhys.spring.demo.TestB;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rhys.Ni
 * @version 1.0
 * @date 2023/3/9 11:19 PM
 */
public class DITest {

    private static PreBuildBeanFactory beanFactory = new PreBuildBeanFactory();

    @Test
    public void testDI() throws Exception {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        //设置BeanClass
        beanDefinition.setBeanClass(TestA.class);
        //定义构造参数依赖
        List<Object> args = new ArrayList<>();
        args.add("testABean");
        //Bean依赖通过BeanReference处理
        args.add(new BeanReference("bBean"));
        //定义beanDefinition的构造参数列表
        beanDefinition.setConstructorArgumentValues(args);
        //注册BeanDefinition
        beanFactory.registryBeanDefinition("aBean", beanDefinition);

        //配置TestBBean
        beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(TestB.class);

        args = new ArrayList<>();
        args.add("testBBean");

        beanDefinition.setConstructorArgumentValues(args);
        beanFactory.registryBeanDefinition("bBean", beanDefinition);

        beanFactory.instantiateSingleton();

        TestA testA = (TestA) beanFactory.getBean("aBean");
        testA.execute();
    }
}
