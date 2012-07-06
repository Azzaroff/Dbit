package tree.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import tree.database.data.User;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class CreateUserActivity extends Activity{
	
	protected static final int CAMERA_PIC_REQUEST = 4711;
	protected static final String AVATAR_PATH = "/mnt/sdcard/treeDB/img/avatar.jpg";
	private Button photoButton;
	private Button createButton;
	private ImageView photoView;
	
	private EditText name;
	private EditText password; 
	
	private Toast toast;
	
	private Bitmap avatar;
	
	private DatabaseHandler dbhandler;
	
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user);
		
//		toast = Toast.makeText(context, text, duration);
		
		dbhandler = new DatabaseHandler();
		user = new User();
		
		photoButton = (Button)findViewById(R.id.pictureButton);
		createButton = (Button)findViewById(R.id.createUserButton);
		photoView = (ImageView)findViewById(R.id.avatarImage);
		
		name = (EditText)findViewById(R.id.nametext);
		password = (EditText)findViewById(R.id.pwdtext);
		
		//implement the buttons
		photoButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
				startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  
			}
		});
		
		createButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//test, if all fields were filled
				
				if(name.getText().length() == 0){
					toast = Toast.makeText(getApplicationContext(), getText(R.string.nameFailure) , Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				if(password.getText().length() == 0){
					toast = Toast.makeText(getApplicationContext(), getText(R.string.pwdFailure) , Toast.LENGTH_LONG);
					toast.show();
					return;
				}
				Log.i(this.getClass().getSimpleName(), "Create Button");
				File file = new File(AVATAR_PATH);
				//insert new user into database
				if(avatar != null){
					user = new User(name.getText().toString(), password.getText().toString(), 0, avatar);
					if(dbhandler.addUser(user)){
						file.delete(); // remove the picture from the sd card
						Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
						intent.putExtra("UserData", user);
						startActivity(intent);
					}else{
						toast = Toast.makeText(getApplicationContext(), getText(R.string.miscFailure) , Toast.LENGTH_LONG);
						toast.show();
					}
				}
			}
		});
		
		//show default image or, if a user image was taken, show this one
		File file = new File("/mnt/sdcard/treeDB/tmp/");
		if(!file.exists()){
			file.mkdir();
			setPicture(BitmapFactory.decodeFile("/mnt/sdcard/treeDB/img/baum.jpg"));
		}else{
			file = new File(AVATAR_PATH);
			if(!file.exists()){
				setPicture(BitmapFactory.decodeFile("/mnt/sdcard/treeDB/img/baum.jpg"));
			}else{
				avatar = BitmapFactory.decodeFile(AVATAR_PATH);
				setPicture(avatar);
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(this.getClass().getSimpleName(), "result: "+resultCode);
		Log.i(this.getClass().getSimpleName(), "request: "+requestCode);
	    if (requestCode == CAMERA_PIC_REQUEST) {
	    	Log.i(this.getClass().getSimpleName(), "camera return");
	    	avatar = (Bitmap) data.getExtras().get("data");
	    	setPicture(avatar);
	    	writePictureToStorage(avatar);
	    	
	    }
	}
	
	private void setPicture(Bitmap thumbnail){
		photoView.setImageBitmap(thumbnail.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/8*3, getWindowManager().getDefaultDisplay().getWidth()/2, true));
	}
	
	private void writePictureToStorage(Bitmap picture){
		File file = new File(AVATAR_PATH);
    	try {
			file.createNewFile();
			FileOutputStream fo = new FileOutputStream(file);
			picture.compress(Bitmap.CompressFormat.JPEG, 85, fo);
			fo.flush();
			fo.close();
			MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
