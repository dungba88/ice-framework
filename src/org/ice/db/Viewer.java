package org.ice.db;

import java.util.HashMap;
import java.util.Map;

import org.ice.utils.FieldUtils;

public class Viewer {
	
	protected Map<String, Object> map;
	protected Object obj;
	protected String fields;

	public Viewer(Object obj, String fields)	{
		this.obj = obj;
		this.fields = fields;
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
