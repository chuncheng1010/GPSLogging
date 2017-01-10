package main.gpslogging;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    public ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    String imagePath = "/GPSLogging/photo/";
	String thumbPath = "/GPSLogging/thumb/";
    
    HashMap<String, String> arraydata;
    public boolean[] isCheckedConfrim;
    //public ImageLoader imageLoader; 
    
    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
        isCheckedConfrim = new boolean[data.size()];
    }
    
    public void setAllChecked(boolean ischeked) {
        int tempSize = isCheckedConfrim.length;        
        for(int a=0 ; a<tempSize ; a++){
        	isCheckedConfrim[a] = ischeked;
        }
        	
    }
    
    public void setChecked(int position) {
        isCheckedConfrim[position] = !isCheckedConfrim[position];
        
    }
    
    public int getCount() {
        return data.size();
    }
    
    public void removeItem(int position)
    {
    	data.remove(position);
    }
    
    
    public Object getItem(int position) {
        return position;
    }
    
    public void setData(ArrayList<HashMap<String, String>> d)
    {
    	data = d;
    	isCheckedConfrim = new boolean[data.size()];
    }
    
    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        ViewHolder holder;
        if(convertView==null)
        {
            vi = inflater.inflate(R.layout.uploadlistview, null);
            holder = new ViewHolder();
            holder.date = (TextView)vi.findViewById(R.id.photodate); 
            holder.checkstate = (CheckBox)vi.findViewById(R.id.select);
            //checkstate.setChecked(((ListView)parent).isItemChecked(position));
            //checkstate.setFocusable(false);
            //checkstate.setClickable(false);
            
            /*
            holder.checkstate.setTag(position);
            holder.checkstate.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				int position1 = Integer.parseInt(buttonView.getTag().toString());
				//buttonView.setChecked(isCheckedConfrim[position]);
				//buttonView.setChecked(isChecked);
				isCheckedConfrim[position1] = isChecked;
				Log.d("check",String.valueOf(position1));
				//setChecked(position1);
			}
        	
        });
        */
           holder.thumb_image=(ImageView)vi.findViewById(R.id.imageView1); // thumb image
           
           /*
           holder.thumb_image.setTag(position);
           holder.thumb_image.setOnClickListener(new View.OnClickListener() {
			
        	@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position1 = Integer.parseInt(v.getTag().toString());
				HashMap<String, String>  arraydata1 = new HashMap<String, String>();
		        arraydata1 = data.get(position1);
		        String imgurl = arraydata1.get(UploadPhotoActivity.KEY_THUMB_URL);
		        imgurl = String.format("%s%s%s",Environment.getExternalStorageDirectory().getAbsolutePath(),imagePath,imgurl);
		        //Log.d("img",imgurl);
		        Intent intent = new Intent(v.getContext(),ImageViewActivity.class);
		        
		        intent.putExtra("imgpath", imgurl);
		        activity.startActivity(intent);
			}
		});
        */
           
        vi.setTag(holder);
        
        }
        else
        {
        	holder = (ViewHolder)vi.getTag();        	
        }
        
        holder.checkstate.setTag(position);
        holder.checkstate.setOnCheckedChangeListener(new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			// TODO Auto-generated method stub
			int position1 = Integer.parseInt(buttonView.getTag().toString());
			//buttonView.setChecked(isCheckedConfrim[position]);
			//buttonView.setChecked(isChecked);
			isCheckedConfrim[position1] = isChecked;
			Log.d("check",String.valueOf(position1));
			//setChecked(position1);
		}
    	
    });
        
        holder.thumb_image.setTag(position);
        holder.thumb_image.setOnClickListener(new View.OnClickListener() {
			
     	@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int position1 = Integer.parseInt(v.getTag().toString());
				HashMap<String, String>  arraydata1 = new HashMap<String, String>();
		        arraydata1 = data.get(position1);
		        String imgurl = arraydata1.get(UploadPhotoActivity.KEY_THUMB_URL);
		        imgurl = String.format("%s%s%s",Environment.getExternalStorageDirectory().getAbsolutePath(),imagePath,imgurl);
		        //Log.d("img",imgurl);
		        Intent intent = new Intent(v.getContext(),ImageViewActivity.class);
		        
		        intent.putExtra("imgpath", imgurl);
		        activity.startActivity(intent);
			}
		});
        
        arraydata = new HashMap<String, String>();
        arraydata = data.get(position);        
        //checkstate.setChecked(isCheckedConfrim[position]);        
        // Setting all values in listview
        holder.date.setText(arraydata.get(UploadPhotoActivity.KEY_DATE));
        
        String imgurl = arraydata.get(UploadPhotoActivity.KEY_THUMB_URL);
        imgurl = String.format("%s%s%s",Environment.getExternalStorageDirectory().getAbsolutePath(),thumbPath,imgurl);
        Bitmap bitmap = BitmapFactory.decodeFile(imgurl);        
        holder.thumb_image.setImageBitmap(bitmap);
        holder.checkstate.setChecked(isCheckedConfrim[position]);
        return vi;
    }
    class ViewHolder {
    	public TextView date;
    	public CheckBox checkstate;
    	public ImageView thumb_image;
    }
}