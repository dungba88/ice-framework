/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package org.ice.db.adapters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.ice.config.setup.DatabaseConfigSetup;
import org.ice.db.adapters.statements.IStatementSelect;


/**
 * An interface for all database adapters. It is a <i>high
 * level adapter</i> which means it is primarily used for 
 * interfacing between the application's model and the JDBC
 * driver. With the adapter interface and implementations,
 * developers can now interact with database of their
 * choices and do not have to change the data access layer
 * code (or at a very small degree) when the underlying database
 * changed (e.g: when migrate to MySQL from MS SQL Server)
 * If at any point, developers want to access the underlying
 * database's connection (e.g: to make use of a specific feature
 * that is included in their databases' vendors only), they
 * can use the <code>getConnection</code> method.
 * Currently, the adapter is <code>singleton</code>
 * 
 * @author dungba
 */
public interface IAdapter {
	
	/**
	 * Get the last thrown error/exception
	 * @return The last error/exception thrown in
	 * the adapter's code
	 */
	public Exception getLastError();

	/**
	 * Get the current connection to the database.
	 * The connection is bound to the adapter, so that
	 * it will also be <code>singleton</code>
	 * @return the current connection.
	 */
	public Connection getConnection();

	/**
	 * Change the connection. This method should be
	 * invoked by the framework only.
	 * @param connection the new connection to the database
	 */
	public void setConnection(Connection connection);

	/**
	 * Change the auto-commit mode, which in turn enables 
	 * or disables the automatic roll-back.
	 * If set to <code>false</code>, whenever a transaction
	 * causes an exception, it will be automatically rolled
	 * back 
	 * @param autoCommit
	 */
	public void setAutoCommit(boolean autoCommit);

	/**
	 * Get the current auto-commit flag
	 * @return the current auto-commit flag
	 * @see IAdapter#setAutoCommit(boolean)
	 */
	public boolean isAutoCommit();

	/**
	 * Get the default driver's class name, e.g: <code>com.mysql.jdbc.Driver</code>
	 * This value will be used if none specified by the application
	 * @return the database default driver's class name
	 * @see DatabaseConfigSetup
	 */
	public String getDriverName();
	
	/**
	 * Construct the database connection string, which is typically
	 * driver-specific, using provided information. 
	 * E.g: <code>jdbc:mysql://localhost/mydb?characterEncoding=UTF8</code>
	 * @param host the database server's host. Required
	 * @param port the database server's port. Optional
	 * @param dbName the database name
	 * @return the connection string, which is then used
	 * for establishing connection to the database
	 */
	public String getConnectionString(String host, String port,
			String dbName);

	/**
	 * Load a row from the database using primary key
	 * and map the result to the desired object.
	 * @param obj the object to be loaded
	 * @return false if there is no row containing the
	 * specified primary key
	 * @throws Exception if any SQLException is thrown
	 */
	public boolean load(Table obj) throws Exception;

	/**
	 * Perform a query to database using Ice Query Syntax
	 * against a <code>Table</code> object
	 * @param obj the object used for evaluating the query
	 * @param query the query in Ice Query Syntax
	 * @return an <code>ArrayList</code> of objects with
	 * the same type of <code>obj</code>
	 * @throws Exception if any SQLException is thrown
	 */
	public ArrayList query(Table obj, String query) throws Exception;

	/**
	 * Perform a SELECT query to database using Ice Query
	 * Syntax
	 * @param obj the object used for evaluating the query
	 * @param where the WHERE clause
	 * @param choice fields to be included in final result
	 * @param order the ORDER BY clause
	 * @param group the GROUP BY clause
	 * @param pageIndex the offset in the LIMIT clause divided
	 * by <code>pageSize</code>
	 * @param pageSize the number of returned rows
	 * @return an <code>ArrayList</code> of objects with
	 * the same type of <code>obj</code>
	 * @throws Exception if any SQLException is thrown
	 */
	public ArrayList select(Table obj, String where, String choice,
			String order, String group, int pageIndex, int pageSize)
			throws Exception;

	/**
	 * Perform an UPDATE query to database using Ice Query
	 * Syntax
	 * @param obj the object used for evaluating the query
	 * @param fields the fields to be updated
	 * @param where the WHERE clause
	 * @return the row count for the UPDATE statement or -1
	 * in case of failure
	 * @throws Exception if any SQLException is thrown
	 */
	public int update(Table obj, String fields, String where)
			throws Exception;

	/**
	 * Perform an INSERT query to database using Ice Query
	 * Syntax
	 * @param obj the object used for evaluating the query
	 * @param fields the fields to be included in the query
	 * @return the row count for the INSERT statement or -1
	 * in case of failure
	 * @throws Exception if any SQLException is thrown
	 */
	public int insert(Table obj, String fields) throws Exception;

	/**
	 * Perform a DELETE query to database using Ice Query
	 * Syntax
	 * @param obj the object used for evaluating the query
	 * @param where the WHERE statement
	 * @return the row count for the DELETE statement or -1
	 * in case of failure
	 * @throws Exception if any SQLException is thrown
	 */
	public int delete(Table obj, String where) throws Exception;

	public ArrayList selectJoin(Table primaryObj, Table foreignObj,
			String foreignKey, String where, String primaryChoice,
			String foreignChoice, String order, String group, int pageIndex,
			int pageSize, Class<? extends Table> returnClass) throws Exception;

	/**
	 * Close the current connection to the database
	 * To retrieve a connection again, you must use
	 * <code>AdapterFactory.setupAdapter()</code>
	 */
	public void close();

	/**
	 * Start a batch process, used for efficiently performing
	 * multiple queries
	 * @throws SQLException
	 */
	public void startBatch() throws SQLException;
	
	/**
	 * Add a raw query to the current batch. The query
	 * will not be pre-processed by Ice
	 * @param sql the raw query to be added
	 * @throws SQLException
	 */
	public void addBatch(String sql) throws SQLException;
	
	/**
	 * Clear the current batch
	 * @throws SQLException
	 */
	public void clearBatch() throws SQLException;
	
	/**
	 * Execute the batch
	 * @throws SQLException
	 */
	public void batchUpdate() throws SQLException;

	/**
	 * Get a <code>IAdapterSelect</code> object used for
	 * constructing complex SELECT statements
	 * @return the <code>IAdapterSelect</code> object
	 */
	public IStatementSelect getSelectStatement();
}
