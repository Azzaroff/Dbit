package tree.database;

import java.util.ArrayList;

import tree.database.data.Group;
import tree.database.data.User;
import tree.database.misc.GrouplistAdapter;
import tree.database.misc.SettingsTreelistAdapter;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
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
	
	private SharedPreferences prefs;
	
	//treelist
	private ListView myTreeListView;
	private SettingsTreelistAdapter taList;
	//grouplist
	private ListView groupListView;
	private GrouplistAdapter gaList;
	private ArrayList<Group> groups;
	
	//dialogs
	private static final int SHOW_CHANGE_PWD_DIALOG = 0;
	private static final int SHOW_CREATE_GROUP_DIALOG = 1;
	private static final int SHOW_JOIN_GROUP_DIALOG = 2;
	private static final int SHOW_LEAVE_GROUP_DIALOG = 3;
	private static final int SHOW_DELETE_USER_DIALOG = 4;
	private Dialog change_pwd_dialog;
	private Dialog create_group_dialog;
	private Dialog join_group_dialog;
	private Dialog leave_group_dialog;
	private Dialog delete_user_dialog;
	private Group selectedgroup;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		Bundle extras = getIntent().getExtras();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getParent());
		
		dbhandle = new DatabaseHandler();
		user = extras.getParcelable("UserData");
		
		avatarPicture = (ImageView) findViewById(R.id.avatarImage);
		
		changeButton = (Button)findViewById(R.id.pwdButton);
		deleteButton = (Button)findViewById(R.id.delButton);
		
		//treelist
		myTreeListView = (ListView) findViewById(R.id.myTreeList);
		//grouplist
		groupListView = (ListView) findViewById(R.id.myGroupList);
		
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
		
		deleteButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				delete_user_dialog = onCreateDialog(SHOW_DELETE_USER_DIALOG);
				onPrepareDialog(SHOW_DELETE_USER_DIALOG, delete_user_dialog);
				showDialog(SHOW_DELETE_USER_DIALOG, delete_user_dialog);
			}
		});
		
		//fill grouplist
		fillGroupList();
		groupListView.setScrollbarFadingEnabled(true);
		
		myTreeListView.setScrollbarFadingEnabled(true);
		
		groupListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long ID) {
				// TODO Auto-generated method stub
				Log.i(this.getClass().getSimpleName(), "Item Click");
				if(ID == 0){
					create_group_dialog = onCreateDialog(SHOW_CREATE_GROUP_DIALOG);
					onPrepareDialog(SHOW_CREATE_GROUP_DIALOG, create_group_dialog);
					showDialog(SHOW_CREATE_GROUP_DIALOG);
				}else{
					selectedgroup = groups.get((int)ID);
					if(selectedgroup.MemberList.contains(user.ID)){
						leave_group_dialog = onCreateDialog(SHOW_LEAVE_GROUP_DIALOG);
						onPrepareDialog(SHOW_LEAVE_GROUP_DIALOG, leave_group_dialog);
						showDialog(SHOW_LEAVE_GROUP_DIALOG);
					}else{
						join_group_dialog = onCreateDialog(SHOW_JOIN_GROUP_DIALOG);
						onPrepareDialog(SHOW_JOIN_GROUP_DIALOG, join_group_dialog);
						showDialog(SHOW_JOIN_GROUP_DIALOG);
					}
				}
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
		    dialog.setContentView(R.layout.create_group_dialog);
		    return dialog;
			}
		case SHOW_JOIN_GROUP_DIALOG:{
			dialog = new Dialog(getParent());
			dialog.setContentView(R.layout.join_group_dialog);
			return dialog;
		}
		case SHOW_LEAVE_GROUP_DIALOG:{
			dialog = new Dialog(getParent());
			dialog.setContentView(R.layout.leave_group_dialog);
			return dialog;
		}
		case SHOW_DELETE_USER_DIALOG:{
			dialog = new Dialog(getParent());
			dialog.setContentView(R.layout.delete_user_dialog);
			return dialog;
		}
		}
		return null;
	}
	
	protected void onPrepareDialog(int id, final Dialog dialog){
//		super.onPrepareDialog(id, dialog);
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
							if(!dbhandle.addGroup(user.ID, g.Name, g.Password)){
								Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
								toast.show();
							}else{
								fillGroupList();
								dialog.dismiss();
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
				dialog.setTitle(getText(R.string.join_group_title) +" "+ selectedgroup.Name);
				
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
						if(selectedgroup.testPassword(g_pwd.getText().toString())){
							if(!dbhandle.addUserToGroup(selectedgroup.ID, user.ID)){
								Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
								toast.show();
							}else{
								fillGroupList();
								dialog.dismiss();
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
				dialog.setTitle(getText(R.string.leave_group_title) +" "+ selectedgroup.Name);
				final Button leave_group_button = (Button)dialog.findViewById(R.id.leave_group_ok_button);
				final Button cancel_button = (Button)dialog.findViewById(R.id.leave_group_cancel_button);
				
				cancel_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				leave_group_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(!dbhandle.removeUserFromGroup(selectedgroup.ID, user.ID)){
							Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
							toast.show();
						}else{
							fillGroupList();
							dialog.dismiss();
						}
					}
				});
				break;
			}
			case SHOW_DELETE_USER_DIALOG:{
				dialog.setTitle(getText(R.string.delete_user_title));
				final Button delete_button = (Button)dialog.findViewById(R.id.delete_user_ok_button);
				final Button cancel_button = (Button)dialog.findViewById(R.id.delete_user_cancel_button);
				
				cancel_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				delete_button.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(!dbhandle.deleteUser(user.ID)){
							Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
							toast.show();
						}else{
							dialog.dismiss();
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
			case SHOW_DELETE_USER_DIALOG:{
				dialog.show();
			}
		}
	}
	
	protected void fillGroupList(){
		groups = dbhandle.getGroupList(user.ID);
		gaList = new GrouplistAdapter(this, groups, user);
		groupListView.setAdapter(gaList);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//fill treelist
		taList = new SettingsTreelistAdapter(this, dbhandle.getUsersTreeList(user, this));
		myTreeListView.setAdapter(taList);	
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getSimpleName(), keyCode+"");
		if(keyCode == 82){ //menu key
			startActivity(new Intent(getParent(), Preferences.class));
		}
		return super.onKeyDown(keyCode, event);
	}
}
