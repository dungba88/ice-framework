package org.ice.view;

import java.util.HashMap;
import java.util.Map;

import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;

public abstract class AbstractView {

	protected String template;
	protected Map<String, Object> params;
	
	public AbstractView()	{
		params = new HashMap<String, Object>();
	}
	
	public void setTemplate(String template)	{
		this.template = template;
	}
	
	public void setParam(String name, Object value)	{
		params.put(name, value);
	}
	
	public Object getParam(String name)	{
		return params.get(name);
	}
	
	public abstract String render();
}
