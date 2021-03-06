package org.ruoxue.miyukisyllabus.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import org.ruoxue.miyukisyllabus.Data.CourseDataDAO;
import org.ruoxue.miyukisyllabus.Data.SettingsDAO;
import org.ruoxue.miyukisyllabus.Data.SettingsDTO;
import org.ruoxue.miyukisyllabus.UIComponents.AppCompatActivityWithSettings;
import org.ruoxue.miyukisyllabus.UIComponents.ManagedApplication;
import org.ruoxue.miyukisyllabus.R;
import org.ruoxue.miyukisyllabus.Util.Static;

public class SettingActivity extends AppCompatActivityWithSettings {
    Toolbar mToolbar;
    LinearLayout mItem_currentWeek;
    TextView     mValue_currentWeek;

    LinearLayout mItem_displayName;
    TextView     mValue_displayName;

    LinearLayout mItem_profileImage;

    LinearLayout mItem_backgroundImage;

    LinearLayout mItem_changeSyllabusBg;

    LinearLayout mItem_theme;
    TextView mValue_theme;

    LinearLayout mItem_notifyCourse;
    Switch       mValue_notifyCourse;

    LinearLayout mItem_resoreDefaults;

    LinearLayout mItem_showWelcomeTips;
    Switch       mValue_showWelcomeTips;

    LinearLayout mItem_clearLoginState;

    LinearLayout mItem_clearCache;

    LinearLayout mItem_clearCourses;

    LinearLayout mItem_summerTime;
    Switch       mValue_summerTime;

    LinearLayout mItem_notifyBefore;
    TextView     mValue_notifyBefore;

    File tmp_file;

    int which_type_of_image_select;
    int state = 0;

    //SettingsDAO sdao = new SettingsDAO();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ManagedApplication.getInstance().addActivity(this);

        setContentView(R.layout.activity_setting);

        SettingsDTO.dao.loadSettings();

        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });

        mItem_currentWeek = (LinearLayout)findViewById(R.id.setting_current_week);
        mValue_currentWeek = (TextView)findViewById(R.id.setting_val_week);
        mValue_currentWeek.setText(SettingsDTO.getOpenSchoolDate());

        mItem_displayName = (LinearLayout)findViewById(R.id.setting_display_name);
        mValue_displayName = (TextView)findViewById(R.id.setting_val_nickname);
        mValue_displayName.setText(SettingsDTO.getUserName());

        mItem_profileImage = (LinearLayout)findViewById(R.id.setting_profile_image);

        mItem_backgroundImage = (LinearLayout)findViewById(R.id.setting_change_bg);

        mItem_theme = (LinearLayout)findViewById(R.id.setting_theme);
        mValue_theme = (TextView)findViewById(R.id.setting_val_night_mode);
        mValue_theme.setText(SettingsDTO.getTheme());

        mItem_notifyCourse = (LinearLayout)findViewById(R.id.setting_notificate);
        mValue_notifyCourse = (Switch)findViewById(R.id.setting_notification_state);
        mValue_notifyCourse.setChecked(SettingsDTO.isNotifyCourses());

        mItem_resoreDefaults = (LinearLayout)findViewById(R.id.setting_restore_defaults);

        mItem_showWelcomeTips = (LinearLayout)findViewById(R.id.setting_display_welcome);
        mValue_showWelcomeTips = (Switch)findViewById(R.id.setting_display_welcome_val);
        mValue_showWelcomeTips.setChecked(SettingsDTO.isWelcome());

        mItem_clearCache = (LinearLayout)findViewById(R.id.setting_clear_cache);

        mItem_clearLoginState = (LinearLayout)findViewById(R.id.setting_reset_login_state);

        mItem_changeSyllabusBg = (LinearLayout)findViewById(R.id.setting_change_bg_syllabus);

        mItem_clearCourses = (LinearLayout)findViewById(R.id.setting_delete_course);

        mItem_summerTime = (LinearLayout)findViewById(R.id.setting_summber_time);
        mValue_summerTime = (Switch)findViewById(R.id.setting_summber_time_val);
        mValue_summerTime.setChecked(SettingsDTO.isSummmerTime());

        mItem_notifyBefore = (LinearLayout)findViewById(R.id.setting_notify_time_before);
        mValue_notifyBefore = (TextView)findViewById(R.id.setting_val_notify_time_before);
        mValue_notifyBefore.setText(SettingsDTO.getNotifyTimeBefore()+"分钟");

        mItem_summerTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean val = !mValue_summerTime.isChecked();
                mValue_summerTime.setChecked(val);
                try {
                    SettingsDTO.setSummmerTime(val);
                    Toast.makeText(SettingActivity.this, "更改将于重启程序后生效", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mItem_notifyBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final NumberPicker value = new NumberPicker(SettingActivity.this);
                value.setMinValue(1);
                value.setMaxValue(29);
                value.setValue(SettingsDTO.getNotifyTimeBefore());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("选择提前的分钟数")
                        .setView(value)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SettingsDTO.setNotifyTimeBefore(value.getValue());
                                mValue_notifyBefore.setText(value.getValue()+"分钟");
                                Toast.makeText(SettingActivity.this, "更改将于重启程序后生效", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        mItem_resoreDefaults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(SettingActivity.this, "已恢复默认设置，请重新打开程序。", Toast.LENGTH_SHORT).show();
                //android.os.Process.killProcess(android.os.Process.myPid());
                new android.app.AlertDialog.Builder(SettingActivity.this)
                        .setTitle(getResources().getString(R.string.setting_item_restore_defaults))
                        .setMessage("所有设置都会被删除，是否继续？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SettingsDTO.dao.dropTable();
                                new android.app.AlertDialog.Builder(SettingActivity.this)
                                        .setCancelable(false)
                                        .setTitle(getResources().getString(R.string.setting_item_restore_defaults))
                                        .setMessage("已恢复默认设置，请重新打开程序。")
                                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ManagedApplication.getInstance().exit();
                                            }
                                        })
                                        .show();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });

        this.setTitle("设置");

        mItem_currentWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePicker value = new DatePicker(SettingActivity.this);
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle(null)
                        .setView(value)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String v = value.getYear() + "-" + (value.getMonth()+1) + "-" + value.getDayOfMonth();
                                try {
                                    SettingsDTO.setOpenSchoolDate(v);
                                    mValue_currentWeek.setText(v);
                                    state = Static.changeState(state, Static.RETVAL_SETTING_CURRENT_WEEK);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        mItem_displayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText value = new EditText(SettingActivity.this);
                value.setHint("输入显示的姓名");
                value.setText("" + SettingsDTO.getUserName());
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("显示的姓名")
                        .setView(value)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String v = value.getText().toString();
                                SettingsDTO.setUserName(v);
                                mValue_displayName.setText(v);
                                state = Static.changeState(state, Static.RETVAL_SETTING_DISPLAY_NAME);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        mItem_notifyCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean val = !mValue_notifyCourse.isChecked();
                mValue_notifyCourse.setChecked(val);
                try {
                    SettingsDTO.setNotifyCourses(val);
                    Toast.makeText(SettingActivity.this, "更改将于重启程序后生效", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mItem_showWelcomeTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean val = !mValue_showWelcomeTips.isChecked();
                mValue_showWelcomeTips.setChecked(val);
                try {
                    SettingsDTO.setWelcome(val);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mItem_profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which_type_of_image_select = 1;
                startSelectPictureDialog();
                state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_PROFILE);
            }
        });

        mItem_backgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which_type_of_image_select = 2;
                startSelectPictureDialog();
                state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_BACKGROUND);
            }
        });

        mItem_changeSyllabusBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                which_type_of_image_select = 3;
                startSelectPictureDialog();
            }
        });

        mItem_clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Static.gotWriteExtenralPermission) {
                    alert_if_no_permission();
                    return;
                }
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("清除缓存")
                        .setMessage("所有的缓存图片将会被删除，头像和背景也会恢复为默认。是否确认清除？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Static.deleteDir(new File(Static.PATH_FILES_DIR));
                                Toast.makeText(SettingActivity.this, "缓存已清除", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });

        mItem_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] themes = Static.getThemeList();

                int selected = 0;
                for (int i=0; i<themes.length; i++) {
                    if (themes[i].equals(SettingsDTO.getTheme())) {
                        selected = i;
                        break;
                    }
                }

                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("选择主题")
                        .setSingleChoiceItems(themes, selected, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String theme_name = themes[which];
                                mValue_theme.setText(theme_name);
                                SettingsDTO.setTheme(theme_name);
                                Static.themeChanged = true;
                                dialog.dismiss();

                                Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                        .setPositiveButton("确定", null).setNegativeButton("取消", null)
                        .show();
            }
        });

        mItem_clearLoginState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Static.rp.loginSuccess = false;
                Toast.makeText(SettingActivity.this, "已清除登录状态", Toast.LENGTH_SHORT).show();
            }
        });
        
        mItem_clearCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    new AlertDialog.Builder(SettingActivity.this)
                            .setTitle(getResources().getString(R.string.syllabs_action_clear_data))
                            .setMessage("所有课程表信息都会被删除，是否继续？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CourseDataDAO().clearData();
                                    Toast.makeText(SettingActivity.this, "已清除所有课程数据。", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        // automatically handle clicks on the Home/Up btoutn, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Static.PHOTO_REQUEST_TAKEPHOTO:// 当选择拍照时调用
                startPhotoZoom(Uri.fromFile(tmp_file));
                break;
            case Static.PHOTO_REQUEST_GALLERY:// 当选择从本地获取图片时
                if (data != null) {
                    tmp_file = Static.tempFile();
                    //Toast.makeText(SettingActivity.this, "图片选择：" + Static.getPathByUri4kitkat(SettingActivity.this, data.getData()), Toast.LENGTH_LONG).show();
                    //Toast.makeText(SettingActivity.this, "图片选择：" + Uri.parse(Static.getPathByUri4kitkat(SettingActivity.this, data.getData())), Toast.LENGTH_LONG).show();
                    startPhotoZoom(Uri.fromFile(new File(Static.getPathByUri4kitkat(SettingActivity.this, data.getData()))), Uri.fromFile(tmp_file));
                }
                break;
            case Static.PHOTO_REQUEST_CUT:// 返回的结果
                if (data != null) {
                    Toast.makeText(SettingActivity.this, "图片已设置。", Toast.LENGTH_SHORT).show();
                    try {
                        switch (which_type_of_image_select) {
                            case 1:
                                SettingsDTO.setAvaterImg(tmp_file.getAbsolutePath());
                                state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_PROFILE);
                                break;
                            case 2:
                                SettingsDTO.setRbackgoundImg(tmp_file.getAbsolutePath());
                                state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_BACKGROUND);
                                break;
                            case 3:
                                SettingsDTO.setSyllabusBackgroundImg(tmp_file.getAbsolutePath());
                                break;
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startSelectPictureDialog() {
        if (!Static.gotWriteExtenralPermission) {
            alert_if_no_permission();
            return;
        }
        new AlertDialog.Builder(SettingActivity.this)
                .setTitle("选择图片")
                .setMessage("你可以选择一个图片或者使用相机拍摄相片作为头像或者背景图片。")
                .setPositiveButton("拍摄相片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tmp_file = Static.tempFile();
                        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmp_file));
                        startActivityForResult(cameraintent, Static.PHOTO_REQUEST_TAKEPHOTO);
                    }
                })
                .setNegativeButton("选择图片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent getAlbum = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        getAlbum.addCategory(Intent.CATEGORY_OPENABLE);
                        getAlbum.setType("image/*");
                        startActivityForResult(getAlbum, Static.PHOTO_REQUEST_GALLERY);
                    }
                })
                .setNeutralButton("恢复默认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            switch (which_type_of_image_select) {
                                case 1:
                                    SettingsDTO.setAvaterImg("");
                                    state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_PROFILE);
                                    break;
                                case 2:
                                    SettingsDTO.setRbackgoundImg("");
                                    state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_BACKGROUND);
                                    break;
                                case 3:
                                    SettingsDTO.setSyllabusBackgroundImg("");
                                    break;
                            }
                        } catch (Exception e) {

                        }
                    }
                }).show();
    }

    private void startPhotoZoom(Uri uri) {
        startPhotoZoom(uri, uri);
    }

    private void startPhotoZoom(Uri uri, Uri to) {
        switch (which_type_of_image_select) {
            case 1:
                startPhotoZoom(uri, to, 1, 1, 300, 300);
                break;
            case 2:
                startPhotoZoom(uri, to, 1.59, 1, 479, 301);
                break;
            case 3:
                View vw = getWindow().getDecorView();
                startPhotoZoom(uri, to, 1, ((double)vw.getHeight()) / vw.getWidth(), vw.getWidth(), vw.getHeight());
                break;
        }
    }

    private void startPhotoZoom(Uri uri, Uri to, double aspectX, double aspectY, int outputX, int outputY){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");

        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("output", to);
        startActivityForResult(intent, Static.PHOTO_REQUEST_CUT);
    }

    public void finishActivity() {
        Intent intent = new Intent();
        intent.putExtra("state", state);
        SettingActivity.this.setResult(RESULT_OK, intent);
        finish();
    }

    void alert_if_no_permission() {
        new AlertDialog.Builder(SettingActivity.this)
                .setTitle("没有权限")
                .setMessage("没有读写外部存储的权限，因此该功能无法使用。若要使用请前往设置，授予读写外部存储的权限。")
                .setPositiveButton("确定", null).show();
    }
}
