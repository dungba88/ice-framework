package org.ice.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.Cookie;

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
			view.setTemplate(getResourceUrl()+template);
			view.render();
		}
	}

	@Override
	public void postDispatch() {
		
	}
	
	public void setTemplate(String template)	{
		this.template = template;
	}
	
	public boolean isUsingTemplate()	{
		return (template != null);
	}

	@Override
	public String getResponse() {
		return content;
	}

	@Override
	public void destroy() {
		
	}
	
	@Override
	public void setResponse(HttpResponse response) {
		this.response = response;
		this.content = response.getBody();
	}
	
	@Override
	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	@Override
	public HttpRequest getRequest() {
		return request;
	}
	
	public void setSession(String name, String value)	{
		request.setSession(name, value);
	}
	
	public Object getSession(String name)	{
		return request.getSession(name);
	}
	
	public void destroySession()	{
		request.destroySession();
	}
	
	public void clearSession(String name)	{
		request.clearSession(name);
	}
	
	public void addCookie(Cookie cookie)	{
		response.addCookie(cookie);
	}
	
	public Cookie[] getCookies()	{
		return request.getCookies();
	}

	@Override
	public void setHeader(String headerName, String value) {
		response.setHeader(headerName, value);
	}

	@Override
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
		return Config.resourceUrl;
	}
	
	public void echo(String s)	{
		content += s;
	}
}
