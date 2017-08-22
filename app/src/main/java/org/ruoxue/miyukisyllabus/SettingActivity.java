package org.ruoxue.miyukisyllabus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import ProgramFeatures.ProgramConfig;
import ProgramFeatures.Static;

public class SettingActivity extends AppCompatActivity {
    Toolbar mToolbar;
    LinearLayout mItem_currentWeek;
    TextView     mValue_currentWeek;

    LinearLayout mItem_displayName;
    TextView     mValue_displayName;

    LinearLayout mItem_profileImage;

    LinearLayout mItem_backgroundImage;

    LinearLayout mItem_nightMode;
    TextView     mValue_nightMode;

    LinearLayout mItem_notifyCourse;
    Switch       mValue_notifyCourse;

    LinearLayout mItem_customJwcURL;
    TextView     mValue_customJwcURL;

    LinearLayout mItem_editConfigFile;

    LinearLayout mItem_showWelcomeTips;
    Switch       mValue_showWelcomeTips;

    LinearLayout mItem_clearCache;

    File tmp_file;

    int which_type_of_image_select;
    int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        Static.loadConfig();

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
        mValue_currentWeek.setText(ProgramConfig.open_school_date);

        mItem_displayName = (LinearLayout)findViewById(R.id.setting_display_name);
        mValue_displayName = (TextView)findViewById(R.id.setting_val_nickname);
        mValue_displayName.setText(ProgramConfig.display_name);

        mItem_profileImage = (LinearLayout)findViewById(R.id.setting_profile_image);

        mItem_backgroundImage = (LinearLayout)findViewById(R.id.setting_change_bg);

        mItem_nightMode = (LinearLayout)findViewById(R.id.setting_night_mode);
        mValue_nightMode = (TextView)findViewById(R.id.setting_val_night_mode);
        mValue_nightMode.setText(ProgramConfig.night_mode == 0 ? "亮色主题" : (ProgramConfig.night_mode == 1 ? "暗色主题" : "自动切换"));

        mItem_notifyCourse = (LinearLayout)findViewById(R.id.setting_notificate);
        mValue_notifyCourse = (Switch)findViewById(R.id.setting_notification_state);
        mValue_notifyCourse.setChecked(ProgramConfig.notify_courses);

        mItem_customJwcURL = (LinearLayout)findViewById(R.id.setting_edit_jwc_url);
        mValue_customJwcURL = (TextView)findViewById(R.id.setting_jwc_url);
        mValue_customJwcURL.setText(ProgramConfig.jwc_base_url);

        mItem_editConfigFile = (LinearLayout)findViewById(R.id.setting_edit_config);

        mItem_showWelcomeTips = (LinearLayout)findViewById(R.id.setting_display_welcome);
        mValue_showWelcomeTips = (Switch)findViewById(R.id.setting_display_welcome_val);
        mValue_showWelcomeTips.setChecked(ProgramConfig.welcome);

        mItem_clearCache = (LinearLayout)findViewById(R.id.setting_clear_cache);

        mItem_editConfigFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(Static.PATH_CONFIG_FILE)), "application/json");
                startActivity(intent);
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
                                    ProgramConfig.open_school_date = v;
                                    ProgramConfig.json.remove("open_school_date");
                                    try {
                                        ProgramConfig.json.put("open_school_date", v);
                                        Static.WriteSettings();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
                value.setText("" + ProgramConfig.display_name);
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("显示的姓名")
                        .setView(value)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String v = value.getText().toString();
                                try {
                                    ProgramConfig.display_name = v;
                                    ProgramConfig.json.remove("display_name");
                                    ProgramConfig.json.put("display_name", v);
                                    Static.WriteSettings();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                state = Static.changeState(state, Static.RETVAL_SETTING_DISPLAY_NAME);
                                //Toast.makeText(SettingActivity.this, ""+state, Toast.LENGTH_SHORT).show();
                                mValue_displayName.setText(v);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });

        mItem_customJwcURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText value = new EditText(SettingActivity.this);
                value.setHint("自定义教务处地址");
                value.setText("" + ProgramConfig.jwc_base_url);
                new AlertDialog.Builder(SettingActivity.this)
                        .setTitle("输入登入入口地址")
                        .setView(value)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String v = value.getText().toString();
                                try {
                                    ProgramConfig.jwc_base_url = v;
                                    ProgramConfig.json.remove("jwc_base_url");
                                    ProgramConfig.json.put("jwc_base_url", v);
                                    Static.WriteSettings();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mValue_customJwcURL.setText(v);
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
                    ProgramConfig.notify_courses = val;
                    ProgramConfig.json.remove("notify_courses");
                    ProgramConfig.json.put("notify_courses", val);
                    Static.WriteSettings();
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
                    ProgramConfig.welcome = val;
                    ProgramConfig.json.remove("welcome");
                    ProgramConfig.json.put("welcome", val);
                    Static.WriteSettings();
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

        mItem_clearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        mItem_nightMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SettingActivity.this)
                        .setSingleChoiceItems(new String[]{"亮色主题", "暗色主题", "自动切换"}, ProgramConfig.night_mode, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ProgramConfig.night_mode = which;
                                ProgramConfig.json.remove("night_mode");
                                try {
                                    ProgramConfig.json.put("night_mode", which);
                                    mValue_nightMode.setText(ProgramConfig.night_mode == 0 ? "亮色主题" : (ProgramConfig.night_mode == 1 ? "暗色主题" : "自动切换"));
                                    Static.WriteSettings();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                            }
                        })
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
                                ProgramConfig.profile_image_url = tmp_file.getAbsolutePath();
                                ProgramConfig.json.remove("profile_image_url");
                                ProgramConfig.json.put("profile_image_url", tmp_file.getAbsolutePath());
                                Static.WriteSettings();
                                break;
                            case 2:
                                ProgramConfig.background_image_url = tmp_file.getAbsolutePath();
                                ProgramConfig.json.remove("background_image_url");
                                ProgramConfig.json.put("background_image_url", tmp_file.getAbsolutePath());
                                Static.WriteSettings();
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
                                    ProgramConfig.profile_image_url = "";
                                    ProgramConfig.json.remove("profile_image_url");
                                    ProgramConfig.json.put("profile_image_url", "");
                                    state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_PROFILE);
                                    break;
                                case 2:
                                    ProgramConfig.background_image_url = "";
                                    ProgramConfig.json.remove("background_image_url");
                                    ProgramConfig.json.put("background_image_url", "");
                                    state = Static.changeState(state, Static.RETVAL_SETTING_CHANGE_BACKGROUND);
                                    break;
                            }
                            Static.WriteSettings();
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
}
