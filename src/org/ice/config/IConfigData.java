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
 * An interface for all classes holding/storing configuration
 * meta-data. It is used internally in the <code>org.ice.config</code>
 * package, with the exception of <code>Config</code>
 * 
 * @author dungba
 */
public interface IConfigData {

	/**
	 * Retrieves a configuration property, with a default 
	 * value
	 * @param name the property's name
	 * @param defaultValue the default value
	 * @return the value associated with the property
	 */
	public String get(String name, String defaultValue);

	/**
	 * Retrieves a configuration property, without a default
	 * value
	 * @param name
	 * @return
	 */
	public String get(String name);

	/**
	 * Stores a configuration property, with a specified
	 * name and value
	 * @param name the property's name
	 * @param value the property's value
	 */
	public void set(String name, String value);
}
