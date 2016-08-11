package com.cjx.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.cjx.R;
import com.cjx.data.Constant;
import com.cjx.helper.GetRunCount;
import com.cjx.interfaces.OnGetRunCountListener;
import com.cjx.ui.ProgressView;
import com.cjx.utils.CheckNet;
import com.rey.material.widget.Button;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 南湖跑圈数查询
 * */
public class QueryRunningActivity extends AppCompatActivity implements OnGetRunCountListener{

    private Toolbar toolbar = null;
    private ProgressView progressView = null;
    private EditText username = null;
    private EditText password = null;
    private Button query = null;
    private ImageView seePassword = null;
    private CheckBox remember = null;

    //标记密码可见状态
    private boolean isVisiable = false;

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    //标记密码记住状态
    private boolean isRemember = false;

    //记录圈数
    private float mCount;
    //初始化圈数
    private static float initCount = 0;

    //全局唯一
    private static OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_running);

        init();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Constant.FUNCTION[0]);
        setSupportActionBar(toolbar);
        //显示返回按钮并监听
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressView = (ProgressView) findViewById(R.id.run_count);
        username = (EditText) findViewById(R.id.username);

        password = (EditText) findViewById(R.id.password);
        setPasswordVisible();

        seePassword = (ImageView) findViewById(R.id.see_password);
        seePassword.setImageResource(R.mipmap.lock);
        seePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeVisibleAndIcon();
            }
        });

        remember = (CheckBox) findViewById(R.id.remember);
        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    editor.putBoolean("isRemember",true);
                    editor.putString("username",username.getText().toString());
                    editor.putString("password",password.getText().toString());
                }else{
                    editor.clear();
                }

                editor.commit();
            }
        });

        query = (Button) findViewById(R.id.query);

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckNet.isNetworkAvailable(QueryRunningActivity.this)){
                    if (TextUtils.isEmpty(username.getText().toString())
                            || TextUtils.isEmpty(password.getText().toString())){
                        Toast.makeText(QueryRunningActivity.this, "请输入用户名和密码", Toast.LENGTH_SHORT).show();
                    }else {
                        if (initCount != 0){
                            initCount = 0;
                        }
                        query.setVisibility(View.INVISIBLE);

                        GetRunCount getRunCount = new GetRunCount(okHttpClient);
                        getRunCount.setOnGetRunCountListener(new GetRunCount.OnGetRunCountListener() {
                            @Override
                            public void onSuccess(String count) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(QueryRunningActivity.this, "查询成功", Toast.LENGTH_SHORT).show();
                                        query.setVisibility(View.VISIBLE);
                                    }
                                });

                                mCount = Float.parseFloat(count) * 1.0f / 100;
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        while (initCount <= mCount + 0.01) {
                                            progressView.setPercent(initCount);
                                            initCount += 0.01f;

                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).start();
                            }

                            @Override
                            public void onFailed(final String me) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(QueryRunningActivity.this, me, Toast.LENGTH_SHORT).show();
                                        query.setVisibility(View.VISIBLE);
                                    }
                                });

                            }
                        });
                        getRunCount.get(username.getText().toString(),password.getText().toString());
                    }
                }else {
                    Toast.makeText(QueryRunningActivity.this, "网络无连接", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    /**
     * 设置密码可见状态
     * */
    private void setPasswordVisible(){
        if (isVisiable){
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.requestFocus();
        }else{
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.requestFocus();
        }
    }

    /**
     * 改变密码显示状态及图标
     * */
    private void changeVisibleAndIcon(){
        isVisiable = !isVisiable;

        if (isVisiable){
            seePassword.setImageResource(R.mipmap.open);
        }else{
            seePassword.setImageResource(R.mipmap.lock);
        }
        setPasswordVisible();
    }

    @Override
    protected void onResume() {
        super.onResume();

        isRemember = sharedPreferences.getBoolean("isRemember",false);
        if (isRemember){
            username.setText(sharedPreferences.getString("username",""));
            password.setText(sharedPreferences.getString("password",""));
        }

        remember.setChecked(isRemember);
        username.requestFocus();
    }

    @Override
    public void onGetSuccess(String count) {

    }
}
