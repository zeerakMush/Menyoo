package com.dd.menyoo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.model.OrderModel;
import com.dd.menyoo.model.QuestionModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 01-Mar-16.
 */
public class AboutUsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ArrayList<QuestionModel> mDataList;
    private boolean isEditEnabled;
    private int mRowIndex = -1;
    Context mCtx;

    public AboutUsAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void setData(ArrayList<QuestionModel> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }

    public void setDeleteEnabled(){
        isEditEnabled = !isEditEnabled;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.faq_question_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        holder.tvAnswer.setVisibility(View.GONE);
        holder.tvQuestion.setText(mDataList.get(position).getQuestion());
        holder.tvAnswer.setText(mDataList.get(position).getAnswer());
        holder.tvQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.tvAnswer.setVisibility(holder.tvAnswer.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
            }
        });

        // holder.quickImageView.setImageResource(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {


        private TextView tvQuestion,tvAnswer;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvQuestion = (TextView) itemView.findViewById(R.id.tv_question);
            tvAnswer = (TextView) itemView.findViewById(R.id.tv_answer);

        }
    }



}
