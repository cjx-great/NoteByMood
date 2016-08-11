package com.cjx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cjx.R;
import com.cjx.bean.TrainNote;
import com.cjx.interfaces.OnItemClickListener;
import com.cjx.interfaces.OnItemLongClickListener;

import java.util.List;

/**
 * Created by CJX on 2016/8/6.
 */
public class TrainRecyclerAdapter extends RecyclerView.Adapter<TrainRecyclerAdapter.TrainViewHolder> {

    private Context context = null;
    private LayoutInflater inflater = null;
    private List<TrainNote> noteList = null;

    private OnItemClickListener onItemClickListener = null;
    private OnItemLongClickListener onItemLongClickListener = null;

    public TrainRecyclerAdapter(Context context, List<TrainNote> noteList) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.noteList = noteList;
    }

    @Override
    public TrainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.train_recycler_item,parent,false);

        return new TrainViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TrainViewHolder holder, int position) {
        holder.noteText.setText(noteList.get(position).getNoteText());
        holder.noteTime.setText(noteList.get(position).getNoteDate());
    }

    @Override
    public int getItemCount() {
        return noteList == null ? 0 : noteList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    class TrainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener{

        TextView noteText = null;
        TextView noteTime = null;

        public TrainViewHolder(View itemView) {
            super(itemView);

            noteText = (TextView) itemView.findViewById(R.id.train_note_text);
            noteTime = (TextView) itemView.findViewById(R.id.train_note_time);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null){
                onItemClickListener.onClick(view);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (onItemLongClickListener != null){
                onItemLongClickListener.onLongClick(view);
            }
            return true;
        }
    }
}
