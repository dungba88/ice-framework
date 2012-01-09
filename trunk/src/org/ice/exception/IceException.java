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
package org.ice.exception;

public class IceException extends Exception {
	
	private static final long serialVersionUID = -990801405440968897L;
	
	public int status;

	public IceException(String exception)	{
		super(exception);
		status = 500;
	}
	
	public IceException(String exception, int status)	{
		super(exception);
		this.status = status;
	}

	public IceException(Throwable targetException, int status) {
		super(targetException);
		this.status = status;
	}
}
