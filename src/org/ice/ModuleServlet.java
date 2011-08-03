package org.ice;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ice.module.FrontModule;

public class ModuleServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1059690109835686359L;
	private FrontModule frontModule;
	
	public void init()	{
		frontModule = new FrontModule();
	}

	public void service(HttpServletRequest request, HttpServletResponse response)	{
		frontModule.dispatch(request, response);
	}
}
