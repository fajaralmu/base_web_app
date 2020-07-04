package com.fajar.entitymanagement.repository;

import org.hibernate.Session;

public interface PersistenceOperation<T> {
	
	public T doPersist(Session hibernateSession);

}
