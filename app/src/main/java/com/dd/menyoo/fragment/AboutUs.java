package com.dd.menyoo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.dd.menyoo.R;
import com.dd.menyoo.adapter.AboutUsAdapter;
import com.dd.menyoo.adapter.MenuTabAdapter2;
import com.dd.menyoo.model.QuestionModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends BaseFragment {


    public AboutUs() {
        // Required empty public constructor
    }
    RecyclerView rv_questions;
    RelativeLayout ll_main;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rv_questions =(RecyclerView) view.findViewById(R.id.rv_questions);
        ll_main = (RelativeLayout)view.findViewById(R.id.ll_main);
        ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setQuestionAdapter();

    }
    public void setQuestionAdapter(){
        AboutUsAdapter auAdapter = new AboutUsAdapter(getActivity());
        rv_questions.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        auAdapter.setData(setQuestions());
        rv_questions.setAdapter(auAdapter);
    }

    public ArrayList<QuestionModel> setQuestions(){
        ArrayList<QuestionModel> questions = new ArrayList<>();
        for(int i=0;i<5;i++){
            questions.add(new QuestionModel("Question "+i,getString(R.string.dummy_text)));
        }
        return questions;
    }

}
