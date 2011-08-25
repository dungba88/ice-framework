package model;

import java.util.ArrayList;

public class PartnerNews extends Base {

	public PartnerNews()	{
		super();
		this.table = "partnernews";
		this.key = "id";
	}

	public ArrayList<PartnerNews> listLatest() throws Exception {
		return this.select(null, null, "since DESC", null, 0, 5);
	}
}
