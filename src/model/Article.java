package model;

import java.util.ArrayList;

public class Article extends Base {
	
	public long id;
	public long userId;
	public String title;
	public String summary;
	public String content;
	public int selected;
	public int vote;
	public int type;
	
	public String userName;

	public Article()	{
		super();
		this.table = "articles";
		this.key = "id";
	}
	
	public ArrayList<Article> fetchLatest() throws Exception {
		return this.foreignJoin(new User(), "userId", null, "name AS userName", null, "since DESC", null, 0, 5);
	}

	public ArrayList<Article> fetchMostVoted() throws Exception {
		return this.select("selected = ?selected", "id, userId, title, summary, vote", "vote DESC", null, 0, 5);
	}
}
