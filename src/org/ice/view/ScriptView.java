package org.ice.view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ice.logger.Logger;

public class ScriptView extends AbstractView {
	
	public ScriptView()	{
		
	}
	
	public ScriptView(HttpServletRequest request, HttpServletResponse response)	{
		super(request, response);
	}

	@Override
	public void render() {
		RequestDispatcher dispatcher = request.getRequestDispatcher(template);
		if (dispatcher != null)	{
			try {
				dispatcher.include(request, response);
			} catch (Exception ex)	{
				Logger.getLogger().log("Template throws Exception: "+ex.toString(), Logger.LEVEL_WARNING);
			}
		} else {
			Logger.getLogger().log("Template not found: "+template, Logger.LEVEL_WARNING);
		}
	}
}
