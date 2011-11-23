package org.ice;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ice.module.FrontModule;

/**
 * This is the entry-point servlet of every Ice applications
 * It just forwards the request to the FrontModule
 * @author Griever
 *
 */
public class ModuleServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1059690109835686359L;
	private FrontModule frontModule;
	
	public void init() throws UnavailableException	{
		if (!Config.ready)	{
			throw new UnavailableException("The application has failed to setup properly and cannot start. Please check the log for more details.");
		}
		frontModule = new FrontModule();
	}

	public void service(HttpServletRequest request, HttpServletResponse response)	{
		frontModule.dispatch(request, response);
	}
}
