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
package org.ice.config;

/**
 * A factory used for creating/retrieving configuration
 * parsers. The produced object is currently not <code>singleton</code>,
 * so each attempt to invoke the <code>getParser</code>
 * method will result in the creation of new object
 * 
 * @author dungba
 */
public class ConfigParserFactory {

	/**
	 * Retrieves the parser object based on its class name.
	 * Note that the produced object is not <code>singleton</code>
	 * @param parserClassName the class name of the parser
	 * @return the parser object
	 */
	public static IConfigParser getParser(String parserClassName) {
		try {
			Class<?> c = Class.forName(parserClassName);
			Object parser = c.newInstance();
			if (parser instanceof IConfigParser)	{
				return (IConfigParser) parser;
			}
		} catch (Exception ex) {
			System.out.println(ex.toString());
		}
		return null;
	}
}
