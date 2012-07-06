package tree.database;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

	public class BrowseTabGroup1Activity extends ActivityGroup{
		public static BrowseTabGroup1Activity group;
		private ArrayList<View> history;
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.history = new ArrayList<View>();
	        group = this;
	        
	        Intent intent = new Intent(this, BrowseTabActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
	        intent.putExtras(getIntent().getExtras());
	        intent.putExtra("Tab", ""+1);
	        View view = getLocalActivityManager().startActivity("Near", intent).getDecorView();
	        
	        setContentView(view);
	    }
}