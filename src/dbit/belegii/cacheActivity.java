package dbit.belegii;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class cacheActivity extends Activity {
    private Buffer buffer = new FiFo(3);
	
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
    

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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
			Toast.makeText(cacheActivity.this, "no Connection to Postgres", Toast.LENGTH_SHORT).show();
		}
		
		Query buffelem = new Query(query.getText().toString(), result);
		buffer.add(buffelem);
    }
}