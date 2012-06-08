package tree.database;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LogOnActivity extends Activity{

	private Button logonButton;
	private Button createUserButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logon);
		
		logonButton = (Button)findViewById(R.id.logonbutton);
		createUserButton = (Button)findViewById(R.id.createuserbutton);
		
		//button implementation
		logonButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LogOnActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		
		createUserButton.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LogOnActivity.this, CreateUserActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
}
