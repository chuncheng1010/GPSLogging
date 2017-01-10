package main.gpslogging;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageViewActivity extends Activity {

	ImageView img;
	String imgpath;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
	    setContentView(R.layout.imageviewactivity);
	    img = (ImageView)findViewById(R.id.imageView1);
	    imgpath = getIntent().getStringExtra("imgpath");
	    
	    Bitmap bitmap = BitmapFactory.decodeFile(imgpath);        
        img.setImageBitmap(bitmap);
	    
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		savedInstanceState.putString("imgpath", imgpath);
		super.onRestoreInstanceState(savedInstanceState);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		imgpath = outState.getString("imgpath");
		super.onSaveInstanceState(outState);
	}

}
