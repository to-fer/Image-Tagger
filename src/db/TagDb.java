package db;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class TagDb {
	private SqlJetDb db;

	/*
	 * Represents the primary key name of every table in the database, which
	 * represents the path of a file that has been tagged.
	 */
	private static final String PRIMARY_KEY_NAME = "path";

	public TagDb(String path) {
		File dbFile = new File(path);

        boolean dbFileExists = dbFile.exists();
		if (!dbFileExists) {
			try {
				dbFile.createNewFile();
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}
		}

		try {
			db = SqlJetDb.open(dbFile, true);
            if (!dbFileExists)
			    db.getOptions().setAutovacuum(true);
		} catch (SqlJetException e) {
			e.printStackTrace();
		} finally {
			try {
				db.commit();
			} catch (SqlJetException e) {
				e.printStackTrace();
			}
		}
	}

	public void createTable(String tableName) {
		try {
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			db.createTable("CREATE TABLE " + tableName + " (" + PRIMARY_KEY_NAME + " TEXT NOT NULL PRIMARY KEY)");
		} catch (SqlJetException e) {
            e.printStackTrace();
		} finally {
			try {
				db.commit();
			} catch (SqlJetException e1) {
				e1.printStackTrace();
			}
		}
	}

	public File[] getTableFiles(String tableName) {
		if (!getTableNames().contains(tableName))
			throw new IllegalArgumentException(
					"Table name does not exist in database.");

		Vector<File> tableFiles = new Vector();

		try {
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			ISqlJetTable table = db.getTable(tableName);
            ISqlJetCursor cursor = table.order(table.getPrimaryKeyIndexName());
			do {
				tableFiles.add(new File(cursor.getString(PRIMARY_KEY_NAME)));
			} while (cursor.next());
		} catch (SqlJetException e) {
			e.printStackTrace();
		} finally {
			try {
				db.commit();
			} catch (SqlJetException e) {
				e.printStackTrace();
			}
    }

		return tableFiles.toArray(new File[0]);
	}

	/*
	public boolean tableContainsPath(String tableName, String path) {
		if (!ArrayUtils.contains(TABLE_NAMES, tableName))
			throw new IllegalArgumentException(
					"Table name does not exist in database.");

		boolean success = false;

		try {
			db.beginTransaction(SqlJetTransactionMode.READ_ONLY);
			success = db.getTable(tableName).lookup(PRIMARY_KEY_NAME, path)
					.getString(path) != null;
		} catch (SqlJetException e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				db.commit();
			} catch (SqlJetException e) {
				e.printStackTrace();
			}
		}

		return success;
	}
	*/
	
	public void addPathToTable(String tableName, String path) {
		if (!getTableNames().contains(tableName))
			throw new IllegalArgumentException(
					"Table name does not exist in database.");

		try {
			db.beginTransaction(SqlJetTransactionMode.WRITE);
			db.getTable(tableName).insert(path);
		} catch (SqlJetException e) {
			e.printStackTrace();
		} finally {
			try {
				db.commit();
			} catch (SqlJetException e) {
				e.printStackTrace();
			}
		}
	}

   public Set<String> getTableNames() {
       Set<String> tableNames = null;
       try {
           tableNames = db.getSchema().getTableNames();
       } catch (SqlJetException e) {
           e.printStackTrace();
       }
       return tableNames;
   }

	public void close() {
		try {
			db.close();
		} catch (SqlJetException e) {
			e.printStackTrace();
		}
	}
}
