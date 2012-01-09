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
package org.ice.utils.validate.file;

import org.ice.utils.UploadFile;

public class FileSizeValidator implements FileValidator {
	
	private long min;
	private long max;
	
	public FileSizeValidator(long min, long max)	{
		this.min = min;
		this.max = max;
	}

	public boolean validate(UploadFile file) {
		long size = file.getFileItem().getSize();
		if (min != -1 && size < min) {
			return false;
		}
		if (max != -1 && size > max)
			return false;
		return true;
	}

}
