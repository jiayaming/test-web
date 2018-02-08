package com.jiayaming.dubbo.web.comm;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

public class ExceptionCatch{
	public Object catContrException(ProceedingJoinPoint pjd) throws Throwable {
		Object o=null;
		String methodName=((MethodSignature)pjd.getSignature()).getMethod().getName();
		try {
			o=pjd.proceed();
		} catch (Exception e) {
			System.out.println("aaaaa");
			e.printStackTrace();
		}
		return o;
	}
}
