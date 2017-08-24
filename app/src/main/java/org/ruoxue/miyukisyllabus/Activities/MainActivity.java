package org.ruoxue.miyukisyllabus.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.ruoxue.miyukisyllabus.UIComponents.AppCompatActivityWithSettings;
import org.ruoxue.miyukisyllabus.UIComponents.ManagedApplication;
import org.ruoxue.miyukisyllabus.UIComponents.NavigationDrawerCallbacks;
import org.ruoxue.miyukisyllabus.UIComponents.NavigationDrawerFragment;
import org.ruoxue.miyukisyllabus.R;
import org.ruoxue.miyukisyllabus.UIComponents.CourseListAdapter;
import org.ruoxue.miyukisyllabus.UIComponents.CourseListOnClick;
import org.ruoxue.miyukisyllabus.Data.CourseData;
import org.ruoxue.miyukisyllabus.Data.CourseDataDAO;
import org.ruoxue.miyukisyllabus.Data.SettingsDAO;
import org.ruoxue.miyukisyllabus.Data.SettingsDTO;
import org.ruoxue.miyukisyllabus.Util.Static;

public class MainActivity extends AppCompatActivityWithSettings
        implements NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    // UI - 课程表列表
    private ListView list_daily_course;

    // 数据
    private CourseDataDAO dao = new CourseDataDAO();
    private List<CourseData> class_list = null;

    private SettingsDAO settingDao = new SettingsDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ManagedApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        // 初始化
        dao.createList();
        settingDao.createTable();

        Static.PATH_DATA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+this.getPackageName();
        Static.PATH_FILES_DIR = Static.PATH_DATA_DIR + "/files/";
        Static.PATH_FILE_CHECKCODE = Static.PATH_DATA_DIR + "/files/checkCode.gif";

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Apply for WRITE_EXTERNAL_STORAGE permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Static.PERMISSION_WRITE_EXTERNAL_STORAGE);
        }
        else {
            Static.gotWriteExtenralPermission = true;
            init();
        }

    }

    private void init() {
        Static.checkResourceDir();
        settingDao.loadSettings();

        mNavigationDrawerFragment.setUserData((SettingsDTO.getUserName() == null || SettingsDTO.getUserName().equals("")) ? "未设置" : SettingsDTO.getUserName(), "", BitmapFactory.decodeResource(getResources(), R.drawable.profile_maki));

        try {
            if (!SettingsDTO.getRbackgoundImg().equals("")) {
                //Toast.makeText(MainActivity.this, "Background: " + ProgramConfig.background_image_url, Toast.LENGTH_LONG).show();
                mNavigationDrawerFragment.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(SettingsDTO.getRbackgoundImg())));
            }
        }
        catch (Exception e) {
            mNavigationDrawerFragment.setBackground(getResources().getDrawable(R.drawable.wallpaper));
            e.printStackTrace();
        }

        try {
            if (!SettingsDTO.getAvaterImg().equals("")) {
                //Toast.makeText(MainActivity.this, "Profile: " + ProgramConfig.profile_image_url, Toast.LENGTH_LONG).show();
                mNavigationDrawerFragment.setAvater(BitmapFactory.decodeFile(SettingsDTO.getAvaterImg()));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        // 检查是否是第一次初始化
        if (SettingsDTO.isFirstInit()) {
            settingDao.setSetting(settingDao.KEY_FIRST_INIT, "false");
            SettingsDTO.setFirstInit(false);

            new android.support.v7.app.AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("首次运行程序")
                    .setMessage("这是您首次运行此程序，您可以选择导入或手动编辑课程表。")
                    .setPositiveButton("导入课程表", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("手动编辑", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(MainActivity.this, SyllabusActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNeutralButton("不用了", null)
                    .show();
        }
        else if (SettingsDTO.isWelcome()) {
            int hour = new Date().getHours();
            String greeting = "";
            if (hour < 5) greeting = "午夜好";
            else if (hour < 9) greeting = "早安";
            else if (hour < 12) greeting = "上午好";
            else if (hour < 13) greeting = "中午好";
            else if (hour < 18) greeting = "下午好";
            else greeting = "晚上好";
            Toast.makeText(this, greeting + "，" + SettingsDTO.getUserName(), Toast.LENGTH_SHORT).show();
        }

        refreshList();
    }

    protected void refreshList() {
        // Get today's course
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int day = (cal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7;
        class_list = dao.getDailyCourse(SettingsDTO.getCurrentWeek(), day);

        this.setTitle("今日课程(" + class_list.size() + ")");

        // 初始化list
        list_daily_course = (ListView)findViewById(R.id.list_daily_course);

        CourseListAdapter list_dc_adapter = new CourseListAdapter(this, class_list, new CourseListOnClick() {
            @Override
            public void onClick(View v, final int position) {
                CourseData t = (CourseData)class_list.get(position);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle((String)class_list.get(position).getName())
                        .setMessage("上课地点："+t.getClassroom()+"\n教师："+t.getTeacher()+"\n上课周数："+t.getStartWeek()+"到"+t.getEndWeek()+"周 ("+t.getSpecialTimeString()+")")
                        .setPositiveButton("确定", null)
                        .setNegativeButton("在课程表中查看", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, SyllabusActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putInt("index", 0);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        })
                        .show();
            }
        });

        list_daily_course.setAdapter(list_dc_adapter);
        list_daily_course.deferNotifyDataSetChanged();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
        Intent intent;
        Bundle bundle;
        switch (position) {
            case 0:
                break;
            case 1:
                intent = new Intent(MainActivity.this, SyllabusActivity.class);
                startActivity(intent);
                break;
            case 2:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                bundle = new Bundle();
                bundle.putString("destination", "import");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                bundle = new Bundle();
                bundle.putString("destination", "score");
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); //设置不要刷新将要跳到的界面
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //它可以关掉所要到的界面中间的activity
                startActivityForResult(intent, Static.RESULT_CODE_SETTINGS);
                break;
            case 5:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                break;
            default:
                Toast.makeText(this, "(ERROR 0) 暂未实现此功能。", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //alert(new Integer(grantResults.length).toString(), new Integer(grantResults[0]).toString() + ", permission=" + PackageManager.PERMISSION_GRANTED);
        switch (requestCode) {
            case Static.PERMISSION_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Static.gotWriteExtenralPermission = true;
                    init();
                } else {
                    //没有授予权限，程序仍然可以运行，但某些功能被禁用
                    init();
                }
                return;
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            settingDao.loadSettings();
            refreshList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int state = data.getIntExtra("state", 0);
        settingDao.loadSettings();
        //Toast.makeText(MainActivity.this, "设置已经更改, STATE="+state, Toast.LENGTH_SHORT).show();

        if (Static.getState(state, Static.RETVAL_SETTING_DISPLAY_NAME)){
            mNavigationDrawerFragment.setName(SettingsDTO.getUserName().equals("") ? "未设置" : SettingsDTO.getUserName());
        }
        if (Static.getState(state, Static.RETVAL_SETTING_CHANGE_PROFILE)){
            if (!SettingsDTO.getAvaterImg().equals(""))
                mNavigationDrawerFragment.setAvater(new BitmapDrawable(BitmapFactory.decodeFile(SettingsDTO.getAvaterImg())).getBitmap());
            else
                mNavigationDrawerFragment.setAvater(((BitmapDrawable)getResources().getDrawable(R.drawable.profile_maki)).getBitmap());
        }
        if (Static.getState(state, Static.RETVAL_SETTING_CHANGE_BACKGROUND)){
            if (!SettingsDTO.getRbackgoundImg().equals(""))
                mNavigationDrawerFragment.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(SettingsDTO.getRbackgoundImg())));
            else
                mNavigationDrawerFragment.setBackground(getResources().getDrawable(R.drawable.wallpaper));
        }
        if (Static.getState(state, Static.RETVAL_SETTING_NIGHT_MODE)) {
            Toast.makeText(MainActivity.this, "夜间模式被更改", Toast.LENGTH_SHORT).show();
        }
        if (Static.getState(state, Static.RETVAL_COURSE_LIST_CHANGED)) {
            Toast.makeText(MainActivity.this, "课程表被更改", Toast.LENGTH_SHORT).show();
        }

    }
}
