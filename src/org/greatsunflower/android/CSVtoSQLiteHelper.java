package org.greatsunflower.android;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class CSVtoSQLiteHelper extends SQLiteOpenHelper {
	
	public static final String TABLE_TAXANOMY = "taxanomy";
	public static final String TABLE_PLANTS = "plants";
	public static final String TABLE_POLLINATORS = "pollinators";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TAXA = "taxa";
	public static final String COLUMN_MIDDLE_LEVEL = "middle_level";
	public static final String COLUMN_COMMON_NAME = "common_name";
	public static final String COLUMN_VISITOR_GENUS = "visitor_genus";
	public static final String COLUMN_VISITOR_SPECIES = "visitor_species";
	public static final String COLUMN_PLANT_FAMILY = "plant_family";
	public static final String COLUMN_PLANT_GENUS = "plant_genus";
	public static final String COLUMN_PLANT_SPECIES = "plant_species";
	public static final String COLUMN_VAR_SUBSPECIES = "var_subspecies";
	
	public static final String COLUMN_PLANT_LEVEL0 = "plant_level0";
	public static final String COLUMN_PLANT_LEVEL1 = "plant_level1";
	
	public static final String COLUMN_POLLINATOR_LEVEL0 = "pollinator_level0";
	public static final String COLUMN_POLLINATOR_LEVEL1 = "pollinator_level1";
	public static final String COLUMN_POLLINATOR_LEVEL2 = "pollinator_level2";
	
	private static final String DATABASE_NAME = "taxanomy.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_TAXANOMY + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TAXA + " text not null, " 
			+ COLUMN_MIDDLE_LEVEL + " text not null, " 
			+ COLUMN_COMMON_NAME + " text not null, "
			+ COLUMN_VISITOR_GENUS + " text not null, "
			+ COLUMN_VISITOR_SPECIES + " text not null, "
			+ COLUMN_PLANT_FAMILY + " text not null, "
			+ COLUMN_PLANT_GENUS + " text not null, "
			+ COLUMN_PLANT_SPECIES + " text not null, "
			+ COLUMN_VAR_SUBSPECIES + " text not null);";
	
	private static final String TABLE_CREATE_PLANTS = "create table "
			+ TABLE_PLANTS + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_PLANT_LEVEL0 + " text not null, " 
			+ COLUMN_PLANT_LEVEL1 + " text);";
	
	private static final String TABLE_CREATE_POLLINATORS = "create table "
			+ TABLE_POLLINATORS + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_POLLINATOR_LEVEL0 + " text not null, " 
			+ COLUMN_POLLINATOR_LEVEL1 + " text, " 
			+ COLUMN_POLLINATOR_LEVEL2 + " text);";

	public CSVtoSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		context.deleteDatabase(DATABASE_NAME);
//		Log.d("CSV ACTION", "deleting database");
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(TABLE_CREATE_PLANTS);
		database.execSQL(TABLE_CREATE_POLLINATORS);
		Log.d("CSV ACTION", DATABASE_CREATE);
		Log.d("PLANT ACTION", TABLE_CREATE_PLANTS);
		Log.d("POLLINATOR ACTION", TABLE_CREATE_POLLINATORS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w("CSV ACTION",
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAXANOMY);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_POLLINATORS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANTS);
		onCreate(db);
	}

}
