package com.example.hjortehandlerneapp;

import java.sql.ResultSet;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends ActionBarActivity {
	private EditText textbox;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textbox = (EditText)findViewById(R.id.tbox);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void btnNextClick(View v) {
    	SqlManager.EditInformation(SqlManager.createNewUser(textbox.getText().toString(), "Humus"));
//    	ResultSet s = SqlManager.FetchInformation(SqlManager.GETALL);
    	
//    	Intent i = new Intent(getApplicationContext(),MapActivity.class);
//    	i.putExtra("name", textbox.getText().toString());
//    	startActivity(i);
    }
}
