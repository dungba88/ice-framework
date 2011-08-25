package model;

import java.util.ArrayList;

import org.ice.db.Table;

public class Base extends Table {

	public long totalRows;
	
	public long countTotal() throws Exception {
		ArrayList<? extends Base> list = this.select(null, "COUNT("+key+") AS totalRows", null, null);
		return list.get(0).totalRows;
	}
}
