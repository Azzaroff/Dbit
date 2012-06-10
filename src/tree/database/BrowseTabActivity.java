package tree.database;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import tree.database.data.Tree;
import tree.database.data.User;
import tree.database.misc.LazyAdapter;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
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
	
	private TextView nameText;
	private TextView longitudeText;
	private TextView latitudeText;
	private EditText addComment;
	private ListView commentList;
	
	private DatabaseHandler dbhandle;
	
	private int selectedtree;
	private ArrayList<Tree> treelist;
	
	/*list adapter for listview*/
	LazyAdapter laList = null;
	private ArrayList<HashMap<String, String>> myListing = new ArrayList<HashMap<String, String>>();
	
	//user
	User user;
	
	//GPS Location Manager
	private LocationManager locationManager;
	private GeoUpdateHandler updatehandler = new GeoUpdateHandler(); //handles gps updates
	private Location currentLocation;
	
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
		
		Gallery gallery = (Gallery) findViewById(R.id.gallery);
	    gallery.setAdapter(new ImageAdapter(this, Integer.parseInt(extras.getString("Tab"))));

	    nameText = (TextView)findViewById(R.id.nametext);
	    longitudeText = (TextView)findViewById(R.id.longtext);
	    latitudeText = (TextView)findViewById(R.id.lattext);
	    commentList = (ListView)findViewById(R.id.list);
	    addComment = (EditText)findViewById(R.id.addcomment);
	    addComment.setText(getText(R.string.commentLabel));
	    
	    //fill comment list
	    laList = new LazyAdapter(getParent(), dbhandle.getCommentList(treelist.get(selectedtree).ID));
	    // Assign adapter to ListView
	    commentList.setAdapter(laList);
	    commentList.setScrollbarFadingEnabled(true);
	    
	    //gallery on click
	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            Toast.makeText(BrowseTabActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	            
	            selectedtree = position;
	            
	            //fill properties
	            Tree tree = treelist.get(selectedtree);
	            longitudeText.setText(""+tree.Location[1]+"° "+getText(R.string.longitude));
	            latitudeText.setText(""+tree.Location[0]+"° "+getText(R.string.latitude));
	            nameText.setText(tree.Name);
	            
	            //fill comment list
	            if(tree.Comments == null){
	            	tree.Comments = dbhandle.getCommentList(tree.ID);
	            }
	            laList = new LazyAdapter(getParent(), tree.Comments);
	            commentList.setAdapter(laList);
	    	    commentList.setScrollbarFadingEnabled(true);
	        }
	    });
	    
	}
	
	public class ImageAdapter extends BaseAdapter {
	    int mGalleryItemBackground;
	    private Context mContext;

	    private File[] imageList;

	    public ImageAdapter(Context c, int tabID) {
	        mContext = c;
	        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.BrowseGallery);
	        mGalleryItemBackground = attr.getResourceId(
	                R.styleable.BrowseGallery_android_galleryItemBackground, 0);
	        attr.recycle();
	        
	        //read Images
	        File file = new File("/mnt/sdcard/treeDB/img");
	        if(file.isDirectory()){
	        	imageList = file.listFiles();
	        	Log.i(this.getClass().getSimpleName(), "is Directory");
	        }
	        
	        //get trees from DB
	        float[] location = new float[2];
	        location[0] = (float)currentLocation.getLatitude();
	        location[1] = (float)currentLocation.getLongitude();
	        treelist = dbhandle.getTreeList(location, 15, user);
	    }

	    public int getCount() {
	        return imageList.length;
	    }

	    public Object getItem(int position) {
	        return position;
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView = new ImageView(mContext);

	        //imageView.setImageResource(mImageIds[position]);
	        imageView.setImageBitmap(BitmapFactory.decodeFile(imageList[position].getAbsolutePath()));
	        
//	        imageView.setImageBitmap(treelist.get(position).Images.get(0));
	        imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
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
    	
        if(gps_enabled)
        	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, updatehandler);
        	currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(network_enabled)
        	locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, updatehandler);
        	currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        
    }
	
	public class GeoUpdateHandler implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			int lat = (int) (location.getLatitude() * 1E6);
			int lng = (int) (location.getLongitude() * 1E6);
			currentLocation = location;
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}
