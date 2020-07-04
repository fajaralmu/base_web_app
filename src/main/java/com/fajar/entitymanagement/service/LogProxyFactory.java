package com.fajar.entitymanagement.service;

import java.lang.reflect.Field;
import java.util.Date;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.jpa.repository.JpaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogProxyFactory {

	private static final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();
	
	public static void setLoggers(Object obj) {
		log.info("set logger to object: {}", obj.getClass());
		
		Field[] fields = obj.getClass().getDeclaredFields();
		for (Field field : fields) {
			Autowired annotated = field.getAnnotation(Autowired.class);
			if(annotated == null) {
				continue;
			}
			field.setAccessible(true);
			try {
				Object fieldValue = field.get(obj); 
				boolean isInterface = fieldValue.getClass().isInterface();
				boolean repo = fieldValue.getClass().getSuperclass()!=null &&
							fieldValue.getClass().getSuperclass().equals(JpaRepository .class);
				 
				if(isInterface || repo) {
					continue;
				}
				field.set(obj, logWrapper(fieldValue));
			} catch (Exception e) { 
				continue;
			}
		}
		
	}
	
	public static Object logWrapper(Object obj) {
		
		
		ProxyFactory proxyFactory = new ProxyFactory(obj);
		Logger logger = LoggerFactory.getLogger(obj.getClass());
		MethodInterceptor mi = new MethodInterceptor() {
			@Override
			public Object invoke(MethodInvocation invocation) throws Throwable {

				Date start = new Date();

				/**
				 * prints method name
				 */

				Object methodName = invocation.getMethod().getName();
				logger.info("=========>[Execute Method: {}]", methodName);  
				try {
					/**
					 * prints parameters of the method
					 */
					String[] params = null;
					try {
						params = discoverer.getParameterNames(invocation.getMethod()); 
					}catch ( Exception e) {
						// TODO: handle exception
					}
					if(params == null) {
						params = new String[] {"arg0"};
					}
					
					Object[] arguments = invocation.getArguments();
					if(null == arguments || arguments.length == 0) {
						logger.info("[No Argument]");
					}else
						for (int i = 0; i < arguments.length; i++) {
							String parameterName = params[i] == null ? "arg" + i : params[i];
							logger.info("[argument{}] {}:{}",i, parameterName, arguments[i]);
						}
				}catch(Exception ex) {
					System.out.println("[ERROR] logging "+methodName);
				}
				try {
					final Object retVal = invocation.proceed();
					Date end = new Date();
					String retValToLog = "";
					
					if(retVal != null && retVal.toString().length() > 500) {
						retValToLog = retVal.toString().substring(0, 490).concat("... [*_*]");
					}
					
					logger.info("[{} has return value] : {}", invocation.getMethod().getName(), retValToLog);
					logger.info("<=========[Finish Execute Method: {}] with success in {}ms", invocation.getMethod().getName(),
							 end.getTime() - start.getTime());
					
					return retVal;
				} catch (Throwable e) {
					Date end = new Date();
					logger.error("<=========[Finish Execute Method: {}] with error in {}ms", invocation.getMethod().getName(),
							 end.getTime() - start.getTime(), e);
					throw e;
				}
			}
		};
		proxyFactory.addAdvice(mi);
		obj = proxyFactory.getProxy();
		return obj;
	}
}
