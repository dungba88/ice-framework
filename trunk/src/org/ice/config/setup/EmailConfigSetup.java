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
import org.ice.registry.RegistryFactory;
import org.ice.utils.Mail;

/**
 * Subclass of <code>ConfigSetup</code>, used for setting up 
 * the email transportation. For the class to be effective, 
 * applications must provide the following information in the 
 * configuration meta-data:
 * - <code>ice.email.enable</code>: <b>Must be <code>true</code></b>,
 * otherwise email will not be enabled
 * - <code>ice.email.server</code>: The SMTP server's host
 * - <code>ice.email.port</code>: The SMTP port
 * - <code>ice.email.username</code>: Username required to login
 * the SMTP server, or blank if not required
 * - <code>ice.email.username</code>: Password required to login
 * the SMTP server, or blank if not required
 * - <code>ice.email.usessl</code>: Whether enable SSL or not
 * 
 * @author dungba
 * @see ConfigSetup
 * @see Mail
 */
public class EmailConfigSetup implements ConfigSetup {

	public void setup(IConfigData data) throws Exception {
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
