package com.example.mydiary;

import android.content.Intent;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateActivity extends AppCompatActivity implements View.OnClickListener {
    String author,title,note,time,updatetime,title1,note1;
    private TextView tvTime;
    private TextView tvAuthor;
    private EditText editTitle;
    private EditText editText;
    private TextView edTime;
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        /*获取对象*/
        tvTime = (TextView)findViewById(R.id.tv_time);
        tvAuthor = (TextView)findViewById(R.id.tv_author);
        editTitle = (EditText)findViewById(R.id.edit_title);
        editText = (EditText)findViewById(R.id.edit_text);
        edTime = (TextView)findViewById(R.id.tv_ed_time);
        Button btn_save=  findViewById(R.id.save_button);
        Button btn_cancel= findViewById(R.id.cancel_button);

        /*获取内容*/
        getValues();

        /*显示内容*/
        tvTime.setText(time);
        tvAuthor.setText(author);
        editTitle.setText(title);
        editText.setText(note);
        edTime.setText(updatetime);

        /*设置监听器*/
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            /*保存按钮*/
            case R.id.save_button:
                title1=editTitle.getText().toString();
                note1=editText.getText().toString();
                if(TextUtils.isEmpty(title1)){
                    Toast.makeText(UpdateActivity.this,"请输入标题",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(note1)){
                    Toast.makeText(UpdateActivity.this,"请输入内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!(title1.equals(title))||!(note1.equals(note))) {
                    //更新时间
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");
                    Date update = new Date(System.currentTimeMillis());
                    String uptime = formatter.format(update);
                    edTime.setText("编辑于：" + uptime);
                    updatetime = edTime.getText().toString();
                }
                dbHelper = new  DatabaseHelper(this,"Dairy.db",null,3);
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                ContentValues values = new ContentValues();
                values.put("updatetime",updatetime);
                values.put("title",title1);
                values.put("note",note1);
                db.update("dairy",values,"time=?",new String[]{time});
                dbHelper.close();
                Intent intent=new Intent(UpdateActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            /*取消按钮*/
            case R.id.cancel_button:
                dbHelper.close();
                Intent intent1=new Intent(UpdateActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
            default:
                break;
        }
    }
    /*获取内容*/
    private void getValues(){
        Intent intent = getIntent();
        String tmp = intent.getStringExtra("id");
        int id = Integer.valueOf(tmp);
        SharedPreferences sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        String author = sp.getString("userName",null);

        dbHelper = new DatabaseHelper(UpdateActivity.this, "Dairy.db",null,3);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("dairy",null,"author=?",new String[]{author},null,null,null);
        if (cursor.moveToFirst()){
            int i = 0;
            do{
                if(i == id){
                    author = cursor.getString(cursor.getColumnIndex("author"));
                    title = cursor.getString(cursor.getColumnIndex("title"));
                    note = cursor.getString(cursor.getColumnIndex("note"));
                    time = cursor.getString(cursor.getColumnIndex("time"));
                    updatetime = cursor.getString(cursor.getColumnIndex("updatetime"));
                    break;
                }
                i++;
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
}

