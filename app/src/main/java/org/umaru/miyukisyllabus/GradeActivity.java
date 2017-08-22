package org.umaru.miyukisyllabus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.drm.ProcessedData;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import GradeRelated.RequestParamters;
import ProgramFeatures.Static;

public class GradeActivity extends AppCompatActivity {
    Toolbar mToolbar;
    String year;
    String semester;
    String type;

    ListView mGradeList;
    ArrayList<HashMap<String, String>> data = new ArrayList<>();

    ProgressDialog process_dialog = null;
    ArrayList<HashMap<String, String>> result;

    final int
        CODE_GET_INFO_FAILED = 3,
        CODE_GET_INFO_COMPLETE = 4;

    private void addItem(String title, String desc) {
        HashMap<String, String> t = new HashMap<>();
        t.put("title", title);
        t.put("desc", desc);
        data.add(t);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade);

        //获取参数
        Intent b = getIntent();

        try {
            year = b.getStringExtra("year");
        }
        catch (Exception e) {
            year = "2015-2016";
        }

        try {
            semester = b.getStringExtra("semester");
        }
        catch (Exception e) {
            semester = "2";
        }

        try {
            type = b.getStringExtra("type");
        }
        catch (Exception e) {
            type = "btn_xq";
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

        this.setTitle("查询结果");

        mGradeList = (ListView)findViewById(R.id.grade_grade_list);
        mGradeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("position="+position);
                HashMap<String, String> t = result.get(position);
                new AlertDialog.Builder(GradeActivity.this)
                        .setTitle(t.get("cname"))
                        .setMessage(
                                "学年: " + t.get("year") + "\n" +
                                        "学期: " + t.get("semester") + "\n" +
                                        "课程编码: " + t.get("ccode") + "\n" +
                                        "课程名称: " + t.get("cname") + "\n" +
                                        "课程类型: " + t.get("ctype") + "\n" +
                                        "课程归属: " + t.get("cblongs") + "\n" +
                                        "分数: " + t.get("score") + "\n" +
                                        "绩点: " + t.get("point") + "\n" +
                                        "成绩: " + t.get("grade") + "\n" +
                                        "辅修标记: " + t.get("secondaryFlag") + "\n" +
                                        "补考成绩: " + t.get("addgarde") + "\n" +
                                        "重修成绩: " + t.get("restudygrade") + "\n" +
                                        "专业: " + t.get("dept") + "\n" +
                                        "备注: " + t.get("comment") + "\n" +
                                        "重修标记: " + t.get("restudyFlag")
                        )
                        .setPositiveButton("确定", null)
                        .show();
            }
        });


        process_dialog = new ProgressDialog(this);
        process_dialog.setTitle("请等待");
        process_dialog.setMessage("正在查询您的成绩。");
        process_dialog.setCancelable(false);
        process_dialog.show();

        new Thread(new t_query_grade()).start();
    }

    class t_query_grade implements Runnable {
        public void run() {
            result = QueryGrade(year, semester);
            if (result == null){
                sendMessage(CODE_GET_INFO_FAILED);
                return;
            }
            sendMessage(CODE_GET_INFO_COMPLETE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_grade, menu);
        return true;
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

    private void sendMessage(int code) {
        Message msg = new Message();
        msg.arg1 = code;
        msgHandler.sendMessage(msg);
    }

    Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case CODE_GET_INFO_COMPLETE:
                    for (HashMap<String, String> e : result) {
                        addItem(e.get("cname"), e.get("grade"));
                    }
                    SimpleAdapter adapter = new SimpleAdapter(GradeActivity.this, data, R.layout.listgroup_item, new String[]{ "title", "desc" }, new int[]{ R.id.item_title, R.id.item_desc });
                    mGradeList.setAdapter(adapter);
                    mGradeList.deferNotifyDataSetChanged();
                    process_dialog.dismiss();
                    break;
                case CODE_GET_INFO_FAILED:
                    Toast.makeText(GradeActivity.this, "获取成绩失败，请返回重试", Toast.LENGTH_SHORT).show();
                    process_dialog.dismiss();
                    break;
            }
        }
    };

    private ArrayList<HashMap<String, String>> QueryGrade(String year, String semeter) {
        String __EVENTTARGET = "";
        String __VIEWSTATE2 = "";
        String __EVENTARGUMENT = "";

        String request_url = Static.rp.baseUrl + "/" + Static.rp.urls.get("成绩查询");

        Connection conn_1 = Jsoup.connect(request_url);
        conn_1.header("Referer", Static.rp.referer);
        conn_1.header("Cookies", Static.rp.cookies.toString());
        conn_1.timeout(30000);
        conn_1.followRedirects(false);
        Document d1;
        System.out.println("referer: "+conn_1.request().header("Referer"));
        try {
            d1 = conn_1.get();
            __EVENTTARGET = d1.select("[name=__EVENTTARGET]").val();
            __EVENTARGUMENT = d1.select("[name=__EVENTARGUMENT]").val();
            __VIEWSTATE2 = d1.select("[name=__VIEWSTATE]").val();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }

        System.out.println(d1.html());

        ArrayList<HashMap<String, String>> t1 = new ArrayList<HashMap<String, String>>();
        try {
            Connection conn_2 = Jsoup.connect(request_url)
                    .header("Referer", Static.rp.referer)
                    .header("Cookies", Static.rp.cookies.toString())
                    .data("__EVENTTARGET", __EVENTTARGET)
                    .data("__EVENTARGUMENT", __EVENTARGUMENT)
                    .data("__VIEWSTATE", __VIEWSTATE2)
                    .data("hidLanguage", "")
                    .data("ddlXN", year)
                    .data("ddlXQ", semeter)
                    .data("ddl_kcxz", "")
                    .data(type, "%D1%A7%C6%DA%B3%C9%BC%A8");
            conn_2.timeout(30000);
            Document d = conn_2.post();
            System.out.println(d.html());

            String[] keys = { "year", "semester", "ccode", "cname", "ctype", "cblongs", "score", "point", "grade", "secondaryFlag", "addgarde", "restudygrade", "dept", "comment", "restudyFlag" };
            String[] keyt = { "学年",  "学期", "课程编码", "课程名称", "课程类型", "课程归属",  "分数",    "绩点",   "成绩",   "辅修标记", "补考成绩",   "重修成绩",        "专业",   "备注", "重修标记" };

            Pattern p = Pattern.compile("(?<=<td>).*?(?=<\\/td>)");
            Elements es = d.select(".datelist tr");
            for (int i=1; i<es.size(); i++){
                HashMap<String, String> t = new HashMap<String, String>();
                Matcher m = p.matcher(es.get(i).html());
                for (int j=0; m.find(); j++) {
                    t.put(keys[j], m.group(0).replace("&nbsp;", ""));
                }
                t1.add(t);
                //addItem(t.get("cname"), t.get("score"));
            }
            return t1;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
