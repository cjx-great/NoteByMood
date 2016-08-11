package com.cjx.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cjx.R;
import com.cjx.activity.QueryRunningActivity;
import com.cjx.adapter.RecyclerViewDecoration;
import com.cjx.adapter.StudyRecyclerAdapter;
import com.cjx.bean.StudyItem;
import com.cjx.data.Constant;
import com.cjx.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class StudyFragment extends Fragment {

    private RecyclerView recyclerView = null;
    private StudyRecyclerAdapter adapter = null;

    private List<StudyItem> studyItemList = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_study, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.study_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new RecyclerViewDecoration(getActivity(),
                RecyclerViewDecoration.VERTICAL_LIST));

        studyItemList = new ArrayList<>();
        for (int i = 0; i < Constant.FUNCTION.length; i++) {
            StudyItem item = new StudyItem();
            item.setIcon(Constant.ICON[i]);
            item.setFunction(Constant.FUNCTION[i]);
            item.setWhetherTurn(Constant.WHETHERTURN[i]);
            studyItemList.add(item);
        }

        adapter = new StudyRecyclerAdapter(getActivity(),studyItemList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view) {
                String function = ((TextView) view.findViewById(R.id.function)).getText().toString();
                switch (function){
                    case "南湖跑查询":
                        startActivity(new Intent(getActivity(), QueryRunningActivity.class));
                        break;
                }
            }
        });
    }
}
