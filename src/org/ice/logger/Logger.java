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
package org.ice.logger;

public class Logger {
	public static final int LEVEL_DEBUG = 0;
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
	
	public static void setLogger(ILogger logger)	{
		Logger.logger = logger;
	}
}
