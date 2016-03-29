package com.dd.menyoo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.menyoo.MenuActivity;
import com.dd.menyoo.R;
import com.dd.menyoo.model.CategoryModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Administrator on 19-Feb-16.
 */
public class FullMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CategoryModel> mDataList;
    private int mRowIndex = -1;
    Context mCtx;
    View.OnClickListener mClickListener;
    private int[] mColors = new int[]{R.color.tab_default, R.color.tab_selected,
            R.color.blue_backgroud, R.color.blue_iphone,
            R.color.btn_color_Sec};


    public FullMenuAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public void setData(ArrayList<CategoryModel> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View itemView = LayoutInflater.from(context).inflate(R.layout.full_menu_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        holder.tvMenu.setText(mDataList.get(position).getName());
        holder.pb_wait.bringToFront();
        if (!mDataList.get(position).getImageName().trim().isEmpty())
            Picasso.with(mCtx)
                    .load(mDataList.get(position).getImageName())
                    .into(holder.ivMenu, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pb_wait.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        holder.ivMenu.setTag(mDataList.get(position));
        if (mClickListener != null)
            holder.ivMenu.setOnClickListener(mClickListener);
        // holder.quickImageView.setImageResource(mDataList.get(position));
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivMenu;
        private TextView tvMenu;
        private ProgressBar pb_wait;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ivMenu = (ImageView) itemView.findViewById(R.id.iv_menuImage);
            tvMenu = (TextView) itemView.findViewById(R.id.tv_menu);
            pb_wait = (ProgressBar) itemView.findViewById(R.id.pb_menu);

        }
    }

    public void setmClickListener(View.OnClickListener mClickListener) {
        this.mClickListener = mClickListener;
    }
}
