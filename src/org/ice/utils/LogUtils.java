package org.ice.utils;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ice.Config;

public class LogUtils {

	public static void log(Level level, Exception ex) {
		Logger.getLogger((String) Config.get("logger")).log(level, ex.toString(), ex);
	}
	
	public static void log(Level level, String msg) {
		Logger.getLogger((String) Config.get("logger")).log(level, msg);
	}
}
