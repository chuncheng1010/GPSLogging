package main.gpslogging;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class MainActivity extends Activity{
	
	SharedPreferences shared;
	
	TextView titleText,statusText, returnTextView, photoTextView;
	ImageView recImage;
	ImageButton startLogBtn, endLogBtn, takePhotoBtn, uploadPhotoBtn;
	
	Location currentPosition;
	Thread t;
	Boolean logFlag = false;
	int timeInterval;
	int TAKE_CAMERA = 1;
	String imagePath = "/GPSLogging/photo";
	String thumbPath = "/GPSLogging/thumb";
	String imageUrl;
	String fileName1;
	String USERAGENT = "Mozilla/5.0 (Linux; U; Android 3.1; en-us; en-us; sdk Build/MASTER) AppleWebKit/534.13 (KHTML, like Gecko) Version/4.0 Safari/534.13";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.activity_main);
        
        shared = getSharedPreferences("loggingGPSSetting",MODE_PRIVATE);
        
        titleText = (TextView)findViewById(R.id.titleText);
        statusText = (TextView)findViewById(R.id.statusText);
        returnTextView = (TextView)findViewById(R.id.textView2);
        photoTextView = (TextView)findViewById(R.id.textView3);
        
        recImage = (ImageView)findViewById(R.id.recImage);
        startLogBtn = (ImageButton)findViewById(R.id.startLogBtn);
        endLogBtn = (ImageButton)findViewById(R.id.endLogBtn);
        takePhotoBtn = (ImageButton)findViewById(R.id.takePhotoBtn);
        uploadPhotoBtn = (ImageButton)findViewById(R.id.uploadPhotoBtn);
        
        
        titleText.setText(R.string.mainTitle);
        statusText.setText(R.string.emptyString);
        returnTextView.setText(R.string.returnLabel);
        photoTextView.setText(R.string.photoTextLabel);
        
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

    	// Define a listener that responds to location updates
    	LocationListener locationListener = new LocationListener() {
    	    public void onLocationChanged(Location location) {
    	      // Called when a new location is found by the network location provider.
    	    	getGPSData(location);    	      
    	    }

    	    public void onStatusChanged(String provider, int status, Bundle extras) {}

    	    public void onProviderEnabled(String provider) {}

    	    public void onProviderDisabled(String provider) {}
    	};

    	// Register the listener with the Location Manager to receive location updates
    	locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    	
    	Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            String message = String.format(
                    "Current Location \n Longitude: %1$s \n Latitude: %2$s",
                    location.getLongitude(), location.getLatitude()
            );
            currentPosition = location;
            Log.d("current",message);
        }
    	String timeval = shared.getString("interval", "60");
        timeInterval = Integer.parseInt(timeval) * 1000;
        t = new Thread(new Runnable()
        {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true)
				{
					if ( logFlag ){
						try {
							Thread.sleep(timeInterval);
							logGPSData();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}					
				}
			}
        	
        });
        t.start();
        
        // Logging GPS Data
        startLogBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recImage.setImageResource(R.drawable.rec);
				logFlag = true;
			}
		});
        
        endLogBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recImage.setImageResource(R.drawable.norec);
				logFlag = false;
			}
		});
        
        uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent it = new Intent(v.getContext(),UploadPhotoActivity.class);
				startActivity(it);
				/*
				HttpPost httppost = new HttpPost("https://www.google.com/voice/sms/send");
				httppost.setHeader("User-agent",USERAGENT);
				httppost.setHeader("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
				*/
			}
		});
        
        takePhotoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				 Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				 Calendar cal = Calendar.getInstance();
					TelephonyManager telephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
					String fileName = telephony.getLine1Number();
					String imagePath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + imagePath;
					File filelist = new File(imagePath1);
				    if(filelist.exists() == false)
				    {
				    	filelist.mkdirs();
				    }
				    fileName1 = String.format("%4d%02d%02d%02d%02d%02d.jpg",cal.get(Calendar.YEAR),cal.get(Calendar.MONTH) + 1,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
					fileName = String.format("/%s_%s",fileName,fileName1);
					fileName1 = fileName;
					fileName = String.format("%s%s", imagePath1,fileName);
					imageUrl = fileName;
					File phot = new File(fileName);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(phot));
					
					intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, TAKE_CAMERA);
			}
		});
        
        
    }
    
    
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub    	
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
			case 1:
				if (resultCode == RESULT_CANCELED) {
					return;
				}
				if (requestCode == TAKE_CAMERA && resultCode == RESULT_OK)
		         {
					
					String imagePath1 = Environment.getExternalStorageDirectory().getAbsolutePath() + thumbPath;
					File filelist = new File(imagePath1);
				    if(filelist.exists() == false)
				    {
				    	filelist.mkdirs();
				    }
				    
					String fileName = String.format("%s%s", imagePath1,fileName1);
					File phot = new File(fileName);
					try {
						phot.createNewFile();
						OutputStream stream = new FileOutputStream(phot);
						Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
						int width = bitmap.getWidth(); 
					    int height = bitmap.getHeight(); 
					    float scaleWidth = ((float) 50) / width; 
					    float scaleHeight = ((float) 50) / height; 
					    Matrix matrix = new Matrix();
					    matrix.postScale(scaleWidth, scaleHeight);
					    Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
					    resizedBitmap.compress(CompressFormat.JPEG, 100, stream);
					    stream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
		        }
				break;				
		}
	}



	public void getGPSData(Location location)
    {
    	currentPosition = location;
    	Log.d("lat",String.valueOf(currentPosition.getLatitude()));
    	Log.d("long",String.valueOf(currentPosition.getLongitude()));    	
    }
    
    public void logGPSData()
    {   
    	String LOG_FILE = "log.csv";				
        String[] line = new String[8];
        
        if(currentPosition == null)
        	return;
        
        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = mTelephonyMgr.getLine1Number();
        
        line[0] = phoneNumber;
        line[1] = String.valueOf(currentPosition.getLatitude()); // Latitude
        line[2] = String.valueOf(currentPosition.getLongitude()); // Longitude
        line[3] = String.valueOf(currentPosition.getAccuracy()); // Accuracy
        line[4] = String.valueOf(currentPosition.getAltitude()); // Altitude
        line[5] = String.valueOf(currentPosition.getTime()); // Time
        line[6] = String.valueOf(currentPosition.getSpeed()); // Speed
        line[7] = String.valueOf(currentPosition.getBearing()); // Bearing
        
        try {
        	FileOutputStream fos1 = openFileOutput(LOG_FILE,Context.MODE_APPEND);		        	
        	FileWriter fileWriter1 = new FileWriter(fos1.getFD());		        	
        	fileWriter1.close();
        	fos1.close();
        	int flag = 0;
        	File path = getFileStreamPath(LOG_FILE);
            FileReader fis = new FileReader(path);
            CSVReader reader = new CSVReader(fis);
        	if(reader.readNext() != null)
        		flag = 1;
        	reader.close();
        	fis.close();
        	
        	FileOutputStream fos = openFileOutput(LOG_FILE,Context.MODE_APPEND);
        	
        	FileWriter fileWriter = new FileWriter(fos.getFD());
	        CSVWriter writer = new CSVWriter(fileWriter);
	        if(flag == 0)
	        {
	        	String[] st = {"PhoneNumber","Latitude","Longitude","Accuracy","Altitude","Time","Speed","Bearing"};
	        	writer.writeNext(st);
	        }
        	writer.writeNext(line);		        	
        	writer.close();
        	fileWriter.close();
        	fos.close();
        	
        	backup(path, LOG_FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        String url = shared.getString("logserverurl", "http://192.168.0.35/recv/recvGPS.aspx");
		
		try {
		         HttpClient client = new DefaultHttpClient();  
		         
		         HttpPost post = new HttpPost(url);
	         	 
	        	 List<NameValuePair> params = new ArrayList<NameValuePair>();
		         
		         params.add(new BasicNameValuePair("dev_no", phoneNumber));
		         params.add(new BasicNameValuePair("lat", String.valueOf(currentPosition.getLatitude())));
		         params.add(new BasicNameValuePair("lon", String.valueOf(currentPosition.getLongitude())));
		         params.add(new BasicNameValuePair("acc", String.valueOf(currentPosition.getAccuracy())));
		         params.add(new BasicNameValuePair("alt", String.valueOf(currentPosition.getAltitude())));
		         params.add(new BasicNameValuePair("time", String.valueOf(currentPosition.getTime())));
		         params.add(new BasicNameValuePair("speed", String.valueOf(currentPosition.getSpeed())));
		         params.add(new BasicNameValuePair("bearing", String.valueOf(currentPosition.getBearing())));
		         
		         UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
		         post.setEntity(ent);
		       
		         HttpResponse response = client.execute(post);  
		         HttpEntity resEntity = response.getEntity();  
		         
		         if (resEntity != null) {    
		               if ( EntityUtils.toString(resEntity).equals("OK") )
		               {
		            	   // Set Status Bar
		            	   Log.d("Success", "Success");
		               }
		               else
		               {		            	   
		            	   // Set Status Bar error
		            	   Log.d("Fail", "Fail");
		               }		               
		         }
		} catch (Exception e) {
		    e.printStackTrace();
		}

    }
    
	private void backup(File sourceFile,String filename)
	{
	    FileInputStream fis = null;
	    FileOutputStream fos = null;
	    FileChannel in = null;
	    FileChannel out = null;
	    String imagePath1 = "/GPSLogging/log";
	    try
	    {
	    	
	        File backupFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + imagePath1 + "/" + filename);
	        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + imagePath1);
	        if(dir.exists() == false)
	        	dir.mkdirs();
	        backupFile.createNewFile();

	        fis = new FileInputStream(sourceFile);
	        fos = new FileOutputStream(backupFile);
	        in = fis.getChannel();
	        out = fos.getChannel();

	        long size = in.size();
	        in.transferTo(0, size, out);
	    }
	    catch (Throwable e)
	    {
	        e.printStackTrace();
	    }
	    finally
	    {
	        try
	        {
	            if (fis != null)
	                fis.close();
	        }
	        catch (Throwable ignore)
	        {}

	        try
	        {
	            if (fos != null)
	                fos.close();
	        }
	        catch (Throwable ignore)
	        {}

	        try
	        {
	            if (in != null && in.isOpen())
	                in.close();
	        }
	        catch (Throwable ignore)
	        {}

	        try
	        {
	            if (out != null && out.isOpen())
	                out.close();
	        }
	        catch (Throwable ignore)
	        {}
	        //sourceFile.delete();
	    }
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 1, 0, "Config");
		return super.onCreateOptionsMenu(menu);
	}



	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId())
		{
			case 1:
				Intent it = new Intent(this, ConfigActivity.class);
				startActivity(it);
				break;
		
		}
		return super.onOptionsItemSelected(item);
	}
	
}
