package org.ice.view;

import java.util.Enumeration;
import java.util.HashMap;

import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;

public class ScriptView extends TemplateView {
	
	@Override
	public void render(HttpRequest request, HttpResponse response) {
		ScriptProcessor processor = new ScriptProcessor();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			Object value = request.getAttribute(key);
			map.put(key, value);
		}
		String result = processor.process(template, map);
		response.appendBody(result);
	}
}
