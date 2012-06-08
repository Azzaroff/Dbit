package tree.database;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
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
	
	private TextView longitudeText;
	private TextView latitudeText;
	private EditText addComment;
	private ListView commentList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browsetab);
		
		Gallery gallery = (Gallery) findViewById(R.id.gallery);
	    gallery.setAdapter(new ImageAdapter(this));

	    gallery.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView parent, View v, int position, long id) {
	            Toast.makeText(BrowseTabActivity.this, "" + position, Toast.LENGTH_SHORT).show();
	        }
	    });
	    
	    longitudeText = (TextView)findViewById(R.id.longtext);
	    latitudeText = (TextView)findViewById(R.id.lattext);
	    commentList = (ListView)findViewById(R.id.list);
	    addComment = (EditText)findViewById(R.id.addcomment);
	    addComment.setText(getText(R.string.commentLabel));
	}
	
	public class ImageAdapter extends BaseAdapter {
	    int mGalleryItemBackground;
	    private Context mContext;

	    private File[] imageList;

	    public ImageAdapter(Context c) {
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
	        imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
	        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	        imageView.setBackgroundResource(mGalleryItemBackground);

	        return imageView;
	    }
	}
}
