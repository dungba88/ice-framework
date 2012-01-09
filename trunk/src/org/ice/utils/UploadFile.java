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
package org.ice.utils;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.fileupload.FileItem;
import org.ice.Config;
import org.ice.utils.validate.file.FileValidator;

public class UploadFile {

	private FileItem fileItem;
	private boolean overrideAllowed = false;
	private String path;
	private String name;
	private ArrayList<FileValidator> validators;
	
	public UploadFile(FileItem fileItem) {
		this.validators = new ArrayList<FileValidator>();
		this.fileItem = fileItem;
	}
	
	public FileItem getFileItem() {
		return fileItem;
	}
	
	public void preparePath(String base, String name) {
		this.name = name;
		this.path = base + "/" + name;
	}
	
	public void addValidator(FileValidator validator)	{
		validators.add(validator);
	}
	
	public void upload() throws Exception {
		for(FileValidator validator: validators)	{
			if (!validator.validate(this))	{
				throw new Exception(validator.getClass().getName());
			}
		}
		File uploadedFile = getFile();
		if (uploadedFile.exists() && !isOverrideAllowed())
			throw new Exception("File existed");
		fileItem.write(uploadedFile);
	}
	
	public void allowOverride(boolean b)	{
		this.overrideAllowed = b;
	}
	
	public boolean isOverrideAllowed() {
		return this.overrideAllowed;
	}
	
	public File getFile() throws Exception {
		return new File(Config.get("basePath").toString() + Config.get("resourceUrl").toString() + path);
	}
	
	public String getFullPath() {
		return path;
	}
	
	public String getName() {
		return name;
	}
}
