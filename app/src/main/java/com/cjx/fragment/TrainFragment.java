package com.cjx.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.cjx.R;
import com.cjx.adapter.TrainRecyclerAdapter;
import com.cjx.bean.TrainNote;
import com.cjx.helper.TrainDBHelper;
import com.cjx.helper.TrainDBOp;
import com.cjx.interfaces.OnItemClickListener;
import com.cjx.interfaces.OnItemLongClickListener;
import com.cjx.ui.TrainAddOrUpdateDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainFragment extends Fragment {

    private RecyclerView recyclerView = null;
    private FloatingActionButton add = null;

    private LinearLayoutManager linearLayoutManager = null;

    private TrainRecyclerAdapter adapter = null;

    private static List<Map<String,String>> list = new ArrayList<>();
    private List<TrainNote> trainNoteList = new ArrayList<>();

    private TrainDBHelper dbHelper = null;
    //数据库读操作
    private TrainDBOp readOperator = null;
    //数据库写操作
    private TrainDBOp writeOperator = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_train, container, false);

        initDB();
        getData();
        init(view);
        return view;
    }

    /**
     * 初始化数据库
     * */
    private void initDB(){
        dbHelper = new TrainDBHelper(getActivity());
    }

    private void init(View view){
        recyclerView = (RecyclerView) view.findViewById(R.id.train_recycler);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        //不设置分割线
//        recyclerView.addItemDecoration(new RecyclerViewDecoration(getActivity(),
//                RecyclerViewDecoration.VERTICAL_LIST));

        adapter = new TrainRecyclerAdapter(getActivity(),trainNoteList);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(View view) {
                update(view);
            }
        });
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                delete(view);
                return true;
            }
        });
        recyclerView.setAdapter(adapter);


        add = (FloatingActionButton) view.findViewById(R.id.train_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add();
            }
        });

    }

    /**
     * 从数据库读取数据
     * */
    private void getData(){
        list.clear();
        trainNoteList.clear();

        readOperator = new TrainDBOp(dbHelper.getReadableDatabase());
        list = readOperator.set();
        for (int i = 0; i < list.size(); i++) {
            Map<String,String> map = list.get(i);
            TrainNote note = new TrainNote();
            note.setNoteText(map.get("note"));
            note.setNoteDate(map.get("time"));
            trainNoteList.add(note);
        }

    }

    private void update(View view){
        TrainAddOrUpdateDialog dialog = new TrainAddOrUpdateDialog();
        final String old = ((TextView)view.findViewById(R.id.train_note_text)).getText().toString();
        dialog.setOnUpdateDialog(new TrainAddOrUpdateDialog.OnUpdateDialog() {
            @Override
            public void onSuccess(String note, String time) {
                writeOperator = new TrainDBOp(dbHelper.getWritableDatabase());
                writeOperator.update(old,note,time);

                getData();
                adapter.notifyDataSetChanged();
            }
        });
        dialog.setHint(old);
        dialog.setFlag(2);
        dialog.show(getActivity().getFragmentManager(),"update");
    }

    private void add(){
        TrainAddOrUpdateDialog dialog = new TrainAddOrUpdateDialog();
        dialog.setOnAddDialog(new TrainAddOrUpdateDialog.OnAddDialog() {
            @Override
            public void onSuccess(String note, String time) {
                writeOperator = new TrainDBOp(dbHelper.getWritableDatabase());
                writeOperator.insert(note,time);

                getData();
                adapter.notifyDataSetChanged();
            }
        });
        dialog.setFlag(1);
        dialog.show(getActivity().getFragmentManager(),"add");
    }

    private void delete(final View view){
        new AlertDialogWrapper.Builder(getActivity())
                .setTitle("它承载了你的记忆")
                .setMessage("确定删除吗?")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        writeOperator = new TrainDBOp(dbHelper.getWritableDatabase());
                        writeOperator.delete(((TextView)view.findViewById(R.id.train_note_text)).getText().toString());

                        getData();
                        adapter.notifyDataSetChanged();
                    }
                }).show();
    }

}
