package org.ice.utils;

import java.io.File;
import java.util.ArrayList;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.ice.Config;
import org.ice.registry.RegistryFactory;
import org.ice.validate.file.IFileValidator;

public class UploadFile {

	private FileItem fileItem;
	private boolean overrideAllowed = false;
	private String path;
	private String name;
	private ArrayList<IFileValidator> validators;
	
	public UploadFile(FileItem fileItem) {
		this.validators = new ArrayList<IFileValidator>();
		this.fileItem = fileItem;
	}
	
	public FileItem getFileItem() {
		return fileItem;
	}
	
	public void preparePath(String base, String name) {
		this.name = name;
		this.path = base + "/" + name;
	}
	
	public void addValidator(IFileValidator validator)	{
		validators.add(validator);
	}
	
	public void upload() throws Exception {
		for(IFileValidator validator: validators)	{
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
