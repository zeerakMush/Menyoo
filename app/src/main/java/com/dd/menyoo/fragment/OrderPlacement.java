package com.dd.menyoo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.MenuModel;
import com.dd.menyoo.model.OrderModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderPlacement extends BaseFragment implements View.OnClickListener {


    public OrderPlacement() {
        // Required empty public constructor
    }
    MenuModel mMenuModel;
    TextView mTitle,mPrice,mDescription,mQuantity,mTotalPrice,mViewMore;
    Button mAddBTn,mSubBtn,mAddToBasket;
    EditText mComment ;
    int quantity;
    double totalPrize,prize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_placement, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTitle = (TextView)view.findViewById(R.id.tv_title_menu);
        mPrice = (TextView)view.findViewById(R.id.tv_price);
        mQuantity = (TextView)view.findViewById(R.id.tv_quantity);
        mDescription = (TextView)view.findViewById(R.id.tv_description);
        mViewMore = (TextView)view.findViewById(R.id.tv_viewmore);
        mTotalPrice = (TextView)view.findViewById(R.id.tv_total);
        mAddBTn = (Button)view.findViewById(R.id.btn_add);
        mSubBtn = (Button)view.findViewById(R.id.btn_sub);
        mAddToBasket = (Button) view.findViewById(R.id.btn_addToBasket);
        mComment = (EditText)view.findViewById(R.id.et_comment);
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
        setListeners();setFeilds();
    }

    private void setFeilds(){
        if(mMenuModel!=null){
            quantity = 1;
            totalPrize = mMenuModel.getPrice();
            prize = totalPrize;
            mTitle.setText(mMenuModel.getTitle());
            mPrice.setText(String.format("RM %.2f",mMenuModel.getPrice()));
            mDescription.setText(mMenuModel.getDescription());
            mQuantity.setText(String.format("Quantity %d",quantity));
            mTotalPrice.setText(String.format("RM %.2f",totalPrize));

        }
    }

    public void setmMenuModel(MenuModel mMenuModel) {
        this.mMenuModel = mMenuModel;
    }

    private void setListeners(){
        mSubBtn.setOnClickListener(this);
        mAddToBasket.setOnClickListener(this);
        mAddBTn.setOnClickListener(this);
        mViewMore.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add:
                addItem();
                break;
            case R.id.btn_sub:
                subItem();
                break;
            case R.id.btn_addToBasket:
                if(mMenuModel.isfirstTimeItem())
                    AppController.setIsFirstTimeOrderAdded(true);

                ((TabActivity)getActivity()).addOrder(orderFactory());
                break;
            case R.id.tv_viewmore:
                mDescription.setMaxLines(Integer.MAX_VALUE);
                mViewMore.setVisibility(View.GONE);
                break;

        }
    }

    private OrderModel orderFactory() {
        return new OrderModel(mMenuModel,
                mComment.getText().toString(),quantity);
    }

    private void addItem(){
        if(!mMenuModel.isfirstTimeItem()){
            quantity+=1;
            totalPrize=prize*quantity;
            mQuantity.setText(String.format("Quantity %d",quantity));
            mTotalPrice.setText(String.format("RM %.2f",totalPrize));
        }else{
            Toast.makeText(getActivity(),"Only Single item is allowed",Toast.LENGTH_LONG).show();
        }
    }

    private void subItem(){
        if(quantity>1){
            quantity-=1;
            totalPrize=prize*quantity;
        }else{
            quantity=1;
            totalPrize=prize;
        }
        mQuantity.setText(String.format("Quantity %d",quantity));
        mTotalPrice.setText(String.format("RM %.2f",totalPrize));

    }

    TextWatcher commentWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(charSequence.toString().length()>99){
                Toast.makeText(getActivity(),"Limit Reached",Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
