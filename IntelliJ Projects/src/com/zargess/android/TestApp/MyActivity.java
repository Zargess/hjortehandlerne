package com.zargess.android.TestApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.zargess.android.Client.*;

public class MyActivity extends Activity implements ConnectionListener {
    private EditText MessageText;
    private TextView ResponseText;
    private Client c;
    private UIHandler mHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        MessageText = (EditText) findViewById(R.id.input);
        ResponseText = (TextView) findViewById(R.id.textView1);
        mHandler = new UIHandler(ResponseText);

        ResponseText.setMovementMethod(new ScrollingMovementMethod());

        c = new Client("10.0.2.2", 8060, mHandler);
        c.AddListener(this);
        c.Connect();

        /*Intent i = new Intent(getApplicationContext(), Start.class);
        startActivity(i);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_settings:
                Toast.makeText(MyActivity.this, "Indstillinger er valgt", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), Settings.class);
                i.putExtra("ip", c.getIPAddress());
                startActivity(i);
                return true;
            case R.id.menu_connect:
                c.Connect();
                return true;
            case R.id.menu_disconnect:
                c.TerminateClient();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        c.TerminateClient();
        super.onDestroy();
    }

    public void btnClick(View v) {
        c.SendRequest(MessageText.getText().toString());
        MessageText.setText("");
    }

    @Override
    public void Disconnected() {

    }

    @Override
    public void Connected() {

    }
}