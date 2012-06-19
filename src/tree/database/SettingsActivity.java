package tree.database;

import tree.database.data.Group;
import tree.database.data.User;
import tree.database.misc.GrouplistAdapter;
import tree.database.misc.SettingsTreelistAdapter;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

public class SettingsActivity extends Activity{
	
	private ImageView avatarPicture;
	
	private Button changeButton;
	private Button deleteButton;

	private User user;
	
	private DatabaseHandler dbhandle;
	
	//treelist
	private ListView myTreeListView;
	private SettingsTreelistAdapter taList;
	//grouplist
	private ListView groupListView;
	private GrouplistAdapter gaList;
	
	//dialogs
	private static final int SHOW_CHANGE_PWD_DIALOG = 0;
	private static final int SHOW_CREATE_GROUP_DIALOG = 1;
	private static final int SHOW_JOIN_GROUP_DIALOG = 2;
	private static final int SHOW_LEAVE_GROUP_DIALOG = 3;
	private Dialog change_pwd_dialog;
	private Dialog create_group_dialog;
	private Dialog join_group_dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Bundle extras = getIntent().getExtras();
		
		dbhandle = new DatabaseHandler();
		user = extras.getParcelable("UserData");
		
		avatarPicture = (ImageView) findViewById(R.id.avatarImage);
		
		changeButton = (Button)findViewById(R.id.pwdButton);
		deleteButton = (Button)findViewById(R.id.delButton);
		
		//treelist
		myTreeListView = (ListView) findViewById(R.id.myTreeList);
		//grouplist
		groupListView = (ListView) findViewById(R.id.groupList);
		
		Log.i(this.getClass().getSimpleName(), user.toString());
		user = dbhandle.getUserPicture(user);
		if(user.Avatar != null){
			setPicture(user.Avatar);
		}else{
			setPicture(BitmapFactory.decodeResource(getResources(), R.drawable.baum));
		}

		//button events
		changeButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				change_pwd_dialog = onCreateDialog(SHOW_CHANGE_PWD_DIALOG);
				onPrepareDialog(SHOW_CHANGE_PWD_DIALOG, change_pwd_dialog);
				showDialog(SHOW_CHANGE_PWD_DIALOG, change_pwd_dialog);
			}
		});
		
		//fill treelist
		taList = new SettingsTreelistAdapter(this, dbhandle.getTreeList(null, 0.0 , user, this));
		myTreeListView.setAdapter(taList);
		myTreeListView.setScrollbarFadingEnabled(true);
		
		//fill grouplist
		gaList = new GrouplistAdapter(this, dbhandle.getGroupList(user.ID), user);
		groupListView.setAdapter(gaList);
		groupListView.setScrollbarFadingEnabled(true);
		
		groupListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void setPicture(Bitmap thumbnail){
		avatarPicture.setImageBitmap(thumbnail.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true));
	}
	
	public Dialog onCreateDialog(int id){
		final Dialog dialog;
		switch(id){
		case SHOW_CHANGE_PWD_DIALOG:{
				//prepare dialog
			dialog = new Dialog(getParent());
			dialog.setContentView(R.layout.change_password);
			dialog.setTitle(getText(R.string.change_Title));
		 
			return dialog;
			}
		case SHOW_CREATE_GROUP_DIALOG:{
			//prepare dialog
		    dialog = new Dialog(getParent());
		    dialog.setTitle(R.string.create_group_title);
			break;
			}
		case SHOW_JOIN_GROUP_DIALOG:{
			dialog = new Dialog(getParent());
			break;
		}
		case SHOW_LEAVE_GROUP_DIALOG:{
			dialog = new Dialog(getParent());
		}
		}
		return null;
	}
	
	protected void onPrepareDialog(int id, final Dialog dialog, final Group group){
		super.onPrepareDialog(id, dialog);
		switch(id){
			case SHOW_CHANGE_PWD_DIALOG:{
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
				break;
			}
			case SHOW_CREATE_GROUP_DIALOG:{
				// set the custom dialog components - text, image and button
				final EditText pwd_1 = (EditText) dialog.findViewById(R.id.create_group_grouppwd);
				final EditText pwd_2 = (EditText) dialog.findViewById(R.id.create_group_grouppwd2);
				final EditText g_name = (EditText) dialog.findViewById(R.id.create_group_groupname);
				final Button create_group_button = (Button)dialog.findViewById(R.id.create_group_ok_button);
				final Button cancel_button = (Button)dialog.findViewById(R.id.create_group_cancel_button);
				
				create_group_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(pwd_1.getText().toString().equals(pwd_2.getText().toString())){
							Group g = new Group(g_name.getText().toString(), pwd_1.getText().toString());
							if(!dbhandle.addGroup(g.Name, g.Password)){
								Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
								toast.show();
							}
						}else{
							Toast toast = Toast.makeText(getParent(), getText(R.string.pwdFailure), Toast.LENGTH_LONG);
							toast.show();
						}
					}
				});
				
				cancel_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				break;
			}
			case SHOW_JOIN_GROUP_DIALOG:{
				dialog.setTitle(R.string.join_group_title + group.Name);
				
				final EditText g_pwd = (EditText) dialog.findViewById(R.id.join_group_pwd);
				final Button join_group_button = (Button)dialog.findViewById(R.id.join_group_ok_button);
				final Button cancel_button = (Button)dialog.findViewById(R.id.join_group_cancel_button);
				
				cancel_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				join_group_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(group.testPassword(g_pwd.getText().toString())){
							if(!dbhandle.addUserToGroup(group.ID, user.ID)){
								Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
								toast.show();
							}
						}else{
							Toast toast = Toast.makeText(getParent(), getText(R.string.pwdFailure), Toast.LENGTH_LONG);
							toast.show();
						}

					}
				});
				break;
			}
			case SHOW_LEAVE_GROUP_DIALOG:{
				dialog.setTitle(R.string.leave_group_title + group.Name);
				final Button leave_group_button = (Button)dialog.findViewById(R.id.leave_group_ok_button);
				final Button cancel_button = (Button)dialog.findViewById(R.id.leave_group_cancel_button);
				
				cancel_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				leave_group_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(!dbhandle.removeUserFromGroup(group.ID, user.ID)){
							Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
							toast.show();
						}
					}
				});
				break;
			}
		}
		
	}
	protected void showDialog(int id, Dialog dialog){
		switch(id){
			case SHOW_CHANGE_PWD_DIALOG:{
				dialog.show();
			}
			case SHOW_CREATE_GROUP_DIALOG:{
				dialog.show();
			}
			case SHOW_JOIN_GROUP_DIALOG:{
				dialog.show();
			}
			case SHOW_LEAVE_GROUP_DIALOG:{
				dialog.show();
			}
		}
	}
	
	
}
