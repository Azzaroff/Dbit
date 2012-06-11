package tree.database.misc;

import java.util.ArrayList;

import tree.database.R;
import tree.database.data.Comment;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<Comment> comments;
    private static LayoutInflater inflater=null;
    
    public LazyAdapter(Activity a, ArrayList<Comment> comments) {
        this.activity = a;
        this.comments = comments;
        System.out.println("klappt");
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        System.out.println("klappt nicht");
        
        if(comments.size() <= 0){
        	Comment c = new Comment();
        	c.Content = (String) activity.getText(R.string.empty_comment);
        	comments.add(c);
        }
    }

    public int getCount() {
        return comments.size();
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
            vi = inflater.inflate(R.layout.comment_list_item, null);

        TextView text=(TextView)vi.findViewById(R.id.commentlistitem_text1);;
        ImageView image=(ImageView)vi.findViewById(R.id.comment_user_avatar);

        text.setText(comments.get(position).Content);
        
        return vi;
    }
}