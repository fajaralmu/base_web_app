package com.fajar.entitymanagement.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GeneralConfiguration {
	 

//	@Bean
//	public SessionFactory generateSession() {
//		System.out.println("SESSION FACTORY");
//		try {
//			factory = new org.hibernate.cfg.Configuration().setInterceptor(new HibernateInterceptor()).configure()
//					.buildSessionFactory();
////			Session session = factory.openSession();
//			System.out.println("session factory created");
//			return factory;
//		} catch (Throwable ex) {
//			System.err.println("Failed to create sessionFactory object." + ex);
//			throw new ExceptionInInitializerError(ex);
//		}
//		
//
//	}

//	@Bean
//	public Session hibernateSession(SessionFactory sessionFactory) {
//		return sessionFactory.openSession();
//	}

}
