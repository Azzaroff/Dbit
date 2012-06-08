package tree.database;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class BrowseActivity extends Activity{
	 
	private TabHost tabHost;
	private EditText searchBar;
	private LocalActivityManager mlam; 
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.browse);
	    mlam = new LocalActivityManager(this, false);
	    
	    searchBar = (EditText)findViewById(R.id.browse_search);
	    searchBar.setText(getText(R.string.searchText));
	    
	    tabHost = (TabHost) findViewById(android.R.id.tabhost);
	    mlam.dispatchCreate(savedInstanceState);
        tabHost.setup(mlam); 
//	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
//
//	    Resources res = getResources();
//
//	    spec = tabHost.newTabSpec("nearTab")
//	    				.setIndicator("Near", res.getDrawable(R.drawable.ic_tab_calculate))
//	    				.setContent(new Intent(this, BrowseTabGroup1Activity.class));
//	    tabHost.addTab(spec);
//
//	    // Do the same for the other tabs
//	    spec = tabHost.newTabSpec("recentTab").setIndicator("Recent", res.getDrawable(R.drawable.ic_tab_history))
//	                      .setContent(new Intent(this, BrowseTabGroup2Activity.class));
//	    tabHost.addTab(spec);
//
//	    spec = tabHost.newTabSpec("allTab")
//	    				.setIndicator("All", res.getDrawable(R.drawable.ic_tab_impress))
//	    				.setContent(new Intent(this, BrowseTabGroup3Activity.class));
//	    tabHost.addTab(spec);
	    
	    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
	    setupTab(new TextView(this), (String)getText(R.string.nearTabText), new Intent(this, BrowseTabGroup1Activity.class));
	    setupTab(new TextView(this), (String)getText(R.string.recentTabText), new Intent(this, BrowseTabGroup2Activity.class));
	    setupTab(new TextView(this), (String)getText(R.string.allTabText), new Intent(this, BrowseTabGroup3Activity.class));
	    
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
//	            Intent intent = new Intent(getApplicationContext(), OutputActivity.class);
//	            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	            View view = TabGroup1Activity.group.getLocalActivityManager().startActivity("Ergebnisse", intent)
//	                    .getDecorView();
//	            TabGroup1Activity.group.setContentView(view);
	        	MainTabGroup1Activity.group.back();
	        }
	        return true;
	    }
	 
	 private void setupTab(final View view, final String tag, Intent content) {
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
 		mlam.dispatchResume(); 
 		super.onResume();
 	}
 	
 	@Override
 	protected void onPause() {
 		// TODO Auto-generated method stub
 		mlam.dispatchPause(isFinishing());
 		super.onPause();
 	}
}
