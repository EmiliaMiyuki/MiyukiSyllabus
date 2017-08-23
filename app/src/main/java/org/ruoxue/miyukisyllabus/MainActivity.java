package org.ruoxue.miyukisyllabus;

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
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import CourseList.CourseListAdapter;
import CourseList.CourseListOnClick;
import Data.CourseData;
import Data.CourseDataDAO;
import ProgramFeatures.ProgramConfig;
import ProgramFeatures.Static;

public class MainActivity extends ActionBarActivity
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        // 初始化
        dao.createList();

        Static.PATH_DATA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Android/data/"+this.getPackageName();
        Static.PATH_CONFIG_FILE = Static.PATH_DATA_DIR + "/config.json";
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
            init();
        }

    }

    private void init() {
        Static.checkResourceDir();
        Static.loadConfig();
        System.out.println("display-name: "+(ProgramConfig.display_name == null));
        mNavigationDrawerFragment.setUserData((ProgramConfig.display_name == null || ProgramConfig.display_name.equals("")) ? "未设置" : ProgramConfig.display_name, "", BitmapFactory.decodeResource(getResources(), R.drawable.profile_maki));

        try {
            if (!ProgramConfig.background_image_url.equals("")) {
                //Toast.makeText(MainActivity.this, "Background: " + ProgramConfig.background_image_url, Toast.LENGTH_LONG).show();
                mNavigationDrawerFragment.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(ProgramConfig.background_image_url)));
            }
        }
        catch (Exception e) {
            mNavigationDrawerFragment.setBackground(getResources().getDrawable(R.drawable.wallpaper));
            e.printStackTrace();
        }

        try {
            if (!ProgramConfig.profile_image_url.equals("")) {
                //Toast.makeText(MainActivity.this, "Profile: " + ProgramConfig.profile_image_url, Toast.LENGTH_LONG).show();
                mNavigationDrawerFragment.setAvater(BitmapFactory.decodeFile(ProgramConfig.profile_image_url));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        if (ProgramConfig.first_initial) {
            ProgramConfig.json.remove("first_initial");
            try {
                ProgramConfig.json.put("first_initial", false);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            Static.WriteSettings();
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
        else if (ProgramConfig.welcome) {
            Toast.makeText(this, "欢迎你" + ProgramConfig.display_name, Toast.LENGTH_SHORT).show();
        }

        refreshList();
    }

    protected void refreshList() {
        // Get today's course
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int day = (cal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7;
        class_list = dao.getDailyCourse(ProgramConfig.getCurrentWeek(), day);

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
                    //Create directory if dir is not exised
                    //alert("获得了权限", "已经获得权限")
                    init();
                } else {

                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setCancelable(false)
                            .setTitle("没有获得读写外部存储权限")
                            .setMessage("程序请求外部存储权限用于保存设置信息和课程表信息的配置文件，程序没有获得读写外部存储的权限，程序将会退出。请重新打开程序并授权该权限。")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    System.exit(-2);
                                }
                            }).show();
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
            refreshList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int state = data.getIntExtra("state", 0);
        Static.loadConfig();
        //Toast.makeText(MainActivity.this, "设置已经更改, STATE="+state, Toast.LENGTH_SHORT).show();

        if (Static.getState(state, Static.RETVAL_SETTING_DISPLAY_NAME)){
            mNavigationDrawerFragment.setName(ProgramConfig.display_name.equals("") ? "未设置" : ProgramConfig.display_name);
            //Toast.makeText(MainActivity.this, "姓名设置已经更改"+ProgramConfig.display_name, Toast.LENGTH_SHORT).show();
        }
        if (Static.getState(state, Static.RETVAL_SETTING_CHANGE_PROFILE)){
            if (!ProgramConfig.profile_image_url.equals(""))
                mNavigationDrawerFragment.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(ProgramConfig.profile_image_url)));
            else
                mNavigationDrawerFragment.setBackground(getResources().getDrawable(R.drawable.profile_maki));
        }
        if (Static.getState(state, Static.RETVAL_SETTING_CHANGE_BACKGROUND)){
            if (!ProgramConfig.background_image_url.equals(""))
                mNavigationDrawerFragment.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(ProgramConfig.background_image_url)));
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
