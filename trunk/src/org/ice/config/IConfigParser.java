package org.ice.config;

import javax.servlet.ServletContext;

public interface ConfigParser {

	public void parse(ServletContext sc, Object source, ConfigData output) throws Exception;
}
