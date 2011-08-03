package org.ice.logger;

public class Logger {
	public static final int LEVEL_NOTICE = 1;
	public static final int LEVEL_WARNING = 2;
	public static final int LEVEL_ERROR = 3;
	public static final int LEVEL_FATAL = 4;
	
	private static ILogger logger;

	public static ILogger getLogger()	{
		if (logger == null)
			logger = new ConsoleLogger();
		return logger;
	}
}
