package com.kundraLabs.drag2012;

import android.graphics.Bitmap;

public class Global 
{
	public static int ScreenWidth=0;//480
	public static int ScreenHeight=0;//800
	public static int currentLevel=0;
	public static int imagePos=0;
	public static boolean gamePaused=false;
	public static int lastKnown=0;
	public static boolean GameDone=false;
	public static int gameW=150,gameH=150;
	
	public static Integer[] imageIDs = 
	{
						R.drawable.level_0,R.drawable.level_1,
		                R.drawable.level_3,R.drawable.level_2,
		                R.drawable.level_4,R.drawable.level_5,
		                R.drawable.level_6,R.drawable.level_8,
		                R.drawable.level_7,R.drawable.level_9,
		                R.drawable.level_10,R.drawable.level_11,
		                R.drawable.level_12,R.drawable.level_13,
		                R.drawable.level_14,R.drawable.level_15,
		                R.drawable.level_16,R.drawable.level_17,
		                R.drawable.level_18,R.drawable.level_19,
		                R.drawable.level_20,R.drawable.level_21,
		                R.drawable.level_22,R.drawable.level_23,
		                R.drawable.level_24,R.drawable.level_25,
		                R.drawable.level_26,R.drawable.level_27,
		                R.drawable.level_28,R.drawable.level_29,
		                R.drawable.level_30,R.drawable.level_31,
		                R.drawable.level_32,R.drawable.level_33,
		                R.drawable.level_34,R.drawable.level_35,
		                R.drawable.level_36,R.drawable.level_37,
		                R.drawable.level_38,R.drawable.level_39,
		                R.drawable.level_40,R.drawable.level_41,
		                R.drawable.level_42,R.drawable.level_43,
		                R.drawable.level_44,R.drawable.level_45,
		                R.drawable.level_46,R.drawable.level_47,
		                R.drawable.level_48,R.drawable.level_49,
		                R.drawable.level_50
	};
	
	public static int GridX=4;
	public static int GridY=5;
	public static int levelImage=imageIDs[imagePos];
	public static String gameWon="next Level";
	public static String gameLost="Failed Level";
	public static boolean isGameWon=false;
	public static int timeA=120000;
	public static int customImageId;
	public static boolean custom=false;
	private static Bitmap path=null;
	public static int levels[]=new int [51];
	
	public static Bitmap getCustomPic()
	{
		return path;
	}
	
	public static void setCustomPic(Bitmap x)
	{
		path=x;
	}
}
