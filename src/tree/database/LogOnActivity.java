package tree.database;

import tree.database.data.User;
import tree.database.misc.Connectivity;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogOnActivity extends Activity{

	private Button logonButton;
	private Button createUserButton;
	
	private EditText name;
	private EditText pwd;
	
	private User user;
	private DatabaseHandler dbhandle;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logon);
		context = this;
		
		dbhandle = new DatabaseHandler();
		
		logonButton = (Button)findViewById(R.id.logonbutton);
		createUserButton = (Button)findViewById(R.id.createuserbutton);
		
		name = (EditText)findViewById(R.id.nametext);
		pwd = (EditText)findViewById(R.id.pwdtext);
		
		//button implementation
		logonButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				//debug
//				if(user == null) user = dbhandle.getUser("Work");
				//normal
				if(user == null) user = dbhandle.getUser(name.getText().toString());
				//connection error
				if(!Connectivity.isConnected(context)){
					Toast toast = Toast.makeText(getApplicationContext(), R.string.network_error, Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				//db error
				if(user == null){
					Toast toast = Toast.makeText(getApplicationContext(), R.string.logonUserFailure, Toast.LENGTH_LONG);
					toast.show();
					pwd.setText("");
					name.setText("");
					return;
				}
				//test the password
				//debug
//				if(user.testPassword("test")){
				//normal
				if(user.testPassword(pwd.getText().toString())){
					Intent intent = new Intent(LogOnActivity.this, MainActivity.class);
					intent.putExtra("UserData", user);
					startActivity(intent);
				}else{ //password failure
					Toast toast = Toast.makeText(getApplicationContext(), R.string.logonPasswordFailure, Toast.LENGTH_LONG);
					toast.show();
					pwd.setText("");
				}
			}
		});
		
		createUserButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				Intent intent = new Intent(LogOnActivity.this, CreateUserActivity.class);
				startActivity(intent);
			}
		});
		
		pwd.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == 66) logonButton.performClick();
				return false;
			}
		});
	}
	
	@Override  
	public void onBackPressed() {  
		super.onBackPressed();
        this.finish();  
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.i(this.getClass().getSimpleName(), keyCode+"");
		if(keyCode == 82){ //menu key
			startActivity(new Intent(this, Preferences.class));
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
