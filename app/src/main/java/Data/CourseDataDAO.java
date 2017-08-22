package Data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.*;

/**
 * Created by Miyuki on 2017/8/21.
 */

// 课表 (课程名,  特殊时间,  教师,  教室,  星期,  第几节课, 开始于, 结束于)

public class CourseDataDAO {
    SQLiteDatabase db = new Database().getDb();

    public final String TABLE_NAME = "course_list";
    public final String COLUMN_ID = "rowid";
    public final String COLUMN_NAME = "name";
    public final String COLUMN_SPECIAL_TIME = "special_time";
    public final String COLUMN_TEACHER = "teacher";
    public final String COLUMN_CLASSROOM = "classroom";
    public final String COLUMN_WEEKDAY = "weekday";
    public final String COLUMN_CLASS_INDEX = "class_index";
    public final String COLUMN_START_WEEK = "start_week";
    public final String COLUMN_END_WEEK = "end_week";

    public final String SQL_ENTRY_CREATE_TABLE = "create table if not exists " + TABLE_NAME + " ("
            + COLUMN_NAME + "  text,"
            + COLUMN_SPECIAL_TIME + "  integer,"
            + COLUMN_TEACHER + " text,"
            + COLUMN_CLASSROOM + " text,"
            + COLUMN_WEEKDAY + " int,"
            + COLUMN_CLASS_INDEX + " int,"
            + COLUMN_START_WEEK + " int,"
            + COLUMN_END_WEEK + " int) ";

    public final String SQL_ENTRY_SELECT_WEEKLY_COURSE =
            "select rowid, * from " + TABLE_NAME + " where " + COLUMN_SPECIAL_TIME + " & ? != 0 and "
            + COLUMN_START_WEEK + " <= ? and " + COLUMN_END_WEEK + " >= ? ";

    public final String SQL_ENTRY_SELECT_DAILY_COURSE =
            SQL_ENTRY_SELECT_WEEKLY_COURSE + " and " + COLUMN_WEEKDAY + " = ? order by " + COLUMN_CLASS_INDEX + " ASC";

    public final String SQL_ENTRY_INSERT_COURSE = "insert into " + TABLE_NAME + " values (?,?,?,?,?,?,?,?)";

    public final String SQL_ENTRY_SELECT_COURSE =
            "select rowid, * from " + TABLE_NAME + " where " + COLUMN_SPECIAL_TIME + " & ? != 0 and "
                    + COLUMN_START_WEEK + " <= ? and " + COLUMN_END_WEEK + " >= ? " + " and " + COLUMN_WEEKDAY + " = ?  and " + COLUMN_CLASS_INDEX + " = ?";

    public final String SQL_ENTRY_DELETE_WHERE_CLAUSE = COLUMN_SPECIAL_TIME + " & ? != 0 and "
            + COLUMN_START_WEEK + " <= ? and " + COLUMN_END_WEEK + " >= ? " + " and " + COLUMN_WEEKDAY + " = ?  and " + COLUMN_CLASS_INDEX + " = ?";


    public int getSpecifiedTimeByWeek(int week) {
        return week % 2 == 0?2:1;
    }

    public CourseData fillCourseData(Cursor cur) {
        CourseData data = new CourseData();
        data.setName(cur.getString(cur.getColumnIndex(COLUMN_NAME)));
        data.setSpecialTime(cur.getInt(cur.getColumnIndex(COLUMN_SPECIAL_TIME)));
        data.setTeacher(cur.getString(cur.getColumnIndex(COLUMN_TEACHER)));
        data.setClassroom(cur.getString(cur.getColumnIndex(COLUMN_CLASSROOM)));
        data.setWeekday(cur.getInt(cur.getColumnIndex(COLUMN_WEEKDAY)));
        data.setCourseIndex(cur.getInt(cur.getColumnIndex(COLUMN_CLASS_INDEX)));
        data.setStartWeek(cur.getInt(cur.getColumnIndex(COLUMN_START_WEEK)));
        data.setEndWeek(cur.getInt(cur.getColumnIndex(COLUMN_END_WEEK)));
        data.setId(cur.getInt(cur.getColumnIndex(COLUMN_ID)));
        return data;
    }

    public ContentValues fillCV(CourseData d) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NAME, d.getName());
        cv.put(COLUMN_SPECIAL_TIME, d.getSpecialTime());
        cv.put(COLUMN_TEACHER, d.getTeacher());
        cv.put(COLUMN_CLASSROOM, d.getClassroom());
        cv.put(COLUMN_WEEKDAY, d.getWeekday());
        cv.put(COLUMN_CLASS_INDEX, d.getCourseIndex());
        cv.put(COLUMN_START_WEEK, d.getStartWeek());
        cv.put(COLUMN_END_WEEK, d.getEndWeek());
        return cv;
    }

    // Operations

    public List<CourseData> getWeeklyCourse(int week) {
        Cursor cur = db.rawQuery(SQL_ENTRY_SELECT_WEEKLY_COURSE, new String[]{ ""+getSpecifiedTimeByWeek(week), ""+week, ""+week });
        List<CourseData> lst = new ArrayList<>();
        if (cur.getCount() <= 0)
            return lst;
        cur.moveToFirst();
        for (int i=0; i<cur.getCount(); i++) {
            lst.add(fillCourseData(cur));
            cur.moveToNext();
        }
        return lst;
    }

    public void createList() {
        db.execSQL(SQL_ENTRY_CREATE_TABLE);
    }

    public CourseData getCourse(int week, int day, int index) {
        Cursor cur = db.rawQuery(SQL_ENTRY_SELECT_COURSE, new String[]{ ""+getSpecifiedTimeByWeek(week), ""+week, ""+week, ""+day, ""+index });
        if (cur.getCount() <= 0) return null;
        cur.moveToFirst();
        return fillCourseData(cur);
    }
    
    public List<CourseData> getDailyCourse(int week, int day) {
        Cursor cur = db.rawQuery(SQL_ENTRY_SELECT_DAILY_COURSE, new String[]{ ""+getSpecifiedTimeByWeek(week), ""+week, ""+week, ""+day });
        List<CourseData> lst = new ArrayList<>();
        if (cur.getCount() <= 0)
            return lst;
        cur.moveToFirst();
        for (int i=0; i<cur.getCount(); i++) {
            lst.add(fillCourseData(cur));
            cur.moveToNext();
        }
        return lst;
    }

    public boolean insertCourse(CourseData d) {
        return db.insert(TABLE_NAME, null, fillCV(d)) != -1;
    }

    public boolean updateCourse(CourseData d) {
        return db.update(TABLE_NAME, fillCV(d), COLUMN_ID + "=?", new String[] { ""+d.getId() }) != 0;
    }

    public boolean deleteCourse(int week, int day, int index) {
        return db.delete(TABLE_NAME, SQL_ENTRY_DELETE_WHERE_CLAUSE, new String[] { ""+getSpecifiedTimeByWeek(week), ""+week, ""+week, ""+day, ""+index }) != 0;
    }

    public boolean deleteCourse(CourseData d) {
        return db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[] { ""+d.getId() }) != 0;
    }

    public void clearData() {
        db.delete(TABLE_NAME, null, null);
    }
}
