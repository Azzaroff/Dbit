package tree.database;

import tree.database.data.User;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainActivity extends TabActivity{

	private TabHost tabHost;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    
	    requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);	    
        setContentView(R.layout.main); 
        Bundle extras = getIntent().getExtras();
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.logotitle);

//        setContentView(R.layout.main);
        
//	    Resources res = getResources();
	    tabHost = (TabHost) findViewById(android.R.id.tabhost);  // The activity TabHost
//	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab


//	    spec = tabHost.newTabSpec("browse")
//	    				.setIndicator("Browse", res.getDrawable(R.drawable.ic_tab_calculate))
//	    				.setContent(new Intent(this, MainTabGroup1Activity.class));
//	    tabHost.addTab(spec);
//
//	    // Do the same for the other tabs
//	    spec = tabHost.newTabSpec("create").setIndicator("Create", res.getDrawable(R.drawable.ic_tab_history))
//	                      .setContent(new Intent(this, MainTabGroup2Activity.class));
//	    tabHost.addTab(spec);
//
//	    spec = tabHost.newTabSpec("settings")
//	    				.setIndicator("Settings", res.getDrawable(R.drawable.ic_tab_impress))
//	    				.setContent(new Intent(this, MainTabGroup3Activity.class));
//	    tabHost.addTab(spec);
	    
	    
	    tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);	
	    setupTab(new TextView(this), (String)getText(R.string.browseTabText), new Intent(this, MainTabGroup1Activity.class), extras);
	    setupTab(new TextView(this), (String)getText(R.string.createTabText), new Intent(this, MainTabGroup2Activity.class), extras);
	    setupTab(new TextView(this), (String)getText(R.string.settingsTabText), new Intent(this, MainTabGroup3Activity.class), extras);

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
        	MainTabGroup1Activity.group.back();
        }
        return true;
    }
	 
	 private void setupTab(final View view, final String tag, Intent content, Bundle extras) {
		content.putExtras(extras);
 	    View tabview = createTabView(tabHost.getContext(), tag);
 	    TabSpec setContent = tabHost.newTabSpec(tag).setIndicator(tabview).setContent(content);
 	    tabHost.addTab(setContent); //hier gibts probleme
	 	}
 	 
 	private static View createTabView(final Context context, final String text) {
 	    View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
 	    TextView tv = (TextView) view.findViewById(R.id.tabsText);
 	    tv.setText(text);
 	    return view;
 	    }
}
