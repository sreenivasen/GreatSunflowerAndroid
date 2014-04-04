package org.greatsunflower.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Vibrator;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
 
public class GridViewImageAdapter extends BaseAdapter {
 
    private Activity _activity;
    private ArrayList<String> _filePaths = new ArrayList<String>();
    private int imageWidth;
    private SparseBooleanArray mSelectedItemsIds;
 
    public GridViewImageAdapter(Activity activity, ArrayList<String> filePaths,
            int imageWidth) {
        this._activity = activity;
        this._filePaths = filePaths;
        this.imageWidth = imageWidth;
        mSelectedItemsIds = new SparseBooleanArray();
    }
 
    @Override
    public int getCount() {
        return this._filePaths.size();
    }
 
    @Override
    public Object getItem(int position) {
        return this._filePaths.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    @SuppressLint("NewApi")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);
        } else {
            imageView = (ImageView) convertView;
        }
 
        // get screen dimensions
        Bitmap image = decodeFile(_filePaths.get(position), imageWidth,
                imageWidth);
 
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        imageView.setRotation(90);
        imageView.setImageBitmap(image);
 
        // image view click listener
        //imageView.setOnClickListener(new OnImageClickListener(position));
        //imageView.setLongClickable(true);

        return imageView;
    }
 
    class OnImageClickListener implements OnClickListener {
 
        int _postion;
 
        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }
 
        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
//            Intent i = new Intent(_activity, FullScreenViewActivity.class);
//            i.putExtra("position", _postion);
//            _activity.startActivity(i);
        }
 
    }
 
    /*
     * Resizing image size
     */
    public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
        try {
 
            File f = new File(filePath);
 
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
 
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;
 
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void toggleSelection(int position) {
    	Log.d("SREENI","the position being passed in toggle selection: " + position);
		selectView(position, !mSelectedItemsIds.get(position));
	}
    
    public void selectView(int position, boolean value) {
    	Log.d("SREENI","the position being passed in selectview: " + position);
    	Log.d("SREENI","the value being passed in selectview: " + value);
		if (value)
			mSelectedItemsIds.put(position, value);
		else
			mSelectedItemsIds.delete(position);

		notifyDataSetChanged();
	}
    
    public int getSelectedCount() {
    	Log.d("SREENI","get selected count " + mSelectedItemsIds.size());
		return mSelectedItemsIds.size();// mSelectedCount;
	}

	public SparseBooleanArray getSelectedIds() {
		return mSelectedItemsIds;
	}
	
	public void removeSelection() {
		mSelectedItemsIds = new SparseBooleanArray();
		notifyDataSetChanged();
	}
 
}
