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
package org.ice.db;

import java.sql.Connection;
import java.sql.DriverManager;

import org.ice.db.adapters.AbstractAdapter;
import org.ice.db.adapters.IAdapter;
import org.ice.exception.IceException;
import org.ice.utils.FieldUtils;

/**
 * A factory class used for managing database adapter. 
 * Currently it use the <code>java.sql.DriverManager</code>
 * class for retrieving connection. <code>DataSource</code>
 * or connection pooling supports will be implemented in future
 * version.
 * The adapter is <code>singleton</code>
 * 
 * @author dungba
 */
public class AdapterFactory {

	/* The adapter instance */
	private static IAdapter adapter;
	
	/**
	 * Get the adapter instance
	 * @return the adapter instance
	 */
	public static IAdapter getAdapter()	{
		return adapter;
	}

	/**
	 * Setup an adapter using provided information. After
	 * invoking this method, application can use <code>getAdapter</code>
	 * method to retrieve the adapter
	 * @param name the adapter's class name
	 * @param driver the JDBC driver's class name, or default
	 * to the adapter's driver
	 * @param host the database host
	 * @param port the database port, or null for default
	 * @param username the database username
	 * @param password the password associated with username
	 * @param dbName the database name
	 * @return an instance of the adapter
	 * @throws Exception
	 */
	public static IAdapter setupAdapter(String name, String driver, String host, String port, String username, String password, String dbName) throws Exception	{
		if (adapter != null) {
			adapter.close();
			adapter = null;
		}
		try {
			adapter = (AbstractAdapter) FieldUtils.loadClass(name);
			try {
				if (driver == null)
					driver = adapter.getDriverName();
				Class.forName(driver);
			} catch (Exception ex)	{
				throw new IceException("Invalid driver class name: ["+adapter.getDriverName()+"]");
			}
			
			Connection connection = DriverManager.getConnection(adapter.getConnectionString(host, port, dbName), username, password);
			adapter.setConnection(connection);
			return adapter;
		} catch (ClassCastException ex) {
			throw new Exception("Invalid adapter ["+name+"]");
		} catch (Exception ex)	{
			throw ex;
		}
	}
}
