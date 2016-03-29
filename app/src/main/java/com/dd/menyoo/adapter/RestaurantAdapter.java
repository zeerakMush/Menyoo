package com.dd.menyoo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.menyoo.MenuActivity;
import com.dd.menyoo.R;
import com.dd.menyoo.model.RestaurantModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 18-Feb-16.
 */
public class RestaurantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private ArrayList<RestaurantModel> mDataList;
    private ArrayList<RestaurantModel> filteredList;
    ArrayList<RestaurantModel> bufferArr;

    Context mCtx;
    private ModelFilter filter;


    public RestaurantAdapter(Context ctx) {
        this.mCtx = ctx;
        filteredList = new ArrayList<>();
        bufferArr = new ArrayList<>();
    }

    public void setData(ArrayList<RestaurantModel> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
        if(data.size()>1)
            bufferArr.addAll(mDataList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.restaurant_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        holder.pb_wait.bringToFront();
        if (mDataList.get(position).getRestaurantID() == 0) {
            holder.rlLocationSection.setVisibility(View.GONE);
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvLocation.setText(mDataList.get(position).getRestaurantName());
        } else {
            holder.rlLocationSection.setVisibility(View.VISIBLE);
            holder.tvLocation.setVisibility(View.GONE);
            holder.quickTextView.setText(mDataList.get(position).getRestaurantName());
            Picasso.with(mCtx)
                    .load(mDataList.get(position).getImageUrl())
                    .into(holder.quickImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pb_wait.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    })
            ;
            holder.quickImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!mDataList.get(position).isCommingSoon())
                        ((MenuActivity) mCtx).toTabActivity(mDataList.get(position));
                    else{
                        ((MenuActivity)mCtx).showDisableDialog(mCtx,mDataList.get(position).getIsCommingSoonMessage());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView quickImageView;
        private TextView quickTextView;
        private TextView tvLocation;
        private RelativeLayout rlLocationSection;
        private ProgressBar pb_wait;

        public ItemViewHolder(View itemView) {
            super(itemView);
            quickImageView = (ImageView) itemView.findViewById(R.id.quickImageView);
            quickTextView = (TextView) itemView.findViewById(R.id.tv_quickAction);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            rlLocationSection = (RelativeLayout) itemView.findViewById(R.id.rl_rest_Section);
            pb_wait = (ProgressBar) itemView.findViewById(R.id.pb_menu);
        }
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new ModelFilter();
        }
        return filter;
    }



    private class ModelFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();


            if (constraint.length() == 0) {
                filteredList.addAll(bufferArr);
            } else {
                final String filterPattern = constraint.toString().toLowerCase().trim();
                for(int i=0;i<bufferArr.size();i++){
                    if(i==0)
                        filteredList.add(bufferArr.get(0));
                    else if (bufferArr.get(i).getRestaurantName().toLowerCase().contains(constraint)) {
                        filteredList.add(bufferArr.get(i));
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            mDataList.clear();
            mDataList.addAll((ArrayList<RestaurantModel>) results.values);
            notifyDataSetChanged();
        }

    }
}
