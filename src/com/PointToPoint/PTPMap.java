package com.PointToPoint;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.PointToPoint.PTPComms.CommMsgReceiver;
import com.PointToPoint.PTPMath.*;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

public class PTPMap extends MapActivity implements LocationListener {
 private MapController mc;
 private LocationManager map_lm;
 private LocationProvider map_lp;
 private Location myloc,remloc;
 private String remLocStr;
 private ViewGroup vg;
 private MyLocationOverlay localOverlay;
 private PTPSiteOverlay siteOverlay;
 private ItemizedOverlay sliceOverlay;
 private MapView mv;
  
 private SharedPreferences ptpPrefs;
 private Editor ptpPrefsEditor;
 private static final String ptpPrefStr = new String("ptpPrefs");
 
 //private IPTPGPSService gps_service;
 private IPTPMsgService msg_service;
 
 private CommMsgReceiver cmr = new CommMsgReceiver();
 
 private ProgressDialog PTPMap_pd;
 private Drawable site_marker;
 private Drawable fzr_marker;
 
 private double [] last_coords = new double[4];
 private String zSource = new String("gps");
 
 private boolean msgbound;
 //private boolean gpsbound;
 
 private static final int MENU_UPDATE = Menu.FIRST;
 private static final int MENU_UPDATE_FREQ = Menu.FIRST +1;
 
 private ServiceConnection msg_conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName classname,IBinder iservice) {
	            
			   msg_service = IPTPMsgService.Stub.asInterface(iservice);
			   Toast.makeText(PTPMap.this,"connected to Msg Service",Toast.LENGTH_SHORT).show();
			   msgbound = true;
		}
		public void onServiceDisconnected(ComponentName classname) {
			msg_service = null;
			Toast.makeText(PTPMap.this, "disconnected from Msg Service",Toast.LENGTH_SHORT).show();
			msgbound = false;
		}
	};
	
public class CommMsgReceiver extends BroadcastReceiver {
    	
    	Bundle msgBundle;
    	String msgString;
    	
    	@Override
    	    public void onReceive(Context context, Intent intent) {
    	        if (intent.getAction().equals(PTPMsgReceiver.GOT_MSG)) {
    	        	
    	        	msgBundle = intent.getExtras();
    	        	msgString = msgBundle.getString("ptpmsg");
    	        	Log.i("INFO","PTPMap: Got msgString from MsgReceiver:"+msgString);
    	        	
    	        	if (msgIsGPS(msgString)) {
    	        		remLocStr = new String(msgString);
    	        		myloc = map_lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	        		Location stubloc = new Location(LOCATION_SERVICE);
    	    			try {
							remloc = msg_service.msgToLocation(remLocStr,stubloc);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//*LALALALALALALALALALALALALA*.
						//WE SHOULD NOT BE DOING STUFF LIKE THIS IN A BROADCASTLISTENER BUT WE ARE 
						//*LALALALALALALALALALALALALA*.
						
						//Calculate out the Fresnel Zone for the first zone. 
						//This is the point midway between myloc and remloc
						
						PTPMath ptpmathlib = new PTPMath();
						
						float bearing = myloc.bearingTo(remloc);
						
						//Get the distance between the two points so we can do the First Fresnel Zone calculation
						
						double ptpDist = ptpmathlib.calcLinkDist(myloc, remloc);
						double firstFZR = ptpmathlib.calcFzrFirstZone(2.412, 0, ptpDist);
						
						//Now that we have the radius of the first FZR, we need to find out where it is. 
						//The First Fresnel Zone is midway between myloc and remloc
						
						double firstFZRPos = ptpDist / 2; 
						
						//Do path-loss and curvature calculations as low-hanging fruit.
						double fsdBLoss = ptpmathlib.calcFreeSpacePathLoss(2.412, ptpDist);
						double earthCurvature = ptpmathlib.earthCurvature(ptpDist, ptpmathlib.effEarthRadiusinKm());
						
						//Figure out where firstFZRPos is
						
						
						
    	        		drawConnection();
    	        	} else {
    	        		 Toast.makeText(PTPMap.this,msgString,Toast.LENGTH_SHORT).show();
    	        	
    		        }
    		    }
    }
  }  		
	
	@Override
public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String keystr;
		Bundle coordBundle;
 /*set up initial map view and widgets */	
				
	   setContentView(R.layout.ptpmapwin); 
	   mv = (MapView) findViewById(R.id.ptpMapView);
	   mv.setBuiltInZoomControls(true);
	   mv.setSatellite(true);
       vg = (ViewGroup) findViewById(R.id.ptpZoomCtl);
       map_lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       map_lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000,1l, this);
     
       myloc = new Location("gps");
       //We need to initialize the remote end with some value
       remloc = new Location("gps");
       
       myloc = map_lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
       
       last_coords = new double[4]; 
       
       IntentFilter msg_filter = new IntentFilter("com.PointToPoint.PTPMsgReceiver.intent.action.GOT_MSG");
      // CommMsgReceiver msg_receiver = new CommMsgReceiver();
      cmr = new CommMsgReceiver();
    registerReceiver(cmr,msg_filter);
       
       
       //Pull out the coordinates we stuffed into the Intent that launched us 
        
         
            if (savedInstanceState != null) {
    		Log.i("INFO","PTPMap:GOT SAVED INSTANCE STATE");
            } else {
    		      Intent MapIntent = getIntent();
           
    			  coordBundle = MapIntent.getExtras();
    		      last_coords = coordBundle.getDoubleArray("ptpcoords");
        	      myloc.setLatitude(last_coords[0]);
        	      myloc.setLongitude(last_coords[1]);
        	      remloc.setLatitude(last_coords[2]);
        	      remloc.setLongitude(last_coords[3]);
        	      Log.i("INFO","PTPMap pulled this from Bundle:");
        	      int i =0;
        	      for (i=0; i< 4;i++) {
        		  Log.i("INFO",Double.toString(last_coords[i]));
        	   }
    	  }
            
            
     /* Add the overlay for MyLocation */  
       localOverlay = new MyLocationOverlay(this, mv);
	   localOverlay.enableMyLocation();
	   localOverlay.enableCompass();
	   mv.getOverlays().add(localOverlay); 
	   mv.invalidate();
         
	}
	
public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ptpmapmenu,menu);
}
	

public void onStart() {
		super.onStart();
		if (!msgbound) {
			this.bindService(new Intent(PTPMap.this,PTPMsgService.class),msg_conn, Context.BIND_AUTO_CREATE);
			msgbound=true;
		}
		/*
		if (!gpsbound) {
			this.bindService(new Intent(PTPMap.this,PTPGPSService.class),gps_conn, Context.BIND_AUTO_CREATE);
			msgbound=true;
		}
		*/
	}
	
    @Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
    
    @Override
    public void onResume() {
    	   super.onResume();
    	int showCompass = 0;   
    	
    	
    	IntentFilter msg_filter = new IntentFilter("com.PointToPoint.PTPMsgReceiver.intent.action.GOT_MSG");
        cmr = new CommMsgReceiver();
        registerReceiver(cmr,msg_filter);
    	
    	localOverlay.enableMyLocation();
    	
    	ptpPrefs = getSharedPreferences(ptpPrefStr,MODE_PRIVATE);
    	
    	
    	if( ptpPrefs.contains("zSource")) {
    		String zs = ptpPrefs.getString("zSource",null);
    		Log.i("INFO","Got zSource from ptpPrefs as "+zs);
    		zSource = new String(zs);
    	}
    	
    	if( ptpPrefs.contains("compassState")) {
    		showCompass = ptpPrefs.getInt("compassState", -1);
    		Log.i("INFO","Got compassState from ptpPrefs as"+showCompass);
    		
    		if (showCompass == 1) {
        		localOverlay.enableCompass();
        	} else {
        		if (showCompass < 0) {
        			Log.e("ERROR","Could not retrieve compassState from ptpPrefs");
        
        		}
        		localOverlay.disableCompass();
        	}
    	} else {
    		Log.i("ERROR","Could not find compassState in ptpPrefs");
    	}
    	
    }

    @Override
    public void onPause() {
    	   super.onPause();
   
    	Intent pauseIntent = new Intent();   
    	   
    	if (msgbound) {
    		this.unbindService(msg_conn);
    		msgbound = false;
    	}
    	
    	unregisterReceiver(cmr);
    	
    	/*pack up and get ready to head back */
    	
    	last_coords = getLastLocs(myloc,remloc);
    	localOverlay.disableMyLocation();
    	localOverlay.disableCompass();
    	pauseIntent.putExtra("lastPos",last_coords);
    	setResult(RESULT_OK,pauseIntent);
    }
    
 public void onStop() {
    	super.onStop();
    	
    	if (msgbound) {
    		this.unbindService(msg_conn);
    		msgbound=false;
    	}
    	
    	/*Pack up last known positions into a Bundle to return to the caller, then shut down mapping */
    	Intent returnIntent = new Intent();
    	last_coords = getLastLocs(myloc,remloc);
    	localOverlay.disableMyLocation();
    	localOverlay.disableCompass();
    	
    	unregisterReceiver(cmr);
    	
    	returnIntent.putExtra("lastPos",last_coords);
    	setResult(RESULT_OK,returnIntent);
        //we're out...
        finish();
    }
    
 public void onLocationChanged(Location arg0) {
	        myloc = new Location(arg0);
		    String lat = String.valueOf(myloc.getLatitude());
	        String lon = String.valueOf(myloc.getLongitude());
	        String  alt = String.valueOf(myloc.getAltitude());
            Log.e("GPS", "location changed: lat="+lat+", lon="+lon+" alt="+alt);
            localOverlay.enableMyLocation();   
            stayFocused(myloc);
         
            drawRemoteEnd();
            mv.invalidate();
 }
  
 public void onProviderDisabled(String arg0) {
         Log.e("GPS", "provider disabled " + arg0);
         localOverlay.disableMyLocation();
         localOverlay.disableCompass();
 }
 public void onProviderEnabled(String arg0) {
         Log.e("GPS", "provider enabled " + arg0);
 }
 public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
         Log.e("GPS", "status changed to " + arg0 + " [" + arg1 + "]");
 } 
   
 
 private boolean getSharedPrefState(String prefkey,SharedPreferences ptpPrefs2) {
	 
   // CheckBoxPreference ckbox = (CheckBoxPreference) ptpPrefs2;
 	boolean haskey = false;
 	
 	if( ((Preference) ptpPrefs2).getKey().equals(prefkey)) {
 		 haskey = true;
 	} else {
 		     haskey = false;
 	}
 	return haskey;
 }
 
 private void stayFocused(Location loc) {
      GeoPoint mgp = getGeoPointOf(loc);
      mc = mv.getController();	  
      mc.setCenter(mgp);
      mc.animateTo(mgp); 
 } 
 
 private void drawConnection() {
	 
	 String lat = String.valueOf(myloc.getLatitude());
     String lon = String.valueOf(myloc.getLongitude());
     String  alt = String.valueOf(myloc.getAltitude());
     Log.e("GPS", "location changed: lat="+lat+", lon="+lon+" alt="+alt);
     localOverlay.enableMyLocation();   
     stayFocused(myloc);
  
     drawRemoteEnd();
     mv.invalidate();
 }
 
 private void drawRemoteEnd() {
	
	    fzr_marker = this.getResources().getDrawable(R.drawable.p2pfzr);
		site_marker = this.getResources().getDrawable(R.drawable.p2p_dish);
		siteOverlay= new PTPSiteOverlay(site_marker);
		
		Log.i("INFO","in drawRemoteEnd");

   try {    
	   
			String msgstr = new String(msg_service.getPTPTypeMsg("gps"));
			Log.i("INFO","drawRemoteEnd: msgstr is:"+msgstr);
			Location stubloc = new Location(LOCATION_SERVICE);
			remloc = msg_service.msgToLocation(msgstr,stubloc);
			
			siteOverlay.setLocs(myloc, remloc);
			
			 Double remlat = remloc.getLatitude()*1E6;
		     Double remlon = remloc.getLongitude()*1E6;
		     GeoPoint gp = new GeoPoint(remlat.intValue(),remlon.intValue()); 
		     List<Overlay> siteOverlayList = mv.getOverlays();
		     OverlayItem rem_overlay = new OverlayItem(gp,"","");
		     siteOverlay.addOverlay(rem_overlay);
		     siteOverlayList.add(siteOverlay);
		     
    } catch (RemoteException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
  }
}
 
private double[] getLastLocs(Location local, Location remote) {
	
	double [] pos = new double[4];
	myloc = map_lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	pos[0] = local.getLatitude();
	pos[1] = local.getLongitude();
	pos[2] = remote.getLatitude();
	pos[3] = remote.getLongitude();
	
	return pos;
	
}
 
    private boolean msgIsGPS (String msgstr) {
    	return java.util.regex.Pattern.matches("(-?\\d+(\\.\\d+)? ?){2,3}", msgstr);
    }
    
    private GeoPoint getGeoPointOf(Location loc) {
 	   Double latitude = loc.getLatitude()*1E6;                                                                                   
        Double longitude = loc.getLongitude()*1E6;
        GeoPoint geopoint = new GeoPoint(latitude.intValue(),longitude.intValue());
        return geopoint; 
    }
}
 