package tree.database;

import tree.database.data.User;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity{
	
	private ImageView avatarPicture;
	
	private Button changeButton;
	private Button deleteButton;

	private User user;
	
	private DatabaseHandler dbhandle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Bundle extras = getIntent().getExtras();
		
		dbhandle = new DatabaseHandler();
		
		avatarPicture = (ImageView) findViewById(R.id.avatarImage);
		setPicture(BitmapFactory.decodeFile("/mnt/sdcard/treeDB/img/baum.jpg"));
		
		changeButton = (Button)findViewById(R.id.pwdButton);
		deleteButton = (Button)findViewById(R.id.delButton);
		
		user = extras.getParcelable("UserData");
		
		Log.i(this.getClass().getSimpleName(), user.toString());
		user = dbhandle.getUserPicture(user);
		setPicture(user.Avatar);
		

		//button events
		changeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				showDialog();
			}
		});
		
	}
	
	private void setPicture(Bitmap thumbnail){
		avatarPicture.setImageBitmap(thumbnail.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true));
	}
	
	private void showDialog(){
		// custom dialog
		final Dialog dialog = new Dialog(getParent());
		dialog.setContentView(R.layout.changepassword);
		dialog.setTitle(getText(R.string.change_Title));
	 
		// set the custom dialog components - text, image and button
		final EditText old_pwd = (EditText) dialog.findViewById(R.id.changepassword_oldPwd);
		final EditText new_pwd = (EditText) dialog.findViewById(R.id.changepassword_newPwd);
		final EditText rep_new_pwd = (EditText) dialog.findViewById(R.id.changepassword_repeateNewPwd);
 
		Button okButton = (Button) dialog.findViewById(R.id.change_ok_button);
		Button cancelButton = (Button) dialog.findViewById(R.id.change_cancel_button);
		// if button is clicked, close the custom dialog
		okButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(user.testPassword(old_pwd.getText().toString())){
					if(new_pwd.getText().toString().equals(rep_new_pwd.getText().toString())){
						DatabaseHandler dbhandle = new DatabaseHandler();
						dbhandle.setUserPassword(user.ID, user.setNewPassword(new_pwd.getText().toString()));
						dialog.dismiss();
					}else{
						Log.e(getClass().getSimpleName(), "Fehler in neuem Passwort");
						new_pwd.setText("");
						rep_new_pwd.setText("");
					}
				}else{
					Log.e(getClass().getSimpleName(), "falsches Passwort");
					old_pwd.setText("");
				}
			}
		});
		
		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				old_pwd.setText("");
				new_pwd.setText("");
				rep_new_pwd.setText("");
				dialog.dismiss();
			}
		});
 
		dialog.show();
	}
}
