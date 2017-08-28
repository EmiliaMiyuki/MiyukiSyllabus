package org.ruoxue.miyukisyllabus.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.List;

import org.ruoxue.miyukisyllabus.Data.CourseData;
import org.ruoxue.miyukisyllabus.Data.CourseDataDAO;
import org.ruoxue.miyukisyllabus.Data.SettingsDTO;
import org.ruoxue.miyukisyllabus.R;
import org.ruoxue.miyukisyllabus.UIComponents.AppCompatActivityWithSettings;
import org.ruoxue.miyukisyllabus.Util.Static;

public class SyllabusActivity extends AppCompatActivityWithSettings {
    Toolbar mToolbar;
    TextView[] classes = new TextView[25];
    LinearLayout mSyllabusWindow;

    CourseDataDAO dao = new CourseDataDAO();

    int current_shown_week;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        current_shown_week = SettingsDTO.getCurrentWeek();
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

        if (!SettingsDTO.getSyllabusBackgroundImg().equals("")) {
            try {
                mSyllabusWindow = (LinearLayout) findViewById(R.id.syllabus_window);
                mSyllabusWindow.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(SettingsDTO.getSyllabusBackgroundImg())));
                mToolbar.getBackground().setAlpha(183);
            }
            catch (Exception e) {
                Toast.makeText(this, "Can not load file: " + SettingsDTO.getSyllabusBackgroundImg(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        updateCourseList();
    }

    void updateCourseList() {
        updateCourseList(SettingsDTO.getCurrentWeek());
    }

    void updateCourseList(int week) {
        List<CourseData> lst = dao.getWeeklyCourse(week);
        SyllabusActivity.this.setTitle("第"+week+"周"+ (SettingsDTO.getCurrentWeek() != week ? "/非本周":"") + "");

        boolean filled[] = new boolean[25];
        for (int i=0; i<25; i++) filled[i] = false;

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        @ColorInt int background_color = typedValue.data;

        int noClassBlkTranparency = SettingsDTO.getSyllabusBackgroundImg().equals("")?220:0;

        for (final CourseData data : lst) {
            int i = (data.getWeekday()) + data.getCourseIndex() *5;

            if (i >= 25) continue;

            filled[i] = true;

            classes[i].setBackgroundColor(background_color);
            classes[i].getBackground().setAlpha(230);
            classes[i].setClickable(true);
            classes[i].setTextColor(getResources().getColor(R.color.sy_text_class));
            classes[i].setText(data.getName() + "\n" + data.getClassroom());
            classes[i].setGravity(Gravity.CENTER);
            classes[i].setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
            classes[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(SyllabusActivity.this)
                            .setTitle(data.getName())
                            .setMessage("上课周数："+data.getStartWeek()+"到"+data.getEndWeek()+"周\n"+
                                    "教师："+data.getTeacher()+"\n教室："+data.getClassroom())
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

                                    txt_name.setText(data.getName());
                                    txt_week.setText(data.getStartWeek() + "-" + data.getEndWeek());
                                    txt_place.setText(data.getClassroom());
                                    txt_special_time.setSelection(data.getSpecialTime() == 3?0:data.getSpecialTime());
                                    txt_teacher.setText(data.getTeacher());

                                    new AlertDialog.Builder(SyllabusActivity.this)
                                            .setTitle("编辑课程")
                                            .setView(view)
                                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    JSONObject obj = new JSONObject();
                                                    try {
                                                        if (txt_week.getText().toString().equals(""))
                                                            txt_week.setText(txt_week.getHint());
                                                        data.setStartWeek(Integer.parseInt(txt_week.getText().toString().split("\\-")[0]));
                                                        data.setEndWeek(Integer.parseInt(txt_week.getText().toString().split("\\-")[1]));
                                                    } catch (Exception e) {
                                                        Toast.makeText(SyllabusActivity.this, "请输入正确的课程起止时间。", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    data.setClassroom(txt_place.getText().toString());
                                                    data.setName(txt_name.getText().toString());
                                                    if (data.getName().equals("")) {
                                                        Toast.makeText(SyllabusActivity.this, "请输入课程名。", Toast.LENGTH_SHORT).show();

                                                        return;
                                                    }

                                                    int special_time = txt_special_time.getSelectedItemPosition();
                                                    if (special_time == 0) special_time = 3;  // 每周都上的课是3，但选择的index是0
                                                    data.setSpecialTime(special_time);

                                                    data.setCourseIndex(data.getCourseIndex());
                                                    data.setTeacher(txt_teacher.getText().toString());

                                                    dao.insertCourse(data);
                                                    updateCourseList();
                                                }
                                            })
                                            .setNegativeButton("取消", null)
                                            .setNeutralButton("删除", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Static.canCloseDialog(dialog, false);
                                                    if (data == null) {
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
                                                                    dao.deleteCourse(data);
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

        for (int i=0 ;i<25; i++) {
            if (filled[i]) continue;
            final int index = i;
            final CourseData data = new CourseData();
            classes[i].setText("");
            classes[i].setBackgroundColor(Color.WHITE);
            classes[i].getBackground().setAlpha(noClassBlkTranparency);
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
                                                    CourseData d = new CourseData();
                                                    try {
                                                        if (txt_week.getText().toString().equals(""))
                                                            txt_week.setText(txt_week.getHint());
                                                        d.setStartWeek(Integer.parseInt(txt_week.getText().toString().split("\\-")[0]));
                                                        d.setEndWeek(Integer.parseInt(txt_week.getText().toString().split("\\-")[1]));
                                                    } catch (Exception e) {
                                                        Toast.makeText(SyllabusActivity.this, "请输入正确的课程起止时间。", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    d.setClassroom(txt_place.getText().toString());
                                                    d.setName(txt_name.getText().toString());
                                                    if (d.getName().equals("")) {
                                                        Toast.makeText(SyllabusActivity.this, "请输入课程名。", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }

                                                    int special_time = txt_special_time.getSelectedItemPosition();
                                                    if (special_time == 0) special_time = 3;  // 每周都上的课是3，但选择的index是0
                                                    d.setSpecialTime(special_time);

                                                    d.setCourseIndex(index / 5);
                                                    d.setWeekday(index % 5);
                                                    d.setTeacher(txt_teacher.getText().toString());

                                                    if (!dao.insertCourse(d))
                                                        Toast.makeText(SyllabusActivity.this, "修改课程失败", Toast.LENGTH_SHORT).show();
                                                    System.out.println("Alter class list");

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
        }

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
        if (id == R.id.action_choose_week) {
            final NumberPicker value = new NumberPicker(SyllabusActivity.this);
            value.setMinValue(1);
            value.setMaxValue(30);
            value.setValue(current_shown_week);
            new android.app.AlertDialog.Builder(SyllabusActivity.this)
                    .setTitle("选择要查看的周数")
                    .setView(value)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int week = value.getValue();
                            current_shown_week = week;
                            updateCourseList(week);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .setNeutralButton("返回本周", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            current_shown_week = SettingsDTO.getCurrentWeek();
                            updateCourseList(current_shown_week);
                        }
                    })
                    .show();
            return true;
        }
        else if(id == R.id.action_last_week) {
            if (current_shown_week - 1 < 1) {
                Toast.makeText(this, "已经是第一周", Toast.LENGTH_SHORT).show();
                return true;
            }

            current_shown_week--;
            updateCourseList(current_shown_week);
            return true;
        }
        else if (id == R.id.action_next_week) {
            if (current_shown_week + 1 > 30) {
                Toast.makeText(this, "已经是最大周数", Toast.LENGTH_SHORT).show();
                return true;
            }

            current_shown_week++;
            updateCourseList(current_shown_week);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
