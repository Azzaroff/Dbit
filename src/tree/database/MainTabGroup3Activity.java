package tree.database;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainTabGroup3Activity extends ActivityGroup{
	public static MainTabGroup3Activity group;
	private ArrayList<View> history;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.history = new ArrayList<View>();
        group = this;
        
        View view = getLocalActivityManager().startActivity("Settings", 
                new Intent(this, SettingsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)).getDecorView();
        
        setContentView(view);
    }
}