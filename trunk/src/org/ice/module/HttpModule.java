/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.ice.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.ice.Config;
import org.ice.exception.IceException;
import org.ice.exception.NotFoundException;
import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;
import org.ice.utils.UploadFile;
import org.ice.view.ScriptView;
import org.ice.view.TemplateView;

public abstract class HttpModule implements IModule {

	protected String content;
	protected TemplateView view;
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
	
//	public void setHeader(String headerName, String value) {
//		response.setHeader(headerName, value);
//	}
	
//	public String getHeader(String headerName) {
//		return response.getHeader(headerName);
//	}

//	public void setContentType(String contentType)	{
//		response.setContentType(contentType);
//	}
	
	public void sendRedirect(String url)	{
		try {
			response.sendRedirect(url);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getBaseUrl()	{
		return request.getBaseUrl();
	}
	
	public String getResourceUrl()	{
		return this.getBaseUrl()+Config.get("resourceUrl");
	}
	
//	public String getRequestIP() {
//		return request.getIP();
//	}
	
	public void echo(String s)	{
		content += s;
	}
	
	public void init() {
	}

	public void preDispatch(Method method) throws Exception {
		
	}

	public void dispatch(String task) throws Exception {
		Method method = null;
		try {
			method = this.getClass().getMethod(task+"Task", new Class<?>[0]);
		} catch (Exception ex)	{
			throw new NotFoundException("Task ["+request.getTaskName()+"] not found for module ["+request.getModuleName()+"]");
		}
		this.preDispatch (method);

		try {
			method.invoke(this, new Object[0]);
		} catch(InvocationTargetException ex) {
			Throwable target = ex.getTargetException();
			if (target instanceof Exception)
				throw (Exception)target;
			throw new IceException(ex.getTargetException(), 500);
		} catch (Exception ex)	{
			throw new NotFoundException("Task ["+request.getTaskName()+"] not found for module ["+request.getModuleName()+"]");
		}
		
		this.postDispatch (method);
		
		if (isUsingTemplate())	{
			getResponse().setContentType("text/html;charset=UTF-8");
			view.setTemplate(Config.get("resourceUrl")+template);
			view.render(request, response);
		}
	}

	public void postDispatch(Method method) throws Exception {
		
	}
	
	public void destroy() {
		
	}
	
	public String getStreamResponse() {
		return content;
	}

	public void setResponse(HttpResponse response) {
		this.response = response;
		this.content = response.getBody();
	}
	
	public HttpResponse getResponse() {
		return this.response;
	}
	
	public void setRequest(HttpRequest request) {
		this.request = request;
	}

	public HttpRequest getRequest() {
		return request;
	}
	
//	public UploadFile getUploadFile(String name) throws Exception	{
//		return new UploadFile(request.getUploadFile(name));
//	}
	
	public void setView(TemplateView view) {
		this.view = view;
	}
	
	public TemplateView getView() {
		return view;
	}
}
