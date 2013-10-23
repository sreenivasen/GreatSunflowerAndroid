package org.greatsunflower.contentprovider;

import java.util.Arrays;
import java.util.HashSet;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import org.greatsunflower.database.*;


public class ObservationsContentProvider extends ContentProvider {

  // database
  private ObservationDatabaseHelper database;

  // Used for the UriMacher
  private static final int OBSERVATIONS = 10;
  private static final int OBSERVATION_ID = 20;

  private static final String AUTHORITY = "org.greatsunflower.contentprovider";

  private static final String BASE_PATH = "observations";
  public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
      + "/" + BASE_PATH);

  public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
      + "/observations";
  public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
      + "/observation";

  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
  static {
    sURIMatcher.addURI(AUTHORITY, BASE_PATH, OBSERVATIONS);
    sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", OBSERVATION_ID);
  }

  @Override
  public boolean onCreate() {
    database = new ObservationDatabaseHelper(getContext());
    return false;
  }

  @Override
  public Cursor query(Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder) {

    // Uisng SQLiteQueryBuilder instead of query() method
    SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

    // Check if the caller has requested a column which does not exists
    checkColumns(projection);

    // Set the table
    queryBuilder.setTables(ObservationTable.TABLE_OBSERVATION);

    int uriType = sURIMatcher.match(uri);
    switch (uriType) {
    case OBSERVATIONS:
      break;
    case OBSERVATION_ID:
      // Adding the ID to the original query
      queryBuilder.appendWhere(ObservationTable.COLUMN_ID + "="
          + uri.getLastPathSegment());
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }

    SQLiteDatabase db = database.getWritableDatabase();
    Cursor cursor = queryBuilder.query(db, projection, selection,
        selectionArgs, null, null, sortOrder);
    // Make sure that potential listeners are getting notified
    cursor.setNotificationUri(getContext().getContentResolver(), uri);

    return cursor;
  }

  @Override
  public String getType(Uri uri) {
    return null;
  }

  @Override
  public Uri insert(Uri uri, ContentValues values) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsDeleted = 0;
    long id = 0;
    switch (uriType) {
    case OBSERVATIONS:
      id = sqlDB.insert(ObservationTable.TABLE_OBSERVATION, null, values);
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return Uri.parse(BASE_PATH + "/" + id);
  }

  @Override
  public int delete(Uri uri, String selection, String[] selectionArgs) {
    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsDeleted = 0;
    switch (uriType) {
    case OBSERVATIONS:
      rowsDeleted = sqlDB.delete(ObservationTable.TABLE_OBSERVATION, selection,
          selectionArgs);
      break;
    case OBSERVATION_ID:
      String id = uri.getLastPathSegment();
      if (TextUtils.isEmpty(selection)) {
        rowsDeleted = sqlDB.delete(ObservationTable.TABLE_OBSERVATION,
            ObservationTable.COLUMN_ID + "=" + id, 
            null);
      } else {
        rowsDeleted = sqlDB.delete(ObservationTable.TABLE_OBSERVATION,
            ObservationTable.COLUMN_ID + "=" + id 
            + " and " + selection,
            selectionArgs);
      }
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsDeleted;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs) {

    int uriType = sURIMatcher.match(uri);
    SQLiteDatabase sqlDB = database.getWritableDatabase();
    int rowsUpdated = 0;
    switch (uriType) {
    case OBSERVATIONS:
      rowsUpdated = sqlDB.update(ObservationTable.TABLE_OBSERVATION, 
          values, 
          selection,
          selectionArgs);
      break;
    case OBSERVATION_ID:
      String id = uri.getLastPathSegment();
      if (TextUtils.isEmpty(selection)) {
        rowsUpdated = sqlDB.update(ObservationTable.TABLE_OBSERVATION, 
            values,
            ObservationTable.COLUMN_ID + "=" + id, 
            null);
      } else {
        rowsUpdated = sqlDB.update(ObservationTable.TABLE_OBSERVATION, 
            values,
            ObservationTable.COLUMN_ID + "=" + id 
            + " and " 
            + selection,
            selectionArgs);
      }
      break;
    default:
      throw new IllegalArgumentException("Unknown URI: " + uri);
    }
    getContext().getContentResolver().notifyChange(uri, null);
    return rowsUpdated;
  }

  private void checkColumns(String[] projection) {
    String[] available = { ObservationTable.COLUMN_EMAIL, ObservationTable.COLUMN_CATEGORY,
    		ObservationTable.COLUMN_IMAGE, ObservationTable.COLUMN_THUMBNAIL,
    		ObservationTable.COLUMN_PLANT, ObservationTable.COLUMN_POLLINATOR,
    		ObservationTable.COLUMN_DESCRIPTION,ObservationTable.COLUMN_ID };
    if (projection != null) {
      HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
      HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
      // Check if all columns which are requested are available
      if (!availableColumns.containsAll(requestedColumns)) {
        throw new IllegalArgumentException("Unknown columns in projection");
      }
    }
  }

} 
