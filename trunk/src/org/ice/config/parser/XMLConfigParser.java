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
package org.ice.config.parser;

import java.io.StringReader;

import javax.servlet.ServletContext;

import org.ice.config.IConfigData;
import org.ice.config.IConfigParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Default implementation of <code>IConfigParser</code>
 * which takes an XML file or string as the source.
 * For the source to be treated as file, use must prepend
 * <code>file://</code> to the filename
 * The XML file/string must also conform to the standard Ice's
 * XML-based configuration
 * 
 * @author dungba
 */
public class XMLConfigParser implements IConfigParser {

	public void parse(ServletContext sc, Object source, IConfigData output) throws Exception {
		String s = source.toString();
		InputSource is = null;
		if (s.startsWith("file://")) {
			String file = s.substring(7);
			is = new InputSource(sc.getResourceAsStream(file));
		} else {
			is = new InputSource(new StringReader(s));
		}
		
		XMLReader parser = XMLReaderFactory.createXMLReader();
		parser.setContentHandler(new ConfigContentHandler(output));
		parser.parse(is);
	}
	
	class ConfigContentHandler extends DefaultHandler {
		private IConfigData output;
		
		public ConfigContentHandler(IConfigData output) {
			super();
			this.output = output;
		}
		
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (localName.equals("param")) {
				this.output.set(attributes.getValue("name"), attributes.getValue("value"));
			} else if (localName.equals("setup-class")) {
				String oldClass = output.get("setup-classes");
				if (oldClass == null)
					oldClass = "";
				oldClass += attributes.getValue("class")+",";
				this.output.set("setup-classes", oldClass);
			}
		}
	}
}
