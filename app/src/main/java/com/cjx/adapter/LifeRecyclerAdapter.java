package com.cjx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.R;
import com.cjx.data.Constant;
import com.cjx.interfaces.OnItemClickListener;

/**
 * Created by CJX on 2016/8/7.
 */
public class LifeRecyclerAdapter extends RecyclerView.Adapter<LifeRecyclerAdapter.LifeViewHolder>{

    private Context context = null;

    private OnItemClickListener onItemClickListener = null;

    public LifeRecyclerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public LifeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.life_recycler_item,parent,false);

        return new LifeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LifeViewHolder holder, int position) {
        holder.textView.setText(Constant.LIFEFUNCTION[position]);
        holder.imageView.setImageResource(Constant.LIFETURN[position]);
    }

    @Override
    public int getItemCount() {
        return Constant.LIFEFUNCTION.length;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    class LifeViewHolder extends RecyclerView.ViewHolder {

        TextView textView = null;
        ImageView imageView = null;

        public LifeViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.life_function);
            imageView = (ImageView) itemView.findViewById(R.id.life_turn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null){
                        onItemClickListener.onClick(view);
                    }
                }
            });
        }
    }
}
