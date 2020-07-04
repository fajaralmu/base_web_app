package com.fajar.entitymanagement.config;
//package com.fajar.config;
//
//import java.io.Serializable;
//import java.util.Iterator;
//
//import javax.annotation.PostConstruct;
//
//import org.hibernate.EmptyInterceptor;
//import org.hibernate.type.Type;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.orm.jpa.JpaTransactionManager;
//
//
//public class HibernateInterceptor extends EmptyInterceptor {
//
//	/**
//		 * 
//		 */
//	private static final long serialVersionUID = 2533557486449602805L;
// 
//	public static String yy = "19";
//	public static String mm = "11";
//
//	@Autowired
//	private JpaTransactionManager jpaTransactionManager;
//
//	public HibernateInterceptor() {
//
//		System.out.println("==============**********HibernateInterceptor************============");
//	}
//
//	@PostConstruct
//	public void init() {
//
//	}
//
//	@Override
//	public String onPrepareStatement(String sql) {
//
//		String prepedStatement = super.onPrepareStatement(sql);
//		System.out.println("=========INTERCEPTOR=========");
//		System.out.println("BEFORE FILTER SQL="+prepedStatement);
//		prepedStatement = prepedStatement.replace("$mm",mm);
//		prepedStatement = prepedStatement.replace("$yy",yy);
//		System.out.println("AFTER FILTER SQL=" + prepedStatement);
//		return prepedStatement;
//
//	}
//
//	private int updates;
//	private int creates;
//	private int loads;
//	 
//	@Override
//	public void onDelete(
//			Object entity, 
//			Serializable id, 
//			Object[] state, 
//			String[] propertyNames, 
//			Type[] types) {
//		super.onDelete(entity, id, state, propertyNames, types);
//		// do nothing
//	}
//	@Override
//	// This method is called when Employee object gets updated.
//	public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
//			String[] propertyNames, Type[] types) {
////	         if ( entity instanceof Employee ) {
////	            System.out.println("Update Operation");
////	            return true; 
////	         }
//		return true;
//	}
//	@Override
//	public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//		// do nothing
//		return true;
//	}
//	@Override
//	// This method is called when Employee object gets created.
//	public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
//
//		return true;
//	}
//	@Override
//	// called before commit into database
//	public void preFlush(Iterator iterator) {
//		System.out.println("preFlush");
//	}
//	@Override
//	// called after committed into database
//	public void postFlush(Iterator iterator) {
//		System.out.println("postFlush");
//	}
//
//}
