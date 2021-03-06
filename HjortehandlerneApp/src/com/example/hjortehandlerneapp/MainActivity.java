package com.example.hjortehandlerneapp;

import java.net.MalformedURLException;
import java.util.List;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import com.microsoft.windowsazure.mobileservices.*;

public class MainActivity extends ActionBarActivity {
	private EditText textbox;
	private EditText pwordbox;
	private MobileServiceClient mService;
	private MobileServiceTable<Users> mTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textbox = (EditText) findViewById(R.id.tbox);
		pwordbox = (EditText) findViewById(R.id.pword);
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

	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}

	private void createUser(String name, String pword) {
		Users u = new Users();
		u.setName(name);
		u.setLocation("");
		u.setPassword(pword);

		mTable.insert(u, new TableOperationCallback<Users>() {
			@Override
			public void onCompleted(Users user, Exception ex,
					ServiceFilterResponse response) {
				Log.i("New user", user.getId());
				mTable = mService.getTable(Users.class);
				switchActivity(user);
			}
		});
	}

	public void userExists(final String name, final String pword) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		mTable.where().field("name").eq(name)
				.execute(new TableQueryCallback<Users>() {
					@Override
					public void onCompleted(List<Users> result, int count,
							Exception ex, ServiceFilterResponse response) {
						if (result.size() >= 1 ) {
							if(result.get(0).getPassword().equals(pword)) {
								switchActivity(result.get(0));
							} else {
								builder.setCancelable(true);
								builder.setTitle("Wrong password!");
								builder.setMessage("Please try with a different password or username");
								builder.create().show();
							}
						} else {
							createUser(name, pword);
						}
					}
				});
	}

	public void btnNextClick(View v) {
		userExists(textbox.getText().toString(), pwordbox.getText().toString());
	}

	private void switchActivity(Users user) {
		Intent i = new Intent(getApplicationContext(), MapActivity.class);
		i.putExtra("name", user.getName());
		i.putExtra("id", user.getId());
		i.putExtra("password", user.getPassword());
		i.putExtra("location", user.getLocation());
		startActivity(i);
	}
}