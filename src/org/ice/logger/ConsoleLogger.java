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
