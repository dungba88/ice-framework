package org.ice.registry;

import java.util.HashMap;
import java.util.Map;

public class DefaultRegistry implements IRegistry {
	
	private Map<String, Object> map = new HashMap<String, Object>();

	@Override
	public void set(String key, Object value) {
		if (key != null)
			map.put(key, value);
	}

	@Override
	public Object get(String key) {
		try {
			return map.get(key);
		} catch (Exception ex) {
			return null;
		}
	}

}
