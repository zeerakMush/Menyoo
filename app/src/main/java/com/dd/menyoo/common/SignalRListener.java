package com.dd.menyoo.common;

import android.app.Activity;
import android.app.NotificationManager;
import android.util.Log;

import com.dd.menyoo.listeners.ISignalRListener;

/**
 * Created by Administrator on 02-Mar-16.
 */
public class SignalRListener {
    private static SignalRListener signalRListener = null;


    public static SignalRListener getInstance() {
        if (signalRListener == null) {
            signalRListener = new SignalRListener();

        }
        return signalRListener;
    }
    ///[{"UserId":29,"BillId":207,"TableId":1,"RequestId":14},{"UserId":29,"BillId":207,"TableId":1,"RequestId":15},{"UserId":29,"BillId":209,"TableId":1,"RequestId":16},{"UserId":29,"BillId":210,"TableId":1,"RequestId":17},{"UserId":29,"BillId":213,"TableId":1,"RequestId":18},{"UserId":29,"BillId":213,"TableId":1,"RequestId":19},{"UserId":29,"BillId":214,"TableId":1,"RequestId":20},{"UserId":29,"BillId":214,"TableId":1,"RequestId":21},{"UserId":29,"BillId":215,"TableId":1,"RequestId":22},{"UserId":29,"BillId":215,"TableId":1,"RequestId":23},{"UserId":29,"BillId":216,"TableId":1,"RequestId":24},{"UserId":29,"BillId":217,"TableId":1,"RequestId":25},{"UserId":29,"BillId":217,"TableId":1,"RequestId":26},{"UserId":31,"BillId":198,"TableId":1,"RequestId":27},{"UserId":29,"BillId":220,"TableId":1,"RequestId":28},{"UserId":29,"BillId":220,"TableId":1,"RequestId":29},{"UserId":29,"BillId":221,"TableId":1,"RequestId":30},{"UserId":29,"BillId":222,"TableId":1,"RequestId":31}]
    private Activity getCurrentActivity() {
        return AppController.getCurrentActivity();
    }

  /*  public void updatewaiterqueue(final Object obj) {
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(obj) {

                @Override
                public void run() {

                    listener.waiterQueueUpdated((Object) passedObj);

                }
            });
        }
    }*/

    /*public void updateBillQueue(final Object obj){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(obj) {

                @Override
                public void run() {

                    listener.billQueueUpdated((Object) passedObj);

                }
            });
        }
    }*/

    public void updatewaiterqueue(final Object obj1,final Object obj2) {
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(obj1) {

                @Override
                public void run() {

                    listener.waiterQueueUpdated((Object) passedObj);

                }
            });
        }
    }

    public void updateBillQueue(final Object obj1,final Object obj2){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(obj1) {

                @Override
                public void run() {

                    listener.billQueueUpdated((Object) passedObj);

                }
            });
        }
    }

    public void resetBill(){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    listener.resetTable();

                }
            });
        }
    }

    public void settleBill(){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    listener.settleBill();
                }
            });
        }
    }

    public void billUpdated(Object obj){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(obj) {

                @Override
                public void run() {

                    listener.billUpdated(passedObj);


                }
            });
        }
    }

    public void userwanttojoin(Object obj){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(obj) {

                @Override
                public void run() {

                    listener.userWantToJoin(passedObj);


                }
            });
        }
    }

    public void responsetojoinrequest(Object obj){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(obj) {

                @Override
                public void run() {

                    listener.responseToJoin(passedObj);

                }
            });
        }
    }

    public void changeTable(String pre, final String current){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new CustomRunnable(current) {

                @Override
                public void run() {

                    listener.changeTable(current);

                }
            });
        }
    }

    public void joinrequestcancel(){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    listener.joinRequestCancel();

                }
            });
        }
    }

    public void RestaurantActive(final Object object){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();

        if (listener != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    listener.restaurantActive(object);

                }
            });
        }
    }

    public void AppLocked(final Object obj){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();
        Log.e("SignalR",obj.toString());
        if (listener != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    listener.appDisable(obj);

                }
            });
        }
    }

    public void UserBlocked(){
        final ISignalRListener listener = (ISignalRListener) getCurrentActivity();
        if (listener != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {

                    listener.userBlocked();

                }
            });
        }
    }
}
