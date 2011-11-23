package org.ice.config.parser;

import java.io.StringReader;

import javax.servlet.ServletContext;

import org.ice.config.ConfigData;
import org.ice.config.ConfigParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class XMLConfigParser implements ConfigParser {

	@Override
	public void parse(ServletContext sc, Object source, ConfigData output) throws Exception {
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
		private ConfigData output;
		
		public ConfigContentHandler(ConfigData output) {
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
				this.output.set(attributes.getValue("setup-classes"), oldClass);
			}
		}
	}
}
