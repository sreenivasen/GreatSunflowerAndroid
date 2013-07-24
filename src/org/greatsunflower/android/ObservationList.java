package org.greatsunflower.android;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;


 
public class ObservationList extends SherlockListFragment {
	 ActionMode viewMode;
	
    // Array of strings storing country names
    String[] countries = new String[] {
        "India",
        "Pakistan",
        "Sri Lanka",
        "China",
        "Bangladesh",
        "Nepal",
        "Afghanistan",
        "North Korea",
        "South Korea",
        "Japan"
    };
 
    // Array of integers points to images stored in /res/drawable/
    int[] flags = new int[]{
        R.drawable.india,
        R.drawable.pakistan,
        R.drawable.srilanka,
        R.drawable.china,
        R.drawable.bangladesh,
        R.drawable.nepal,
        R.drawable.afghanistan,
        R.drawable.nkorea,
        R.drawable.skorea,
        R.drawable.japan
    };
 
    // Array of strings to store currencies
    String[] currency = new String[]{
        "Indian Rupee",
        "Pakistani Rupee",
        "Sri Lankan Rupee",
        "Renminbi",
        "Bangladeshi Taka",
        "Nepalese Rupee",
        "Afghani",
        "North Korean Won",
        "South Korean Won",
        "Japanese Yen"
    };
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
 
        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
 
        for(int i=0;i<10;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", countries[i]);
            hm.put("cur", currency[i]);
            hm.put("flag", Integer.toString(flags[i]) );
            aList.add(hm);
        }
 
        // Keys used in Hashmap
        String[] from = { "flag","txt","cur" };
 
        // Ids of views in listview_layout
        int[] to = { R.id.flag,R.id.txt,R.id.cur};
 
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getSherlockActivity(), aList, R.layout.activity_observations, from, to);
 
        setListAdapter(adapter);
 
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                viewMode = getSherlockActivity().startActionMode(new ListViewActionMode());
                return true;
            }
        });

    }


    private final class ListViewActionMode implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("Delete")
                    .setIcon(android.R.drawable.ic_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            menu.add("Annotate")
                    .setIcon(android.R.drawable.ic_input_add)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            Toast.makeText(getSherlockActivity(), "Implement actions here bitch:", Toast.LENGTH_SHORT).show();
            viewMode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }
    }


}
