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
package org.ice.config;

/**
 * An interface for setting up environment based
 * on configuration meta-data, which is typically 
 * defined in <code>ice.xml</code>
 * Developers or third parties can provide implementations 
 * depending on their needs and register them using the 
 * <code>setup-class</code> element in <code>ice.xml</code>
 * @author dungba
 *
 */
public interface ConfigSetup {

	/**
	 * Setup environment based on configuration meta-data
	 * Implementation can use information retrieved from
	 * <code>data</code> to configure other resources
	 * E.g: database setup, email setup, etc.
	 * @param data object holds the configuration meta-data
	 * @throws Exception
	 */
	public void setup(IConfigData data) throws Exception;
}
