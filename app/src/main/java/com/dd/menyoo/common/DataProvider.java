package com.dd.menyoo.common;

import android.content.Context;
import android.util.Log;

import com.dd.menyoo.model.BaseResponse;
import com.dd.menyoo.network.SignalRManager;

import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by Administrator on 02-Mar-16.
 */
public class DataProvider {
    private Context cont;

    public DataProvider(Context conxt) {
        this.cont = conxt;
    }

    public DataProvider() {
    }
    public <E> E invokeSignalRService(final Class<E> resultClass, final String method, Object... args)
            throws Exception {
        E response = null;
        try {
            // Check for signal r connectivity
            if (!SignalRManager.getInstance().isConnected())
                Log.e("Signal R","Error");
                //throw new SignalRException(cont.getString(R.string.signalr_error_message));

            HubProxy hub = SignalRManager.getInstance().getHub();
            SignalRFuture<E> awaitResp = hub.invoke(resultClass, method, args);
            response = awaitResp.get();

            /*BaseResponse baseResponse = (BaseResponse) response;*/

            // Check for success status in signal r response
          /*  if (!baseResponse.Success)
                throw new Exception(baseResponse.FaultMessage);*/

        } catch (Exception ex) {
            ex.printStackTrace();
            // System failure exceptions should be hidden from end users
            /*if (!(ex instanceof SignalRException) && !(ex instanceof SproutChatException))
                throw new Exception(cont.getString(R.string.generic_error_msg));
            else*/
                throw ex;
        }

        return response;
    }

    public BaseResponse UserJoinedRestaurant(String resId) {
        try {
            BaseResponse resp = invokeSignalRService(
                    BaseResponse.class, "UserJoinedRestaurant", resId);

            return resp;
        } catch (Exception e) {
            BaseResponse resp = new BaseResponse();
            resp.Success = false;
            resp.FaultMessage = e.getMessage();
            resp.exception = e;
            return resp;
        }
    }

    public Object UserGetWaiterGroup(String RestaurantId){
        try {
            return invokeSignalRService(
                    Object.class, "UserGetWaiterGroup", RestaurantId);


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object UserGetBillGroup(String RestaurantId){
        try {
            return  invokeSignalRService(
                    Object.class, "UserGetBillGroup", RestaurantId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void UserGetWaiter(String RestaurantId,int BillId){
        try {
            invokeSignalRService(Boolean.class,"UserGetWaiter",RestaurantId,BillId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UserGetBill(String RestaurantId,int BillId){
        try {
            invokeSignalRService(Boolean.class,"UserGetBill",RestaurantId,BillId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SubscribeToBill(int billId){
        try {
            invokeSignalRService(Boolean.class,"SubscribeToBill",billId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void UnSubscribeToBill(int billId){
        try {
            invokeSignalRService(Boolean.class,"UnSubscribeToBill",billId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ResponseToJoinRequest(int requestId,int BillId,int RequestState){
        try {
            invokeSignalRService(Boolean.class,"ResponseToJoinRequest",requestId,BillId,RequestState);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
