package com.jiayaming.dubbo.web.comm;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

//@Component("aroundLogout")//放开这里即可增加进出方法时间的打印
@Aspect
public class AroundLogout {
	@Pointcut("execution(* com.jiayaming.dubbo.web.controller..*.*(..))")
	public void pointcut() {
		
	}
	@Around(value="pointcut()")
	public Object logout(ProceedingJoinPoint pjd) throws Throwable {
		String methodName=((MethodSignature)pjd.getSignature()).getMethod().getName();
		System.out.println(methodName+"开始时间"+new Date());
		Object o=pjd.proceed();
		System.out.println(methodName+"结束时间"+new Date());
		return o;
	}
}
