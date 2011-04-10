package com.PointToPoint;

/*PTPComms - Wireless site surveying in your pocket */
/*For my wireless homies around the world     */

import java.util.ArrayList;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Contacts.Phones;
import android.database.Cursor;

public class PTPComms extends Activity implements LocationListener {

    
String lat = new String("37.122436");
String lon = new String("-122.48464");
String alt = new String("0");
int my_endpt =0;
int msg_index = 0;

LocationManager lm;
LocationProvider lp;
Location curloc;

TextView endpt1pos;
TextView endpt2pos;
TextView comm_msg;

//private TextView rcvmsg = (TextView) findViewById(R.id.RcvMsgText);
EditText msgdest;
EditText msgtxt;

private double [] coordBundle = new double[4];

private ListView msgLv;
private List<String> msgList = new ArrayList<String>();
private ArrayAdapter<String> msgAdapter;


private SmsManager sm;

private IPTPMsgService msg_service;
private IPTPGPSService gps_service;
private boolean msgbound;
private boolean gpsbound;

private final int MAP_RESULT=0;
private static final int MENU_ATTITUDE = Menu.FIRST + 4;
private static final int MENU_MAP = Menu.FIRST + 3;
private static final int MENU_CALC = Menu.FIRST + 2;
private static final int MENU_SETTINGS = Menu.FIRST +1;
private static final int MENU_ABOUT= Menu.FIRST; 

private ServiceConnection msg_conn = new ServiceConnection() {
	public void onServiceConnected(ComponentName classname,IBinder iservice) {
            
		   msg_service = IPTPMsgService.Stub.asInterface(iservice);
		   Toast.makeText(PTPComms.this,"connected to Msg Service",Toast.LENGTH_SHORT).show();
		   msgbound = true;
	}
	public void onServiceDisconnected(ComponentName classname) {
		msg_service = null;
		Toast.makeText(PTPComms.this, "disconnected from Msg Service",Toast.LENGTH_SHORT).show();
		msgbound = false;
	}
};


@Override
public void onCreate(Bundle savedInstanceState) {
	
    super.onCreate(savedInstanceState);    
    setContentView(R.layout.main);
    
   // Log.e("INFO","past setContentView");
   Criteria gpsCrit = new Criteria();
   String providerStr = new String();
   
   gpsCrit.setAltitudeRequired(true);
   //gpsCrit.setCostAllowed(true);
   lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
   
   if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) { 
	   lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000,1l, this); 
	 
        /*Even though we just started up, we should try to figure out where we are */
	   curloc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
   
        Log.i("INFO","past getLastKnownLocation call");     
          
       /* 
       float latf = (float) curloc.getLatitude();
       float lonf = (float) curloc.getLongitude();
       float altf = (float) curloc.getAltitude();
   
       String curlat = new String (String.valueOf(latf));
       String curlon = new String (String.valueOf(lonf));
       String curalt = new String (String.valueOf(altf));
   
      //Update the Comms display with our current position
   
       endpt1pos = (TextView) findViewById(R.id.LocalPosTxt);
	   endpt1pos.setText(curlat+" "+curlon+" "+curalt);
	
	   */
	
	   //Set up the Adapters to populate the ListView
  
       msgLv = (ListView) findViewById(R.id.MsgListView);
   
	   String init_msg = new String("No new messages");
	   msgList.add(init_msg);
	   msgAdapter = new ArrayAdapter<String>(this,R.layout.msg_item,msgList);
	
	   msgLv.setAdapter(msgAdapter);
	   //msgLv.setOnItemClickListener(msgClickListener);
	   msgLv.setSelection(0);
   
       sm = SmsManager.getDefault();
       Log.e("INFO","past SmsManager call");
    
       IntentFilter msg_filter = new IntentFilter("com.PointToPoint.PTPMsgReceiver.intent.action.GOT_MSG");
    
       CommMsgReceiver msg_receiver = new CommMsgReceiver();
    
       registerReceiver(msg_receiver,msg_filter);
      } else {
    	  AlertDialog.Builder ad = new AlertDialog.Builder(this);
    	  ad.setMessage("The PointToPoint Comms and Map Activities need the GPS turned on")
    	         .setCancelable(false)
    	         .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
    	             public void onClick(DialogInterface dialog, int id) {
    	            	 Toast.makeText( PTPComms.this, "Launching Settings", Toast.LENGTH_SHORT ).show();
                    	 Intent GpsIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                    	 startActivity(GpsIntent);
    	             }
    	         })
    	         .setNegativeButton("Exit to Menu", new DialogInterface.OnClickListener() {
    	             public void onClick(DialogInterface dialog, int id) {
    	                  dialog.cancel();
    	                  finish();
    	             }
    	         });
    	  AlertDialog GPS_alert = ad.create();
	                  GPS_alert.show();
   }
}

public void onStart() {
	super.onStart();
	if (!msgbound) {
		this.bindService(new Intent(PTPComms.this,PTPMsgService.class),msg_conn, Context.BIND_AUTO_CREATE);
		msgbound=true;
	}
	lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE); 
    lp = lm.getProvider(LocationManager.GPS_PROVIDER);  
      if (lp != null) { 
          lm.requestLocationUpdates(lp.getName(),0, 10, locationListener);           
      }
	  
}

@Override
public void onResume() {
	   super.onResume();
	   if (!msgbound) {
			this.bindService(new Intent(PTPComms.this,PTPMsgService.class),msg_conn, Context.BIND_AUTO_CREATE);
			msgbound=true;
		}
	
}

public void onPause() {
	super.onPause();
	if (msgbound) {
		this.unbindService(msg_conn);
		msgbound=false;
	}
}

public void onStop() {
	super.onStop();
	try {
	msg_service.removeMsg();
	} catch (RemoteException re) {
		   Log.e("PTPComms","service error",re);
	}
	if (msgbound) {
		this.unbindService(msg_conn);
		msgbound=false;
	}
}

public boolean onCreateOptionsMenu(Menu menu) {
	menu.add(0, MENU_ATTITUDE,0,"Attitude");
    menu.add(0, MENU_MAP, 0, "Map");
    menu.add(0,MENU_CALC,0,"Calc");
    menu.add(0, MENU_SETTINGS,0,"Settings");
    menu.add(0,MENU_ABOUT,0,"About");
    return true;
}

public void GPSChkBoxHandler(View target) {
	
	LocationManager alm = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
    if( alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER ) )
    {
    	Toast.makeText( PTPComms.this, "GPS on", Toast.LENGTH_SHORT ).show();
    }
    else
    {
    	Toast.makeText( PTPComms.this, "Please turn on GPS", Toast.LENGTH_SHORT ).show();
    	Intent GpsIntent = new Intent( Settings.ACTION_SECURITY_SETTINGS );
    	startActivity(GpsIntent);
    }
	
    lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Log.i("INFO","past LocationManager call");
    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,(float)10.0, locationListener);
}

public void SendBtnClickHandler(View target) {
	 Log.i("INFO", "in SendBtn ClickListener");
	 
	 EditText msgdest = (EditText) findViewById(R.id.MsgDest);
	 EditText msgtxt = (EditText) findViewById(R.id.SendMsgText);

	 String msgdest_str = msgdest.getText().toString();
	 Log.i("INFO","msgdest_str="+msgdest_str);
	 String msg_str = msgtxt.getText().toString();
	 Log.i("INFO","msg_str="+msg_str);
	 
     Log.i("INFO", "Sending message:"+msg_str+"to "+msgdest_str);
          sm.sendTextMessage(msgdest_str, null, msg_str,null, null);
          Log.i("INFO", "Message sent");
}

public void UpdateBtnClickHandler(View target) {
	   my_endpt = 1;
	   Log.i("INFO", "in UpdateBtnClickHandler");
	   String smsstr = null;
	   String msgstr = null;
	   String numstr = null;
	   String namestr = null;
	   StringBuffer msgbuf = new StringBuffer();
	   boolean isNewMsg = false;
	   
	   updateLocalPos();
	     
		 try {
			 isNewMsg = msg_service.hasNewMsg();
			 Log.i("INFO", "UpdateBtnClickHandler:isNewMsg="+isNewMsg);
			 if (isNewMsg) {
			  smsstr = msg_service.getPTPMsg();
			  
			  if(!isNull(smsstr)) {
				  msgstr = smsstr.substring((smsstr.indexOf(":"))+1);
				  numstr = smsstr.substring(0, (smsstr.indexOf(":")));
				  Log.i("INFO", "msgstr="+msgstr);
				  Log.i("INFO", "numstr="+numstr);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
			   if(msgIsGPS(msgstr)) {
			          endpt2pos = (TextView) findViewById(R.id.RemotePosVal);
			   	      endpt2pos.setText(msgstr);
			   	   } else {
			   		   namestr = getNameFromNumber(numstr);
			   		   Log.i("INFO", "namestr="+namestr);
			   		   
			   		   if (namestr.equals("NONE")) {
			   			   msgbuf.append(numstr);
			   		   } else {
			   			   msgbuf.append(namestr);
			   		   }
			   		   
			   		   msgbuf.append(" "+msgstr);
			   		  
			   		   Log.i("INFO", "msgbuf="+msgbuf);
			   	       msgList.add(msgbuf.toString());
			   		   msgAdapter.notifyDataSetChanged();
			   		   msgLv.setAdapter(msgAdapter);
			   		   msg_index++;
			   	   }
			  }
			 }
		   } catch (DeadObjectException doe) {
			   Log.e("PTPComms","service error",doe);
			   
		   } catch (RemoteException re) {
			   Log.e("PTPComms","service error",re);
		   }
	   } 

public void LinkBtnClickHandler(View target) {
	Log.i("INFO", "in LinkBtnClickHandler");
	my_endpt = 2;  
	Intent intent = new Intent(PTPComms.this,PTPLinkMarginCalc.class);
    startActivity(intent);
}

public void FZRBtnClickHandler(View target) {
	Log.i("INFO", "in FZRBtnClickHandler");
	Intent intent = new Intent(PTPComms.this,PTPFZRCalc.class);
	startActivity(intent);
}

public void MapBtnClickHandler(View target) {
	Log.i("INFO", "in MapBtnClickHandler ");
	
	/*Get postion data and Bundle (sic) it up to ship over to the PTPMap Activity */
	
	endpt1pos = (TextView) findViewById(R.id.LocalPosTxt);
	endpt2pos = (TextView) findViewById(R.id.RemotePosVal);
	
    String endpt1_str = new String(endpt1pos.getText().toString());
    String endpt2_str = new String(endpt2pos.getText().toString());
    
    Log.i("INFO","MapBtnClickHandler: endpt1_str="+endpt1_str+" endpt2_str="+endpt2_str);
    
    String [] lpos_str = endpt1_str.split(" ",3);
    String [] rpos_str = endpt2_str.split(" ", 3);
	
	coordBundle[0] = Double.valueOf(lpos_str[0]);
	coordBundle[1] = Double.valueOf(lpos_str[1]);
	coordBundle[2] = Double.valueOf(rpos_str[0]);
	coordBundle[3] = Double.valueOf(rpos_str[1]);
	
	
	Intent intent = new Intent(PTPComms.this,PTPMap.class);
	
	/*pack up positional data into an Intent.extra Bundle before we launch the MapActivity */
	
	intent.putExtra("ptpcoords",coordBundle);
	startActivityForResult(intent, MAP_RESULT);
	//startActivity(intent);
}

/*When we return from an Activity, we're going to get a similar bundle back */
/*Here's how we deal with that.                                             */

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    super.onActivityResult(requestCode, resultCode, intent);
    
    Bundle returnBundle = new Bundle(); 
     returnBundle = intent.getExtras();
     
     if ((returnBundle.get("lastPos"))!=null) {
    	 coordBundle = returnBundle.getDoubleArray("lastPos");
     }  
}


/*Callback to deal with when a LocationUpdate triggers our requestLocationUpdates call */
 
private final LocationListener locationListener = new LocationListener() {
	   public void onLocationChanged(Location loc) {
		   
		    double lat = loc.getLatitude();
	        double lon = loc.getLongitude();
	        double alt = loc.getAltitude();
	        Log.i("GPS", "location changed in main: lat="+lat+", lon="+lon+" alt="+alt);
	        
	            endpt1pos = (TextView) findViewById(R.id.LocalPosTxt);
	        	endpt1pos.setText(lat+" "+lon+" "+alt);
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
	
};

/*regex method to check if an SMS is a GPS fix vs. text message */
/*There are probably ways to game or break this, but it seems to mostly work */
private boolean msgIsGPS (String msgstr) {
	return java.util.regex.Pattern.matches("(-?\\d+(\\.\\d+)? ?){2,3}", msgstr);
}

public void msgClickListener(AdapterView<?>  parent, View  view, int position, long id) {
	
	Log.i("INFO","msgClickListener caught click on item"+position);
}



private boolean isNull (String str) {
    return str.equals(null);
}

/* Look up sender # in your Contacts list so we can show the name rather than # in the message scroll */
/* This is in case the SMS you got is a message rather than set of coordinates */
private String getNameFromNumber(String numstr) {
	
	String name = null;
	
	Log.i("INFO","at getNameFromNumber");
	
	Uri uri = Uri.withAppendedPath(Phones.CONTENT_FILTER_URL, Uri.encode(numstr));
   
	Cursor curs = getContentResolver().query(uri, 
	                    new String[] { Phones.DISPLAY_NAME }, null, null, null);
	if (curs != null && curs.moveToFirst()) {
	    name = curs.getString(curs.getColumnIndex(Phones.DISPLAY_NAME));
	    curs.close();
	} else {
		    name = new String("NONE");
	}
	Log.i("INFO","leaving getNameFromNumber");
	return name;
}

public void updateLocalPos() {
	 
	/*We might need to imperatively update our location. This method does that */  
	
	   curloc = new Location(lm.getLastKnownLocation(LocationManager.GPS_PROVIDER));
	   String curlat = new String (String.valueOf(curloc.getLatitude()));
	   String curlon = new String (String.valueOf(curloc.getLongitude()));
	   String curalt = new String (String.valueOf(curloc.getAltitude()));
	   endpt1pos = (TextView) findViewById(R.id.LocalPosTxt);
	   endpt1pos.setText(curlat+" "+curlon+" "+curalt);
}

public void updateComms(String smsstr) {
	   my_endpt = 1;
	   Log.i("INFO", "in UpdateComms");
	   String msgstr = null;
	   String numstr = null;
	   String namestr = null;
	   StringBuffer msgbuf = new StringBuffer();
	   boolean isNewMsg = false;
	   
	   updateLocalPos();
	     
			  if(!isNull(smsstr)) {
				  msgstr = smsstr.substring((smsstr.indexOf(":"))+1);
				  numstr = smsstr.substring(0, (smsstr.indexOf(":")));
				  Log.i("INFO", "msgstr="+msgstr);
				  Log.i("INFO", "numstr="+numstr);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         
			   if(msgIsGPS(msgstr)) {
			          endpt2pos = (TextView) findViewById(R.id.RemotePosVal);
			   	      endpt2pos.setText(msgstr);
			   	   } else {
			   		   namestr = getNameFromNumber(numstr);
			   		   Log.i("INFO", "namestr="+namestr);
			   		   
			   		   if (namestr.equals("NONE")) {
			   			   msgbuf.append(numstr);
			   		   } else {
			   			   msgbuf.append(namestr);
			   		   }
			   		   
			   		   msgbuf.append(" "+msgstr);
			   		  
			   		   Log.i("INFO", "msgbuf="+msgbuf);
			   	       msgList.add(msgbuf.toString());
			   		   msgAdapter.notifyDataSetChanged();
			   		   msgLv.setAdapter(msgAdapter);
			   		   msg_index++;
			   	   }
			  }
    }

public class CommMsgReceiver extends BroadcastReceiver {
	
	Bundle msgBundle;
	String msgString;
	
	@Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(PTPMsgReceiver.GOT_MSG)) {
	          
	        	
	        	msgBundle = intent.getExtras();
	        	msgString = msgBundle.getString("ptpmsg");
	        	Log.i("INFO","Got msgString from MsgReceiver:"+msgString);
	        	updateComms(msgString);
		        }
		    }
}

public void onLocationChanged(Location location) {
	  curloc = new Location(location);
	    String lat = String.valueOf(curloc.getLatitude());
      String lon = String.valueOf(curloc.getLongitude());
      String  alt = String.valueOf(curloc.getAltitude());
      Log.e("GPS", "location changed: lat="+lat+", lon="+lon+" alt="+alt);
	
}

public void onProviderDisabled(String provider) {
	// TODO Auto-generated method stub
	
}

public void onProviderEnabled(String provider) {
	// TODO Auto-generated method stub
	
}

public void onStatusChanged(String provider, int status, Bundle extras) {
	// TODO Auto-generated method stub
	
}
}


