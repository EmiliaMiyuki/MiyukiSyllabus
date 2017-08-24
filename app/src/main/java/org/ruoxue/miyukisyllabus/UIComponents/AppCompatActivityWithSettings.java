package org.ruoxue.miyukisyllabus.UIComponents;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.ruoxue.miyukisyllabus.Data.SettingsDAO;
import org.ruoxue.miyukisyllabus.Data.SettingsDTO;
import org.ruoxue.miyukisyllabus.Util.Static;

/**
 * Created by Miyuki on 2017/8/24.
 */

public class AppCompatActivityWithSettings extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle bundle) {
        if (SettingsDTO.getTheme().equals("")) {
            new SettingsDAO().loadSettings();
        }
        setTheme(Static.getThemeIdByThemeName(SettingsDTO.getTheme()));


        super.onCreate(bundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Static.themeChanged)
        {
            Static.themeChanged = false;
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);

            
        }
    }
}
