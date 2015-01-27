package com.ok.mathlightalarm.screen;

import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ok.mathlightalarm.R;
import com.ok.mathlightalarm.math.Calculator;
import com.ok.mathlightalarm.service.AlarmManagerHelper;


public class AlarmScreen extends Activity implements OnClickListener {
	
	public final String TAG = this.getClass().getSimpleName();
    private TextView txtquestion;
	private WakeLock mWakeLock;
	private MediaPlayer mPlayer;
    private Calculator mathQuestion;
    private Button Abutton,Bbutton, Cbutton,dismissButton;
    float[] numberArray=new float[3];
	int [] ChosenArray=new int[3];
	private float select=-1;
	private static final int WAKELOCK_TIMEOUT = 60 * 1000;
	private int math;
	private int trueCount;
	private int max,min; 
	private Camera camera;
	private final Context context = this;
	private Thread runnable,control;
	//private myThread myThreadObj;
	private boolean selected=true;
	private boolean flash;
	private boolean isActivityPaused = false;
	private long timeNow;
	private int controlCount;
	private boolean alarmActive;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		trueCount=1;
		max=10;
		min=5;
		timeNow = System.currentTimeMillis()/1000;
		controlCount=0;
		alarmActive=false;
		//Setup layout
		this.setContentView(R.layout.activity_alarm_screen);
		Drawable drawable ;
		Date dt = new Date();
		int hours = dt.getHours();
		Resources res = getResources();
	   
		RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.AlarmScreenRelativeLayout);
		if(10>=hours && hours>5)
			drawable = res.getDrawable(R.drawable.sunrise);
		else  if(17>=hours && hours>10)
		drawable = res.getDrawable(R.drawable.noon);
		else if(20>=hours && hours>17)
			drawable = res.getDrawable(R.drawable.sunset);
		else
	    drawable = res.getDrawable(R.drawable.night); 
		relativeLayout.setBackgroundDrawable(drawable);
	/*	final PackageManager pm = context.getPackageManager();
		if (isCameraSupported(pm)) 
		{
			camera = Camera.open();
		}*/
		//myThreadObj=new myThread(camera);
		mathQuestion=new Calculator(10,5);
		generate(10,5);
		String name = getIntent().getStringExtra(AlarmManagerHelper.NAME);
		int timeHour = getIntent().getIntExtra(AlarmManagerHelper.TIME_HOUR, 0);
		int timeMinute = getIntent().getIntExtra(AlarmManagerHelper.TIME_MINUTE, 0);
		String tone = getIntent().getStringExtra(AlarmManagerHelper.TONE);
		 math=getIntent().getIntExtra(AlarmManagerHelper.MATH, 0);
		 flash=getIntent().getBooleanExtra(AlarmManagerHelper.FLASH,false);
		TextView tvName = (TextView) findViewById(R.id.alarm_screen_name);
		tvName.setText(""+math);
		txtquestion = (TextView) findViewById(R.id.textQuestion);
		TextView tvTime = (TextView) findViewById(R.id.alarm_screen_time);
		tvTime.setText(String.format("%02d : %02d", timeHour, timeMinute));
   
		dismissButton = (Button) findViewById(R.id.alarm_screen_button);
	    Abutton = (Button) findViewById(R.id.A);
		 Bbutton = (Button) findViewById(R.id.B);
		 Cbutton = (Button) findViewById(R.id.C);
		 Abutton.setOnClickListener(this);
		 Bbutton.setOnClickListener(this);
		 Cbutton.setOnClickListener(this);
		 dismissButton.setOnClickListener(this);
	     dismissButton.setEnabled(false);
      if(flash)
      {
    	  final PackageManager pm = context.getPackageManager();
  		if (isCameraSupported(pm)) 
  		{
  			camera = Camera.open();
  			 onFlash(true);
  		}
  	  
      }
	
      if(math!=0)
      {
    	draw();
      }
       else
      {    	alarmActive=true;
    	   Abutton.setEnabled(false);
    	   Bbutton.setEnabled(false);
    	   Cbutton.setEnabled(false);
    	   dismissButton.setEnabled(true);
      }
		//Play alarm tone
		mPlayer = new MediaPlayer();
		try {
			if (tone != null && !tone.equals("")) {
				Uri toneUri = Uri.parse(tone);
				if (toneUri != null) {
					
					mPlayer.setDataSource(this, toneUri);
					mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
					mPlayer.setLooping(true);
					mPlayer.prepare();
					mPlayer.start();
				}
			}
		} catch (Exception e) {
			mPlayer.release();
			e.printStackTrace();
		}
		//control();
		//Ensure wakelock release
		Runnable releaseWakelock = new Runnable() {

			@Override
			public void run() {
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
               
				if (mWakeLock != null && mWakeLock.isHeld()) {
					mWakeLock.release();
				}
			
			}
		};

		new Handler().postDelayed(releaseWakelock, WAKELOCK_TIMEOUT);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();

		// Set the window to keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);

		// Acquire wakelock
		PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		if (mWakeLock == null) {
			mWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), TAG);
		}

		if (!mWakeLock.isHeld()) {
			mWakeLock.acquire();
			Log.i(TAG, "Wakelock aquired!!");
		}

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (alarmActive)
			{
			super.onBackPressed();
			try {
				mPlayer.stop();
			} catch (Exception e) {

			}
			try {
				mPlayer.release();
			} catch (Exception e) {

			}
			stopThread(runnable);
			finish();
			}
	}

	@Override
	protected void onStop(){
	      super.onStop();
	      stopThread(runnable);

	}
	
	@Override
	protected void onDestroy() {
	super.onDestroy();
	try {
		mPlayer.stop();
	} catch (Exception e) {

	}
	try {
		mPlayer.release();
	} catch (Exception e) {

	}
      stopThread(runnable);
	}
	@Override
	protected void onPause() {
		super.onPause();
		if (mWakeLock != null && mWakeLock.isHeld()) {
			mWakeLock.release();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.alarm_screen_button)
		{
			try {
				mPlayer.stop();
			} catch (Exception e) {

			}
			try {
				mPlayer.release();
			} catch (Exception e) {

			}
			stopThread(runnable);
			finish();
	    }
		else if(v.getId() == R.id.A)
		{ 
			
			select=numberArray[ChosenArray[0]];
			controller();
	    }
		else if(v.getId() == R.id.B)
		{
			
			select=numberArray[ChosenArray[1]];
			controller();
	    }
		else if(v.getId() == R.id.C)
		{
			
			select=numberArray[ChosenArray[2]];
			controller();
	    }
	} 
	 public void controller()
	    {
	    	if(mathQuestion.getResult()==select)
	    	{
	    		trueCount++;
	           if(trueCount==1 && trueCount<=math )
	           {
	        	   generate( max*trueCount,min*trueCount);
		    	    draw();
	           }
	           else if(trueCount==2 && trueCount<=math )
	           {
	        	   generate( max*trueCount,min*trueCount);
		    	    draw();
	           }
	           else if(trueCount==3 && trueCount<=math )
	           {
	        	   generate( max*trueCount,min*trueCount);
		    	    draw();
	           }
	           else
	    		{
	        	   dismissButton.setEnabled(true);
	        	   alarmActive=true;
	    		}
	    	}
	    	else
	    		{
	    		 generate( max*trueCount,min*trueCount);
	    	    draw();
	    	    }
	    }

	public void draw()
	{
		txtquestion.setText(" "+mathQuestion.getNumberA()+"  "+mathQuestion.getOp()+"  "+mathQuestion.getNumberB());
   		Abutton.setText(""+numberArray[ChosenArray[0]]);
   		Bbutton.setText(""+numberArray[ChosenArray[1]]);	
   		Cbutton.setText(""+numberArray[ChosenArray[2]]);
		
	}
	public  void generate(int max ,int min)
	{
		
		mathQuestion.generate();
		numberArray=mathQuestion.getNumberArray();
		ChosenArray=mathQuestion.getChosenArray();
		
	}
	/*public void control()
	{
		control = new Thread() {
		       @Override 
		       public void run() {
		    	   try {
		    	   while(!isActivityPaused ) {
		    		       controlCount++;
		    		    	  Thread.sleep(1000);
		    		    		if(controlCount>5)
		    					{
		    						mPlayer.stop();
		    						stopThread(runnable);
		    						finish();
		    					}
		    			            }
		    	       }
		    	   catch(Throwable t) {}

		       }
		};
		new Thread(control).start(); 
		
	}*/
	public void onFlash(boolean on) {
		
		PackageManager pm = context.getPackageManager();
		final Parameters p = camera.getParameters();
		
		if (isFlashSupported(pm)) {
			runnable = new Thread() {
			       @Override 
			       public void run() {
			    	   try {
			    	   while(!isActivityPaused ) {
			    		    
			    		    
			    		    	  Thread.sleep(1000);
			    		      
			    		    	p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			    				camera.setParameters(p);
			    				camera.startPreview();
			    		    	camera.startPreview();
	
			    		    	Thread.sleep(1000);
			    			    p.setFlashMode(Parameters.FLASH_MODE_OFF);
			    				camera.setParameters(p);
			    			    camera.stopPreview();
			    			            }
			    	       }
			    	   catch(Throwable t) {}

			       }
			};
			new Thread(runnable).start(); 
	
		} 
		else {
			
			AlertDialog alertDialog = new AlertDialog.Builder(context).create();
			alertDialog.setTitle("No Camera Flash");
			alertDialog
					.setMessage("The device's camera doesn't support flash.");
			alertDialog.setButton(RESULT_OK, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(final DialogInterface dialog,
								final int which) {
							Log.e("err",
									"The device's camera doesn't support flash.");
						}
					});
			alertDialog.show();
		}
	}
	
	private synchronized void stopThread(Thread runnable)
	{
		/*if (control != null)
	    {
			isActivityPaused = true;
			control=null;
			
	    }*/
	    if (runnable != null)
	    {
	    	isActivityPaused = true;
	        runnable = null;
	        if (camera != null) {
				camera.release();
			}
	    }
	    
	}

	/**
	 * @param packageManager
	 * @return true <b>if the device support camera flash</b><br/>
	 *         false <b>if the device doesn't support camera flash</b>
	 */
	private boolean isFlashSupported(PackageManager packageManager) {
		// if device support camera flash?
		if (packageManager
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
			return true;
		}
		return false;
	}

	/**
	 * @param packageManager
	 * @return true <b>if the device support camera</b><br/>
	 *         false <b>if the device doesn't support camera</b>
	 */
	private boolean isCameraSupported(PackageManager packageManager) {
		// if device support camera?
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		}
		return false;
	}
}
