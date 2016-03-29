package com.dd.menyoo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.fragment.BaseFragment;
import com.dd.menyoo.fragment.Basket;
import com.dd.menyoo.model.OrderModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 24-Feb-16.
 */
public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private HashMap<String,OrderModel> mDataList;
    private ArrayList<String> mKeys;
    private boolean isEditEnabled;
    private int mRowIndex = -1;
    private Basket fragment;
    Context mCtx;
    private int[] mColors = new int[]{R.drawable.sample_1,R.drawable.sample_2,R.drawable.sample_3};


    public OrderAdapter(Context ctx, BaseFragment fragment) {
        this.mCtx = ctx;
        this.fragment =(Basket) fragment;
    }

    public void setData(HashMap<String,OrderModel> data,ArrayList<String> keys) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
        this.mKeys = keys;
    }

    public void setDeleteEnabled(){
        isEditEnabled = !isEditEnabled;
        notifyDataSetChanged();
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
        final String key = mKeys.get(position);
        final OrderModel orderItem = mDataList.get(key);
        if(isEditEnabled){
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.llChangeQuantity.setVisibility(View.VISIBLE);
            holder.tvQuantity.setText(String.format("%d", mDataList.get(key).getQuantity()));}
        else{
            holder.btnDelete.setVisibility(View.GONE);
            holder.llChangeQuantity.setVisibility(View.GONE);
        }

        //String orderText = Html.fromHtml("<small>X</small> <font size=\"16\">"+orderItem.getQuantity()+"\""+orderItem.getMenu().getTitle()+"</font>").toString();

        String orderName = String.format("x%d %s",orderItem.getQuantity(),orderItem.getMenu().getTitle());
        holder.tvTitle.setText(orderName);
        holder.tvPrice.setText(String.format("RM %.2f",orderItem.getMenu().getPrice()*orderItem.getQuantity()));
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TabActivity)mCtx).showGenricDialog(mCtx, String.format("Delete %s", orderItem.getMenu().getTitle()),
                        "Yes", "No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                fragment.removeItemFromBasket(position);
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
            }
        });
        holder.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mDataList.get(key).getMenu().isfirstTimeItem()){
                    int quantity=mDataList.get(key).getQuantity()+1;
                    fragment.chageQuanttiy(position,quantity);
                    mDataList.get(key).setQuantity(quantity);
                    notifyDataSetChanged();
                }else{
                    Toast.makeText(mCtx,"Only Single item is allowed",Toast.LENGTH_LONG).show();
                }

            }
        });
        holder.subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity=mDataList.get(key).getQuantity()-1;
                if(quantity>0){
                    fragment.chageQuanttiy(position,quantity);
                    mDataList.get(key).setQuantity(quantity);
                    notifyDataSetChanged();
                }

            }
        });
        if(!orderItem.getComment().trim().isEmpty()){
            holder.tvComment.setVisibility(View.VISIBLE);
            holder.tvComment.setText(orderItem.getComment());
        }else
            holder.tvComment.setVisibility(View.GONE);

    }
    public void remove(int position){
        mDataList.remove(mKeys.get(position));
        mKeys.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataList.size());
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitle,tvPrice,tvComment,tvQuantity;
        private Button btnDelete, addBtn, subBtn;
        private LinearLayout llChangeQuantity;


        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_order_name);
            tvComment = (TextView) itemView.findViewById(R.id.tv_order_comment);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_order_prize);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
            tvQuantity = (TextView)itemView.findViewById(R.id.tv_quantity);
            addBtn = (Button)itemView.findViewById(R.id.btn_add);
            subBtn = (Button)itemView.findViewById(R.id.btn_sub);
            llChangeQuantity = (LinearLayout)itemView.findViewById(R.id.ll_changeQuantity);
        }
    }




}
