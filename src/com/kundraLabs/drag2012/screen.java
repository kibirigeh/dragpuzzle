package com.kundraLabs.drag2012;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.google.ads.AdSize;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class screen extends SurfaceView implements SurfaceHolder.Callback
{
	ArrayList<String> scramble=new ArrayList<String>();
	private static final String TAG = screen.class.getSimpleName();
	gameThread thread=null;
	boolean running=false;
	boolean newGame=true;
	boolean update=false;
	boolean newLevel=false;
	boolean dragging=false;
	boolean custom=Global.custom;
	boolean stopTimer=false;
	private Map<String,Bitmap> images= new HashMap<String,Bitmap>();
	private Map<String,Bitmap> badGrid=new HashMap<String,Bitmap>();
	public int[]units=new int[(4*5)];
	int moveX=0;
	int moveY=0;
	int origin=0;
	int destination=0;
	Canvas canvas;
	public int currentImage=R.drawable.level_1;
	Paint p = new Paint(Color.WHITE);
	Activity main;
    boolean restoring = false;
    int u[]=new int[4*5];
	public long currentT;
	public int sizeOfAdsH=AdSize.BANNER.getHeight();
	public int widthS,heightS;
	public int wS,hS;
	
	public screen(Context context,Handler a,Activity b) 
	{
		super(context);
		widthS=(Global.ScreenWidth);
		heightS=(Global.ScreenHeight-150);
		wS=4;
		hS=5;
		main=b;
		init(a);
		// TODO Auto-generated constructor stub
	}
	public screen(Context context,Handler a,Activity b,int uv[]) 
	{
		super(context);
		main=b;
		this.u=uv;
		restoring=true;
		wS=4;
		hS=5; 
		widthS=(Global.ScreenWidth);
		heightS=(Global.ScreenHeight-Global.gameH);
		init(a);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected void onDraw(Canvas canvas) 
	{
		// TODO Auto-generated method stub
		//super.onDraw(canvas);
	}
	
	 public void drawUnit(Canvas c,int current,int x,int y)
     {
		 c.drawBitmap(unit(Integer.toString(current),images),x,y,null);
     }
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if(running)
		{
			final int action = event.getAction();
		    switch (action)
		    {
		    case MotionEvent.ACTION_MOVE:
		    	moveX=(int)event.getX();
		    	moveY=(int)event.getY();
		    	dragging=true;
				 break;
			 case MotionEvent.ACTION_DOWN:
				 origin=getUnit((int)event.getX(),(int)event.getY());
				 Log.d("finger on","origin is"+origin);
				 break;
			 case MotionEvent.ACTION_UP:
				 destination=getUnit((int)event.getX(),(int)event.getY());
				 dragging=false;
				 Log.d("finger off","destination is"+destination);
				 try
				    {
				    	updateLocation(origin,destination);
				    	update=true;
				    }
				    catch(Exception e)
				    {
				    	Log.d("trying to update","something went wrong");
				    }
				 break;
		    }
		}
		return true;
	}
	
	public void drawMovingUnit()
	{
		int x=0;
		int y=0;
		int i=0;
		Log.d("moving unit","dragging a unit");
		Canvas c=getHolder().lockCanvas(null);
		try
		{
			c.drawColor(Color.BLACK);
			Bitmap temp=unit(Integer.toString(origin),badGrid);
			for(int a=0;a<hS;a++)
			{
				x=0;
				for(int b=0;b<wS;b++)
				{
					if(i!=origin)
					{
						c.drawBitmap(badGrid.get(Integer.toString(i)),x,y,null);
						Log.d("dragging", "Just drew "+i+" and x is "+x+" and y is "+y);
					}
					x+=(widthS/wS);
					i++;
				}
				y+=(heightS/hS);
			}
			c.drawBitmap(temp,moveX-(temp.getWidth()/2),moveY-(temp.getHeight()/2),null);
			Log.d("dragging", "done drawing unit");
		}
		finally
		{
			getHolder().unlockCanvasAndPost(c);
			dragging=false;
		}
	}
	
	public void init(Handler b)
	{
		checkLevel();
		running=true;
		scramble.clear();
		getHolder().addCallback(this);
		thread = new gameThread(getHolder(),this,b);
		setFocusable(true); // make sure we get key events
		random();
		createMap();
		thread.setRunning(false);
		running=false;
	}
	
	public void checkLevel()
	{
		currentImage=Global.levelImage;
	}
	
	public void end()
	{
		thread.setRunning(false);
		this.running=false;
		main.finish();
	}
	
	public void createMap()
	{
		Bitmap original=null,bitmap=null;
		Log.d("Screen",""+heightS+" is the height and ads "+(AdSize.BANNER.getHeight()));
		if(!custom)
		{
			Log.d(TAG, "Loaded resource");
			original=BitmapFactory.decodeResource(getResources(),currentImage);
		}
		else
		{
			original=Global.getCustomPic();
		}
		bitmap = Bitmap.createScaledBitmap(original,(widthS),(heightS), true);
		Log.d(TAG, "slicing start");
		int offX=0;
		int offY=0;
		int i=0;
		for(int a=0;a<hS;a++)
		{
			offX=0;
			for(int b=0;b<wS;b++)
			{
				images.put(Integer.toString(i),Bitmap.createBitmap(bitmap,offX,offY,widthS/wS,heightS/hS));
				Log.d(TAG, "Just sliced "+i+" and x is "+offX+" and y is "+offY+"width is"+(widthS/wS));
				offX+=(widthS/wS);
				i++;
				
			}
			offY+=(heightS/hS);
		}
		Log.d(TAG, "slicing done");
		if(restoring)
		{
			for(int b=0;b<(hS*wS);b++)
			{
				badGrid.put(Integer.toString(b),images.get(Integer.toString(u[b])));
				units[b]=u[b];
				Log.d("BadGrid",u[b]+" restored "+units[b]+ "to units");
			}
		}
		else
		{
			for(int b=0;b<(wS*hS);b++)
			{
				badGrid.put(Integer.toString(b),images.get(scramble.get(b)));
				units[b]=Integer.parseInt(scramble.get(b));
				Log.d("BadGrid",scramble.get(b)+" added "+units[b]+ "to units");
			}
		}
		restoring=false;
		Log.d(TAG, "badGrid done");
	}
	
	public Bitmap unit(String key,Map<String,Bitmap>images)
	{
		Bitmap result=images.get(key);
		return result;
	}
	
	public void random()
	{
		Random rand = new Random();
		while (scramble.size() < (hS*wS))
		{

		    int random = rand.nextInt(wS*hS);
		    if (!scramble.contains(Integer.toString(random))) 
		    {
		        scramble.add(Integer.toString(random));
		    }
		}
	}
	
	public void createBackground(Canvas c)
	{
			canvas=c;
			Log.d(TAG, "background");
			int x,y=0;
			int i=0;
			try
			{
				c=getHolder().lockCanvas(null);
				for(int a=0;a<hS;a++)
				{
					x=0;
					for(int b=0;b<wS;b++)
					{
						c.drawBitmap(badGrid.get(Integer.toString(i)),x,y,null);
						Log.d(TAG, "Just drew "+i+" and x is "+x+" and y is "+y);
						x+=(widthS/wS);
						i++;
					}
					y+=(heightS/hS);
				}
				Log.d(TAG, "drawing done");
			}
			finally
			{
				getHolder().unlockCanvasAndPost(c);
			}
			
	}
	public void upDateGame(Canvas c)
	{
		
		Log.d("update","Updating game");
		int x,y=0;
		int i=0;
		try
		{
			c=getHolder().lockCanvas(null);
			for(int a=0;a<hS;a++)
			{
				x=0;
				for(int b=0;b<wS;b++)
				{
					
					c.drawBitmap(badGrid.get(Integer.toString(i)),x,y,null);
					Log.d("update", "Just drew "+i+" and x is "+x+" and y is "+y);
					x+=(widthS/wS);
					i++;
				}
				y+=(heightS/hS);
			}
			Log.d("update", "done updating");
		}
		finally
		{
			getHolder().unlockCanvasAndPost(c);
			update=false;
		}
	}
	
	public void updateLocation(int x,int y)
	{
		String first=Integer.toString(x);
		String second=Integer.toString(y);
		int t1=units[x];
		int t2=units[y];
		Log.d("tracking","origin "+x+" destination "+y);
		Bitmap temp1=badGrid.get(first);
		Bitmap temp2=badGrid.get(second);
		badGrid.put(first,temp2);
		badGrid.put(second, temp1);
		units[x]=t2;
		units[y]=t1;
		Log.d("tracking","Finished Switching");
	}

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) 
	{
		// TODO Auto-generated method stub
		running=true;
		thread.setRunning(true);
		thread.start();
	}
	
	public int[] getState()
	{
		return units;
	}
	
	public int getUnit(int eventx,int eventy)
	{
		int result=0;//the return
		int i=0;
		boolean found=false;
		
		Log.d(TAG, "offX is "+eventx+" and offY is "+eventy);
		
		int x,y=0;
		
		for(int a=0;a<hS;a++)
		{
			x=0;
			for(int b=0;b<wS;b++)
			{
				Log.d(TAG, "scanning "+i);
				if((eventx>=x&&eventx<(x+(widthS/wS))&&(eventy>=y&&eventy<(y+(heightS/hS)))))
				{
					result=i;
					Log.d("found","x is "+eventx+" y is "+eventy+" i is "+i+" for xone"+(x)+" xtwo"+(x+(widthS/wS))+" for Yone"+y+" Ytwo"+(y+(heightS/hS)));
					found=true;
					break;
				}
				x+=(widthS/wS);
				i++;
			}
			y+=(heightS/hS);
			if(found)
			{
				break;
			}
		}
		return result;
	}
	public boolean checkWin()
	{
		boolean result=true;
		for(int i=0;i<(hS*wS);i++)
		{
			if(!(units[i]==i))
			{
				Log.d("Checking win","trackimage  "+i+" contains "+units[i]);
				result=false;
				break;
			}
		}
		return result;
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// TODO Auto-generated method stub
		running=false;
		boolean retry = true;
		thread.setRunning(false);
		while (retry) 
		{
			 try 
			 {
				 thread.join();
				 //timer.cancel();
				 retry = false;
			 } 
			 catch (InterruptedException e) 
			 {
				 e.printStackTrace();
			 }
		 }
	}
	
	class gameThread extends Thread 
	{
			Handler handler;
			CountDown clock;
		  //private SurfaceHolder myThreadSurfaceHolder;
		  private screen myThreadSurfaceView;
		  private boolean myThreadRun=false,gameLost = false;
		  
		  public gameThread(SurfaceHolder surfaceHolder, screen surfaceView,Handler a) 
		  {
			  //myThreadSurfaceHolder = surfaceHolder;
			  myThreadSurfaceView = surfaceView;
			  handler=a;
		  }
		  public void setRunning(boolean b) 
		  {
			  myThreadRun = b;
		  }
		@Override
		public void run() 
		{
			Canvas c=null;
		// TODO Auto-generated method stub
		//super.run();
		Log.d("Thread","is started");
		while(myThreadRun)
		{
			if(newGame)
			{
				myThreadSurfaceView.createBackground(c);
		        running=true;
				newGame=false;
			}
			if(update)
			{
				myThreadSurfaceView.upDateGame(c);
				Message m = new Message();
		        Bundle a = new Bundle();
		        a.putString("state","vibrate");
		        m.setData(a);
		        this.handler.sendMessage(m);
		        
				if(myThreadSurfaceView.checkWin())
				{
					Log.d("checkWin","winner has finished");
					Message msg = new Message();
			        Bundle b = new Bundle();
			        b.putString("state","win");
			        msg.setData(b);
			        running=false;
			        this.handler.sendMessage(msg);
					setRunning(false);
				}
			}
			if(dragging)
			{
				myThreadSurfaceView.drawMovingUnit();
			}
			
			if(this.gameLost)
			{
				Message msg = new Message();
		        Bundle b = new Bundle();
		        b.putString("state","loss");
		        msg.setData(b);
		        this.handler.sendMessage(msg);
		        gameLost=false;
		        running=false;
				setRunning(false);
			}
		}
       }
	}
	
}
