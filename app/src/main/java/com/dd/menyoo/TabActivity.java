package com.dd.menyoo;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.common.SharedPrefManager;
import com.dd.menyoo.fragment.Action;
import com.dd.menyoo.fragment.BaseFragment;
import com.dd.menyoo.fragment.Basket;
import com.dd.menyoo.fragment.CheckBill;
import com.dd.menyoo.fragment.MenuTab1;
import com.dd.menyoo.fragment.SpecialMenu;
import com.dd.menyoo.listeners.ICallBack;
import com.dd.menyoo.listeners.ISignalRListener;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.GuestRequestModel;
import com.dd.menyoo.model.OrderModel;
import com.dd.menyoo.model.RestaurantModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.SignalRManager;
import com.dd.menyoo.network.TaskCompleted;
import com.facebook.login.LoginManager;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TabActivity extends BaseClass implements ISignalRListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 7;
    private static final String WAITER_QUEUE_START_TIME = "waiterTime"
            + AppController.getCurrentRestaurent().getRestaurantID();
    private static final String BILL_QUEUE_START_TIME = "billTime"
            + AppController.getCurrentRestaurent().getRestaurantID();
    private static final String IS_CAN_GET_BILL = "isCanGetBill" +
            AppController.getCurrentRestaurent().getRestaurantID();
    private static final String GET_BILL_ID = "GetBillID" +
            AppController.getCurrentRestaurent().getRestaurantID();
    private static final String GET_RES_ID = "GetResId";

    private TextView mTvRestaurantName, mTvTableCode, mBasketCount, mBannerText;
    private ImageButton mHome, mBack;

    RestaurantModel restaurantModel;
    ProgressBar pbTableCode;
    public ArrayList<CheckBillModel> billArr;
    private LinearLayout llBottomMenu, llMenu, llSpecials, llbasket, llAction, llChkBill;

    private EditText mEtTableCode;
    private HashMap<String, OrderModel> mOrdersMap;
    private boolean isOrderSubmitted, isTableAcquired;
    private static final int REQUEST_QR = 311;
    ArrayList<String> mOrderKey;
    public long timeWaiterStart = 0L;
    public long timeBillStart = 0L;
    public int orderInQueueWaiter = -1;
    public int orderInQueueBill = -1;
    private Button mQrScanBtn, mCancelTableRequest;
    private boolean isCanGetBill = true;
    public SharedPrefManager sharedPref;
    private RelativeLayout rl_tableCode, rl_tableJoin, rlBanner;
    Queue<GuestRequestModel> guestRequestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        initView();
        setEditTextImeAction();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //redirectOnPushNotification();
        if ((AppController.getLoginUser().isGuest()) ||
                (AppController.isTableOccupiedInAnyRes() && !AppController.getCurrentRestaurent().isTableOccupied()))
            mEtTableCode.setVisibility(View.GONE);
        else {
            if (SignalRManager.getInstance().isConnected())
                checkBillId(AppController.getCurrentRestaurent().getRestaurantID(),
                        AppController.getLoginUser().getUserId());
            else {
                Thread reconnectThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SignalRManager.getInstance().ReConnect(new ICallBack() {

                                @Override
                                public void isConnected(Boolean isConnected) {
                                    if (isConnected) {
                                        checkBillId(AppController.getCurrentRestaurent().getRestaurantID(),
                                                AppController.getLoginUser().getUserId());
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                reconnectThread.start();
            }
        }
    }

    /***
     * UI based Methods
     **/

    private void initView() {

        mTvRestaurantName = (TextView) findViewById(R.id.res_name);
        llBottomMenu = (LinearLayout) findViewById(R.id.ll_bottom_menu);
        llSpecials = (LinearLayout) findViewById(R.id.ll_special);
        llMenu = (LinearLayout) findViewById(R.id.ll_menu);
        llbasket = (LinearLayout) findViewById(R.id.ll_basket);
        llAction = (LinearLayout) findViewById(R.id.ll_action);
        llChkBill = (LinearLayout) findViewById(R.id.ll_checkbill);
        mBasketCount = (TextView) findViewById(R.id.basket_count);
        mBannerText = (TextView) findViewById(R.id.tv_bannerText);

        mBack = (ImageButton) findViewById(R.id.btn_back);
        mQrScanBtn = (Button) findViewById(R.id.btn_qr);
        mHome = (ImageButton) findViewById(R.id.btn_home);
        mEtTableCode = (EditText) findViewById(R.id.et_tableCode);
        mTvTableCode = (TextView) findViewById(R.id.tv_tableCode);
        pbTableCode = (ProgressBar) findViewById(R.id.pb_tablecode);
        rl_tableCode = (RelativeLayout) findViewById(R.id.rl_table_code);
        rl_tableJoin = (RelativeLayout) findViewById(R.id.rl_table_request_wait);
        rlBanner = (RelativeLayout) findViewById(R.id.rl_banner);
        AppController.setIsFirstTimeOrderAdded(false);
        mOrderKey = new ArrayList<>();
        mOrdersMap = new HashMap<>();
        restaurantModel = AppController.getCurrentRestaurent();
        sharedPref = new SharedPrefManager(this);
        billArr = new ArrayList<>();

        if (restaurantModel != null) {
            mTvRestaurantName.setText(restaurantModel.getRestaurantName());
            if (restaurantModel.isReadOnly()) {
                llbasket.setVisibility(View.GONE);
                llAction.setVisibility(View.GONE);
                llChkBill.setVisibility(View.GONE);
                rl_tableCode.setVisibility(View.GONE);
            }
        }

        isTableAcquired = false;

        getFragmentManager().findFragmentById(R.id.fragmentWindow);
        getFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Fragment fr = getFragmentManager().findFragmentById(R.id.fragmentWindow);
                if (fr != null) {
                    Log.e("fragment=", fr.getClass().getSimpleName());
                    if (fr.getClass().getSimpleName().equals("SpecialMenu")) {
                        reSetBottomMenu();
                        llSpecials.setBackgroundResource(R.color.btn_color_default);
                    } else if (fr.getClass().getSimpleName().equals("MenuTab1")) {
                        reSetBottomMenu();
                        llMenu.setBackgroundResource(R.color.btn_color_default);
                    } else if (fr.getClass().getSimpleName().equals("Basket")) {
                        reSetBottomMenu();
                        llbasket.setBackgroundResource(R.color.btn_color_default);
                    } else if (fr.getClass().getSimpleName().equals("Action")) {
                        reSetBottomMenu();
                        llAction.setBackgroundResource(R.color.btn_color_default);
                    } else if (fr.getClass().getSimpleName().equals("CheckBill")) {
                        reSetBottomMenu();
                        llChkBill.setBackgroundResource(R.color.btn_color_default);
                    }
                }
            }
        });

        replaceFragment(new SpecialMenu(), false);
        getBannerMessage();


    }

    public void subscribeToQueues() {
        waiterQueueUpdated(new Gson().toJsonTree(dataPro.UserGetWaiterGroup("" + restaurantModel.getRestaurantID())));
        billQueueUpdated(new Gson().toJsonTree(dataPro.UserGetBillGroup("" + restaurantModel.getRestaurantID())));
    }

    public void registerToRestrauant() {
        if (SignalRManager.getInstance().isConnected()) {
            subscribeToQueues();
        } else {
            Thread reconnectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SignalRManager.getInstance().ReConnect(new ICallBack() {

                            @Override
                            public void isConnected(Boolean isConnected) {
                                if (isConnected) {
                                    if (isTableAcquired) {
                                        subscribeToQueues();
                                        dataPro.SubscribeToBill(AppController.getBillId());
                                    }
                                }
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            reconnectThread.start();
        }

    }

    public void replaceFragment(BaseFragment fragment, boolean isToAddBackStack) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentWindow, fragment, "KK");
        View view = this.getCurrentFocus();
        closeKeyBoard(view);
        if (isToAddBackStack)
            fragmentTransaction.addToBackStack("KK");
        fragmentTransaction.commit();
    }

    public void closeKeyBoard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void toSpecials(View view) {
        reSetBottomMenu();
        llSpecials.setBackgroundResource(R.color.btn_color_default);
        replaceFragment(new SpecialMenu(), true);
    }

    public void toMenu(View view) {
        reSetBottomMenu();
        view.setBackgroundResource(R.color.btn_color_default);
        replaceFragment(new MenuTab1(), true);
    }

    public void toBasket(View view) {
        if (!AppController.getLoginUser().isGuest()) {
            reSetBottomMenu();
            // view.setBackgroundResource(R.color.btn_color_default);
            Basket fragment = new Basket();
            fragment.setmHashMapOrder(mOrdersMap, mOrderKey);
            replaceFragment(fragment, true);
        } else
            showDisableDialog(this, this.getString(R.string.guest_disable));

    }

    public void toAction(View view) {
        if (!AppController.getLoginUser().isGuest() &&
                AppController.getCurrentRestaurent().isRestaurantActive()
                && !AppController.isAppDisable()) {
            reSetBottomMenu();
            view.setBackgroundResource(R.color.btn_color_default);
            replaceFragment(new Action(), true);
        } else {
            if (AppController.getLoginUser().isGuest())
                showDisableDialog(this, this.getString(R.string.guest_disable));
            else if (!AppController.getCurrentRestaurent().isRestaurantActive())
                showDisableDialog(this, this.getString(R.string.restaurant_disbled_message));
            else if (AppController.isAppDisable())
                showDisableDialog(this, this.getString(R.string.app_disable_message));
        }
    }

    public void toBill(View view) {
        if (!AppController.getLoginUser().isGuest()) {
            reSetBottomMenu();
            view.setBackgroundResource(R.color.btn_color_default);
            CheckBill chkFrag = new CheckBill();
            chkFrag.setmCheckBillArr(billArr);
            replaceFragment(chkFrag, true);
        } else {
            showDisableDialog(this, this.getString(R.string.guest_disable));
        }
    }

    private void reSetBottomMenu() {
        int count = llBottomMenu.getChildCount();
        for (int i = 0; i < count; i++) {
            llBottomMenu.getChildAt(i).setBackgroundResource(R.color.bottom_menu_color);
        }
    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            toRestaurants();
        }
    }


    public void toRestaurants(View view) {
        toRestaurants();
    }

    public void toRestaurants() {
       /* if(!isGotAnyAceptedOrder){*/
        Intent intentAct = new Intent(TabActivity.this,
                MenuActivity.class);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentAct);
        finish();
        /*}else{
            Toast.makeText(this,"You have some accepted orders",Toast.LENGTH_LONG).show();
        }*/
    }

    public void backButton(View view) {
        getFragmentManager().popBackStack();
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            toRestaurants();
        }
    }

    //region Order Related Methods

    /***
     * Order Related Methods
     **/

    public void addOrder(OrderModel order) {
        String key = order.getComment() + order.getMenu().getItemID();
        if (!mOrdersMap.containsKey(key)) {
            mOrdersMap.put(key, order);
            mOrderKey.add(key);
        } else {
            OrderModel orderModel = mOrdersMap.get(key);
            orderModel.addQuantity(order.getQuantity());
            mOrdersMap.put(key, orderModel);
        }
        updateBasketItemCount();

        showGenricDialog(this, "Item has been added to the basket." +
                        " Please go to basket to confirm and submit your order", "Ok", "Go to Basket",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        toBasket(null);
                    }
                }
        );
        //Toast.makeText(TabActivity.this,"Order Added to Basket",Toast.LENGTH_LONG).show();
        getFragmentManager().popBackStack();
        /*toBasket(null);*/
    }

    public void updateBasketItemCount() {
        if (mOrdersMap.size() > 0 && mOrderKey != null) {
            int quantity = 0;
            for (String keyTa : mOrderKey) {
                quantity += mOrdersMap.get(keyTa).getQuantity();
            }
            mBasketCount.setVisibility(View.VISIBLE);
            mBasketCount.setText(String.format("%d", quantity));
        } else
            mBasketCount.setVisibility(View.GONE);
    }

    public boolean isOrderSubmitted() {
        return isOrderSubmitted;
    }

    public void setOrderSubmitted(boolean orderSubmitted) {
        isOrderSubmitted = orderSubmitted;
    }

    public void clearOrder() {
        mOrderKey = new ArrayList<>();
        mOrdersMap = new HashMap<>();
        mBasketCount.setVisibility(View.GONE);
    }
    //endregion

    //region Table Related Methods

    /***
     * Table Related Methods
     **/

    protected void getTableId(String tableId) {
        pbTableCode.setVisibility(View.VISIBLE);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(TabActivity.this,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                AppHelper.showConnectionAlert(TabActivity.this);
                pbTableCode.setVisibility(View.GONE);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                Log.e("Menyoo", obj.toString());
                afterGetTable(obj);
            }
        });
        String url = String.format("GetTableForUser?tablecode=%s&userid=%d&restaurantId=%d",
                tableId, AppController.getLoginUser().getUserId(), AppController.getCurrentRestaurent().getRestaurantID());
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterGetTable(Object obj) {
        try {
            pbTableCode.setVisibility(View.GONE);
            JSONObject jsonObj = new JSONObject(obj.toString());
            if (!jsonObj.has("Status")) {
                if (jsonObj.has("TableId")) {
                    String id = jsonObj.getString("TableId");
                    int billId = jsonObj.getInt("BillId");
                    setTable(billId, id);
                    AppController.setIsHost(true);
                } else {
                    String tableNumber = jsonObj.getString("TableNumber");
                    int billId = jsonObj.getInt("BillId");
                    String hostName = jsonObj.getString("UserName");
                    int requestId = jsonObj.getInt("RequestId");
                    joinTableWait(tableNumber, billId, hostName, requestId);
                    AppController.setIsHost(false);
                }
            } else {
                String msg = jsonObj.getString("Message");
                Toast.makeText(TabActivity.this, msg, Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setTable(int billId, String tableCode) {
        AppController.setTableId(tableCode);
        AppController.setBillId(billId);
        registerToRestrauant();
        dataPro.SubscribeToBill(billId);
        mEtTableCode.setVisibility(View.GONE);
        mTvTableCode.setVisibility(View.VISIBLE);
        mQrScanBtn.setVisibility(View.GONE);
        isTableAcquired = true;
        checkBill();
        mTvTableCode.setText(String.format("Table %s", tableCode));
        if (sharedPref.getIntByKey(GET_BILL_ID) == billId) {
            isCanGetBill = sharedPref.getBooleanByKey(IS_CAN_GET_BILL);
        } else {
            sharedPref.setIntForKey(billId, GET_BILL_ID);
            sharedPref.setBooleanForKey(true, IS_CAN_GET_BILL);
            isCanGetBill = true;
        }

    }

    protected void checkBillId(int resID, int userID) {

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(TabActivity.this,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                AppHelper.showConnectionAlert(TabActivity.this);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                Log.e("Menyoo", obj.toString());
                afterCheckTable(obj);
            }
        });
        String url = String.format("CheckBillStatus?restaurantId=%d&UserId=%d", resID, userID);
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterCheckTable(Object obj) {
        try {
            pbTableCode.setVisibility(View.GONE);
            JSONObject jsonObj = new JSONObject(obj.toString());
            if (jsonObj.has("Status")) {
                String message = jsonObj.getString("Message");
                //Toast.makeText(TabActivity.this,message,Toast.LENGTH_LONG).show();
            } else {
                String id = jsonObj.getString("TableNumber");
                int billId = jsonObj.getInt("BillId");

                boolean isHost = !jsonObj.getBoolean("IsGuest");
                if (isHost) {
                    guestRequestQueue = new LinkedList<>();
                    JSONArray jsonArray = jsonObj.getJSONArray("AllRequestsToJoin");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        guestRequestQueue.add(getGuestRequestModel(jsonArray.getJSONObject(i)));
                    }
                }
                AppController.setIsHost(isHost);
                if (guestRequestQueue != null && !guestRequestQueue.isEmpty()) {
                    userWantToJoin(guestRequestQueue.element());
                }
                setTable(billId, id);

                timeWaiterStart = sharedPref.getLongbyKey(WAITER_QUEUE_START_TIME);
                timeBillStart = sharedPref.getLongbyKey(BILL_QUEUE_START_TIME);

                /*isGotAnyAceptedOrder = true;*/
                isOrderSubmitted = true;
            }
            // Toast.makeText(TabActivity.this,)
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void tableCancel() {
        mEtTableCode.setVisibility(View.VISIBLE);
        mTvTableCode.setVisibility(View.GONE);
        isTableAcquired = false;
        isOrderSubmitted = false;
        bufferBilledItem = new ArrayList<>();
        mOrdersMap = new HashMap<>();
        mOrderKey = new ArrayList<>();
        AppController.setTableId(""+0);
        AppController.setBillId(0);
        orderInQueueBill = -1;
        orderInQueueWaiter = -1;
        timeBillStart = 0L;
        timeWaiterStart = 0L;
        toSpecials(null);
    }

    public void updateTable(String id) {
        mTvTableCode.setText(String.format("Table %s", id));
        AppController.setTableId(id);
    }

    public boolean checkIsTableAcquired() {
        return isTableAcquired;
    }

    public void showTableDialog() {
        TextView myMsg = new TextView(this);
        myMsg.setText("Please Enter Table Code To Proceed,\n Thank You");
        myMsg.setPadding(40, 40, 40, 40);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);

        AlertDialog dialog = new AlertDialog.Builder(TabActivity.this)
                .setView(myMsg)
                .show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mEtTableCode.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(TabActivity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(mEtTableCode, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public void cancelTable(final boolean isToHome) {
        AppHelper.getInstance().showProgressDialog("Please Wait", TabActivity.this);
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(TabActivity.this,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.showConnectionAlert(TabActivity.this);

            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                afterCancelTable(obj, isToHome);
            }
        });
        String url = String.format("CancelTable?BillId=%d", AppController.getBillId());

        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterCancelTable(Object obj, boolean isToHome) {
        AppHelper.getInstance().hideProgressDialog();
        try {
            JSONObject jObj = new JSONObject(obj.toString());
            String status = jObj.getString("status");
            String message = jObj.getString("Message");
            Toast.makeText(TabActivity.this, message, Toast.LENGTH_LONG).show();
            if (status.equals("Success")) {
                if (!isToHome)
                    tableCancel();
                else
                    toRestaurants();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setEditTextImeAction() {
        mEtTableCode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getTable();
                    return false;
                }
                return false;
            }
        });
    }

    public void getTable() {
        if (!AppHelper.isContainSpecialCharachters(mEtTableCode.getText().toString())) {
            getTableId(mEtTableCode.getText().toString());
        } else {
            mEtTableCode.setText("");
            Toast.makeText(TabActivity.this, "Invalid Table Code", Toast.LENGTH_LONG).show();
        }
    }
    //endregion

    //region Queue Related Methods
    @Override
    public void waiterQueueUpdated(Object obj) {
        //Toast.makeText(this,"WaiterQueueUpdated",Toast.LENGTH_LONG).show();
        Log.e("SiganalR", obj.toString());
        FragmentManager manager = getFragmentManager();
        Action fragment = null;
        boolean isContains = false;
        if (manager.findFragmentById(R.id.fragmentWindow) instanceof Action)
            fragment = (Action) manager.findFragmentById(R.id.fragmentWindow);

        try {
            JSONArray jsoArr = new JSONArray(obj.toString());
            for (int i = 0; i < jsoArr.length(); i++) {
                JSONObject jObj = jsoArr.getJSONObject(i);
                int billID = jObj.getInt("BillId");
                int userID = jObj.getInt("UserId");
                if (billID == AppController.getBillId() /*&&
                        userID ==AppController.getLoginUser().getUserId()*/) {
                    //Toast.makeText(this, "" + i, Toast.LENGTH_LONG).show();
                    orderInQueueWaiter = i;
                    isContains = true;
                    if (fragment != null)
                        fragment.waiterQueueUpdated(orderInQueueWaiter);
                    break;
                }
            }
            if (!isContains) {
                if (orderInQueueWaiter != -1) {
                    AppHelper.getInstance().NotificationsBuilder("Waiter is on the way", this,
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));

                }
                orderInQueueWaiter = -1;
                if (fragment != null)
                    fragment.waiterQueueUpdated(orderInQueueWaiter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void billQueueUpdated(Object obj) {
        Log.e("SiganalR", obj.toString());
        FragmentManager manager = getFragmentManager();
        Action fragment = null;
        boolean isContains = false;
        if (manager.findFragmentById(R.id.fragmentWindow) instanceof Action)
            fragment = (Action) manager.findFragmentById(R.id.fragmentWindow);

        try {
            JSONArray jsoArr = new JSONArray(new Gson().toJsonTree(obj).toString());
            for (int i = 0; i < jsoArr.length(); i++) {
                JSONObject jObj = jsoArr.getJSONObject(i);
                int billID = jObj.getInt("BillId");
                int userID = jObj.getInt("UserId");
                if (billID == AppController.getBillId() /*&&
                        userID ==AppController.getLoginUser().getUserId()*/) {
                    //Toast.makeText(this, "" + i, Toast.LENGTH_LONG).show();
                    orderInQueueBill = i;
                    isContains = true;
                    if (fragment != null)
                        fragment.billQueueUpdated(orderInQueueBill);
                    break;
                }
            }
            if (!isContains) {
                if (orderInQueueBill != -1) {
                    AppHelper.getInstance().NotificationsBuilder("Bill is on the way", this,
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
                    sharedPref.setBooleanForKey(false, IS_CAN_GET_BILL);
                    isCanGetBill = false;
                }
                orderInQueueBill = -1;
                if (fragment != null)
                    fragment.billQueueUpdated(orderInQueueBill);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isCanGetBill() {
        return isCanGetBill;
    }
    //endregion

    //region SignalR Methods
    @Override
    public void resetTable() {
        toRestaurants();
        Toast.makeText(this, "Your Table is cancelled by admin", Toast.LENGTH_LONG).show();
    }

    @Override
    public void settleBill() {
        toRestaurants();
        Toast.makeText(this, "Your Bill is settled.Thank you", Toast.LENGTH_LONG).show();
    }

    @Override
    public void changeTable(String current) {
        updateTable(current);
    }

    @Override
    public void billUpdated(Object obj) {
        afterCheckBill(new Gson().toJsonTree(obj));
        /*FragmentManager manager = getFragmentManager();
        Basket fragment=null;
        if(manager.findFragmentById(R.id.fragmentWindow) instanceof Basket)
            fragment = (Basket)manager.findFragmentById(R.id.fragmentWindow);
        if(fragment!=null){
            fragment.afterCheckBill(new Gson().toJsonTree(obj));
        }*/
    }

    @Override
    public void restaurantActive(Object obj) {
        try {
            JSONObject jsonObject = new JSONObject(obj.toString());
            int resID = jsonObject.getInt("RestaurantId");
            boolean isActive = jsonObject.getBoolean("Active");
            if (resID == AppController.getCurrentRestaurent().getRestaurantID()) {
                AppController.getCurrentRestaurent().setRestaurantActive(isActive);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void appDisable(Object obj) {
        try {
            JSONObject jsonObject = new JSONObject((new Gson().toJsonTree(obj)).toString());
            boolean isAppLock = jsonObject.getBoolean("AppLock");
            AppController.setIsAppDisable(isAppLock);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void userBlocked() {
        signOut();
    }

    public void signOut() {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        sharedPrefManager.SetUserIsLoggedIn(null);
        LoginManager.getInstance().logOut();
        Intent intentAct = new Intent(TabActivity.this,
                LoginActivity.class);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentAct);
        finish();
    }
    //endregion

    //region QR Code Methods

    /***
     * Qr code Methods
     ***/

    public void ToQrScanner(View view) {
        /*int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(this, QrScanner.class);
            startActivityForResult(i, REQUEST_QR);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_QR) {
            String text = data.getStringExtra("result");
            mEtTableCode.setText(text);
            getTable();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
          /*  case MY_PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(this, QrScanner.class);
                    startActivityForResult(i, REQUEST_QR);

                } else {


                }
                return;
            }*/
        }
    }
    //endregion

    //region Collaboration
    AlertDialog dialog;

    public void showGuestDialog(String message) {
        TextView myMsg = new TextView(this);
        myMsg.setText(message);
        myMsg.setPadding(40, 40, 40, 40);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        if (dialog != null) {
            dialog.dismiss();
        }
        dialog = new AlertDialog.Builder(this)
                .setView(myMsg)
                .show();
    }

    public void tableStateChange(int billId, boolean isToSubscribe) {
        if (rl_tableCode.getVisibility() == View.VISIBLE) {
            rl_tableJoin.setVisibility(View.VISIBLE);
            rl_tableCode.setVisibility(View.GONE);
        } else {
            rl_tableJoin.setVisibility(View.GONE);
            rl_tableCode.setVisibility(View.VISIBLE);
        }
        if (isToSubscribe) {
            dataPro.SubscribeToBill(billId);
        } else {
            dataPro.UnSubscribeToBill(billId);
        }
    }

    @Override
    public void userWantToJoin(Object obj) {
        try {
            if (AppController.isHost()) {
                JSONObject jsonObj = new JSONObject(new Gson().toJson(obj));
                GuestRequestModel guestReq = getGuestRequestModel(jsonObj);
                AppController.setCurrentGuest(guestReq);
                afterUserJoinRequest(guestReq);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public GuestRequestModel getGuestRequestModel(JSONObject jsonObj) {

        try {
            int requestId = jsonObj.getInt("RequestId");
            String tableNumber = jsonObj.getString("TableNumber");
            int billId = jsonObj.getInt("BillId");
            String hostName = jsonObj.getString("UserName");
            return new GuestRequestModel(tableNumber, billId, hostName, requestId);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void responseToJoin(Object obj) {
        try {
            JSONObject jsonObj = new JSONObject(new Gson().toJsonTree(obj).toString());
            int billId = jsonObj.getInt("billId");
            int reqState = jsonObj.getInt("RequestState");
            int reqId = jsonObj.getInt("RequestId");
            if (runable != null)
                handler.removeCallbacks(runable);

            if (reqState == 1) {
                if (AppController.getCurrentGuest() != null && !AppController.isHost() &&
                        reqId == AppController.getCurrentGuest().getRequestId()) {
                    setTable(billId, AppController.getTableId());
                        /*AppController.setIsJoinTableAsGuest(true);*/
                    rl_tableJoin.setVisibility(View.GONE);
                    showGuestDialog("You have joined the table");
                } else if (AppController.isHost()) {
                    showGuestDialog(String.format("%s has joined the table. Order together and enjoy!", AppController.getCurrentGuest().getname()));
                }
            } else {
                if (AppController.getCurrentGuest() != null && !AppController.isHost() && AppController.getCurrentGuest().getRequestId() == reqId) {
                    showGuestDialog(String.format("Host rejected your request"));
                    AppController.setCurrentGuest(null);
                    AppController.setTableId(""+0);
                    AppController.setBillId(0);
                    tableStateChange(billId, false);
                }
            }
            if (guestRequestQueue != null && !guestRequestQueue.isEmpty()) {
                afterUserJoinRequest(guestRequestQueue.element());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void joinRequestCancel() {
        if (requestDialog != null) {
            requestDialog.dismiss();
        }
    }

    Handler handler = new Handler();
    Runnable runable;

    AlertDialog requestDialog;

    private void afterUserJoinRequest(final GuestRequestModel grm) {
        String msg = String.format("%s  has" +
                " requested to become guest on your Table. Would you like to accept the" +
                " request?", grm.getname());
        requestDialog = showGuestRequesDialog(this, msg, "Accept", "Reject", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dataPro.ResponseToJoinRequest(grm.getRequestId(), grm.getBillId(), 1);
                if (guestRequestQueue != null && !guestRequestQueue.isEmpty()) {
                    guestRequestQueue.remove();
                }

            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dataPro.ResponseToJoinRequest(grm.getRequestId(), grm.getBillId(), 3);
                if (guestRequestQueue != null && !guestRequestQueue.isEmpty()) {
                    guestRequestQueue.remove();
                }
            }
        });
    }

    private void joinTableWait(String tableNumber, int billid, String name, int reqId) {
        AppController.setCurrentGuest(new GuestRequestModel(tableNumber, billid, name, reqId));
        AppController.setBillId(billid);
        AppController.setTableId(tableNumber);
        tableStateChange(billid, true);
        showGuestDialog(String.format("Table %s is already occupied. An approval request is sent to the Host, " +
                "%s to accept you as guest. Please wait.", tableNumber, name));
        runable = new Runnable() {
            @Override
            public void run() {
                cancelGuestRequest(null);
            }
        };
        handler.postDelayed(runable, 900000);

    }

    public void cancelGuestRequest(View view) {
        tableStateChange(AppController.getBillId(), false);
        AppController.setCurrentGuest(null);
        AppController.setTableId(""+0);
        AppController.setBillId(0);
    }

    //endregion

    //region Check Bill Related Methods

    ArrayList<CheckBillModel> bufferBilledItem;

    public void checkBill() {

        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        nameValuePairs.add(new BasicNameValuePair("billId",
                AppController.getBillId().toString()));
        NetworkManagerOld httpMan = new NetworkManagerOld(this,
                NetworkManagerOld.EnCallType.POSTFORLOGIN, NetworkManagerOld.ReturnTypeForReponse.String, nameValuePairs,
                new TaskCompleted() {

                    @Override
                    public void onTaskFailed() {
                        // TODO Auto-generated method stub
                        AppHelper.showConnectionAlert(TabActivity.this);
                    }

                    @Override
                    public void onTaskCompletedSuccessfully(Object obj) {
                        // TODO Auto-generated method stub
                        afterCheckBill(obj);
                        Log.e("Menyoo", obj.toString());
                    }
                });

        String url = "GetCheckBillDetails";
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterCheckBill(Object obj) {
        try {
            JSONArray jsonArr = new JSONArray(obj.toString());
            bufferBilledItem = new ArrayList<>();
            if (billArr.size() > 0)
                bufferBilledItem.addAll(billArr);
            billArr = new ArrayList<>();
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jObj = jsonArr.getJSONObject(i);
                String userNAme = jObj.getString("UserName");
                boolean itemState = jObj.getBoolean("ItemState");
                Integer orderId = jObj.getInt("OrderId");
                Integer itemId = jObj.getInt("ItemId");
                Integer itemQuantity = jObj.getInt("ItemQuaintity");
                String itemCode = jObj.getString("ItemCode");
                String itemName = jObj.getString("ItemName");
                String ItemComments = jObj.getString("ItemComments");
                double unitPrice = jObj.getDouble("UnitPrice");
                Integer orderRequestState = jObj.getInt("OrderRequestStateId");
                billArr.add(new CheckBillModel(itemState, userNAme, orderId, itemId, itemQuantity, itemCode
                        , itemName, ItemComments, unitPrice, orderRequestState));
            }
            updateBasketFragment();
            //generateItemStateChangeNoti();
            if (billArr.size() > 0) {
                llChkBill.setVisibility(View.VISIBLE);
                llChkBill.setEnabled(true);
            } else
                llChkBill.setVisibility(View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateBasketFragment() {
        FragmentManager manager = getFragmentManager();
        if (manager.findFragmentById(R.id.fragmentWindow) instanceof Basket) {
            Basket fragment = (Basket) manager.findFragmentById(R.id.fragmentWindow);
            if (fragment != null) {
                fragment.updateCheckBillBtn();
            }
        } else if (manager.findFragmentById(R.id.fragmentWindow) instanceof CheckBill) {
            CheckBill fragment = (CheckBill) manager.findFragmentById(R.id.fragmentWindow);
            if (fragment != null) {
                fragment.updatedata(billArr);
            }
        }

    }

    public void generateItemStateChangeNoti() {
        for (int i = 0; i < bufferBilledItem.size() - 1; i++) {
            if (bufferBilledItem.get(i).getItemId().equals(billArr.get(i).getItemId()))
                if (bufferBilledItem.get(i).isItemState() != billArr.get(i).isItemState())
                    AppHelper.getInstance().newNotificationStyle(bufferBilledItem.get(i).getItemName() + "  is accepted", this,
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
        }
    }

    public void redirectOnPushNotification() {

        String text;

        if (getIntent().getExtras() != null)
            text = getIntent().getExtras().getString("To");
        else
            text = null;
        if (text != null) {
            if (text.equalsIgnoreCase("Action"))
                replaceFragment(new Action(), true);
            getIntent().getExtras().clear();
        }
    }

    //endregion

    //region Banner Methode
    public void collapseBanner(View view) {
        AppHelper.collapse(rlBanner);
    }

    public void setBannerText(String text) {
        if (text != null && !text.trim().isEmpty()) {
            mBannerText.setText(text);
            AppHelper.expand(rlBanner);
        }

    }

    public void getBannerMessage() {

        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(TabActivity.this,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.showConnectionAlert(TabActivity.this);

            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                try {
                    JSONObject jObj = new JSONObject(obj.toString());
                    String meesage = jObj.getString("Message");
                    setBannerText(meesage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        String url = String.format("RestaurantMessage?restaurantId=%d", AppController.getCurrentRestaurent().getRestaurantID());

        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }


    //endregion

    //region ssaving data
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("RESID", AppController.getCurrentRestaurent().getRestaurantID());
        savedInstanceState.putInt("BillID", AppController.getBillId());
        // etc.
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        AppController.getCurrentRestaurent().setRestaurantID(savedInstanceState.getInt("RESID"));
        AppController.setBillId(savedInstanceState.getInt("BillID"));
    }
    //endregion

}

