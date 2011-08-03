package org.ice;

import javax.servlet.ServletContext;

import org.ice.db.AdapterFactory;
import org.ice.logger.Logger;
import org.ice.module.IErrorHandler;

public class Config {
	
	public static boolean debugMode;
	public static IErrorHandler errorHandler;
	public static String version = "1.0";
	public static String resourceUrl;
	public static ServletContext servletContext;
	
	public static void load(ServletContext sc)	{
		servletContext = sc;
		
		//Application environment
		debugMode = false;
		String appEnv = sc.getInitParameter("ice.app.env");
		if (appEnv != null && appEnv.equals("development"))	{
			debugMode = true;
		}
		String handler = sc.getInitParameter("ice.app.errorhandler");
		if (handler != null)	{
			try {
				Class<?> c = Class.forName(handler);
				Object obj = c.newInstance();
				if (obj instanceof IErrorHandler)	{
					errorHandler = (IErrorHandler) obj;
				} else {
					Logger.getLogger().log("Invalid error handler: "+handler, Logger.LEVEL_WARNING);
				}
			} catch (Exception ex)	{
				Logger.getLogger().log("Error handler not found: "+handler, Logger.LEVEL_WARNING);
			}
		}
		
		//path
		resourceUrl = sc.getInitParameter("ice.path.resource");
		if (resourceUrl == null)
			resourceUrl = "resource";
		
		//Database
		String host = sc.getInitParameter("ice.db.host");
		String port = sc.getInitParameter("ice.db.port");
		String db = sc.getInitParameter("ice.db.name");
		String username = sc.getInitParameter("ice.db.username");
		String password = sc.getInitParameter("ice.db.password");
		String adapter = sc.getInitParameter("ice.db.adapter");
		try {
			AdapterFactory.setupAdapter(adapter, host, port, username, password, db);
		} catch (Exception ex)	{
			Logger.getLogger().log(ex.toString(), Logger.LEVEL_FATAL);
		}
	}
}
