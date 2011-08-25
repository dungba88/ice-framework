package module;

import org.ice.module.HttpModule;

public class IndexModule extends HttpModule {
	
	public void init()	{
		setContentType("text/html");
	}
	
	public void indexTask() throws Exception	{
		this.view.setParam("baseUrl", this.getBaseUrl());
		this.view.setParam("resourceUrl", this.getResourceUrl());
		setTemplate("/index.html");
	}
}
