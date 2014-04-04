package org.greatsunflower.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;

@SuppressLint("NewApi")
public class ImageSelectGridview extends SherlockFragmentActivity {

	GridView mGrid;
	private ArrayList<String> imagePaths = new ArrayList<String>();
	private ArrayList<Integer> imageIds = new ArrayList<Integer>();
	private GalleryUtils utils;
	private int sessionId;
	private SharedPreferences pref;
	private int columnWidth;
	private int totalImagesSelected = 0;

	final ImageSelectGridview imageGridView = this;

	private ObservationsDataSource datasource;
	private SparseBooleanArray imagesSelected;

	Button doneButton;
	String images = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		pref = getApplicationContext().getSharedPreferences("APP_PREFERENCES",
				MODE_PRIVATE);
		Log.d("SESSION DETAILS", "Session Id: " + pref.getInt("SESSION_ID", -1));
		sessionId = pref.getInt("SESSION_ID", -1);

		loadApps();

		setContentView(R.layout.select_images);
		doneButton = (Button) findViewById(R.id.nextButton);

		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				AppConstant.GRID_PADDING, r.getDisplayMetrics());
		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COL) * padding)) / (AppConstant.NUM_OF_COL));

		mGrid = (GridView) findViewById(R.id.myGrid);
		mGrid.setAdapter(new AppsAdapter(imagePaths, imageIds, columnWidth));
		mGrid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE_MODAL);
		mGrid.setMultiChoiceModeListener(new MultiChoiceModeListener());

		doneButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("GRID VIEW", "acknowledging click");
				Log.d("GRID VIEW", "number of images selected: "
						+ totalImagesSelected);
				Log.d("GRID VIEW", images);
				images = "";
				for (int i = 0; i <= imagesSelected.size(); i++) {
					if (imagesSelected.get(i)) {
						Log.d("GRID VIEW","image Id " + i + " " + imageIds.get(i));
						images = + imageIds.get(i) + "," + images ;
					}
				}
				Editor editor = pref.edit();
				editor.putString("IMAGE_IDS", images);
				editor.commit();
				imageGridView.finish();
			}
		});

	}

	private List<ResolveInfo> mApps;

	private void loadApps() {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
		utils = new GalleryUtils(this);

		datasource = new ObservationsDataSource(this);
		datasource.open();
		imagePaths = datasource.getSessionImages(sessionId);
		imageIds = datasource.getSessionImageIds(sessionId);
		datasource.close();
	}

	public class AppsAdapter extends BaseAdapter {

		private ArrayList<String> filePaths = new ArrayList<String>();
		private ArrayList<Integer> imageIds = new ArrayList<Integer>();
		private int imageWidth;

		public AppsAdapter(ArrayList<String> filePaths,
				ArrayList<Integer> imageIds, int imageWidth) {
			this.filePaths = filePaths;
			this.imageWidth = imageWidth;
			this.imageIds = imageIds;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			CheckableLayout l;
			ImageView i;
			BitmapShader shader;

			if (convertView == null) {
				i = new ImageView(ImageSelectGridview.this);
				// i.setScaleType(ImageView.ScaleType.FIT_CENTER);
				// i.setLayoutParams(new ViewGroup.LayoutParams(200, 200));

				i.setScaleType(ImageView.ScaleType.CENTER_CROP);
				i.setLayoutParams(new GridView.LayoutParams(imageWidth,
						imageWidth));
				i.setRotation(90);
				l = new CheckableLayout(ImageSelectGridview.this);
				// l.setLayoutParams(new GridView.LayoutParams(
				// GridView.LayoutParams.WRAP_CONTENT,
				// GridView.LayoutParams.WRAP_CONTENT));
				l.addView(i);
			} else {
				l = (CheckableLayout) convertView;
				i = (ImageView) l.getChildAt(0);
			}

			// ResolveInfo info = mApps.get(position);
			// i.setImageDrawable(info.activityInfo.loadIcon(getPackageManager()));
			Bitmap image = decodeFile(filePaths.get(position), imageWidth,
					imageWidth);

			i.setImageBitmap(getRoundedCornerBitmap(image, 125));

			return l;
		}

		public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
			Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
					bitmap.getHeight(), Config.ARGB_8888);
			Canvas canvas = new Canvas(output);

			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bitmap.getWidth() + 80,
					bitmap.getHeight() + 80);
			final RectF rectF = new RectF(rect);
			final float roundPx = pixels;

			paint.setAntiAlias(true);
			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bitmap, rect, rect, paint);

			return output;
		}

		public Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
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
				return BitmapFactory.decodeStream(new FileInputStream(f), null,
						o2);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		public final int getCount() {
			// return mApps.size();
			return imagePaths.size();
		}

		public final Object getItem(int position) {
			// return mApps.get(position);
			return imagePaths.get(position);
		}

		public final long getItemId(int position) {
			return position;
		}
	}

	public class CheckableLayout extends FrameLayout implements Checkable {
		private boolean mChecked;

		public CheckableLayout(Context context) {
			super(context);
		}

		@SuppressWarnings("deprecation")
		public void setChecked(boolean checked) {
			mChecked = checked;
			setBackgroundDrawable(checked ? getResources().getDrawable(
					R.drawable.blue) : null);
		}

		public boolean isChecked() {
			return mChecked;
		}

		public void toggle() {
			setChecked(!mChecked);
		}

	}

	public class MultiChoiceModeListener implements
			GridView.MultiChoiceModeListener {
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// mode.setTitle("Select Items");
			// mode.getMenuInflater().inflate(R.menu.contextual_grid, menu);
			mode.setTitle("One item selected");
			return true;
		}

		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return true;
		}

		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return true;
		}

		public void onDestroyActionMode(ActionMode mode) {
		}

		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			int selectCount = mGrid.getCheckedItemCount();
			totalImagesSelected = mGrid.getCheckedItemCount();
			imagesSelected = mGrid.getCheckedItemPositions();

			Log.d("GRID VIEW", "position: " + position + " poistions: "
					+ mGrid.getCheckedItemPositions().toString() + " imageId: "
					+ imageIds.get(position) + "boolean value: " + checked);
			switch (selectCount) {
			case 1:
				mode.setTitle("One item selected");
				break;
			default:
				mode.setTitle(selectCount + "");
				break;
			}
		}

	}
}