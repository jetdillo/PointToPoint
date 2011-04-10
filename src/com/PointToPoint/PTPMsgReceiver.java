package com.PointToPoint;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;


public class PTPMsgReceiver extends BroadcastReceiver {

    public static final String GOT_MSG = "com.PointToPoint.PTPMsgReceiver.intent.action.GOT_MSG";
    private static final String SMS_REC_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    
    private StringBuilder sb = new StringBuilder();
    
    @Override
    public void onReceive(final Context context, final Intent intent) {
    	
    	String smsstr = null;
    	String bodystr = null;
    	String typefile = null;
    	
        if (intent.getAction().equals(PTPMsgReceiver.SMS_REC_ACTION)) {
           
        	Log.i("PTPMsgReceiver","onReceive triggered");
        	
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                for (Object pdu : pdus) {
                    SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                    sb.append(smsMessage.getDisplayOriginatingAddress());
                    sb.append(":");
                    sb.append(smsMessage.getDisplayMessageBody());
                    sb.append(" ");
                    bodystr = new String(smsMessage.getDisplayMessageBody());
                }
            }
           // Toast.makeText(context, "PTPMsgReceiver - " + sb.toString(), Toast.LENGTH_LONG).show();
           // Log.i("PTPMsgReceiver - Message is ", sb.toString());
           
            FileOutputStream fos = null;
            try {
                fos =  context.openFileOutput("ptpsms",Context.MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write(sb.toString());
                /* ensure that everything is
                 * really written out and close */
            
                osw.flush();
                osw.close();
            } catch (FileNotFoundException e ){
            	Log.e("PTPMsgReceiver CreateFile",e.getLocalizedMessage());
            	
            } catch (IOException e) {
            	Log.e("PTPMsgReceiver General IOException",e.getLocalizedMessage());
            }
             finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        // swallow
                    }
                }
            }
             
            pushPTPMsgToType(sb.toString(),context);
            
            Intent gotmsg = new Intent();
            gotmsg.setAction(GOT_MSG);
            gotmsg.putExtra("ptpmsg", sb.toString());
            context.sendBroadcast(gotmsg);
            
           }       
    }
    
public String getPTPMsg() {
    	String PTPMsg_str = new String(sb.toString());
    	return PTPMsg_str;
    }


private void pushPTPMsgToType(String fullmsg,Context context) {
	 
	 String datastr = null;
	 String numstr = null;
	 String outstr = null;
	  String fsname = null;
	   
	 //split the message string into the payload and receiver strings
	 
       //split from the position of the ":" to the end 	 
	   datastr = new String(fullmsg.substring((fullmsg.indexOf(":"))+1));
	   //split from the start of the string to the ":"  
	   numstr = new String (fullmsg.substring(0, (fullmsg.indexOf(":"))));
	   FileOutputStream fos = null;
	   
	   Log.i("pushPTPMsgToType","numstr:"+numstr+" datastr:"+datastr);
	   	   
	   if (msgIsGPS(datastr)) {
		
		  outstr = new String(datastr);
		  fsname = new String("ptpgps");
		  Log.i("pushPTPMsgToType",outstr+" is a GPS message");
		 
		  
	   } else {
		   	   outstr = new String(fullmsg);
		   	   fsname = new String("ptptxt");
		   	Log.i("pushPTPMsgToType",outstr+" is a Text message");
	   }
		   
           try {
        	   Log.i("pushPTPMsgToType","filename:"+fsname+" numstr:"+numstr+" datastr:"+datastr);
               fos =  context.openFileOutput(fsname,Context.MODE_PRIVATE);
               
               
               OutputStreamWriter osw = new OutputStreamWriter(fos);
               osw.write(outstr);
               osw.flush();
               osw.close();
           } catch (FileNotFoundException e ){
           	Log.e("PTPMsgReceiver CreateFile",e.getLocalizedMessage());
           	
           } catch (IOException e) {
           	Log.e("PTPMsgReceiver General IOException",e.getLocalizedMessage());
           }
            finally {
               if (fos != null) {
                   try {
                       fos.flush();
                       fos.close();
                   } catch (IOException e) {
                       // swallow
                   }
               }
           }
	   
Log.i("Read file contents as:", fullmsg);

 }

private boolean msgIsGPS (String msgstr) {
	return java.util.regex.Pattern.matches("(-?\\d+(\\.\\d+)? ?){2,3}", msgstr);
	//return java.util.regex.Pattern.matches("-?\\d+(\\.\\d+)? -?\\d+(\\.\\d+)?", msgstr);
}
}