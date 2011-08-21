package module;

import org.ice.db.Result;

public class AjaxModule extends BaseAjaxModule {

	public void indexTask()	{
		result = new Result(true, null, null);
	}
	
	public void checkUserStatusTask()	{
		if (viewer.id == -1)	{
			result = new Result(true, null, false);
		} else {
			result = new Result(true, null, viewer.getInfo());
		}
	}
}
