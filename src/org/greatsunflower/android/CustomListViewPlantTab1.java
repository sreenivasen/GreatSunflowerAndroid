package org.greatsunflower.android;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CustomListViewPlantTab1 extends ArrayAdapter<SQLiteObservations> {
	 Context context;
	 List<SQLiteObservations> plantItems;
	 private ObservationsDataSource datasource;
	 
	 private SparseBooleanArray mSelectedItemsIds;
	 
	    public CustomListViewPlantTab1(Context context, int resourceId, List<SQLiteObservations> items) {
	        super(context, resourceId, items);
	        this.context = context;
	        this.plantItems = items;
	        mSelectedItemsIds = new SparseBooleanArray();
	    }
	 
	    /*private view holder class*/
	    private class ViewHolder {
	        TextView plantFamily;
	        TextView plantId;
	        ImageView deleteButton;
	        int itemPosition;
	    }
	    
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ViewHolder holder = null;
	        BitmapFactory.Options options = new BitmapFactory.Options();
	        SQLiteObservations observation = getItem(position);
	        
	        LayoutInflater mInflater = (LayoutInflater) context
	                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        if (convertView == null) {
	            convertView = mInflater.inflate(R.layout.listview_planttab1, null);
	            holder = new ViewHolder();
	            holder.plantFamily = (TextView) convertView.findViewById(R.id.plantFamily);
	            holder.plantId = (TextView) convertView.findViewById(R.id.plantId);	         
	            holder.deleteButton = (ImageView) convertView.findViewById(R.id.removePlant);	            
	            holder.itemPosition = position;


	            convertView.setTag(holder);
	        } else
	            holder = (ViewHolder) convertView.getTag();
	        
	        holder.deleteButton.setOnClickListener(new OnClickListener(){
	        	
				@Override
				public void onClick(View view){
					TableRow r1 = (TableRow) view.getParent();
					ViewHolder delHolder = (ViewHolder) ((View) view.getParent().getParent().getParent()).getTag();
					Log.d("REMOVE PLANT", "current position: " +  delHolder.itemPosition + "");
					TextView id = (TextView) r1.findViewById(R.id.plantId);
					TextView plantFamily = (TextView) r1.findViewById(R.id.plantFamily);
					removePollinatorItem(id.getText().toString(), delHolder.itemPosition, plantFamily.getText().toString());
					Log.d("REMOVE PLANT", id.getText().toString() + " " );
					
				}
			});
	 
	        convertView.setBackgroundColor(mSelectedItemsIds.get(position)? 0x9962B1F6 : Color.TRANSPARENT);
	        
	        holder.plantFamily.setText(observation.getPlantFamily());
	        holder.plantId.setText(String.valueOf(observation.getId()));	 
	        return convertView;
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
		
		public void removePollinatorItem(String pollinatorId, int itemPosition, String pollinatorType){
			final int removeId = Integer.valueOf(pollinatorId);
			final int removeitemPosition = itemPosition;
			datasource = new ObservationsDataSource(context);
			new AlertDialog.Builder(context)
		    .setTitle(pollinatorType)
		    .setMessage("Are you sure you want to delete?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
					
					datasource.open();
					if (datasource.deletePollinator(removeId)){
						Log.d("REMOVE POLLINATOR", "successfully deleted the pollinator");
						//pollinatorItems.remove(removeitemPosition);
						//notifyDataSetChanged();
						
					}
					else
						Log.d("REMOVE POLLINATOR", " unsucessful in removing the pollinator");
					
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		     .show();			
		}

}
