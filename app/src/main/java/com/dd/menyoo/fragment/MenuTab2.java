package com.dd.menyoo.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
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
import com.dd.menyoo.model.CategoryExtra;
import com.dd.menyoo.model.CategoryKeyModel;
import com.dd.menyoo.model.CategoryModel;
import com.dd.menyoo.model.MenuModel;
import com.dd.menyoo.model.Options;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MenuTab2 extends BaseFragment {

    RecyclerView rvMenuTab2;
    TextView tvTopHeader,tvCategoryMessage;
    ProgressBar pbMenu;
    ArrayList<MenuModel> mMenuItemArray;
    MenuTabAdapter2 mtAdapter;
    CategoryModel categoryModel;
    ArrayList<CategoryKeyModel> mKey;
    HashMap<Integer,ArrayList<MenuModel>> mMapMenu;


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
        tvCategoryMessage = (TextView)view.findViewById(R.id.tv_extra_description_category);
        pbMenu = (ProgressBar)view.findViewById(R.id.pb_menu);
        tvTopHeader.setText(categoryModel.getName());
        if(!categoryModel.getCategoryMessage().trim().isEmpty()&&!categoryModel.getCategoryMessage().equals("null")){
            tvCategoryMessage.setVisibility(View.VISIBLE);
            tvCategoryMessage.setText(categoryModel.getCategoryMessage());
        }
        setMenuTab2Adapter();getCategoriesItems(categoryModel.getCategoryId());
    }

    public void setMenuTab2Adapter(){
        mMenuItemArray = new ArrayList<>();
        mtAdapter = new MenuTabAdapter2(getActivity());
        rvMenuTab2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mtAdapter.setData(new HashMap<Integer, ArrayList<MenuModel>>(),new ArrayList<CategoryKeyModel>());
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
                afterGetCategoryItemsNew(obj);
            }
        });
        String url =String.format("GetMenuForCategories?category=%d", categoryId);

        String[] params = {getString(R.string.url_offline)+url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }



    private void afterGetCategoryItemsNew(Object object){
        try {
            pbMenu.setVisibility(View.GONE);
            mKey = new ArrayList<>();
            mMapMenu = new HashMap<>();
            ArrayList<MenuModel> menuArray;
            JSONArray jsonArr = new JSONArray(object.toString());
            JSONArray categoryArr = jsonArr.getJSONArray(0);
            CategoryKeyModel ckm;
            for(int i=0;i<categoryArr.length();i++){
                ckm = new CategoryKeyModel();
                ckm.setCategoryName(categoryArr.getJSONObject(i).getString("Name"));
                ckm.setKey(categoryArr.getJSONObject(i).getInt("MenuId"));
                mKey.add(ckm);
            }

            for(int i=1;i<jsonArr.length();i++){
                JSONArray innerJsonArray = jsonArr.getJSONArray(i);
                menuArray = new ArrayList<>();
                Integer key=0;
                for(int j=0;j<innerJsonArray.length();j++){
                    JSONObject jObj = innerJsonArray.getJSONObject(j);
                    int id = jObj.getInt("MenuId");
                    String name = jObj.getString("Name");
                    String description = jObj.getString("Description");
                    key = jObj.getInt("ParentId");
                    double prize = jObj.getDouble("Price");
                    ArrayList<CategoryExtra> variants= getExtraOptionData(jObj.getJSONArray("Extras"));
                    boolean isExtraData=false;
                    if(description.length()>170)
                        isExtraData = true;
                    MenuModel menuModel =new MenuModel(name,description,prize,id,isExtraData,variants);
                    for(CategoryKeyModel ckm2:mKey){
                        if(ckm2.getKey().equals(key))
                            menuModel.setCategoryName(ckm2.getCategoryName());
                    }
                    menuArray.add(menuModel);
                }
                mMapMenu.put(key,menuArray);
            }
            mtAdapter.setData(mMapMenu,mKey);
            mtAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCategory(CategoryModel cm) {
        this.categoryModel = cm;
    }

    public ArrayList<CategoryExtra> getExtraOptionData(JSONArray extras){
        try {
            ArrayList<CategoryExtra> categoryExtras = new ArrayList<>();
            for(int i=0;i<extras.length();i++){
                String optionName = "";
                ArrayList<Options> options ;
                JSONObject jExtra = extras.getJSONObject(i);
                optionName = jExtra.getString("OptionName");
                options = new ArrayList<>();
                JSONArray jOptionName = jExtra.getJSONArray("Options");
                for(int j=0;j<jOptionName.length();j++){
                    String name = jOptionName.getJSONObject(j).getString("option");
                    double price =jOptionName.getJSONObject(j).getDouble("price");
                    int id = jOptionName.getJSONObject(j).getInt("optionId");
                    options.add(new Options(name,price,id));
                }
                categoryExtras.add(new CategoryExtra(optionName,options));
            }
            return categoryExtras;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    /*private void afterGetCategoryItems(Object obj){
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
            // mtAdapter.setData(mMenuItemArray);
            mtAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/
}
