package model;

public class Question extends Base {
	
	public long id;
	public long userId;
	public long targetId;
	public long targetNextId;
	public String title;
	public String content;
	public int anonymous;
	public int vote;

	public Question()	{
		super();
		this.table = "questions";
		this.key = "id";
	}
}
