package com.example.main.EventOperation;

public class Card {

    public static String  Constant_Name="H";
    private  String Name;
    private String Subject;
    private String Class2;
    private String Date2;
    private String Time2;
    private  String  ID;
    private String Status;

    public Card(String name, String subject, String class2, String date2, String time2, String status, String type, String content, String student) {
        Name = name;
        Subject = subject;
        Class2 = class2;
        Date2 = date2;
        Time2 = time2;
        Status = status;
        this.type = type;
        this.content = content;
        this.student = student;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    private  String type,content,student;
    private String text;

    public  static int count_for_accpeted;


    private String year,month;

    public Card(String year, String month) {
        this.year = year;
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Card(){

    }

    public Card(String name, String subject, String class2, String date2, String time2,String Status) {
        Name = name;
        Subject = subject;
        Class2 = class2;
        Date2 = date2;
        Time2 = time2;
        this.Status = Status;

    }

    public Card(String subject, String class2, String date2, String time2) {
        Subject = subject;
        Class2 = class2;
        Date2 = date2;
        Time2 = time2;

    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getClass2() {
        return Class2;
    }

    public void setClass2(String class2) {
        Class2 = class2;
    }

    public String getDate2() {
        return Date2;
    }

    public void setDate2(String date2) {
        Date2 = date2;
    }

    public String getTime2() {
        return Time2;
    }

    public void setTime2(String time2) {
        Time2 = time2;
    }

    public void setKey(String key) {
    }


}
