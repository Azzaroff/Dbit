package tree.database;

import tree.database.data.User;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logon);
		
		dbhandle = new DatabaseHandler();
		
		logonButton = (Button)findViewById(R.id.logonbutton);
		createUserButton = (Button)findViewById(R.id.createuserbutton);
		
		name = (EditText)findViewById(R.id.nametext);
		pwd = (EditText)findViewById(R.id.pwdtext);
		
		//button implementation
		logonButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				if(user == null) user = dbhandle.getUser(name.getText().toString());
				//test the password
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
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LogOnActivity.this, CreateUserActivity.class);
				startActivity(intent);
			}
		});
		
		pwd.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == 66) logonButton.performClick();
				return false;
			}
		});
	}
	
	
}
