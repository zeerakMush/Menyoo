package com.dd.menyoo.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.TabActivity;
import com.dd.menyoo.adapter.FullMenuAdapter;
import com.dd.menyoo.adapter.MenuTabAdapter2;
import com.dd.menyoo.adapter.SectionedGridRecyclerViewAdapter;
import com.dd.menyoo.adapter.SpecialTabAdapter;
import com.dd.menyoo.common.AppController;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.CategoryModel;
import com.dd.menyoo.model.MenuModel;
import com.dd.menyoo.model.SectionModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuTab1 extends BaseFragment {


    public MenuTab1() {
        // Required empty public constructor
    }

    RecyclerView rvMenuTab1, rvMenuTab2;
    TextView tvTopHeader;
    ArrayList<CategoryModel> mCategoryArr;
    ProgressBar pbMenu;
    ArrayList<MenuModel> mMenuItemArray;
    ArrayList<SectionModel> mSectionArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_tab1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvMenuTab1 = (RecyclerView) view.findViewById(R.id.rv_menuTab1);
        rvMenuTab2 = (RecyclerView) view.findViewById(R.id.rv_menuTab2);
        tvTopHeader = (TextView) view.findViewById(R.id.tv_topHeader);
        pbMenu = (ProgressBar) view.findViewById(R.id.pb_menu);
        mSectionArray = new ArrayList<>();
        setAdapter();
        getCategories();

    }

    FullMenuAdapter fmAdapter;
    MenuTabAdapter2 mtAdapter;
    SectionedGridRecyclerViewAdapter mSectionedAdapter;

    public void setAdapter() {
        mCategoryArr = new ArrayList<>();
        fmAdapter = new FullMenuAdapter(getActivity());
        rvMenuTab1.setHasFixedSize(true);
        rvMenuTab1.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        fmAdapter.setData(mCategoryArr);
        fmAdapter.setmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryModel categoryModel = (CategoryModel) view.getTag();
                MenuTab2 menuTab2 = new MenuTab2();
                menuTab2.setCategory(categoryModel);
                ((TabActivity) getActivity()).replaceFragment(menuTab2, true);
            }
        });

        rvMenuTab1.setAdapter(fmAdapter);


    }

    protected void getCategories() {
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
                AppHelper.showConnectionAlert(getActivity());
                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                afterGetCategories(obj);
            }
        });
        String url = String.format("GetRestaurantCategories?restaurantid=%d",
                AppController.getCurrentRestaurent().getRestaurantID());

        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }



    private void afterGetCategories(Object obj) {
        try {
            pbMenu.setVisibility(View.GONE);
            JSONArray jsonArr = new JSONArray(obj.toString());
            for (int i = 0; i < jsonArr.length(); i++) {
                JSONObject jObj = jsonArr.getJSONObject(i);
                int id = jObj.getInt("CategoryId");
                String name = jObj.getString("Name");
                String imageNAme = jObj.getString("DisplayPicture");
                String parentID = jObj.getString("ParentId");
                String categoryMessage = jObj.getString("CategoryMessage");
                if (parentID != null && !parentID.equals("null")) {
                    mCategoryArr.add(new CategoryModel(id, name, imageNAme, parentID,categoryMessage));
                } else {
                    SectionModel sectionModel = new SectionModel();
                    sectionModel.setCategoryID(id);
                    sectionModel.setSectionName(name);
                    mSectionArray.add(sectionModel);
                }
            }
            setSections();
            rvMenuTab1.setAdapter(mSectionedAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setSections() {
        updateSectionItemsCount();
        List<SectionedGridRecyclerViewAdapter.Section> sections = new ArrayList<>();
        int nextPosition = 0;
        for (SectionModel section : mSectionArray) {
            sections.add(new SectionedGridRecyclerViewAdapter.Section(nextPosition, section.getSectionName()));
            nextPosition += section.getItemCounts();
        }
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
        mSectionedAdapter = new SectionedGridRecyclerViewAdapter(getActivity(), R.layout.section, R.id.section_text, rvMenuTab1, fmAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));
    }

    public void updateSectionItemsCount() {
        for (int index = 0; index < mSectionArray.size(); index++) {
            for (int item = 0; item < mCategoryArr.size(); item++) {
                mSectionArray.get(index).updateItemCount(Integer.parseInt(mCategoryArr.get(item).getParentID()));
            }
        }
    }
}
