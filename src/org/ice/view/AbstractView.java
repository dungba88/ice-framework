package org.ice.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractView {

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected String template;
	
	public AbstractView()	{
		
	}
	
	public AbstractView(HttpServletRequest request, HttpServletResponse response)	{
		this.request = request;
		this.response = response;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void setTemplate(String template)	{
		this.template = template;
	}
	
	public void setAttributes(String name, String value)	{
		request.setAttribute(name, value);
	}
	
	public abstract void render();
}
