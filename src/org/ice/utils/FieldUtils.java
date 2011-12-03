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
