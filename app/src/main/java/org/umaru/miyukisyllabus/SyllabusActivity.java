package org.umaru.miyukisyllabus;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Space;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.lang.reflect.Field;

import ProgramFeatures.CourseInfo;
import ProgramFeatures.ProgramConfig;
import ProgramFeatures.Static;

public class SyllabusActivity extends AppCompatActivity {
    Toolbar mToolbar;
    TextView[] classes = new TextView[25];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syllabus);

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

        this.setTitle("课程表");

        classes[0] = (TextView)findViewById(R.id.sy11);
        classes[1] = (TextView)findViewById(R.id.sy12);
        classes[2] = (TextView)findViewById(R.id.sy13);
        classes[3] = (TextView)findViewById(R.id.sy14);
        classes[4] = (TextView)findViewById(R.id.sy15);
        classes[5] = (TextView)findViewById(R.id.sy21);
        classes[6] = (TextView)findViewById(R.id.sy22);
        classes[7] = (TextView)findViewById(R.id.sy23);
        classes[8] = (TextView)findViewById(R.id.sy24);
        classes[9] = (TextView)findViewById(R.id.sy25);
        classes[10] = (TextView)findViewById(R.id.sy31);
        classes[11] = (TextView)findViewById(R.id.sy32);
        classes[12] = (TextView)findViewById(R.id.sy33);
        classes[13] = (TextView)findViewById(R.id.sy34);
        classes[14] = (TextView)findViewById(R.id.sy35);
        classes[15] = (TextView)findViewById(R.id.sy41);
        classes[16] = (TextView)findViewById(R.id.sy42);
        classes[17] = (TextView)findViewById(R.id.sy43);
        classes[18] = (TextView)findViewById(R.id.sy44);
        classes[19] = (TextView)findViewById(R.id.sy45);
        classes[20] = (TextView)findViewById(R.id.sy51);
        classes[21] = (TextView)findViewById(R.id.sy52);
        classes[22] = (TextView)findViewById(R.id.sy53);
        classes[23] = (TextView)findViewById(R.id.sy54);
        classes[24] = (TextView)findViewById(R.id.sy55);

        updateCourseList();
    }

    void updateCourseList() {
        for (int i=0; i<25; i++) {
            final int curr_i = i;
            boolean has_class = ProgramConfig.courses.containsKey((i / 5) * 7 + i % 5);
            if (!has_class) {
                classes[i].setText("");
                classes[i].setBackgroundColor(Color.TRANSPARENT);
                classes[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(SyllabusActivity.this)
                                .setTitle("这个时间没有课程")
                                .setMessage("你可以手动添加在该时间的课程，也可以从教务系统导入课程。")
                                .setNegativeButton("导入", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SyllabusActivity.this, LoginActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putString("destination", "import");
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                })
                                .setPositiveButton("手动添加", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        LayoutInflater inflater = LayoutInflater.from(SyllabusActivity.this);
                                        View view = inflater.inflate(R.layout.add_new_course_dialog, null);

                                        final EditText txt_name = (EditText) view.findViewById(R.id.syllabus_edit_name);
                                        final EditText txt_week = (EditText) view.findViewById(R.id.syllabus_edit_week);
                                        final Spinner txt_special_time = (Spinner) view.findViewById(R.id.syllabus_edit_special_time);
                                        final EditText txt_place = (EditText) view.findViewById(R.id.syllabus_edit_place);
                                        final EditText txt_teacher = (EditText) view.findViewById(R.id.syllabus_edit_teacher);

                                        new AlertDialog.Builder(SyllabusActivity.this)
                                                .setTitle("添加课程")
                                                .setView(view)
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        CourseInfo ci = new CourseInfo();
                                                        JSONObject obj = new JSONObject();
                                                        try {
                                                            if (txt_week.getText().toString().equals(""))
                                                                txt_week.setText(txt_week.getHint());
                                                            ci.startWeek = Integer.parseInt(txt_week.getText().toString().split("\\-")[0]);
                                                            ci.endWeek = Integer.parseInt(txt_week.getText().toString().split("\\-")[1]);
                                                        } catch (Exception e) {
                                                            Toast.makeText(SyllabusActivity.this, "请输入正确的课程起止时间。", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        ci.place = txt_place.getText().toString();
                                                        ci.name = txt_name.getText().toString();
                                                        if (ci.name.equals("")) {
                                                            Toast.makeText(SyllabusActivity.this, "请输入课程名。", Toast.LENGTH_SHORT).show();

                                                            return;
                                                        }
                                                        ci.special_time = txt_special_time.getSelectedItemPosition();
                                                        ci.index = (curr_i / 5) * 7 + curr_i % 5;
                                                        ci.teacher = txt_teacher.getText().toString();
                                                        ProgramConfig.putCourseInfo(ci);
                                                        Static.WriteSettings();
                                                        ProgramConfig.courses.put((curr_i / 5) * 7 + curr_i % 5, ci);
                                                        updateCourseList();
                                                    }
                                                })
                                                .setNegativeButton("取消", null)
                                                .setCancelable(false)
                                                .show();
                                    }
                                })
                                .show();
                    }
                });
                continue;
            }
            final CourseInfo t = ProgramConfig.courses.get((i / 5) * 7 + i % 5);
            classes[i].setBackgroundColor(genRandColor());
            classes[i].setTextColor(getResources().getColor(R.color.sy_text_class));
            classes[i].setText(t.name + "\n" + t.place);
            classes[i].setGravity(Gravity.CENTER);
            classes[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            classes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(SyllabusActivity.this)
                            .setTitle(t.name)
                            .setMessage("上课周数："+t.startWeek+"到"+t.endWeek+"周\n"+
                                    "教师："+t.teacher+"\n教室："+t.place)
                            .setNegativeButton("编辑", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //// TODO: 2016/8/21 编辑
                                    LayoutInflater inflater = LayoutInflater.from(SyllabusActivity.this);
                                    View view = inflater.inflate(R.layout.add_new_course_dialog, null);

                                    final EditText txt_name = (EditText) view.findViewById(R.id.syllabus_edit_name);
                                    final EditText txt_week = (EditText) view.findViewById(R.id.syllabus_edit_week);
                                    final Spinner txt_special_time = (Spinner) view.findViewById(R.id.syllabus_edit_special_time);
                                    final EditText txt_place = (EditText) view.findViewById(R.id.syllabus_edit_place);
                                    final EditText txt_teacher = (EditText) view.findViewById(R.id.syllabus_edit_teacher);

                                    txt_name.setText(t.name);
                                    txt_week.setText(t.startWeek + "-" + t.endWeek);
                                    txt_place.setText(t.place);
                                    txt_special_time.setSelection(t.special_time);
                                    txt_teacher.setText(t.teacher);

                                    new AlertDialog.Builder(SyllabusActivity.this)
                                            .setTitle("编辑课程")
                                            .setView(view)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    int tmp = ProgramConfig.findCourseObjectByIndex(t.index);
                                                    System.out.println(ProgramConfig.course.length());
                                                    ProgramConfig.course.remove(tmp);
                                                    ProgramConfig.courses.remove(t.index);
                                                    System.out.println(ProgramConfig.course.length());

                                                    CourseInfo ci = new CourseInfo();
                                                    JSONObject obj = new JSONObject();
                                                    try {
                                                        if (txt_week.getText().toString().equals(""))
                                                            txt_week.setText(txt_week.getHint());
                                                        ci.startWeek = Integer.parseInt(txt_week.getText().toString().split("\\-")[0]);
                                                        ci.endWeek = Integer.parseInt(txt_week.getText().toString().split("\\-")[1]);
                                                    } catch (Exception e) {
                                                        Toast.makeText(SyllabusActivity.this, "请输入正确的课程起止时间。", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    ci.place = txt_place.getText().toString();
                                                    ci.name = txt_name.getText().toString();
                                                    if (ci.name.equals("")) {
                                                        Toast.makeText(SyllabusActivity.this, "请输入课程名。", Toast.LENGTH_SHORT).show();

                                                        return;
                                                    }
                                                    ci.special_time = txt_special_time.getSelectedItemPosition();
                                                    ci.index = (curr_i / 5) * 7 + curr_i % 5;
                                                    ci.teacher = txt_teacher.getText().toString();
                                                    ProgramConfig.putCourseInfo(ci);
                                                    Static.WriteSettings();
                                                    ProgramConfig.courses.put((curr_i / 5) * 7 + curr_i % 5, ci);
                                                    updateCourseList();
                                                }
                                            })
                                            .setNegativeButton("取消", null)
                                            .setNeutralButton("删除", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Static.canCloseDialog(dialog, false);
                                                    final int tmp = ProgramConfig.findCourseObjectByIndex(t.index);
                                                    if (tmp == -1) {
                                                        Toast.makeText(SyllabusActivity.this, "该课程不存在。", Toast.LENGTH_SHORT).show();
                                                        Static.canCloseDialog(dialog, true);
                                                        return;
                                                    }
                                                    new AlertDialog.Builder(SyllabusActivity.this)
                                                            .setTitle("确认删除")
                                                            .setMessage("是否确认删除？")
                                                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    System.out.println(ProgramConfig.course.length());
                                                                    ProgramConfig.course.remove(tmp);
                                                                    ProgramConfig.courses.remove(t.index);
                                                                    System.out.println(ProgramConfig.course.length());
                                                                    Static.WriteSettings();
                                                                    updateCourseList();
                                                                    Static.canCloseDialog(dialog, true);
                                                                }
                                                            })
                                                            .setNegativeButton("否", null)
                                                            .show();
                                                }
                                            })
                                            .setCancelable(false)
                                            .show();
                                }
                            })
                            .setPositiveButton("确定", null)
                            .show();
                }
            });
        }
    }

    protected int genRandColor() {
        System.out.println((int)(Math.random()*4));
        return getResources().getColor(new int[] { R.color.sy_class_1, R.color.sy_class_2, R.color.sy_class_3, R.color.sy_class_4 }[(int)(Math.random()*4)]);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_syllabus, menu);
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
