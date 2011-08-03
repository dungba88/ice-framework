package org.ice.view;

import java.util.HashMap;
import java.util.Map;

import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;

public abstract class AbstractView {

	protected HttpRequest request;
	protected HttpResponse response;
	protected String template;
	protected Map<String, Object> params;
	
	public AbstractView()	{
		params = new HashMap<String, Object>();
	}
	
	public AbstractView(HttpRequest request, HttpResponse response)	{
		this.request = request;
		this.response = response;
	}
	
	public HttpRequest getRequest() {
		return request;
	}

	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public HttpResponse getResponse() {
		return response;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
	}
	
	public void setTemplate(String template)	{
		this.template = template;
	}
	
	public void setParam(String name, Object value)	{
		params.put(name, value);
	}
	
	public Object getParam(String name)	{
		return params.get(name);
	}
	
	public abstract void render();
}
