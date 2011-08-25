package module;

import model.QuestionCatch;

import org.ice.db.Result;

public class CatchwordModule extends BaseAjaxModule {

	public void browseCatchWordTask() throws Exception {
		result = new Result(true, null, new QuestionCatch().browseCatchWord());
	}
}
