package com.cjx.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cjx.R;
import com.cjx.adapter.ContactRecyclerAdapter;
import com.cjx.adapter.RecyclerViewDecoration;
import com.cjx.bean.ContactItem;
import com.cjx.ui.IndexLayout;
import com.cjx.utils.MapSerializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取通讯录联系人
 * */
public class GetContactsActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private TextView sure = null;
    private RecyclerView recyclerView = null;
    private ContactRecyclerAdapter adapter = null;
    private LinearLayoutManager linearLayoutManager = null;

    private List<ContactItem> contactList = new ArrayList<>();

    //存储选中的联系人
    //key--姓名   value--电话
    private Map<String,String> checkedContacts = new HashMap<>();

    private IndexLayout indexLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_contacts);

        getData();
        init();
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                contactList.clear();
            }
        });

        sure = (TextView) findViewById(R.id.contact_sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkedContacts.size() > 0){
                    Intent intent = new Intent(GetContactsActivity.this,SendSMSActivity.class);
                    //数据封装进Bundle
                    MapSerializable mapSe = new MapSerializable();
                    mapSe.setContactsMap(checkedContacts);

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("contact",mapSe);
                    intent.putExtras(bundle);

                    setResult(2,intent);
                    finish();
                }else {
                    Toast.makeText(GetContactsActivity.this, "请选择至少一位联系人", Toast.LENGTH_SHORT).show();
                }

                contactList.clear();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.phone_contacts_list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new RecyclerViewDecoration(this, RecyclerViewDecoration.VERTICAL_LIST));

        adapter = new ContactRecyclerAdapter(this,contactList);
        adapter.setOnContactCheckedListener(new ContactRecyclerAdapter.OnContactCheckedListener() {
            @Override
            public void onChecked(int position, List<ContactItem> resultList) {
                checkedContacts.put(resultList.get(position).getName(),resultList.get(position).getPhoneNumber());
            }

            @Override
            public void onQuitChecked(int position, List<ContactItem> resultList) {
                checkedContacts.remove(resultList.get(position).getName());
            }
        });

        recyclerView.setAdapter(adapter);

        //索引布局
        indexLayout = (IndexLayout) findViewById(R.id.index);
        indexLayout.setOnIndexClickListener(new IndexLayout.OnIndexClickListener() {
            @Override
            public void onLetter(String index) {
                linearLayoutManager.scrollToPositionWithOffset(adapter.getScrollPosition(index), 0);
            }

            @Override
            public void onArrow() {
                linearLayoutManager.scrollToPositionWithOffset(0,0);
            }
        });

    }

    /**
     * 获取联系人数据
     * */
    private void getData(){
        ContactItem contact;

        ContentResolver resolver = this.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            contact = new ContactItem();

            int nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
            contact.setName(cursor.getString(nameIndex));

            //联系人的ID索引
            String id =  cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            //第一个参数是确定查询电话号，第三个参数是查询具体某个人的过滤值
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
            //可能有多个手机号
            int m = 1;
            while (phoneCursor.moveToNext()){
                //只要第一个号
                if (m == 1){
                    String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    contact.setPhoneNumber(phone);
                    m ++;
                }
            }
            phoneCursor.close();

            contactList.add(contact);

        }
        cursor.close();

    }

}
