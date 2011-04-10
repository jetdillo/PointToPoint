package com.PointToPoint;

import android.location.Location;
import android.util.Log;
import java.lang.Math;
import com.PointToPoint.Constants;

    public class PTPMath {

   //Calculate the nth Fresnel zone using Fn= sqrt((n*wavelength*d1*d2)/d1+d2)	
   //A "slice" is a single 2D radius in the volume of LOS space between two points. 
   //d1 = distance from point 1 to arbitrary point P
   //d2 = distance from point 2 to arbitrary point P
   //We should be able to assemble a group of slices into a volume that represents the whole LOS space.
    	
	public float calcFzrSlice(int n, float wavelength, float d1, float d2) {
	float slice;
	slice = (float) Math.sqrt((n*wavelength*d1*d2)/d1+d2);
	return slice;
	}
	
	public double calcFzrFirstZone(double frequency, int units, double dist) {
		double firstzone = 0.00;
		
		if (units == 0) {
		firstzone = 17.32 * (Math.sqrt(dist/(4*frequency)));
		} else {
			    firstzone = 72.05 * (Math.sqrt(dist/(4*frequency)));
		}
		return firstzone; 
	}
	
    public double calcFreeSpacePathLoss(double frequency, double distance) {
	
	   double fspl = 0.0000;
	
      	fspl = 36.56 + (20*(Math.log10(frequency)) + (20*Math.log10(distance)));
	   return fspl;
    }
    
    public double effEarthRadiusinMiles() {
    	
    	return (4/3) * 3963;
    }
    public double effEarthRadiusinKm() {
    	return (4/3) * 6378;
    }

    public double earthCurvature(double linkdist, double eer)
    {
    	return Math.pow(linkdist,2)/(8 * eer);
    			
    }
    
     public float[] calcFzrSlices(int steps, float wavelength,float link_distance) {
	        float[] slices;
	        int i =1;
 	        float d1 = (float) 0.0;
	        float d2 = (float) 0.0;
	
	        slices = new float[steps];
	
	        for (i=1;i <=steps;i++) {
			    d1 = (link_distance/steps)*i;
			    d2 = link_distance - d1;
			    slices[i-1] = calcFzrSlice(i,wavelength,d1,d2);
			    Log.i("INFO", "d1="+d1+" d2="+d2+ " distance="+link_distance+" zone["+(i-1)+"]="+slices[i-1]+" ");
	        }
	
	return slices;
	
    }

    public double mWToDbm ( double mw) {
    double dbm = 0.0000;
    	dbm=(10*(Math.log10(mw))) + 30;
    	
    	return dbm;
    }
    
    public double dBMToMw( double dbm ) {
    	double mw = 0.0000;
    	  mw = Math.pow(10,(Math.log10(dbm)));
    	  return mw;
    }
    
    public double freqToWave(double freq) {
    	double wavelen  = com.PointToPoint.Constants.C / freq;
    	return wavelen;
    }
    
    public double calcLinkDist(Location loc1, Location loc2) {
    	
    	double dist = (double) 0.00;
    	
    	dist = (double) loc1.distanceTo(loc2);
    	
    	return dist;
    }
    	
}