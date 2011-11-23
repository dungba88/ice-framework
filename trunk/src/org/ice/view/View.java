package org.ice.view;

public interface View {

	public void setParam(String name, Object value);
	
	public Object getParam(String name);
	
	public abstract String render();
}
