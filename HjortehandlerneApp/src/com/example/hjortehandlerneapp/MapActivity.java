package com.example.hjortehandlerneapp;

import java.net.MalformedURLException;
import java.util.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.microsoft.windowsazure.mobileservices.*;

public class MapActivity extends Activity implements OnMapLoadedCallback {
	private GoogleMap map;
	private LatLng myLocation;
	private Users user;
	private MarkerOptions marker;
	private HashMap<String, MarkerOptions> otherUsers;
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
		getUser();
		otherUsers = new HashMap<String, MarkerOptions>();
		try {
			mService = new MobileServiceClient(
					"https://pervasivehjorte.azure-mobile.net/",
					"dcsHgpaDELtZnyJwPatgDKuGjlvPOH95", this);
			mTable = mService.getTable(Users.class);
		} catch (MalformedURLException e) {
			createAndShowDialog(
					"There was an error creating the Mobile Service. Verify the URL",
					"Error");
			e.printStackTrace();
		}
	}

	private void getUser() {
		Intent i = getIntent();
		final String id = i.getStringExtra("id");
		mTable.where().field("id").eq(id).execute(new TableQueryCallback<Users>() {
			@Override
			public void onCompleted(List<Users> users, int count, Exception ex,
					ServiceFilterResponse response) {
				if (users.size() == 1) {
					user = users.get(0);
				} else {
					createAndShowDialog("An error orcurred with the user id: " + id, "Error");
				}
			}
		});
	}

	@Override
	public void onMapLoaded() {
		if (map != null) {
			map.setMyLocationEnabled(true);
			// Set the map to current location
			map.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
				@Override
				public void onMyLocationChange(Location location) {
					myLocation = new LatLng(location.getLatitude(), location
							.getLongitude());
					if (marker == null) {
						marker = new MarkerOptions().position(myLocation)
								.title("My location").snippet(user.getName())
								.draggable(false);
						map.addMarker(marker);
					} else {
						marker.position(myLocation);
					}
					updatePositionOnServer(myLocation);
					CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
							myLocation, 16);
					map.animateCamera(update);
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
								MarkerOptions marker = new MarkerOptions()
										.position(stringToCoordinate(user.getLocation()))
										.snippet(user.getName())
										.draggable(false);
								otherUsers.put(user.getId(), marker);
								map.addMarker(marker);
							} else {
								MarkerOptions marker = otherUsers.get(user.getId());
								marker.position(stringToCoordinate(user.getLocation()));
							}
						}
					}
				});
	}

	private LatLng stringToCoordinate(String s) {
		String[] temp = s.split(",");
		double lat = Double.parseDouble(temp[0]);
		double lng = Double.parseDouble(temp[1]);

		return new LatLng(lat, lng);
	}

	private void updatePositionOnServer(LatLng myLocation) {
		String pos = myLocation.toString();
		pos = pos.replace("lat/lng: (", "");
		pos = pos.replace(")", "");
		user.setLocation(pos);
		mTable.update(user, new TableOperationCallback<Users>() {
			@Override
			public void onCompleted(Users arg0, Exception arg1, ServiceFilterResponse arg2) {}
		});
	}

	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}
}