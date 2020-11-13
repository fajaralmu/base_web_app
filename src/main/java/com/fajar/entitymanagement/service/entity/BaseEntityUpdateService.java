package com.fajar.entitymanagement.service.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.service.FileService;
import com.fajar.entitymanagement.service.LogProxyFactory;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseEntityUpdateService<T extends BaseEntity> {

	@Autowired
	protected FileService fileService;
	@Autowired
	protected EntityRepository entityRepository;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse saveEntity(T baseEntity, boolean newRecord ) {
		log.error("saveEntity Method not implemented");
		return WebResponse.failed("method not implemented");
	}

	protected T copyNewElement(T source, boolean newRecord) {
		try {
			return (T) EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
		}catch (Exception e) {
			log.error("Error copy new element");
			e.printStackTrace();
			return source;
		}
	}

	protected List<String> removeNullItemFromArray(String[] array) {
		List<String> result = new ArrayList<>();
		for (String string : array) {
			if (string != null) {
				result.add(string);
			}
		}
		return result;

	}
	
	protected EntityUpdateInterceptor<T> getUpdateInterceptor(T baseEntity){
		return baseEntity.modelUpdateInterceptor();
	}
}
