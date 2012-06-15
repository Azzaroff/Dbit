package tree.database.misc;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GpsHandler {
	
	//host activity
	private Activity activity;
	
	//GPS Location Manager
	private LocationManager locationManager;
	private GeoUpdateHandler updatehandler = new GeoUpdateHandler(); //handles gps updates
	private Location currentLocation;
	
	public GpsHandler(Activity activity){
		this.activity = activity;
	}
	
	/**
	 * sets the map to the current location
	 */
	public Location updateLocation(){
		
		boolean gps_enabled = false;
		boolean network_enabled = false;

		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		
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
	    return currentLocation;
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

