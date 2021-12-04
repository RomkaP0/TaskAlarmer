package com.kradyk.taskalarmer;

public class SearchItem {
    private String date;
    private String timeb;
    private String timenotif;
    private String desc;
    private String timee;
    private String id;
    private String title;

    public SearchItem(String id ,String title, String date, String timeb, String timee, String timenotif, String desc){
        this.timee = timee;
        this.title = title;
        this.date = date;
        this.timeb = timeb;
        this.timenotif = timenotif;
        this.desc = desc;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeb() {
        return timeb;
    }

    public void setTimeb(String timeb) {
        this.timeb = timeb;
    }

    public String getTimee() {
        return timee;
    }

    public void setTimee(String timee) {
        this.timee = timee;
    }

    public String getTimenotif() {
        return timenotif;
    }

    public void setTimenotif(String timenotif) {
        this.timenotif = timenotif;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
