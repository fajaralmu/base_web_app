//package com.fajar.entitymanagement.convert;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.persistence.Column;
//import javax.persistence.JoinColumn;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.fajar.entitymanagement.annotation.FormField;
//import com.fajar.entitymanagement.entity.BaseEntity;
//import com.fajar.entitymanagement.entity.Menu;
//import com.fajar.entitymanagement.entity.Page;
//import com.fajar.entitymanagement.entity.Profile;
//import com.fajar.entitymanagement.entity.RegisteredRequest;
//import com.fajar.entitymanagement.entity.User;
//import com.fajar.entitymanagement.entity.UserRole;
//import com.fajar.entitymanagement.util.CollectionUtil;
//import com.fajar.entitymanagement.util.EntityUtil;
//import com.fajar.entitymanagement.util.StringUtil;
//
//public class GolangConversion {
//
//	public static void main(String[] args) {
//		String str = "type InterfaceEntity interface {\r\n" + "}";
//		System.out.println(str);
//		printObject(BaseEntity.class, User.class, UserRole.class, Menu.class, Page.class, Profile.class,
//				RegisteredRequest.class);
//	}
//
//	private static void printObject(Class<?>... classes) {
//		for (int i = 0; i < classes.length; i++) {
//			printObjectType(classes[i]);
//		}
//
//	}
//
//	static String enhanceAcronyms(String str) {
//		String extractedString = StringUtil.extractCamelCase(str);
//		String[] words = extractedString.split(" ");
//		StringBuilder stringBuilder = new StringBuilder();
//
//		for (int i = 0; i < words.length; i++) {
//			String word = words[i];
//			if (word.length() <= 3) {
//				word = word.toUpperCase();
//			}
//			stringBuilder.append(word);
//		}
//		return stringBuilder.toString();
//	}
//
//	static void printObjectType(Class<?> clazz) {
//
//		System.out.println("//" + clazz.getSimpleName() + " is the entity");
//		System.out.println("type " + clazz.getSimpleName() + " struct {");
//
//		List<Field> fields = EntityUtil.getDeclaredFields(clazz, false, true);
//		Class<?> superClass = clazz.getSuperclass();
//
//		if (superClass != null && superClass.equals(Object.class) == false) {
//			// System.out.println("\t" + superClass.getSimpleName());
//		}
//		System.out.println("\tInterfaceEntity\n\tgorm.Model");
//
//		for (Field field : fields) {
//
//			String fieldName = StringUtil.upperCaseFirstChar(field.getName());
//			fieldName = enhanceAcronyms(fieldName);
//
//			Class<?> fieldType = field.getType();
//
//			String fieldTypeName = fieldType.getCanonicalName().startsWith("java")
//					? fieldType.getSimpleName().toLowerCase()
//					: fieldType.getSimpleName();
//
//			switch (fieldTypeName) {
//			case "boolean":
//				fieldTypeName = "bool";
//				break;
//			case "long":
//			case "biginteger":
//				fieldTypeName = "int";
//				break;
//			case "date":
//				fieldTypeName = "time.Time";
//				break;
//
//			default:
//				break;
//			}
//
//			System.out.print("\t" + fieldName + " ");
//			if (EntityUtil.getFieldAnnotation(field, JoinColumn.class) != null) {
//				System.out.print("*");
//			}
//			System.out.print(fieldTypeName);
//
//			printFieldTags(field);
//			System.out.print("\n");
//		}
//		System.out.println("}");
////		System.out.println(tableNameTemplate(clazz));
//	}
//
//	private static String tableNameTemplate(Class<?> _class) {
//		String template = "func ($className) TableName() string {\r\n" + "  return \"$result\"\n" + "}";
//		template = template.replace("$className", _class.getSimpleName());
//		String tableName = StringUtil.camelCaseToSnakeCase(_class.getSimpleName());
//		template = template.replace("$result", tableName);
//		return template;
//	}
//
//	private static void printFieldTags(Field field) {
//		boolean hasTag = false;
//		Column column = EntityUtil.getFieldAnnotation(field, Column.class);
//		String gormTag = "", customTag = "";
//		if (null != column) {
//			List<String> gormTags = new ArrayList<String>();
//			if (column.unique()) {
//				gormTags.add("unique");
//			}
//			if (!column.nullable()) {
//				gormTags.add("not null");
//			}
//			if (gormTags.size() > 0) {
//				Object[] tagsArray = gormTags.toArray();
//				String[] stringArray = CollectionUtil.toArrayOfString(tagsArray);
//				String fieldTag = String.join(";", stringArray);
//
//				gormTag = "gorm:\"" + fieldTag + "\" ";
//
//				hasTag = true;
//			}
//		}
//
//		JoinColumn joinColumn = EntityUtil.getFieldAnnotation(field, JoinColumn.class);
//		boolean hasJoinColumnTag = false;
//
//		List<String> customTags = new ArrayList<String>();
//		String foreignKey = field.getType().getSimpleName() + "ID";
//		if (null != joinColumn) {
//			customTags.add("foreignKey:" + foreignKey);
//			hasJoinColumnTag = true;
//
//		}
//
//		FormField formField = EntityUtil.getFieldAnnotation(field, FormField.class);
//		if (null != formField) {
//			customTags.add("type:" + formField.type());
//			if (formField.multiple()) {
//				customTags.add("multipleImage:TRUE");
//			}
//			if (!formField.required()) {
//				customTags.add("required:FALSE");
//			}
//			if (!formField.emptyAble( )) {
//				customTags.add("emptyAble:FALSE");
//			}
//			if (!formField.lableName().equals("")) {
//				customTags.add("lableName:" + formField.lableName());
//			}
//			if (!formField.defaultValue().equals("")) {
//				customTags.add("defaultValue:" + formField.defaultValue());
//			}
//			if (!formField.optionItemName().equals("")) {
//				customTags.add("optionItemName:" + formField.optionItemName());
//			}
//			if (formField.availableValues().length > 0) {
//
//				customTags.add("availableValues:" + String.join(",", formField.availableValues()));
//			}
//			if (formField.detailFields().length > 0) {
//
//				customTags.add("detailFields:" + String.join(",", formField.detailFields()));
//			}
//		}
//
//		if (customTags.size() > 0) {
//			Object[] tagsArray = customTags.toArray();
//			String[] stringArray = CollectionUtil.toArrayOfString(tagsArray);
//			String fieldTag = String.join(";", stringArray);
//			customTag = "custom:\"" + fieldTag + "\"";
//			hasTag = true;
//		}
//
//		if (hasTag) {
//			System.out.print("\t`" + gormTag + "" + customTag + "`");
//		}
//
//		if (hasJoinColumnTag) {
//			System.out.println();
//			System.out.println("\t" + foreignKey + " uint16");
//		}
//	}
//
//}
