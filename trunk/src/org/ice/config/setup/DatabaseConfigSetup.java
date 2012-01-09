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

import org.ice.config.IConfigData;
import org.ice.config.ConfigSetup;
import org.ice.db.AdapterFactory;
import org.ice.logger.Logger;

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
 * 
 * @author dungba
 * @see ConfigSetup
 * @see AdapterFactory
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
		try {
			AdapterFactory.setupAdapter(adapter, host, port, username,
					password, db);
		} catch (Exception ex) {
			Logger.getLogger().log(ex.toString(), Logger.LEVEL_FATAL);
		}
	}

}
