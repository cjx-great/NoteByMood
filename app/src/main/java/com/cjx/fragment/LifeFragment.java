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
import android.widget.Toast;

import com.cjx.R;
import com.cjx.activity.GuideActivity;
import com.cjx.activity.SendSMSActivity;
import com.cjx.adapter.LifeRecyclerAdapter;
import com.cjx.adapter.RecyclerViewDecoration;
import com.cjx.interfaces.OnItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class LifeFragment extends Fragment {

    private RecyclerView recyclerView = null;
    private LifeRecyclerAdapter adapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_life, container, false);
        init(view);
        return view;
    }

    private void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.life_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new RecyclerViewDecoration(getActivity(),
                RecyclerViewDecoration.VERTICAL_LIST));

        adapter = new LifeRecyclerAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view) {
                handle(view);
            }
        });
    }

    /**
     * 处理点击事件
     * */
    private void handle(View view){
        String name = ((TextView) view.findViewById(R.id.life_function)).getText().toString();

        switch (name){
            case "导航":
                startActivity(new Intent(getActivity(), GuideActivity.class));
                break;
            case "短信群发":
                startActivity(new Intent(getActivity(), SendSMSActivity.class));
                break;
        }
    }
}
