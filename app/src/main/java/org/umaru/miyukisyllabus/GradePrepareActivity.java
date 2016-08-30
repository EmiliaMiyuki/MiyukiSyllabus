package org.umaru.miyukisyllabus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import GradeRelated.RequestParamters;

public class GradePrepareActivity extends AppCompatActivity {
    Toolbar mToolbar;

    EditText txt_year;
    EditText txt_semester;
    Button btn_grade_prepare_current;
    Button btn_semester_grade;
    Button btn_year_grade;
    Button btn_all_grade;

    RequestParamters rp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_prepare);

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

        this.setTitle("成绩查询");

        txt_year = (EditText)findViewById(R.id.grade_prepare_year_edit);
        txt_semester = (EditText)findViewById(R.id.grade_prepare_semester_edit);

        btn_grade_prepare_current = (Button)findViewById(R.id.grade_prepare_current);
        btn_semester_grade = (Button)findViewById(R.id.grade_prepare_semester);
        btn_year_grade = (Button)findViewById(R.id.grade_prepare_year);
        btn_all_grade = (Button)findViewById(R.id.grade_prepare_all);

        btn_grade_prepare_current.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                String year = "";
                String semester = "";
                int iy = 1900 + now.getYear();
                int im = now.getMonth();
                if (im >= 9) {
                    year = "" + (iy - 1) + "-" + iy;
                    semester = "2";
                }
                else if (im == 1) {
                    year = "" + (iy - 2) + "-" + (iy - 1);
                    semester = "2";
                }
                else if (im <= 6){
                    year = "" + (iy - 1) + "-" + iy;
                    semester = "1";
                }
                else {
                    year = "" + (iy - 1) + "-" + iy;
                    semester = "2";
                }
                System.out.println("year: " + year + ", semester: " + semester);
                Intent intent = new Intent(GradePrepareActivity.this, GradeActivity.class);
                intent.putExtra("year", year);
                intent.putExtra("semester", semester);
                intent.putExtra("type", "btn_xq");
                startActivity(intent);
            }
        });

        btn_semester_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_year.getText().toString().equals("") || txt_semester.getText().toString().equals("")){
                    Toast.makeText(GradePrepareActivity.this, "请完整输入学年和学期。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern p = Pattern.compile("201\\d\\-201\\d");
                Matcher m = p.matcher(txt_year.getText().toString());
                if (!m.find()) {
                    Toast.makeText(GradePrepareActivity.this, "请输入正确格式的学年。", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Integer.parseInt(txt_semester.getText().toString());
                }
                catch (Exception e){
                    Toast.makeText(GradePrepareActivity.this, "请输入正确格式的学期。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GradePrepareActivity.this, GradeActivity.class);
                intent.putExtra("year", txt_year.getText().toString());
                intent.putExtra("semester", txt_semester.getText().toString());
                intent.putExtra("type", "btn_xq");
                startActivity(intent);
            }
        });

        btn_year_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_year.getText().toString().equals("")){
                    Toast.makeText(GradePrepareActivity.this, "请输入学年和学期。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Pattern p = Pattern.compile("201\\d\\-201\\d");
                Matcher m = p.matcher(txt_year.getText().toString());
                if (!m.find()) {
                    Toast.makeText(GradePrepareActivity.this, "请输入正确格式的学年。", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GradePrepareActivity.this, GradeActivity.class);
                intent.putExtra("year", txt_year.getText().toString());
                intent.putExtra("semester", "");
                intent.putExtra("type", "btn_xn");
                startActivity(intent);
            }
        });

        btn_all_grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GradePrepareActivity.this, GradeActivity.class);
                intent.putExtra("year", "");
                intent.putExtra("semester", "");
                intent.putExtra("type", "btn_zcj");
                startActivity(intent);
            }
        });
    }

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
