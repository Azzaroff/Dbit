package tree.database;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

//public class TabGroup2Activity extends TabGroupActivity {
//	@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        startChildActivity("Historie", new Intent(this,HistoryActivity.class));
//    }
//}
public class BrowseTabGroup2Activity extends ActivityGroup{
	public static BrowseTabGroup2Activity group;
	private ArrayList<View> history;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.history = new ArrayList<View>();
        group = this;
        
        Intent intent = new Intent(this, BrowseTabActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(getIntent().getExtras());
        View view = getLocalActivityManager().startActivity("Recent", intent).getDecorView();
        
        setContentView(view);
    }	
}