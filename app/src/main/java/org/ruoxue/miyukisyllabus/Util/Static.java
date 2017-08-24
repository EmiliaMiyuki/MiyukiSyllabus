package org.ruoxue.miyukisyllabus.Util;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.ruoxue.miyukisyllabus.Network.RequestParamters;

import org.jsoup.*;
import org.ruoxue.miyukisyllabus.R;

/**
 * Created by Miyuki on 2016/7/31.
 */
public class Static {
    public static boolean gotWriteExtenralPermission = false;

    // Permission strings
    public static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1;

    // Constants
    public static final int PHOTO_REQUEST_TAKEPHOTO = 1;
    public static final int PHOTO_REQUEST_GALLERY = 2;
    public static final int PHOTO_REQUEST_CUT = 3;

    // Paths
    public static String PATH_DATA_DIR = "";
    public static String PATH_FILES_DIR = "";
    public static String PATH_FILE_CHECKCODE = "";

    // MainActivity return values
    public static final int RETVAL_SETTING_CURRENT_WEEK = 0x0001;
    public static final int RETVAL_SETTING_DISPLAY_NAME = 0x0002;
    public static final int RETVAL_SETTING_CHANGE_PROFILE = 0x0004;
    public static final int RETVAL_SETTING_CHANGE_BACKGROUND = 0x0008;
    public static final int RETVAL_SETTING_NIGHT_MODE = 0x0010;
    public static final int RETVAL_SETTING_NOTIFY_COURSE = 0X0020;
    public static final int RETVAL_COURSE_LIST_CHANGED = 0X0040;

    // Result code
    public static final int RESULT_CODE_SETTINGS = 1;
    public static final int RESULT_CODE_COURSE_LIST = 2;

    public static final RequestParamters rp = new RequestParamters();

    public static String package_name = "org.ruoxue.miyukisyllabus";

    public static void checkResourceDir() {
        File dir = new File(PATH_DATA_DIR);
        if (!dir.exists())
            dir.mkdirs();
        dir = new File(PATH_FILES_DIR);
        if (!dir.exists())
            dir.mkdirs();
    }

    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[] { split[1] };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            // (and
            // general)
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     *            The context.
     * @param uri
     *            The Uri to query.
     * @param selection
     *            (Optional) Filter used in the query.
     * @param selectionArgs
     *            (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        android.database.Cursor cursor = null;
        final String column = "_data";
        final String[] projection = { column };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri
     *            The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static File tempFile() {
        return new File(Static.PATH_FILES_DIR + new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss").format(new Date(System.currentTimeMillis())) + ".jpg");
    }

    public static int changeState(int curr_state, int state) {
        return curr_state | state;
    }

    public static boolean getState(int curr_state, int state) {
        return (curr_state & state) != 0;
    }

    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++)
                deleteDir(new File(dir, children[i]));
        }
        else
            dir.delete();
    }


    public static InputStream GetPage(String url, String cookie) {
        try {
            HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
            connection.setConnectTimeout(2000);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", " Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
            if (cookie != null)
                connection.setRequestProperty("cookie", cookie);

            InputStream in = new BufferedInputStream(connection.getInputStream());
            return in;
        } catch (IOException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        return null;
    }

    public static String GetString(InputStream in){
        Reader reader = new InputStreamReader(in);
        String str="";
        try {
            for (int ch=reader.read(); ch != -1; ch=reader.read()){
                str+=(char)ch;
            }
        }
        catch (Exception e){
            return "";
        }
        return str;
    }

    public static boolean saveBinaryFile(String desc, String url, String cookie) {
        try {
            InputStream image = GetPage(url, cookie);
            File f = new File(desc);
            if (!f.exists()){
                f.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(f);
            for (int b=image.read(); b!=-1; b=image.read())
                fos.write((byte)b);
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println(e.toString());
            e.printStackTrace();
            return false;
        }
    }

    public static Bitmap getBitmap(String url, String cookie) {
        InputStream image = GetPage(url, cookie);
        return BitmapFactory.decodeStream(image);
    }

    public static Connection Connect(String url, RequestParamters rp){
        Connection conn = Jsoup.connect(url)
                .header("Referer", rp.referer).followRedirects(false);
        System.out.println("Referer: "+rp.referer);
        for (HttpCookie cookie : rp.cookies) {
            String[] tmp = cookie.toString().split("=");
            conn.cookie(tmp[0], tmp[1]);
            System.out.println("Cookie: "+tmp[0]+"="+tmp[1]);
        }
        return conn;
    }

    public static void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String THEME_NAME_PINK = "粉红色 (Pink)", THEME_NAME_PURPLE = "紫色 (Purple)", THEME_NAME_BLUE="蓝色 (Blue)", THEME_NAME_BLACK = "黑色 (Black)";

    public static String[] getThemeList() {
        return new String[] { THEME_NAME_PINK, THEME_NAME_PURPLE, THEME_NAME_BLUE, THEME_NAME_BLACK };
    }

    public static int getThemeIdByThemeName(String name) {
        if (name.equals(THEME_NAME_BLUE)) return R.style.AppTheme_Blue;
        if (name.equals(THEME_NAME_PINK)) return R.style.AppTheme;
        if (name.equals(THEME_NAME_PURPLE)) return R.style.AppTheme_Purple;
        if (name.equals(THEME_NAME_BLACK)) return R.style.AppTheme_Black;
        else return R.style.AppTheme;
    }

    public static boolean themeChanged = false;


}
