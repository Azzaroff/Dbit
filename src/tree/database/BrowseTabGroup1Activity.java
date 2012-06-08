package tree.database;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
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
	        View view = getLocalActivityManager().startActivity("Near", intent).getDecorView();
	        
	        replaceView(view);
	    }
		
		public void replaceView(View v) {  
            // Adds the old one to history  
		    history.add(v);  
		            // Changes this Groups View to the new View.  
		    setContentView(v);  
		}  
		
		public void back() {  
		    if(history.size() > 0) {  
		        history.remove(history.size()-1);  
		        setContentView(history.get(history.size()-1));  
		    }else {  
		        finish();  
		    }  
		}  
		
		@Override  
		public void onBackPressed() {  
		    BrowseTabGroup1Activity.group.back();  
		    return;  
		}
		
		public void clearHistory(){
			history.clear();
		}
}