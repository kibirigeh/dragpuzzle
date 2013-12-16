package com.kundraLabs.drag2012;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CountDown
{
	private long mTotalCD; // Total time
	private screen Screen;
	private Handler hand;
	long elapsed;
    static long INTERVAL=1000;
    static long TIMEOUT=5000;
    private Timer timer;
    private boolean clockRunning=false; 
	                // time
	public CountDown(Handler handler,long Time,screen view)
	{
		mTotalCD = Time;
	    Screen=view;
	    hand=handler;
	    TIMEOUT=mTotalCD;
	}
	public void createTimer()
	{
		clockRunning=true;
		TimerTask task=new TimerTask()
		  {
	            @Override
	            public void run() 
	            {
	                elapsed+=INTERVAL;
	                if(elapsed>=mTotalCD)
	                {
	                    this.cancel();
	                    if(!Screen.checkWin()&&Screen.running)
	                    {
		   		    		 Log.d("CountDown","times up");
		   			    	 Message msg = new Message();
		   				     Bundle b = new Bundle();
		   				     b.putString("state","loss");
		   				     msg.setData(b);
		   				     hand.sendMessage(msg);
		   				     Screen.thread.setRunning(false);
	   		    	 	}
	                    return;
	                }
	                Screen.running=true;
	      		  	Screen.thread.setRunning(true);
	                Screen.currentT=elapsed;
			    	Log.d("CountDown",""+((mTotalCD-Screen.currentT)/1000));
	            }
	        };
	        timer = new Timer();
	        timer.scheduleAtFixedRate(task, INTERVAL, INTERVAL);
	}
		
	public void stopTimer()
	{
		if(clockRunning)
		{
			this.timer.cancel();
		}
	}
		
}
