package org.ice.config.setup;

import org.ice.Config;
import org.ice.config.ConfigData;
import org.ice.config.ConfigSetup;
import org.ice.db.AdapterFactory;
import org.ice.logger.Logger;
import org.ice.module.IErrorHandler;
import org.ice.registry.RegistryFactory;
import org.ice.service.Mail;
import org.ice.utils.FieldUtils;

public class SystemConfigSetup implements ConfigSetup {

	@Override
	public void setup(ConfigData data) throws Exception {
		//The foremost configuration: Registry
		RegistryFactory.setupRegistry(data.get("ice.app.registry"));
		
		//Application environment
		Config.debugMode = false;
		String appEnv = data.get("ice.app.env");
		if (appEnv != null && appEnv.equals("development"))	{
			Config.debugMode = true;
		}
		RegistryFactory.getRegistry().set("config.debugMode", Config.debugMode);
		
		String handler = data.get("ice.app.errorhandler");
		if (handler != null)	{
			try {
				IErrorHandler errorHandler = (IErrorHandler) FieldUtils.loadClass(handler);
				RegistryFactory.getRegistry().set("config.errorHandler", errorHandler);
			} catch (ClassCastException ex) {
				Logger.getLogger().log("Invalid error handler: "+handler, Logger.LEVEL_WARNING);
			} catch (Exception ex)	{
				Logger.getLogger().log("Error handler not found: "+handler, Logger.LEVEL_WARNING);
			}
		}
		
		//path
		String resourceUrl = data.get("ice.path.resource");
		if (resourceUrl == null)
			resourceUrl = "resource";
		RegistryFactory.getRegistry().set("config.resourceUrl", resourceUrl);
		RegistryFactory.getRegistry().set("config.basePath", Config.servletContext.getRealPath("/"));
		
		//Database
		String host = data.get("ice.db.host");
		String port = data.get("ice.db.port");
		String db = data.get("ice.db.name");
		String username = data.get("ice.db.username");
		String password = data.get("ice.db.password");
		String adapter = data.get("ice.db.adapter");
		try {
			AdapterFactory.setupAdapter(adapter, host, port, username, password, db);
		} catch (Exception ex)	{
			Logger.getLogger().log(ex.toString(), Logger.LEVEL_FATAL);
		}
		
		//email
		boolean useEmail = false;
		String useEmailCfg = data.get("ice.email.enable");
		if (useEmailCfg != null && useEmailCfg.equalsIgnoreCase("true"))	{
			useEmail = true;
		}
		
		if (useEmail)	{
			String emailServer = data.get("ice.email.server");
			String emailPort = data.get("ice.email.port");
			String emailUsername = data.get("ice.email.username");
			String emailPassword = data.get("ice.email.password");
			String useSSL = data.get("ice.email.usessl");
			if (useSSL == null || useSSL.isEmpty())	{
				useSSL = "false";
			}
			Mail mail = new Mail();
			mail.setup(emailServer, emailPort, useSSL, emailUsername, emailPassword);
			RegistryFactory.getRegistry().set("config.mail", mail);
		}
	}

}