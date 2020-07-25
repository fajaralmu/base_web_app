package com.fajar.entitymanagement.convert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import com.fajar.entitymanagement.entity.BaseEntity;
import com.fajar.entitymanagement.entity.Menu;
import com.fajar.entitymanagement.entity.Page;
import com.fajar.entitymanagement.entity.Profile;
import com.fajar.entitymanagement.entity.RegisteredRequest;
import com.fajar.entitymanagement.entity.User;
import com.fajar.entitymanagement.entity.UserRole;
import com.fajar.entitymanagement.util.CollectionUtil;
import com.fajar.entitymanagement.util.EntityUtil;
import com.fajar.entitymanagement.util.StringUtil;

public class GolangConversion {

	public static void main(String[] args) {
		printObject(BaseEntity.class, User.class, UserRole.class, Menu.class, Page.class, Profile.class,
				RegisteredRequest.class);
	}

	private static void printObject(Class<?>... classes) {
		for (int i = 0; i < classes.length; i++) {
			printObjectType(classes[i]);
		}

	}
	
	static String enhanceAcronyms(String str) {
		String extractedString = StringUtil.extractCamelCase(str);
		String[] words = extractedString.split(" ");
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < words.length; i++) {
			String word = words[i];
			if(word.length() <= 3) {
				word = word.toUpperCase();
			}
			stringBuilder.append(word);
		}
		return stringBuilder.toString();
	}

	static void printObjectType(Class<?> clazz) {

		System.out.println("//" + clazz.getSimpleName() + " is the entity");
		System.out.println("type " + clazz.getSimpleName() + " struct {");

		List<Field> fields = EntityUtil.getDeclaredFields(clazz, false, true);
		Class<?> superClass = clazz.getSuperclass();
		
		if (superClass != null && superClass.equals(Object.class) == false) {
			System.out.println("\t" + superClass.getSimpleName());
		}

		for (Field field : fields) {

			String fieldName = StringUtil.upperCaseFirstChar(field.getName());
			fieldName = enhanceAcronyms(fieldName);
			
			Class<?> fieldType = field.getType();

			String fieldTypeName = fieldType.getCanonicalName().startsWith("java")
					? fieldType.getSimpleName().toLowerCase()
					: fieldType.getSimpleName();

			switch (fieldTypeName) {
			case "boolean":
				fieldTypeName = "bool";
				break;
			case "long":
			case "biginteger":
				fieldTypeName = "int";
				break;
			case "date":
				fieldTypeName = "time.Time";
				break;

			default:
				break;
			}

			System.out.print("\t" + fieldName + " " + fieldTypeName);

			printFieldTags(field);
			System.out.print("\n");
		}
		System.out.println("}");
	}

	private static void printFieldTags(Field field) {

		Column column = EntityUtil.getFieldAnnotation(field, Column.class);
		if (null != column) {
			List<String> tags = new ArrayList<String>();
			if (column.unique()) {
				tags.add("unique");
			}
			if (!column.nullable()) {
				tags.add("not null");
			}
			Object[] tagsArray = tags.toArray();
			String[] stringArray = CollectionUtil.toArrayOfString(tagsArray);
			String fieldTag = String.join(";", stringArray);
			if (stringArray.length > 0)
				System.out.print("\t`gorm:\"" + fieldTag + "\"`");
		}
	}

}
