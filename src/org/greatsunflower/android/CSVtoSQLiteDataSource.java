package org.greatsunflower.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class CSVtoSQLiteDataSource {

	private SQLiteDatabase database;
	private CSVtoSQLiteHelper dbHelper;
	private static final String TABLE_TAXANOMY = null;
	private AssetManager assetManager = null;

	public CSVtoSQLiteDataSource(Context context) {
		dbHelper = new CSVtoSQLiteHelper(context);
		assetManager = context.getAssets();
		
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public boolean populatePollinators(){
		int i = 0;
		try {
			BufferedReader pollinatorReader = new BufferedReader(new InputStreamReader(
					assetManager.open("gsp_pollinators.csv")));
			Log.d("POLLINATOR POPULATE", "file opened");
			pollinatorReader.readLine();
			String csvline = "";
			String tableName = "pollinators";
			String columns = "pollinator_level0,pollinator_level1,pollinator_level2";
			String str1 = "INSERT INTO " + tableName + " (" + columns
					+ ") values(?,?,?)";
			ArrayList<String> lines = new ArrayList<String>();
			while((csvline = pollinatorReader.readLine()) != null){
				lines.add(csvline);
			}		
			pollinatorReader.close();
			int k;
			long startTime = System.currentTimeMillis();
			database.beginTransaction();
			SQLiteStatement stmt = database.compileStatement(str1);
			String[] str;
				for(String line : lines){
				str = line.split(",");
				for (k=0; k < str.length; k++){
					//Log.d("POLLINATORS INSERT", String.valueOf(k+1) + ": " + DatabaseUtils.sqlEscapeString(str[k]));
					stmt.bindString(k+1, str[k]);
				}
				stmt.execute();
			    stmt.clearBindings();
				i++;
				}
				Log.d("POLLINATORS Populate", "Rows inserted: " + String.valueOf(i));
				long endTime = System.currentTimeMillis();
				Log.d("POLLINATORS Populate", "Time Taken for DB insert: " + String.valueOf(endTime-startTime));
				database.setTransactionSuccessful();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			database.endTransaction();
		}
		return true;
	}
	
	public boolean populatePlants(){
		try {
			int i = 0;
			BufferedReader plantReader = new BufferedReader(new InputStreamReader(
					assetManager.open("gsp_plants.csv")));
			Log.d("PLANTS POPULATE", "file opened");
			plantReader.readLine();
			String csvline = "";
			String tableName = "plants";
			String columns = "plant_level0,plant_level1";
			String str1 = "INSERT INTO " + tableName + " (" + columns
					+ ") values(?,?)";
			ArrayList<String> lines = new ArrayList<String>();
			while((csvline = plantReader.readLine()) != null){
				lines.add(csvline);
			}		
			plantReader.close();
			int k;
			long startTime = System.currentTimeMillis();
			database.beginTransaction();
			SQLiteStatement stmt = database.compileStatement(str1);
			String[] str;
				for(String line : lines){
				str = line.split(",");
				for (k=0; k < str.length; k++){
					Log.d("PLANTS INSERT", String.valueOf(k+1) + ": " + DatabaseUtils.sqlEscapeString(str[k]));
					stmt.bindString(k+1, str[k]);
				}
				stmt.execute();
			    stmt.clearBindings();
				i++;
				}
				Log.d("PLANTS INSERT", "Rows inserted: " + String.valueOf(i));
				long endTime = System.currentTimeMillis();
				Log.d("PLANTS INSERT", "Time Taken for DB insert: " + String.valueOf(endTime-startTime));
				database.setTransactionSuccessful();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			database.endTransaction();
		}
		return true;
	}

	public boolean populateDatabase() {
		Log.d("CSV Populate", "Entered");
		populatePlants();
		populatePollinators();
//		int i = 0;
//		try {
//			BufferedReader reader = new BufferedReader(new InputStreamReader(
//					assetManager.open("taxanomy.csv")));
//			Log.d("CSV Populate", "file opened");
//			reader.readLine();
//			
//			String csvline = "";
//			String tableName = "taxanomy";
//			String columns = "taxa, middle_level, common_name, visitor_genus, visitor_species";
//			columns = columns + ", plant_family, plant_genus, plant_species, var_subspecies";
//			String str1 = "INSERT INTO " + tableName + " (" + columns
//					+ ") values(?,?,?,?,?,?,?,?,?)";
//			//String str2 = ");";
//			
//			ArrayList<String> lines = new ArrayList<String>();
//			long csvStartTime = System.currentTimeMillis();
//			while((csvline = reader.readLine()) != null){
//				lines.add(csvline);
//			}
//			long csvEndTime = System.currentTimeMillis();		
//			reader.close();
//			Log.d("CSV Populate", "Time Taken for CSV read: " + String.valueOf(csvEndTime-csvStartTime));
//			
//			int k;
//			long startTime = System.currentTimeMillis();
//			database.beginTransaction();
//			SQLiteStatement stmt = database.compileStatement(str1);
//			String[] str;
//			//while ((line = reader.readLine()) != null) {
//				for(String line : lines){
//				
//				//StringBuilder sb = new StringBuilder(str1);
//				str = line.split(",");
//				for (k=0; k < str.length-1; k++){
//					
//					//sb.append(DatabaseUtils.sqlEscapeString(str[k]) + ",");
//					//Log.d("CSV INSERT", String.valueOf(k+1) + ": " + DatabaseUtils.sqlEscapeString(str[k]));
//					//stmt.bindString(k+1, DatabaseUtils.sqlEscapeString(str[k]));
//					stmt.bindString(k+1, str[k]);
//				}
//				//sb.append("'" + str[k++] + "'");
//				//stmt.bindString(k+1, DatabaseUtils.sqlEscapeString(str[k++]));
//				stmt.bindString(k+1, str[k]);
//				//Log.d("CSV INSERT", String.valueOf(k) + ": " + DatabaseUtils.sqlEscapeString(str[k]));
//				if (k < 9){
//					//sb.append(",");
//				
//				for(; k < 8; k++){
//					//sb.append("'',");
//					stmt.bindString(k+1, "");
//					//Log.d("CSV INSERT", String.valueOf(k+1) + ": " + "");
//				}
//				//sb.append("''");
//				stmt.bindString(k+1, "");
//				//Log.d("CSV INSERT", String.valueOf(k+1) + ": " + "");
//				
//				}
//				//Log.d("CSV INSERT", stmt.toString());
//				stmt.execute();
//			    stmt.clearBindings();
//				
//				//sb.append(str2);
//				//Log.d("CSV", "Row:" + String.valueOf(i) + " " + sb.toString() );
//				//database.execSQL(sb.toString());
//				i++;
//			}
//			Log.d("CSV Populate", "Rows inserted: " + String.valueOf(i));
//			long endTime = System.currentTimeMillis();
//			Log.d("CSV Populate", "Time Taken for DB insert: " + String.valueOf(endTime-startTime));
//			database.setTransactionSuccessful();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		finally{
//			database.endTransaction();
//		}
		return true;
	}
	
	public int getRowCount(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT * FROM "  + CSVtoSQLiteHelper.TABLE_POLLINATORS;
		Log.d("CSV ACTION", selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		int count = 0;
		if(null != cursor){
//		    if(cursor.getCount() > 0){
//		      cursor.moveToFirst();
//		      count = cursor.getInt(0);
//		    }
			count = cursor.getCount();
		    cursor.close();
		}
		return count;
	}
	
	
	public String[] getDistinctPlantFamily(){
		Log.d("CSV GET DISTINCT", "Entered");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT plant_family FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
//		selectQuery = selectQuery + " WHERE middle_level <> '' AND common_name <> ''";
		
		Log.d("CSV GET QUERY", selectQuery);
		
		Cursor cursor1 = db.rawQuery(selectQuery, null);
		
		if (cursor1 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor1.getCount());

		String[] plantFamily = new String[cursor1.getCount()];
		if (cursor1.moveToFirst()) {
            for(int j = 0; j < cursor1.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "plant family: " + cursor1.getString(cursor1.getColumnIndex("plant_family")));
    			plantFamily[j] = cursor1.getString(cursor1.getColumnIndex("plant_family"));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor1.moveToNext();
            }

        }

		else {
			plantFamily = null;
		}
		cursor1.close();
		return plantFamily;
	}
	
	public ArrayList<String> getDistinctPlantGenus(String data){
		ArrayList<String> plantGenus = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT plant_genus FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		selectQuery = selectQuery + " WHERE plant_family = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND plant_genus <> " + "'" + "'";
		selectQuery = selectQuery + " " + "order by plant_genus asc";

		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		
		if (cursor2 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor2.getCount());
		
		
		if (cursor2.moveToFirst()) {
            for(int j = 0; j < cursor2.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "plant_genus: " + cursor2.getString(cursor2.getColumnIndex("plant_genus")));
    			plantGenus.add(cursor2.getString(cursor2.getColumnIndex("plant_genus")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor2.moveToNext();
            }

        }

		else {
			plantGenus.add("Sorry! I could not drill down further");
		}
		cursor2.close();
		return plantGenus;
	}
	
	public ArrayList<String> getDistinctPlantSpecies(String data){
		ArrayList<String> plantSpecies = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT plant_species FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		selectQuery = selectQuery + " WHERE plant_genus = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND plant_species <> " + "'" + "'";
		selectQuery = selectQuery + " " + "order by plant_species asc";

		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		
		if (cursor2 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor2.getCount());
		
		
		if (cursor2.moveToFirst()) {
            for(int j = 0; j < cursor2.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "plant_species: " + cursor2.getString(cursor2.getColumnIndex("plant_species")));
    			plantSpecies.add(cursor2.getString(cursor2.getColumnIndex("plant_species")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor2.moveToNext();
            }

        }

		else {
			plantSpecies.add("Sorry! I could not drill down further");
		}
		cursor2.close();
		return plantSpecies;
	}
	
	public ArrayList<String> getDistinctPlantVarSubSpecies(String data){
		ArrayList<String> plantVarSubSpecies = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT var_subspecies FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		selectQuery = selectQuery + " WHERE plant_species = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND var_subspecies <> " + "'" + "'";
		selectQuery = selectQuery + " " + "order by var_subspecies asc";

		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		
		if (cursor2 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor2.getCount());
		
		
		if (cursor2.moveToFirst()) {
            for(int j = 0; j < cursor2.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "var_subspecies: " + cursor2.getString(cursor2.getColumnIndex("var_subspecies")));
    			plantVarSubSpecies.add(cursor2.getString(cursor2.getColumnIndex("var_subspecies")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor2.moveToNext();
            }

        }

		else {
			plantVarSubSpecies.add("Sorry! I could not drill down further");
		}
		cursor2.close();
		return plantVarSubSpecies;
	}
	
	public ArrayList<String> getDistinctPollinatorLevel2(String data){
		ArrayList<String> pollinatorLevel2 = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT pollinator_level2 FROM "  + CSVtoSQLiteHelper.TABLE_POLLINATORS;
		selectQuery = selectQuery + " WHERE pollinator_level1 = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND pollinator_level2 <> " + "'" + "'";
		selectQuery = selectQuery + " " + "order by pollinator_level2 asc";

		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		
		if (cursor2 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor2.getCount());
		
		
		if (cursor2.moveToFirst()) {
            for(int j = 0; j < cursor2.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "var_subspecies: " + cursor2.getString(cursor2.getColumnIndex("pollinator_level2")));
    			pollinatorLevel2.add(cursor2.getString(cursor2.getColumnIndex("pollinator_level2")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor2.moveToNext();
            }

        }

		else {
			pollinatorLevel2.add("Sorry! I could not drill down further");
		}
		cursor2.close();
		return pollinatorLevel2;
	}
	
	public ArrayList<String> getDistinctPollinatorLevel1(String data){
		ArrayList<String> pollinatorLevel1 = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT pollinator_level1 FROM "  + CSVtoSQLiteHelper.TABLE_POLLINATORS;
		selectQuery = selectQuery + " WHERE pollinator_level0 = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND pollinator_level1 <> " + "'" + "'";
		selectQuery = selectQuery + " " + "order by pollinator_level1 asc";

		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		
		if (cursor2 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor2.getCount());
		
		
		if (cursor2.moveToFirst()) {
            for(int j = 0; j < cursor2.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "var_subspecies: " + cursor2.getString(cursor2.getColumnIndex("pollinator_level1")));
    			pollinatorLevel1.add(cursor2.getString(cursor2.getColumnIndex("pollinator_level1")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor2.moveToNext();
            }

        }

		else {
			pollinatorLevel1.add("Sorry! I could not drill down further");
		}
		cursor2.close();
		return pollinatorLevel1;
	}
	
	public String[] getDistinctPollinatorLevel0(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT pollinator_level0 FROM "  + CSVtoSQLiteHelper.TABLE_POLLINATORS;		
		Cursor cursor1 = db.rawQuery(selectQuery, null);
		
		if (cursor1 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor1.getCount());

		String[] pollinators_level0 = new String[cursor1.getCount()];
		if (cursor1.moveToFirst()) {
            for(int j = 0; j < cursor1.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "taxa: " + cursor1.getString(cursor1.getColumnIndex("pollinator_level0")));
    			pollinators_level0[j] = cursor1.getString(cursor1.getColumnIndex("pollinator_level0"));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor1.moveToNext();
            }

        }

		else {
			pollinators_level0 = null;
		}
		cursor1.close();
		return pollinators_level0;
	}
	
	public ArrayList<String> getDistinctPlantLevel1(String data){
		ArrayList<String> plantsLevel1 = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT plant_level1 FROM "  + CSVtoSQLiteHelper.TABLE_PLANTS;
		selectQuery = selectQuery + " WHERE plant_level0 = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND plant_level0 <> " + "'" + "'";
		selectQuery = selectQuery + " " + "order by plant_level1 asc";

		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		
		if (cursor2 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor2.getCount());
		
		
		if (cursor2.moveToFirst()) {
            for(int j = 0; j < cursor2.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "var_subspecies: " + cursor2.getString(cursor2.getColumnIndex("plant_level1")));
    			plantsLevel1.add(cursor2.getString(cursor2.getColumnIndex("plant_level1")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor2.moveToNext();
            }

        }

		else {
			plantsLevel1.add("Sorry! I could not drill down further");
		}
		cursor2.close();
		return plantsLevel1;
	}
	
	public String[] getDistinctPlantLevel0(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT plant_level0 FROM "  + CSVtoSQLiteHelper.TABLE_PLANTS;		
		Cursor cursor1 = db.rawQuery(selectQuery, null);
		
		if (cursor1 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor1.getCount());

		String[] plants_level0 = new String[cursor1.getCount()];
		if (cursor1.moveToFirst()) {
            for(int j = 0; j < cursor1.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "taxa: " + cursor1.getString(cursor1.getColumnIndex("plant_level0")));
    			plants_level0[j] = cursor1.getString(cursor1.getColumnIndex("plant_level0"));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor1.moveToNext();
            }

        }

		else {
			plants_level0 = null;
		}
		cursor1.close();
		return plants_level0;
	}
	
	public String[] getDistinctTaxa(){
		Log.d("CSV GET DISTINCT", "Entered");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT taxa FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		//selectQuery = selectQuery + " WHERE middle_level <> '' AND common_name <> ''";
		
		Log.d("CSV GET QUERY", selectQuery);
		
		Cursor cursor1 = db.rawQuery(selectQuery, null);
		
		if (cursor1 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor1.getCount());

		String[] taxas = new String[cursor1.getCount()];
		if (cursor1.moveToFirst()) {
            for(int j = 0; j < cursor1.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "taxa: " + cursor1.getString(cursor1.getColumnIndex("taxa")));
  			taxas[j] = cursor1.getString(cursor1.getColumnIndex("taxa"));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor1.moveToNext();
            }

        }

		else {
			taxas = null;
		}
		cursor1.close();
		return taxas;
	}
	

	public ArrayList<String> getDistinctMiddleLevel(String data){
		ArrayList<String> mid = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT middle_level FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		selectQuery = selectQuery + " WHERE taxa = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND middle_level <> " + "'" + "'";
		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor2 = db.rawQuery(selectQuery, null);
		
		if (cursor2 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor2.getCount());
		
		
		if (cursor2.moveToFirst()) {
            for(int j = 0; j < cursor2.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "middle_level: " + cursor2.getString(cursor2.getColumnIndex("middle_level")));
    			mid.add(cursor2.getString(cursor2.getColumnIndex("middle_level")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor2.moveToNext();
            }

        }

		else {
			mid.add("Sorry! I could not drill down further");
		}
		cursor2.close();
		return mid;
	}
	
	public ArrayList<String> getDistinctCommonName(String data){
		ArrayList<String> common = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT common_name FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		selectQuery = selectQuery + " WHERE middle_level = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND common_name <> " + "'" + "'";
		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor3 = db.rawQuery(selectQuery, null);
		
		if (cursor3 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor3.getCount());
		
		
		if (cursor3.moveToFirst()) {
            for(int j = 0; j < cursor3.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "common_name: " + cursor3.getString(cursor3.getColumnIndex("common_name")));
    			common.add(cursor3.getString(cursor3.getColumnIndex("common_name")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor3.moveToNext();
            }

        }

		else {
			common.add("Sorry! I could not drill down further");
		}
		cursor3.close();
		return common;
	}
	
	public ArrayList<String> getDistinctVisitorGenus(String data){
		ArrayList<String> common = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT visitor_genus FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		selectQuery = selectQuery + " WHERE common_name = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND visitor_genus <> " + "'" + "'";
		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor3 = db.rawQuery(selectQuery, null);
		
		if (cursor3 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor3.getCount());
		
		
		if (cursor3.moveToFirst()) {
            for(int j = 0; j < cursor3.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "visitor_genus: " + cursor3.getString(cursor3.getColumnIndex("visitor_genus")));
    			common.add(cursor3.getString(cursor3.getColumnIndex("visitor_genus")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor3.moveToNext();
            }

        }

		else {
			common.add("Sorry! I could not drill down further");
		}
		cursor3.close();
		return common;
	}
	
	public ArrayList<String> getDistinctVisitorSpecies(String data){
		ArrayList<String> common = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String selectQuery = "SELECT DISTINCT visitor_species FROM "  + CSVtoSQLiteHelper.TABLE_TAXANOMY;
		selectQuery = selectQuery + " WHERE visitor_genus = ";
		selectQuery = selectQuery + " " + DatabaseUtils.sqlEscapeString(data) + "";
		selectQuery = selectQuery + " AND visitor_species <> " + "'" + "'";
		Log.d("CSV GET QUERY", selectQuery);
		Cursor cursor3 = db.rawQuery(selectQuery, null);
		
		if (cursor3 == null)
			Log.d("CSV GET DISTINCT", "the cursor is null");
		else
			Log.d("CSV GET DISTINCT", "number of rows returned: " + cursor3.getCount());
		
		
		if (cursor3.moveToFirst()) {
            for(int j = 0; j < cursor3.getCount(); j++ ){
    			Log.d("CSV GET DISTINCT", "visitor_genus: " + cursor3.getString(cursor3.getColumnIndex("visitor_species")));
    			common.add(cursor3.getString(cursor3.getColumnIndex("visitor_species")));
            	 Log.d("CSV GET DISTINCT","value of J: " + String.valueOf(j));
                cursor3.moveToNext();
            }

        }

		else {
			common.add("Sorry! I could not drill down further");
		}
		cursor3.close();
		return common;
	}
}
