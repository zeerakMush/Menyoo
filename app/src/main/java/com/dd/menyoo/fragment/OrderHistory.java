package com.dd.menyoo.fragment;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.adapter.CheckBillAdapter;
import com.dd.menyoo.adapter.OrderAdapter;
import com.dd.menyoo.adapter.OrderHistoryAdapter;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.CategoryExtra;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.Options;
import com.dd.menyoo.model.OrderHistoryModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderHistory extends BaseFragment {

    ProgressBar pbWait;
    RecyclerView rvOrderHistory;
    OrderHistoryAdapter ohma;
    public OrderHistory() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pbWait = (ProgressBar)view.findViewById(R.id.pb_wait);
        rvOrderHistory = (RecyclerView)view.findViewById(R.id.rv_orders);
        getOrderHistory();setAdapter();
    }

    public void setAdapter(){
        ohma = new OrderHistoryAdapter(getActivity());
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        ohma.setData(new ArrayList<OrderHistoryModel>());
        ohma.set_listener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = (int)view.getTag();
                checkBill(id);
            }
        });
        rvOrderHistory.setAdapter(ohma);
    }

    protected void getOrderHistory() {
        pbWait.setVisibility(View.VISIBLE);
        pbWait.bringToFront();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
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
                afterGetOrder(obj);
            }
        });

        String url =String.format("GetBillHistory?UserId=%d", AppController.
                getLoginUser().getUserId());
        String[] params = {getString(R.string.url_offline)+url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterGetOrder(Object obj){
        try {
            pbWait.setVisibility(View.GONE);
            JSONArray jsonArr = new JSONArray(obj.toString());
            ArrayList<OrderHistoryModel> orderHistoryArr = new ArrayList<>();
            for(int i=0;i<jsonArr.length();i++){
                JSONObject jObj = jsonArr.getJSONObject(i);
                String resName= jObj.getString("RestaurantName");
                int billId = jObj.getInt("BillId");
                String date = jObj.getString("CreateDate");
                date =  AppHelper.getDateTime(date);
                orderHistoryArr.add(new OrderHistoryModel(resName,billId,date));
            }
            ohma.setData(orderHistoryArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkBill(Integer billId ){
        AppHelper.getInstance().showProgressDialog("Please Wait",getActivity());
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        nameValuePairs.add(new BasicNameValuePair("billId",
                billId.toString()));
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
        String[] params = {getActivity().getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterCheckBill(Object obj){
        try {
            JSONArray jsonArr = new JSONArray(obj.toString());
            ArrayList<CheckBillModel> billArr = new ArrayList<>();
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
                String userName = jObj.getString("UserName");
                String acceptedTime = jObj.getString("CreateTime");
                ArrayList<CategoryExtra> variants= getExtraOptionData(jObj.getJSONArray("Extras"));
                acceptedTime =  AppHelper.getDateTimeForAcceptedTime(acceptedTime);
                Integer orderRequestState = jObj.getInt("OrderRequestStateId");
                billArr.add(new CheckBillModel(itemState,userName,orderId,itemId,itemQuantity,itemCode
                        ,itemName,ItemComments,unitPrice,orderRequestState,acceptedTime,variants));
            }
            showOrderDialog(billArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CategoryExtra> getExtraOptionData(JSONArray extras){
        try {
            ArrayList<CategoryExtra> categoryExtras = new ArrayList<>();
            for(int i=0;i<extras.length();i++){
                String optionName = extras.getJSONObject(i).getString("Option");
                double price = extras.getJSONObject(i).getDouble("Price");
                Options option = new Options(optionName,price);
                ArrayList<Options> optionses = new ArrayList<>();
                optionses.add(option);
                categoryExtras.add(new CategoryExtra("",optionses));
            }
            return categoryExtras;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    public void showOrderDialog(ArrayList<CheckBillModel> data) {

        AppHelper.getInstance().hideProgressDialog();
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.order_history_dialog);

        RecyclerView rvOrderHistory = (RecyclerView) dialog.findViewById(R.id.rv_order_history);
        TextView tvQuantity = (TextView) dialog.findViewById(R.id.tv_total_quantity);
        TextView tvCost = (TextView) dialog.findViewById(R.id.tv_total_cost);
        TextView SubTotal = (TextView) dialog.findViewById(R.id.tv_subtotal);
        TextView ServiceCharge = (TextView) dialog.findViewById(R.id.tv_service_charge);
        TextView Gst = (TextView) dialog.findViewById(R.id.tv_gst);
        int tQuantity = 0;
        double subTotal = 0, gst = 0, tCost = 0, serviceCharge = 0;


        for (CheckBillModel i : data) {
            subTotal += i.getUnitPrice() * i.getQuantity();
            tQuantity += i.getQuantity();
        }
        serviceCharge = subTotal*0.1;
        gst = (subTotal+serviceCharge)*0.06;
        tCost = subTotal+gst+serviceCharge;

        tvQuantity.setText(String.format("T.Quantity: %d", tQuantity));
        tvCost.setText(String.format("T.Cost: RM %.2f", tCost));
        Gst.setText(String.format("RM %.2f", gst));
        ServiceCharge.setText(String.format("RM %.2f", serviceCharge));
        SubTotal.setText(String.format("RM %.2f", subTotal));


        CheckBillAdapter chkAdapter = new CheckBillAdapter(getActivity());
        rvOrderHistory.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        chkAdapter.setData(data);
        rvOrderHistory.setAdapter(chkAdapter);
        dialog.show();

    }


}
