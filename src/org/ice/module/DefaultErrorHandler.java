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
package org.ice.module;

import org.ice.Config;

public class DefaultErrorHandler extends HttpModule implements IErrorHandler {
	
	private Exception exception;
	
	public Exception getException() {
		return exception;
	}

	public void setException(Exception ex) {
		this.exception = ex;
	}
	
	public void errorTask()	{
		getResponse().setContentType("text/html");
		echo("<h2>Error occurred</h2>");
		echo("<b>Detail:</b> "+exception.toString());
		StackTraceElement[] stacks = exception.getStackTrace();
		echo("<br /><h4>Stack Trace:</h4>");
		echo("<div style='margin: -10px 0 0 50px'>");
		for(StackTraceElement element: stacks)	{
			echo(element.toString()+"<br />");
		}
		echo("</div>");
		echo("<hr /><i>This report is automatically generated by Ice Framework "+Config.version+"</i>");
	}

}
