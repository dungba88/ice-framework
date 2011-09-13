package org.ice.view;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.ice.Config;
import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;

public class ScriptView extends AbstractView {
	
	public ScriptView()	{
		super();
	}
	
	public ScriptView(HttpRequest request, HttpResponse response)	{
		super(request, response);
	}

	@Override
	public void render() {
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
			text = builder.toString();
			Set<String> keys = params.keySet();
			for(String key: keys)	{
				Object value = params.get(key);
				text = text.replaceAll("\\{"+key+"\\}", value.toString());
			}
			response.appendBody(text);
		} catch (Exception ex)	{
			response.appendBody("Failed to read template: "+ex.toString());
		}
	}
}
