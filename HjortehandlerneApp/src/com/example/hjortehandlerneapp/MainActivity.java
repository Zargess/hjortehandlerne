package com.example.hjortehandlerneapp;

import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.microsoft.windowsazure.mobileservices.*;

public class MainActivity extends ActionBarActivity {
	private EditText textbox;
	private MobileServiceClient mService;
	private MobileServiceTable<Users> mTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textbox = (EditText) findViewById(R.id.tbox);

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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}

	private void createUser(String name) {
		Users u = new Users();
		u.setName(name);
		u.setLocation("");
		u.setPassword("DinMor");

		mTable.insert(u, new TableOperationCallback<Users>() {
			@Override
			public void onCompleted(Users user, Exception ex,
					ServiceFilterResponse response) {
				Log.i("New user", user.getId());
				mTable = mService.getTable(Users.class);
				switchActivity(user.getName(), user.getId());
			}
		});
	}

	public void userExists(final String name) {
		mTable.where().field("name").eq(name)
				.execute(new TableQueryCallback<Users>() {
					@Override
					public void onCompleted(List<Users> result, int count,
							Exception ex, ServiceFilterResponse response) {
						if (result.size() == 1) {
							Users u = result.get(0);
							switchActivity(u.getName(), u.getId());
						} else {
							createUser(name);
						}
					}
				});
	}

	public void btnNextClick(View v) {
		userExists(textbox.getText().toString());
	}

	private void switchActivity(String name, String id) {
		Intent i = new Intent(getApplicationContext(), MapActivity.class);
		i.putExtra("name", name);
		i.putExtra("id", id);
		startActivity(i);
	}
}