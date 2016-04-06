package com.dd.menyoo.network;

import android.util.Log;

import com.dd.menyoo.BaseClass;
import com.dd.menyoo.R;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.SharedPrefManager;
import com.dd.menyoo.common.SignalRListener;
import com.dd.menyoo.listeners.ICallBack;

import java.net.URLDecoder;
import java.util.Timer;
import java.util.TimerTask;

import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.Constants;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.http.CookieCredentials;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by Administrator on 02-Mar-16.
 */
public class SignalRManager {

    private HubProxy mHub = null;
    private HubConnection mConnection = null;
    private static SignalRManager mSignalR = null;
    private static TimerTask backgroundCheck;
    public static boolean shouldAutoConnect = true;
    volatile boolean isConnected;
    Thread newThread;

    private SignalRManager() {
    }

    public static SignalRManager getInstance() {
        if (mSignalR == null) {
            mSignalR = new SignalRManager();
            shouldAutoConnect = true;

        }

        return mSignalR;
    }


    public void cancelDisconnection() {
        if (backgroundCheck != null) {
            backgroundCheck.cancel();
            backgroundCheck = null;
        }
    }

    public void forceDisconnect() {
        getInstance().shouldAutoConnect = false;
        Disconnect();
        ;
    }

    public void disconnectAfterFew() {
        backgroundCheck = new TimerTask() {

            @Override
            public void run() {

                getInstance().shouldAutoConnect = false;
                Disconnect();
                ;
                backgroundCheck.cancel();
            }
        };

        Timer isBackgroundChecker = new Timer();
        isBackgroundChecker.schedule(backgroundCheck, 2000, 1000);

    }

    public HubProxy getHub() {
        return mHub;
    }

    public ConnectionState currentSignalRState() {
        if (mConnection == null)
            return ConnectionState.Disconnected;

        return mConnection.getState();
    }

    public boolean isReconnecting() {
        if (mConnection == null)
            return false;

        return mConnection.getState() == ConnectionState.Reconnecting;
    }

    public boolean isConnecting() {
        if (mConnection == null)
            return false;

        return mConnection.getState() == ConnectionState.Connecting;
    }

    public boolean isConnected() {

        if (mConnection == null) {
            isConnected = false;
            return isConnected;

        }
        isConnected = mConnection.getState() == ConnectionState.Connected;
        return isConnected;
    }

    public boolean isDisconnected() {
        if (mConnection == null)
            return true;

        return mConnection.getState() == ConnectionState.Disconnected;
    }

    public void Disconnect() {
        try {
            if (mConnection != null)
                mConnection.stop();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void initializeVariables() throws Exception {

        //String host="http://203.130.28.214:3374/MenyooDev";
        String host = AppController.getCustResources().getString(R.string.url_offline_signalR);
        mConnection = new HubConnection(host);
        mHub = mConnection.createHubProxy("menyooHub");
        mHub.subscribe(SignalRListener.getInstance());
    }

    public synchronized void Connect(final ICallBack callBack) {
        try {
            // Just to make sure existing connection should be closed before opening a new one
            if (this.isConnected() || this.isConnecting()) {
                return;
            }
            Platform.loadPlatformComponent(new AndroidPlatformComponent());
            // Change to the IP address and matching port of your SignalR
            // server.
            initializeVariables();
            mConnection.stateChanged(new StateChangedCallback() {
                @Override
                public void stateChanged(ConnectionState oldState,
                                         ConnectionState newState) {
                    switch (newState) {
                        case Connected:
                            Log.e("SignalR Status", "Connected Successfully");
                            if (callBack != null)
                                callBack.isConnected(true);
                            break;
                        case Disconnected:
                            Log.e("SignalR Status", "Disconnected");
                            mConnection = null;
                            mHub = null;
                            if (shouldAutoConnect) {
                                ReConnect(null);
                            }
                            break;
                        case Reconnecting:
                            Log.e("SignalR Status", "Reconnecting");
                            break;
                        case Connecting:
                            Log.e("SignalR Status", "Connecting");
                            break;
                        default:
                            break;
                    }
                }
            });
            SignalRFuture<Void> awaitConnection = mConnection.start();
            awaitConnection.onError(new ErrorCallback() {
                @Override
                public void onError(Throwable error) {
                    Log.e("Error", error.toString());
                    forceDisconnect();
                    if (AppController.isActivityVisible()) {
                        ((BaseClass) AppController.getCurrentActivity()).showDialogFromNotUi(error.toString());
                    }
                }
            });
            awaitConnection.get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void ReConnect(final ICallBack callback) {
        if (this.isConnected() || this.isConnecting()) {
            return;
        }

        newThread = new Thread() {
            @Override
            public void run() {
                Connect(callback);
            }
        };
        // Disconnect();
        mConnection = null;
        mHub = null;
        if (newThread.isAlive()) {
            newThread.interrupt();
            Log.e("Menyoo", "ConThread destroy");
        }
        newThread.start();
        Log.e("Menyoo", "ConThread running");
    }
}