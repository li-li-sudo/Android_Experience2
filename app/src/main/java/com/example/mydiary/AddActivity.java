package com.example.mydiary;


import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvTime;
    private TextView tvAuthor;
    private EditText editTitle;
    private EditText editText;
    private TextView edTime;
    private PopupWindow mPopWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Button btn_save = findViewById(R.id.save_button);
        Button btn_cancel = findViewById(R.id.cancel_button);
        tvTime=findViewById(R.id.tv_time);
        tvAuthor=findViewById(R.id.tv_author);
        editTitle=findViewById(R.id.edit_title);
        editText=findViewById(R.id.edit_text);
        edTime=findViewById(R.id.tv_ed_time);
        btn_save.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        /***日期设置***/
        /*设置时间格式*/
        SimpleDateFormat formatter =   new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss");
        /*获得当前系统时间*/
        Date curDate =  new Date(System.currentTimeMillis());
        String time= formatter.format(curDate);
        tvTime.setText("创建于："+time);
        edTime.setText("编辑于："+time);

        /***作者设置***/
        SharedPreferences sp=getSharedPreferences("userInfo", MODE_PRIVATE);
        String author=sp.getString("userName",null);
        tvAuthor.setText(author);

        /***标题设置***/
        editTitle.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){
            /***保存按钮***/
            case R.id.save_button:
                /*获取文本内容*/
                String Title=editTitle.getText().toString();
                String time= tvTime.getText().toString();
                String author=tvAuthor.getText().toString();
                String title=editTitle.getText().toString();
                String note=editText.getText().toString();
                String updatetime=edTime.getText().toString();

                /***判断输入内容是否符合要求***/

                /*1.标题非空*/
                if(TextUtils.isEmpty(Title)){
                    Toast.makeText(AddActivity.this,"请输入标题",Toast.LENGTH_SHORT).show();
                    return;
                }

                /*2.内容非空*/
                else if(TextUtils.isEmpty(note)){
                    Toast.makeText(AddActivity.this,"请输入内容",Toast.LENGTH_SHORT).show();
                    return;
                }
                /*满足条件*/
                else {
                    DatabaseHelper dbHelper=new DatabaseHelper(this,"Dairy.db",null,3);
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    ContentValues values = new ContentValues();
                    values.put("author",author);
                    values.put("title",title);
                    values.put("note",note);
                    values.put("time",time);
                    values.put("updatetime",updatetime);
                    db.insert("dairy",null,values);
                    values.clear();
                    dbHelper.close();
                    Intent intent=new Intent(AddActivity.this,MainActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.cancel_button:
                startActivity(new Intent(AddActivity.this,MainActivity.class));
                AddActivity.this.finish();
                break;
            default:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
    /*菜单点击事件*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.addPic_item:
                showPopupWindow();
                break;
            case R.id.back_item:
                startActivity(new Intent(AddActivity.this,MainActivity.class));
                AddActivity.this.finish();
                break;
            default:
        }
        return true;
    }
    private void showPopupWindow() {
        /*设置contentView*/
        View contentView = LayoutInflater.from(AddActivity.this).inflate(R.layout.activity_popwindow, null);
        mPopWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        mPopWindow.setContentView(contentView);
        //设置各个控件的点击响应
        TextView tv_photo = (TextView)contentView.findViewById(R.id.pop_photo);
        TextView tv_picture = (TextView)contentView.findViewById(R.id.pop_picture);
        TextView tv_cancel = (TextView)contentView.findViewById(R.id.pop_cancel);
        tv_photo.setOnClickListener(this);
        tv_picture.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        //显示PopupWindow
        View rootview = LayoutInflater.from(AddActivity.this).inflate(R.layout.activity_add, null);
        mPopWindow.showAtLocation(rootview, Gravity.BOTTOM, 0, 0);

    }


}
