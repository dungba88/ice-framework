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

import javax.servlet.ServletContext;

/**
 * Interface for all configuration parser, which takes a source
 * object (e.g: a file or an key-value object), parse and store
 * result in the output
 * Developers can also provide their own implementation and register
 * them using <code>ice.config.parser</code> parameter in the <code>web.xml</code>
 * 
 * @author dungba
 */
public interface IConfigParser {

	/**
	 * Parses a source object and store the result in the output
	 * @param sc used for accessing the servlet's context 
	 * @param source the source object
	 * @param output the output used for storing parsed information
	 * @throws Exception
	 */
	public void parse(ServletContext sc, Object source, IConfigData output) throws Exception;
}
