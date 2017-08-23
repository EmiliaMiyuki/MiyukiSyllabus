package org.ruoxue.miyukisyllabus;

/**
 * Created by Miyuki on 2017/8/23.
 */

import java.util.LinkedList;
import java.util.List;
import android.app.Activity;
import android.app.Application;

public class ManagedApplication extends Application {
    private List<Activity> activitys = null;
    private static ManagedApplication instance;
    private ManagedApplication() {
        activitys = new LinkedList<Activity>();
    }

    public static ManagedApplication getInstance() {
        if (null == instance) {
            instance = new ManagedApplication();
        }
        return instance;
    }
    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activitys != null && activitys.size() > 0) {
            if(!activitys.contains(activity)){
                activitys.add(activity);
            }
        }else{
            activitys.add(activity);
        }
    }
    // 遍历所有Activity并finish
    public void exit() {
        if (activitys != null && activitys.size() > 0) {
            for (Activity activity : activitys) {
                activity.finish();
            }
        }
        System.exit(0);
    }
}
