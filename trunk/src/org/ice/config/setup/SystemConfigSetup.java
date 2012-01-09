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

import org.ice.Config;
import org.ice.config.IConfigData;
import org.ice.config.ConfigSetup;
import org.ice.logger.Logger;
import org.ice.module.IErrorHandler;
import org.ice.registry.RegistryFactory;
import org.ice.utils.FieldUtils;

/**
 * Subclass of <code>ConfigSetup</code>, used for setting up 
 * the base environment. This class use the following information 
 * in the configuration meta-data:
 * - <code>ice.app.registry</code>: The registry provider, default 
 * value is <code>org.ice.registry.DefaultRegistry</code>
 * - <code>ice.app.env</code>: The environment, either <code>development</code>
 * or <code>production</code>
 * - <code>ice.app.errorhandler</code>: The application's error handler, 
 * default value is <code>org.ice.module.ErrorHandler</code>
 * 
 * @author dungba
 * @see ConfigSetup
 * @see Config
 */
public class SystemConfigSetup implements ConfigSetup {

	public void setup(IConfigData data) throws Exception {
		// The foremost configuration: Registry
		RegistryFactory.setupRegistry(data.get("ice.app.registry", "org.ice.registry.DefaultRegistry"));

		// Application environment
		Config.debugMode = false;
		String appEnv = data.get("ice.app.env");
		if (appEnv != null && appEnv.equals("development")) {
			Config.debugMode = true;
		}
		RegistryFactory.getRegistry().set("config.debugMode", Config.debugMode);

		String handler = data.get("ice.app.errorhandler", "org.ice.module.ErrorHandler");
		if (handler != null) {
			try {
				IErrorHandler errorHandler = (IErrorHandler) FieldUtils
						.loadClass(handler);
				RegistryFactory.getRegistry().set("config.errorHandler",
						errorHandler);
			} catch (ClassCastException ex) {
				Logger.getLogger().log("Invalid error handler: " + handler,
						Logger.LEVEL_WARNING);
			} catch (Exception ex) {
				Logger.getLogger().log("Error handler not found: " + handler,
						Logger.LEVEL_WARNING);
			}
		}

		// path
		String resourceUrl = data.get("ice.path.resource");
		if (resourceUrl == null)
			resourceUrl = "resource";
		RegistryFactory.getRegistry().set("config.resourceUrl", resourceUrl);
		RegistryFactory.getRegistry().set("config.basePath",
				Config.servletContext.getRealPath("/"));
	}

}