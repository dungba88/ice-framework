package org.ice.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;

import javax.servlet.ServletRequest;

import org.ice.Config;
import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;
import org.ice.logger.Logger;

public class ScriptView extends TemplateView {
	
	@Override
	public void render(HttpRequest request, HttpResponse response) {
		InputStream is = Config.servletContext.getResourceAsStream(template);
		if (is == null)	{
			response.appendBody("Template not found: "+template);
			return;
		}
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String text = "";
			while((text = br.readLine())!=null)	{
				builder.append(text);
				builder.append("\n");
			}
			Enumeration<String> names = request.getAttributeNames();
			text = builder.toString();
			while (names.hasMoreElements())	{
				String key = names.nextElement();
				Object value = request.getAttribute(key);
				try {
					text = text.replaceAll("\\{"+key+"\\}", value.toString());
				} catch (Exception ex)	{
					Logger.getLogger().log("Error while processing template: "+ex.toString()+" - Current key: "+key, Logger.LEVEL_WARNING);
				}
			}
			response.appendBody(text);
		} catch (Exception ex)	{
			response.appendBody("Failed to read template: "+ex.toString());
		}
	}
}
