package com.dd.menyoo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.model.CheckBillModel;
import com.dd.menyoo.model.OrderModel;

import java.util.ArrayList;
import java.util.HashMap;


public class CheckBillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<CheckBillModel> mDataList;
    Context mCtx;

    public CheckBillAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void setData(ArrayList<CheckBillModel> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.basket_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        CheckBillModel item = mDataList.get(position);

        String orderName = String.format("x%d %s",item.getQuantity(),item.getItemName());
        holder.tvTitle.setText(orderName);
        holder.tvPrice.setText(String.format("RM %.2f",item.getUnitPrice()*item.getQuantity()));
        if(!item.getItemComment().trim().isEmpty()){
            holder.tvComment.setVisibility(View.VISIBLE);
            holder.tvComment.setText(item.getItemComment());
        }else
            holder.tvComment.setVisibility(View.GONE);

        String text;
        if(item.isItemState()){
            text = " <font color='"
                    + mCtx.getResources().getColor(R.color.green) + "'>" + "Accepted"
                    + "</font>";
        }else{
            text = " <font color='"
                    + mCtx.getResources().getColor(R.color.red) + "'>" + "Pending"
                    + "</font>";
        }
        holder.tvState.setText(Html.fromHtml(text +" - By "+item.getUserName()),
                TextView.BufferType.SPANNABLE);
        holder.tvState.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle,tvPrice,tvComment,tvState;
        private Button btnDelete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_order_name);
            tvComment = (TextView) itemView.findViewById(R.id.tv_order_comment);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_order_prize);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            tvState = (TextView) itemView.findViewById(R.id.tv_order_state);
        }
    }
}
