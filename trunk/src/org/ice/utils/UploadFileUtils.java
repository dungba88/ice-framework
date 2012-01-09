package org.ice.utils;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadFileUtils {

	public static FileItem getUploadFile(HttpServletRequest request, String file) throws Exception {
		ServletFileUpload fileUpload = new ServletFileUpload(new DiskFileItemFactory());
		List<FileItem> list = fileUpload.parseRequest(request);
		for(FileItem fileItem: list) {
			if (fileItem.getFieldName().equals(file)) {
				return fileItem;
			}
		}
		return null;
	}
	
	public boolean isMultipart(HttpServletRequest request) {
		return ServletFileUpload.isMultipartContent(request);
	}
}
