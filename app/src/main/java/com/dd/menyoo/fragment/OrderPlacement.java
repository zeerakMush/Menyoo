package com.dd.menyoo.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.CategoryExtra;
import com.dd.menyoo.model.MenuModel;
import com.dd.menyoo.model.Options;
import com.dd.menyoo.model.OrderModel;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPlacement extends BaseFragment implements View.OnClickListener {


    public OrderPlacement() {
        // Required empty public constructor
    }

    MenuModel mMenuModel;
    TextView mTitle, mPrice, mDescription, mQuantity, mTotalPrice, mViewMore,mViewMoreOptions;
    Button mAddBTn, mSubBtn, mAddToBasket;
    LinearLayout llExtraData;
    Spinner spinnerTop, spinnerBottom;
    EditText mComment;
    int quantity;
    double totalPrize, prize;
    ArrayList<Integer> mVaraitsID;
    ArrayList<Double> mExtraPrice;
    ArrayList<String> mExtraName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_placement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitle = (TextView) view.findViewById(R.id.tv_title_menu);
        mPrice = (TextView) view.findViewById(R.id.tv_price);
        mQuantity = (TextView) view.findViewById(R.id.tv_quantity);
        mDescription = (TextView) view.findViewById(R.id.tv_description);
        mViewMore = (TextView) view.findViewById(R.id.tv_viewmore);
        mTotalPrice = (TextView) view.findViewById(R.id.tv_total);
        mAddBTn = (Button) view.findViewById(R.id.btn_add);
        mSubBtn = (Button) view.findViewById(R.id.btn_sub);
        llExtraData = (LinearLayout) view.findViewById(R.id.ll_extra_data);
        mViewMoreOptions = (TextView) view.findViewById(R.id.tv_viewMore_options);mViewMoreOptions.setOnClickListener(this);
        /*spinnerTop = (Spinner)view.findViewById(R.id.spinner_top);
        spinnerBottom = (Spinner)view.findViewById(R.id.spinner_bottom);*/

        mAddToBasket = (Button) view.findViewById(R.id.btn_addToBasket);
        mComment = (EditText) view.findViewById(R.id.et_comment);
        mComment.addTextChangedListener(commentWatcher);

        ViewTreeObserver vto = mDescription.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (AppHelper.isTextViewEllipsized(mDescription)) {
                    mViewMore.setVisibility(View.VISIBLE);
                }
            }
        });

        setListeners();
        setFeilds();
    }

    private void setFeilds() {
        if (mMenuModel != null) {
            quantity = 1;
            totalPrize = mMenuModel.getPrice();
            prize = totalPrize;
            mTitle.setText(mMenuModel.getTitle());
            mPrice.setText(String.format("RM %.2f", mMenuModel.getPrice()));
            mDescription.setText(mMenuModel.getDescription());
            mQuantity.setText(String.format("Quantity %d", quantity));
            mTotalPrice.setText(String.format("RM %.2f", totalPrize));

            if (mMenuModel.getVariants() != null && mMenuModel.getVariants().size() > 0) {
                setSpinners(mMenuModel.getVariants());
            }

        }
    }

    private int displayedSpinner;

    public void setSpinners(ArrayList<CategoryExtra> extras) {
        displayedSpinner = 0;
        mVaraitsID = new ArrayList<>();
        mExtraPrice = new ArrayList<>();
        mExtraName = new ArrayList<>();

        for (CategoryExtra cExtra : extras) {
            if(displayedSpinner<2){
                addSpinnersData(cExtra,displayedSpinner);
            }
        }
        setViewMoreAddons();
    }

    public void setmMenuModel(MenuModel mMenuModel) {
        this.mMenuModel = mMenuModel;
    }

    private void setListeners() {
        mSubBtn.setOnClickListener(this);
        mAddToBasket.setOnClickListener(this);
        mAddBTn.setOnClickListener(this);
        mViewMore.setOnClickListener(this);
    }
    public void setViewMoreAddons(){
        if(mMenuModel.getVariants().size() - displayedSpinner !=0){
            mViewMoreOptions.setVisibility(View.VISIBLE);
        }else{
            mViewMoreOptions.setVisibility(View.GONE);
        }
    }
    public void addSpinnersData(CategoryExtra extra ,int position){
        addSpinner(extra, position);
        mVaraitsID.add(0);
        mExtraPrice.add(0.0);
        mExtraName.add("");
        displayedSpinner+=1;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                addItem(true);
                break;
            case R.id.btn_sub:
                subItem();
                break;
            case R.id.btn_addToBasket:
                if (mMenuModel.isfirstTimeItem())
                    AppController.setIsFirstTimeOrderAdded(true);

                ((TabActivity) getActivity()).addOrder(orderFactory());
                break;
            case R.id.tv_viewmore:
                mDescription.setMaxLines(Integer.MAX_VALUE);
                mViewMore.setVisibility(View.GONE);
                break;
            case R.id.tv_viewMore_options:
                addSpinnersData(mMenuModel.getVariants().get(displayedSpinner),displayedSpinner);
                setViewMoreAddons();
                break;

        }
    }

    private OrderModel orderFactory() {
        OrderModel orderModel;
        if (mVaraitsID != null && mVaraitsID.size() > 0)
            orderModel = new OrderModel(mMenuModel,
                    mComment.getText().toString(), quantity, mVaraitsID,mExtraPrice,mExtraName);
        else
            orderModel = new OrderModel(mMenuModel,
                    mComment.getText().toString(), quantity);
        return orderModel;
    }

    private void addItem(boolean isToShowToast) {
        if (!mMenuModel.isfirstTimeItem()) {
            quantity += 1;
            totalPrize = prize * quantity;
            mQuantity.setText(String.format("Quantity %d", quantity));
            mTotalPrice.setText(String.format("RM %.2f", totalPrize));
        } else {
            if(isToShowToast)
            Toast.makeText(getActivity(), "Only Single item is allowed", Toast.LENGTH_LONG).show();
        }
    }

    private void subItem() {
        if (quantity > 1) {
            quantity -= 1;
            totalPrize = prize * quantity;
        } else {
            quantity = 1;
            totalPrize = prize;
        }
        mQuantity.setText(String.format("Quantity %d", quantity));
        mTotalPrice.setText(String.format("RM %.2f", totalPrize));

    }

    TextWatcher commentWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (charSequence.toString().length() > 99) {
                Toast.makeText(getActivity(), "Limit Reached", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    ///Spinner Varaint
    public void addSpinner(final CategoryExtra extra, int position) {
        final Spinner spinner = new Spinner(getActivity(), Spinner.MODE_DIALOG);
        spinner.setPrompt(extra.getOptionName());
        spinner.setTag(position);
        LinearLayout.LayoutParams params;
        llExtraData.setVisibility(View.VISIBLE);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppHelper.dpToPx(40, getActivity()));
        llExtraData.addView(spinner, params);
        addSpaceView(false);
        addSpaceView(true);
        String[] optionsArr = new String[extra.getOptions().size()];
        int i = 0;
        for (Options options : extra.getOptions()) {
            optionsArr[i++] = options.getName();
        }
        ArrayAdapter<String> gameKindArray = new ArrayAdapter<>
                (getActivity(), android.R.layout.simple_spinner_item, optionsArr);
        gameKindArray.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        spinner.setAdapter(gameKindArray);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mVaraitsID.size() > 0)
                    mVaraitsID.set((Integer) spinner.getTag(), extra.getOptions().get(i).getID());
                if (mExtraPrice.size() > 0)
                    mExtraPrice.set((Integer) spinner.getTag(), extra.getOptions().get(i).getPrice());
                if (mExtraName.size() > 0)
                    mExtraName.set((Integer) spinner.getTag(), extra.getOptions().get(i).getName());
                updatePrce();
                /*prize = mMenuModel.getPrice();
                prize += extra.getOptions().get(i).getPrice();*/
                /*addItem();subItem();*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void addSpaceView(boolean isToAddMargin) {
        View view = new View(getActivity());
        LinearLayout.LayoutParams params;
        view.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppHelper.dpToPx(1, getActivity()));
        if (isToAddMargin)
            params.topMargin = AppHelper.dpToPx(10, getActivity());
        llExtraData.addView(view, params);
    }

    public void updatePrce() {
        prize = mMenuModel.getPrice();
        for (Double price : mExtraPrice) {
            this.prize += price;
        }
        addItem(false);
        subItem();
    }

}
