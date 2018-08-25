package org.ruoxue.miyukisyllabus.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.ruoxue.miyukisyllabus.R;
import org.ruoxue.miyukisyllabus.Util.Static;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvalActivity extends AppCompatActivity {
    Toolbar mToolbar;

    private LinearLayout 	mEval_auto;
    private LinearLayout    mEval_mannual;

    ProgressDialog progressDialog;

    String evalMessage = "";

    String __EVENTTARGET;
    String  __EVENTARGUMENT;
    String __VIEWSTATE2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eval);

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

        mEval_auto = (LinearLayout) findViewById(R.id.eval_auto);
        mEval_mannual = (LinearLayout) findViewById(R.id.eval_mannual);

        this.setTitle("教学评价");

        mEval_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(EvalActivity.this);
                progressDialog.setTitle("请等待");
                progressDialog.setMessage("正在获取待评价的课程");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread(threadEval).start();
            }
        });

        mEval_mannual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    Runnable threadEval = new Runnable() {
        @Override
        public void run() {
            // 获取教学评价地址
            HashMap<String, String> EvalUrls = new HashMap<>();
            for (String key : Static.rp.urls.keySet()) {
                String url = Static.rp.urls.get(key);
                if (url.contains("xsjxpj.aspx")) {
                    EvalUrls.put(key, url);
                }
            }

            if (EvalUrls.size() == 0) {
                sendMessage(MSG_EVAL_CANNOT_GET_URL);
                return;
            }

            String evalCurrentClassName = "";

            try {
                String courseID = "";
                String url = "";

                // 评价每一门课程
                for (String name : EvalUrls.keySet()) {
                    url = EvalUrls.get(name);

                    // GET一次页面获得参数
                    Connection conn_1 = Jsoup.connect(Static.rp.baseUrl + "/" + url);
                    conn_1.header("Referer", Static.rp.referer);
                    conn_1.header("Cookies", Static.rp.cookies.toString());
                    conn_1.timeout(30000);
                    conn_1.followRedirects(false);

                    Document d1 = conn_1.get();
                    __EVENTTARGET    = d1.select("[name=__EVENTTARGET]").val();
                    __EVENTARGUMENT = d1.select("[name=__EVENTARGUMENT]").val();
                    __VIEWSTATE2     = d1.select("[name=__VIEWSTATE]").val();

                    // 根据地址获取课程ID
                    Pattern pattern = Pattern.compile("xkkh=(.*?)&");
                    Matcher matcher = pattern.matcher((String)EvalUrls.get(name));
                    if (!matcher.find()) {
                        throw new Exception("Cannot get course ID");
                    }
                    courseID = matcher.group(1);

                    Log.d("Eval Class", "正在评价课程： " + name + " [" + courseID + "]");

                    // POST页面以评价
                    evalMessage = "正在评价课程: " + name + " [" + courseID + "]";
                    sendMessage(MSG_EVAL_NEXT);

                    conn_1.data("pjkc", courseID)
                            .data("__VIEWSTATE", __VIEWSTATE2)
                            .data("__EVENTTARGET", __EVENTTARGET)
                            .data("__EVENTARGUMENT", __EVENTARGUMENT)
                            .data("DataGrid1:_ctl2:JS1", "A")
                            .data("DataGrid1:_ctl2:txtjs1", "")
                            .data("DataGrid1:_ctl3:JS1", "A")
                            .data("DataGrid1:_ctl3:txtjs1", "")
                            .data("DataGrid1:_ctl4:JS1", "A")
                            .data("DataGrid1:_ctl4:txtjs1", "")
                            .data("DataGrid1:_ctl5:JS1", "A")
                            .data("DataGrid1:_ctl5:txtjs1", "")
                            .data("DataGrid1:_ctl6:JS1", "A")
                            .data("DataGrid1:_ctl6:txtjs1", "")
                            .data("DataGrid1:_ctl7:JS1", "A")
                            .data("DataGrid1:_ctl7:txtjs1", "")
                            .data("DataGrid1:_ctl8:JS1", "A")
                            .data("DataGrid1:_ctl8:txtjs1", "")
                            .data("DataGrid1:_ctl9:JS1", "B")
                            .data("DataGrid1:_ctl9:txtjs1", "")
                            .data("pjxx", "")
                            .data("txt1", "")
                            .data("TextBox1", "")
                            .data("Button1", "");
                    Document d2 = conn_1.post();
                }

                // 提交评价
                // POST以提交评价
                evalMessage = "正在提交评价";
                sendMessage(MSG_EVAL_NEXT);
                Log.d("Eval Class","正在提交评价");
                Connection conn_1 = Jsoup.connect(Static.rp.baseUrl + "/" + url);
                conn_1.header("Referer", Static.rp.referer);
                conn_1.header("Cookies", Static.rp.cookies.toString());
                conn_1.timeout(30000);
                conn_1.followRedirects(false);
                conn_1.data("pjkc", courseID)
                        .data("__VIEWSTATE", __VIEWSTATE2)
                        .data("__EVENTTARGET", __EVENTTARGET)
                        .data("__EVENTARGUMENT", __EVENTARGUMENT)
                        .data("DataGrid1:_ctl2:JS1", "A")
                        .data("DataGrid1:_ctl2:txtjs1", "")
                        .data("DataGrid1:_ctl3:JS1", "A")
                        .data("DataGrid1:_ctl3:txtjs1", "")
                        .data("DataGrid1:_ctl4:JS1", "A")
                        .data("DataGrid1:_ctl4:txtjs1", "")
                        .data("DataGrid1:_ctl5:JS1", "A")
                        .data("DataGrid1:_ctl5:txtjs1", "")
                        .data("DataGrid1:_ctl6:JS1", "A")
                        .data("DataGrid1:_ctl6:txtjs1", "")
                        .data("DataGrid1:_ctl7:JS1", "A")
                        .data("DataGrid1:_ctl7:txtjs1", "")
                        .data("DataGrid1:_ctl8:JS1", "A")
                        .data("DataGrid1:_ctl8:txtjs1", "")
                        .data("DataGrid1:_ctl9:JS1", "B")
                        .data("DataGrid1:_ctl9:txtjs1", "")
                        .data("pjxx", "")
                        .data("txt1", "")
                        .data("TextBox1", "")
                        .data("Button2", ""); // Button2是提交评价
                Document d2 = conn_1.post();

                sendMessage(MSG_EVAL_SUCCESS);

            }
            catch (Exception e) {
                e.printStackTrace();
                evalMessage = "无法评价课程：" + evalCurrentClassName;
                sendMessage(MSG_EVAL_CANNOT_EVAL);
            }
        }
    };

    private void sendMessage(int code) {
        Message msg = new Message();
        msg.arg1 = code;
        msgHandler.sendMessage(msg);
    }
    final int MSG_EVAL_SUCCESS = 0;
    final int MSG_EVAL_CANNOT_GET_URL = 1;
    final int MSG_EVAL_CANNOT_EVAL = 2;
    final int MSG_EVAL_CANNOT_SUBMIT = 3;
    final int MSG_EVAL_NEXT = 4;
    private Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (progressDialog == null) {
                Log.e("Eval", "Progress Dialog is not created");
                return;
            }
            switch (msg.arg1) {
                case MSG_EVAL_SUCCESS:
                    progressDialog.dismiss();
                    new AlertDialog.Builder(EvalActivity.this)
                            .setMessage("评价成功")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EvalActivity.this.finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    break;
                case MSG_EVAL_CANNOT_GET_URL:
                    progressDialog.dismiss();
                    new AlertDialog.Builder(EvalActivity.this)
                            .setTitle("评价失败")
                            .setMessage("没有获取到需要评价的课程。可能是不需要评价。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EvalActivity.this.finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    break;
                case MSG_EVAL_CANNOT_EVAL:
                case MSG_EVAL_CANNOT_SUBMIT:
                    progressDialog.dismiss();
                    new AlertDialog.Builder(EvalActivity.this)
                            .setTitle("评价失败")
                            .setMessage(evalMessage)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EvalActivity.this.finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    break;
                case MSG_EVAL_NEXT:
                    progressDialog.setMessage(evalMessage);
                    break;
            }
        }
    };
}
