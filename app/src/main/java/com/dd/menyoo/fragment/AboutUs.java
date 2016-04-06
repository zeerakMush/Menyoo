package com.dd.menyoo.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.menyoo.R;
import com.dd.menyoo.adapter.AboutUsAdapter;
import com.dd.menyoo.adapter.MenuTabAdapter2;
import com.dd.menyoo.common.AppHelper;
import com.dd.menyoo.model.QuestionModel;
import com.dd.menyoo.network.NetworkManagerOld;
import com.dd.menyoo.network.TaskCompleted;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutUs extends BaseFragment {


    public AboutUs() {
        // Required empty public constructor
    }
    RecyclerView rv_questions;
    RelativeLayout ll_main;
    ArrayList<QuestionModel> questionModels;
    AboutUsAdapter auAdapter;
    TextView tvAboutUs;

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
        tvAboutUs = (TextView)view.findViewById(R.id.tv_about_us);
        ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        setQuestionAdapter();
        getQuestion();
        AppHelper.getInstance().showProgressDialog("Please Wait",getActivity());
    }
    public void setQuestionAdapter(){

        auAdapter = new AboutUsAdapter(getActivity());
        rv_questions.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        auAdapter.setData(setQuestions());
        rv_questions.setAdapter(auAdapter);
    }

    public ArrayList<QuestionModel> setQuestions(){
        ArrayList<QuestionModel> questions = new ArrayList<>();
       /* for(int i=0;i<5;i++){
            questions.add(new QuestionModel("Question "+i,getString(R.string.dummy_text)));
        }*/
        return questions;
    }

    protected void getQuestion() {
        List<NameValuePair> nameValuePairs = new ArrayList<>(
                2);
        NetworkManagerOld httpMan = new NetworkManagerOld(getActivity(),
                NetworkManagerOld.EnCallType.POSTFORALL, NetworkManagerOld.ReturnTypeForReponse.String,
                nameValuePairs, new TaskCompleted() {

            @Override
            public void onTaskFailed() {
                // TODO Auto-generated method stub
                AppHelper.getInstance().hideProgressDialog();
                Log.e("Menyoo", "Some Error");
                AppHelper.showConnectionAlert(getActivity());

                // / afterCatlogUpdate(null);
            }

            @Override
            public void onTaskCompletedSuccessfully(Object obj) {
                // TODO Auto-generated method stub
                Log.e("Menyoo", obj.toString());
                afterGetQuestion(obj);
                AppHelper.getInstance().hideProgressDialog();
            }
        });
        String url = "AnswerAndQuestonForApp";
        String[] params = {getString(R.string.url_offline) + url};
        httpMan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    public void afterGetQuestion(Object obj){
//[{"AboutUs":"About us title","Answer":"this is the long answer."},
// [{"Question":"who is who","Answer":"i dont know."},
// {"Question":"what is what","Answer":"i still don\u0027t know."}]]
        try {
            JSONArray jArray = new JSONArray(obj.toString());
            String aboutUsDescription = jArray.getJSONObject(0).getString("Answer");
            questionModels = new ArrayList<>();
            for(int i=1;i<jArray.length();i++){
                String question = jArray.getJSONObject(i).getString("Question");
                String answer = jArray.getJSONObject(i).getString("Answer");
                questionModels.add(new QuestionModel(question,answer));
            }
            auAdapter.setData(questionModels);
            auAdapter.notifyDataSetChanged();
            tvAboutUs.setText(aboutUsDescription);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
