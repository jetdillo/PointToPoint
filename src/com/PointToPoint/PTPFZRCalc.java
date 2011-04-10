  package com.PointToPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.PointToPoint.PTPMath;

import java.util.regex.*;
import java.lang.Math;

public class PTPFZRCalc extends Activity {
	
	private EditText FZR_Dist_TV;
	private EditText FZR_Frequency;
	private Spinner FZR_DistUnit;
	private Spinner FZR_Channels;
	private TextView FZR_TV;
	private TextView FZR_Block_TV;
	
	private Button FZR_BackBtn;
	private Button FZR_FresnelBtn;
	private Button FZR_RangeBtn;
	private Button FZR_CalcBtn;
	private CheckBox FZR_ChkBox;
	
	private double[] FZRVars;
	private double FZRVal = 0;
	private double[ ] FZRSlice;
	private String[ ] FZRLabels;
	private String[ ] FZRStr;
	private double [ ] FZR_FreqArr =  { 2.412, 2.437, 2.462, 5.180, 5.200, 5.220, 5.240, 5.745, 5.765, 5.785, 5.805 };
	
	private LocationManager lm;
	
	private IPTPGPSService gps_service;
	private IPTPMsgService msg_service;
	private boolean gps_bound;
	private boolean msg_bound;

	private ServiceConnection gps_service_conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName classname,IBinder iservice) {
	            
			   gps_service = IPTPGPSService.Stub.asInterface(iservice);
			   Toast.makeText(PTPFZRCalc.this,"connected to GPS Service",Toast.LENGTH_SHORT).show();
			   gps_bound = true;
		}
		
		public void onServiceDisconnected(ComponentName classname) {
			gps_service = null;
			Toast.makeText(PTPFZRCalc.this, "disconnected from GPS Service",Toast.LENGTH_SHORT).show();
			gps_bound = false;
		}
	};

	private ServiceConnection msg_service_conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName classname,IBinder iservice) {
	            
			   msg_service = IPTPMsgService.Stub.asInterface(iservice);
			   Toast.makeText(PTPFZRCalc.this,"connected to Msg Service",Toast.LENGTH_SHORT).show();
			   msg_bound = true;
		}
		public void onServiceDisconnected(ComponentName classname) {
			msg_service = null;
			Toast.makeText(PTPFZRCalc.this, "disconnected from Msg Service",Toast.LENGTH_SHORT).show();
			msg_bound = false;
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
		
	    super.onCreate(savedInstanceState);    
	    setContentView(R.layout.fresnel_calc); 
	    
	    FZR_CalcBtn = (Button) findViewById(R.id.FZRCalcBtn);
	    FZR_Dist_TV = (EditText) findViewById(R.id.link_dist_val);
	    FZR_Frequency = (EditText) findViewById(R.id.freq_val);
	    FZR_ChkBox = (CheckBox) findViewById(R.id.FZR_GPS_ChkBox);
	    FZR_DistUnit = (Spinner) findViewById(R.id.UnitSpinner);
	   // FZR_Channels = (Spinner) findViewById(R.id.chanspinner);
	    
	    //Set up the drop-down "Spinner" for choosing English vs. Metric units. 
	   ArrayAdapter<CharSequence> Units =  ArrayAdapter.createFromResource(this, R.array.units, R.layout.spinner_view);

	    Units.setDropDownViewResource(R.layout.spinner_view_dropdown);
	    FZR_DistUnit.setAdapter(Units);
	    
	    Log.i("INFO","Past FZR_DistUnit setup");
	    
	    //Set up the drop-down "Spinner" for choosing which WiFi Channel to use
	    //This will be used to update the Frequency EditText
	   
	   /*
	    ArrayAdapter<CharSequence> Channels = ArrayAdapter.createFromResource(this, R.array.channels, R.layout.channel_spinner);
	    Channels.setDropDownViewResource(R.layout.spinner_view_dropdown);
	    FZR_Channels.setAdapter(Channels);
	 
	    
	    Log.i("INFO","Past FZR_Channels setup");
	    */
	    
	    //If the GPS Checkbox is checked, attach us to the PTPGPSService

	    FZR_ChkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	        {
	            if ( isChecked )
	            {
	            	LocationManager alm = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
	                if( alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) )
	                {
	                	Toast.makeText( PTPFZRCalc.this, "GPS on", Toast.LENGTH_SHORT ).show();
	                }
	                else
	                {
	                	Toast.makeText( PTPFZRCalc.this, "Please turn on GPS", Toast.LENGTH_SHORT ).show();
	                	Intent GpsIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
	                	startActivity(GpsIntent);
	                }

	            	if (!gps_bound) {
	        			bindService(new Intent(PTPFZRCalc.this,PTPGPSService.class),gps_service_conn, Context.BIND_AUTO_CREATE);
	        			Toast.makeText( PTPFZRCalc.this, "Press the Update button to sync local and remote positions", Toast.LENGTH_LONG ).show();
	        			FZR_Dist_TV.setText("GPS");
	        			FZR_Dist_TV.setEnabled(false);
	            	}
	            }
	            if ( !isChecked )
	            	if (gps_bound) {
	            		gps_bound = false;
	            		unbindService(gps_service_conn);
	            	}
	        }
	    });
	   
	    //If something is selected from the "Channels" Spinner, match the index of the selection with the corresponding text in the Channels array
	    //and put that into the Frequency EditText.
	    //In other words, if somebody selects "Channel 1", the Frequency EditText will be set to read "2.412"
	  /*
	    FZR_Channels.setOnItemSelectedListener(new OnItemSelectedListener() {
	    	
	        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
	          
	     	    FZR_Frequency = (EditText) findViewById(R.id.freq_val);
	     	    FZR_Frequency.setText(Double.toString(FZR_FreqArr[position]));
	        }

	        public void onNothingSelected(AdapterView<?> parentView) {
	            // We're just kinda not doing nothin' here. 
	        }

	    });
	   */
	        Log.e("INFO","past FZR_DistUnit Spinner setup");   
	}
	
	public void onStart() {
		super.onStart();
		/*
		if (!gps_bound) {
			this.bindService(new Intent(PTPFZRCalc.this,PTPGPSService.class),gps_service_conn, Context.BIND_AUTO_CREATE);
		}
		*/
	}
	
	public void onPause() {
		super.onPause();
		if (gps_bound) {
			gps_bound = false;
			this.unbindService(gps_service_conn);
		}
	}
	
	public void CalcFzClickHandler(View target) throws RemoteException {
		
		String FZRValStr;
		String FZRDistStr;
		String FZRFreqStr;
		String FZRDistUnitStr;
		String FZRChannelStr;
		String FZRRadiusStr;
		String FZRBlockStr;
		String FZRFeetStr = "Feet";
		String FZRMilesStr = "Miles";
		String FZRMetersStr = "Meters";
		String FZRKlicksStr = "Kilometers"; 
		
		
		final double FZRFeet = (double) 72.6;
		final double FZRMeter = (double) 17.26;
		
		double FZRDist = 0.00;
		double FZRRadius = 0.00;
		double FZRBlock = 0.00;

		Location myloc;
		Location remote_loc;
		
		
		double FZRFreqVal=(double)0.0;
		double gps_dist = (double)0.0;
		
		FZRDistUnitStr = FZR_DistUnit.getSelectedItem().toString();
		//FZRChannelStr = FZR_Channels.getSelectedItem().toString();
		FZRFreqStr = FZR_Frequency.getText().toString();
	    	
		FZRFreqVal = Double.valueOf(FZRFreqStr).doubleValue();

		FZRRadiusStr = new String("0.000");
	
		//If the user has selected Miles for Units we do one set of calculations
		//If they select Km, we do another. 
		//We actually have to do both here because some of the formulas here use feet in the calculation
		//and some use meters. 
		
		if( (FZRDistUnitStr.matches(FZRFeetStr)) || (FZRDistUnitStr.matches(FZRMilesStr)) ){
			Log.i("INFO", "Using US Units");
			
			//If we're not getting our data from the GPS we need to get stuff out of the TextEdit fields
			
	      if (!gps_bound) {		
			FZRDistStr = new String(FZR_Dist_TV.getText().toString());
			FZRDist = Double.valueOf(FZRDistStr).doubleValue();
	      } else {
	    	  //We are using the GPS, so we go get the distance from PTPGPSService which is running in the background
	    	  	  gps_dist= (double) gps_service.getDist();
	    	  	  FZRDist = (double) (FZRDist * 0.621371);
	      }
	           FZRRadius = (double) ( FZRFeet * (Math.sqrt(FZRDist/ (4*FZRFreqVal) ) ) );
	  	       FZRBlock = (double) (FZRRadius *0.20);
	  	       Log.i("INFO","FZRRadius="+(Double.toString(FZRRadius))+" "+FZRRadius);
	  	       
		}
		
		//As above, so below. 
		//User is using Metric as their units. 
		if( ( FZRDistUnitStr.matches(FZRKlicksStr)) || ( FZRDistUnitStr.matches(FZRMetersStr)) ) {
			Log.i("INFO", "Using Metric Units");
			if (!gps_bound) {
			FZRDistStr = new String(FZR_Dist_TV.getText().toString());
			FZRDist = Double.valueOf(FZRDistStr).doubleValue();
			} else {
				  gps_dist= (double) gps_service.getDist();
	    	  	  FZRDist = gps_dist/1000;
			}
			     FZRRadius = (double) (FZRMeter * (Math.sqrt(FZRDist/(4*FZRFreqVal) ) ) );
			     FZRBlock = (double) (FZRRadius *0.20);
			     Log.i("INFO","FZRRadius="+(Double.toString(FZRRadius))+" "+FZRRadius);
		}
		  if (!gps_bound) {
		   if (!this.validate()) {
			   return;
		   }
		  } else {
			  FZR_Dist_TV = (EditText) findViewById(R.id.link_dist_val);
			  FZR_Dist_TV.setText(Double.toString(gps_dist));
		  }  
		   
			   FZRRadiusStr = new String(Double.toString(FZRRadius));
	 		   FZRBlockStr = new String(Double.toString(FZRBlock)+" "+FZRDistUnitStr);
	 		   Log.i("INFO", " First Fresnel Zone Radius is:"+FZRRadiusStr);
	 			
			   	FZR_TV = (TextView) findViewById(R.id.fzr_val);
			   	FZR_Block_TV = (TextView) findViewById(R.id.fzr_clearance);
			    FZR_TV.setText(FZRRadiusStr+" "+FZRDistUnitStr);
			    FZR_Block_TV.setText(FZRBlockStr+" "+FZRDistUnitStr);
			   	
	}
	
	public void GPSChkBoxHandler(View target) {
		
		if (!gps_bound) {
			this.bindService(new Intent(PTPFZRCalc.this,PTPGPSService.class),gps_service_conn, Context.BIND_AUTO_CREATE);
		}
	    
	}
	
	private boolean validate() {
    boolean valid = false;
    int i = 0;
    
    Log.i("INFO", " In FZRCalc validate");   
    
    
        FZR_Dist_TV = (EditText) findViewById(R.id.link_dist_val);
        FZR_Frequency= (EditText) findViewById(R.id.freq_val);
        
        Log.i("INFO", "FZR validate after EditText findViewById");
        
        FZRVars = new double [2];
        FZRLabels = new String[2];
        FZRStr = new String[2];
        
        FZRLabels[0] = "Distance";
        FZRLabels[1] = "Frequency";
     
        
        Log.i("INFO", "FZR validate after FZRVars init");

        //        FZRVars[0] = Integer.parseInt(TXPower.getText().toString());

        
        FZRStr[0] = FZR_Dist_TV.getText().toString();
        FZRStr[1] = FZR_Frequency.getText().toString();
        
        Log.i("INFO", "FZR validate after FZRStr assigns");

        for (i=0;i <2;i++) {
        			if (!this.isNumeric(FZRStr[i])) {
        			    valid = false;
        			    new AlertDialog.Builder(this)
        			      .setMessage(FZRLabels[i]+" Must be a positive numeric value")
        			      .setPositiveButton("Oops",null)
        			      .show();
        			} else {
        					valid = true;
        					FZRVars[i] = Double.valueOf(FZRStr[i]).doubleValue();
        			}
        }

        return valid;
    }
	
public void FZRUpdateBtnHandler(View target) {
		   Log.i("INFO", "in FZRUpdateBtnHandler");
		   String msgstr = null;
		   
			   try {
				   msgstr = new String (msg_service.getPTPMsg());
				   if(msgIsGPS(msgstr)) {
				     //Save message string as a Location Object
					 //Get Local Position and save that as a Location as well
		     
				   	   }
			   } catch (DeadObjectException doe) {
				   Log.e("PointToPoint","service error",doe);
				   
			   } catch (RemoteException re) {
				   Log.e("PointToPoint","service error",re);
			   }
		   } 	
	
public boolean isNumeric(String numstr) {  
		 return java.util.regex.Pattern.matches("\\d+(\\.\\d+)?", numstr);  
} 
private boolean msgIsGPS (String msgstr) {
	return java.util.regex.Pattern.matches("-?\\d+(\\.\\d+)? -?\\d+(\\.\\d+)?", msgstr);
}
}
