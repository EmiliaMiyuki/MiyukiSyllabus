package org.ruoxue.miyukisyllabus.Data;

/**
 * Created by Miyuki on 2017/8/21.
 */

public class CourseData {
    private int     id;
    private String  name;
    private int     specialTime;
    private String  teacher;
    private String  classroom;
    private int     weekday;
    private int     courseIndex;
    private int     startWeek;
    private int     endWeek;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSpecialTime() {
        return specialTime;
    }

    public String getSpecialTimeString() {
        if (specialTime == 1) return "单周";
        else if (specialTime == 2) return "双周";
        return "";
    }

    public void setSpecialTime(int specialTime) {
        this.specialTime = specialTime;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public int getWeekday() {
        return weekday;
    }

    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }

    public int getCourseIndex() {
        return courseIndex;
    }

    public void setCourseIndex(int courseIndex) {
        this.courseIndex = courseIndex;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public String getDescription() { return "第"+(getCourseIndex()+1)+"节，"+getClassroom(); }

}
