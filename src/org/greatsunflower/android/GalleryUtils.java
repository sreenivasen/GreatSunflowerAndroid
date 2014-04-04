package org.greatsunflower.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
 
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;
 
public class GalleryUtils {
 
    private Context _context;
	private ObservationsDataSource datasource;
 
    // constructor
    public GalleryUtils(Context context) {
        this._context = context;
		datasource = new ObservationsDataSource(context);
		datasource.open();
    }
 
    // Reading file paths from SDCard
    public ArrayList<String> getFilePaths(int sessionId) {
        ArrayList<String> filePaths = new ArrayList<String>();
 
//        File directory = new File(
//                android.os.Environment.getExternalStorageDirectory()
//                        + File.separator + AppConstant.PHOTO_ALBUM);
 

        
 //       File directory = new File(Environment.getExternalStorageDirectory(),"GreatSunflowerProject");
        
        File directory =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"GreatSunflowerProject");
        
        // check for directory
        if (directory.isDirectory()) {
            // getting list of file paths
            File[] listFiles = directory.listFiles();
 
            // Check for count
            if (listFiles.length > 0) {
 
                // loop through all files
                for (int i = 0; i < listFiles.length; i++) {
 
                    // get file path
                    String filePath = listFiles[i].getAbsolutePath();
 
                    // check for supported file extension
                    if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    }
                }
            } else {
                // image directory is empty
                Toast.makeText(
                        _context,
                        AppConstant.PHOTO_ALBUM
                                + " is empty. Please load some images in it !",
                        Toast.LENGTH_LONG).show();
            }
            
            filePaths = datasource.getSessionImages(sessionId);
 
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(_context);
            alert.setTitle("Error!");
            alert.setMessage(AppConstant.PHOTO_ALBUM
                    + " directory path is not valid! Please set the image directory name AppConstant.java class");
            alert.setPositiveButton("OK", null);
            alert.show();
        }
 
        return filePaths;
    }
 
    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());
 
        if (AppConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;
 
    }
 
    /*
     * getting screen width
     */
    @SuppressLint("NewApi")
	public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) _context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
 
        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }
}
