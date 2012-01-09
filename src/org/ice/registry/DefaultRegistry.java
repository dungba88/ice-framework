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
package org.ice.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultRegistry implements IRegistry {
	
	private Map<String, Object> map = new ConcurrentHashMap<String, Object>();

	public void set(String key, Object value) {
		if (key != null)
			map.put(key, value);
	}

	public Object get(String key) {
		try {
			return map.get(key);
		} catch (Exception ex) {
			return null;
		}
	}

}
