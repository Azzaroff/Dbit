package tree.database.misc;

import java.util.ArrayList;

import tree.database.R;
import tree.database.SettingsActivity;
import tree.database.data.Group;
import tree.database.data.User;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GrouplistAdapter extends BaseAdapter{
	private Activity activity;
    private ArrayList<Group> groups;
    private static LayoutInflater inflater=null;
    private User user;
    
    public GrouplistAdapter(Activity a, ArrayList<Group> groups, User user) {
        this.activity = a;
        this.groups = groups;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.user = user;

        //add "create" item
    	Group g = new Group();
    	g.Name = activity.getText(R.string.empty_groups).toString();
    	this.groups.add(0, g);
    	Log.i(this.getClass().getSimpleName(), "Number of Groups: "+(this.groups.size()-1));
    }

    public int getCount() {
        return groups.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.group_list_item, null);

        TextView groupname = (TextView) vi.findViewById(R.id.groupList_Name);
        CheckBox subscribeBox = (CheckBox) vi.findViewById(R.id.groupList_checkbox);
        
        Log.i(this.getClass().getSimpleName(), "Fill grouplist view.");
        
        groupname.setText(groups.get(position).Name);
        if(position == 0){
        	subscribeBox.setChecked(false);
        }else{
        	subscribeBox.setChecked(groups.get(position).MemberList.contains(user.ID));
        }
        return vi;
    }
}
