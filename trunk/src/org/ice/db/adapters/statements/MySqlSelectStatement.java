package org.ice.db.adapters.statements;

import org.ice.db.adapters.Table;

/**
 * Default MySQL SELECT statement builder, shipped along with
 * the default MySQL adapter
 * 
 * @author dungba
 */
public class MySqlSelectStatement implements IStatementSelect {

	private String query;
	private String select;
	private String from;
	private String where;
	private String limit;
	private String order;
	private String group;
	private String having;
	private IStatementSelect union;
	
	public IStatementSelect select(String fields) {
		select = "SELECT "+fields;
		return this;
	}

	public IStatementSelect from(Table... tables) {
		from = "";
		for(Table table: tables) {
			from += "`"+table.table()+"`,";
		}
		if (!from.isEmpty()) {
			from = from.substring(0, from.length()-2);
		}
		from = "FROM "+from;
		return this;
	}

	public IStatementSelect where(String...wheres) {
		where = "";
		for(int i=0;i<wheres.length;i++) {
			if (i < wheres.length-2) {
				where += "( "+wheres[i]+" ) AND ";
			}
		}
		where = "WHERE "+where;
		return this;
	}

	public IStatementSelect limit(String limit) {
		limit = "LIMIT "+where;
		return this;
	}

	public IStatementSelect orderBy(String order) {
		order = "ORDER BY "+order;
		return this;
	}

	public IStatementSelect groupBy(String group) {
		group = "GROUP BY "+group;
		return this;
	}

	public IStatementSelect having(String having) {
		having = "HAVING "+having;
		return this;
	}

	public IStatementSelect union(IStatementSelect select) {
		this.union = select;
		return this;
	}

	public String toString() {
		return query;
	}
}
