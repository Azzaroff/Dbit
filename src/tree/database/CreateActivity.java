package tree.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import tree.database.data.Tree;
import tree.database.data.User;
import tree.database.misc.GpsHandler;
import tree.database.misc.TreelistDialogTreelistAdapter;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
	private Button selectButton;
	
	private CheckBox addCheckBox;
	
	private static final int CAMERA_PIC_REQUEST = 4711;
	protected static final String TREE_PATH = "/mnt/sdcard/treeDB/tmp/tree.jpg";
	protected static final String TMP_PATH = "/mnt/sdcard/treeDB/tmp/";
	
	private Bitmap picture;
	
	private DatabaseHandler dbhandle;
	private GpsHandler gpshandle;
	
	private User user;
	
	private static final int SHOW_TREELIST_DIALOG = 0;
	private Dialog treelistDialog;
	private Tree selectedTree = null;
	private Tree localSelectedTree = null;
	
	private SharedPreferences prefs;
	
	//camera intent stuff
	Uri imageUri;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_tree);
		
		Bundle extras = getIntent().getExtras();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getParent());
		
		user = extras.getParcelable("UserData");

		dbhandle = new DatabaseHandler();
		gpshandle = new GpsHandler(getParent());
		
		photoView = (ImageView) findViewById(R.id.pictureView);
		setPicture(picture);
		
		takePictureButton = (Button) findViewById(R.id.pictureButton);
		locationButton = (ToggleButton) findViewById(R.id.locationToggle);
		createButton = (Button) findViewById(R.id.create_tree_create_button);
		selectButton = (Button) findViewById(R.id.create_tree_select_button);
		selectButton.setEnabled(false);
		
		addCheckBox = (CheckBox) findViewById(R.id.create_tree_checkbox);
		
		nameText = (EditText) findViewById(R.id.nametext);
		ageText = (EditText) findViewById(R.id.agetext);
		sizeText = (EditText) findViewById(R.id.sizetext);
		
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
					if(picture == null){
						Toast toast = Toast.makeText(getParent(), getText(R.string.no_picture), Toast.LENGTH_LONG);
						toast.show();
						return;
					}
					if(addCheckBox.isChecked()){ //add new tree
						ExifInterface exif = new ExifInterface(TREE_PATH);
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
								//there are no gps information in the pictures
								Location l = gpshandle.updateLocation();
								if(l != null){
									location[0] = (float) l.getLatitude();
									location[1] = (float) l.getLongitude();
								}else{ //we can not get any information from the gps sensor, may we have to wait?
									Toast toast = Toast.makeText(getParent(), getText(R.string.no_loc_support), Toast.LENGTH_LONG);
									toast.show();
									locationButton.performClick();
									location = null;
								}						
							}
						}else{
							location = null;
						}
						if(!dbhandle.addTree(user.ID, picture, nameText.getText().toString(), location, Double.parseDouble(sizeText.getText().toString()), Integer.parseInt(ageText.getText().toString()))){
							Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
							toast.show();
						}else{
							File file = new File(TREE_PATH);
							if(file.exists()) file.delete();
							
							Toast toast = Toast.makeText(getParent(), getText(R.string.db_success), Toast.LENGTH_LONG);
							toast.show();
							
							picture = null;
							setPicture(picture);
							
							//go back to main
						}
					}else{ //add picture to existing tree
						if(selectedTree == null){
							//no tree selected
							Toast toast = Toast.makeText(getParent(), getText(R.string.tree_selection_error), Toast.LENGTH_LONG);
							toast.show();
						}else{
							if(!dbhandle.addTreeImage(user.ID, selectedTree.ID, picture)){
								Toast toast = Toast.makeText(getParent(), getText(R.string.db_error), Toast.LENGTH_LONG);
								toast.show();
							}else{
								File file = new File(TREE_PATH);
								if(file.exists()) file.delete();
								
								Toast toast = Toast.makeText(getParent(), getText(R.string.db_success), Toast.LENGTH_LONG);
								toast.show();
								
								picture = null;
								setPicture(picture);
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		addCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				nameText.setEnabled(isChecked);
				ageText.setEnabled(isChecked);
				sizeText.setEnabled(isChecked);
				selectButton.setEnabled(!isChecked);
				locationButton.setEnabled(isChecked);
				if(isChecked){ //add new tree
					if(selectedTree != null){
						nameText.setText("");
						ageText.setText("");
						sizeText.setText("");
						locationButton.setSelected(false);
					}
				}else{ //add picture to existing tree
					if(selectedTree != null){
						nameText.setText(selectedTree.Name);
						ageText.setText(""+selectedTree.Age);
						sizeText.setText(""+selectedTree.Size);
						if(selectedTree.Location[0] < Double.MAX_VALUE){
							locationButton.setSelected(true);
						}else{
							locationButton.setSelected(false);
						}
					}
				}
			}
		});
		
		addCheckBox.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(this.getClass().getSimpleName(), "checkbox");
			}
		});
		
		selectButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				treelistDialog = onCreateDialog(SHOW_TREELIST_DIALOG);
				onPrepareDialog(SHOW_TREELIST_DIALOG, treelistDialog);
				showDialog(SHOW_TREELIST_DIALOG, treelistDialog);
			}
		});
	}
	
	public void takePicture(){
		ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		getParent().startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(this.getClass().getSimpleName(), "result: "+resultCode);
		Log.i(this.getClass().getSimpleName(), "request: "+requestCode);
	    if (requestCode == CAMERA_PIC_REQUEST) {
	    	Log.i(this.getClass().getSimpleName(), "camera return");
	    	if (resultCode == Activity.RESULT_OK) {
                try {
                    picture = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageUri);
                    picture = BitmapFactory.decodeFile(getRealPathFromURI(imageUri));
                    setPicture(picture);
                    if(picture != null){
        	    		picture = writePictureToStorage(picture);
        	    	}
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
	    	
	    }
	}
	
	private void setPicture(Bitmap thumbnail){
		Matrix matrix = new Matrix();
		matrix.preRotate(90);
		if(thumbnail == null){
			//ew wurde noch kein Foto gemacht
			File file = new File(TREE_PATH);
			if(file.exists()){
				//aber es liegt eines auf der sd karte
				picture = BitmapFactory.decodeFile(file.getAbsolutePath());
				photoView.setImageBitmap(Bitmap.createBitmap(Bitmap.createScaledBitmap(picture, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true), 0, 0, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, matrix, true));
			}else{
				//es liegt auch keines auf der sd karte, zeige ein standart bild
				thumbnail = BitmapFactory.decodeFile("/mnt/sdcard/treeDB/img/baum.jpg");
				photoView.setImageBitmap(Bitmap.createScaledBitmap(thumbnail,  getWindowManager().getDefaultDisplay().getWidth()/8*3, getWindowManager().getDefaultDisplay().getWidth()/2, true));
			}
		}else{
			//es wurde ein foto gemacht, zeige es
			photoView.setImageBitmap(Bitmap.createBitmap(Bitmap.createScaledBitmap(thumbnail, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, true), 0, 0, getWindowManager().getDefaultDisplay().getWidth()/2, getWindowManager().getDefaultDisplay().getWidth()/8*3, matrix, true));
		}
		
	}
	
	private Bitmap writePictureToStorage(Bitmap picture){
		File folder = new File(TMP_PATH);
		if(!folder.exists()){
			folder.mkdir();
			Log.i(this.getClass().getSimpleName(), "create Folder "+TMP_PATH);
		}
		//scale bitmap
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
    	return picture;
	}
	
	public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	protected Dialog onCreateDialog(int id){
		final Dialog dialog;
		switch(id){
		case SHOW_TREELIST_DIALOG:{
			//prepare dialog
			dialog = new Dialog(getParent());
			dialog.setContentView(R.layout.treelist_dialog);
			dialog.setTitle(getText(R.string.treelist_dialog_title));
			return dialog;
		}
		};
		return null;
	}
	
	protected void onPrepareDialog(int id, final Dialog dialog){
//		super.onPrepareDialog(id, dialog);
		switch(id){
			case SHOW_TREELIST_DIALOG:{
				final ListView treelistview = (ListView) dialog.findViewById(R.id.treelist_dialog_TreeList);
				//get treelist
				Location l = gpshandle.updateLocation();
				float loc[] = new float[2];
				if(l != null){
					loc[0] = (float)l.getLatitude();
					loc[1] = (float)l.getLongitude();
				}
				final ArrayList<Tree> trees = dbhandle.getTreeListWithRadius(loc, prefs.getInt("distance", 5), user, getParent(), prefs.getInt("time", 5));
				//set treelist adapter
				final TreelistDialogTreelistAdapter tdtaList = new TreelistDialogTreelistAdapter(this, trees);
				if(selectedTree != null) tdtaList.setSelectedTree(selectedTree);
				treelistview.setAdapter(tdtaList);
				
				//set treelist clicklistener
				treelistview.setOnItemClickListener(new OnItemClickListener() {
					
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long ID) {
						// TODO Auto-generated method stub
						localSelectedTree = trees.get((int)ID);
						tdtaList.setSelectedTree(localSelectedTree);
						tdtaList.notifyDataSetChanged();
					}
				});
				
				Button okButton = (Button) dialog.findViewById(R.id.treelist_dialog_ok_button);
				Button cancelButton = (Button) dialog.findViewById(R.id.treelist_dialog_cancel_button);
				// if button is clicked, close the custom dialog
				okButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						if(localSelectedTree != null){
							selectedTree = localSelectedTree;
							nameText.setText(selectedTree.Name);
							sizeText.setText(""+selectedTree.Size);
							ageText.setText(""+selectedTree.Age);
							dialog.dismiss();
						}else{
							Toast toast = Toast.makeText(getParent(), getText(R.string.tree_selection_error), Toast.LENGTH_LONG);
							toast.show();
						}
					}
				});
				
				cancelButton.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						localSelectedTree = null;
						dialog.dismiss();
					}
				});
				break;
			}
		};
	}
			
	protected void showDialog(int id, Dialog dialog){
		switch(id){
			case SHOW_TREELIST_DIALOG:{
				dialog.show();
			}
		};
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
