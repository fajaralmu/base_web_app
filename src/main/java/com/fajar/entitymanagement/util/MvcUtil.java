package com.fajar.entitymanagement.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fajar.entitymanagement.controller.MvcAccountController;
import com.fajar.entitymanagement.entity.setting.EntityProperty;

public class MvcUtil {

	public static String getHost(HttpServletRequest request) {
		StringBuffer url = request.getRequestURL();
		String uri = request.getRequestURI();
		String host = url.substring(0, url.indexOf(uri)); // result
		return host;
	}

	public static Model constructCommonModel(HttpServletRequest request, EntityProperty entityProperty, Model model,
			String string, String string2) {
		return constructCommonModel(request, entityProperty, model, string, string2, null);
	}

	public static Model constructCommonModel(HttpServletRequest request, EntityProperty entityProperty, Model model,
			String title, String page, String option) {

		boolean withOption = false;
		String optionJson = "null";

		if (null != option) {
			System.out.println("=========REQUEST_OPTION: " + option);
			String[] options = option.split("&");
			Map<String, Object> optionMap = new HashMap<String, Object>();
			for (String optionItem : options) {
				String[] optionKeyValue = optionItem.split("=");
				if (optionKeyValue == null || optionKeyValue.length != 2) {
					continue;
				}
				optionMap.put(optionKeyValue[0], optionKeyValue[1]);
			}
			if (optionMap.isEmpty() == false) {
				withOption = true;
				optionJson = MyJsonUtil.mapToJson(optionMap);
				System.out.println("=========GENERATED_OPTION: " + optionMap);
				System.out.println("=========OPTION_JSON: " + optionJson);
			}
		}
		model.addAttribute("title", title);
		model.addAttribute("entityProperty", entityProperty);
		model.addAttribute("page", page);

		model.addAttribute("withOption", withOption);
		model.addAttribute("options", optionJson);
		model.addAttribute("singleRecord", false);
		return model;
	}

	public static List<Method> getRequesMappingMethods(Class<?> _class) {
		Method[] methods = _class.getMethods();

		List<Method> result = new ArrayList<Method>();

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getAnnotation(RequestMapping.class) != null) {
				result.add(method);
			}
		}
		return result;
	}

}
