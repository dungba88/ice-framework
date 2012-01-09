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
package org.ice;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.ice.config.IConfigData;
import org.ice.config.IConfigParser;
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
	
	public static void load(ServletContext sc)	{
		servletContext = sc;
		IConfigData data = null;
		
		try {
			String outputClass = sc.getInitParameter("ice.config.output");
			Object obj = FieldUtils.loadClass(outputClass);
			
			IConfigParser parser = ConfigParserFactory.getParser(sc.getInitParameter("ice.config.parser"));
			if (obj instanceof IConfigData) {
				data = (IConfigData) obj;
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
