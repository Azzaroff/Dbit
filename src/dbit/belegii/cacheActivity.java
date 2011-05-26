package dbit.belegii;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class cacheActivity extends Activity {
    private Buffer buffer = new LRU(3);
    
    //preferences
    SharedPreferences preferences;
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //generates a new buffer
        switch(Integer.parseInt(preferences.getString("bufferType", "0"))){
	        case 0:{
	        	buffer = new FiFo(Integer.parseInt(preferences.getString("buffersize", "3")));
	        	break;
	        }
	        case 1:{
	        	buffer = new LRU(Integer.parseInt(preferences.getString("buffersize", "3")));
	        	break;
	        }
	        case 2:{
	        	buffer = new FiFo(Integer.parseInt(preferences.getString("buffersize", "3")));
	        	break;
	        }
        }
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			startActivity(new Intent(this, Preferences.class));
		}
	    return(true);
	}
    
    public void startQuery(View view) {
    	final EditText query = (EditText)findViewById(R.id.edit);
        final TextView output = (TextView) findViewById(R.id.output);
    	
    	Toast.makeText(cacheActivity.this, query.getText(), Toast.LENGTH_SHORT).show();
    
    	output.setText("");
    	
    	String result = "";
    	
    	try {
    		Log.i("postgres","Go Postgres!");
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://anna05.medien.uni-weimar.de/felix?user=felix&password=felix1621";
			Connection conn = DriverManager.getConnection(url);
			//asking the buffer
			Query q;
			if((q = (buffer.get(query.getText().toString()))) != null){
				output.append(q.result);
			}else{//no matching item in buffer			
				Statement st = conn.createStatement();
				ResultSet rs = st.executeQuery(query.getText().toString());
				ResultSetMetaData rsmd = rs.getMetaData();
	
			    int numCols = rsmd.getColumnCount();
				
				while (rs.next()) {
					Log.i("postgres","getting one column");
					String row = "";
				    for (int i = 1; i <= numCols; i++) {
				    	 row = row + "| " + rs.getString(i);
					}
				    row = row + " |";
				    Log.i("postgres",row);
				    output.append(row + "\n");
				    result += row;
				    result += "\n";
				}
				rs.close();
				st.close();
				//add the query and its result to the buffer
				Query buffelem = new Query(query.getText().toString(), result);
				buffer.add(buffelem);
			}			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	//generates a new buffer
        switch(Integer.parseInt(preferences.getString("bufferType", "0"))){
	        case 0:{
	        	buffer = new FiFo(Integer.parseInt(preferences.getString("buffersize", "3")));
	        	Log.i(this.getClass().getSimpleName(), "new buffer type: FIFO");
	        	break;
	        }
	        case 1:{
	        	buffer = new LRU(Integer.parseInt(preferences.getString("buffersize", "3")));
	        	Log.i(this.getClass().getSimpleName(), "new buffer type: LRU");
	        	break;
	        }
	        case 2:{
	        	buffer = new FiFo(Integer.parseInt(preferences.getString("buffersize", "3")));
	        	Log.i(this.getClass().getSimpleName(), "new buffer type: FIFO");
	        	break;
	        }
        }
    }
}