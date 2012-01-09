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
package org.ice;

import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ice.module.FrontModule;

/**
 * This is the entry-point servlet of every Ice applications
 * It just forwards the request to the FrontModule
 * @author Griever
 *
 */
public class ModuleServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1059690109835686359L;
	private FrontModule frontModule;
	
	public void init() throws UnavailableException	{
		if (!Config.ready)	{
			throw new UnavailableException("The application has failed to setup properly and cannot start. Please check the log for more details.");
		}
		frontModule = new FrontModule();
	}

	public void service(HttpServletRequest request, HttpServletResponse response)	{
		frontModule.dispatch(request, response);
	}
}
