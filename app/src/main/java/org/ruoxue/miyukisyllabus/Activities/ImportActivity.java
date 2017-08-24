package org.ruoxue.miyukisyllabus.Activities;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.ruoxue.miyukisyllabus.Data.CourseData;
import org.ruoxue.miyukisyllabus.Data.CourseDataDAO;
import org.ruoxue.miyukisyllabus.R;
import org.ruoxue.miyukisyllabus.UIComponents.AppCompatActivityWithSettings;
import org.ruoxue.miyukisyllabus.Util.Static;

public class ImportActivity extends AppCompatActivityWithSettings {
    Toolbar mToolbar;
    Button btn_from_personal;
    Button btn_from_major;

    ProgressDialog progressDialog;

    CourseDataDAO dao = new CourseDataDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);

        btn_from_major = (Button)findViewById(R.id.btn_import_from_major);
        btn_from_personal = (Button)findViewById(R.id.btn_import_from_personal);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.setTitle("导入课表");

        btn_from_personal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(ImportActivity.this);
                progressDialog.setTitle("请等待");
                progressDialog.setMessage("正在获取课程表");
                progressDialog.setCancelable(false);
                progressDialog.show();

                new Thread(t_get_course_list_from_personal).start();
            }
        });
    }

    public Runnable t_get_course_list_from_personal = new Runnable() {
        @Override
        public void run() {
            boolean result = getCourseList(Static.rp.baseUrl + "/" + Static.rp.urls.get("学生个人课表"));
            /// TODO: 更新
            progressDialog.dismiss();
            sendMessage(result ? E_SUCCESS : E_FAILED);
        }
    };

    public boolean getCourseList(String req_url) {
        Elements classes_tmp;
        try {
            Connection conn_1 = Jsoup.connect(req_url);
            conn_1.header("Referer", Static.rp.referer);
            conn_1.header("Cookies", Static.rp.cookies.toString());
            conn_1.timeout(30000);
            conn_1.followRedirects(false);

            Document doc_class = conn_1.get();

            classes_tmp = doc_class.select("#Table1 tr");
            System.out.println(classes_tmp.size());

            dao.clearData();
            for (int i=0; i<5; i++) {
                Element e = classes_tmp.get((i+1)*2);

                Pattern p = Pattern.compile("(?<=<td(( class=\"noprint\"){0,1}) align=\"Center\"(( rowspan=\"2\"){0,1})(( width=\"7%\"){0,1})?>).*?(?=<\\/td1?>)");
                Matcher m = p.matcher(e.html());

                for (int j=0; j<7; j++) {
                    if (m.find()){
                        String t = m.group(0).replace("<br>", "\n").replace("&nbsp;", " ");
                        getCourseInfo(t, i, j);
                    }
                    else {
                        System.out.println("Does not match: "+(i*7+j));
                    }
                }
            }
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    void getCourseInfo(String src, int index, int weekday) {
        Pattern p = Pattern.compile("(.*?)\\n周(.*?)?第(\\d),\\d节\\{第(\\d{1,2})\\-(\\d{1,2})周(\\|(([单|双])周))?\\}\\n(.*)?\\n(.*)");
        Matcher m = p.matcher(src);

        while (m.find()) {
            CourseData t = new CourseData();
            t.setName(m.group(1));
            t.setCourseIndex(index);
            t.setWeekday(weekday);
            t.setStartWeek(Integer.parseInt(m.group(4)));
            t.setEndWeek(Integer.parseInt(m.group(5)));
            String st = "" + m.group(8);
            t.setSpecialTime(st.equals("单") ? 1 : st.equals("双") ? 2 : 3);
            t.setTeacher(m.group(9));
            t.setClassroom(m.group(10));

            System.out.println("课程名称: " + t.getName());
            System.out.println("上课时间: " + "周" + t.getWeekday() + "第" + t.getCourseIndex() + "节");

            if (!dao.insertCourse(t))
                Toast.makeText(ImportActivity.this, "获取课表时发生错误 (code 1)", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage(int code) {
        Message msg = new Message();
        msg.arg1 = code;
        msgHandler.sendMessage(msg);
    }

    final int E_FAILED = 1,
        E_SUCCESS = 2;

    Handler msgHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case E_FAILED:
                    Toast.makeText(ImportActivity.this, "获取课表失败，请重试", Toast.LENGTH_SHORT).show();
                    break;
                case E_SUCCESS:
                    Toast.makeText(ImportActivity.this, "获取课表成功", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
