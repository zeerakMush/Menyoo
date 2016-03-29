package com.dd.menyoo.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.RoundedTransformation;
import com.dd.menyoo.fragment.BaseFragment;
import com.dd.menyoo.fragment.OrderPlacement;
import com.dd.menyoo.fragment.SpecialMenu;
import com.dd.menyoo.model.MenuModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Administrator on 19-Feb-16.
 */
public class SpecialTabAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MenuModel> mDataList;
    private static final int CHILD_ITEM_TYPE = 1;
    private static final int CONTENTS_TYPE = 2;
    private int type;


    private int mRowIndex = -1;
    BaseFragment mFragment;
    Context mCtx;
    private int[] mColors = new int[]{R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3};


    public SpecialTabAdapter(Context ctx, BaseFragment baseFragment) {
        this.mCtx = ctx;
        this.mFragment = baseFragment;
    }

    public void setData(ArrayList<MenuModel> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        //mDataList = new ArrayList<>();
        View itemView = LayoutInflater.from(context).inflate(R.layout.special_menu_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        final MenuModel menuItem = mDataList.get(position);
        holder.pb_wait.bringToFront();
        holder.tvTitle.setText(menuItem.getTitle());
        holder.tvDescrition.setText(menuItem.getDescription());
        holder.tvPrice.setText(String.format("RM %.2f", menuItem.getPrice()));

        if (position == 0) {
            String text = "";
            if (menuItem.getItemType() == MenuModel.type.Specail.getValue()) {
                text = "Special Offers";
            } else if (menuItem.getItemType() == MenuModel.type.FirstTime.getValue()) {
                text = "First Timer";
            } else if (menuItem.getItemType() == MenuModel.type.Poplar.getValue()) {
                text = "Popular Dishes";
            }
            holder.llTopHeader.setVisibility(View.VISIBLE);
            holder.tvTopHeader.setText(text);
        } else if (mDataList.get(position - 1).isSpecial() && !menuItem.isSpecial()) {
            String text = "";
            if (menuItem.getItemType() == MenuModel.type.FirstTime.getValue()) {
                text = "First Timer";
            } else if (menuItem.getItemType() == MenuModel.type.Poplar.getValue()) {
                text = "Popular Dishes";
            }
            holder.llTopHeader.setVisibility(View.VISIBLE);
            holder.tvTopHeader.setText(text);
        } else if (mDataList.get(position - 1).isPopular() && !menuItem.isPopular()) {
            holder.llTopHeader.setVisibility(View.VISIBLE);
            holder.tvTopHeader.setText("First Timer");
        } else {
            holder.llTopHeader.setVisibility(View.GONE);
        }

        if (menuItem.isSpecial()) {
            holder.tvDiscount.setVisibility(View.GONE);
            holder.tvDiscount.setText(menuItem.getDiscount());
        } else {
            holder.tvDiscount.setVisibility(View.GONE);
        }
        if (AppController.getCurrentRestaurent().isReadOnly()) {
            holder.btnAdd.setVisibility(View.GONE);
        }

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!AppController.getLoginUser().isGuest() &&
                        AppController.getCurrentRestaurent().isRestaurantActive()
                        && !AppController.isAppDisable()) {
                    if (((TabActivity) mCtx).checkIsTableAcquired()) {
                        OrderPlacement orderPlacement = new OrderPlacement();
                        orderPlacement.setmMenuModel(menuItem);
                        ((TabActivity) mCtx).replaceFragment(orderPlacement, true);
                    } else {
                        ((TabActivity) mCtx).showTableDialog();
                    }
                } else {
                    if (AppController.getLoginUser().isGuest())
                        ((TabActivity) mCtx).showDisableDialog(mCtx, mCtx.getString(R.string.guest_disable));
                    else if (!AppController.getCurrentRestaurent().isRestaurantActive())
                        ((TabActivity) mCtx).showDisableDialog(mCtx, mCtx.getString(R.string.restaurant_disbled_message));
                    else if (AppController.isAppDisable())
                        ((TabActivity) mCtx).showDisableDialog(mCtx, mCtx.getString(R.string.app_disable_message));
                }
                //Toast.makeText(mCtx,"Amount Added" + menuItem.getPrice(),Toast.LENGTH_LONG).show();
            }
        });
        if (!mDataList.get(position).getImageName().trim().isEmpty())
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((SpecialMenu) mFragment).zoomImageFromThumb(view, mDataList.get(position).getImageName());
                }
            });
        Resources r = mCtx.getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, r.getDisplayMetrics());
        if (!mDataList.get(position).getImageName().trim().isEmpty())
            Picasso.with(mCtx)
                    .load(mDataList.get(position).getImageName())
                    .transform(new RoundedTransformation((int) px, 4))
                    .resizeDimen(R.dimen.list_detail_image_size, R.dimen.list_detail_image_size)
                    .centerCrop()
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.pb_wait.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });
        /*else
            holder.pb_wait.setVisibility(View.GONE);*/

        // holder.quickImageView.setImageResource(mDataList.get(position));
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView tvTitle, tvDescrition, tvPrice, tvTopHeader, tvDiscount;
        private Button btnAdd;
        LinearLayout llTopHeader;
        private ProgressBar pb_wait;

        public ItemViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.iv_menuImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title_menu);
            tvDescrition = (TextView) itemView.findViewById(R.id.tv_description);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvTopHeader = (TextView) itemView.findViewById(R.id.tv_topHeader);
            btnAdd = (Button) itemView.findViewById(R.id.btn_add);
            llTopHeader = (LinearLayout) itemView.findViewById(R.id.ll_header);
            tvDiscount = (TextView) itemView.findViewById(R.id.tv_discount);
            pb_wait = (ProgressBar) itemView.findViewById(R.id.pb_menu);
        }
    }
}
