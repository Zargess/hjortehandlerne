package com.zargess.android.TestApp;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Created by MFH on 30-04-2014.
 */
public class UIHandler extends Handler implements Serializable{
    private TextView TextView;

    public UIHandler(TextView textView) {
        super();
        TextView = textView;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.obj.toString().length() > 0) {
            TextView.append("\n" + msg.obj.toString());
        }
        super.handleMessage(msg);
    }
}
