package org.ice.validate.file;

import org.ice.utils.UploadFile;

public class FileExtValidator implements FileValidator {
	
	private String ext;
	
	public FileExtValidator(String ext)	{
		this.ext = ext;
	}

	@Override
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
