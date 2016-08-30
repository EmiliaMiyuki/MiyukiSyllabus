package ProgramFeatures;

/**
 * Created by Miyuki on 2016/7/31.
 */
import android.util.Log;
import android.widget.Toast;

import java.io.*;
import java.util.*;

import org.json.*;
import org.umaru.miyukisyllabus.MainActivity;

public class ProgramConfig {
    public static boolean first_initial;

    public static int current_week;

    public static String display_name;

    public static String profile_image_url;

    public static String background_image_url;

    /*
     * night_mode
     *   1  Light  2  Dark  3  Auto-switch
     */
    public static int night_mode;

    public static boolean notify_courses;

    public static String jwc_base_url;

    public static String saved_user_name;

    public static String saved_password;

    public static HashMap<Integer, CourseInfo> courses = new HashMap<>();

    public static String web_params_login_url;

    public static String web_params_main_url;

    public static boolean welcome;

    // JSON Object
    public static JSONObject json;

    public static JSONArray course;

    public static void load(String file_url) throws IOException, org.json.JSONException {
        Reader reader = new InputStreamReader(new FileInputStream(file_url),"UTF-8");
        BufferedReader fin = new BufferedReader(reader);
        String str= "";
        String s;
        while ((s=fin.readLine())!=null) {
            byte[] by = s.getBytes();
            String header = Integer.toHexString(by[0]).toUpperCase();
            //判断是否拥有无法识别的字符
            if (header.equalsIgnoreCase("FFFFFFEF") || header.equalsIgnoreCase("3F")) {
                str += s.substring(1) + "\n";
                continue;
            }
            str += s + "\n";
        }
        System.out.println((int) str.charAt(0));
        fin.close();
        json = new JSONObject(str);
        //System.out.println(json.keySet());
        ProgramConfig.first_initial = json.getBoolean("first_initial");
        ProgramConfig.current_week = json.getInt("current_week");
        ProgramConfig.display_name = json.getString("display_name");
        ProgramConfig.profile_image_url = json.getString("profile_image_url");
        ProgramConfig.background_image_url = json.getString("background_image_url");
        ProgramConfig.night_mode = json.getInt("night_mode");
        ProgramConfig.notify_courses = json.getBoolean("notify_courses");
        ProgramConfig.jwc_base_url = json.getString("jwc_base_url");
        ProgramConfig.saved_user_name = json.getString("saved_user_name");
        ProgramConfig.saved_password = json.getString("saved_password");
        ProgramConfig.web_params_login_url = json.getJSONObject("web_params").getString("login_url");
        ProgramConfig.web_params_main_url = json.getJSONObject("web_params").getString("main_url");
        ProgramConfig.welcome = json.getBoolean("welcome");
        ProgramConfig.course = json.getJSONArray("courses");

        System.out.println("JSON ARRAY Length: "+course.length());
        for (int i=0; i<course.length(); i++) {
            CourseInfo t = new CourseInfo();
            JSONObject o = course.getJSONObject(i);
            t.name = o.getString("name");
            t.index = o.getInt("index");
            t.place = o.getString("place");
            t.teacher = o.getString("teacher");
            t.startWeek = o.getInt("startWeek");
            t.endWeek = o.getInt("endWeek");
            t.special_time = o.getInt("special_time");
            if (courses.containsKey(t.index)){
                courses.remove(t.index);
            }
            courses.put(t.index, t);
        }
    }

    public static void putCourseInfo(CourseInfo value) {
        JSONObject o = new JSONObject();
        try {
            o.put("name", value.name);
            o.put("index", value.index);
            o.put("place", value.place);
            o.put("teacher", value.teacher);
            o.put("startWeek", value.startWeek);
            o.put("endWeek", value.endWeek);
            o.put("special_time", value.special_time);
            course.put(o);
        }
        catch (Exception e){

        }
    }

    public static void clearCourseInfo() {
        for (int i=course.length()-1; i>=0; i--) {
            course.remove(i);
        }
    }

    public static int findCourseObjectByIndex(int index) {
        int cl = course.length();
        for (int i=0; i<cl; i++) {
            try {
                JSONObject t = (JSONObject)course.get(i);
                if (t.getInt("index") == index) {
                    return i;
                }
            }
            catch (Exception e) {
                return -1;
            }
        }
        return -1;
    }

}
