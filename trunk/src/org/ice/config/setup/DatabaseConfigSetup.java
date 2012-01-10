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
package org.ice.config.setup;

import java.util.logging.Level;

import org.ice.config.IConfigData;
import org.ice.config.ConfigSetup;
import org.ice.db.AdapterFactory;
import org.ice.db.adapters.IAdapter;
import org.ice.utils.LogUtils;

/**
 * Subclass of <code>ConfigSetup</code>, used for setting up 
 * the database. For the class to be effective, applications 
 * must provide the following information in the configuration 
 * meta-data:
 * - <code>ice.db.host</code>: The database server's host, 
 * can be IP or host name
 * - <code>ice.db.port</code>: The database server's port, 
 * leave blank for default port
 * - <code>ice.db.name</code>: The database name
 * - <code>ice.db.username</code>: The database server's
 * username, must have appropriate access
 * - <code>ice.db.password</code>: The password of the
 * user, leave blank if the user does not have a password
 * - <code>ice.db.adapter</code>: The adapter used for accessing 
 * the JDBC layer. The adapter is dependent from the driver
 * - <code>ice.db.driver</code>: The JDBC driver used. If
 * leave blank, Ice framework will use the default value 
 * specified by the adapter by calling the <code>getDriverName</code>
 * method
 * 
 * @author dungba
 * @see ConfigSetup
 * @see AdapterFactory
 * @see IAdapter#getDriverName()
 */
public class DatabaseConfigSetup implements ConfigSetup {

	public void setup(IConfigData data) throws Exception {
		// Database
		String host = data.get("ice.db.host");
		String port = data.get("ice.db.port");
		String db = data.get("ice.db.name");
		String username = data.get("ice.db.username");
		String password = data.get("ice.db.password");
		String adapter = data.get("ice.db.adapter");
		String driver = data.get("ice.db.driver");
		try {
			AdapterFactory.setupAdapter(adapter, driver, host, port, username,
					password, db);
		} catch (Exception ex) {
			LogUtils.log(Level.SEVERE, ex);
		}
	}

}
