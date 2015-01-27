package com.ok.mathlightalarm;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
public class myThread extends Thread {
	  private boolean selected=true;
	  private Camera camera ;
	  private final Parameters p = camera.getParameters();
	    public myThread(Camera camera) {
	      this.camera=camera;
	    }
	  public void run() {
	    while(true) {
	    
	      try {
	        sleep(1000);
	                 // Örneðin her seferinde 1 saniye beklenecek
	    	if(selected)
	    	{
	    	p.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(p);
			camera.startPreview();
	    	camera.startPreview();
	    	selected=false;
	    	}
	    	else
	    	{
	    		
		        p.setFlashMode(Parameters.FLASH_MODE_OFF);
			    camera.setParameters(p);
		        camera.stopPreview();
		        selected=true;
	    	}
	      }
	      catch(InterruptedException e) {
	      
	      }
	      
	    }
	  }
	}