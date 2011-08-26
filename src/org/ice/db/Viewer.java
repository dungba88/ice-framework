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
