package tree.database;

import java.util.ArrayList;
import java.util.Collections;

import tree.database.data.Tree;
import tree.database.data.User;
import tree.database.misc.GpsHandler;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class BrowseActivity extends Activity{
	 
	private TabHost tabHost;
	private LocalActivityManager mlam;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.browse);
	    mlam = new LocalActivityManager(this, false);
	    
	    
	    tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    mlam.dispatchCreate(savedInstanceState);
        tabHost.setup(mlam);
        Bundle extras = getIntent().getExtras();
        
	    
	    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
	    setupTab(new TextView(this), (String)getText(R.string.nearTabText), new Intent(this, BrowseTabGroup1Activity.class), extras);
	    setupTab(new TextView(this), (String)getText(R.string.recentTabText), new Intent(this, BrowseTabGroup2Activity.class), extras);
	    setupTab(new TextView(this), (String)getText(R.string.allTabText), new Intent(this, BrowseTabGroup3Activity.class), extras);
	    
	    tabHost.setCurrentTab(0);
	}
	
	public void switchTab(int tabid){
		if(tabHost != null){
			tabHost.setCurrentTab(tabid);
		}		
	}
	
	 @Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        	finish();
	        }
	        return true;
	    }
	 
	 private void setupTab(final View view, final String tag, Intent content, Bundle extras) {
		content.putExtras(extras);
 	    View tabview = createTabView(tabHost.getContext(), tag);
 	        TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(content);
 	    tabHost.addTab(setContent);
	 	}
	 	 
 	private static View createTabView(final Context context, final String text) {
 	    View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
 	    TextView tv = (TextView) view.findViewById(R.id.tabsText);
 	    tv.setText(text);
 	    return view;
 	}
 	
 	@Override
 	protected void onResume() {
 		// TODO Auto-generated method stub
 		super.onResume();
 		mlam.dispatchResume(); 
 	}
 	
 	@Override
 	protected void onPause() {
 		// TODO Auto-generated method stub
 		mlam.dispatchPause(isFinishing());
 		super.onPause();
 	}
}
