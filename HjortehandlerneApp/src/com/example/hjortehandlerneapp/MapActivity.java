package com.example.hjortehandlerneapp;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends Activity implements OnMapLoadedCallback {
	private GoogleMap map;
	private LatLng myLocation;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		map = ((MapFragment) getFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setOnMapLoadedCallback(this);
	}

	private void centerMapOnMyLocation() {
		if (map != null) {
			map.setMyLocationEnabled(true);

			//Set the map to current location
			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			 
			    @Override
			    public void onMyLocationChange(Location location) {
			        myLocation = new LatLng(location.getLatitude(), location.getLongitude());
			 
			        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(myLocation, 16);
			 
			        map.moveCamera(update);
			    }
			});
		}
	}

	@Override
	public void onMapLoaded() {
		centerMapOnMyLocation();
	}

}
