package org.greatsunflower.android;

import java.text.SimpleDateFormat;
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


	public ObservationsDataSource(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public String[] getSessionObservationDetails(int sessionId){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String[] sessionDetails = new String[3];
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_SESSIONS + " WHERE " + MySQLiteHelper.COLUMN_SESSION_ID + " = " + sessionId;
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		SQLiteObservations observation = cursorToObservation(cursor);
		sessionDetails[0] = observation.getObservationType();
		sessionDetails[1] = observation.getStartDateTime();
		sessionDetails[2] = observation.getEndDateTime();
		cursor.close();
		return sessionDetails;
	}
	
	public int getPlantsCount(int sessionId){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		int count = 0;
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_PLANTS + " WHERE " + MySQLiteHelper.COLUMN_SESSION + " = " + sessionId;
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		count = cursor.getCount();
		cursor.close();
		return count;
	}
	
	public int[] getPollinatorsCount(int sessionId){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		int[] pollinatorCount = new int[2];
		int count = 0, distinctCount = 0;
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_POLLINATORS + " WHERE " + MySQLiteHelper.COLUMN_SESSION + " = " + sessionId;
		Cursor cursor = db.rawQuery(selectQuery, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			distinctCount++;
			count = count + Integer.valueOf(cursor.getString(cursor.getColumnIndex("pollinator_count")));
			cursor.moveToNext();
		}
		cursor.close();
		pollinatorCount[0] = distinctCount;
		pollinatorCount[1] = count;
		return pollinatorCount;
	}
	
	public int getImageCount(int sessionId){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		int count = 0;
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_IMAGES + " WHERE " + MySQLiteHelper.COLUMN_SESSION_ID + " = " + sessionId;
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			count++;
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return count;
		
	}
	
	public void createImages(int sessionId, int pollinatorId, String latitude, String longitude,String imagePath, String createdDtTime){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_SESSION_ID, sessionId);
		values.put(MySQLiteHelper.COLUMN_POLLINATOR_ID, pollinatorId);
		values.put(MySQLiteHelper.COLUMN_LAT, latitude);
		values.put(MySQLiteHelper.COLUMN_LONG, longitude);
		values.put(MySQLiteHelper.COLUMN_IMAGE, imagePath);
		values.put(MySQLiteHelper.COLUMN_IMAGE_DTTIME, createdDtTime);
		
		long insertId = database.insert(MySQLiteHelper.TABLE_IMAGES,null, values);
	}
	
	public ArrayList<String> getSessionImages(int sessionId){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<String> filePaths = new ArrayList<String>();
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_IMAGES 
				+ " WHERE session_id" + " = " + sessionId
				+ " ORDER BY image_dtTime DESC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.d("GRID VIEW", selectQuery);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			filePaths.add(cursor.getString(cursor.getColumnIndex("imagepath")));
			Log.d("GRID VIEW", cursor.getString(cursor.getColumnIndex("session_id")) );
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return filePaths;
	}
	
	public ArrayList<Integer> getSessionImageIds(int sessionId){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<Integer> imageIds = new ArrayList<Integer>();
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_IMAGES 
				+ " WHERE session_id" + " = " + sessionId
				+ " ORDER BY image_dtTime DESC";
		Cursor cursor = db.rawQuery(selectQuery, null);
		Log.d("GRID VIEW", selectQuery);
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			imageIds.add(Integer.valueOf((cursor.getString(cursor.getColumnIndex("_id")))));
			Log.d("GRID VIEW", cursor.getString(cursor.getColumnIndex("_id")) );
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return imageIds;
	}
	
	public void createSession(int sessionId, String observationType, int isSubmitted, String startDtTime, String endDtTime){
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_SESSION_ID, sessionId);
		values.put(MySQLiteHelper.COLUMN_OBSERVATION, observationType);
		values.put(MySQLiteHelper.COLUMN_SUBMISSION, isSubmitted);
		values.put(MySQLiteHelper.COLUMN_SESSION_START, startDtTime);
		values.put(MySQLiteHelper.COLUMN_SESSION_END, endDtTime);
		
		long insertId = database.insert(MySQLiteHelper.TABLE_SESSIONS,null, values);
	}
	
	public void updateSessionEndDateTime(int sessionId, String endDtTime){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		ContentValues args = new ContentValues();
	    args.put(MySQLiteHelper.COLUMN_SESSION_END, endDtTime);
	    db.update(MySQLiteHelper.TABLE_SESSIONS, args, MySQLiteHelper.COLUMN_SESSION_ID + "=" + sessionId, null);
	}
	
	public void updateSessionSubmissionStatus(int sessionId, int isSubmitted){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		ContentValues args = new ContentValues();
	    args.put(MySQLiteHelper.COLUMN_SUBMISSION, isSubmitted);
	    db.update(MySQLiteHelper.TABLE_SESSIONS, args, MySQLiteHelper.COLUMN_SESSION_ID + "=" + sessionId, null);
	}
	
	public SQLiteObservations createPlants(String s1, String s2, String s3, String s4, int session, String datetime){
		Log.d("CREATE PLANT", "ENTERED METHOD");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(MySQLiteHelper.COLUMN_PLANT_FAMILY, s1);		
		values.put(MySQLiteHelper.COLUMN_PLANT_GENUS, s2);
		values.put(MySQLiteHelper.COLUMN_PLANT_SPECIES, s3);
		values.put(MySQLiteHelper.COLUMN_PLANT_VAR_SUBSPECIES, s4);
		values.put(MySQLiteHelper.COLUMN_SESSION, session);
		values.put(MySQLiteHelper.COLUMN_DATETIME,datetime);
		
		long insertId = db.insert(MySQLiteHelper.TABLE_PLANTS,null, values);
		
		Log.d("SREENI", "INSERTID: insert id of the value recently inserted: " + insertId );
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_PLANTS + " where " + MySQLiteHelper.COLUMN_ID + " = " + insertId;
		Cursor cursor = database.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
			SQLiteObservations observation = cursorToPlant(cursor);

		// make sure to close the cursor
		cursor.close();
		return observation;

	}

	public SQLiteObservations createPollinator(int count, String s1, String s2, String s3, String s4, String s5, int session, String datetime) {
		Log.d("CREATE POLLINATOR", "ENTERED METHOD");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		
		Log.d("CREATE POLLINATOR", "SESSION ID: " + session);
		
		values.put(MySQLiteHelper.COLUMN_POLLINATOR, s1);		
		values.put(MySQLiteHelper.COLUMN_MIDDLE_LEVEL, s2);
		values.put(MySQLiteHelper.COLUMN_COMMON_NAME, s3);
		values.put(MySQLiteHelper.COLUMN_VISITOR_GENUS, s4);
		values.put(MySQLiteHelper.COLUMN_VISITOR_SPECIES, s5);
		values.put(MySQLiteHelper.COLUMN_SESSION, session);
		values.put(MySQLiteHelper.COLUMN_DATETIME,datetime);
		values.put(MySQLiteHelper.COLUMN_COUNT,count);
		
		long insertId = db.insert(MySQLiteHelper.TABLE_POLLINATORS,null, values);
		
		Log.d("SREENI", "INSERTID: insert id of the value recently inserted: " + insertId );
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_POLLINATORS + " where " + MySQLiteHelper.COLUMN_ID + " = " + insertId;
		Cursor cursor = database.rawQuery(selectQuery, null);
		
		cursor.moveToFirst();
			SQLiteObservations observation = cursorToPollinator(cursor);

		// make sure to close the cursor
		cursor.close();
		db.close();
		Log.d("CREATE POLLINATOR", observation.getPollinatorCount() + " COUNT");
		Log.d("CREATE POLLINATOR", "SESSION ID: " + observation.getSessionId());
		return observation;
		

	}
	
	

	public boolean deleteObservation(int id) {
		//long id = observation.getId();
		//System.out.println("Observation deleted with id: " + id);
		
		if ((database.delete(MySQLiteHelper.TABLE_POLLINATORS,MySQLiteHelper.
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
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_POLLINATORS + " WHERE " + MySQLiteHelper.COLUMN_ID + " = " + id;
		Cursor cursor = db.rawQuery(selectQuery, null);
		SQLiteObservations temp = new SQLiteObservations();
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			temp.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
			temp.setPollinator(cursor.getString(cursor.getColumnIndex("pollinator")));
			temp.setFlowerName(cursor.getString(cursor.getColumnIndex("flowername")));
			temp.setImagePath(cursor.getString(cursor.getColumnIndex("imagepath")));
			cursor.close();
		} else {
			temp = null;
		}
	        //db.close();
		return temp;
		
	}
	
	public int[] getSessionObservations(int sessionId){

		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_POLLINATORS + " WHERE " + MySQLiteHelper.COLUMN_SESSION + " = " + sessionId;
		Log.d("ANNOTATION SCREEN", selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor == null)
			Log.d("ANNOTATION SCREEN", "the cursor is null");
		else
			Log.d("ANNOTATION SCREEN", "number of rows returned: " + cursor.getCount());

		int[] observationIds = new int[cursor.getCount()];
		if (cursor .moveToFirst()) {

            for(int j = 0; j < cursor.getCount(); j++ ){
    			Log.d("ANNOTATION SCREEN", "ID: " + cursor.getString(cursor.getColumnIndex("_id")));
  			observationIds[j] = Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id")));
            	 Log.d("ANNOTATION SCREEN","value of J: " + String.valueOf(j));
                cursor.moveToNext();
            }

        }

		else {
			observationIds = null;
		}
		cursor.close();
		return observationIds;
	}
	
	public int getLastSessionId(){
		int session;
		Log.d("CAMERA", "Entering into the getLastSessionId method");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_SESSIONS + " order by session_id desc";
		Log.d("CAMERA", selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if (cursor == null)
			Log.d("CAMERA", "the cursor is null");
		else
			Log.d("CAMERA", "number of rows returned: " + cursor.getCount());
		
		
		Log.d("CAMERA", "After Execution of the query inside the method");
		if (cursor.moveToFirst()) {

			Log.d("CAMERA", "Entering IF condition inside the method");
			if (cursor.getString(cursor.getColumnIndex("session_id")) == null){
				Log.d("CAMERA", "NULL VALUE FOUND for last session");
			 session = 0;
			}
			else{
				Log.d("CAMERA", "NOT NULL VALUE FOUND for last session");
			session = Integer.parseInt(cursor.getString(cursor.getColumnIndex("session_id")));
			}
			Log.d("CAMERA", "found last session number: " + session);
			cursor.close();
			return session ;
		} else {
			Log.d("CAMERA", "Entering ELSE condition inside the method");
			return 0;
		}
		
	}
	
	public List<SQLiteObservations> getAllPollinators(int sessionId) {
		List<SQLiteObservations> observations = new ArrayList<SQLiteObservations>();

//		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBSERVATIONS,
//				allColumns, null, null, null, null, null);
		
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_POLLINATORS + 
				" where session = " + sessionId + " order by " + MySQLiteHelper.COLUMN_ID + " desc";
		Cursor cursor = database.rawQuery(selectQuery, null);
		Log.d("FRAGMENT 1", selectQuery);
		Log.d("FRAGMENT 1", "rows returned: " + cursor.getCount());
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SQLiteObservations observation = cursorToPollinator(cursor);
			observations.add(observation);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return observations;
	}
	
	public List<SQLiteObservations> getAllPlantObservations(int sessionId) {
		List<SQLiteObservations> plantObservations = new ArrayList<SQLiteObservations>();
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_PLANTS + 
				" where session = " + sessionId + " order by " + MySQLiteHelper.COLUMN_ID + " desc";
		Cursor cursor = database.rawQuery(selectQuery, null);
		Log.d("PLANT OBSERVATIONS", selectQuery);
		Log.d("PLANT OBSERVATIONS", "rows returned: " + cursor.getCount());
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SQLiteObservations observation = cursorToPlant(cursor);
			plantObservations.add(observation);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return plantObservations;
	}
	
	public boolean deletePlantObservation(int id){
		if ((database.delete(MySQLiteHelper.TABLE_PLANTS,MySQLiteHelper.
				COLUMN_ID + " = " + id, null))> 0){
			Log.d("SREENI", "successfully deleted: " + id );
			return true;
		}
		else {
			Log.d("SREENI", "could not delete the id " + id );
			return false;
		}
	}
	
	public boolean deletePollinator(int id){
		if ((database.delete(MySQLiteHelper.TABLE_POLLINATORS,MySQLiteHelper.
				COLUMN_ID + " = " + id, null))> 0){
			Log.d("SREENI", "successfully deleted: " + id );
			return true;
		}
		else {
			Log.d("SREENI", "could not delete the id " + id );
			return false;
		}
	}

	public List<SQLiteObservations> getAllObservations() {
		List<SQLiteObservations> observations = new ArrayList<SQLiteObservations>();

//		Cursor cursor = database.query(MySQLiteHelper.TABLE_OBSERVATIONS,
//				allColumns, null, null, null, null, null);
		
		String selectQuery = "SELECT * FROM "  + MySQLiteHelper.TABLE_SESSIONS + " order by " + MySQLiteHelper.COLUMN_ID + " desc";
		Cursor cursor = database.rawQuery(selectQuery, null);
		
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
		observation.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
		observation.setSessionId(cursor.getString(cursor.getColumnIndex("session_id")));
		observation.setObservationType(cursor.getString(cursor.getColumnIndex("observation_type")));
		observation.setStartDateTime(cursor.getString(cursor.getColumnIndex("session_startDtTime")));
		observation.setEndDateTime(cursor.getString(cursor.getColumnIndex("session_endDtTime")));
		observation.setIsSubmitted(cursor.getString(cursor.getColumnIndex("is_submitted")));
		return observation;
	}
	
	private SQLiteObservations cursorToPollinator(Cursor cursor) {
		SQLiteObservations observation = new SQLiteObservations();
		observation.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
		observation.setPollinator(cursor.getString(cursor.getColumnIndex("pollinator")));
		observation.setMiddleLevel(cursor.getString(cursor.getColumnIndex("middle_level")));
		observation.setCommonName(cursor.getString(cursor.getColumnIndex("common_name")));
		observation.setVisitorGenus(cursor.getString(cursor.getColumnIndex("visitor_genus")));
		observation.setVisitorSpecies(cursor.getString(cursor.getColumnIndex("visitor_species")));
		observation.setSessionId(cursor.getString(cursor.getColumnIndex("session")));
		observation.setPollinatorCount(Integer.parseInt(cursor.getString(cursor.getColumnIndex("pollinator_count"))));
		return observation;
	}
	
	private SQLiteObservations cursorToPlant(Cursor cursor) {
		SQLiteObservations observation = new SQLiteObservations();
		observation.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex("_id"))));
		observation.setPlantFamily(cursor.getString(cursor.getColumnIndex("plant_family")));
		observation.setPlantGenus(cursor.getString(cursor.getColumnIndex("plant_genus")));
		observation.setPlantSpecies(cursor.getString(cursor.getColumnIndex("plant_species")));
		observation.setPlantVarSubSpecies(cursor.getString(cursor.getColumnIndex("plant_var_subspecies")));
		observation.setSessionId(cursor.getString(cursor.getColumnIndex("session")));
		return observation;
	}
}