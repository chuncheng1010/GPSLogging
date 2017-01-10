package main.gpslogging;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
//import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Environment;


import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.SurfaceHolder.Callback;

import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;


class Preview extends SurfaceView implements Callback{

	SurfaceHolder mHolder;
	Camera mCamera;
	
	public Preview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setPreviewSize(width, height);
		mCamera.setParameters(parameters);
		mCamera.startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera = Camera.open();
		//findFrontFacingCamera();
		if(mCamera == null)
		{
			Log.d("camera", "open fail");
			return;
		}
		//findFrontFacingCamera();
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			mCamera.release();
			mCamera = null;
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}	
	
	/*
	private void findFrontFacingCamera() {
		int cameraCount = 0;
	    //Camera cam = null;
	    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
	    cameraCount = Camera.getNumberOfCameras();
	    for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
	        Camera.getCameraInfo( camIdx, cameraInfo );
	        //if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT  ) {
	            try {
	            	mCamera = Camera.open( camIdx );
	            } catch (RuntimeException e) {
	                Log.e("TAG", "Camera failed to open: " + e.getLocalizedMessage());
	            }
	      //  }
	    }
	}
	*/
}
public class CameraView extends Activity{

	private Preview mPreview = null;
	private Button play,crop,done;
	private View surface;
	private ImageView imageview;
	String imagePath = "/GPSLogging/photo";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cameraview);
		play = (Button)findViewById(R.id.play);
		crop = (Button)findViewById(R.id.crop);
		done = (Button)findViewById(R.id.done);		
		
		/*
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		mPreview = new Preview(this);
		setContentView(mPreview);
		*/
		
		done.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//mPreview = new Preview(surface);
				finish();
				
				
			}
			
		});
		crop.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mPreview == null)
					return;
				mPreview.mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
				play.setEnabled(false);
			}
			
		});
		play.setOnClickListener(new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setWindowProperty();
				//finish();
				
			}
			
		});
		
		
	}
	PictureCallback jpegCallback = new PictureCallback()
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			FileOutputStream outStream = null;
			FileInputStream inputStream = null;
			File file = null;
			//long fileNamePrefix = System.currentTimeMillis();
			Calendar cal = Calendar.getInstance();
			TelephonyManager telephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
			String fileName = telephony.getLine1Number();
			imagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + imagePath;
			File filelist = new File(imagePath);
		    if(filelist.exists() == false)
		    {
		    	filelist.mkdirs();
		    }
			fileName = String.format("/%s_%4d%02d%02d%02d%02d%02d",fileName,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
			fileName = String.format("%s%s.jpg", imagePath,fileName);
			try {
				outStream = new FileOutputStream(fileName);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			try {
				outStream.write(data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
			file = new File(fileName);
			try {
				inputStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			mPreview.setVisibility(View.GONE);
			Bitmap bi;
			bi = BitmapFactory.decodeStream(inputStream,null,null);
			imageview.setImageBitmap(bi);
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			
		}
		
	};
	PictureCallback rawCallback = new PictureCallback()
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			
		}
		
	};
	ShutterCallback shutterCallback = new ShutterCallback()
	{

		@Override
		public void onShutter() {
			// TODO Auto-generated method stub
			
		}
		
	};
	private void setWindowProperty()
	{
		mPreview = new Preview(this);
		imageview = (ImageView)findViewById(R.id.picview);
		((FrameLayout)findViewById(R.id.frameLayout1)).addView(mPreview);
		
		
	}
}
