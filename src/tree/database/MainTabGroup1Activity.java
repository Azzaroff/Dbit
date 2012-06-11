package tree.database;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

	public class MainTabGroup1Activity extends ActivityGroup{
		public static MainTabGroup1Activity group;
		private ArrayList<View> history;
		
		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.history = new ArrayList<View>();
	        group = this;

	        
	        Intent intent = new Intent(this, BrowseActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        intent.putExtras(getIntent().getExtras());
	        View view = getLocalActivityManager().startActivity("Browse", intent).getDecorView(); //hier stürzt er ab
	        System.out.println("main activity 1");
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
		    MainTabGroup1Activity.group.back();  
		    return;  
		}
		
		public void clearHistory(){
			history.clear();
		}
}