package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CatchWord extends Base {

	private static Map<Long, CatchWord> catchwords;
	
	public long id;
	public String catchWord;
	public long contextId;
	public String avatar;
	public String description;

	public CatchWord()	{
		super();
		this.table = "catchwords";
		this.key = "id";
	}
	
	public Map<Long, CatchWord> getAllCatchWords() throws Exception {
		if (catchwords != null)
			return catchwords;
		ArrayList<CatchWord> cws = this.select(null);
		catchwords = new HashMap<Long, CatchWord>();
		for(CatchWord cw: cws) {
			catchwords.put(cw.id, cw);
		}
		return catchwords;
	}
}
