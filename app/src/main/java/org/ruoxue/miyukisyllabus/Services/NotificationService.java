package org.ruoxue.miyukisyllabus.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import org.ruoxue.miyukisyllabus.Data.CourseData;
import org.ruoxue.miyukisyllabus.Data.CourseDataDAO;
import org.ruoxue.miyukisyllabus.Data.SettingsDTO;
import org.ruoxue.miyukisyllabus.R;

import java.util.Calendar;
import java.util.Date;

public class NotificationService extends Service {
    private NotificationManager nm;
    private ConditionVariable cond;

    private boolean not_request_stop = true;

    public NotificationService() {

    }

    @Override
    public void onCreate() {
        nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        cond = new ConditionVariable(false);
        Thread t = new Thread(mTask);
        t.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    private Runnable mTask = new Runnable() {
        public void run() {
            // 首次进入服务时，计算距离下次45分的时间(每小时45分定时发送消息)
            boolean is_morning_class = new Date().getHours() <= 12;
            int start_time = 60;
            if (!is_morning_class && SettingsDTO.isSummmerTime())
                start_time = 30;

            start_time -= SettingsDTO.getNotifyTimeBefore();

            int minutes = start_time - new Date().getMinutes();

            if (minutes <= 0) {
                minutes += 60;
            }

            System.out.println("Next notification send: "+minutes);
            //sendNotification("将在"+minutes+"分钟后发送通知", "test");

            while (not_request_stop) {
                if (cond.block(minutes * 60 * 1000))
                    break;
                int hour = new Date().getHours();
                // 上下午转换
                if (new Date().getHours() == 12 || new Date().getHours() == 1)
                    minutes = 30;
                minutes = 60;

                // 不通知的时间
                if (hour < 12 && !(hour == 7 || hour == 9 || hour == 11))
                    continue;
                if (!SettingsDTO.isSummmerTime() && !(hour == 13 || hour == 15 || hour == 18))
                    continue;
                if (SettingsDTO.isSummmerTime() && !(hour == 14 || hour == 16 || hour == 19))
                    continue;

                int index = 0;
                //获得当前是第几节课
                if (hour <= 12) {
                    // 上午
                    if (hour < 8)
                        index = 0;
                    else if (hour < 10)
                        index = 1;
                }
                else if (hour <= 17) {
                    // 下午
                    if (hour <= 14)
                        index = 2;
                    else if (hour <= 16)
                        index = 3;
                }
                else {
                    // 晚上
                    index = 4;
                }

                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date());
                int day = (cal.get(Calendar.DAY_OF_WEEK) - 2 + 7) % 7;

                CourseData data = new CourseDataDAO().getCourse(SettingsDTO.getCurrentWeek(), day, index);
                if (data == null)
                    continue;

                sendNotification("下一节课："+data.getName(), "上课地点"+data.getClassroom());
            }
            // 停止服务。
            NotificationService.this.stopSelf();
        }
    };

    private final IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            return super.onTransact(code, data, reply, flags);
        }
    };

    final int NOTIFY_ID = 0x12450;

    private void sendNotification(String title, String description) {
        Notification notification = new Notification.Builder(this).setSmallIcon(R.drawable.ic_today_course)
                .setContentTitle(title).setContentText(description).build();
        nm.notify(NOTIFY_ID, notification);
    }

    @Override
    public void onDestroy() {
        nm.cancel(NOTIFY_ID);
        cond.open();
    }
}
