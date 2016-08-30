package org.umaru.miyukisyllabus;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.CookieManager;
import java.net.*;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import GradeRelated.RequestParamters;
import ProgramFeatures.ProgramConfig;
import ProgramFeatures.Static;

public class LoginActivity extends AppCompatActivity {
    Toolbar mToolbar;

    Button btnLogin;
    ImageView mCode;
    EditText mUsername;
    EditText mPassword;
    EditText mVerify;
    CheckBox mRememberPassword;

    final int TOAST_ERROR = 0,
            TOAST_OK = 1,
            TOAST_MSGGET_ERROR = 2,
            E_GET_VERIFY_CODE_COMPLETE = 3,
            E_LOGIN_COMPLETED = 4,
            TOAST_TIMEOUT = 5;

    boolean viewstate_got = false;
    boolean password_saved = false;

    Class redirectActivityName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //获取参数表示专项的位置
        String destination = getIntent().getExtras().getString("destination");
        if (destination.equals("import")){
            redirectActivityName = ImportActivity.class;
        }
        else if (destination.equals("score")) {
            redirectActivityName = GradePrepareActivity.class;
        }
        else {
            Toast.makeText(this, "错误的意图。destination="+destination, Toast.LENGTH_SHORT).show();
            finish();
        }

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.setTitle("登录教务系统");

        btnLogin = (Button)findViewById(R.id.login_login);
        mCode = (ImageView)findViewById(R.id.login_verify_image);
        mUsername = (EditText)findViewById(R.id.login_user);
        mPassword = (EditText)findViewById(R.id.login_password);
        mVerify = (EditText)findViewById(R.id.login_verify);
        mRememberPassword = (CheckBox)findViewById(R.id.login_remember_pwd);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getViewState();
            }
        });

        mCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getVerfiyCode(false);
            }
        });

        if (!ProgramConfig.saved_user_name.equals("") && !ProgramConfig.saved_password.equals("")) {
            mUsername.setText(ProgramConfig.saved_user_name);
            mPassword.setHint(R.string.login_password_rem);
            mRememberPassword.setChecked(true);
            password_saved = true;
        }

        if (Static.rp.loginSuccess) {
            Intent intent = new Intent(LoginActivity.this, redirectActivityName);
            startActivity(intent);
            finish();
            return;
        }

        getVerfiyCode(true);

    }

    ProgressDialog process_dialog = null;

    public void getVerfiyCode(boolean getCookie) {
        process_dialog = new ProgressDialog(this);
        process_dialog.setTitle("请等待");
        process_dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LoginActivity.this.finish();
            }
        });
        process_dialog.setMessage("正在获取信息...");
        process_dialog.show();

        if (getCookie)
            new Thread(Thread_getVerifyCodeAndCookie).start();
        else
            new Thread(Thread_getVerifyCode).start();
    }

    Runnable Thread_getVerifyCode = new Runnable() {
        @Override
        public void run() {
            System.out.println("获取Cookie...");
            CookieManager manager = new CookieManager();
            CookieHandler.setDefault(manager);
            Static.saveBinaryFile(Static.PATH_FILE_CHECKCODE, "http://newjwc.tyust.edu.cn/CheckCode.aspx", Static.rp.cookies.toString());
            CookieStore cookieJar = manager.getCookieStore();
            Static.rp.cookies = cookieJar.getCookies();

            for (HttpCookie cookie : Static.rp.cookies) {
                System.out.println(cookie);
            }
            sendMessage(E_GET_VERIFY_CODE_COMPLETE);
        }
    };

    Runnable Thread_getVerifyCodeAndCookie = new Runnable() {
        @Override
        public void run() {
            try {
                CookieManager manager = new CookieManager();
                CookieHandler.setDefault(manager);

                Connection conn = Jsoup.connect("http://newjwc.tyust.edu.cn").timeout(10000).method(Connection.Method.GET);
                org.jsoup.Connection.Response res = conn.execute();
                Static.rp.doc = res.parse();
                System.out.println("获取viewstate...");
                Element ele = Static.rp.doc.select("input[name=__VIEWSTATE]").get(0);

                Static.rp.viewstate = ele.attr("value");

                System.out.println("获取Cookie...");
                CookieStore cookieJar = manager.getCookieStore();
                Static.rp.cookies = cookieJar.getCookies();

                System.out.println("获取验证码...");
                Static.saveBinaryFile(Static.PATH_FILE_CHECKCODE, "http://newjwc.tyust.edu.cn/CheckCode.aspx", Static.rp.cookies.toString());

                for (HttpCookie cookie : Static.rp.cookies) {
                    System.out.println(cookie);
                }

                sendMessage(E_GET_VERIFY_CODE_COMPLETE);
            } catch (IOException e) {
                e.printStackTrace();
                sendMessage(TOAST_MSGGET_ERROR);
                return;
            }
        }
    };

    private void getViewState() {
        process_dialog = new ProgressDialog(LoginActivity.this);
        process_dialog.setTitle("请等待");
        process_dialog.setMessage("正在登录...");
        process_dialog.show();

        //final String t = "2";
        new Thread() {
            public void run(){
                String password = mPassword.getText().toString();
                if (password_saved && password.equals("")) {
                    password = ProgramConfig.saved_password;
                }
                if (mRememberPassword.isChecked()) {
                    if (!(mUsername.getText().toString().equals(ProgramConfig.saved_user_name) && password.equals(ProgramConfig.saved_password))) {
                        ProgramConfig.saved_user_name = mUsername.getText().toString();
                        ProgramConfig.saved_password = password;
                        ProgramConfig.json.remove("saved_password");
                        ProgramConfig.json.remove("saved_user_name");
                        try {
                            ProgramConfig.json.put("saved_password", ProgramConfig.saved_password);
                            ProgramConfig.json.put("saved_user_name", ProgramConfig.saved_user_name);
                            Static.WriteSettings();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (password_saved) {
                    ProgramConfig.saved_user_name = "";
                    ProgramConfig.saved_password = "";
                    ProgramConfig.json.remove("saved_password");
                    ProgramConfig.json.remove("saved_user_name");
                    try {
                        ProgramConfig.json.put("saved_password", ProgramConfig.saved_password);
                        ProgramConfig.json.put("saved_user_name", ProgramConfig.saved_user_name);
                        Static.WriteSettings();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                login(mUsername.getText().toString(), password, mVerify.getText().toString());
            }
        }.start();
    }

    private void login(String user_name, String password, String code) {
        ///TODO: 路径
        System.out.println("code=" + code);
        Connection conn = Jsoup.connect("http://newjwc.tyust.edu.cn/Default2.aspx");
        conn.data("__VIEWSTATE", Static.rp.viewstate);
        conn.data("txtUserName", user_name);
        conn.data("TextBox2", password);
        conn.data("txtSecretCode", code);
        try {
            conn.data("RadioButtonList1", URLEncoder.encode("学生", "UTF8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.data("Button1", "");
        conn.data("lbLanguage", "");
        conn.data("hidPdrs", "");
        conn.data("hidsc", "");
        conn.timeout(30000);
        System.out.println("传送的session id: ");
        for (HttpCookie cookie: Static.rp.cookies) {
            String[] tmp = cookie.toString().split("=");
            //conn.cookie(tmp[0], tmp[1]);
            System.out.println(tmp[0]+"="+tmp[1]);
            //conn.cookie(tmp[0], tmp[1]);
        }

        try {
            Static.rp.doc = conn.post();
            String location = Static.rp.doc.location();
            Static.rp.referer = location;

            if(location.indexOf("xs_main.aspx") != -1){
                sendMessage(TOAST_OK);
                Elements eles = Static.rp.doc.select(".sub a");
                for (Element e : eles){
                    System.out.println(e.html() + ": " + e.attr("href"));
                    Static.rp.urls.put(e.html(), e.attr("href"));
                }
                sendMessage(E_LOGIN_COMPLETED);
            }
            else {
                sendMessage(TOAST_ERROR);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            sendMessage(TOAST_TIMEOUT);
            e.printStackTrace();
        }
    }

    private void sendMessage(int code) {
        Message msg = new Message();
        msg.arg1 = code;
        msgHandler.sendMessage(msg);
    }

    Handler msgHandler = new Handler(){
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.arg1) {
                case TOAST_ERROR:
                    Elements ee = Static.rp.doc.select("script");
                    String reason = "";
                    Pattern p = Pattern.compile("<script (.*)?>alert\\('(.*)?'\\);");
                    Matcher m = p.matcher(ee.get(ee.size() - 1).toString());
                    if (m.find()) {
                        reason = ": " + m.group(2);
                    }
                    Toast.makeText(LoginActivity.this, "登录失败" + reason, Toast.LENGTH_SHORT).show();
                    process_dialog.dismiss();
                    getVerfiyCode(false);
                    break;
                case TOAST_OK:
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    process_dialog.dismiss();
                    Static.rp.loginSuccess = true;
                    break;
                case E_GET_VERIFY_CODE_COMPLETE:
                    mCode.setImageBitmap(BitmapFactory.decodeFile(Static.PATH_FILE_CHECKCODE));
                    process_dialog.dismiss();
                    break;
                case TOAST_MSGGET_ERROR:
                    Toast.makeText(LoginActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case E_LOGIN_COMPLETED:
                    Intent intent = new Intent(LoginActivity.this, redirectActivityName);
                    startActivity(intent);
                    finish();
                    break;
                case TOAST_TIMEOUT:
                    Toast.makeText(LoginActivity.this, "请求超时，请重试。", Toast.LENGTH_SHORT).show();
                    process_dialog.dismiss();
                    break;
            }
        }
    };

    public void showErrorDialog_verifycode () {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("发生错误")
                .setMessage("获取验证码失败。")
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        LoginActivity.this.finish();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginActivity.this.finish();
                    }
                }).show();
    }

    public void showErrorDialog_viewstate () {
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle("发生错误")
                .setMessage("获取VIEWSTATE和cookie失败。")
                .setPositiveButton("确定", null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
