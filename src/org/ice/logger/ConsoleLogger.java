package org.ice.logger;

public class ConsoleLogger extends AbstractLogger {

	@Override
	protected void doLog(String msg, int level) {
		switch(level)	{
		case Logger.LEVEL_DEBUG: 
			System.out.println("[DEBUG] "+msg);
			break;
		case Logger.LEVEL_NOTICE: 
			System.out.println("[NOTICE] "+msg);
			break;
		case Logger.LEVEL_WARNING: 
			System.out.println("[WARN] "+msg);
			break;
		case Logger.LEVEL_ERROR: 
			System.out.println("[ERROR] "+msg);
			break;
		case Logger.LEVEL_FATAL: 
			System.out.println("[FATAL] "+msg);
			break;
		default:
			System.out.println(msg);
		}
	}

}
