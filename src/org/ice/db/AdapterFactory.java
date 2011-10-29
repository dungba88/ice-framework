package org.ice.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.ice.exception.IceException;
import org.ice.utils.FieldUtils;

public class AdapterFactory {
	
	private static Adapter adapter;
	
	public static Adapter getAdapter()	{
		return adapter;
	}

	public static Adapter setupAdapter(String name, String host, String port, String username, String password, String dbName) throws IceException	{
		if (adapter != null)
			return adapter;
		try {
			adapter = (Adapter) FieldUtils.loadClass(name);
			try {
				Class.forName(adapter.getDriverName());
			} catch (Exception ex)	{
				throw new IceException("Invalid driver class name: ["+adapter.getDriverName()+"]");
			}
			
			Connection connection = DriverManager.getConnection(adapter.getConnectionString(host, port, dbName), username, password);
			adapter.setConnection(connection);
			return adapter;
		} catch (ClassCastException ex) {
			throw new IceException("Invalid adapter ["+name+"]");
		} catch (Exception ex)	{
			throw new IceException(ex.toString());
		}
	}
}
