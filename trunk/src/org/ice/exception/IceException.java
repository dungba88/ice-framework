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
