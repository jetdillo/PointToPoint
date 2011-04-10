package com.PointToPoint;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;


public class PTPSiteOverlay extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Location myloc;
    private Location remoteloc;
	private GeoPoint mgp;
	private GeoPoint rgp;
	
	public PTPSiteOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		
	}

	public void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}	
	
	@Override
	protected OverlayItem createItem(int i) {
	  return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	 public void draw(Canvas canvas, MapView mv, boolean shadow) {

	    	    Paint paint = new Paint();
	    	    paint.setARGB(255, 10, 10, 255);
	    	    paint.setAntiAlias(true);
	    	    paint.setStrokeWidth(3);
	    	
	            Point mypt = new Point();
	            Point rempt = new Point();
	            Projection proj = mv.getProjection();
	            proj.toPixels(mgp, mypt);
	            proj.toPixels(rgp, rempt);
	       
	       if (shadow == false) {
	       
	    	   canvas.drawLine(mypt.x, mypt.y, rempt.x, rempt.y, paint);     
	       }
	       super.draw(canvas, mv, shadow);
	    }
	     
	    public void setLocs(Location loc1, Location loc2) {
	    	
	    	 myloc = new Location(loc1);
	    	 remoteloc = new Location(loc2);
	    	 Double mylat = myloc.getLatitude()*1E6;
	         Double mylon = myloc.getLongitude()*1E6;
	         Double remotelat = remoteloc.getLatitude()*1E6;
	         Double remotelon = remoteloc.getLongitude()*1E6;
	         mgp = new GeoPoint(mylat.intValue(),mylon.intValue());
	         rgp = new GeoPoint(remotelat.intValue(),remotelon.intValue());
	    }
	    
	    @Override
	    public boolean onTap(GeoPoint gp, MapView mv) {
	    	
	    GeoPoint tgp = gp;
	    Double taplat = (tgp.getLatitudeE6())/1E6;
	    Double taplon = (tgp.getLongitudeE6())/1E6;
	    
	    Log.i("INFO", "PTPSiteOverlay.onTap hit overlay");
	    
	    Point tapPoint = new Point();
	    Projection tapProj = mv.getProjection();
	    
	    String tapString = new String("Lat="+taplat+"Lon="+taplon);
	    tapProj.toPixels(tgp,tapPoint);
	    return true; 
	    	
	    }

	    }
