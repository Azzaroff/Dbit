package tree.database;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import tree.database.data.Comment;
import tree.database.data.Tree;
import tree.database.data.User;
import tree.database.misc.Connectivity;
import tree.database.misc.GpsHandler;
import tree.database.misc.LazyBrowseAdapter;
import tree.database.services.DatabaseHandler;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

	private EditText searchBar;
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
	private ArrayList<Tree> oldtreelist = new ArrayList<Tree>();
	
	/*list adapter for listview*/
	LazyBrowseAdapter laList = null;
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
	
	//preferences
	SharedPreferences prefs;
	
	//gps infos
	GpsHandler gpshandle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_tab); 
		
		final Bundle extras = getIntent().getExtras();
		dbhandle = new DatabaseHandler();
		user = extras.getParcelable("UserData");
		
		//get the preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(getParent());

		//gets the current location
		gpshandle = new GpsHandler(getParent());
		gpshandle.updateLocation();
		
		//get display dimensions
		displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        
        searchBar = (EditText)findViewById(R.id.browse_search);
		nameText = (TextView)findViewById(R.id.nametext);
	    longitudeText = (TextView)findViewById(R.id.browse_longtext);
	    latitudeText = (TextView)findViewById(R.id.browse_lattext);
	    sizeText = (TextView)findViewById(R.id.browse_sizetext);
	    ageText = (TextView)findViewById(R.id.browse_agetext);
	    commentList = (ListView)findViewById(R.id.list);
	    addComment = (EditText)findViewById(R.id.addcomment);
	    
	    gallery = (Gallery) findViewById(R.id.gallery);
//	    gallery.setAdapter(new ImageAdapter(this, Integer.parseInt(extras.getString("Tab")), displaymetrics));
	    gallery.setLongClickable(true);

////	    fill comment list
//	    if(treelist.size() > 0){
//	    	laList = new LazyBrowseAdapter(this, dbhandle.getCommentList(treelist.get(selectedtree).ID));
//	    }else{
//	    	laList = new LazyBrowseAdapter(this, new ArrayList<Comment>());
//	    }
//	    
////	     Assign adapter to ListView
//	    commentList.setAdapter(laList);
	    commentList.setScrollbarFadingEnabled(true);
	    
	    //gallery on click
	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            Toast.makeText(BrowseTabActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	            
	            selectedtree = getSelectedTree(position);
	            
	           showTreeInfo(position);
	           laList = new LazyBrowseAdapter(getParent(), dbhandle.getCommentList(treelist.get(position).ID), user);
	           commentList.setAdapter(laList);
	        }
	    });
	    
	    gallery.setOnLongClickListener(new OnLongClickListener() {
			
			public boolean onLongClick(View v) {
				//debug
				Log.i(this.getClass().getSimpleName(), "long click");
				//prepare dialog
				image_dialog.setTitle(treelist.get(selectedtree).Name);
				//get the right picture to show
				Bitmap high_res_image = dbhandle.getHighResPicture(treelist.get(selectedtree).ID, getSelectedTreeImage(gallery.getSelectedItemPosition()), Connectivity.getConnectionSpeed(getParent()));
				if(high_res_image != null){
					image_dialog_image.setImageBitmap(high_res_image);
				}else{
					image_dialog_image.setImageBitmap(treelist.get(selectedtree).Images.get(getSelectedTreeImage(gallery.getSelectedItemPosition())));
				}
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
					if(user.Rights >= User.READ_WRITE_COMMENTS){
						if(saveComment()){
							addComment.setText("");
							addComment.clearFocus();
							InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(addComment.getWindowToken(), 0);
							laList = new LazyBrowseAdapter(getParent(), dbhandle.getCommentList(treelist.get(selectedtree).ID), user);
					        commentList.setAdapter(laList);
						}else{
							Toast toast = Toast.makeText(getParent(), getParent().getText(R.string.db_error), Toast.LENGTH_LONG);
							toast.show();
						}
					}else{
						addComment.setText("");
						addComment.clearFocus();
						InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(addComment.getWindowToken(), 0);
						Toast toast = Toast.makeText(getParent(), getParent().getText(R.string.no_rights_comment_write), Toast.LENGTH_LONG);
						toast.show();
					}
					
				}
				return false;
			}
		});
	    image_dialog = onCreateDialog(SHOW_PICTURE_DIALOG);
	    
	    searchBar.setOnKeyListener(new OnKeyListener() {
			
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(searchBar.getText().toString().length() == 0){
					if(oldtreelist.size() > 0){
						treelist.clear();
						treelist.addAll(oldtreelist);
					}else if(treelist.size()> 0){
						oldtreelist.addAll(treelist);
					}
				}else{
					ArrayList<Tree> treesSubList = new ArrayList<Tree>();
					for(Tree t : treelist){
						if(t.Name.startsWith(searchBar.getText().toString())){
							treesSubList.add(t);
						}
					}
					Collections.sort(treesSubList);
					if(oldtreelist.size() == 0)	oldtreelist.addAll(treelist); //store the old treelist
					treelist.clear();
					treelist.addAll(treesSubList);
				}
				//fill gallery
				gallery.setAdapter(new ImageAdapter(getParent(), Integer.parseInt(extras.getString("Tab")), displaymetrics, treelist));
				//fill comment list
				if(treelist.size() > 0){
			    	laList = new LazyBrowseAdapter(getParent(), dbhandle.getCommentList(treelist.get(0).ID), user);
			    }else{
			    	laList = new LazyBrowseAdapter(getParent(), new ArrayList<Comment>(), user);
			    }
				return false;
			}
		});
	}
	
	private void showTreeInfo(int selectedTree){
		if(treelist.size() == 0) return;
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
        laList = new LazyBrowseAdapter(getParent(), tree.Comments, user);
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
	        treelist = dbhandle.getTreeList(location, prefs.getInt("distance", 5), user, getParent(), prefs.getInt("time", 5));

	        //if there are no trees, a dummy tree will be inserted
	        if(treelist.size() == 0){
	        	Tree t = new Tree(0, getText(R.string.no_tree).toString(), 0, 0.0, new Double[]{0.0,0.0},
	        			new java.sql.Timestamp((new Date()).getTime()));
	        	t.Images.add(BitmapFactory.decodeResource(getResources(), R.drawable.baum));
	        	treelist.add(t);
	        }
	        
			imageList = new ArrayList<Bitmap>();
	        for(Tree t : treelist){
	        	for(Bitmap b : t.Images){
	        		imageList.add(b);
	        	}
	        }
	        //show tree infos
	        showTreeInfo(0);
	    }
	    
	    public ImageAdapter(Context c, int tabID, DisplayMetrics displaymetrics, ArrayList<Tree> treelist) {
	        mContext = c;
	        TypedArray attr = mContext.obtainStyledAttributes(R.styleable.BrowseGallery);
	        mGalleryItemBackground = attr.getResourceId(
	                R.styleable.BrowseGallery_android_galleryItemBackground, 0);
	        attr.recycle();
	        displaywidth = displaymetrics.widthPixels;
	        
	        //get trees from DB
	        float[] location = new float[2];
	        if(currentLocation != null){
	        	location[0] = (float)currentLocation.getLatitude();
	        	location[1] = (float)currentLocation.getLongitude();
	        }

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
	
	protected int getSelectedTree(int selectedItem){
		int selectedTree = 0;
		for(Tree t : treelist){
			if(t.Images.size() < selectedItem){
				return selectedTree;
			}else{
				selectedItem -= t.Images.size();
				selectedTree++;
			}
		}
		return -1;
	}
	
	protected int getSelectedTreeImage(int selectedItem){
		for(Tree t : treelist){
			if(t.Images.size() < selectedItem){
				return selectedItem;
			}else{
				selectedItem -= t.Images.size();
			}
		}
		return -1;
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Bundle extras = getIntent().getExtras();
		Log.i(this.getClass().getSimpleName(), "onResume");
		gallery.setAdapter(new ImageAdapter(this, Integer.parseInt(extras.getString("Tab")), displaymetrics));
//    	fill comment list
	    if(treelist.size() > 0){
	    	laList = new LazyBrowseAdapter(this, dbhandle.getCommentList(treelist.get(selectedtree).ID), user);
	    }else{
	    	laList = new LazyBrowseAdapter(this, new ArrayList<Comment>(), user);
	    }
	    
	//     Assign adapter to ListView
	    commentList.setAdapter(laList);
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
