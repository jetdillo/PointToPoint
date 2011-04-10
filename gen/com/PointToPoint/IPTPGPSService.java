/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/dillo/projects/rklabs/eclipse/PointToPoint/src/com/PointToPoint/IPTPGPSService.aidl
 */
package com.PointToPoint;
import java.lang.String;
import android.os.RemoteException;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Binder;
import android.os.Parcel;
import android.location.Location;
public interface IPTPGPSService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.PointToPoint.IPTPGPSService
{
private static final java.lang.String DESCRIPTOR = "com.PointToPoint.IPTPGPSService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an IPTPGPSService interface,
 * generating a proxy if needed.
 */
public static com.PointToPoint.IPTPGPSService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.PointToPoint.IPTPGPSService))) {
return ((com.PointToPoint.IPTPGPSService)iin);
}
return new com.PointToPoint.IPTPGPSService.Stub.Proxy(obj);
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
case TRANSACTION_getLocalLoc:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.getLocalLoc();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_getDist:
{
data.enforceInterface(DESCRIPTOR);
float _result = this.getDist();
reply.writeNoException();
reply.writeFloat(_result);
return true;
}
case TRANSACTION_strToLocation:
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
android.location.Location _result = this.strToLocation(_arg0, _arg1);
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
private static class Proxy implements com.PointToPoint.IPTPGPSService
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
public java.lang.String getLocalLoc() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getLocalLoc, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public float getDist() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
float _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getDist, _data, _reply, 0);
_reply.readException();
_result = _reply.readFloat();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public android.location.Location strToLocation(java.lang.String location_str, android.location.Location strloc) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.location.Location _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(location_str);
if ((strloc!=null)) {
_data.writeInt(1);
strloc.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_strToLocation, _data, _reply, 0);
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
static final int TRANSACTION_getLocalLoc = (IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getDist = (IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_strToLocation = (IBinder.FIRST_CALL_TRANSACTION + 2);
}
public java.lang.String getLocalLoc() throws android.os.RemoteException;
public float getDist() throws android.os.RemoteException;
public android.location.Location strToLocation(java.lang.String location_str, android.location.Location strloc) throws android.os.RemoteException;
}
