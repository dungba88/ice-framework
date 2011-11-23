package org.ice.config;

public interface ConfigData {

	public String get(String name);
	
	public void set(String name, String value);
}
