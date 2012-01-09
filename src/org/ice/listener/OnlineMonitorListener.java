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
package org.ice.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.ice.Config;
import org.ice.logger.Logger;

public class OnlineMonitorListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent hse) {
		Logger.getLogger().log("Session created: "+hse.getSession().getId(), Logger.LEVEL_DEBUG);
		Config.online++;
	}

	public void sessionDestroyed(HttpSessionEvent hse) {
		Logger.getLogger().log("Session destroyed: "+hse.getSession().getId(), Logger.LEVEL_DEBUG);
		if (Config.online > 0)
			Config.online--;
	}
}
