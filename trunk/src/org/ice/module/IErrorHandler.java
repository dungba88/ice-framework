package org.ice.module;

public interface IErrorHandler extends IModule {

	public Exception getException();
	
	public void setException(Exception ex);
}
