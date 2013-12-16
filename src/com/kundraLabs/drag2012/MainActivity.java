package com.kundraLabs.drag2012;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity 
{
	private SharedPreferences mPrefs;
	int level=0;
	int imageRes;
	LayoutInflater inflater;
	RelativeLayout layout;
	Gallery gallery;
	Typeface font;
	TextView text;
	Button play,custom,exit;
	private static int REQUEST_CODE = 1;
    private Bitmap bitmap;
	private Integer[] imageIDs = Global.imageIDs;
	private int heightA,widthA;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	DisplayMetrics dm = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(dm);
    	heightA = dm.heightPixels;
    	widthA = dm.widthPixels;
    	mPrefs = getPreferences(MODE_PRIVATE);
    	Global.GridX=mPrefs.getInt("gridX",4);
		Global.GridY=mPrefs.getInt("gridY", 5);
		for(int i=0;i<Global.levels.length;i++)//restore or default to lock
		{
			Global.levels[i]=mPrefs.getInt(""+i,0);
			Log.d("Restored "+i, ""+Global.levels[i]);
		}
		Global.levels[0]=1;//flag level one to unlocked
		level=mPrefs.getInt("lastKnown",0);
		Global.currentLevel=mPrefs.getInt("currentLevel",level);
    	font = Typeface.createFromAsset(getAssets(),"fonts/textFont.ttf");
    	inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 layout = (RelativeLayout) inflater.inflate(R.layout.game,null);
		 gallery=(Gallery) layout.findViewById(R.id.gallery1);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
         WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        text=(TextView) layout.findViewById(R.id.LevelChooseText);
        text.setTypeface(font);
        setContentView(layout);
        play = (Button) layout.findViewById(R.id.playLevel);
        custom = (Button) layout.findViewById(R.id.customLevel);
        //exit = (Button) layout.findViewById(R.id.ExitButton);

        play.setTypeface(font);
        play.setTextColor(Color.WHITE);
		play.getBackground().setAlpha(20);//set opacity of background
		//exit.setTypeface(font);
        //exit.setTextColor(Color.WHITE);
		//exit.getBackground().setAlpha(20);//set opacity of background
		custom.setTypeface(font);
		custom.setTextColor(Color.WHITE);
		custom.getBackground().setAlpha(20);//set opacity of background
    	imageRes=imageIDs[level];
        //imageView.setImageResource(imageRes);
        layout.setBackgroundResource(imageRes);
        if(Global.currentLevel>1)
        {
        	play.setText("Continue");
        }
        else
        {
        	play.setText("Play");
        }
        gallery.setAdapter(new ImageAdapter(this));   
        gallery.setSelection(level);
        
        play.setOnClickListener(new OnClickListener() 
		{
		      public void onClick(View view) 
		      {
		    	  	Global.currentLevel=level+1;
		    	  	Log.d("Level status","level set to "+Global.currentLevel);
		    	  	Global.levelImage=imageRes;
		    	  	Intent intent = new Intent();
		            intent.setClass(MainActivity.this,GameActivity.class);
		            startActivity(intent); 
		      }
		});
        
        custom.setOnClickListener(new OnClickListener() 
		{
		      public void onClick(View view) 
		      {
		    	  Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		    	  photoPickerIntent.setType("image/*");
		    	  startActivityForResult(photoPickerIntent,REQUEST_CODE);
		      }
		});
        
        /*exit.setOnClickListener(new OnClickListener() 
		{
		      public void onClick(View view) 
		      {
		    	  	finish();
		      }
		});*/
        
        gallery.setOnItemClickListener(new OnItemClickListener() 
        {
            public void onItemClick(AdapterView parent,View v, int position, long id) 
            {   
            	Global.custom=false;
            	Log.d("selected"," position is "+position);
            	
            	if(Global.levels[position]==1)
            	{
            		Log.d("Level status",""+Global.levels[position]);
            		level=position;
            		imageRes=imageIDs[level];
                    //imageView.setImageResource(imageRes);
            		layout.setBackgroundResource(imageRes);
            		layout.invalidate();
            	}
            	else 
            	{
            		Log.d("selected","locked "+Global.levels[position]);
            		//imageView.setImageResource(R.drawable.locked);
            		layout.setBackgroundResource(R.drawable.locked);
            		layout.invalidate();
            	}
            }
        });
        
        Toast.makeText(this, "Choose a level and press play or continue", Toast.LENGTH_LONG).show();
    }
     
    @Override
	  protected void onActivityResult(int requestCode, int resultcode, Intent intent)
	  {
		  super.onActivityResult(requestCode, resultcode, intent);
		  if (requestCode == REQUEST_CODE)
		  {
			  if (intent != null) 
			  {
				  Log.d("Photo ", "idButSelPic Photopicker: " + intent.getDataString());
				  Cursor cursor = getContentResolver().query(intent.getData(), null, null, null, null);
				  cursor.moveToFirst();  //if not doing this, 01-22 19:17:04.564: ERROR/AndroidRuntime(26264): Caused by: android.database.CursorIndexOutOfBoundsException: Index -1 requested, with a size of 1
				  int idx = cursor.getColumnIndex(ImageColumns.DATA);
				  String fileSrc = cursor.getString(idx);
				  Log.d("Photo ", "Picture:" + fileSrc);
				  bitmap = BitmapFactory.decodeFile(fileSrc); //load preview image
				  
				  Global.custom=true;
				  
				  	int width = bitmap.getWidth();
			        int height = bitmap.getHeight();
			        int newWidth = widthA;
			        int newHeight = heightA;

			        // calculate the scale - in this case = 0.4f
			        float scaleWidth = ((float) newWidth) / width;
			        float scaleHeight = ((float) newHeight) / height;

			        // createa matrix for the manipulation
			        Matrix matrix = new Matrix();
			        // resize the bit map
			        matrix.postScale(scaleWidth, scaleHeight);
			        // rotate the Bitmap
			        matrix.postRotate(90);

			        // recreate the new Bitmap
			        Bitmap scaled = Bitmap.createBitmap(bitmap, 0, 0,width, height, matrix, true);
			        
			        Global.setCustomPic(scaled);
					//imageView.setImageBitmap(scaled);
			        Drawable d = new BitmapDrawable(getResources(),scaled);
					layout.setBackgroundDrawable(d);
					layout.invalidate();
			  }
			  else 
			  {
				  Log.d("Photo ", "idButSelPic Photopicker canceled");
			  }
		  }
	  } 
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
	 protected void onResume()
	 {
    	
    	Global.GameDone=false;
		 //level=mPrefs.getInt("lastKnown",Global.currentLevel);
		 level=getHighest();
		 Log.d("Resume","level "+level);
		 if(Global.levels[level]==0)
		 {
			 level=level-1;
		 }
		 imageRes=imageIDs[level];
		 if(!Global.custom)
	     {
			layout.setBackgroundResource(imageRes);
			layout.invalidate();
	     }
		 gallery.setSelection(level);
		 super.onResume();
	 }
    
    public int getHighest()
    {
    	for(int i=level;i<Global.levels.length;i++)
    	{
    		if(Global.levels[i]!=1)
    		{
    			return i;
    		}
    	}
    	return 1;
    }
    @Override
    protected void onPause() 
    {
        super.onPause();
        save();
    }
    
    public void save()
    {
    	 SharedPreferences.Editor ed = mPrefs.edit();
         for(int i=0;i<Global.levels.length;i++)
         {
         	ed.putInt(""+i,Global.levels[i]);
         }
         ed.putInt("currentLevel",Global.currentLevel);
         ed.putInt("lastKnown",Global.lastKnown);
         ed.putInt("gridX",Global.GridX);
         ed.putInt("gridY",Global.GridY);
         ed.commit();
    }
    
   
    
    class ImageAdapter extends BaseAdapter 
    {
        private Context context;
        private int itemBackground;
 
        public ImageAdapter(Context c) 
        {
            context = c;
            //---setting the style---
            TypedArray a = obtainStyledAttributes(R.styleable.Gallery1);
            itemBackground = a.getResourceId(R.styleable.Gallery1_android_galleryItemBackground, 0);
            a.recycle();                    
        }
 
        //---returns the number of images---
        public int getCount() 
        {
            return imageIDs.length;
        }
 
        //---returns the ID of an item--- 
        public Object getItem(int position) 
        {
            return position;
        }            
 
        public long getItemId(int position)
        {
            return position;
        }
 
        //---returns an ImageView view---
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ImageView imageView = new ImageView(context);
            imageView.setImageResource(imageIDs[position]);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(170, 140));
            imageView.setBackgroundResource(itemBackground);
            return imageView;
        }
    }  
}
