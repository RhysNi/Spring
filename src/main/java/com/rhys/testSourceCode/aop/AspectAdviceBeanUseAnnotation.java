package com.rhys.testSourceCode.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Component
@EnableAspectJAutoProxy
public class AspectAdviceBeanUseAnnotation {

	// 定义一个全局的Pointcut
	@Pointcut("execution(* com.rhys.testSourceCode.aop.*.exec*(..))")
	public void doMethods() {
	}

	@Pointcut("execution(* com.rhys.testSourceCode.aop.*.service*(..))")
	public void services() {
	}

	// 定义一个Before Advice
	@Before("doMethods() && args(tk,..)")
	public void before3(String tk) {
		System.out.println("----------- AspectAdviceBeanUseAnnotation before3  增强  参数tk= " + tk);
	}

	@Around("services() && args(name,..)")
	public Object around2(ProceedingJoinPoint pjp, String name) throws Throwable {
		System.out.println("--------- AspectAdviceBeanUseAnnotation arround2 参数 name=" + name);
		System.out.println("----------- AspectAdviceBeanUseAnnotation arround2 环绕-前增强 for " + pjp);
		Object ret = pjp.proceed();
		System.out.println("----------- AspectAdviceBeanUseAnnotation arround2 环绕-后增强 for " + pjp);
		return ret;
	}

	@AfterReturning(pointcut = "services()", returning = "retValue")
	public void afterReturning(Object retValue) {
		System.out.println("----------- AspectAdviceBeanUseAnnotation afterReturning 增强 , 返回值为： " + retValue);
	}

	@AfterThrowing(pointcut = "services()", throwing = "e")
	public void afterThrowing(JoinPoint jp, Exception e) {
		System.out.println("----------- AspectAdviceBeanUseAnnotation afterThrowing 增强  for " + jp);
		System.out.println("----------- AspectAdviceBeanUseAnnotation afterThrowing 增强  异常 ：" + e);
	}

	@After("doMethods()")
	public void after(JoinPoint jp) {
		System.out.println("----------- AspectAdviceBeanUseAnnotation after 增强  for " + jp);
	}

	/*
	 * BeanDefinitionRegistryPostProcessor BeanFactoryPostProcessor
	 * InstantiationAwareBeanPostProcessor Bean实例创建前后 BeanPostProcessor
	 */
}
