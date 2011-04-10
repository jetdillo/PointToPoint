package com.PointToPoint;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.location.Location;

import android.app.Service;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;

import android.os.Handler;
import android.os.RemoteException;
import android.content.Context;
import android.content.Intent;

public class PTPMsgService extends Service {
	private static final String LOG_TAG = "PTPMsgService";
	private StringBuffer ssb = new StringBuffer(" ");
	private Handler serviceHandler = null;
	private String msgstr = new String("NONE");
	private String prev_msg = new String("NONE");
	private static int msg_index = 0;
	private int GPSMSG = 0;
	private int TXTMSG = 1;
		
	private final IPTPMsgService.Stub binder = new IPTPMsgService.Stub() {
		public String getPTPMsg() {
			 FileInputStream fis = null;
			 
			 Log.i("INFO","msgstr="+msgstr);
			   try {
				   	fis = openFileInput("ptpsms");
				   	byte[] msgbuf = new byte[fis.available()];
				   	while (fis.read(msgbuf) !=-1) {}
				   	   msgstr = new String(msgbuf);
				   	   
				   	   Log.i("Read file contents as:", msgstr);
				   	   
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
	    Log.i("Read file contents as:", msgstr);
	    
		return msgstr;	   
	  }

   public String getPTPTypeMsg(String msgtype) {
	   
	   String fsname = null;
	   String msgstr = null;
	   FileInputStream fis = null;
	   
	   Log.i("INFO","In getPTPTypeMsg");
	   
	   if (msgtype.equals("gps")) {
		   fsname = new String("ptpgps");
	   } else {
		   fsname = new String("ptptxt");
	   }
	   try {
	        fis = openFileInput(fsname);
	        byte[] msgbuf = new byte[fis.available()];
	        while(fis.read(msgbuf) !=-1) {}
	             msgstr = new String(msgbuf);
	             Log.i("INFO","Read "+msgstr+" from "+fsname);
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
	   
	   return msgstr;
   }
	   		
		public void sendPTPLocMsg(Location myloc) {
			
			String latmsg_str = new String (String.valueOf(myloc.getLatitude()));
			String lonmsg_str = new String (String.valueOf(myloc.getLongitude()));
			String altmsg_str = new String (String.valueOf(myloc.getAltitude()));
			
			String locmsg_str = new String (latmsg_str+" "+lonmsg_str+" "+altmsg_str+" ");
			
			
			SmsManager sm = SmsManager.getDefault();
			
			String msgdest_str = new String("1-555-555-1212");
			sm.sendTextMessage(msgdest_str, null, locmsg_str,null, null);
			
		}
		
		public boolean hasNewMsg() {
		   boolean isNewMsg;	
		   Log.i("INFO", "in hasNewMsg");
		   String cur_msg = new String(getPTPMsg());
		   Log.i("INFO","hasNewMsg:"+"prev_msg="+prev_msg+" cur_msg="+cur_msg);
		   if(!(prev_msg.equals(cur_msg))) {
			   isNewMsg=true;
		   } else {
			   	   isNewMsg=false;
		   }
		   Log.i("INFO","isNewMsg:"+isNewMsg);		   
		   prev_msg= new String(cur_msg);
		   return isNewMsg;
		}
		
		public void removeMsg() {
			deleteFile("ptpsms");
		}
		
		public Location msgToLocation(String gpsstr, Location loc) {
			Location msgLoc = new Location(loc);
			String latstr = null;
			String lonstr = null;
			String altstr = null;
			
			latstr = new String(gpsstr.substring(0,(gpsstr.indexOf(" "))));
			lonstr = new String(gpsstr.substring((gpsstr.indexOf(" "))));
			//altstr = new String(gpsstr.substring((gpsstr.indexOf(",")),(gpsstr.indexOf(","))));
			//Altitude is in here somewhere...
			
			Log.i("msgToLocation","latstr="+latstr+" lonstr="+lonstr);
			
			
			msgLoc.setLatitude(Double.valueOf(latstr).doubleValue());
			msgLoc.setLongitude(Double.valueOf(lonstr).doubleValue());
			return msgLoc;
		}
		
		public boolean msgIsGPS() throws RemoteException {
			// TODO Auto-generated method stub
			//return java.util.regex.Pattern.matches("-?\\d+(\\.\\d+)? -?\\d+(\\.\\d+)?", msgstr);
			return java.util.regex.Pattern.matches("(-?\\d+(\\.\\d+)? ?){2,3}", msgstr);
		}
		
  };
	public IBinder onBind(Intent intent) {
		Log.i("PTPMsgService","Got bind request");
		return this.binder;
	}
	
}    