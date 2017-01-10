package main.gpslogging;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;



import android.app.Activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;

import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class UploadPhotoActivity extends Activity {

	static final String KEY_DATE = "date";
	static final String KEY_CHECK = "check";
	static final String KEY_THUMB_URL = "thumb_url";
	
	static final int LIST_UI = 1;
	
	SharedPreferences shared;
	
	String imagePath = "/GPSLogging/photo";
	String thumbPath = "/GPSLogging/thumb";
	TextView back, titleText, selectAllText, statusBarText;
	ListView uploadphotoList;
	LazyAdapter adapter;
	ImageButton uploadPhotoBtn,uploadCancel;
	CheckBox photoAllCheckBox;
	Boolean uploadFlag = true;
	String url;
	String sizeType;
	UIHandler handler;
	
	 String phoneNumber;
	
	ArrayList<HashMap<String, String>> dataList;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    // TODO Auto-generated method stub
	    
	    shared = getSharedPreferences("loggingGPSSetting",MODE_PRIVATE);
	    
	    url = shared.getString("photoserverurl", "http://192.168.0.35/recv/uploadPhoto.aspx");
	    sizeType = shared.getString("resizetype", "Default");
	    
	    TelephonyManager telephony = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		phoneNumber = telephony.getLine1Number();
	    
	    handler = new UIHandler();
	    
	    setContentView(R.layout.uploadphoto);
	    
	    titleText = (TextView)findViewById(R.id.photoUploadTitleText);
	    titleText.setText(R.string.photoListTitleText);
	    statusBarText = (TextView)findViewById(R.id.uploadPhotoStatusText);
	    statusBarText.setText(R.string.emptyString);
	    
	    back = (TextView)findViewById(R.id.photoUploadTitleText);
	    back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	    
	    uploadCancel = (ImageButton)findViewById(R.id.cancelPhotoBtn);
	    
	    uploadCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				uploadFlag = false;
			}
		});
	    
	    photoAllCheckBox = (CheckBox)findViewById(R.id.photoAllCheckBox);
	    photoAllCheckBox.setText(R.string.selectAllText);
	    
	    uploadphotoList = (ListView)findViewById(R.id.uploadPhotoList);
	    
	    uploadphotoList.setItemsCanFocus(false);
	    
	    dataList = new ArrayList<HashMap<String, String>>();
	    
	    String imagePath1 = String.format("%s%s",Environment.getExternalStorageDirectory().getAbsolutePath(), thumbPath);
	    File filelist = new File(imagePath1);
	    if(filelist.exists() == false)
	    {
	    	filelist.mkdirs();
	    }
	    File[] arrayfiles = filelist.listFiles();
	    String str;
	    String[] date;
	    try{
	    	if(arrayfiles.length == 0)
	    	{
	    		photoAllCheckBox.setVisibility(View.INVISIBLE);
	    		
	    	}
		    for(int i = 0;i < arrayfiles.length;i++)
		    {
		    	str = arrayfiles[i].getName();
		    	date = str.split("_");
		    	HashMap<String, String> map = new HashMap<String, String>();
		    	map.put(KEY_THUMB_URL, str);
		    	map.put(KEY_CHECK, "false");
		    	map.put(KEY_DATE, date[1].substring(0, 4) + "/" + date[1].substring(4, 6) + "/" + date[1].substring(6, 8) + " " + date[1].substring(8, 10) + ":" + date[1].substring(10, 12));
		    	dataList.add(map);
		    }
	    }catch(Exception e){
	    	//photoAllCheckBox.setVisibility(View.INVISIBLE);
	    }
	    adapter = new LazyAdapter(this,dataList);
	    uploadphotoList.setAdapter(adapter);
	    
	    
	    
	    photoAllCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked)
				{
					
					adapter.setAllChecked(true);
					adapter.notifyDataSetChanged();
					photoAllCheckBox.setText(R.string.unselectAllText);
				}
				else
				{
					adapter.setAllChecked(false);
					adapter.notifyDataSetChanged();
					photoAllCheckBox.setText(R.string.selectAllText);
				}
			}
	    	
	    });
	    
	    /*
	    uploadphotoList.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				adapter.setChecked(arg2);
	            
				adapter.notifyDataSetChanged();
			}
	    	
	    });
	    */
	    
	    uploadPhotoBtn = (ImageButton)findViewById(R.id.uploadPhotoBtn);
	    
	    uploadPhotoBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(adapter.isCheckedConfrim.length == 0)
				{
					
					return;
				}
				uploadFlag = true;				
				final ProgressDialog prog = ProgressDialog.show(UploadPhotoActivity.this, "", "Processing...",true,false);
				new Thread(new Runnable() {
					public void run() {
						// TODO Auto-generated method stub
						
						try {
							
								
							for(int i = 0; i < adapter.isCheckedConfrim.length;i++)
							{
								if(uploadFlag == false)
								{
									//handler.sendEmptyMessage(LIST_UI);
									break;
								}
								HashMap<String, String> arraydata = new HashMap<String, String>();
								arraydata = adapter.data.get(i);
								if(adapter.isCheckedConfrim[i])
								{	
									
									if ( sendFile(arraydata.get(UploadPhotoActivity.KEY_THUMB_URL)) == true )
									
									{
										//adapter.removeItem(i);			            
										//adapter.notifyDataSetChanged();
									}						
								}
							}
						}catch(Exception e){}
						prog.dismiss();
						handler.sendEmptyMessage(LIST_UI);
					}
				}).start();
			}
			});
	    
	}
	
	public void listviewreDraw()
	{
		dataList = new ArrayList<HashMap<String, String>>();
	    
	    String imagePath1 = String.format("%s%s",Environment.getExternalStorageDirectory().getAbsolutePath(), imagePath);
	    File filelist = new File(imagePath1);
	    if(filelist.exists() == false)
	    {
	    	filelist.mkdirs();
	    }
	    File[] arrayfiles = filelist.listFiles();
	    String str;
	    String[] date;
	    for(int i = 0;i < arrayfiles.length;i++)
	    {
	    	str = arrayfiles[i].getName();
	    	date = str.split("_");
	    	HashMap<String, String> map = new HashMap<String, String>();
	    	map.put(KEY_THUMB_URL, str);
	    	map.put(KEY_CHECK, "false");
	    	map.put(KEY_DATE, date[1].substring(0, 4) + "/" + date[1].substring(4, 6) + "/" + date[1].substring(6, 8) + date[1].substring(8, 10) + ":" + date[1].substring(10, 12));
	    	dataList.add(map);
	    }
	    
	    photoAllCheckBox.setChecked(false);
	    //adapter = new LazyAdapter(this,dataList);
	    //uploadphotoList.setAdapter(adapter);
	    adapter.setData(dataList);
	    adapter.notifyDataSetChanged();
	}
	
	public Boolean sendFile(String filename)
	{
		
		//String url = shared.getString("photoserverurl", "http://192.168.0.61/upload.php");
		
		String imgPath = "/GPSLogging/photo/";
		
		
		try {
			
			
		         HttpClient client = new DefaultHttpClient();  
		         
		         HttpPost post = new HttpPost(url);
	         	 String attachfile = Environment.getExternalStorageDirectory().getAbsolutePath() + imgPath + filename;
		         File file = new File(attachfile);
		         
		         //FileBody bin = new FileBody(file.getAbsoluteFile());
		         Bitmap bm;
		         bm = BitmapFactory.decodeFile(attachfile);
		         ByteArrayOutputStream bos = new ByteArrayOutputStream();
		         bm.compress(CompressFormat.JPEG, 75, bos);
		         byte[] data = bos.toByteArray();
		         
	        	 
		         String filedata = Base64.encodeToString(data, 0);	         
	        	 
	        	 List<NameValuePair> params = new ArrayList<NameValuePair>();
		         
		         params.add(new BasicNameValuePair("dev_no", phoneNumber));
		         params.add(new BasicNameValuePair("sizeType", sizeType));
		         params.add(new BasicNameValuePair("filedata", filedata));
		         params.add(new BasicNameValuePair("filename", filename));
		         
		         
		         UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
		         post.setEntity(ent);
		         
		         HttpResponse response = client.execute(post);  
		         HttpEntity resEntity = response.getEntity();
		         //prog.dismiss();
		         if (resEntity != null) {
		        	//Log.i("response:", EntityUtils.toString(resEntity));
		               if ( EntityUtils.toString(resEntity).equals("OK") ){
		            	   backup(file,filename);
		            	   return true;
		               }
		               else
		               {
		            	   return false;
		               }
		         }
		         
			
		} catch (Exception e) {
			//prog.dismiss();
		    e.printStackTrace();
		    return false;
		}
		return false;
	}
	
	private void backup(File sourceFile,String filename)
	{
	    FileInputStream fis = null;
	    FileOutputStream fos = null;
	    FileChannel in = null;
	    FileChannel out = null;
	    String imagePath1 = "/GPSLogging/sent";
	    String imagePath2 = "/GPSLogging/sent1";
	    File sourceFile1 = new File(String.format("%s%s/%s",Environment.getExternalStorageDirectory().getAbsolutePath(), thumbPath,filename));
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
	        backupFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + imagePath2 + "/" + filename);
	        dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + imagePath2);
	        if(dir.exists() == false)
	        	dir.mkdirs();
	        backupFile.createNewFile();
	        
	        fis = new FileInputStream(sourceFile1);
	        fos = new FileOutputStream(backupFile);
	        in = fis.getChannel();
	        out = fos.getChannel();

	        size = in.size();
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
	        sourceFile.delete();
	        sourceFile1.delete();
	    }
	}
	
	class UIHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			
			// TODO Auto-generated method stub			
			switch(msg.what)
			{
				case LIST_UI:
					listviewreDraw();
					break;					
			}
			super.handleMessage(msg);
		}
		
	}
	
	

}
