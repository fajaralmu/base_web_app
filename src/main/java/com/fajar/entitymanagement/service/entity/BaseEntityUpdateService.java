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
public class BaseEntityUpdateService {

	@Autowired
	protected FileService fileService;
	@Autowired
	protected EntityRepository entityRepository;
	
	@PostConstruct
	public void init() {
		LogProxyFactory.setLoggers(this);
	}

	public WebResponse saveEntity(BaseEntity baseEntity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		log.error("saveEntity Method not implemented");
		return WebResponse.failed("method not implemented");
	}

	protected BaseEntity copyNewElement(BaseEntity source, boolean newRecord) {
		return EntityUtil.copyFieldElementProperty(source, source.getClass(), !newRecord);
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
}
