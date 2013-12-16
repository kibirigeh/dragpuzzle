package com.kundraLabs.drag2012;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.google.ads.AdSize;

public class SplashActivity extends Activity 
{
	Display display;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        //Create an object of type SplashHandler
        SplashHandler mHandler = new SplashHandler();
        // set the layout for this activity
        setContentView(R.layout.control);

        // Create a Message object
        Message msg = new Message();
        //Assign a unique code to the message.
        //Later, this code will be used to identify the message in Handler class.
        msg.what = 1;
        // Send the message with a delay of 3 seconds(3000 = 3 sec).
        mHandler.sendMessageDelayed(msg, 3000);
      
        setScreenDimensions();
    } 
    public void setScreenDimensions()
    {
    	DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	int height = dm.heightPixels;
    	int width = dm.widthPixels;
    	Global.ScreenWidth=width;
    	Global.ScreenHeight=height;
    	//Global.ScreenHeight-=((AdSize.BANNER.getHeight()*2));
    	Log.d("com.kundra screen","width is "+width+" height is "+height);
    }
    
    // Handler class implementation to handle the message
    private class SplashHandler extends Handler 
    {
        
        //This method is used to handle received messages
        public void handleMessage(Message msg)
        {
            // switch to identify the message by its code
            switch (msg.what)
            {
            default:
            case 0:
              super.handleMessage(msg);
              
              //Create an intent to start the new activity.
              // Our intention is to start MainActivity
              Intent intent = new Intent();
              intent.setClass(SplashActivity.this,MainActivity.class);
              startActivity(intent);
              // finish the current activity
              SplashActivity.this.finish();
              break;
            case 1:
            	super.handleMessage(msg);
            	setContentView(R.layout.presents);
            	Message m = new Message();
            	m.what=2;
            	this.sendMessageDelayed(m,2000);
            	break;
            case 2:
            	super.handleMessage(msg);
            	setContentView(R.layout.drag2012);
            	Message e = new Message();
            	e.what=0;
            	this.sendMessageDelayed(e,2000);
            	break;
            }
          }
    }    
}