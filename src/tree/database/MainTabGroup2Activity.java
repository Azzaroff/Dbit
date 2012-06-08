package tree.database;

import java.util.ArrayList;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

//public class TabGroup2Activity extends TabGroupActivity {
//	@Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        startChildActivity("Historie", new Intent(this,HistoryActivity.class));
//    }
//}
public class MainTabGroup2Activity extends ActivityGroup{
	public static MainTabGroup2Activity group;
	private ArrayList<View> history;
	
	private static final int CAMERA_PIC_REQUEST = 4711;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.history = new ArrayList<View>();
        group = this;
        
        Intent intent = new Intent(this, CreateActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(getIntent().getExtras());
        View view = getLocalActivityManager().startActivity("Create", intent).getDecorView();
        
        setContentView(view);
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Route the data to the subactivity
		((CreateActivity)(getLocalActivityManager().getCurrentActivity())).onActivityResult(requestCode, resultCode, data);
	}
	  
}