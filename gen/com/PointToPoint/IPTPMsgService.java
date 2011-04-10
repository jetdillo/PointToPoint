/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/dillo/projects/rklabs/eclipse/PointToPoint/src/com/PointToPoint/IPTPMsgService.aidl
 */
package com.PointToPoint;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import android.location.Location;
public interface IPTPMsgService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.PointToPoint.IPTPMsgService
{
private static final java.lang.String DESCRIPTOR = "com.PointToPoint.IPTPMsgService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IPTPMsgService interface,
 * generating a proxy if needed.
 */
public static com.PointToPoint.IPTPMsgService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.PointToPoint.IPTPMsgService))) {
return ((com.PointToPoint.IPTPMsgService)iin);
}
return new com.PointToPoint.IPTPMsgService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getPTPMsg:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getPTPMsg();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_hasNewMsg:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.hasNewMsg();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_removeMsg:
{
data.enforceInterface(DESCRIPTOR);
this.removeMsg();
reply.writeNoException();
return true;
}
case TRANSACTION_msgIsGPS:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.msgIsGPS();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getPTPTypeMsg:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.getPTPTypeMsg(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_msgToLocation:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
android.location.Location _arg1;
if ((0!=data.readInt())) {
_arg1 = android.location.Location.CREATOR.createFromParcel(data);
}
else {
_arg1 = null;
}
android.location.Location _result = this.msgToLocation(_arg0, _arg1);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.PointToPoint.IPTPMsgService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.lang.String getPTPMsg() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPTPMsg, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean hasNewMsg() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_hasNewMsg, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void removeMsg() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_removeMsg, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public boolean msgIsGPS() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_msgIsGPS, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String getPTPTypeMsg(java.lang.String msgtype) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(msgtype);
mRemote.transact(Stub.TRANSACTION_getPTPTypeMsg, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public android.location.Location msgToLocation(java.lang.String gpsstr, android.location.Location loc) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.location.Location _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(gpsstr);
if ((loc!=null)) {
_data.writeInt(1);
loc.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_msgToLocation, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.location.Location.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getPTPMsg = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_hasNewMsg = (IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_removeMsg = (IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_msgIsGPS = (IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_getPTPTypeMsg = (IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_msgToLocation = (IBinder.FIRST_CALL_TRANSACTION + 5);
}
public java.lang.String getPTPMsg() throws android.os.RemoteException;
public boolean hasNewMsg() throws android.os.RemoteException;
public void removeMsg() throws android.os.RemoteException;
public boolean msgIsGPS() throws android.os.RemoteException;
public java.lang.String getPTPTypeMsg(java.lang.String msgtype) throws android.os.RemoteException;
public android.location.Location msgToLocation(java.lang.String gpsstr, android.location.Location loc) throws android.os.RemoteException;
}
