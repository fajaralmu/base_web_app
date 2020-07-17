package com.fajar.entitymanagement.controller;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fajar.entitymanagement.dto.WebResponse;
import com.fajar.entitymanagement.util.ThreadUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author fajar
 *
 */
@Controller
@RequestMapping("web")
@Slf4j
public class MvcUtilController extends BaseController {

	@Autowired
	private ObjectMapper objectMapper;
	
	public MvcUtilController() {
		log.info("-----------------MvcUtilController------------------");
	}

	@GetMapping(value = "notfound")
	public String notFoundPage(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak ditemukan");
		return "error/notfound";
	}

	@ExceptionHandler({ RuntimeException.class })
	public String databaseError() {
		// Nothing to do. Returns the logical view name of an error page, passed
		// to the view-resolver(s) in usual way.
		// Note that the exception is NOT available to this view (it is not added
		// to the model) but see "Extending ExceptionHandlerExceptionResolver"
		// below.
		return "error/notfound";
	}

	@RequestMapping(value = "app-error")
	public ModelAndView renderErrorPage(HttpServletRequest httpRequest, HttpServletResponse httpServletResponse) throws Exception {

		boolean isGet = httpRequest.getMethod().toLowerCase().equals("get");

		if (isGet) {

			ModelAndView errorPage = new ModelAndView("error/errorPage");

			int httpErrorCode = getErrorCode(httpRequest);
			errorPage.addObject("errorCode", httpErrorCode);
			errorPage.addObject("errorException", getAttribute(httpRequest, "javax.servlet.error.exception"));
			errorPage.addObject("errorMessage", getAttribute(httpRequest, "javax.servlet.error.message"));

			ThreadUtil.run(() -> {
				printHttpRequestAttrs(httpRequest);
			});

			return errorPage;
		} else {
			String jsonString = objectMapper.writeValueAsString(WebResponse.builder()
					.code(String.valueOf(httpServletResponse.getStatus())).message("ERROR").build());
			
			httpServletResponse.setContentType("application/json");
			httpServletResponse.getWriter().write(jsonString);
			
			return null;
		}
	}

	private void printHttpRequestAttrs(HttpServletRequest httpRequest) {
		Enumeration<String> attrNames = httpRequest.getAttributeNames();
		log.debug("========= error request http attrs ========");
		int number = 1;
		while (attrNames.hasMoreElements()) {
			String attrName = attrNames.nextElement();
			Object attributeValue = httpRequest.getAttribute(attrName);

			log.debug(number + ". " + attrName + " : " + attributeValue + " || TYPE: "
					+ (attributeValue == null ? "" : attributeValue.getClass()));
			printException(attributeValue);

			number++;
		}
		log.debug("===== ** end ** ====");
	}

	private void printException(Object ex) {
		try {
			Exception e = (Exception) ex;
			e.printStackTrace();
		} catch (Exception e) {

		}
	}

	private int getErrorCode(HttpServletRequest httpRequest) {
		try {
			return (Integer) httpRequest.getAttribute("javax.servlet.error.status_code");
		} catch (Exception e) {

			return 500;
		}
	}

	private Object getAttribute(HttpServletRequest httpServletRequest, String name) {
		return httpServletRequest.getAttribute(name);
	}

	@GetMapping(value = "noaccess")
	public String halamanNotAccessable(Model model) throws IOException {
		model.addAttribute("pesan", "Halaman tidak dapat diakses");
		return "error/notfound";
	}

}
