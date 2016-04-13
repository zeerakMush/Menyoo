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
import com.dd.menyoo.model.CategoryExtra;
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
        double additionalPrice = 0.0;

        CheckBillModel item = mDataList.get(position);

        String orderName = String.format("x%d %s",item.getQuantity(),item.getItemName());
        holder.tvTitle.setText(orderName);
        if(item.getVaraints()!=null)
        {   additionalPrice = 0.0;
            for(CategoryExtra extra: item.getVaraints())
                additionalPrice += extra.getOptions().get(0).getPrice();
        }
        holder.tvPrice.setText(String.format("RM %.2f",(item.getUnitPrice()+additionalPrice)*item.getQuantity()));
        if(!item.getItemComment().trim().isEmpty()){
            holder.tvComment.setVisibility(View.VISIBLE);
            holder.tvComment.setText(item.getItemComment());
        }

        String text;
        if(item.isItemState()){
            text = " <font color='"
                    + mCtx.getResources().getColor(R.color.green) + "'>" + "Accepted"
                    + "</font>";
            if(!item.getAcceptedTime().trim().isEmpty()){
                holder.tvAcceptedTime.setText(item.getAcceptedTime());
                holder.tvAcceptedTime.setVisibility(View.VISIBLE);
            }else{
                holder.tvAcceptedTime.setVisibility(View.GONE);
            }

        }else{
            text = " <font color='"
                    + mCtx.getResources().getColor(R.color.red) + "'>" + "Pending"
                    + "</font>";
            holder.tvAcceptedTime.setVisibility(View.GONE);
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

        private TextView tvTitle,tvPrice,tvComment,tvState,tvAcceptedTime;
        private Button btnDelete;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_order_name);
            tvComment = (TextView) itemView.findViewById(R.id.tv_order_comment);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_order_prize);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            tvState = (TextView) itemView.findViewById(R.id.tv_order_state);
            tvAcceptedTime = (TextView) itemView.findViewById(R.id.tv_accepted_time);
        }
    }
}
