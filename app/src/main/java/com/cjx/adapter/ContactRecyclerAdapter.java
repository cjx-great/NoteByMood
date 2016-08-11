package com.cjx.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cjx.R;
import com.cjx.bean.ContactItem;
import com.cjx.helper.SpellComparator;
import com.cjx.helper.SpellUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by CJX on 2016/8/9.
 */
public class ContactRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<ContactItem> contactList;

    // datas转换成拼音
    private List<String> datasToSpellList;
    // 分组字母List
    private List<String> letterList;
    // 最终结果(包含分组的字母)
    private List<ContactItem> resultList;

    //记录已经选中的CheckBox
    private List<Integer> checkedBox = new ArrayList<>();

    public ContactRecyclerAdapter(Context context,List<ContactItem> contactList) {
        this.context = context;
        this.contactList = contactList;

        handleDatas();
    }

    /**
     * 对数据进行排序和分类
     * */
    private void handleDatas(){
        datasToSpellList = new ArrayList<>();

        Map<String, ContactItem> map = new HashMap<>();

        for (int i = 0; i < contactList.size(); i++) {
            //转换为拼音
            String spell = SpellUtils.convertToSpell(contactList.get(i).getName());
            // key是转换后的值，value是转换之前的值
            // 列表中最终显示的是中文，而不是拼音，拼音只是用来排序和分组用
            map.put(spell,contactList.get(i));
            datasToSpellList.add(spell);
        }

        //排序
        Collections.sort(datasToSpellList, new SpellComparator());

        letterList = new ArrayList<>();
        resultList = new ArrayList<>();

        for (int i = 0; i < datasToSpellList.size(); i++) {
            String spell = datasToSpellList.get(i);
            //大写首字母
            String spellStart = (spell.charAt(0) + "").toUpperCase(Locale.ENGLISH);
            //将数据首字母添加至list
            if (!letterList.contains(spellStart)){
                // 是字母
                if (spellStart.hashCode() >= "A".hashCode() && spellStart.hashCode() <= "Z".hashCode()) {
                    letterList.add(spellStart);
                    resultList.add(new ContactItem(spellStart, 1));
                } else {
                    if (!letterList.contains("#")) {
                        letterList.add("#");
                        resultList.add(new ContactItem("#", 1));
                    }
                }
            }

            resultList.add(new ContactItem(map.get(spell).getName(), map.get(spell).getPhoneNumber(),2));

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1 ){     //divider
            view = LayoutInflater.from(context).inflate(R.layout.contact_divider,parent,false);
            return new DividerViewHolder(view);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.contact_item,parent,false);
            return new ContactViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DividerViewHolder){
            ((DividerViewHolder) holder).divider_spell.setText(resultList.get(position).getName());
        }else if (holder instanceof ContactViewHolder){
            ((ContactViewHolder) holder).name.setText(resultList.get(position).getName());
            ((ContactViewHolder) holder).phonenumber.setText(resultList.get(position).getPhoneNumber());
            ((ContactViewHolder) holder).setPosition(position);

            //设置tag 否则划回来时选中消失
            ((ContactViewHolder) holder).checkBox.setTag(new Integer(position));
            if (checkedBox != null){
                ((ContactViewHolder) holder).checkBox.setChecked(checkedBox.contains(new Integer(position)));
            }else {
                ((ContactViewHolder) holder).checkBox.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return resultList == null ? 0 : resultList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return resultList.get(position).getType();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder{

        CheckBox checkBox;
        TextView name;
        TextView phonenumber;
        int position;

        public ContactViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.item_checked);
            name = (TextView) itemView.findViewById(R.id.name);
            phonenumber = (TextView) itemView.findViewById(R.id.phone_number);

            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked){
                        if (!checkedBox.contains(checkBox.getTag())){
                            checkedBox.add(new Integer(position));
                        }

                        if (onContactCheckedListener != null){
                            onContactCheckedListener.onChecked(position,resultList);
                        }
                    }else{
                        if (checkedBox.contains(checkBox.getTag())){
                            checkedBox.remove(new Integer(position));
                        }

                        if (onContactCheckedListener != null){
                            onContactCheckedListener.onQuitChecked(position,resultList);
                        }
                    }
                }
            });
    }

        public void setPosition(int position) {
            this.position = position;
        }
    }

    class DividerViewHolder extends RecyclerView.ViewHolder{
        TextView divider_spell;

        public DividerViewHolder(View itemView) {
            super(itemView);

            divider_spell = (TextView) itemView.findViewById(R.id.divider_spell);
        }
    }

    public interface OnContactCheckedListener{
        void onChecked(int position,List<ContactItem> resultList);
        void onQuitChecked(int position,List<ContactItem> resultList);
    }

    private OnContactCheckedListener onContactCheckedListener = null;

    public void setOnContactCheckedListener(OnContactCheckedListener onContactCheckedListener) {
        this.onContactCheckedListener = onContactCheckedListener;
    }

    /**
     * 通过传入的字母定位
     * */
    public int getScrollPosition(String letter) {
        if (letterList.contains(letter)) {
            for (int i = 0; i < resultList.size(); i++) {
                if (resultList.get(i).getName().equals(letter)) {
                    return i;
                }
            }
        }

        return -1; // -1不会滑动
    }
}
