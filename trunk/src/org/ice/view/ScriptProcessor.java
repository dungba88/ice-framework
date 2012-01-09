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
package org.ice.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import org.ice.Config;

public class ScriptProcessor {

	public String process(String template, Map<String, Object> map) {
		InputStream is = Config.servletContext.getResourceAsStream(template);
		if (is == null)	{
			return "Template not found: "+template;
		}
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String text = "";
			while((text = br.readLine())!=null)	{
				builder.append(text);
				builder.append("\n");
			}
			Set<String> names = map.keySet();
			text = builder.toString();
			for(String key: names) {
				Object value = map.get(key);
				try {
					text = text.replaceAll("\\{"+key+"\\}", value.toString());
				} catch (Exception ex)	{
//					Logger.getLogger().log("Error while processing template: "+ex.toString()+" - Current key: "+key, Logger.LEVEL_WARNING);
				}
			}
			return text;
		} catch (Exception ex)	{
			return "Failed to read template: "+ex.toString();
		}
	}
}
