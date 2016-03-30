package com.dd.menyoo.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.adapter.MenuTabAdapter2;
import com.dd.menyoo.model.CategoryModel;
import com.dd.menyoo.model.MenuModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MenuTab2 extends BaseFragment {

    RecyclerView rvMenuTab2;
    TextView tvTopHeader;
    ProgressBar pbMenu;
    ArrayList<MenuModel> mMenuItemArray;
    MenuTabAdapter2 mtAdapter;
    CategoryModel categoryModel;


    public MenuTab2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_tab2, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMenuTab2 = (RecyclerView)view.findViewById(R.id.rv_menuTab2);
        tvTopHeader = (TextView)view.findViewById(R.id.tv_topHeader);
        pbMenu = (ProgressBar)view.findViewById(R.id.pb_menu);
        tvTopHeader.setText(categoryModel.getName());
        setMenuTab2Adapter();getCategoriesItems(categoryModel.getCategoryId());
    }

    public void setMenuTab2Adapter(){
        mMenuItemArray = new ArrayList<>();
        mtAdapter = new MenuTabAdapter2(getActivity());
        rvMenuTab2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mtAdapter.setData(mMenuItemArray);
        rvMenuTab2.setAdapter(mtAdapter);
    }

    protected void getCategoriesItems(int categoryId) {
        pbMenu.setVisibility(View.VISIBLE);
        pbMenu.bringToFront();
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo",obj.toString());
                afterGetCategoryItems(obj);
            }
        });
        String url =String.format("GetMenuForCategories?category=%d", categoryId);

        String[] params = {getString(R.string.url_offline)+url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void afterGetCategoryItems(Object obj){
        try {
            pbMenu.setVisibility(View.GONE);
            JSONArray jsonArr = new JSONArray(obj.toString());
            for(int i=0;i<jsonArr.length();i++){
                JSONObject jObj = jsonArr.getJSONObject(i);
                int id = jObj.getInt("MenuId");
                String name = jObj.getString("Name");
                String description = jObj.getString("Description");
                double prize = jObj.getDouble("Price");
                boolean isExtraData=false;
                if(description.length()>140)
                    isExtraData = true;
                mMenuItemArray.add(new MenuModel(name,description,prize,id,isExtraData));
            }
            mtAdapter.setData(mMenuItemArray);
            mtAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCategory(CategoryModel cm) {
        this.categoryModel = cm;
    }
}
