package tree.database;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create);
		
		photoView = (ImageView) findViewById(R.id.pictureView);
		setPicture(BitmapFactory.decodeFile("/mnt/sdcard/treeDB/img/baum.jpg"));
		
		takePictureButton = (Button) findViewById(R.id.pictureButton);
		
		nameText = (EditText) findViewById(R.id.nametext);
		ageText = (EditText) findViewById(R.id.agetext);
		sizeText = (EditText) findViewById(R.id.sizetext);
		
		locationButton = (ToggleButton) findViewById(R.id.locationToggle);
		createButton = (Button) findViewById(R.id.createButton);
		
		takePictureButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePicture();
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
	    	setPicture((Bitmap) data.getExtras().get("data"));
	    }
	}
	
	private void setPicture(Bitmap thumbnail){
		photoView.setImageBitmap(thumbnail.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true));
	}
	
}
