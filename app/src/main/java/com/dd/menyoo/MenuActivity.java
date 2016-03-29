package com.dd.menyoo;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.menyoo.adapter.RestaurantAdapter;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.common.SharedPrefManager;
import com.dd.menyoo.fragment.AboutUs;
import com.dd.menyoo.fragment.BaseFragment;
import com.dd.menyoo.fragment.OrderHistory;
import com.dd.menyoo.fragment.Profile;
import com.dd.menyoo.model.RestaurantModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;
import com.facebook.login.LoginManager;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends BaseClass
        implements View.OnClickListener {


    private ImageButton mIBtnSearch, mIBtnHome, mBtnDrawerToggle, mCollapseBanner;
    private DrawerLayout mDrawer;
    private EditText mEtSearch;
    private RecyclerView mRvRestraunents;
    private TextView tvUserName;
    private ProgressBar pbWait;
    private LinearLayout llHome, llProfile, llAboutUs, llOrderHistory;
    private RelativeLayout rlPullInstruction, rlBanner;
    ArrayList<RestaurantModel> mRestaurantArr;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        initViews();
        setListeners();
        //addFooter();
    }

    public void initViews() {
        mBtnDrawerToggle = (ImageButton) findViewById(R.id.drawer_toggle);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mIBtnSearch = (ImageButton) findViewById(R.id.btn_search);
        mIBtnHome = (ImageButton) findViewById(R.id.btn_home);
        mCollapseBanner = (ImageButton) findViewById(R.id.btn_collapse_banner);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mRvRestraunents = (RecyclerView) findViewById(R.id.rv_restaurant);
        tvUserName = (TextView) findViewById(R.id.tv_username);
        pbWait = (ProgressBar) findViewById(R.id.pb_wait);
        llHome = (LinearLayout) findViewById(R.id.llHome);
        llProfile = (LinearLayout) findViewById(R.id.llProfile);
        llAboutUs = (LinearLayout) findViewById(R.id.llAboutUs);
        llOrderHistory = (LinearLayout) findViewById(R.id.llOrderHistory);
        rlPullInstruction = (RelativeLayout) findViewById(R.id.rl_pull_instruction);
        rlBanner = (RelativeLayout) findViewById(R.id.rl_banner);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);


        AppController.setIsTableOccupiedInAnyRes(false);
        mRestaurantArr = new ArrayList<>();
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                rcAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRestraunents();
            }
        });
    }

    RestaurantAdapter rcAdapter;

    public void setListeners() {
        mBtnDrawerToggle.setOnClickListener(this);
        mIBtnSearch.setOnClickListener(this);
        mIBtnHome.setOnClickListener(this);
        rcAdapter = new RestaurantAdapter(this);
        mCollapseBanner.setOnClickListener(this);
        mRvRestraunents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rcAdapter.setData(mRestaurantArr);
        mRvRestraunents.setAdapter(rcAdapter);
        if (AppController.getLoginUser() != null)
            tvUserName.setText(String.format("Hi, %s", AppController.getLoginUser().getFirstName()));
        getRestraunents();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.drawer_toggle:
                mDrawer.openDrawer(GravityCompat.START);
                break;
            case R.id.btn_search:
                // mEtSearch.setVisibility(mEtSearch.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE);
                if (mEtSearch.getVisibility() == View.VISIBLE) {
                    AppHelper.collapse(mEtSearch);
                } else {
                    AppHelper.expand(mEtSearch);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                break;
            case R.id.btn_home:
                finishFragment();
                break;
            case R.id.btn_collapse_banner:
                collapseBanner();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (fragment == null)
                super.onBackPressed();
            else {
                finishFragment();
                resetSlider();
                llHome.setBackgroundResource(R.color.bottom_menu_color);
            }
        }
    }

    public void toTabActivity(RestaurantModel restaurantModel) {
        AppController.setCurrentRestaurent(restaurantModel);
        /*dataPro.UserGetWaiterGroup(""+restaurantModel.getRestaurantID());
        dataPro.UserGetBillGroup(""+restaurantModel.getRestaurantID());*/

        Intent intent = new Intent(this, TabActivity.class);
        startActivity(intent);
    }

    protected void getRestraunents() {
        pbWait.setVisibility(View.VISIBLE);
        pbWait.bringToFront();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(this,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                Log.e("Menyoo", "Some Error");
                AppHelper.showConnectionAlert(MenuActivity.this);
                swipeContainer.setRefreshing(false);
                rlPullInstruction.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        rlPullInstruction.setVisibility(View.GONE);
                    }
                }, 4000);
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                afterGetRestaurants(obj);
            }
        });
        String url = String.format("GetAllRestaurant?Userid=%d", AppController.getLoginUser().getUserId());
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterGetRestaurants(Object obj) {
        try {
            mRestaurantArr = new ArrayList<>();
            mRestaurantArr.add(new RestaurantModel(0, "Solaris Dutamas, Kuala Lumpur", ""));
            JSONObject jsonObject = new JSONObject(obj.toString());
            JSONArray jsonArr = new JSONArray();
            if (jsonObject.has("Status")) {
                signOut(null);
                Toast.makeText(this, "You have been blocked", Toast.LENGTH_LONG).show();
            } else {
                jsonArr = jsonObject.getJSONArray("RestaurantData");
            }

            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jObj = jsonArr.getJSONObject(i);

                int id = jObj.getInt("RestaurantId");
                String name = jObj.getString("RestaurantName");
                String imageImage = jObj.getString("FileName");
                boolean isTableOccupied = jObj.getBoolean("IsTableOccupied");
                boolean isResActive = jObj.getBoolean("Active");
                boolean isComingSoon = jObj.getBoolean("IsCommingSoon");
                boolean isReadOnly = jObj.getBoolean("IsReadOnly");
                String feedBackEmail = jObj.getString("FeedBackEmail");
                if (!isComingSoon)
                    mRestaurantArr.add(new RestaurantModel(id, name, imageImage, isTableOccupied, isResActive, feedBackEmail, isReadOnly));
                else {
                    String comingSoonMessage = jObj.getString("IsCommingSoonMessage");
                    mRestaurantArr.add
                            (new RestaurantModel(id, name, imageImage, isTableOccupied, isResActive, isComingSoon, comingSoonMessage));
                }
                if (isTableOccupied)
                    AppController.setIsTableOccupiedInAnyRes(true);


            }
            swipeContainer.setRefreshing(false);
            pbWait.setVisibility(View.GONE);
            rcAdapter.setData(mRestaurantArr);
            rcAdapter.notifyDataSetChanged();
            checkRestaurantState();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /***
     * Navigation Slider Methods
     **/

    public void signOut(View view) {
        SharedPrefManager sharedPrefManager = new SharedPrefManager(this);
        sharedPrefManager.SetUserIsLoggedIn(null);
        LoginManager.getInstance().logOut();
        Intent intentAct = new Intent(MenuActivity.this,
                LoginActivity.class);
        intentAct.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentAct);
        finish();
    }

    public void changeFragment(BaseFragment fragmentInner) {
        fl_fragment = (FrameLayout) findViewById(R.id.fl_fragment);
        fl_fragment.setVisibility(View.VISIBLE);
        fl_fragment.bringToFront();
        fragment = fragmentInner;
        replaceFragemtn(fragmentInner, false);

    }

    BaseFragment fragment;
    FrameLayout fl_fragment;

    public void replaceFragemtn(Fragment fragment, boolean isToAddBackStack) {

        mIBtnSearch.setVisibility(View.GONE);
        //mIBtnHome.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_fragment, fragment, "KK");
        if (isToAddBackStack)
            fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void finishFragment() {
        mIBtnSearch.setVisibility(View.VISIBLE);
        mIBtnHome.setVisibility(View.GONE);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.remove(fragment);
        fl_fragment.setVisibility(View.GONE);
        fragmentTransaction.commit();
    }

    public void toProfile(View view) {
        if (!AppController.getLoginUser().isGuest()) {
            resetSlider();
            llProfile.setBackgroundResource(R.color.bottom_menu_color);
            changeFragment(new Profile());
        } else
            showDisableDialog(this, this.getString(R.string.guest_disable));

    }

    public void toAboutUs(View view) {
        resetSlider();
        llAboutUs.setBackgroundResource(R.color.bottom_menu_color);
        changeFragment(new AboutUs());
    }

    public void toOrderHistory(View view) {
        if (!AppController.getLoginUser().isGuest()) {
            resetSlider();
            llOrderHistory.setBackgroundResource(R.color.bottom_menu_color);
            changeFragment(new OrderHistory());
        } else
            showDisableDialog(this, this.getString(R.string.guest_disable));
    }

    public void toHome(View view) {
        resetSlider();
        llHome.setBackgroundResource(R.color.bottom_menu_color);
        if (fl_fragment != null)
            finishFragment();
    }

    public void resetSlider() {
        llHome.setBackgroundResource(R.color.background_color);
        llProfile.setBackgroundResource(R.color.background_color);
        llAboutUs.setBackgroundResource(R.color.background_color);
        llOrderHistory.setBackgroundResource(R.color.background_color);
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
    }

    public void collapseBanner() {
        AppHelper.collapse(rlBanner);
    }

    protected void checkRestaurantState() {
        AppHelper.getInstance().showProgressDialog("Checking ", this);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(this,
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.getInstance().hideProgressDialog();
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                AppHelper.getInstance().hideProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(obj.toString());
                    boolean isAppLock = jsonObject.getBoolean("AppLock");
                    AppController.setIsAppDisable(isAppLock);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("Menyoo", obj.toString());
            }
        });
        String url = "IsAppLocked";
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

}
