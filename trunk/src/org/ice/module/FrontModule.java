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

import java.util.ArrayList;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ice.Config;
import org.ice.exception.IceException;
import org.ice.exception.NotFoundException;
import org.ice.http.HttpRequest;
import org.ice.http.HttpRequestParser;
import org.ice.http.HttpResponse;
import org.ice.module.router.DefaultRouter;
import org.ice.module.router.IRouter;
import org.ice.utils.LogUtils;
import org.ice.utils.StringUtils;

/**
 * The front module, which receives request from ModuleServlet,
 * parse and dispatch the request to corresponding modules.
 * @author Griever
 *
 */
public class FrontModule {

	protected HttpRequestParser requestParser;
	protected ArrayList<IRouter> routers;
	
	@SuppressWarnings("unchecked")
	public FrontModule()	{
		requestParser = new HttpRequestParser();
		routers = (ArrayList<IRouter>) Config.get("routers");
		if (routers == null || routers.isEmpty()) {
			routers = new ArrayList<IRouter>();
			routers.add(new DefaultRouter());
		}
	}
	
	/**
	 * Dispatch the request and send the response to client.
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void dispatch(HttpServletRequest request, HttpServletResponse response)	{
		try {
			request.setCharacterEncoding("UTF-8");
		} catch(Exception ex) {
			LogUtils.log(Level.WARNING, "Cannot set character encoding to UTF-8");
		}
		HttpRequest httpRequest = requestParser.parseRequest(new HttpRequest(request));
		HttpResponse httpResponse = new HttpResponse(response);
		
		Exception exception = null;
		try {
			boolean found = false;
			for(IRouter router: routers)	{
				IModule module = router.route(httpRequest);
				if (module != null)	{
					found = true;
					exception = dispatchModule(module, httpRequest, httpResponse, httpRequest.getTaskName());
					break;
				}
			}
			if (!found)
				throw new NotFoundException("No routers match for request: "+request.getRequestURL().toString());
		} catch (IceException ex)	{
			httpResponse.setException(ex);
			httpResponse.setStatus(ex.status);
			exception = ex;
		}
		
		if (Config.get("errorHandler") != null && exception != null)	{
			Class<? extends IErrorHandler> c = (Class<? extends IErrorHandler>) Config.get("errorHandler").getClass();
			try {
				IErrorHandler handler = (IErrorHandler) c.newInstance();
				handler.setException(exception);
				httpResponse.clearContent();
				dispatchModule(handler, httpRequest, httpResponse, "error");
			} catch (Exception ex)	{
				httpResponse.setStatus(500);
				httpResponse.setException(ex);
			}
		}
		
		httpResponse.sendResponse();
		httpResponse = null;
	}

	/**
	 * Dispatch request to the specified module
	 * @param module
	 * @param httpResponse
	 * @param task
	 * @return
	 */
	private Exception dispatchModule(IModule module, HttpRequest httpRequest, HttpResponse httpResponse, String task) {
		try {
			module.setRequest(httpRequest);
			module.setResponse(httpResponse);
			
			module.init();
			String taskFunction = formatTaskName(task);
			module.dispatch(taskFunction);
			httpResponse.appendBody(module.getStreamResponse());
			module.destroy();
			module = null;
			return null;
		} catch (IceException ex)	{
			httpResponse.setException(ex);
			httpResponse.setStatus(ex.status);
			return ex;
		} catch (Exception ex)	{
			httpResponse.setException(ex);
			httpResponse.setStatus(500);
			return ex;
		}
	}

	private String formatTaskName(String taskName)	{
		return StringUtils.ucwords(taskName, '-').replaceAll("-", "");
	}
}
