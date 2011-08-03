package org.ice.module;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ice.Config;
import org.ice.exception.IceException;
import org.ice.http.HttpRequest;
import org.ice.http.HttpRequestParser;
import org.ice.http.HttpResponse;
import org.ice.module.router.DefaultRouter;
import org.ice.utils.StringUtils;

public class FrontModule {

	protected boolean dispatched;
	protected HttpRequest request;
	protected HttpRequestParser requestParser;
	protected DefaultRouter router;
	
	public FrontModule()	{
		requestParser = new HttpRequestParser();
		router = new DefaultRouter();
	}
	
	/**
	 * Dispatch the request and send the response to client.
	 * @param request
	 * @param response
	 */
	public void dispatch(HttpServletRequest request, HttpServletResponse response)	{
		this.request = requestParser.parseRequest(new HttpRequest(request));
		HttpResponse httpResponse = new HttpResponse(response);
		
		dispatched = true;
		Exception exception = null;
		try {
			IModule module = router.route(this.request);
			exception = dispatchModule(module, httpResponse, this.request.getTaskName());
		} catch (IceException ex)	{
			httpResponse.setException(ex);
			httpResponse.setStatus(ex.status);
			exception = ex;
		}
		
		if (Config.errorHandler != null && exception != null)	{
			Class<?> c = Config.errorHandler.getClass();
			try {
				IErrorHandler handler = (IErrorHandler) c.newInstance();
				handler.setException(exception);
				httpResponse.clearContent();
				dispatchModule(handler, httpResponse, "error");
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
	private Exception dispatchModule(IModule module, HttpResponse httpResponse, String task) {
		try {
			module.setRequest(this.request);
			module.setResponse(httpResponse);
			
			module.init();
			String taskFunction = formatTaskName(task);
			module.dispatch(taskFunction);
			httpResponse.appendBody(module.getResponse());
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
