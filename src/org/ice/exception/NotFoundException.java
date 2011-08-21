package org.ice.exception;


public class NotFoundException extends IceException {

	private static final long serialVersionUID = -721363503976573230L;

	public NotFoundException(String msg) {
		super(msg, 404);
	}

}
