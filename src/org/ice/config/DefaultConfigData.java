package org.ice.config;

import java.util.Hashtable;

public class DefaultConfigData implements ConfigData {
	
	private Hashtable<String, String> hash = new Hashtable<String, String>();

	@Override
	public String get(String name) {
		return hash.get(name);
	}

	@Override
	public void set(String name, String value) {
		hash.put(name, value);
	}

}
