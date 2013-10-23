package org.greatsunflower.database;


import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ObservationTable {

  // Database table
  public static final String TABLE_OBSERVATION = "observation";
  public static final String COLUMN_ID = "_id";
  public static final String COLUMN_EMAIL = "emailid";
  public static final String COLUMN_CATEGORY = "category";
  public static final String COLUMN_IMAGE = "image";
  public static final String COLUMN_THUMBNAIL = "thumbnail";
  public static final String COLUMN_PLANT = "plant";
  public static final String COLUMN_POLLINATOR = "pollinator";
  public static final String COLUMN_DESCRIPTION = "description";

  // Database creation SQL statement
  private static final String DATABASE_CREATE = "create table " 
      + TABLE_OBSERVATION
      + "(" 
      + COLUMN_ID + " integer primary key autoincrement, " 
      + COLUMN_EMAIL + " text not null, "
      + COLUMN_CATEGORY + " text not null, " 
      + COLUMN_IMAGE + " text not null, "
      + COLUMN_THUMBNAIL + " text not null, "
      + COLUMN_PLANT + " text not null,"
      + COLUMN_POLLINATOR + " text not null, "
      + COLUMN_DESCRIPTION + " text not null" 
      + ");";

  public static void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  public static void onUpgrade(SQLiteDatabase database, int oldVersion,
      int newVersion) {
    Log.w(ObservationTable.class.getName(), "Upgrading database from version "
        + oldVersion + " to " + newVersion
        + ", which will destroy all old data");
    database.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION);
    onCreate(database);
  }
} 
