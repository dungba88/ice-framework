package org.ice.http;

import java.util.regex.Pattern;

import org.ice.utils.StringUtils;

public class HttpRequestParser  {

	public HttpRequest parseRequest(HttpRequest request) {
		//find rewritten parameters
		String mod = null;
		String task = null;
		String baseUrl = request.getBaseUrl();
		StringBuffer requestUrl = request.getRequestURL();
		if (requestUrl.length() > baseUrl.length())	{
			requestUrl.delete(0, baseUrl.length());
			String extra = StringUtils.strip(requestUrl.toString(), "/");
			String[] params = extra.split("\\/");
			String[] p = null;
			if (params.length > 2)	{
				p = new String[params.length-2];
				for(int i=2;i<params.length;i++)	{
					p[i-2] = params[i].trim();
				}
			} else {
				p = new String[0];
			}
			request.setParams(p);
			
			mod = params.length > 0 ? params[0] : null;
			task = params.length > 1 ? params[1] : null;
		}
		
		if (mod == null)	{
			mod = request.getParam("mod");
		}
		if (task == null)
			task = request.getParam("task");
		
		if (mod == null || !this.checkValid(mod))	{
			mod = "index";
		}
		if (task == null || !this.checkValid(task))	{
			task = "index";
		}
		
		request.setModuleName(mod);
		request.setTaskName(task);
		
		return request;
	}
	
	protected boolean checkValid(String param)	{
		if (param.isEmpty()) return false;
		return !Pattern.matches("/[^a-zA-Z\\-]/", param);
	}
}
