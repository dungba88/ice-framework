package model;

import java.util.ArrayList;
import java.util.Map;

public class QuestionCatch extends Base {
	
	public long id;
	public long questionId;
	public long catchWordId;
	public String catchWord;
	public long totalQuestion;
	
	public QuestionCatch() {
		super();
		this.table = "questioncatch";
		this.key = "id";
	}
	
	public Object browseCatchWord() throws Exception {
		Map<Long, CatchWord> list = new CatchWord().getAllCatchWords();
		ArrayList<QuestionCatch> catches = this.select(null, "catchWordId, COUNT(questionId) AS totalQuestion", "totalQuestion DESC", "catchWordId", 0, 29);
		for(QuestionCatch questionCatch: catches) {
			questionCatch.id = questionCatch.catchWordId;
			if (list.containsKey(questionCatch.catchWordId)) {
				questionCatch.catchWord = list.get(questionCatch.catchWordId).catchWord;
			}
		}
		return this.view(catches, "id, catchWord, totalQuestion");
	}
}
