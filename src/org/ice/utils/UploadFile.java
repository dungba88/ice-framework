package org.ice.utils;

import java.io.File;

import org.apache.tomcat.util.http.fileupload.FileItem;
import org.ice.Config;

public class UploadFile {

	private FileItem fileItem;
	private boolean overrideAllowed;
	private String path;
	
	public UploadFile(FileItem fileItem) {
		this.fileItem = fileItem;
	}
	
	public FileItem getFileItem() {
		return fileItem;
	}
	
	public void preparePath(String base, String name) {
		this.path = base + "/" + name;
	}
	
	public void upload() throws Exception {
		this.upload(this.path);
	}
	
	public void upload(String path) throws Exception {
		this.path = path;
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
	
	public File getFile() {
		return new File(Config.resourceUrl + "/" + path);
	}
	
	public String getPath() {
		return path;
	}
}
