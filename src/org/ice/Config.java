package org.ice;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.ice.db.AdapterFactory;
import org.ice.logger.Logger;
import org.ice.module.IErrorHandler;
import org.ice.registry.RegistryFactory;
import org.ice.service.Mail;
import org.ice.utils.FieldUtils;

public class Config {
	
	public static boolean debugMode;
	public static boolean ready = false;
	public static String version = "1.0";
	public static ServletContext servletContext;
	
	public static void load(ServletContext sc)	{
		servletContext = sc;
		
		//The foremost configuration: Registry
		try {
			RegistryFactory.setupRegistry(sc.getInitParameter("ice.app.registry"));
		} catch (Exception ex) {
			return;
		}
		
		//Application environment
		debugMode = false;
		String appEnv = sc.getInitParameter("ice.app.env");
		if (appEnv != null && appEnv.equals("development"))	{
			debugMode = true;
		}
		RegistryFactory.getRegistry().set("config.debugMode", debugMode);
		
		String handler = sc.getInitParameter("ice.app.errorhandler");
		if (handler != null)	{
			try {
				IErrorHandler errorHandler = (IErrorHandler) FieldUtils.loadClass(handler);
				RegistryFactory.getRegistry().set("config.errorHandler", errorHandler);
			} catch (ClassCastException ex) {
				Logger.getLogger().log("Invalid error handler: "+handler, Logger.LEVEL_WARNING);
			} catch (Exception ex)	{
				Logger.getLogger().log("Error handler not found: "+handler, Logger.LEVEL_WARNING);
			}
		}
		
		//path
		String resourceUrl = sc.getInitParameter("ice.path.resource");
		if (resourceUrl == null)
			resourceUrl = "resource";
		RegistryFactory.getRegistry().set("config.resourceUrl", resourceUrl);
		RegistryFactory.getRegistry().set("config.basePath", sc.getRealPath("/"));
		
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
		
		//email
		boolean useEmail = false;
		String useEmailCfg = sc.getInitParameter("ice.email.enable");
		if (useEmailCfg != null && useEmailCfg.equalsIgnoreCase("true"))	{
			useEmail = true;
		}
		
		if (useEmail)	{
			String emailServer = sc.getInitParameter("ice.email.server");
			String emailPort = sc.getInitParameter("ice.email.port");
			String emailUsername = sc.getInitParameter("ice.email.username");
			String emailPassword = sc.getInitParameter("ice.email.password");
			String useSSL = sc.getInitParameter("ice.email.usessl");
			if (useSSL == null || useSSL.isEmpty())	{
				useSSL = "false";
			}
			Mail mail = new Mail();
			mail.setup(emailServer, emailPort, useSSL, emailUsername, emailPassword);
			RegistryFactory.getRegistry().set("config.mail", mail);
		}
		ready = true;
	}

	public static void unload(ServletContext servletContext2) {
		try {
			AdapterFactory.getAdapter().getConnection().close();
			
	        Enumeration<Driver> drivers = DriverManager.getDrivers();
	        while (drivers.hasMoreElements()) {
	            Driver driver = drivers.nextElement();
	            try {
	                DriverManager.deregisterDriver(driver);
	            } catch (Exception ex) {
	            	Logger.getLogger().log(ex.toString(), Logger.LEVEL_WARNING);
	            }

	        }
		} catch (Exception ex)	{
			Logger.getLogger().log(ex.toString(), Logger.LEVEL_WARNING);
		}
	}
	
	public static Object get(String key) {
		return RegistryFactory.getRegistry().get("config."+key);
	}
	
	public static void set(String key, Object value) {
		RegistryFactory.getRegistry().set("config."+key, value);
	}
}
