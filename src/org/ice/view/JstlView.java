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
package org.ice.view;

import javax.servlet.RequestDispatcher;

import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;

public class JstlView extends TemplateView {

	public void render(HttpRequest request, HttpResponse response) {
		RequestDispatcher rd = request.getRequestDispatcher(template);
		if (rd == null) {
			response.appendBody("Template not found: "+template);
			return;
		}
		try {
			rd.include(request, response);
		} catch (Exception ex) {
//			Logger.getLogger().log("Error while processing template: "+ex.toString(), Logger.LEVEL_WARNING);
			response.appendBody("Failed to read template: "+ex.toString());
		}
	}

}
