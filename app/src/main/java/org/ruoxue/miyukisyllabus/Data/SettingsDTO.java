package org.ruoxue.miyukisyllabus.Data;

import org.ruoxue.miyukisyllabus.Util.Static;
import org.ruoxue.miyukisyllabus.Util.WeekCount;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Miyuki on 2017/8/23.
 */

public class SettingsDTO {
    static boolean firstInit;
    static String  userName;
    static String  avaterImg;
    static String  rbackgoundImg;
    static String  theme = "";
    static boolean notifyCourses;
    static String  jwcUserName;
    static String  jwcPassword;
    static String  openSchoolDate;
    static boolean  welcome;
    static String syllabusBackgroundImg = "";

    public static final SettingsDAO dao = new SettingsDAO();

    public static String getSyllabusBackgroundImg() {
        return syllabusBackgroundImg;
    }

    public static void setSyllabusBackgroundImg(String syllabusBackgroundImg) {
        SettingsDTO.syllabusBackgroundImg = syllabusBackgroundImg;
        dao.setSetting(dao.KEY_SYLLABUS_IMG, syllabusBackgroundImg);
    }

    public static boolean isFirstInit() {
        return firstInit;
    }

    public static void setFirstInit(boolean firstInit) {
        SettingsDTO.firstInit = firstInit;
        dao.setSetting(dao.KEY_FIRST_INIT, ""+firstInit);
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        SettingsDTO.userName = userName;
        dao.setSetting(dao.KEY_USER_NAME, userName);
    }

    public static String getAvaterImg() {
        return avaterImg;
    }

    public static void setAvaterImg(String avaterImg) {
        SettingsDTO.avaterImg = avaterImg;
        dao.setSetting(dao.KEY_AVATER_IMG, avaterImg);
    }

    public static String getRbackgoundImg() {
        return rbackgoundImg;
    }

    public static void setRbackgoundImg(String rbackgoundImg) {
        SettingsDTO.rbackgoundImg = rbackgoundImg;
        dao.setSetting(dao.KEY_BACKGROUND_IMG, rbackgoundImg);
    }

    public static String getTheme() {
        return theme;
    }

    public static void setTheme(String theme) {
        SettingsDTO.theme = theme;
        dao.setSetting(dao.KEY_THEME, theme);
    }

    public static boolean isNotifyCourses() {
        return notifyCourses;
    }

    public static void setNotifyCourses(boolean notifyCourses) {
        SettingsDTO.notifyCourses = notifyCourses;
        dao.setSetting(dao.KEY_NOTIFY_COURSE, notifyCourses?"true":"false");
    }

    public static String getJwcUserName() {
        return jwcUserName;
    }

    public static void setJwcUserName(String jwcUserName) {
        SettingsDTO.jwcUserName = jwcUserName;
        dao.setSetting(dao.KEY_JWC_USER_NAME, jwcUserName);
    }

    public static String getJwcPassword() {
        return jwcPassword;
    }

    public static void setJwcPassword(String jwcPassword) {
        SettingsDTO.jwcPassword = jwcPassword;
        dao.setSetting(dao.KEY_JWC_PASSWORD, jwcPassword);
    }

    public static String getOpenSchoolDate() {
        return openSchoolDate;
    }

    public static void setOpenSchoolDate(String openSchoolDate) {
        SettingsDTO.openSchoolDate = openSchoolDate;
        dao.setSetting(dao.KEY_OPEN_SCHOOL_DATE, openSchoolDate);
    }

    public static boolean isWelcome() {
        return welcome;
    }

    public static void setWelcome(boolean welcome) {
        SettingsDTO.welcome = welcome;
        dao.setSetting(dao.KEY_SHOW_WELCOME, welcome?"true":"false");
    }

    public static int getCurrentWeek() {
        try {
            return WeekCount.timeBettwen(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), getOpenSchoolDate(), WeekCount.WEEK) + 1;
        }
        catch (Exception e) {
            return 1;
        }
    }
}
