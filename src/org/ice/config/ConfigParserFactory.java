package org.ice.config;

public class ConfigParserFactory {

	public static ConfigParser getParser(String parserClassName) {
		try {
			Class<?> c = Class.forName(parserClassName);
			Object parser = c.newInstance();
			if (parser instanceof ConfigParser)	{
				return (ConfigParser) parser;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return null;
	}
}
