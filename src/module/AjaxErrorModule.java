package module;

import org.ice.db.Result;
import org.ice.module.IErrorHandler;

public class AjaxErrorModule extends BaseAjaxModule implements IErrorHandler {

	private Exception exception;

	public void errorTask()	{
		if (exception instanceof exception.AccessDeniedException)	{
		}
		result = new Result(false, exception.getMessage(), exception.getClass().getCanonicalName());
	}

	@Override
	public Exception getException() {
		return exception;
	}

	@Override
	public void setException(Exception ex) {
		this.exception = ex;
	}
}
