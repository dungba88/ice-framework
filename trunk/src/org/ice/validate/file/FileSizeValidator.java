package org.ice.validate.file;

import org.ice.utils.UploadFile;

public class FileSizeValidator implements FileValidator {
	
	private long min;
	private long max;
	
	public FileSizeValidator(long min, long max)	{
		this.min = min;
		this.max = max;
	}

	@Override
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
