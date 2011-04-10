package com.PointToPoint;

interface IPTPGPSService {

  String getLocalLoc();
  float getDist();
  Location strToLocation(String location_str, in Location strloc);
  
}
