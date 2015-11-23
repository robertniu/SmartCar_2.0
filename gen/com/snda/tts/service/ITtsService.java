/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\github\\SmartCar_2.0\\src\\com\\snda\\tts\\service\\ITtsService.aidl
 */
package com.snda.tts.service;
public interface ITtsService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.snda.tts.service.ITtsService
{
private static final java.lang.String DESCRIPTOR = "com.snda.tts.service.ITtsService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.snda.tts.service.ITtsService interface,
 * generating a proxy if needed.
 */
public static com.snda.tts.service.ITtsService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.snda.tts.service.ITtsService))) {
return ((com.snda.tts.service.ITtsService)iin);
}
return new com.snda.tts.service.ITtsService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
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
case TRANSACTION_activate:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.activate();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_speak:
{
data.enforceInterface(DESCRIPTOR);
com.snda.tts.service.TtsTask _arg0;
if ((0!=data.readInt())) {
_arg0 = com.snda.tts.service.TtsTask.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _result = this.speak(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopCaller:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.stopCaller(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_stopCallerAll:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _result = this.stopCallerAll(_arg0);
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.snda.tts.service.ITtsService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
/**
         * Activates the permission to access all other methods,
         * which need internet connection and some very small data transition.
         * It is only needed to call once after apk install.
         * @param
         * @return
         *     0 if successful.
         *     -1 if network connection failed.
         *     -2 if there is no permission for the calling application. Read online doc to apply permission.
         */
@Override public int activate() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_activate, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
         * Sends speak task to TTS service
         * @param ttsTask
         *     The tts task object used in TTS task queue
         * @return
         *     0 if successful.
         */
@Override public int speak(com.snda.tts.service.TtsTask ttsTask) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((ttsTask!=null)) {
_data.writeInt(1);
ttsTask.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_speak, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
         * Removes the first task sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
@Override public int stopCaller(java.lang.String caller) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(caller);
mRemote.transact(Stub.TRANSACTION_stopCaller, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/**
         * Removes all tasks sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
@Override public int stopCallerAll(java.lang.String caller) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(caller);
mRemote.transact(Stub.TRANSACTION_stopCallerAll, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_activate = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_speak = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_stopCaller = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_stopCallerAll = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
/**
         * Activates the permission to access all other methods,
         * which need internet connection and some very small data transition.
         * It is only needed to call once after apk install.
         * @param
         * @return
         *     0 if successful.
         *     -1 if network connection failed.
         *     -2 if there is no permission for the calling application. Read online doc to apply permission.
         */
public int activate() throws android.os.RemoteException;
/**
         * Sends speak task to TTS service
         * @param ttsTask
         *     The tts task object used in TTS task queue
         * @return
         *     0 if successful.
         */
public int speak(com.snda.tts.service.TtsTask ttsTask) throws android.os.RemoteException;
/**
         * Removes the first task sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
public int stopCaller(java.lang.String caller) throws android.os.RemoteException;
/**
         * Removes all tasks sent by the caller.
         * @param caller
         *     The caller who sends the speak task
         * @return
         *     0 if successful.
         */
public int stopCallerAll(java.lang.String caller) throws android.os.RemoteException;
}
