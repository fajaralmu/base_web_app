package com.fajar.entitymanagement.config;

import static java.lang.System.out;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import com.fajar.entitymanagement.util.StringUtil;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomFilter implements javax.servlet.Filter {

	public CustomFilter() {
		log.info("_________________CustomFilter______________");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		Date startTime = new Date();
		HttpServletRequest req = (HttpServletRequest) request;

		final String randomID = randomID();
		/*
		 * ================== REQUEST ==================
		 */
		out.println();
		out.println("****************************** BEGIN API " + randomID + " ***************************");
		out.println(uriInfo(req));
		printRequestHeaders(req);
		out.println("********************************************************************");
		out.println();

		try {
			chain.doFilter(request, response);

		} catch (Exception e) {

			throw e;
		} finally {
			Date endTime = new Date();
			HttpServletResponse res = (HttpServletResponse) response;
			/*
			 * ================== RESPONSE =================
			 */
			out.println();
			out.println("***************************** END API *******************************");
			out.println(uriInfo(req));
			printResponseHeaders(res);
			out.println("Status: " + res.getStatus() + " Duration: " + getDuration(startTime, endTime) + " ms");
			out.println("*********************************************************************");
			out.println();
		}

	}

	private String uriInfo(HttpServletRequest req) {
		return ("URI: [" + req.getMethod() + "]" + req.getRequestURI());
	};

	private String randomID() {

		return StringUtil.generateRandomNumber(5);
	}

	private void printResponseHeaders(HttpServletResponse res) {
		Collection<String> headers = res.getHeaderNames();
		for (String header : headers) {
			out.println(header + ": " + res.getHeader(header));
		}
	}

	private void printRequestHeaders(HttpServletRequest req) {
		Enumeration<String> headers = req.getHeaderNames();
		while (headers.hasMoreElements()) {
			String header = headers.nextElement();
			out.println(header + ": " + req.getHeader(header));
		}
	}

	private long getDuration(Date startTime, Date endTime) {
		return endTime.getTime() - startTime.getTime();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	} 

	// other methods
}
