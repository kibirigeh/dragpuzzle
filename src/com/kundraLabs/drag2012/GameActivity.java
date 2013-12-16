package com.kundraLabs.drag2012;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdView;

public class GameActivity extends Activity
{
	private SharedPreferences mPrefs;
	Handler handler;
	 Toast toast ;
	 screen view;
	 AdView adT,adB;
	 RelativeLayout gameL;
	 LinearLayout adb1,adb2;
	 RelativeLayout.LayoutParams layG;
	 String myId="a15022e7f58f7d7";
	 Dialog dialog;
	 Typeface font;
	 TextView alertText;
	 Button dialogButton,quitButton;
	 int viewUnits[]=new int[4*5];
	 CountDown clock;
	 boolean start=false;
	 boolean pause=false;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	start=true;
    	mPrefs = getPreferences(MODE_PRIVATE);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        	gameL = (RelativeLayout) inflater.inflate(R.layout.game_screen,null);
        	adb1=(LinearLayout) gameL.findViewById(R.id.adB1);
        	adb2=(LinearLayout) gameL.findViewById(R.id.adL1);
        	Global.lastKnown=mPrefs.getInt("laskKnown", Global.currentLevel);
   
			dialog = new Dialog(GameActivity.this);
			dialog.setContentView(R.layout.alert);
			if(Global.custom)
			{
				dialog.setTitle("Custom Level");
			}
			else
			{
				dialog.setTitle("Level "+Global.currentLevel);
			}
			font = Typeface.createFromAsset(getAssets(),"fonts/textFont.ttf");
			dialogButton = (Button) dialog.findViewById(R.id.alertButton);
			quitButton = (Button) dialog.findViewById(R.id.alertCancel);
			alertText = (TextView) dialog.findViewById(R.id.alertText);
			alertText.setTypeface(font);
			dialogButton.setTypeface(font);
			dialogButton.getBackground().setAlpha(50);//set opacity of background
			
			dialogButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					dialog.dismiss();
					if(Global.gamePaused)
					{
						clock=new CountDown(handler,mPrefs.getLong("currentTime",90000),view);
						clock.createTimer();
					}
					else
					{
						clock=new CountDown(handler,90000,view);
						clock.createTimer();
					}
					//finish();
				}
			});
			
			quitButton.setTypeface(font);
			quitButton.getBackground().setAlpha(50);//set opacity of background
			quitButton.setOnClickListener(new OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					dialog.dismiss();
					endALL();					
					//android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			dialog.setCancelable(false);
			
       handler = new Handler() 
       { 
         public void handleMessage(Message msg) 
         { 
        	 String state=msg.getData().getString("state");
        	 if(state=="win")
        	 {
        		 ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
        		 Log.d("Handler received",state);
        		 displayGameWon();
        	 }
        	 else if(state=="loss")
        	 {
        		 ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(100);
        		 Log.d("Handler received",state);
        		 displayGameLost();
        	 }
        	 else if(state=="vibrate")
        	 {
        		 vibrate();
        		 Log.d("Handler received",state);
        	 }
        	 else if(state=="quit")
        	 {
        		 endALL();
        	 }
         } 
       }; 
       
       OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener()
       {
    
		     @Override
		     public void onGlobalLayout() 
		     {
		      /* Callback method to be invoked when the global layout state 
		       * or the visibility of views within the view tree changes
		       */
		    	// Log.d("Screen","gameL layout "+gameL.getHeight()+" adb1 "+adb1.getHeight()+" adb2 "+adb2.getHeight());
		    	 //Global.ScreenHeight=gameL.getHeight()-(adb1.getHeight()+adb2.getHeight());
		    	 Global.gameH=adb1.getHeight()+adb2.getHeight();
		     }
       };
       
       /*OnPreDrawListener onPreDrawListener = new OnPreDrawListener(){
    
    	@Override
    	public boolean onPreDraw() 
    	{
       * Callback method to be invoked when the view tree is about 
       * to be drawn. At this point, all views in the tree have been 
       * measured and given a frame. Clients can use this to adjust 
       * their scroll bounds or even to request a new layout before 
       * drawing occurs.
       *
    		Log.d("Screen","gameL preDraw "+gameL.getHeight());
    		return true;
     }};*/
      
       initAds();
       Log.d("Screen",""+(gameL.getHeight()));
       clock=new CountDown(handler,90000,view);
       //ViewTreeObserver viewTreeObserver = gameL.getViewTreeObserver();
       //viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
       view = new screen(this,handler,this);
       //gameL.addView(view,layG);
       layG = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
       layG.addRule(RelativeLayout.ABOVE,adb1.getId());
       layG.addRule(RelativeLayout.BELOW,adb2.getId());
     
       setContentView(gameL);
       gameL.addView(view,layG);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) 
        {
        	super.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
     
 	 public void vibrate()
 	 {
 		((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(20);
 	 }
 	 
 	 public void updateLevel()
 	 {
 		// int counter=0;
 		 
 		 if(!Global.GameDone)
 		 {
 			if(Global.levels[Global.currentLevel]!=1)
	 		 {
	 			Global.levelImage=Global.imageIDs[Global.currentLevel];
	 			Global.currentLevel++;
	 			if(Global.currentLevel>Global.lastKnown)
	 	 		 {
	 	 			 Global.lastKnown=Global.currentLevel;
	 	 		 }
	 			else
	 			{
	 				Global.currentLevel=Global.lastKnown;
	 			}
	 			Global.levels[Global.currentLevel-1]=1;
	 		 }
	 		 else
	 		 {
	 			 endALL();
	 		 }
 		 }
 	 }
 	 
 	 public void displayGameWon()
 	 {
 		 Global.isGameWon=true;
 		 if((Global.currentLevel+1)>51)
 		 {
 			 Global.GameDone=true;
 		 }
 		Intent intent = new Intent();
        intent.setClass(GameActivity.this,GameState.class);
        startActivity(intent);
 	 }
 	 public void displayGameLost()
 	 {
 		 
 		 Global.isGameWon=false;
  		Intent intent = new Intent();
         intent.setClass(GameActivity.this,GameState.class);
         startActivity(intent);
 	 }
 	 
 	 public void save()
     {
     	 SharedPreferences.Editor ed = mPrefs.edit();
          for(int i=Global.currentLevel-1;i<Global.levels.length;i++)
          {
          	ed.putInt(""+i,Global.levels[i]);
          }
          ed.putInt("currentLevel",Global.currentLevel);
          ed.putInt("lastKnown",Global.lastKnown);
          ed.putInt("gridX",Global.GridX);
          ed.putInt("gridY",Global.GridY);
          ed.commit();
     }
 	 	 
 	public void initAds()
 	{
 		adT=( AdView) gameL.findViewById(R.id.adView1);
 		adB=(AdView)  gameL.findViewById(R.id.adView2);
 		layG = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
 		layG.addRule(RelativeLayout.BELOW,R.id.adView1);
 		layG.addRule(RelativeLayout.ABOVE,R.id.adView2);
 	}
 	
 	protected void onStart()
 	{
 		if(start)
 		{
 			if(Global.gamePaused)
 			{
 				RestoreStateView();
 	    		dialogButton.setText("play");
 	    		Toast.makeText(this,"Hit play to continue",Toast.LENGTH_LONG).show();
 	    		gameL.removeView(view);
 		 		adT.loadAd(new AdRequest());
 		 		adB.loadAd(new AdRequest());
 		 		view=new screen(this.getApplicationContext(),handler,this,viewUnits);
 		 		gameL.addView(view,layG);
 		 		Global.gamePaused=false;
 			}
 			else
 			{
 				dialogButton.setText("Start");
 				if(Global.isGameWon)
 		 	 	{
 					if(Global.custom)
 					{
 						Global.isGameWon=false;
 						endALL();
 					}
 					else
 					{
 						updateLevel();
 						Global.isGameWon=false;
 					}
 		 	 	}
 		 		if(Global.custom)
 				{
 					dialog.setTitle("Custom Level");
 				}
 				else
 				{
 					dialog.setTitle("Level "+Global.currentLevel);
 				}
 		 		gameL.removeView(view);
 		 		adT.loadAd(new AdRequest());
 		 		adB.loadAd(new AdRequest());
 		 		view=new screen(this.getApplicationContext(),handler,this);
 		 		gameL.addView(view,layG);
 			}
 			start=false;
 		}
 		dialog.show();
 		super.onStart();
 	}
 	
 	@Override
 	protected void onResume()
 	{
 		//Toast.makeText(this,"ACTIVITY RESUMED ",Toast.LENGTH_LONG).show();
 		if(!start)
 		{
 			if(pause)
 			{
 				//Toast.makeText(this,"Game Paused and RESUMED ",Toast.LENGTH_LONG).show();
 	 			RestoreStateView();
 	 			dialogButton.setText("play");
 	 			Toast.makeText(this,"Hit play or contune to resume",Toast.LENGTH_LONG).show();
 	 			gameL.removeView(view);
 	 			adT.loadAd(new AdRequest());
 	 			adB.loadAd(new AdRequest());
 	 			view=new screen(this.getApplicationContext(),handler,this,viewUnits);
 	 			gameL.addView(view,layG);
 	 			pause=false;
 	 			Global.gamePaused=false;
 			}
 	 		else
 	 		{
 	 			//Toast.makeText(this,"Game not Paused and Resumed ",Toast.LENGTH_LONG).show();
 	 			dialogButton.setText("Start");
 	 			
 	 			if(Global.isGameWon)
 	 			{
 	 				if(Global.custom)
 	 				{
 	 					Global.isGameWon=false;
 	 					endALL();
 					}
 	 				else
 	 				{
 	 					//Toast.makeText(this,"Level Updated after gameWon",Toast.LENGTH_LONG).show();
 	 					updateLevel();
 	 					Global.isGameWon=false;
 					}
 			 	 }
 	 			if(Global.custom)
 	 			{
 	 				dialog.setTitle("Custom Level");
 				}
 	 			else
 	 			{
 	 				dialog.setTitle("Level "+Global.currentLevel);
 				}
 	 			gameL.removeView(view);
 	 			adT.loadAd(new AdRequest());
 	 			adB.loadAd(new AdRequest());
 	 			view=new screen(this.getApplicationContext(),handler,this);
 	 			gameL.addView(view,layG);
 			}
 			dialog.show();
 		}
 		super.onResume();
 	}
    	
 	@Override
    protected void onDestroy() 
 	{
 		save();
 		clock.stopTimer();
 		//Toast.makeText(this, "You have destroyed activity", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
 	 	 	
 	public void endALL()
 	{
 		//Toast.makeText(this, "You have exited the level", Toast.LENGTH_LONG).show();
 		try
 		{
 			view.running=false;
 			view.thread.setRunning(false);
 			clock.stopTimer();
 			super.finish();
 		}
 		catch(Exception e)
 		{
 			e.printStackTrace();
 		}
 	}
 	
 	 public void SaveStateView()
     {
     	SharedPreferences.Editor ed = mPrefs.edit();
     	int units[]=view.getState();
     	for(int i=0;i<units.length;i++)
     	{
     		ed.putInt("unit "+i,units[i]);
     	}
     	ed.putLong("currentTime ",(long)view.currentT*1000);
     	ed.commit();
     }
     public void RestoreStateView()
     {
     	for(int i=0;i<(4*5);i++)
     	{
     		viewUnits[i]=mPrefs.getInt("unit "+i,i);
     		Log.d("valueRestore",""+viewUnits[i]);
     	}
     }
     
     @Override
     public void onStop()
     {
    	 SaveStateView();
    	 Global.gamePaused=true;
     	 save();
     	 clock.stopTimer();
    	 super.onStop();
     }
     
 	@Override
 	public void onWindowFocusChanged(boolean hasFocus) 
 	{
        super.onWindowFocusChanged(hasFocus);
        if(!hasFocus)
        {
        	if(Global.gamePaused)
        	{
        		pause=true;
        	}
        } 
        else
        {
        	if(Global.gamePaused)
        	{
        		pause=false;
        	}
        }
 	}
}