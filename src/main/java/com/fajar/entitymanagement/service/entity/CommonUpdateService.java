package com.fajar.entitymanagement.service.entity;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fajar.entitymanagement.annotation.FormField;
import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.util.EntityUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonUpdateService extends BaseEntityUpdateService {

	@Override
	public WebResponse saveEntity(BaseEntity entity, boolean newRecord, EntityUpdateInterceptor updateInterceptor) {
		log.info("saving entity: {}", entity.getClass());
		entity = (BaseEntity) copyNewElement(entity, newRecord);

		validateEntityFields(entity, newRecord);
		interceptPreUpdate(entity, updateInterceptor);
		BaseEntity newEntity = entityRepository.save(entity);

		return WebResponse.builder().entity(newEntity).build();
	}

	/**
	 * execute things before persisting
	 * 
	 * @param entity
	 * @param updateInterceptor
	 */
	private void interceptPreUpdate(BaseEntity entity, EntityUpdateInterceptor updateInterceptor) {

		if (null != updateInterceptor && null != entity) {
			log.info("Pre Update {}", entity.getClass().getSimpleName());
			try {
				updateInterceptor.preUpdate(entity);
				log.info("success pre update");
			} catch (Exception e) {

				log.error("Error pre update entity");
				e.printStackTrace();
			}
		}
	}

	/**
	 * validate object properties' value
	 * 
	 * @param entity
	 * @param newRecord
	 */
	private void validateEntityFields(BaseEntity entity, boolean newRecord) {
		log.info("validating entity: {} newRecord: {}", entity.getClass(), newRecord);
		try {

			BaseEntity existingEntity = null;
			if (!newRecord) {
				existingEntity = (BaseEntity) entityRepository.findById(entity.getClass(), entity.getId());
			}

			List<Field> fields = EntityUtil.getDeclaredFields(entity.getClass());
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);

				try {

					FormField formfield = field.getAnnotation(FormField.class);
					if (null == formfield) {
						continue;
					}

					Object fieldValue = field.get(entity);

					switch (formfield.type()) {
					case FIELD_TYPE_IMAGE:

						if (newRecord == false && fieldValue == null && existingEntity != null) {
							Object existingImage = field.get(existingEntity);
							field.set(entity, existingImage);
						} else {
							String imageName = updateImage(field, entity);
							field.set(entity, imageName);
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
			log.error("Error validating entity {}", entity.getClass().getSimpleName());
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
	private String updateImage(Field field, BaseEntity object) {
		try {
			Object base64Value = field.get(object);
			return writeImage(object, base64Value);

		} catch (IllegalArgumentException | IllegalAccessException e) {

			e.printStackTrace();
		}
		return null;
	}

	private String writeImage(BaseEntity object, Object base64Value) {
		String fileName = null;
		if (null != base64Value && base64Value.toString().trim().isEmpty() == false) {
			try {
				fileName = fileService.writeImage(object.getClass().getSimpleName(), base64Value.toString());
			} catch (IOException e) {
				log.error("Error writing image for {}", object.getClass().getSimpleName());
				e.printStackTrace();
			}
		}
		return fileName;
	}
}
