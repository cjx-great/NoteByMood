package com.cjx.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.cjx.R;
import com.cjx.adapter.SMSListAdapter;
import com.cjx.bean.ContactItem;
import com.cjx.utils.MapSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 群发短信
 * */
public class SendSMSActivity extends AppCompatActivity {

    private Toolbar toolbar = null;
    private EditText message = null;
    private ListView contacts = null;
    private FloatingActionButton send = null;

    private SMSListAdapter adapter = null;
    private List<ContactItem> contactItemList = new ArrayList<>();

    //短信发送成功接收到的回复
    private String SEND_SUCCESS = "SEND_SUCCESS";
    private Intent sendIntent = null;
    private PendingIntent pendingIntent = null;
    private IntentFilter intentFilter = null;
    private BroadcastReceiver receiver = null;

    //记录初始发送时联系人个数
    private static int count = 0;
    //记录未发送成功的条数
    private static int failed = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        init();
        initReceiver();
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("群发短信");
        setSupportActionBar(toolbar);
        //显示返回按钮并监听
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!message.getText().toString().equals("")){
                    showDialog();
                }else{
                    finish();
                }
            }
        });

        message = (EditText) findViewById(R.id.message);
        contacts = (ListView) findViewById(R.id.contact_list);
        send = (FloatingActionButton) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contactItemList.size() == 0){
                    Toast.makeText(SendSMSActivity.this, "请选择联系人", Toast.LENGTH_SHORT).show();
                }else if (message.getText().toString().equals("")){
                    Toast.makeText(SendSMSActivity.this, "请输入信息内容", Toast.LENGTH_SHORT).show();
                }else {
                    count = contactItemList.size();
                    send(0);

                    count --;
                }
            }
        });

        adapter = new SMSListAdapter(this,contactItemList);
        contacts.setAdapter(adapter);

        contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long id) {
                new AlertDialogWrapper.Builder(SendSMSActivity.this)
                        .setTitle("确定删除该联系人?")
                        .setMessage(contactItemList.get(position).getName() + "\n"
                                + contactItemList.get(position).getPhoneNumber())
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                contactItemList.remove(position);
                                adapter.notifyDataSetChanged();
                                dialogInterface.dismiss();
                            }
                        }).show();

                return true;
            }
        });
    }

    /**
     * 注册监听短信发送成功的广播
     * */
    private void initReceiver(){
        sendIntent = new Intent(SEND_SUCCESS);
        pendingIntent = PendingIntent.getBroadcast(SendSMSActivity.this,
                0,sendIntent,0);
        intentFilter = new IntentFilter(SEND_SUCCESS);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                /* android.content.BroadcastReceiver.getResultCode()方法 */
                switch (getResultCode()) {
                    /* 发送短信成功 */
                    case Activity.RESULT_OK:
                        //还有未发送的就继续发送
                        if (count > 0){
                            contactItemList.remove(failed);
                            adapter.notifyDataSetChanged();

                            send(failed);
                            count --;
                        }
                        break;

                    /* 发送短信失败 */
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                    default:
                        //还有未发送的就继续发送
                        Toast.makeText(SendSMSActivity.this, "发送给" + contactItemList.get(failed).getName() + "失败", Toast.LENGTH_SHORT).show();
                        if (count > 0){
                            send(failed + 1);
                            count --;
                        }
                        failed ++;
                        break;
                }
            }
        };

        this.registerReceiver(receiver, intentFilter);
    }

    /**
     * 执行发送操作
     *
     * 此方法只能调用contactItemList.size()次
     * */
    private void send(int index){
        SmsManager smsManager = SmsManager.getDefault();
        //界面已经做了字数限制，此处不再分割
        //每次向联系人列表的第一个发送
        smsManager.sendTextMessage(contactItemList.get(index).getPhoneNumber(),null,
                message.getText().toString(),pendingIntent,null);
    }

    /**
     * 弹框提醒
     * */
    private void showDialog(){
        new AlertDialogWrapper.Builder(this)
                .setMessage("数据会丢失,确定退出编辑吗？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.send_sms_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_contact:
                startActivityForResult(new Intent(this,GetContactsActivity.class),1);
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2){
            Bundle bundle = data.getExtras();
            MapSerializable mapSe = (MapSerializable) bundle.get("contact");
            Map<String,String> map = mapSe.getContactsMap();

            ContactItem item;
            for (Map.Entry entry : map.entrySet()) {
                item = new ContactItem();
                item.setName((String) entry.getKey());
                item.setPhoneNumber((String) entry.getValue());
                contactItemList.add(item);
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        if (!message.getText().toString().equals("")){
            showDialog();
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
