package main.gpslogging;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ConfigActivity extends Activity {

	/** Called when the activity is first created. */
	SharedPreferences.Editor edit;
	SharedPreferences shared;
	
	EditText intervalText, logServerUrlText, photoServerUrlText;
	Spinner resizeTypeCombo;
	//Button setConfigBtn;
	ArrayList<String> typeVal = new ArrayList<String>();
	TextView configTitleText, configReturnLabel, configReturnLabel1, intervalLabel, configSendLabel, configResizeLabel;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.configactivity);
	    
	    // TODO Auto-generated method stub
	    logServerUrlText = (EditText)findViewById(R.id.logServerUrlText);
	    photoServerUrlText = (EditText)findViewById(R.id.photoServerUrlText);
	    intervalText = (EditText)findViewById(R.id.intervalTextValue);
	    resizeTypeCombo = (Spinner)findViewById(R.id.resizeCombo);
	    //setConfigBtn = (Button)findViewById(R.id.setConfigBtn);
	    
	    configTitleText = (TextView)findViewById(R.id.configTitleText);
	    configReturnLabel = (TextView)findViewById(R.id.configReturnLabel);
	    configReturnLabel1 = (TextView)findViewById(R.id.sendServerUrlLabel1);
	    intervalLabel = (TextView)findViewById(R.id.intervalLabel);
	    configSendLabel = (TextView)findViewById(R.id.configSendLabel);
	    configResizeLabel = (TextView)findViewById(R.id.configResizeLabel);
	    
	    configTitleText.setText(R.string.settingTitleText);
	    configReturnLabel.setText(R.string.returnLabel);
	    configReturnLabel1.setText(R.string.sendServerUrl);
	    intervalLabel.setText(R.string.sendInterval);
	    configSendLabel.setText(R.string.sendSetting);
	    configResizeLabel.setText(R.string.resizeLabel);
	    
	    
	    shared = getSharedPreferences("loggingGPSSetting",MODE_PRIVATE);
	    edit = shared.edit();
	    
	    logServerUrlText.setText(shared.getString("logserverurl", "http://192.168.0.35/recv/recvGPS.aspx"));
	    photoServerUrlText.setText(shared.getString("photoserverurl", "http://192.168.0.35/recv/uploadPhoto.aspx"));
	    intervalText.setText(shared.getString("interval", "60"));
	    String resizeType = shared.getString("resizetype", "");
	    
	    ArrayAdapter<CharSequence> adapterCity =
             	new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        adapterCity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //ArrayList<String> typeVal = new ArrayList<String>();
        resizeTypeCombo.setAdapter(adapterCity);
        
        adapterCity.add("No Resize");
        adapterCity.add("XGA");
        adapterCity.add("SVGA");
        adapterCity.add("VGA");
        adapterCity.add("AVGA");
        adapterCity.add("QVGA");
        adapterCity.add("QCIF");
        
        typeVal.add("Default");
        typeVal.add("XGA");
        typeVal.add("SVGA");
        typeVal.add("VGA");
        typeVal.add("AVGA");
        typeVal.add("QVGA");
        typeVal.add("QCIF");
        
        for ( int i = 0; i < typeVal.size(); i ++ )
        {
        	if(resizeType.equals(typeVal.get(i)))
        	{
        		resizeTypeCombo.setSelection(i);
        		break;
        	}
        }
        
        
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		String resizeType = typeVal.get((int)resizeTypeCombo.getSelectedItemId());
		edit.putString("resizetype", resizeType);
						
		edit.putString("logserverurl", logServerUrlText.getText().toString());
		edit.putString("photoserverurl", photoServerUrlText.getText().toString());
		edit.putString("interval", intervalText.getText().toString());
		edit.commit();
		
		super.onDestroy();
	}

}
