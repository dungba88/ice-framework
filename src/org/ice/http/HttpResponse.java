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

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class HttpResponse extends HttpServletResponseWrapper  {

	protected HttpServletResponse response;
	protected String body;
	protected Exception exception;
	protected boolean showException;
	
	public HttpResponse(HttpServletResponse response) {
		super(response);
		this.body = "";
		this.showException = true;
	}
	
	public void setException(Exception ex)	{
		this.exception = ex;
	}
	
	public Exception getException()	{
		return exception;
	}
	
	public void appendBody(String body)	{
		this.body += body;
	}
	
	public void outputBody()	{
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.print(body);
		} catch (Exception ex)	{
			
		} finally {
			if (writer != null)
				writer.close();
		}
	}
	
	public void sendResponse()	{
		response.setCharacterEncoding("UTF-8");
		if (showException && exception != null)	{
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
				writer.print(exception.toString());
			} catch (Exception ex)	{
				
			} finally {
				if (writer != null)
					writer.close();
			}
			return;
		}
		outputBody();
	}
	
	public void clearContent() {
		body = "";
		exception = null;
	}

	public String getBody() {
		return body;
	}
	
}
