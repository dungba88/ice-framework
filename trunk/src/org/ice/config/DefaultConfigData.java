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

import java.util.Hashtable;

/**
 * Default implementation of <code>IConfigData</code>
 * which makes use of a <code>Hashtable</code> as the
 * storage
 * 
 * @author dungba
 */
public class DefaultConfigData implements IConfigData {
	
	private Hashtable<String, String> hash = new Hashtable<String, String>();

	public String get(String name) {
		return hash.get(name);
	}

	public void set(String name, String value) {
		hash.put(name, value);
	}

	public String get(String name, String defaultValue) {
		String value = hash.get(name);
		if (value == null)
			return defaultValue;
		return value;
	}

}
