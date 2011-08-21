package model;

import java.util.ArrayList;
import java.util.Map;

public class CatchWord extends Base {

	private static Map<Long, CatchWord> catchwords;
	
	public long id;
	public long userId;
	public long targetId;
	public long targetNextId;
	public String title;
	public String content;
	public int anonymous;
	public int vote;

	public CatchWord()	{
		super();
		this.table = "catchwords";
		this.key = "id";
	}
	
	public Map<Long, CatchWord> getAllCatchWords() throws Exception {
		if (catchwords != null)
			return catchwords;
		ArrayList<CatchWord> cws = this.select(null);
		for(CatchWord cw: cws) {
			catchwords.put(cw.id, cw);
		}
		return catchwords;
	}
}
