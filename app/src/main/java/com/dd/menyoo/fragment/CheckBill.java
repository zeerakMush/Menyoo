package com.dd.menyoo.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.adapter.CheckBillAdapter;
import com.dd.menyoo.adapter.OrderAdapter;
import com.dd.menyoo.model.CategoryExtra;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.OrderModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CheckBill extends BaseFragment {


    public CheckBill() {
        // Required empty public constructor
    }

    RecyclerView mRvOrder;
    TextView mTQuantity, mTCost,mTvInstruction,mSubTotal,mServiceCharge,mGst;
    CheckBillAdapter chkAdapter;
    ArrayList<CheckBillModel> mCheckBillArr;
    private SwipeRefreshLayout swipeContainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_bill, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRvOrder = (RecyclerView) view.findViewById(R.id.rv_order);
        mTCost = (TextView) view.findViewById(R.id.tv_total_cost);
        mTQuantity = (TextView) view.findViewById(R.id.tv_total_quantity);
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mTvInstruction = (TextView) view.findViewById(R.id.tv_instruction);
        mSubTotal = (TextView) view.findViewById(R.id.tv_subtotal);
        mServiceCharge = (TextView) view.findViewById(R.id.tv_service_charge);
        mGst = (TextView) view.findViewById(R.id.tv_gst);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((TabActivity)getActivity()).checkBill();
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mTvInstruction.setVisibility(View.GONE);
            }
        },1000);

        setAdapter();
    }

    public void setAdapter() {
        chkAdapter = new CheckBillAdapter(getActivity());
        mRvOrder.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false));
        chkAdapter.setData(mCheckBillArr);
        mRvOrder.setAdapter(chkAdapter);
        setViews(mCheckBillArr);
    }

    public void setViews(ArrayList<CheckBillModel> chkArr) {

        int tQuantity = 0;
        double subTotal = 0, gst = 0, tCost = 0, serviceCharge = 0;
        double additionalPrice = 0.0;

        for (CheckBillModel i : chkArr) {
            if(i.getVaraints()!=null)
            {   additionalPrice = 0.0;
                for(CategoryExtra extra: i.getVaraints())
                    additionalPrice += extra.getOptions().get(0).getPrice();
                    /*additionalPrice +=price;*/
                    /*orderItem.getMenu().setPrice(orderItem.getMenu().getPrice()+additionalPrice);*/
            }
            subTotal += (i.getUnitPrice() + additionalPrice)* i.getQuantity();
            tQuantity += i.getQuantity();
        }
        serviceCharge = subTotal*0.1;
        gst = (subTotal+serviceCharge)*0.06;
        tCost = subTotal+gst+serviceCharge;

        mTQuantity.setText(String.format("T.Quantity: %d", tQuantity));
        mTCost.setText(String.format("T.Cost: RM %.2f", tCost));
        mGst.setText(String.format("RM %.2f", gst));
        mServiceCharge.setText(String.format("RM %.2f", serviceCharge));
        mSubTotal.setText(String.format("RM %.2f", subTotal));


    }

    public void setmCheckBillArr(ArrayList<CheckBillModel> mCheckBillArr) {
        this.mCheckBillArr = mCheckBillArr;
    }

    public void updatedata(ArrayList<CheckBillModel> chkModel){
        chkAdapter.setData(chkModel);
        chkAdapter.notifyDataSetChanged();
        swipeContainer.setRefreshing(false);
        setViews(chkModel);
    }
}
