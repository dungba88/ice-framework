package org.ice.config.setup;

import org.ice.Config;
import org.ice.config.ConfigData;
import org.ice.config.ConfigSetup;
import org.ice.logger.Logger;
import org.ice.module.IErrorHandler;
import org.ice.registry.RegistryFactory;
import org.ice.utils.FieldUtils;

public class SystemConfigSetup implements ConfigSetup {

	@Override
	public void setup(ConfigData data) throws Exception {
		// The foremost configuration: Registry
		RegistryFactory.setupRegistry(data.get("ice.app.registry"));

		// Application environment
		Config.debugMode = false;
		String appEnv = data.get("ice.app.env");
		if (appEnv != null && appEnv.equals("development")) {
			Config.debugMode = true;
		}
		RegistryFactory.getRegistry().set("config.debugMode", Config.debugMode);

		String handler = data.get("ice.app.errorhandler");
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