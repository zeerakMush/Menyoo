package com.dd.menyoo.fragment;


import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.SignalRManager;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Action extends BaseFragment implements View.OnClickListener {


    public Action() {
        // Required empty public constructor
    }

    LinearLayout llGetwaiter, llGetBill, llChangeCancelTable,llSuggestion,llPrivateFeedback,llLoyaltyReward;
    private TextView tvWaiterStatus, tvWaiterOrder, 
            tvWaiterTimer, tvBiilStatus, tvBillOrder, tvBillTimer,tvAlreadyGetBill;
    Button btnGetWaiter, btnGetBill;
    long updatedTime = 0L;
    private long startTimeWaiter = 0L, startTimeBill = 0L;
    private Handler customHandlerWaiter = new Handler();
    private Handler customHandlerBill = new Handler();

    private static final String WAITER_QUEUE_START_TIME = "waiterTime"
            +AppController.getCurrentRestaurent().getRestaurantID();
    private static final String BILL_QUEUE_START_TIME = "billTime"
            +AppController.getCurrentRestaurent().getRestaurantID();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_action, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llGetBill = (LinearLayout) view.findViewById(R.id.ll_get_bill);
        llGetwaiter = (LinearLayout) view.findViewById(R.id.ll_get_waiter);
        llChangeCancelTable = (LinearLayout)view.findViewById(R.id.ll_cancel_table);llChangeCancelTable.setOnClickListener(this);
        llSuggestion = (LinearLayout)view.findViewById(R.id.ll_suggestions);llSuggestion.setOnClickListener(this);
        llPrivateFeedback = (LinearLayout)view.findViewById(R.id.llPrivateFeedback);llPrivateFeedback.setOnClickListener(this);
        llLoyaltyReward = (LinearLayout)view.findViewById(R.id.llLoyaltyReward);llLoyaltyReward.setOnClickListener(this);

        btnGetBill = (Button) view.findViewById(R.id.btn_get_bill);btnGetBill.setOnClickListener(this);
        btnGetWaiter = (Button) view.findViewById(R.id.btn_get_waiter);btnGetWaiter.setOnClickListener(this);
        tvWaiterOrder = (TextView) view.findViewById(R.id.tv_waiter_no);
        tvWaiterStatus = (TextView) view.findViewById(R.id.tv_waiter_status);
        tvWaiterTimer = (TextView) view.findViewById(R.id.tv_waiter_timer);
        tvBillOrder = (TextView) view.findViewById(R.id.tv_bill_order);
        tvBiilStatus = (TextView) view.findViewById(R.id.tv_bill_status);
        tvBillTimer = (TextView) view.findViewById(R.id.tv_bill_timer);
        tvAlreadyGetBill = (TextView) view.findViewById(R.id.alreadyGetBill);



    }

    @Override
    public void onResume() {
        super.onResume();
        waiterQueueUpdated(((TabActivity)getActivity()).orderInQueueWaiter);
        billQueueUpdated(((TabActivity)getActivity()).orderInQueueBill);

        startTimeWaiter = ((TabActivity) getActivity()).timeWaiterStart;
        startTimeBill = ((TabActivity) getActivity()).timeBillStart;
        timeSwapBuffWaiter=0L;timeSwapBuffBill=0L;

        if (startTimeWaiter > 0) {
            customHandlerWaiter.postDelayed(updateTimerThreadWaiter, 0);
            btnGetWaiter.setBackgroundResource(R.color.bottom_menu_color);
            btnGetWaiter.setEnabled(false);
            llGetwaiter.setVisibility(View.VISIBLE);
        }
        if (startTimeBill > 0) {
            customHandlerWaiter.postDelayed(updateTimerThreadBill, 0);
            btnGetBill.setBackgroundResource(R.color.bottom_menu_color);
            btnGetBill.setEnabled(false);
            llGetBill.setVisibility(View.VISIBLE);
        }
        setBillButton();
    }

    public void setBillButton(){
        if(!((TabActivity)getActivity()).isCanGetBill()){
            btnGetBill.setEnabled(false);
            btnGetBill.setBackgroundResource(R.color.bottom_menu_color);
            tvAlreadyGetBill.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_get_bill:
                if (((TabActivity) getActivity()).checkIsTableAcquired()&& SignalRManager.getInstance().isConnected()) {
                    ((TabActivity) getActivity()).showGenricDialog(getActivity(), "Do you want to get Bill?",
                            "Get", "Cancel"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getBill();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                } else {

                    if(!SignalRManager.getInstance().isConnected()&&((TabActivity) getActivity()).checkIsTableAcquired())
                        AppHelper.showConnectionAlert(getActivity());
                    else{
                        ((TabActivity) getActivity()).showTableDialog();
                    }
                }
                break;
            case R.id.btn_get_waiter:
                if (((TabActivity) getActivity()).checkIsTableAcquired()&&SignalRManager.getInstance().isConnected()) {
                    ((TabActivity) getActivity()).showGenricDialog(getActivity(), "Do you want to get Waiter?",
                            "Get", "Cancel"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getWaiter();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                } else {
                    if(!SignalRManager.getInstance().isConnected()&&((TabActivity) getActivity()).checkIsTableAcquired())
                        AppHelper.showConnectionAlert(getActivity());
                    else{
                        ((TabActivity) getActivity()).showTableDialog();
                    }
                }
                break;
            case R.id.ll_cancel_table:
                if(SignalRManager.getInstance().isConnected()){
                    if (((TabActivity) getActivity()).checkIsTableAcquired() && (AppController.isHost()))
                        showTableModifyDialog();
                    else if(!((TabActivity) getActivity()).checkIsTableAcquired())
                        ((TabActivity) getActivity()).showTableDialog();
                    else if(!AppController.isHost()){
                        ((TabActivity)getActivity()).showGuestDialog("Guest cannot access this feature.");
                    }
                }else{
                    AppHelper.showConnectionAlert(getActivity());
                }
                break;
            case R.id.ll_suggestions:
                ((TabActivity)getActivity()).replaceFragment(new Suggestions(),true);
                break;
            case R.id.llPrivateFeedback:
                ((TabActivity)getActivity()).showDisableDialog(getActivity(),
                        "This feature will be released soon. Stay tuned.");
                break;
            case R.id.llLoyaltyReward:
                 if(AppController.getCurrentRestaurent().isLoyaltyFeatureActive())
                    ((TabActivity)getActivity()).replaceFragment(new Loyality(),true);
                else
                     ((TabActivity)getActivity()).showDisableDialog(getActivity(),
                             "This feature will be released soon. Stay tuned.");
                break;
        }
    }

    /**Change and Cancel Tabel***/

    private void showTableModifyDialog() {
        ((TabActivity)getActivity()).showGenricDialog(getActivity(), "Do you want to cancel table?",
                "No", "Cancel Table",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TabActivity)getActivity()).cancelTable(false);
                    }
                });

        /*((TabActivity)getActivity()).showGenricDialog(getActivity(), "What do you want to do?",
                "Change Table", "Cancel Table",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        changeTable();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((TabActivity)getActivity()).cancelTable(false);
                    }
                });*/
    }

    private void changeTable() {
        showChangeTableDialog();
    }

    private void showChangeTableDialog() {
        AppHelper.getInstance().hideProgressDialog();
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.change_table_dialog);
        final EditText etTable = (EditText)dialog.findViewById(R.id.et_tableCode);
        Button btnSubmit = (Button) dialog.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTable(etTable.getText().toString());
                ((TabActivity)getActivity()).closeKeyBoard(etTable);
                dialog.dismiss();
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        dialog.show();
    }
    private void changeTable(String tableID){
        AppHelper.getInstance().showProgressDialog("Please Wait",getActivity());
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.showConnectionAlert(getActivity());
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo",obj.toString());
                afterTableUpdate(obj);
            }
        });
        String url =String.format("ChangeTable?restaurantId=%d&BillId=%d&TableCode=%s",AppController.getCurrentRestaurent().getRestaurantID()
                ,AppController.getBillId(),tableID);

        String[] params = {getString(R.string.url_offline)+url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterTableUpdate(Object obj){
        AppHelper.getInstance().hideProgressDialog();
        try {
            JSONObject jObj = new JSONObject(obj.toString());
            String status =jObj.getString("status");
            String message = jObj.getString("Message");
            Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
            if(status.equals("Success")){
                String tableNo = jObj.getString("TableNumber");
                ((TabActivity)getActivity()).updateTable(tableNo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**SignalR getCall and callBacks***/

    private void getWaiter() {
        ((TabActivity) getActivity()).dataPro.UserGetWaiter("" + AppController.getCurrentRestaurent().getRestaurantID(), AppController.getBillId());
        startWaiterClock();
    }

    private void startWaiterClock(){
        timeSwapBuffWaiter = 0L;
        btnGetWaiter.setBackgroundResource(R.color.bottom_menu_color);
        llGetwaiter.setVisibility(View.VISIBLE);
        startTimeWaiter = SystemClock.uptimeMillis();
        ((TabActivity)getActivity()).sharedPref.setLongForKey(startTimeWaiter,WAITER_QUEUE_START_TIME);
        ((TabActivity) getActivity()).timeWaiterStart = startTimeWaiter;
        customHandlerWaiter.postDelayed(updateTimerThreadWaiter, 0);
        btnGetWaiter.setEnabled(false);
    }

    private void getBill() {
        ((TabActivity) getActivity()).dataPro.UserGetBill("" + AppController.getCurrentRestaurent().getRestaurantID(), AppController.getBillId());
        startBillClock();
    }

    private void startBillClock(){
        timeSwapBuffBill = 0L;
        btnGetBill.setBackgroundResource(R.color.bottom_menu_color);
        llGetBill.setVisibility(View.VISIBLE);
        startTimeBill = SystemClock.uptimeMillis();
        ((TabActivity)getActivity()).sharedPref.setLongForKey(startTimeBill,BILL_QUEUE_START_TIME);
        ((TabActivity) getActivity()).timeBillStart = startTimeBill;
        customHandlerBill.postDelayed(updateTimerThreadBill, 0);
        btnGetBill.setEnabled(false);
    }

    long timeInMillisecondsWaiter=0L,timeSwapBuffWaiter=0L;
    long timeInMillisecondsBill=0L,timeSwapBuffBill=0L;

    private Runnable updateTimerThreadWaiter = new Runnable() {
        public void run() {
            timeInMillisecondsWaiter = (SystemClock.uptimeMillis()) - startTimeWaiter;
            updatedTime = timeSwapBuffWaiter + timeInMillisecondsWaiter;
            setTime(tvWaiterTimer,updatedTime);
            customHandlerWaiter.postDelayed(this, 0);
        }
    };

    private Runnable updateTimerThreadBill = new Runnable() {
        public void run() {
            timeInMillisecondsBill = (SystemClock.uptimeMillis()) - startTimeBill;
            updatedTime = timeSwapBuffBill + timeInMillisecondsBill;
            setTime(tvBillTimer,updatedTime);
            customHandlerBill.postDelayed(this, 0);
        }
    };

    public void setTime(TextView tv,long mills){
        int secs = (int) (mills / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        tv.setText(String.format("%02dm:%s", mins, String.format("%02ds", secs)));
    }


    private void pausewaiterCloack(){
        timeSwapBuffWaiter += timeInMillisecondsWaiter;
        customHandlerWaiter.removeCallbacks(updateTimerThreadWaiter);

    }
    private void pauseBillClock(){
        timeSwapBuffBill += timeInMillisecondsBill;
        customHandlerBill.removeCallbacks(updateTimerThreadBill);

    }

    public void waiterQueueUpdated(int order) {
        if(order>=0){
            tvWaiterOrder.setText(String.format("%d%s in queue", order+1,getSuffix(order+1)));
            tvWaiterStatus.setText("Please Wait");
            tvWaiterStatus.setTextColor(getResources().getColor(R.color.white));
            if(((TabActivity)getActivity()).timeWaiterStart==0L){
                startWaiterClock();
            }
        }else {
            tvWaiterOrder.setText("-");
            tvWaiterStatus.setText("On the way");
            pausewaiterCloack();
            tvWaiterStatus.setTextColor(getResources().getColor(R.color.btn_color_default));
            btnGetWaiter.setEnabled(true);
            btnGetWaiter.setBackgroundResource(R.color.btn_color_default);
            if(((TabActivity)getActivity()).timeWaiterStart>0L){
                llGetwaiter.setVisibility(View.VISIBLE);
                timeSwapBuffWaiter = SystemClock.uptimeMillis() - ((TabActivity)getActivity()).timeWaiterStart;
                setTime(tvWaiterTimer,timeSwapBuffWaiter);
            }
            if(AppController.isActivityVisible()){
                ((TabActivity)getActivity()).timeWaiterStart=0L;
                ((TabActivity)getActivity()).sharedPref.setLongForKey(0L,WAITER_QUEUE_START_TIME);
            }

            startTimeWaiter= 0L;
            updatedTime = 0L;
        }
    }

    public void billQueueUpdated(int order){
        if(order>=0){
            tvBillOrder.setText(String.format("%d%s in queue", order+1,getSuffix(order+1)));
            tvBiilStatus.setText("Please Wait");
            tvBiilStatus.setTextColor(getResources().getColor(R.color.white));
            if(((TabActivity)getActivity()).timeBillStart==0L){
                startBillClock();
            }
        }else {
            tvBillOrder.setText("-");
            tvBiilStatus.setText("On the way");
            pauseBillClock();
            tvBiilStatus.setTextColor(getResources().getColor(R.color.btn_color_default));
            btnGetBill.setEnabled(true);
            btnGetBill.setBackgroundResource(R.color.btn_color_default);
            if(((TabActivity)getActivity()).timeBillStart>0L){
                llGetBill.setVisibility(View.VISIBLE);
                timeSwapBuffBill = SystemClock.uptimeMillis() - ((TabActivity)getActivity()).timeBillStart;
                setTime(tvBillTimer,timeSwapBuffBill);
            }
            if(AppController.isActivityVisible()){
                ((TabActivity)getActivity()).timeBillStart=0L;
                ((TabActivity)getActivity()).sharedPref.setLongForKey(0L,BILL_QUEUE_START_TIME);
            }
            setBillButton();

            startTimeBill= 0L;
            updatedTime = 0L;
        }
    }

    String getSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:  return "st";
            case 2:  return "nd";
            case 3:  return "rd";
            default: return "th";
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseBillClock();pausewaiterCloack();
    }
}
