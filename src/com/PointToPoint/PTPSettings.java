package com.PointToPoint;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

public class PTPSettings extends PreferenceActivity {
	
	public SharedPreferences ptpPrefs;
	public Editor ptpPrefsEditor;
	
    CheckBoxPreference gpsLaunchState;
    CheckBoxPreference compassState;
    CheckBoxPreference tiltState;
    ListPreference zdataSource;
    OnPreferenceClickListener PrefStateClickHandler;
    
    final int GPS_LAUNCH_STATE = R.id.gpsLaunchState;
    final int COMPASSSTATE = R.id.compassState;
    final int TILTSTATE = R.id.tiltState;
   
    private static final String ptpPrefStr = new String("ptpPrefs");
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.ptpprefs);
        //setContentView(R.xml.ptpprefs);
        
        PrefStateClickHandler onPrefStateClickHandler = new PrefStateClickHandler();
        
       gpsLaunchState = (CheckBoxPreference) findPreference("gpsLaunchState");
       compassState = (CheckBoxPreference) findPreference("compassState");
       tiltState = (CheckBoxPreference) findPreference("tiltState");
       zdataSource = (ListPreference) findPreference("zSource");
       
       gpsLaunchState.setOnPreferenceClickListener(onPrefStateClickHandler);
       compassState.setOnPreferenceClickListener(onPrefStateClickHandler);
       tiltState.setOnPreferenceClickListener(onPrefStateClickHandler);
       zdataSource.setOnPreferenceClickListener(onPrefStateClickHandler);
    }
    
 class PrefStateClickHandler implements OnPreferenceClickListener {

        public boolean onPreferenceClick(Preference pref) {
        	    boolean result;
        	    
        	    
        	    if (pref instanceof CheckBoxPreference ) {
        	    
                CheckBoxPreference CBP = (CheckBoxPreference)pref;
                Log.i("INFO","At PrefStateClickHandler");
                ptpPrefs = getSharedPreferences(ptpPrefStr,MODE_PRIVATE);
    	    	  ptpPrefsEditor = ptpPrefs.edit();
    	    	  
                try {
                	    
                        if( CBP.getKey().equals("gpsLaunchState")) {
                        	result = CBP.isChecked();
                        	Log.i("INFO","PrefStateClickHandler:gpsLaunchState result="+result);
                            if( CBP.isChecked() ) {
                            	Log.i("INFO","PrefStateClickHandler:gpsLaunchState=1");
                  	    	  ptpPrefsEditor.putInt("gpsLaunchState", 1);
                  	    	  CBP.setChecked(true);
                            } else {
                            	Log.i("INFO","PrefStateClickHandler:gpsLaunchState=0");
                        	ptpPrefsEditor.putInt("gpsLaunchState",0);
                        	CBP.setChecked(false);
                            }
                        
                        }
                        if( CBP.getKey().equals("compassState")) {
                        	result = CBP.isChecked();
                        	Log.i("INFO","PrefStateClickHandler:compassState result="+result);
                            if( CBP.isChecked() ) {
                            	Log.i("INFO","PrefStateClickHandler:compassState=1");
                  	    	  ptpPrefsEditor.putInt("compassState", 1);
                  	    	  CBP.setChecked(true);
                            } else {
                            	Log.i("INFO","PrefStateClickHandler:compassState=0");
                        	ptpPrefsEditor.putInt("compassState",0);
                        	CBP.setChecked(false);
                            }
                        }
                        
                        if( CBP.getKey().equals("tiltState")) {
                        	result = CBP.isChecked();
                        	Log.i("INFO","PrefStateClickHandler:tiltState result="+result);
                            if( CBP.isChecked() ) {
                            	Log.i("INFO","PrefStateClickHandler:tiltState=1");
                  	    	  ptpPrefsEditor.putInt("tiltState", 1);
                  	    	  CBP.setChecked(true);
                            } else {
                            	Log.i("INFO","PrefStateClickHandler:tiltState=0");
                        	ptpPrefsEditor.putInt("tiltState",0);
                        	CBP.setChecked(false);
                        
                            }
                        
                        }
                          ptpPrefsEditor.commit();
                          return CBP.isChecked();
                          
                } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                }
                
        	  }
        	  if (pref instanceof ListPreference) {
        		  ListPreference LP = (ListPreference) pref;
        		  ptpPrefs = getSharedPreferences(ptpPrefStr,MODE_PRIVATE);
    	    	  ptpPrefsEditor = ptpPrefs.edit();
    	    	  try {
    	    		if (LP.getKey().equals("zSource")) {
    	    			String zs = new String (LP.getValue());
    	    			ptpPrefsEditor.putString("zSource", zs);
    	    			ptpPrefsEditor.commit();
    	    			//float ptpDist = 
    	    		}
    	    	  } catch (Exception e) {
    	    		  e.printStackTrace();
    	    	  }
        	  }
        	  return false;
      }
 }
 private boolean isNull (String str) {
	    return str.equals(null);
	}
    
}

