package org.ice.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.ice.Config;
import org.ice.logger.Logger;

public class OnlineMonitorListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent hse) {
		Logger.getLogger().log("Session created: "+hse.getSession().getId(), Logger.LEVEL_DEBUG);
		Config.online++;
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent hse) {
		Logger.getLogger().log("Session destroyed: "+hse.getSession().getId(), Logger.LEVEL_DEBUG);
		if (Config.online > 0)
			Config.online--;
	}
}
