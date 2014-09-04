package com.zargess.android.TestApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by MFH on 02-05-2014.
 */
public class Settings  extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.settings);
        Intent i = getIntent();
        String test = i.getStringExtra("ip");
    }

    public void btnBack(View v) {
        finish();
    }
}
