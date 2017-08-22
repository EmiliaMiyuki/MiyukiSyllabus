package ProgramFeatures;

/**
 * Created by Miyuki on 2016/7/31.
 */

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import org.json.*;

public class ProgramConfig {
    public static boolean first_initial;
    public static String display_name;
    public static String profile_image_url;
    public static String background_image_url;
    public static int night_mode;
    public static boolean notify_courses;
    public static String jwc_base_url;
    public static String saved_user_name;
    public static String saved_password;
    public static String web_params_login_url;
    public static String web_params_main_url;
    public static String open_school_date;
    public static boolean welcome;

    public static String package_name = "org.ruoxue.miyukisyllabus";

    // JSON Object
    public static JSONObject json;

    public static void load(String file_url) throws IOException, org.json.JSONException {
        Reader reader = new InputStreamReader(new FileInputStream(file_url), "UTF-8");
        BufferedReader fin = new BufferedReader(reader);
        String str = "";
        String s;
        while ((s = fin.readLine()) != null) {
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
        json = new JSONObject(str);;
        ProgramConfig.first_initial = json.getBoolean("first_initial");
        ProgramConfig.open_school_date = json.getString("open_school_date");
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
    }

    public static int getCurrentWeek() {
        try {
            return 1 + (int)( new Date().getTime() -  new SimpleDateFormat("yyyy-MM-dd").parse(ProgramConfig.open_school_date).getTime())/3600/24/7/1000 ;
        }
        catch (Exception e) {
            return 1;
        }
    }

}
