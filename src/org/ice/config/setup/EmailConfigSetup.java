package org.ice.config.setup;

import org.ice.config.ConfigData;
import org.ice.config.ConfigSetup;
import org.ice.registry.RegistryFactory;
import org.ice.service.Mail;

public class EmailConfigSetup implements ConfigSetup {

	@Override
	public void setup(ConfigData data) throws Exception {
		// email
		boolean useEmail = false;
		String useEmailCfg = data.get("ice.email.enable");
		if (useEmailCfg != null && useEmailCfg.equalsIgnoreCase("true")) {
			useEmail = true;
		}

		if (useEmail) {
			String emailServer = data.get("ice.email.server");
			String emailPort = data.get("ice.email.port");
			String emailUsername = data.get("ice.email.username");
			String emailPassword = data.get("ice.email.password");
			String useSSL = data.get("ice.email.usessl");
			if (useSSL == null || useSSL.isEmpty()) {
				useSSL = "false";
			}
			Mail mail = new Mail();
			mail.setup(emailServer, emailPort, useSSL, emailUsername,
					emailPassword);
			RegistryFactory.getRegistry().set("config.mail", mail);
		}
	}

}
