package com.dd.menyoo.fragment;


import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.adapter.OrderAdapter;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.OrderModel;
import com.dd.menyoo.model.OrderModelForPost;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.SignalRManager;
import com.dd.menyoo.network.TaskCompleted;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Basket extends BaseFragment implements View.OnClickListener {

    HashMap<String, OrderModel> mHashMapOrder;
    ArrayList<String> mKeys;
    Button btnSubmit, btnCheckBill,mEdit,mGtoMenu;
    RecyclerView mRvOrder;
    ArrayList<OrderModelForPost> mOrderModelPost;
    TextView mTQuantity, mTCost, mNoOrder;
    OrderAdapter orderAdapter;
    String dialogText;
    ArrayList<CheckBillModel> billArr;

    public Basket() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basket, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvOrder = (RecyclerView) view.findViewById(R.id.rv_order);
        mTCost = (TextView) view.findViewById(R.id.tv_total_cost);
        mTQuantity = (TextView) view.findViewById(R.id.tv_total_quantity);
        mNoOrder = (TextView) view.findViewById(R.id.tv_noorder);
        btnSubmit = (Button) view.findViewById(R.id.btn_submit);
        mGtoMenu = (Button)view.findViewById(R.id.btn_gto_menu);
        mEdit = (Button)view.findViewById(R.id.btn_edit);mEdit.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);mGtoMenu.setOnClickListener(this);
        btnCheckBill = (Button) view.findViewById(R.id.btn_check_bill);btnCheckBill.setOnClickListener(this);
        mOrderModelPost = new ArrayList<>();
        dialogText = "";
        setAdapter();
        setOrderArrForPost();
    }

    public void setAdapter() {
        orderAdapter = new OrderAdapter(getActivity(),this);
        mRvOrder.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        orderAdapter.setData(mHashMapOrder, mKeys);
        mRvOrder.setAdapter(orderAdapter);
        setViews();
    }

    public void setOrderArrForPost() {
        mOrderModelPost = new ArrayList<>();
        OrderModel orderItem;
        for (int i = 0; i < mKeys.size(); i++) {
            OrderModelForPost op = new OrderModelForPost();
            orderItem = mHashMapOrder.get(mKeys.get(i));
            op.setItemId(orderItem.getMenu().getItemID());
            op.setItemQuaintity(orderItem.getQuantity());
            op.setUnitPrice(orderItem.getMenu().getPrice());
            op.setItemComments(orderItem.getComment());
            mOrderModelPost.add(op);
        }
    }

    public void setViews() {
        updateCheckBillBtn();
        /*if(((TabActivity)getActivity()).isOrderSubmitted())
            btnCheckBill.setEnabled(true);*/
        /*if(((TabActivity)getActivity()).checkIsTableAcquired()){
            checkBill();
        }*/

        if (mHashMapOrder.size() == 0) {
            if(dialogText.length()>1)
                ((TabActivity)getActivity()).showGenricDialog(getActivity(), dialogText, "Ok", "Check Bill", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        btnCheckBill.performClick();
                    }
                });

            /*new AlertDialog.Builder(getActivity())
                    .setMessage(dialogText)
                    .setPositiveButton("Go back to Menu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            gtoMenu();
                        }
                    })
                    .show();*/
            mGtoMenu.setVisibility(View.VISIBLE);
            mNoOrder.setVisibility(View.VISIBLE);
            btnSubmit.setEnabled(false);
            mTQuantity.setText("T.Quantity: N/A");
            mTCost.setText("T.Cost: N/A");
        } else {
            double tCost = 0;
            int tQuantity = 0;
            for (String i : mKeys) {
                OrderModel orderItem = mHashMapOrder.get(i);
                tCost += orderItem.getMenu().getPrice() * orderItem.getQuantity();
                tQuantity += orderItem.getQuantity();
            }
            mTQuantity.setText(String.format("T.Quantity: %d", tQuantity));
            mTCost.setText(String.format("T.Cost: RM %.2f", tCost));
        }
    }

    public void gtoMenu(){
        ((TabActivity)getActivity()).replaceFragment(new MenuTab1(),true);
    }

    public void setmHashMapOrder(HashMap<String, OrderModel> mHashMapOrder, ArrayList<String> keys) {
        this.mHashMapOrder = mHashMapOrder;
        this.mKeys = keys;
    }

    protected void sendOrder() {
        AppHelper.getInstance().showProgressDialog("Order submitting",getActivity());
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        nameValuePairs.add(new BasicNameValuePair("item",
                getJObj().toString()));
        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORLOGIN, NetworkManagerOld.ReturnTypeForReponse.String, nameValuePairs,
                new TaskCompleted() {

                    @Override
                    public void onTaskFailed() {
                        // TODO Auto-generated method stub
                        AppHelper.showConnectionAlert(getActivity());
                    }

                    @Override
                    public void onTaskCompletedSuccessfully(Object obj) {
                        // TODO Auto-generated method stub
                        afterSendOrder(obj);
                        Log.e("Menyoo", obj.toString());
                    }
                });

        String url = "GetUserOrder";
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterSendOrder(Object obj) {
        try {
            JSONObject jsonObj = new JSONObject(obj.toString());
            String status = jsonObj.getString("Status");
            if (status.equals("Success")) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Order Submitted Successfully", Snackbar.LENGTH_LONG)
                        .setActionTextColor(Color.RED)
                        .show();
                ((TabActivity)getActivity()).setOrderSubmitted(true);
                /*btnCheckBill.setEnabled(true);*/
                ((TabActivity)getActivity()).checkBill();
                ((TabActivity) getActivity()).clearOrder();
                setmHashMapOrder(new HashMap<String, OrderModel>(), new ArrayList<String>());
                //setViews();
                dialogText = "Your order has been submitted.";
                setAdapter();
            }else{
                dialogText = "Your order has not been submitted.";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            AppHelper.getInstance().hideProgressDialog();
        }

    }

    public JSONObject getJObj() {
        JsonArray arr;
        JSONObject obj = null;
        try {
            obj = new JSONObject();
            Gson gson = new GsonBuilder().create();
            arr = gson.toJsonTree(mOrderModelPost).getAsJsonArray();
            String jsonArr = arr.toString().replace("/", "");
            JSONArray jsonArray = new JSONArray(jsonArr);
            obj.accumulate("UserId", AppController.getLoginUser().getUserId());
            obj.accumulate("BillId", AppController.getBillId());
            obj.accumulate("OrderDetails", jsonArray);
            Log.e("JsonObj", obj.toString());
            return obj;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    private void checkBill() {

        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        nameValuePairs.add(new BasicNameValuePair("billId",
                AppController.getBillId().toString()));
        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORLOGIN, NetworkManagerOld.ReturnTypeForReponse.String, nameValuePairs,
                new TaskCompleted() {

                    @Override
                    public void onTaskFailed() {
                        // TODO Auto-generated method stub
                        AppHelper.showConnectionAlert(getActivity());
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

    public void afterCheckBill(Object obj){
        try {
            JSONArray jsonArr = new JSONArray(obj.toString());
            billArr = new ArrayList<>();
            for(int i=0;i<jsonArr.length();i++){
                JSONObject jObj = jsonArr.getJSONObject(i);
                boolean itemState = jObj.getBoolean("ItemState");
                Integer orderId = jObj.getInt("OrderId");
                Integer itemId = jObj.getInt("ItemId");
                Integer itemQuantity = jObj.getInt("ItemQuaintity");
                String itemCode = jObj.getString("ItemCode");
                String itemName = jObj.getString("ItemName");
                String ItemComments = jObj.getString("ItemComments");
                double unitPrice = jObj.getDouble("UnitPrice");
                Integer orderRequestState = jObj.getInt("OrderRequestStateId");
                billArr.add(new CheckBillModel(itemState,orderId,itemId,itemQuantity,itemCode
                        ,itemName,ItemComments,unitPrice,orderRequestState));
            }
            if(billArr.size()>0){
                btnCheckBill.setEnabled(true);
            }else
                btnCheckBill.setEnabled(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toCheckBill() {
        CheckBill chkFrag = new CheckBill();
        chkFrag.setmCheckBillArr(((TabActivity)getActivity()).billArr);
        ((TabActivity)getActivity()).replaceFragment(chkFrag,true);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                if(SignalRManager.getInstance().isConnected()){
                    ((TabActivity)getActivity()).showGenricDialog(getActivity(), "Submit Order ?", "Proceed", "Cancel"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    sendOrder();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                }else{
                    AppHelper.showConnectionAlert(getActivity());
                }

                break;
            case R.id.btn_edit:
                orderAdapter.setDeleteEnabled();
                break;
            case R.id.btn_check_bill:
                /*checkBill();*/toCheckBill();
                break;
            case R.id.btn_gto_menu:
                gtoMenu();
                break;
        }
    }



    public void removeItemFromBasket(int pos){
        orderAdapter.remove(pos);
        setOrderArrForPost();setViews();
        ((TabActivity)getActivity()).updateBasketItemCount();
        /*mHashMapOrder.remove(mKeys.get(pos));
        mKeys.remove(pos);*/
        /*orderAdapter.setData(mHashMapOrder,mKeys);
        orderAdapter.notifyDataSetChanged();
        */
    }

    public void chageQuanttiy(int pos,int newQuantity){
        mHashMapOrder.get(mKeys.get(pos)).setQuantity(newQuantity);
        setViews();
        ((TabActivity)getActivity()).updateBasketItemCount();
        setOrderArrForPost();
    }

    public void updateCheckBillBtn(){
        if(((TabActivity)getActivity()).billArr.size()>0)
            btnCheckBill.setEnabled(true);
        else
            btnCheckBill.setEnabled(false);
    }

}
