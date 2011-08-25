package model;

public class Answer extends Base {
	
	public long id;
	public long userId;
	public long questionId;
	public String content;
	public int vote;
	public int downVote;

	public Answer()	{
		super();
		this.table = "answers";
		this.key = "id";
	}
}
