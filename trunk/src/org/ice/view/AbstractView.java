package org.ice.view;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractView implements View {

	protected Map<String, Object> params;
	
	public AbstractView()	{
		params = new HashMap<String, Object>();
	}
	
	public void setParam(String name, Object value)	{
		params.put(name, value);
	}
	
	public Object getParam(String name)	{
		return params.get(name);
	}
	
	public abstract String render();
}
