package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ObservationsDataSource {

	private static final String TABLE_OBSERVATIONS = null;
	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
			MySQLiteHelper.COLUMN_POLLINATOR, MySQLiteHelper.COLUMN_FLOWER };

	public ObservationsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public SQLiteObservations createObservations(String pollinator) {
		Log.d("SREENI", "CREATE OBSERVATION: Entering create observations method");
		Log.d("SREENI", "POLLINATOR: pollinator name being passed: " + pollinator);
		ContentValues values = new ContentValues();
		Log.d("SREENI", "CONTENT VALUES: New content value object created");
		values.put(MySQLiteHelper.COLUMN_POLLINATOR, pollinator);
		Log.d("SREENI", "SQLITEHELPER: pollinator value successfully put");
		long insertId = database.insert(MySQLiteHelper.TABLE_OBSERVATIONS,
				null, values);
		Log.d("SREENI", "INSERTID: insert id of the value recently inserted: " + insertId );
		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBSERVATIONS,
				allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
				null, null, null);
		Log.d("SREENI", "CURSOR: successfully created cursor to parse" );
		cursor.moveToFirst();
		Log.d("SREENI", "CURSOR: successfully moved to first pointer" );
		SQLiteObservations newObservation = cursorToObservation(cursor);
		Log.d("SREENI", "CURSOR: new observation SQLiteObservations object created" );
		cursor.close();
		Log.d("SREENI", "CURSOR: cursor successfully closed" );
		return newObservation;
	}

	public boolean deleteObservation(int id) {
		//long id = observation.getId();
		//System.out.println("Observation deleted with id: " + id);
		
		if ((database.delete(MySQLiteHelper.TABLE_OBSERVATIONS,MySQLiteHelper.
				COLUMN_ID + " = " + id, null))> 0){
			Log.d("SREENI", "successfully deleted: " + id );
			return true;
		}
		else {
			Log.d("SREENI", "could not delete the id " + id );
			return false;
		}
			
	}
	
	public SQLiteObservations getObservation(int id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_OBSERVATIONS + " WHERE " + MySQLiteHelper.COLUMN_ID + " = " + id;
		Cursor cursor = db.rawQuery(selectQuery, null);
		SQLiteObservations temp = new SQLiteObservations();
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			temp.setId(Integer.parseInt(cursor.getString(0)));
			temp.setPollinator(cursor.getString(1));
			temp.setFlowerName(cursor.getString(2));
			cursor.close();
		} else {
			temp = null;
		}
	        //db.close();
		return temp;
		
	}

	public List<SQLiteObservations> getAllObservations() {
		List<SQLiteObservations> observations = new ArrayList<SQLiteObservations>();

		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBSERVATIONS,
				allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SQLiteObservations observation = cursorToObservation(cursor);
			observations.add(observation);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return observations;
	}

	private SQLiteObservations cursorToObservation(Cursor cursor) {
		SQLiteObservations observation = new SQLiteObservations();
		observation.setId((int)cursor.getLong(0));
		observation.setPollinator(cursor.getString(1));
		return observation;
	}
}