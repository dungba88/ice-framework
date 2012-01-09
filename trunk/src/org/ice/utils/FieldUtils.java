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
package org.ice.utils;

import java.lang.reflect.Field;

public class FieldUtils {

	public static Object getValue(Object obj, String fieldName)	{
		try {
			Field field = obj.getClass().getField(fieldName);
			return field.get(obj);
		} catch (Exception ex)	{
			return null;
		}
	}
	
	public static void setValue(Object obj, String fieldName, Object value)	{
		try {
			Field field = obj.getClass().getField(fieldName);
			field.set(obj, value);
		} catch (Exception ex)	{
		}
	}
	
	public static Object loadClass(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Class<?> c = Class.forName(className);
		return c.newInstance();
	}
}
