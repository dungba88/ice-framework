package module;

import model.Answer;
import model.CatchWord;
import model.Question;
import model.User;

import org.ice.db.Result;
import org.ice.db.Viewer;

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
	
	public void getExpertsTask() throws Exception	{
		User user = new User();
		result = new Result(true, null, user.getExperts());
	}
	
	public void countStatsTask() throws Exception {
		Viewer viewer = new Viewer(null, null);
		viewer.put("user", new User().countTotal());
		viewer.put("question", new Question().countTotal());
		viewer.put("answer", new Answer().countTotal());
		viewer.put("catchword", new CatchWord().getAllCatchWords().size());
		
		result = new Result(true, null, viewer.getMap());
	}
}
