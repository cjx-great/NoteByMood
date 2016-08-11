package com.cjx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.R;
import com.cjx.bean.StudyItem;
import com.cjx.interfaces.OnItemClickListener;

import java.util.List;

/**
 * Created by CJX on 2016/8/6.
 */
public class StudyRecyclerAdapter extends RecyclerView.Adapter<StudyRecyclerAdapter.StudyViewHolder>{

    private Context context = null;
    private List<StudyItem> studyItemList = null;

    private OnItemClickListener onItemClickListener = null;

    public StudyRecyclerAdapter(Context context, List<StudyItem> studyItemList) {
        this.context = context;
        this.studyItemList = studyItemList;
    }

    @Override
    public StudyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.study_recycler_item,parent,false);

        return new StudyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StudyViewHolder holder, int position) {
        holder.icon.setImageResource(studyItemList.get(position).getIcon());
        holder.function.setText(studyItemList.get(position).getFunction());
        holder.whetherTurn.setImageResource(studyItemList.get(position).getWhetherTurn());
    }

    @Override
    public int getItemCount() {
        return studyItemList == null ? 0 : studyItemList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class StudyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView icon = null;
        TextView function = null;
        ImageView whetherTurn = null;

        public StudyViewHolder(View itemView) {
            super(itemView);

            icon = (ImageView) itemView.findViewById(R.id.icon);
            function = (TextView) itemView.findViewById(R.id.function);
            whetherTurn = (ImageView) itemView.findViewById(R.id.whether_turn);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null){
                onItemClickListener.onClick(view);
            }
        }
    }
}
