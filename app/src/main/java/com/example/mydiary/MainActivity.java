package com.example.mydiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView listView = null;   //显示列表的ListView
    List<String> list = new ArrayList();   //存放列表的信息
    private EditText et_search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*获取登陆用户*/
        SharedPreferences sp=getSharedPreferences("userInfo", MODE_PRIVATE);
        final String author=sp.getString("userName",null);

        et_search = (EditText)findViewById(R.id.search_et_input);
        /*搜索*/
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            /*Text内容改变时调用此方法*/
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = et_search.getText().toString().trim();
                showSearch(author,text);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        /*展示用户的信息*/
        showAll(author);

        /***点击事件-更改***/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent i=new Intent(MainActivity.this,UpdateActivity.class);
                i.putExtra("id",String.valueOf(id));
                startActivity(i);//启动第二个activity并把i传递过去
            }
        });
        /***长点击事件-删除***/
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                /***弹出对话框,确定删除操作***/
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("删除");
                dialog.setMessage("是否删除该条笔记？");
                dialog.setCancelable(false);
                //确定按键
                final DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this, "Dairy.db",null,3);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = dbHelper.getReadableDatabase();
                        Cursor cursor = db.query("dairy",null,null,null,null,null,null);
                        int i=0;
                        boolean del=false;//判断是否成功删除数据
                        //删除选中数据
                        if (cursor.moveToFirst()){
                            do{
                                //删除数据
                                if(i == position){
                                    String time = cursor.getString(cursor.getColumnIndex("time"));
                                    db.delete("dairy","time=?",new String[]{time});
                                    del=true;
                                    break;
                                }
                                i++;
                            }while(cursor.moveToNext());
                        }
                        //更新列表数据
                        if(del){
                            //移除已删除的信息
                            list.remove(list.get(position));
                            //刷新
                            final ArrayAdapter<String> adapter = new ArrayAdapter(
                                    MainActivity.this,android.R.layout.simple_list_item_1,list);
                            adapter.notifyDataSetChanged();
                            Toast.makeText(MainActivity.this,"删除成功",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this,"删除失败",
                                    Toast.LENGTH_SHORT).show();
                        }
                        cursor.close();
                        showAll(author);
                    }
                });
                //取消按键
                dialog.setNegativeButton("取消",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
                return true;
            }
        });

    }
    public void showSearch(String author,String text){
        final  DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this, "Dairy.db",null,3);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        /*query table：表名，columns：列名 selection：where的约束条件    groupby：需要group by的列    having  对group by结果进一步约束 orderBy 查询结果排列方式*/
        Cursor cursor = db.query("dairy",null,"author=?",new String[]{author},null,null,null);
        list.clear();
        if (cursor.moveToFirst()){
            do{
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String note = cursor.getString(cursor.getColumnIndex("note"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                if(title.contains(text) || note.contains(text))
                    list.add(title+"\n"+time);
            }while(cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter(
                MainActivity.this,android.R.layout.simple_list_item_1,list);
        listView =  (ListView)findViewById(R.id.list_item);
        listView.setAdapter(adapter);
    }
    public void showAll(String author){
        final  DatabaseHelper dbHelper = new DatabaseHelper(MainActivity.this, "Dairy.db",null,3);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        /*query table：表名，columns：列名 selection：where的约束条件    groupby：需要group by的列    having  对group by结果进一步约束 orderBy 查询结果排列方式*/
        Cursor cursor = db.query("dairy",null,"author=?",new String[]{author},null,null,null);
        if (cursor.moveToFirst()){
            do{
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                list.add(title+"\n"+time);
            }while(cursor.moveToNext());
        }
        cursor.close();
        final ArrayAdapter<String> adapter = new ArrayAdapter(
                MainActivity.this,android.R.layout.simple_list_item_1,list);
        listView =  (ListView)findViewById(R.id.list_item);
        listView.setAdapter(adapter);
    }
    /*创建菜单*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    /*菜单点击事件*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            /*添加日记*/
            case R.id.add_item:
                startActivity(new Intent(MainActivity.this,AddActivity.class));
                break;
            /*退出app*/
            case R.id.exit_item:
                System.exit(0);
                break;
            /*退出登录*/
            case R.id.logout_item:
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                MainActivity.this.finish();
                break;
            default:
        }
        return true;
    }
}
