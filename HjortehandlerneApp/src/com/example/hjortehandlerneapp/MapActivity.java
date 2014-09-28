package com.example.hjortehandlerneapp;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;

public class MapActivity extends Activity implements OnMapLoadedCallback {
	private GoogleMap map;
	private LatLng myLocation;
	private Users user;
	private Marker marker;
	private HashMap<String,Marker> otherUsers;
	private MobileServiceClient mService;
	private MobileServiceTable<Users> mTable;
	private WifiManager wifiMan;
	private String wifiName;
	private BluetoothAdapter adapter;
	private final static int REQUEST_ENABLE_BT = 1;
	private boolean bluetoothOn;
	
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
		user.setWifiName("");
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
		wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		saveWifiState();
		adapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothInit();
	}
	
	private void bluetoothInit() {
		if (adapter != null) {
			if (!adapter.isEnabled()) {
				Intent enablebt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enablebt, REQUEST_ENABLE_BT);
			}
			bluetoothOn = true;
			user.setBluetooth(adapter.getAddress());
			updateUser(user);
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
			startActivity(discoverableIntent);
			BroadcastReceiver mReceiver = new BroadcastReceiver() {
			    @Override
				public void onReceive(Context context, Intent intent) {
			        String action = intent.getAction();
			        // When discovery finds a device
			        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
			            // Get the BluetoothDevice object from the Intent
			            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			            mTable = mService.getTable(Users.class);
			            mTable.where().field("bluetooth").eq(device.getAddress()).execute(new TableQueryCallback<Users>() {
							
							@Override
							public void onCompleted(List<Users> users, int arg1, Exception arg2,
									ServiceFilterResponse arg3) {
								if (users.size() > 0) {
									Users u = users.get(0);
									marker.setSnippet(marker.getSnippet() + ", " + u.getName());
								}
							}
						});
			        }
			    }
			};
			// Register the BroadcastReceiver
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			registerReceiver(mReceiver, filter);
		} else {
			bluetoothOn = false;
		}
	}

	private void updateUser(Users u) {
		mTable.update(u, new TableOperationCallback<Users>() {
			
			@Override
			public void onCompleted(Users arg0, Exception arg1,
					ServiceFilterResponse arg2) {
			}
		});
	}

	private void saveWifiState() {
		if(wifiMan.isWifiEnabled()) {
			wifiName = wifiMan.getConnectionInfo().getSSID();
			user.setWifiName(wifiName);
			mTable.update(user, new TableOperationCallback<Users>() {
				@Override
				public void onCompleted(Users arg0, Exception arg1,
						ServiceFilterResponse arg2) {
					// TODO Auto-generated method stub
					
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
		if (bluetoothOn && !adapter.isDiscovering()) {
			adapter.startDiscovery();			
		}
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