package org.ice.exception;


public class AccessDeniedException extends IceException {

	private static final long serialVersionUID = -721363503976573230L;

	public AccessDeniedException(String msg) {
		super(msg, 403);
	}

}
