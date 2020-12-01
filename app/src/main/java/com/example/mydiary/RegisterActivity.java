package com.example.mydiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class RegisterActivity extends AppCompatActivity {
    private ImageButton btn_registered;
    private EditText userName, userPwd, userPwd1;
    private String name, pwd, pwd1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        btn_registered = findViewById(R.id.btn_registered);
        userName = findViewById(R.id.userName);
        userPwd = findViewById(R.id.userPwd);
        userPwd1 = findViewById(R.id.userPwd1);
        /*注册按钮点击事件*/
        btn_registered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,18}$";//密码限制必须包含字母和数字，且在6~18位之间

                /*获取EditText中的内容*/
                getEditString();

                /****判断EditText中的内容是否符合要求****/

                /*1.用户名非空*/
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*2.用户名未被注册*/
                /*从SharedPreferences中读取输入的用户名，判断SharedPreferences中是否有此用户名*/
                else if(isExistUserName(name)){
                    Toast.makeText(RegisterActivity.this, "此账户名已经存在", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*3.密码非空*/
                else if(TextUtils.isEmpty(pwd)){
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*4.密码符合要求*/
                else if(!pwd.matches(reg)){
                    Toast.makeText(RegisterActivity.this, "密码必须包含字母和数字，且在6~18位之间", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*5.第二次输入的密码非空*/
                else if(TextUtils.isEmpty(pwd1)){
                    Toast.makeText(RegisterActivity.this, "请再次输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*6.两次输入的密码一致*/
                else if(!pwd.equals(pwd1)){
                    Toast.makeText(RegisterActivity.this, "输入两次的密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }

                /***满足要求，完成注册，跳转登陆页面***/
                else{
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();

                    /*保存账号和密码到SharedPreferences中*/
                    saveRegisterInfo(name, pwd);

                    /***注册成功后把账号传递到Login中***/
                    Intent intent = new Intent();
                    intent.putExtra("userName", name);
                    /*表示此页面下的内容操作成功将intent返回到上一页面，如果是用back返回过去的则不存在用setResult传递data值*/
                    setResult(RESULT_OK, intent);//RESULT_OK为Activity系统常量，状态码为-1，
                    RegisterActivity.this.finish();//关闭活动
                }
            }
        });
    }

    /***获取EditText中的内容***/
    private void getEditString(){
        name= userName.getText().toString().trim();
        pwd= userPwd.getText().toString().trim();
        pwd1= userPwd1.getText().toString().trim();
    }


    /***判断用户名是否存在****/
    public boolean isExistUserName(String userName){
        boolean judge=false;
        /*getSharedPreferences()方法
         *有两个参数，一个参数用于指定SharedPreferences文件的名字，若指定文件不存在则创建一个；
         * 第二个参数用于指定操作模式，目前只剩默认模式MODE_PRIVATE，可直接传入0。表示只有当前的应用程序才可以对此文件进行读写*/
        SharedPreferences sp=getSharedPreferences("userInfo", 0);
        /*获取用户名密码*/
        String spPwd=sp.getString(userName, "");

        /*如果密码不为空，则用户名存在*/
        if(!TextUtils.isEmpty(spPwd)) {
            judge=true;
        }

        return judge;
    }

    /***保存账号和密码到SharedPreferences中***/
    private void saveRegisterInfo(String userName,String userPwd){
        /*将密码进行MD5加密*/
        String md5Pwd = MD5.getMD5Code(userPwd);

        SharedPreferences sp=getSharedPreferences("userInfo", 0);

        /*调用SharedPreferences对象的edit()方法，获取一个SharedPreferences.Editor对象*/
        SharedPreferences.Editor editor=sp.edit();

        /*向SharedPreferences.Editor对象中添加数据，用putString()方法添加字符串,
         *以用户名为key，密码为value保存在SharedPreferences中*/
        editor.putString(userName, md5Pwd);

        /*调用apply()方法将添加到数据提交，从而完成数据存储*/
        editor.apply();
    }
}