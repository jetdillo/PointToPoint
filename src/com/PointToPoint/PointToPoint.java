package com.PointToPoint;

import com.PointToPoint.PTPComms.CommMsgReceiver;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Gallery.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class PointToPoint extends Activity {
   
	private static final int MENU_COMMS = Menu.FIRST + 4;
	private static final int MENU_MAP = Menu.FIRST + 3;
	private static final int MENU_CALC = Menu.FIRST + 2;
	private static final int MENU_SETTINGS = Menu.FIRST +1;
	private static final int MENU_ABOUT= Menu.FIRST; 
 
    LinearLayout mLinearLayout;
    LocationManager lm;
    LocationProvider lp;
  
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
 
         
        mLinearLayout = new LinearLayout(this);

        ImageView i = new ImageView(this);
        //i.setImageResource(R.drawable.tankdillo320);
        i.setImageResource(R.drawable.mpingwe_splash);
        i.setAdjustViewBounds(true); 
        i.setLayoutParams(new Gallery.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

        mLinearLayout.addView(i);
        setContentView(mLinearLayout);
        
        Criteria gpsCrit = new Criteria();
        String providerStr = new String();
        
        gpsCrit.setAltitudeRequired(true);
        //gpsCrit.setCostAllowed(true);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        
        if (!(lm.isProviderEnabled(LocationManager.GPS_PROVIDER))) { 
     
         	  AlertDialog.Builder ad = new AlertDialog.Builder(this);
         	  ad.setMessage("PointToPoint needs to have the GPS turned on.")
         	         .setCancelable(false)
         	         .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
         	             public void onClick(DialogInterface dialog, int id) {
         	            	 Toast.makeText( PointToPoint.this, "Launching Settings", Toast.LENGTH_SHORT ).show();
                         	 Intent GpsIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                         	 startActivity(GpsIntent);
         	             }
         	         })
         	         .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
         	             public void onClick(DialogInterface dialog, int id) {
         	                  dialog.cancel();
         	                  finish();
         	             }
         	         });
         	  AlertDialog GPS_alert = ad.create();
     	                  GPS_alert.show();
        }
        
        
    }
    
    
    /* Creates the menu items  */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_COMMS, 0, "Comms");
        menu.add(0, MENU_MAP, 0, "Map");
        menu.add(0,MENU_CALC,0,"Calc");
        menu.add(0, MENU_SETTINGS,0,"Settings");
        menu.add(0,MENU_ABOUT,0,"About");
        return true;
    }
   
    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent mIntent;
        double [] posArray = new double[4];
        
        switch (item.getItemId()) {
        case MENU_COMMS:
        	 mIntent = new Intent(PointToPoint.this,PTPComms.class);
        	 Log.i("INFO","Launching PTPComms from Menu");
            startActivity(mIntent);
           
            return true;
        case MENU_MAP:
        	
        	posArray[0] = Double.valueOf("-122.3456");
        	posArray[1] = Double.valueOf("37.1234");
        	posArray[2] = Double.valueOf("-122.3789");
        	posArray[3] = Double.valueOf("37.1567");
        	
        	mIntent = new Intent(PointToPoint.this,PTPMap.class);
        	mIntent.putExtra("ptpcoords",posArray);
        	Log.i("INFO","Launching PTPMap from Menu");
        	startActivity(mIntent);
            return true;
            
        case MENU_CALC:
             calcSelectDialog();        	
             return true;

        case MENU_SETTINGS:
        	mIntent = new Intent(PointToPoint.this,PTPSettings.class);
        	Log.i("INFO","Launching PTPSettings from Menu");
        	startActivity(mIntent);
        	return true;
        }
        
        return false;
    }
    private void calcSelectDialog() { 
    	new AlertDialog.Builder(this) 
    	.setTitle(R.string.app_calc) 
    	.setItems(R.array.calculators, 
    	new DialogInterface.OnClickListener() { 
    	public void onClick( 
    	DialogInterface dialoginterface, int i) { 
    		Intent cmIntent;
    	   switch(i) {
    	   
    	   case 0:
    		   cmIntent = new Intent(PointToPoint.this,PTPLinkMarginCalc.class);
    		   Log.i("INFO", "Launched LinkMarginCalc from Calc Menu");
    		   startActivity(cmIntent);
    		   return;
    		   
    	   case 1:
    		   cmIntent = new Intent(PointToPoint.this,PTPFZRCalc.class);
    		   Log.i("INFO", "Launched FZRCalc from Calc Menu");
    		   startActivity(cmIntent);
    		   return;   	
    	   }
    	} 
    	}) 
    	.show(); 
    	}
}