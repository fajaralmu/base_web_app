package com.fajar.entitymanagement.service.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.annotation.StoreValueTo;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.repository.EntityRepository;
import com.fajar.entitymanagement.service.FileService;
import com.fajar.entitymanagement.service.LogProxyFactory;
import com.fajar.entitymanagement.util.CollectionUtil;
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
	
	/**
	 * validate object properties' value
	 * 
	 * @param object
	 * @param newRecord
	 */
	protected void validateEntityFields(BaseEntity object, boolean newRecord) {
		log.info("validating entity: {} newRecord: {}", object.getClass(), newRecord);
		try {

			BaseEntity existingEntity = null;
			if (!newRecord) {
				existingEntity = (BaseEntity) entityRepository.findById(object.getClass(), object.getId());
			}

			List<Field> fields = EntityUtil.getDeclaredFields(object.getClass());
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				try {

					FormField formfield = field.getAnnotation(FormField.class);
					if (null == formfield) {
						continue;
					}

					Object fieldValue = field.get(object);

					switch (formfield.type()) {
					case FIELD_TYPE_IMAGE:

						if (newRecord == false && fieldValue == null && existingEntity != null) {
							Object existingImage = field.get(existingEntity);
							field.set(object, existingImage);
						} else {
							String imageName = updateImage(field, object, formfield.iconImage());
							field.set(object, imageName);
						}
						break;
					case FIELD_TYPE_FIXED_LIST:
						if (fieldValue == null)
							break;
						if (formfield.multipleSelect()) {
							String storeToFieldName = field.getAnnotation(StoreValueTo.class).value(); 
							
							Field idField = CollectionUtil.getIDFieldOfUnderlyingListType(field);
							Field storeToField = EntityUtil.getDeclaredField(object.getClass(), storeToFieldName);
							
							Object[] valueAsArray = ((Collection) fieldValue).toArray(); 
							CharSequence[] actualFieldValue = new String[valueAsArray.length];
							
							for (int j = 0; j < valueAsArray.length; j++) {
								actualFieldValue[j] = String.valueOf(idField.get(valueAsArray[j]));
							}
							
							storeToField.set(object, String.join("~", actualFieldValue));
						}
						break;
					default:
						break;
					}
				} catch (Exception e) {
					log.error("Error validating field: {}", field.getName());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			//
			log.error("Error validating entity {}", object.getClass().getSimpleName());
			e.printStackTrace();
		}
	}

	/**
	 * update image field, writing to disc
	 * 
	 * @param field
	 * @param object
	 * @return
	 */
	private String updateImage(Field field, BaseEntity object, boolean isIcon) {
		try {
			Object base64Value = field.get(object);
			return writeImage(object, base64Value, isIcon);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			e.printStackTrace();
		}
		return null;
	}

	private String writeImage(BaseEntity object, Object base64Value, boolean isIcon) {
		String fileName = null;
		if (null != base64Value && base64Value.toString().trim().isEmpty() == false) {
			try {
				if(isIcon) {
					fileName = fileService.writeIcon(object.getClass().getSimpleName(), base64Value.toString());
				}else {
					fileName = fileService.writeImage(object.getClass().getSimpleName(), base64Value.toString());
				}
				
			} catch (IOException e) {
				log.error("Error writing image for {}", object.getClass().getSimpleName());
				e.printStackTrace();
			}
		}
		return fileName;
	}
}
