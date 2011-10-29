package org.ice.registry;

public interface IRegistry {
	
	public void set(String key, Object value);
	
	public Object get(String key);
}
