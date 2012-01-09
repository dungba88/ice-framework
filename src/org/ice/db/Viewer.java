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
package org.ice.db;

import java.util.HashMap;
import java.util.Map;

import org.ice.utils.FieldUtils;

public class Viewer {
	
	protected Map<String, Object> map;
	protected Object obj;
	protected String fields;
	
	public Viewer() {
	}

	public Viewer(Object obj, String fields)	{
		this.obj = obj;
		this.fields = fields;
	}
	
	public void put(String key, Object value) {
		if (map == null) map = new HashMap<String, Object>();
		map.put(key, value);
	}
	
	public Object get(String key) {
		if (map.containsKey(key))
			return map.get(key);
		return null;
	}
	
	public Object getMap() {
		return map;
	}
	
	public Object serialize()	{
		map = new HashMap<String, Object>();
		String[] fieldArr = fields.split(",");
		for(String field: fieldArr)	{
			field = field.trim();
			Object value = FieldUtils.getValue(obj, field);
			map.put(field, value);
		}
		return map;
	}
}
