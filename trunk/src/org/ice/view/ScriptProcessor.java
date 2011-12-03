package org.ice.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import org.ice.Config;
import org.ice.logger.Logger;

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
					Logger.getLogger().log("Error while processing template: "+ex.toString()+" - Current key: "+key, Logger.LEVEL_WARNING);
				}
			}
			return text;
		} catch (Exception ex)	{
			return "Failed to read template: "+ex.toString();
		}
	}
}
