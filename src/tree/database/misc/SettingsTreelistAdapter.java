package tree.database.misc;

import java.text.DecimalFormat;
import java.util.ArrayList;

import tree.database.R;
import tree.database.data.Tree;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsTreelistAdapter extends BaseAdapter{
	private Activity activity;
    private ArrayList<Tree> trees;
    private static LayoutInflater inflater=null;
    
    public SettingsTreelistAdapter(Activity a, ArrayList<Tree> trees) {
        this.activity = a;
        this.trees = trees;
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        if(this.trees.size() <= 0){
        	Tree t = new Tree();
        	t.Name = (String) activity.getText(R.string.empty_treelist);
        	t.Age = 0;
        	t.Size = Double.NaN;
        	t.Location = new Double[2];
        	t.Location[0] = Double.MAX_VALUE;
        	t.Location[1] = Double.MAX_VALUE;
        	t.Images = new ArrayList<Bitmap>();
        	t.Images.add(BitmapFactory.decodeResource(activity.getResources(), R.drawable.baum));
        	this.trees.add(t);
        }
    }

    public int getCount() {
        return trees.size();
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
            vi = inflater.inflate(R.layout.my_treelist_item, null);

        Tree tree = trees.get(position);
        
        TextView nameText=(TextView)vi.findViewById(R.id.treelist_nametext);
        TextView latitudeText=(TextView)vi.findViewById(R.id.treelist_lattext);
        TextView longitudeText=(TextView)vi.findViewById(R.id.treelist_longtext);
        TextView sizeText=(TextView)vi.findViewById(R.id.treelist_sizetext);
        TextView ageText=(TextView)vi.findViewById(R.id.treelist_agetext);
        ImageView image=(ImageView)vi.findViewById(R.id.treelist_picture);

        nameText.setText(tree.Name);
        if(tree.Location[0] == Double.MAX_VALUE){
        	longitudeText.setText(activity.getText(R.string.no_location).toString()+" "+activity.getText(R.string.longitude).toString());
        	latitudeText.setText(activity.getText(R.string.no_location).toString()+" "+activity.getText(R.string.latitude).toString());
        }else{
        	DecimalFormat df = new DecimalFormat("0.000000");
        	longitudeText.setText(""+df.format(tree.Location[1])+"° "+activity.getText(R.string.longitude).toString());
        	latitudeText.setText(""+df.format(tree.Location[0])+"° "+activity.getText(R.string.latitude).toString());
        }
        sizeText.setText(Double.toString(tree.Size));
        ageText.setText(""+tree.Age);
        
        image.setImageBitmap(tree.Images.get(0));
        
        return vi;
    }
}
