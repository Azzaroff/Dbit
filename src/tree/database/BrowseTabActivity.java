package tree.database;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import tree.database.data.Comment;
import tree.database.data.Tree;
import tree.database.data.User;
import tree.database.misc.LazyAdapter;
import tree.database.services.DatabaseHandler;
import android.R.dimen;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BrowseTabActivity extends Activity{

	private static final int NEARTAB = 1;
	private static final int RECENTTAB = 1;
	private static final int ALLTAB = 2;	
	
	private Gallery gallery;
	
	private TextView nameText;
	private TextView longitudeText;
	private TextView latitudeText;
	private TextView sizeText;
	private TextView ageText;
	private EditText addComment;
	private ListView commentList;
	
	private DatabaseHandler dbhandle;
	
	private int selectedtree = 0;
	private ArrayList<Tree> treelist = new ArrayList<Tree>();
	
	/*list adapter for listview*/
	LazyAdapter laList = null;
	private ArrayList<HashMap<String, String>> myListing = new ArrayList<HashMap<String, String>>();
	
	//user
	User user;
	
	//GPS Location Manager
	private LocationManager locationManager;
	private GeoUpdateHandler updatehandler = new GeoUpdateHandler(); //handles gps updates
	private Location currentLocation;
	
	//image display dialog
	private Dialog image_dialog;
	private ImageView image_dialog_image;
	
	//dialog IDs
	private static final int SHOW_PICTURE_DIALOG = 0;
	
	//display metrics
	DisplayMetrics displaymetrics;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browsetab); 
		
		Bundle extras = getIntent().getExtras();
		dbhandle = new DatabaseHandler();
		user = extras.getParcelable("UserData");

		//gets the current location
		updateLocation();
		
		//get display dimensions
		displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		nameText = (TextView)findViewById(R.id.nametext);
	    longitudeText = (TextView)findViewById(R.id.browse_longtext);
	    latitudeText = (TextView)findViewById(R.id.browse_lattext);
	    sizeText = (TextView)findViewById(R.id.browse_sizetext);
	    ageText = (TextView)findViewById(R.id.browse_agetext);
	    commentList = (ListView)findViewById(R.id.list);
	    addComment = (EditText)findViewById(R.id.addcomment);
	    
	    gallery = (Gallery) findViewById(R.id.gallery);
	    gallery.setAdapter(new ImageAdapter(this, Integer.parseInt(extras.getString("Tab")), displaymetrics));
	    gallery.setLongClickable(true);

//	    fill comment list
	    if(treelist.size() > 0){
	    	laList = new LazyAdapter(this, dbhandle.getCommentList(treelist.get(selectedtree).ID));
	    }else{
	    	laList = new LazyAdapter(this, new ArrayList<Comment>());
	    }
	    
//	     Assign adapter to ListView
	    commentList.setAdapter(laList);
	    commentList.setScrollbarFadingEnabled(true);
	    
	    //gallery on click
	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            Toast.makeText(BrowseTabActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	            
	            selectedtree = position;
	            
	           showTreeInfo(position);
	           laList = new LazyAdapter(getParent(), dbhandle.getCommentList(treelist.get(position).ID));
	           commentList.setAdapter(laList);
	        }
	    });
	    
	    gallery.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				Toast toast = Toast.makeText(getParent(), "long click - do something useful here", Toast.LENGTH_LONG);
				toast.show();
				//debug
				Log.i(this.getClass().getSimpleName(), "long click");
				//prepare dialog
				image_dialog.setTitle(treelist.get((int)gallery.getSelectedItemId()).Name);
				image_dialog_image.setImageBitmap(treelist.get((int)gallery.getSelectedItemId()).Images.get(0));
				//show dialog
				image_dialog.show();
				
				return false;
			}
		});
	    
	    gallery.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Toast toast = Toast.makeText(getParent(), "long item click - do something useful here", Toast.LENGTH_LONG);
				toast.show();
				//debug
				Log.i(this.getClass().getSimpleName(), "long click");
				onPrepareDialog(SHOW_PICTURE_DIALOG, image_dialog);
				//show dialog
				showDialog(SHOW_PICTURE_DIALOG);
				return false;
			}
		});
	    
	    addComment.setOnKeyListener(new OnKeyListener() {
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == 66){
					if(saveComment()){
						addComment.setText(getParent().getText(R.string.commentLabel));
						addComment.clearFocus();
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(addComment.getWindowToken(), 0);
						laList = new LazyAdapter(getParent(), dbhandle.getCommentList(treelist.get(selectedtree).ID));
				        commentList.setAdapter(laList);
					}else{
						Toast toast = Toast.makeText(getParent(), getParent().getText(R.string.db_error), Toast.LENGTH_LONG);
						toast.show();
					}
					
				}
				return false;
			}
		});
	    image_dialog = onCreateDialog(SHOW_PICTURE_DIALOG);
	}
	
	private void showTreeInfo(int selectedTree){
		 //fill properties
        Tree tree = treelist.get(selectedTree);
        if(tree.Location[0] == Double.MAX_VALUE){
        	longitudeText.setText(getText(R.string.no_location).toString()+" "+getText(R.string.longitude).toString());
        	latitudeText.setText(getText(R.string.no_location).toString()+" "+getText(R.string.latitude).toString());
        }else{
        	DecimalFormat df = new DecimalFormat("0.000000");
        	longitudeText.setText(""+df.format(tree.Location[1])+"° "+getText(R.string.longitude).toString());
        	latitudeText.setText(""+df.format(tree.Location[0])+"° "+getText(R.string.latitude).toString());
        }
        nameText.setText(tree.Name);
        Log.i(this.getClass().getSimpleName(), "klappt");
        sizeText.setText(""+tree.Size);
        ageText.setText(""+tree.Age);
        //fill comment list
        if(tree.Comments == null){
        	tree.Comments = dbhandle.getCommentList(tree.ID);
        }
        laList = new LazyAdapter(getParent(), tree.Comments);
        commentList.setAdapter(laList);
//	    Utilities.setListViewHeightBasedOnChildren(commentList);
	}
	
	protected Dialog onCreateDialog(int id){
		final Dialog dialog;
		switch(id){
		case SHOW_PICTURE_DIALOG:{
			//prepare dialog
		    dialog = new Dialog(getParent().getParent().getParent());
		    dialog.setContentView(R.layout.picture_dialog);
		    dialog.setCancelable(true);
		    Button dialog_button = (Button) dialog.findViewById(R.id.picture_dialog_button);
		    
		    dialog_button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		    return dialog;
		}
			
		}
		return null;
	}
	
	protected void onPrepareDialog(int id, Dialog dialog){
		super.onPrepareDialog(id, dialog);
		switch(id){
			case SHOW_PICTURE_DIALOG:{
				dialog.setTitle(treelist.get((int)gallery.getSelectedItemId()).Name);
				ImageView dialog_image = (ImageView) dialog.findViewById(R.id.picture_dialog_image);
				dialog_image.setImageBitmap(Bitmap.createScaledBitmap(treelist.get((int)gallery.getSelectedItemId()).Images.get(0), (int)(displaymetrics.widthPixels*0.9f), (int)(displaymetrics.widthPixels*0.9)/3*4, true));
				dialog_image.setScaleType(ImageView.ScaleType.FIT_XY);
				break;
			}
		}
		
	}
	
	private boolean saveComment(){
		Date d = new Date();
		Comment c = new Comment(user, d.getTime(), addComment.getText().toString());
		return dbhandle.addComment(treelist.get(selectedtree).ID, c);
	}
	
	public class ImageAdapter extends BaseAdapter {
	    int mGalleryItemBackground;
	    private Context mContext;
	    private int displaywidth = 0;
	    
//	    private File[] imageList;
	    private ArrayList<Bitmap> imageList;

	    public ImageAdapter(Context c, int tabID, DisplayMetrics displaymetrics) {
	        mContext = c;
	        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.BrowseGallery);
	        mGalleryItemBackground = attr.getResourceId(
	                R.styleable.BrowseGallery_android_galleryItemBackground, 0);
	        attr.recycle();
	        displaywidth = displaymetrics.widthPixels;
	        
	        //read Images
//	        File file = new File("/mnt/sdcard/treeDB/img");
//	        if(file.isDirectory()){
//	        	imageList = file.listFiles();
//	        	Log.i(this.getClass().getSimpleName(), "is Directory");
//	        }
	        
	        //get trees from DB
	        float[] location = new float[2];
	        if(currentLocation != null){
	        	location[0] = (float)currentLocation.getLatitude();
	        	location[1] = (float)currentLocation.getLongitude();
	        }
	        treelist = dbhandle.getTreeList(location, 15, user, getParent());
	        
	        imageList = new ArrayList<Bitmap>();
	        for(Tree t : treelist){
	        	for(Bitmap b : t.Images){
	        		imageList.add(b);
	        	}
	        }
	        //show tree infos
	        showTreeInfo(0);
	    }

	    public int getCount() {
	        return imageList.size();
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView = new ImageView(mContext);

//	        imageView.setImageResource(mImageIds[position]);
//	        imageView.setImageBitmap(BitmapFactory.decodeFile(imageList[position].getAbsolutePath()));
	        imageView.setImageBitmap(imageList.get(position));
	        
//	        imageView.setImageBitmap(treelist.get(position).Images.get(0));
	        imageView.setLayoutParams(new Gallery.LayoutParams((displaywidth/3), (displaywidth/9*4)));
	        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	        imageView.setBackgroundResource(mGalleryItemBackground);

	        return imageView;
	    }
	}
	
	/**
     * sets the map to the current location
     */
    private void updateLocation(){
    	
    	boolean gps_enabled = false;
    	boolean network_enabled = false;

    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    	
    	try{gps_enabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}
        
        if(gps_enabled){
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, updatehandler);
        	currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if(network_enabled){
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, updatehandler);
        	currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
    }
	
	public class GeoUpdateHandler implements LocationListener {

		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			currentLocation = location;
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
