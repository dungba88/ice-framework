package org.ice.config.setup;

import org.ice.config.ConfigData;
import org.ice.config.ConfigSetup;
import org.ice.db.AdapterFactory;
import org.ice.logger.Logger;

public class DatabaseConfigSetup implements ConfigSetup {

	@Override
	public void setup(ConfigData data) throws Exception {
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
