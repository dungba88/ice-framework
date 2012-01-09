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
 * can use the <code>getConnection</code> method
 * 
 * @author dungba
 */
public interface IAdapter {
	
	public Exception getLastError();

	public Connection getConnection();

	public void setConnection(Connection connection);

	public void setAutoCommit(boolean autoCommit);

	public boolean isAutoCommit();

	public  String getDriverName();

	public  boolean load(Table obj) throws Exception;

	public  ArrayList query(Table obj, String query) throws Exception;

	public  ArrayList select(Table obj, String where, String choice,
			String order, String group, int pageIndex, int pageSize)
			throws Exception;

	public  int update(Table obj, String fields, String where)
			throws Exception;

	public  boolean insert(Table obj, String fields) throws Exception;

	public  int delete(Table obj, String where) throws Exception;

	public  ArrayList selectJoin(Table primaryObj, Table foreignObj,
			String foreignKey, String where, String primaryChoice,
			String foreignChoice, String order, String group, int pageIndex,
			int pageSize, Class<? extends Table> returnClass) throws Exception;

	public  String getConnectionString(String host, String port,
			String dbName);

	public void close();

	public void startBatch() throws SQLException;
	
	public void addBatch(String sql) throws SQLException;
	
	public void batchUpdate() throws SQLException;

}
