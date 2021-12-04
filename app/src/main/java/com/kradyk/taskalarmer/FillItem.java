package com.kradyk.taskalarmer;

public class FillItem {
    private String title;
    private String timeb;
    private String timee;
    private String timenotif;
    private String id;

    public FillItem(String id ,String title, String timeb, String timee, String timenotif){
        this.timee = timee;
        this.title = title;
        this.timeb = timeb;
        this.timenotif = timenotif;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
