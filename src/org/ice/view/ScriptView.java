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

import java.util.Enumeration;
import java.util.HashMap;

import org.ice.http.HttpRequest;
import org.ice.http.HttpResponse;

public class ScriptView extends TemplateView {
	
	public void render(HttpRequest request, HttpResponse response) {
		ScriptProcessor processor = new ScriptProcessor();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		Enumeration<String> names = request.getAttributeNames();
		while (names.hasMoreElements()) {
			String key = names.nextElement();
			Object value = request.getAttribute(key);
			map.put(key, value);
		}
		String result = processor.process(template, map);
		response.appendBody(result);
	}
}
