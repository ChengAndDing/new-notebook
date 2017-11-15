package com.example.chenghao.newnotebook;

/**
 * Created by chenghao on 2017/10/25.
 */


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import static com.example.chenghao.newnotebook.MainActivity.dbOperator;
import static com.example.chenghao.newnotebook.R.layout.words_add;

public class InteractiveInterface extends Activity{

    private Context context;
    public InteractiveInterface(Context context1){
        this.context = context1;
    }

    //向笔记本中添加单词
    public void interfaceADD(){

        //substantialize the widgets

        final View add_view = ((Activity)context).getLayoutInflater().inflate(words_add,null);
        final EditText et_eng = (EditText) add_view.findViewById(R.id.et_eng);
        final EditText et_fre = (EditText) add_view.findViewById(R.id.et_fre);
        final EditText et_domain = (EditText) add_view.findViewById(R.id.et_domain);
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);  //弹出提示对话框

      alert_builder.setView(add_view).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String eng = et_eng.getText().toString();
                String fre = et_fre.getText().toString();
                String domain = et_domain.getText().toString();
                dbOperator.insertWords(eng, fre, domain);
                Toast toast = Toast.makeText(context, " ADD SUCCESSFULLY ", Toast.LENGTH_SHORT);
                toast.show();
            }
        }).show();
    }

    //笔记本数据更新操作
    public void interfaceEDIT(final String old_eng, final String old_fre, final String old_domain){

        //substantialize the widgets

        final View add_view = ((Activity)context).getLayoutInflater().inflate(words_add,null);
        final EditText et_eng = (EditText) add_view.findViewById(R.id.et_eng);
        final EditText et_fre = (EditText) add_view.findViewById(R.id.et_fre);
        final EditText et_domain = (EditText) add_view.findViewById(R.id.et_domain);
        AlertDialog.Builder alert_builder = new AlertDialog.Builder(context);  //弹出提示对话框
        et_eng.setText(old_eng);
        et_fre.setText(old_fre);
        et_domain.setText(old_domain);

        alert_builder.setView(add_view).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).setPositiveButton("edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String eng = et_eng.getText().toString();
                String fre = et_fre.getText().toString();
                String domain = et_domain.getText().toString();
                dbOperator.editWords(eng, fre, domain,old_eng); //edit the word in database
                Toast toast = Toast.makeText(context, " EDIT SUCCESSFULLY ", Toast.LENGTH_SHORT);
                toast.show();
            }
        }).show();
    }
}
