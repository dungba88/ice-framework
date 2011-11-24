package org.ice.view;

import javax.servlet.RequestDispatcher;

import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;
import org.ice.logger.Logger;

public class JspView extends TemplateView {

	@Override
	public void render(HttpRequest request, HttpResponse response) {
		RequestDispatcher rd = request.getRequestDispatcher(template);
		if (rd == null) {
			response.appendBody("Template not found: "+template);
			return;
		}
		try {
			rd.include(request, response.getUnderlyingResponse());
		} catch (Exception ex) {
			Logger.getLogger().log("Error while processing template: "+ex.toString(), Logger.LEVEL_WARNING);
			response.appendBody("Failed to read template: "+ex.toString());
		}
	}

}
