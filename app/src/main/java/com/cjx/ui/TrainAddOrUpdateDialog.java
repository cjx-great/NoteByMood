package com.cjx.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cjx.R;
import com.cjx.utils.CheckInput;
import com.cjx.utils.TimeFormat;

/**
 * Created by CJX on 2016/8/6.
 */
public class TrainAddOrUpdateDialog extends DialogFragment {

    private EditText input = null;
    private com.rey.material.widget.Button cancel = null;
    private com.rey.material.widget.Button sure = null;

    private String hint = "";
    //标记当前操作
    private int flag;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.train_add_update_dialog,null);

        builder.setTitle("心得小记");
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        input = (EditText) view.findViewById(R.id.train_input);
        input.setText(hint);
        //光标置于最后
        input.requestFocus();
        cancel = (com.rey.material.widget.Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        sure = (com.rey.material.widget.Button) view.findViewById(R.id.sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String me = input.getText().toString();
                if (CheckInput.isLegal(me)){
                    if (flag == 1){
                        if (onAddDialog != null){
                            onAddDialog.onSuccess(me, TimeFormat.getTime());
                            dialog.dismiss();
                        }
                    }else if (flag == 2){
                        if (onUpdateDialog != null){
                            onUpdateDialog.onSuccess(me,TimeFormat.getTime());
                            dialog.dismiss();
                        }
                    }
                }else{
                    Toast.makeText(getActivity(), "未输入", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return dialog;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * 插入操作回调
     * */
    public interface OnAddDialog{
        void onSuccess(String note,String time);
    }

    private OnAddDialog onAddDialog = null;

    public void setOnAddDialog(OnAddDialog onAddDialog) {
        this.onAddDialog = onAddDialog;
    }

    /**
     * 更新操作回调
     * */
    public interface OnUpdateDialog{
        void onSuccess(String note,String time);
    }

    private OnUpdateDialog onUpdateDialog = null;

    public void setOnUpdateDialog(OnUpdateDialog onUpdateDialog) {
        this.onUpdateDialog = onUpdateDialog;
    }

}
