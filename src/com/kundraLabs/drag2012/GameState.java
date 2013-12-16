package com.kundraLabs.drag2012;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GameState extends Activity
{
	LayoutInflater inflater;
	RelativeLayout layout,layout2;
	Typeface font;
	LinearLayout Screen;
	@Override
    public void onCreate(Bundle savedInstanceState) 
    {
		inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 layout = (RelativeLayout) inflater.inflate(R.layout.game_state,null);
		 layout2=(RelativeLayout) inflater.inflate(R.layout.game_done,null);
		 Screen = (LinearLayout) layout.findViewById(R.id.gameStateLinear);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(layout);
		if(Global.custom)
		{
			Drawable d = new BitmapDrawable(getResources(),Global.getCustomPic());
			Screen.setBackgroundDrawable(d);
		}
		else
		{
			Screen.setBackgroundResource(Global.levelImage);
		}
		
		if(Global.GameDone)
		{
			setContentView(R.layout.game_done);
			
		}
		display();
    }
	
	public void display()
	{
		Global.gamePaused=false;
		font = Typeface.createFromAsset(getAssets(),"fonts/textFont.ttf");
		Button play = (Button) Screen.findViewById(R.id.Play);
		play.setTypeface(font);
		play.getBackground().setAlpha(45);//set opacity of background
		if(!Global.GameDone)
		{
			if(Global.isGameWon)
			{
				if(Global.custom)
				{
					play.setText("Puzzle solved");
				}
				else
				{
					play.setText("Next Level");
				}
			}
			else
			{
				if(Global.custom)
				{
					play.setText("Puzzle failled");
				}
				else
				{
					play.setText("Retry Level");
				}
			}
			play.setOnClickListener(new OnClickListener() 
			{
			      public void onClick(View view) 
			      {
			        finish();
			      }
			});
		}
		else
		{
			TextView one,two,three;
			one = (TextView)layout2.findViewById(R.id.GameDoneText1);
			two=(TextView)layout2.findViewById(R.id.GameDoneText2);
			three=(TextView)layout2.findViewById(R.id.GameDoneText3);
			one.setTypeface(font);
			two.setTypeface(font);
			three.setTypeface(font);
		}
		
	}
}
