package com.example.hjortehandlerneapp;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.*;

public class MapActivity extends Activity implements OnMapLoadedCallback {
	private GoogleMap map;
	private LatLng myLocation;
	private Users user;
	private Marker marker;
	private HashMap<String,Marker> otherUsers;
	private MobileServiceClient mService;
	private MobileServiceTable<Users> mTable;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.setOnMapLoadedCallback(this);
		Intent i = getIntent();
		user = new Users();
		user.setId(i.getStringExtra("id"));
		user.setLocation(i.getStringExtra("location"));
		user.setName(i.getStringExtra("name"));
		user.setPassword(i.getStringExtra("password"));
		otherUsers = new HashMap<String,Marker>();
		try {
			mService = new MobileServiceClient(
					"https://pervasivehjorte.azure-mobile.net/",
					"dcsHgpaDELtZnyJwPatgDKuGjlvPOH95", this);
			mTable = mService.getTable(Users.class);
		} catch (MalformedURLException e) {
			createAndShowDialog("There was an error creating the Mobile Service. Verify the URL", "Error");
			e.printStackTrace();
		}
	}

	@Override
	public void onMapLoaded() {
		if (map != null) {
			map.setMyLocationEnabled(true);
			// Set the map to current location
			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
				@Override
				public void onMyLocationChange(Location location) {
					myLocation = new LatLng(location.getLatitude(), location.getLongitude());
					if (marker == null) {
						MarkerOptions op = new MarkerOptions().position(myLocation)
								.title("My location").snippet(user.getName())
								.draggable(false);
						marker = map.addMarker(op);
						CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
								myLocation, 16);
						map.moveCamera(update);
					} else {
						marker.setPosition(myLocation);
					}
					updatePositionOnServer(myLocation);
					findOtherUsers();
				}
			});
		}
	}
	
	private void findOtherUsers() {
		mTable = mService.getTable(Users.class);
		mTable.where().field("id").ne(user.getId()).and().field("location").ne("").execute(new TableQueryCallback<Users>() {
					@Override
					public void onCompleted(List<Users> result, int count,
							Exception ex, ServiceFilterResponse response) {
						for (Users user : result) {
							if (!otherUsers.containsKey(user.getId())) {
								MarkerOptions op = new MarkerOptions()
										.position(stringToCoordinate(user.getLocation()))
										.snippet(user.getName())
										.title("Other gay guy")
										.draggable(false);
								Marker marker = map.addMarker(op);
								otherUsers.put(user.getId(), marker);
							} else {
								Marker marker = otherUsers.get(user.getId());
								marker.setPosition(stringToCoordinate(user.getLocation()));
							}
						}
					}
				});
	}
	
	private LatLng stringToCoordinate(String s){
		String[] temp = s.split(",");
		double lat = Double.parseDouble(temp[0]);
		double lng = Double.parseDouble(temp[1]);
		
		return new LatLng(lat,lng);
	}
	
	private void updatePositionOnServer(LatLng myLocation) {
		String pos = myLocation.toString();
		pos = pos.replace("lat/lng: (", "");
		pos = pos.replace(")", "");
		user.setLocation(pos);
		mTable.update(user, new TableOperationCallback<Users>() {
			@Override
			public void onCompleted(Users arg0, Exception arg1,
					ServiceFilterResponse arg2) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}
}