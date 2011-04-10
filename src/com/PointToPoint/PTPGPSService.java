package com.PointToPoint;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.android.maps.GeoPoint;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.app.Service;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;

public class PTPGPSService extends Service implements LocationListener {

	String lat = new String("37.122436");
    String lon = new String("-122.48464");
    String alt = new String("0.0000");
    private LocationManager lm;
    private LocationProvider lp;
    private Location loc;
    private float[] dist_results;
			
private final IPTPGPSService.Stub binder = new IPTPGPSService.Stub() {
	
	public String getLocalLoc() {
	     
	    final String loc_str = new String(lat+" "+lon+" "+alt);
	    return loc_str;
	     
		}

	public float getDist() {
		
		Criteria cr = new Criteria();
		String gpsProvider = new String();
		cr.setAltitudeRequired(true);
		cr.setCostAllowed(true);
		gpsProvider = lm.getBestProvider(cr, true);

		lp =lm.getProvider(gpsProvider);
		 
		 //Get remote GPS position that's been stored for us. 
		 FileInputStream fis = null;
		 String posstr=null;
		 float dist=(float) 0.0;
		   try {
			   	fis = openFileInput("ptpsms");
			   	byte[] msgbuf = new byte[fis.available()];
			   	while (fis.read(msgbuf) !=-1) {}
			   	   posstr = new String(msgbuf);
			   	   
		   } catch (IOException e) {
			   Log.e("General IOE on trying to get update",e.getLocalizedMessage());
		   } finally {
			  if (fis != null ) {
				  try {
					  fis.close();
				  } catch (IOException e ) {
					  // just eat the IOE
				  }
			  }
		   }
		   if (msgIsGPS(posstr)) {
			   String [] rempos_str = posstr.split(" ");
			   
			   Location remloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			   
			   double remlat = Double.valueOf(rempos_str[0]);
			   double remlon = Double.valueOf(rempos_str[1]);
			   double remalt = Double.valueOf(rempos_str[2]);
			   double locallat = Double.valueOf(lat);
			   double locallon = Double.valueOf(lon);
			   double localalt = Double.valueOf(alt);
			   
			   remloc.setLatitude(remlat);
			   remloc.setLongitude(remlon);
			   remloc.setAltitude(remalt);
			   
			   Log.d("PTPGPS.getDist","remlat="+remlat+" remlon="+remlon+" remalt"+remalt+" locallat="+locallat+" locallon="+locallon+" localalt"+localalt);
			   
			   //we've got the local position and the remote position, now compute the distance
			   dist= (float) loc.distanceTo(remloc);
			   Log.i("INFO","Distance between the two points is "+dist);
		   } else {
			   	   dist=(float) 0.00;
			   	  
			   	   Log.e("PTPGPSService", "Last message was not a GPS fix");
		   }
		   //dist = dist_results[0];
		   return dist;
	}

public Location strToLocation(String loc_str, Location strLoc) {
		  
		
			String lat_str = new String(loc_str.substring(0,(loc_str.indexOf(" "))));
			String lon_str = new String(loc_str.substring((loc_str.indexOf(" "))));
			String alt_str = new String(loc_str.substring((loc_str.lastIndexOf(" "))));
			
			strLoc.setLatitude(Double.valueOf(lat_str).doubleValue());
			strLoc.setLongitude(Double.valueOf(lon_str).doubleValue());
			strLoc.setAltitude(Double.valueOf(alt_str).doubleValue());
			
			return strLoc;   
	   }
	   
};

	public IBinder onBind(Intent intent) {
		Log.i("PTPGPSSService","Got bind request");
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria cr = new Criteria();
		String gpsProvider = new String();
		cr.setAltitudeRequired(true);
		cr.setCostAllowed(true);
		gpsProvider = lm.getBestProvider(cr, true);
		lp =lm.getProvider(gpsProvider);
        lm.requestLocationUpdates(gpsProvider, 1l,1l, this);
		return this.binder;
	}

	@Override
    public void onDestroy() {
          super.onDestroy();
          Log.d( "PTPGPSService","onDestroy" );
    }


  public void onLocationChanged(Location arg0) {
		     loc = arg0;
		     lat = String.valueOf(arg0.getLatitude());
	         lon = String.valueOf(arg0.getLongitude());
	         alt = String.valueOf(arg0.getAltitude());
           Log.e("GPS", "location changed: lat="+lat+", lon="+lon+" alt="+alt);
   }
    
   public void onProviderDisabled(String arg0) {
           Log.e("GPS", "provider disabled " + arg0);
   }
   public void onProviderEnabled(String arg0) {
           Log.e("GPS", "provider enabled " + arg0);
   }
   public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
           Log.e("GPS", "status changed to " + arg0 + " [" + arg1 + "]");
   }
   private boolean msgIsGPS (String msgstr) {
		return java.util.regex.Pattern.matches("-?\\d+(\\.\\d+)? -?\\d+(\\.\\d+)?", msgstr);
	}
   
}    