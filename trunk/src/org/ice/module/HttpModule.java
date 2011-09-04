package org.ice.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ice.Config;
import org.ice.exception.IceException;
import org.ice.exception.NotFoundException;
import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;
import org.ice.view.AbstractView;
import org.ice.view.ScriptView;

public abstract class HttpModule implements IModule {

	protected String content;
	protected AbstractView view;
	private HttpRequest request;
	private HttpResponse response;
	private String template;
	
	public HttpModule()	{
		content = "";
		view = new ScriptView();
	}
	
	public String getParam(String param) {
		return request.getParam(param);
	}
	
	public String getParam(int index) {
		return request.getParam(index);
	}
	
	public String getParam(String param, String defaultValue) {
		return request.getParam(param, defaultValue);
	}
	
	public void setTemplate(String template)	{
		this.template = template;
	}
	
	public boolean isUsingTemplate()	{
		return (template != null);
	}
	
	public void setHeader(String headerName, String value) {
		response.setHeader(headerName, value);
	}
	
	public String getHeader(String headerName) {
		return response.getHeader(headerName);
	}

	public void setContentType(String contentType)	{
		response.setContentType(contentType);
	}
	
	public void redirect(String url)	{
		response.redirect(url);
	}
	
	public String getBaseUrl()	{
		return request.getBaseUrl();
	}
	
	public String getResourceUrl()	{
		return this.getBaseUrl()+Config.resourceUrl;
	}
	
	public String getRequestIP() {
		return request.getIP();
	}
	
	public void echo(String s)	{
		content += s;
	}
	
	@Override
	public void init() {
	}

	@Override
	public void preDispatch() {
		
	}

	@Override
	public void dispatch(String task) throws Exception {
		if (view != null)	{
			view.setRequest(request);
			view.setResponse(response);
		}
		
		this.preDispatch ();

		try {
			Method method = this.getClass().getMethod(task+"Task", new Class<?>[0]);
			method.invoke(this, new Object[0]);
		} catch(InvocationTargetException ex) {
			Throwable target = ex.getTargetException();
			if (target instanceof Exception)
				throw (Exception)target;
			throw new IceException(ex.getTargetException(), 500);
		} catch (Exception ex)	{
			throw new NotFoundException("Task ["+request.getTaskName()+"] not found for module ["+request.getModuleName()+"]");
		}
		
		this.postDispatch ();
		
		if (isUsingTemplate())	{
			setContentType("text/html");
			view.setTemplate(Config.resourceUrl+template);
			view.render();
		}
	}

	@Override
	public void postDispatch() {
		
	}
	
	@Override
	public void destroy() {
		
	}
	
	@Override
	public String getStreamResponse() {
		return content;
	}

	@Override
	public void setResponse(HttpResponse response) {
		this.response = response;
		this.content = response.getBody();
	}
	
	@Override
	public HttpResponse getResponse() {
		return this.response;
	}
	
	@Override
	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	@Override
	public HttpRequest getRequest() {
		return request;
	}
}
