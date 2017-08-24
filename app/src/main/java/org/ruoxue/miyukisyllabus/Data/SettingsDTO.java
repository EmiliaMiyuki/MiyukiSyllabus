package org.ruoxue.miyukisyllabus.Data;

import org.ruoxue.miyukisyllabus.Util.Static;

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

    public static boolean isFirstInit() {
        return firstInit;
    }

    public static void setFirstInit(boolean firstInit) {
        SettingsDTO.firstInit = firstInit;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        SettingsDTO.userName = userName;
    }

    public static String getAvaterImg() {
        return avaterImg;
    }

    public static void setAvaterImg(String avaterImg) {
        SettingsDTO.avaterImg = avaterImg;
    }

    public static String getRbackgoundImg() {
        return rbackgoundImg;
    }

    public static void setRbackgoundImg(String rbackgoundImg) {
        SettingsDTO.rbackgoundImg = rbackgoundImg;
    }

    public static String getTheme() {
        return theme;
    }

    public static void setTheme(String theme) {
        SettingsDTO.theme = theme;
    }

    public static boolean isNotifyCourses() {
        return notifyCourses;
    }

    public static void setNotifyCourses(boolean notifyCourses) {
        SettingsDTO.notifyCourses = notifyCourses;
    }

    public static String getJwcUserName() {
        return jwcUserName;
    }

    public static void setJwcUserName(String jwcUserName) {
        SettingsDTO.jwcUserName = jwcUserName;
    }

    public static String getJwcPassword() {
        return jwcPassword;
    }

    public static void setJwcPassword(String jwcPassword) {
        SettingsDTO.jwcPassword = jwcPassword;
    }

    public static String getOpenSchoolDate() {
        return openSchoolDate;
    }

    public static void setOpenSchoolDate(String openSchoolDate) {
        SettingsDTO.openSchoolDate = openSchoolDate;
    }

    public static boolean isWelcome() {
        return welcome;
    }

    public static void setWelcome(boolean welcome) {
        SettingsDTO.welcome = welcome;
    }

    public static int getCurrentWeek() {
        try {
            return 1 + (int)( new Date().getTime() -  new SimpleDateFormat("yyyy-MM-dd").parse(getOpenSchoolDate()).getTime())/3600/24/7/1000 ;
        }
        catch (Exception e) {
            return 1;
        }
    }
}
