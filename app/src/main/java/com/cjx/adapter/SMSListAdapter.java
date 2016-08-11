package com.cjx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cjx.R;
import com.cjx.bean.ContactItem;

import java.util.List;

/**
 * Created by CJX on 2016-8-10.
 */
public class SMSListAdapter extends BaseAdapter {

    private Context context;
    private List<ContactItem> lists;

    public SMSListAdapter(Context context, List<ContactItem> lists) {
        this.context = context;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public Object getItem(int i) {
        return lists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.send_sms_list_item,null);
            holder = new ViewHolder();
            holder.name = (TextView) view.findViewById(R.id.send_name);

            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(lists.get(position).getName());

        return view;
    }

    class ViewHolder{
        TextView name;
    }
}
