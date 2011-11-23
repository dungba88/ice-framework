package org.ice;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.ice.config.ConfigData;
import org.ice.config.ConfigParser;
import org.ice.config.ConfigParserFactory;
import org.ice.config.ConfigSetup;
import org.ice.db.AdapterFactory;
import org.ice.logger.Logger;
import org.ice.registry.RegistryFactory;
import org.ice.utils.FieldUtils;

public class Config {
	
	public static boolean debugMode;
	public static boolean ready = false;
	public static String version = "1.0";
	public static ServletContext servletContext;
	public static volatile long online = 0;
	
	public static void load(ServletContext sc)	{
		servletContext = sc;
		ConfigData data = null;
		
		try {
			String outputClass = sc.getInitParameter("ice.config.output");
			Object obj = FieldUtils.loadClass(outputClass);
			
			ConfigParser parser = ConfigParserFactory.getParser(sc.getInitParameter("ice.config.parser"));
			if (obj instanceof ConfigData) {
				data = (ConfigData) obj;
			}
			parser.parse(sc, sc.getInitParameter("ice.config.source"), data);
		} catch(Exception ex) {
			System.out.println("Error while parsing configuration");
			ex.printStackTrace();
			return;
		}
		
		String setupClasses = data.get("setup-classes");
		String[] setupClass = setupClasses.split(",");
		for(String cls: setupClass) {
			if (!cls.isEmpty())	{
				try {
					Object obj = FieldUtils.loadClass(cls);
					if (obj instanceof ConfigSetup)	{
						ConfigSetup cs = (ConfigSetup) obj;
						cs.setup(data);
					}
				} catch (Exception ex) {
					System.out.println("Error while setting up");
					ex.printStackTrace();
				}
			}
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
