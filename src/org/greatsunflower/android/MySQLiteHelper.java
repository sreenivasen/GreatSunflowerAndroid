package org.greatsunflower.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_POLLINATORS = "pollinators";
	public static final String TABLE_SESSIONS = "sessions";
	public static final String TABLE_IMAGES = "images";
	public static final String TABLE_PLANTS = "plants";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_POLLINATOR = "pollinator";
	public static final String COLUMN_MIDDLE_LEVEL = "middle_level";
	public static final String COLUMN_COMMON_NAME = "common_name";
	public static final String COLUMN_VISITOR_GENUS = "visitor_genus";
	public static final String COLUMN_VISITOR_SPECIES = "visitor_species";
	public static final String COLUMN_IMAGE = "imagepath";
	public static final String COLUMN_SESSION = "session";
	public static final String COLUMN_DATETIME = "createdDateTime";
	public static final String COLUMN_COUNT = "pollinator_count";
	
	public static final String COLUMN_PLANT_FAMILY = "plant_family";
	public static final String COLUMN_PLANT_GENUS = "plant_genus";
	public static final String COLUMN_PLANT_SPECIES = "plant_species";
	public static final String COLUMN_PLANT_VAR_SUBSPECIES = "plant_var_subspecies";
	
	
	public static final String COLUMN_OBSERVATION = "observation_type";
	public static final String COLUMN_SESSION_ID = "session_id";
	public static final String COLUMN_SESSION_START = "session_startDtTime";
	public static final String COLUMN_SESSION_END = "session_endDtTime";
	public static final String COLUMN_SUBMISSION = "is_submitted";
	
	public static final String COLUMN_IMAGE_DTTIME = "image_dtTime";
	public static final String COLUMN_POLLINATOR_ID = "pollinator_id";
	public static final String COLUMN_LAT = "latitude";
	public static final String COLUMN_LONG = "longitude";

	private static final String DATABASE_NAME = "observations.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String CREATE_TABLE_POLLINATORS = "create table "
			+ TABLE_POLLINATORS + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_POLLINATOR + " text not null, " 
			+ COLUMN_MIDDLE_LEVEL + " text, " 
			+ COLUMN_COMMON_NAME + " text, "
			+ COLUMN_VISITOR_GENUS + " text, "
			+ COLUMN_VISITOR_SPECIES + " text, "
			+ COLUMN_SESSION + " integer, "
			+ COLUMN_COUNT + " integer, "
			+ COLUMN_DATETIME + " datetime);";
	
	private static final String CREATE_TABLE_PLANTS = "create table "
			+ TABLE_PLANTS + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_PLANT_FAMILY + " text not null, " 
			+ COLUMN_PLANT_GENUS + " text, " 
			+ COLUMN_PLANT_SPECIES + " text, "
			+ COLUMN_PLANT_VAR_SUBSPECIES + " text, "
			+ COLUMN_SESSION + " integer, "
			+ COLUMN_DATETIME + " datetime);";
	
	private static final String CREATE_TABLE_SESSION = "create table "
			+ TABLE_SESSIONS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SESSION_ID + " integer, "
			+ COLUMN_OBSERVATION + " text not null, "
			+ COLUMN_SUBMISSION + " integer, "
			+ COLUMN_SESSION_START + " datetime,"
			+ COLUMN_SESSION_END + " datetime);";
	
	private static final String CREATE_TABLE_IMAGES = "create table "
			+ TABLE_IMAGES + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SESSION_ID + " integer, "
			+ COLUMN_POLLINATOR_ID + " integer, "
			+ COLUMN_LAT + " text, "
			+ COLUMN_LONG + " text, "
			+ COLUMN_IMAGE + " text not null, "
			+ COLUMN_IMAGE_DTTIME + " datetime);";

	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		context.deleteDatabase(DATABASE_NAME);
//		Log.d("MYSQLITEHELPER", "deleting database");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_TABLE_POLLINATORS);
		database.execSQL(CREATE_TABLE_SESSION);
		database.execSQL(CREATE_TABLE_IMAGES);
		database.execSQL(CREATE_TABLE_PLANTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POLLINATORS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANTS);
		onCreate(db);
	}

}