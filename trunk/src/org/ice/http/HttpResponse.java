package org.ice.http;

import java.io.PrintWriter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class HttpResponse  {

	protected HttpServletResponse response;
	protected String body;
	protected Exception exception;
	protected boolean showException;
	
	public HttpResponse(HttpServletResponse response) {
		super();
		this.response = response;
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
	
	public void setStatus(int status)	{
		response.setStatus(status);
	}
	
	public String getHeader(String header)	{
		return response.getHeader(header);
	}
	
	public void setHeader(String header, String value)	{
		response.setHeader(header, value);
	}
	
	public void addCookie(Cookie cookie)	{
		response.addCookie(cookie);
	}
	
	public void setContentType(String contentType)	{
		response.setContentType(contentType);
	}
	
	public HttpServletResponse getUnderlyingResponse()	{
		return response;
	}
	
	public void redirect(String url)	{
		try {
			response.sendRedirect(url);
		} catch (Exception ex)	{
			setException(ex);
		}
	}

	public void clearContent() {
		body = "";
		exception = null;
	}

	public String getBody() {
		return body;
	}
	
}
