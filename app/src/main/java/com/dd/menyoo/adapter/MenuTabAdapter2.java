package com.dd.menyoo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.fragment.OrderPlacement;
import com.dd.menyoo.model.CategoryKeyModel;
import com.dd.menyoo.model.MenuModel;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 19-Feb-16.
 */
public class MenuTabAdapter2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MenuModel> mDataList;
    private HashMap<Integer,ArrayList<MenuModel>> mMapArray;
    private ArrayList<CategoryKeyModel> ckmArray;

    private int mRowIndex = -1;
    Context mCtx;
    private int[] mColors = new int[]{R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3};


    public MenuTabAdapter2(Context ctx) {
        this.mCtx = ctx;
    }

    public void setData(HashMap<Integer,ArrayList<MenuModel>> mapArray,ArrayList<CategoryKeyModel> ckmArr) {
        mDataList = new ArrayList<>();
        if (mapArray != mMapArray) {
            this.mMapArray = mapArray;
            for(CategoryKeyModel ckm:ckmArr){
                if(mMapArray.containsKey(ckm.getKey()))
                     mDataList.addAll(mMapArray.get(ckm.getKey()));
            }
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        //mDataList = new ArrayList<>();
        View itemView = LayoutInflater.from(context).inflate(R.layout.menu_tab_2_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, final int position) {
        final ItemViewHolder holder = (ItemViewHolder) rawHolder;
        final MenuModel menuItem = mDataList.get(position);

        holder.tvSectionName.setVisibility(View.GONE);
        holder.spaceView.setVisibility(View.GONE);
        if(position==0&&!menuItem.getCategoryName().equalsIgnoreCase("General")){
            holder.tvSectionName.setVisibility(View.VISIBLE);
            holder.tvSectionName.setText(menuItem.getCategoryName());
        }else if(position!=0&&!mDataList.get(position-1).getCategoryName().equals(menuItem.getCategoryName())){
            holder.tvSectionName.setVisibility(View.VISIBLE);
            holder.tvSectionName.setText(menuItem.getCategoryName());
            holder.spaceView.setVisibility(View.VISIBLE);
        }

        holder.postiionNumber = position;
        holder.tvTitle.setText(menuItem.getTitle());
        holder.tvDescrition.setText(menuItem.getDescription());
        holder.tvPrice.setText(String.format("RM %.2f", menuItem.getPrice()));
        if (AppController.getCurrentRestaurent().isReadOnly()) {
            holder.btnAdd.setVisibility(View.GONE);
        }
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(mCtx,"Amount Added" + menuItem.getPrice(),Toast.LENGTH_LONG).show();
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

            }
        });
        holder.tvViewMore.setVisibility(View.GONE);
        holder.tvDescrition.setMaxLines(3);
        ViewTreeObserver vto = holder.tvDescrition.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (AppHelper.isTextViewEllipsized(holder.tvDescrition)) {
                    holder.tvViewMore.setVisibility(View.VISIBLE);
                    mDataList.get(holder.postiionNumber).setExtraData(true);
                }
            }
        });
        if (mDataList.get(position).isExtraData()) {
            holder.tvViewMore.setVisibility(View.VISIBLE);
        } else
            holder.tvViewMore.setVisibility(View.GONE);
        holder.tvViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
                holder.tvDescrition.setMaxLines(Integer.MAX_VALUE);
            }
        });
        if (AppHelper.isTextViewEllipsized(holder.tvDescrition)) {
            holder.tvViewMore.setVisibility(View.VISIBLE);
            mDataList.get(position).setExtraData(true);
        }

    }


    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {


        private TextView tvTitle, tvDescrition, tvPrice, tvViewMore,tvSectionName;
        private View spaceView;
        private Button btnAdd;
        private int postiionNumber;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title_menu);
            tvDescrition = (TextView) itemView.findViewById(R.id.tv_description);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            btnAdd = (Button) itemView.findViewById(R.id.btn_add);
            tvViewMore = (TextView) itemView.findViewById(R.id.tv_viewmore);
            tvSectionName = (TextView) itemView.findViewById(R.id.section_text);
            spaceView = itemView.findViewById(R.id.space);

        }
    }

}
