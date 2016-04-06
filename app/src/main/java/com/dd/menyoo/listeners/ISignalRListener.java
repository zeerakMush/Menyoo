package com.dd.menyoo.listeners;

/**
 * Created by Administrator on 03-Mar-16.
 */
public interface ISignalRListener {
    void waiterQueueUpdated(Object obj);
    void billQueueUpdated(Object obj);
    void resetTable();
    void settleBill();
    void billUpdated(Object obj);
    void userWantToJoin(Object obj);
    void responseToJoin(Object obj);
    void changeTable(String current);
    void joinRequestCancel();
    void restaurantActive(Object obj);
    void appDisable(Object obj);
    void userBlocked();

}
