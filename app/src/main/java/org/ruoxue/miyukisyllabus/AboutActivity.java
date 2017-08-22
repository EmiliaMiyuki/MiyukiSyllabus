package org.ruoxue.miyukisyllabus;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class AboutActivity extends AppCompatActivity {
    Toolbar mToolbar;

    LinearLayout mItem_License;
    LinearLayout mItem_OpenSource;
    LinearLayout mItem_Source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

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

        mItem_License = (LinearLayout)findViewById(R.id.about_gpl);
        mItem_OpenSource = (LinearLayout)findViewById(R.id.about_license);
        mItem_Source = (LinearLayout)findViewById(R.id.about_source);

        mItem_License.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, WebBrowserActivity.class);
                intent.putExtra("url", "file:///android_asset/html/license.html");
                intent.putExtra("title", "GNU General Public License");
                startActivity(intent);
            }
        });

        mItem_OpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AboutActivity.this, WebBrowserActivity.class);
                intent.putExtra("url", "file:///android_asset/html/open-source.html");
                intent.putExtra("title", "开放源代码许可");
                startActivity(intent);
            }
        });

        mItem_Source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(getResources().getString(R.string.about_source_desc));
                intent.setData(content_url);
                startActivity(intent);
            }
        });

        this.setTitle("关于");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_about, menu);
        return false;
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
