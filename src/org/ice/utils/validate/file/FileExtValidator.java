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

public class FileExtValidator implements FileValidator {
	
	private String ext;
	
	public FileExtValidator(String ext)	{
		this.ext = ext;
	}

	public boolean validate(UploadFile file) {
		String[] exts = ext.split(",");
		String name = file.getFileItem().getName().toLowerCase();
		for(String s: exts)	{
			if (name.endsWith("."+s.trim().toLowerCase()))	{
				return true;
			}
		}
		return false;
	}

}
