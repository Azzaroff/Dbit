package tree.database;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class BrowseTabGroup3Activity extends ActivityGroup{
	public static BrowseTabGroup3Activity group;
	private ArrayList<View> history;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.history = new ArrayList<View>();
        group = this;
        
        Intent intent = new Intent(this, BrowseTabActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(getIntent().getExtras());
        intent.putExtra("Tab", ""+3);
        View view = getLocalActivityManager().startActivity("All", intent).getDecorView();
        
        setContentView(view);
    }
}