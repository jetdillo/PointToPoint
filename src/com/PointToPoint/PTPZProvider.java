package com.PointToPoint;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;

public class PTPZProvider {

	String providerStr;
	String providerURI;
	String providerPath;
	int providerType=0;
	private SharedPreferences ptpPrefs;
	
	
	public PTPZProvider() {

		
	}
	
	public double getAltitudeFromServer(Double latitude, Double longitude) {
	    double result = Double.NaN;
	    HttpClient httpClient = new DefaultHttpClient();
	    HttpContext localContext = new BasicHttpContext();
	    String url = "http://gisdata.usgs.gov/"
	            + "xmlwebservices2/elevation_service.asmx/"
	            + "getElevation?X_Value=" + String.valueOf(longitude)
	            + "&Y_Value=" + String.valueOf(latitude)
	            + "&Elevation_Units=METERS&Source_Layer=-1&Elevation_Only=true";
	    HttpGet httpGet = new HttpGet(url);
	    try {
	        HttpResponse response = httpClient.execute(httpGet, localContext);
	        HttpEntity entity = response.getEntity();
	        if (entity != null) {
	            InputStream instream = entity.getContent();
	            int r = -1;
	            StringBuffer respStr = new StringBuffer();
	            while ((r = instream.read()) != -1)
	                respStr.append((char) r);
	            String tagOpen = "<double>";
	            String tagClose = "</double>";
	            if (respStr.indexOf(tagOpen) != -1) {
	                int start = respStr.indexOf(tagOpen) + tagOpen.length();
	                int end = respStr.indexOf(tagClose);
	                String value = respStr.substring(start, end);
	                result = Double.parseDouble(value);
	            }
	            instream.close();
	        }
	    } catch (ClientProtocolException e) {} 
	    catch (IOException e) {}
	    return result;
	}
	
	/*
	public void getAltitudeFromFile(Double latitude, Double longitude) {
		
	}
    */
	
}
