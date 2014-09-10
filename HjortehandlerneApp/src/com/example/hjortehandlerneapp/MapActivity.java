package com.example.hjortehandlerneapp;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Activity implements OnMapLoadedCallback {
	private GoogleMap map;
	private LatLng myLocation;
	private String name;
	private MarkerOptions marker;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setOnMapLoadedCallback(this);
		Intent i = getIntent();
		name = i.getStringExtra("name");
	}

	private void centerMapOnMyLocation() {
		if (map != null) {
			map.setMyLocationEnabled(true);
			// Set the map to current location
			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
				@Override
				public void onMyLocationChange(Location location) {
					myLocation = new LatLng(location.getLatitude(), location.getLongitude());
					if (marker == null) {
						marker = new MarkerOptions().position(myLocation)
								.title("My location").snippet(name)
								.draggable(false);
						map.addMarker(marker);
					} else {
						marker.position(myLocation);
					}
					CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
							myLocation, 16);
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
