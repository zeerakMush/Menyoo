package com.dd.menyoo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.DataProvider;
import com.dd.menyoo.network.SignalRManager;

/**
 * Created by Administrator on 23-Feb-16.
 */
public abstract class BaseClass extends AppCompatActivity{
    public DataProvider dataPro = null;
    protected AppController mMyApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataPro = new DataProvider(this);
        mMyApp = (AppController) this.getApplicationContext();
        mMyApp.setCurrentActivity(this);
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            AppController.activityResumed();
            SignalRManager.getInstance().cancelDisconnection();
            if(SignalRManager.getInstance().isDisconnected())
                SignalRManager.getInstance().ReConnect(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        //SignalRManager.getInstance().disconnectAfterFew();
        AppController.activityPaused();
        super.onPause();
    }

    public void showGenricDialog(Context ctx,String message,String ptvMsg,String negMsg,
                                 DialogInterface.OnClickListener ptvListener,DialogInterface.OnClickListener negListener){
        final AlertDialog dialog=new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(ptvMsg,ptvListener)
                .setNegativeButton(negMsg, negListener)
                .show();
    }

    public AlertDialog showGuestRequesDialog(Context ctx,String message,String ptvMsg,String negMsg,
                                             DialogInterface.OnClickListener ptvListener,DialogInterface.OnClickListener negListener) {
        return new AlertDialog.Builder(ctx)
                .setMessage(message)
                .setPositiveButton(ptvMsg, ptvListener)
                .setNegativeButton(negMsg, negListener)
                .show();
    }

    public void showDisableDialog(Context ctx, String message){
        TextView myMsg = new TextView(this);
        myMsg.setText(message);
        myMsg.setPadding(40,40,40,40);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

        AlertDialog dialog=new AlertDialog.Builder(ctx)
                .setView(myMsg)
                .show();
    }

    public void showDialogFromNotUi(final String message){
        /*runOnUiThread(new Runnable(){
            @Override
            public void run(){
                showDisableDialog(BaseClass.this,message);
            }
        });*/

    }

}
