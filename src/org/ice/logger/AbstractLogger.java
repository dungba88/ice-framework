package org.ice.logger;

import org.ice.Config;

public abstract class AbstractLogger implements ILogger {

	public void log(String msg, int level)	{
		if (Config.debugMode)
			doLog(msg, level);
	}

	protected abstract void doLog(String msg, int level);
}
