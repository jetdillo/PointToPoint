package com.PointToPoint;

interface IPTPMsgService {
  String getPTPMsg();
  boolean hasNewMsg();
  void removeMsg();
  boolean msgIsGPS();
  String getPTPTypeMsg(String msgtype);
  Location msgToLocation(String gpsstr, in Location loc);
}
