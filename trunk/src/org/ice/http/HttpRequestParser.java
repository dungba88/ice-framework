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
package org.ice.http;

import java.util.regex.Pattern;

import org.ice.utils.StringUtils;

public class HttpRequestParser  {

	public HttpRequest parseRequest(HttpRequest request) {
		//find rewritten parameters
		String mod = null;
		String task = null;
		String servletUrl = request.getServletUrl();
		StringBuffer requestUrl = request.getRequestURL();
		if (requestUrl.length() > servletUrl.length())	{
			requestUrl.delete(0, servletUrl.length());
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
