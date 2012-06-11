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
import android.media.ExifInterface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class CreateActivity extends Activity{
	
	private ImageView photoView;
	private Button takePictureButton;
	
	private EditText nameText;
	private EditText ageText;
	private EditText sizeText;
	
	private ToggleButton locationButton;
	private Button createButton;
	
	private static final int CAMERA_PIC_REQUEST = 4711;
	protected static final String TREE_PATH = "/mnt/sdcard/treeDB/tmp/tree.jpg";
	protected static final String TMP_PATH = "/mnt/sdcard/treeDB/tmp/";
	
	private Bitmap picture;
	
	private DatabaseHandler dbhandle;
	
	private User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create);
		
		Bundle extras = getIntent().getExtras();
		
		user = extras.getParcelable("UserData");

		dbhandle = new DatabaseHandler();
		
		photoView = (ImageView) findViewById(R.id.pictureView);
		setPicture(picture);
		
		takePictureButton = (Button) findViewById(R.id.pictureButton);
		locationButton = (ToggleButton) findViewById(R.id.locationToggle);
		createButton = (Button) findViewById(R.id.createButton);
		
		nameText = (EditText) findViewById(R.id.nametext);
		ageText = (EditText) findViewById(R.id.agetext);
		sizeText = (EditText) findViewById(R.id.sizetext);
		
		locationButton = (ToggleButton) findViewById(R.id.locationToggle);
		createButton = (Button) findViewById(R.id.createButton);
		
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				takePicture();
			}
		});
		
		createButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//teste, ob alles ausgefüllt wurde
				float[] location = new float[2];
				try {
					ExifInterface exif = new ExifInterface(TREE_PATH);
					if(picture == null){
						Toast toast = Toast.makeText(getParent(), getText(R.string.no_picture), Toast.LENGTH_LONG);
						toast.show();
						return;
					}
					if(nameText.getText().toString().length() < 1){
						Toast toast = Toast.makeText(getParent(), getText(R.string.no_name), Toast.LENGTH_LONG);
						toast.show();
						return;
					}
					if(sizeText.getText().toString().length() < 1){
						Toast toast = Toast.makeText(getParent(), getText(R.string.no_size), Toast.LENGTH_LONG);
						toast.show();
						return;
					}
					if(ageText.getText().toString().length() < 1){
						Toast toast = Toast.makeText(getParent(), getText(R.string.no_age), Toast.LENGTH_LONG);
						toast.show();
						return;
					}
					if(locationButton.isChecked()){
						if(!exif.getLatLong(location)){
							Toast toast = Toast.makeText(getParent(), getText(R.string.no_loc_support), Toast.LENGTH_LONG);
							toast.show();
							locationButton.performClick();
							location = null;
						}
					}else{
						location = null;
					}
					if(!dbhandle.addTree(user.ID, picture, nameText.getText().toString(), location, Double.parseDouble(sizeText.getText().toString()), Integer.parseInt(ageText.getText().toString()))){
						Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
						toast.show();
					}else{
						finish();
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
	}
	
	public void takePicture(){
		Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		getParent().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);  
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(this.getClass().getSimpleName(), "result: "+resultCode);
		Log.i(this.getClass().getSimpleName(), "request: "+requestCode);
	    if (requestCode == CAMERA_PIC_REQUEST) {
	    	Log.i(this.getClass().getSimpleName(), "camera return");
	    	picture = (Bitmap) data.getExtras().get("data");
	    	writePictureToStorage(picture);
	    	setPicture(picture);
	    }
	}
	
	private void setPicture(Bitmap thumbnail){
		if(thumbnail == null){
			//ew wurde noch kein Foto gemacht
			File file = new File(TREE_PATH);
			if(file.exists()){
				//aber es liegt eines auf der sd karte
				picture = BitmapFactory.decodeFile(file.getAbsolutePath());
				photoView.setImageBitmap(Bitmap.createScaledBitmap(picture, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true));
			}else{
				//es liegt auch keines auf der sd karte, zeige ein standart bild
				thumbnail = BitmapFactory.decodeFile("/mnt/sdcard/treeDB/img/baum.jpg");
				photoView.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true));
			}
		}else{
			//es wurde ein foto gemacht, zeige es
			photoView.setImageBitmap(Bitmap.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true));
		}
		
	}
	
	private void writePictureToStorage(Bitmap picture){
		File folder = new File(TMP_PATH);
		if(!folder.exists()){
			folder.mkdir();
			Log.i(this.getClass().getSimpleName(), "create Folder "+TMP_PATH);
		}
		File file = new File(TREE_PATH);
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
