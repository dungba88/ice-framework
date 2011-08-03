package org.ice.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.ice.exception.IceException;

public class AdapterFactory {
	
	private static Adapter adapter;
	
	public static Adapter getAdapter()	{
		return adapter;
	}

	public static Adapter setupAdapter(String name, String host, String port, String username, String password, String dbName) throws IceException	{
		if (adapter != null)
			return adapter;
		try {
			Class<?> c = Class.forName(name);
			Object obj = c.newInstance();
			if (obj instanceof Adapter)	{
				adapter = (Adapter) obj;
				
				try {
					Class.forName(adapter.getDriverName());
				} catch (Exception ex)	{
					throw new IceException("Invalid driver class name: ["+adapter.getDriverName()+"]");
				}
				
				Connection connection = DriverManager.getConnection(adapter.getConnectionString(host, port, dbName), username, password);
				adapter.setConnection(connection);
				return adapter;
			}
		} catch (Exception ex)	{
			throw new IceException(ex.toString());
		}
		throw new IceException("Invalid adapter ["+name+"]");
	}
}
