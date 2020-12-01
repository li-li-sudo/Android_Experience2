package com.example.mydiary;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.content.*;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import android.text.method.*;

public class LoginActivity extends AppCompatActivity {

    private SharedPreferences.Editor editor;
    private TextView tv_Register;
    private String userName,userPwd;
    private EditText et_userName,et_userPwd;
    private ImageButton btn_login;
    private CheckBox rememberPwd,showPwd;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*获取对象实例*/
        btn_login = (ImageButton)findViewById(R.id.btn_login);
        tv_Register = (TextView) findViewById(R.id.tv_Register);
        et_userName = (EditText) findViewById(R.id.et_userName);
        et_userPwd = (EditText)findViewById(R.id.et_userPwd);
        rememberPwd = (CheckBox)findViewById(R.id.remeber_pwd);
        showPwd = (CheckBox)findViewById(R.id.show_pwd);

        /*使用当前应用程序的包名作为前缀命名SharedPreferences文件*/
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        /***判断当前是否保存密码状态***/
        boolean isRemember = pref.getBoolean("remember_pwd",false);
        if(isRemember){
            String account = pref.getString("userName","");
            String password = pref.getString("userPwd","");
            et_userName.setText(account);
            et_userPwd.setText(password);
            /*把光标移到文本末尾处*/
            et_userName.setSelection(et_userName.getText().length());
            et_userPwd.setSelection(et_userPwd.getText().length());
            rememberPwd.setChecked(true);
        }

        /*注册按钮点击事件*/
        tv_Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*创建intent对象，参数分别为上下文、要跳转的Activity类*/
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                /*启动该intent对象，实现Activity跳转*/
                startActivity(intent);
            }
        });

        /*登陆按钮点击事件*/
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*获取用户名和密码*/
                getEditString();

                /*获得加密后的密码*/
                String md5Pwd= MD5.getMD5Code(userPwd);
                String spPwd=readPwd(userName);

                /***判断用户名密码是否符合要求***/

                /*1.用户名不为空*/
                if(TextUtils.isEmpty(userName)){
                    Toast.makeText(LoginActivity.this,"请输入用户名",Toast.LENGTH_SHORT).show();
                    return;
                }
                /*2. 用户名存在*/
                else if(!isExistUserName(userName)){
                    Toast.makeText(LoginActivity.this,"此用户名不存在，请注册",Toast.LENGTH_SHORT).show();
                    return;
                }
                /*3.密码不为空*/
                else if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_SHORT).show();
                    return;

                }
                /*4.密码是否正确*/
                else if(!md5Pwd.equals(spPwd)){
                    Toast.makeText(LoginActivity.this, "用户名与密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                /***密码正确则成功登陆***/
                else if(md5Pwd.equals(spPwd)) {
                    /**记录保存密码**/
                    SharedPreferences.Editor editor = pref.edit();
                    if (rememberPwd.isChecked()) {
                        editor.putBoolean("remember_pwd", true);
                        editor.putString("userName", userName);
                        editor.putString("userPwd", userPwd);
                    } else
                        editor.clear();
                    editor.apply();

                    /*提示登录成功*/
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                    /*保存登录状态，在界面保存登录的用户名 定义个方法 saveLoginStatus boolean 状态 , userName 用户名;*/
                    saveLoginStatus(true, userName);

                    /***登录成功后关闭页面进入主页***/
                    Intent intent = new Intent();
                    intent.putExtra("isLogin", true);

                    /*登陆成功后将返回到主页*/
                    setResult(RESULT_OK, intent); //RESULT_OK为Activity系统常量，状态码为-1
                    LoginActivity.this.finish(); //销毁登录界面

                    /*跳转到主界面，登录成功的状态传递到 MainActivity 中*/
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    return;
                }
            }
        });

        /*显示密码复选框点击处理*/
        showPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showPwd.isChecked()){
                    showOrHide(et_userPwd,true);
                }else{
                    showOrHide(et_userPwd,false);
                }
            }
        });
    }
    /*判断用户名是否存在*/
    public boolean isExistUserName(String userName){
        boolean judge=false;
        SharedPreferences sp=getSharedPreferences("userInfo", 0);

        /*获取用户名密码*/
        String spPwd=sp.getString(userName, "");

        /*如果密码不为空，则用户名存在*/
        if(!TextUtils.isEmpty(spPwd)) {
            judge=true;
        }

        return judge;
    }
    /*获取EditText中的内容*/
    private void getEditString() {
        userName=et_userName.getText().toString().trim();
        userPwd=et_userPwd.getText().toString().trim();
    }

    /*从SharedPreferences中根据用户名读取密码*/
    private String readPwd(String userName){
        SharedPreferences sp=getSharedPreferences("userInfo", 0);
        return sp.getString(userName , "");
    }

    /*保存登录状态和登录用户名到SharedPreferences中*/
    private void saveLoginStatus(boolean status,String userName){
        //saveLoginStatus(true, userName);
        //loginInfo表示文件名  SharedPreferences sp=getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences sp=getSharedPreferences("userInfo", 0);
        //获取编辑器
        SharedPreferences.Editor editor=sp.edit();
        //存入boolean类型的登录状态
        editor.putBoolean("isLogin", status);
        //存入登录状态时的用户名
        editor.putString("userName", userName);
        //提交修改
        editor.commit();
    }


    /*注册成功的数据返回至此*/
    @Override
    /*显示数据， onActivityResult
    *startActivityForResult(intent, 1); 从注册界面中获取数据
    * int requestCode , int resultCode , Intent data
    * Login -> startActivityForResult -> onActivityResult();*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String userName=data.getStringExtra("userName");
        et_userName.setText(userName);
        et_userName.setSelection(userName.length());
    }
    /*显示或隐藏密码*/
    private void showOrHide(EditText passwordEdit,boolean isShow){

        int pos = passwordEdit.getSelectionStart();//记住光标开始的位置
        if(isShow){
            passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }else{
            passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        passwordEdit.setSelection(pos);
    }
}
