package org.ice.db.adapters.statements;

import org.ice.db.adapters.Table;

/**
 * A statement interface for constructing a SELECT statement.
 * This interface is typically used for complex statements
 * that cannot be handled by <code>IAdapter.select</code>
 * method. The query must also conform to the Ice Query Syntax
 * <code>IStatementSelect</code> is usually provided by the corresponding
 * <code>IAdapter</code> providers via the <code>getSelectStatement</code>
 * method
 * 
 * @author dungba
 */
public interface IStatementSelect {
	
	/**
	 * Appends SELECT part to the query
	 * @param fields the fields to be included
	 * @return the statement itself
	 */
	public IStatementSelect select(String fields);
	
	/**
	 * Appends FROM part to the query
	 * @param tables list of <code>Table</code> objects that
	 * participates in the query
	 * @return the statement itself
	 */
	public IStatementSelect from(Table...tables);

	/**
	 * Appends WHERE part to the query
	 * @param where the WHERE clause
	 * @return the statement itself
	 */
	public IStatementSelect where(String...wheres);
	
	/**
	 * Appends LIMIT part to the query
	 * @param limit the LIMIT clause
	 * @return the statement itself
	 */
	public IStatementSelect limit(String limit);
	
	/**
	 * Appends ORDER BY part to the query
	 * @param order the ORDER BY clause
	 * @return the statement itself
	 */
	public IStatementSelect orderBy(String order);

	/**
	 * Appends GROUP BY part to the query
	 * @param group the GROUP BY clause
	 * @return the statement itself
	 */
	public IStatementSelect groupBy(String group);
	
	/**
	 * Appends HAVING part to the query
	 * @param having the HAVING clause
	 * @return the statement itself
	 */
	public IStatementSelect having(String having);
	
	/**
	 * Appends UNION part to the query
	 * @param select another SELECT statement to be unioned
	 * @return the statement itself
	 */
	public IStatementSelect union(IStatementSelect select);
}
