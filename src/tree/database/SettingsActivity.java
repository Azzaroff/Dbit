package tree.database;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class SettingsActivity extends Activity{
	
	private ImageView avatarPicture;
	
	private Button changeButton;
	private Button deleteButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		avatarPicture = (ImageView) findViewById(R.id.avatarImage);
		setPicture(BitmapFactory.decodeFile("/mnt/sdcard/treeDB/img/baum.jpg"));
		
		changeButton = (Button)findViewById(R.id.pwdButton);
		deleteButton = (Button)findViewById(R.id.delButton);
		
	}
	
	private void setPicture(Bitmap thumbnail){
		avatarPicture.setImageBitmap(thumbnail.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true));
	}
}
