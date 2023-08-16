package com.rhys.testSourceCode.aop;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;

/**
 * <p>
 * <b>功能描述</b>
 * </p >
 *
 * @author : RhysNi
 * @version : v1.0
 * @date : 2023/8/11 15:47
 * @CopyRight :　<a href="https://blog.csdn.net/weixin_44977377?type=blog">倪倪N</a>
 */
public class RhysIntroductionTest {
    public static void main(String[] args) {

        //创建新的代理工厂。将代理给定目标实现的对应接口。
        ProxyFactory proxyFactory = new ProxyFactory(new RAop());

        //设置是否直接代理目标类，而不仅仅是代理特定接口。默认值为“false”。
        proxyFactory.setProxyTargetClass(true);

        //为给定的通知创建一个默认的拦截器作为通知者
        DefaultIntroductionAdvisor advisor = new DefaultIntroductionAdvisor(new RhysIntroductionInterceptor(),RhysEnhancedAop.class);
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
 * DelegatingIntroductionInterceptor ：方便实现 IntroductionInterceptor 接口。子类只需要扩展这个类并实现要自己引入的接口。在这种情况下，委托是子类实例本身。或者，单独的委托可以实现该接口，并通过委托 bean 属性进行设置。
 * @author Rhys.Ni
 * @date 2023/8/17
 */
class RhysIntroductionInterceptor extends DelegatingIntroductionInterceptor implements RhysEnhancedAop {
    @Override
    public void callEnhance(String msg) {
        System.out.println("execute RhysIntroductionInterceptor.call : " + msg);
    }
}