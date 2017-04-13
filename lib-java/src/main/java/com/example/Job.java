package com.example;

import java.io.Serializable;

/**
 * Created by dmitry on 21.01.17.
 */

public class Job implements Serializable {
    private String title;
    private String date;
    private String urlCode;

    public Job(String title, String date, String urlCode) {
        this.title = title;
        this.date = date;
        this.urlCode = urlCode;
    }

    public Job() {
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

    public String getUrlCode() {
        return urlCode;
    }

    public void setUrlCode(String urlCode) {
        this.urlCode = urlCode;
    }

    @Override
    public boolean equals(Object obj) {
        return this.getUrlCode().equals(((Job) obj).getUrlCode());
    }

    @Override
    public String toString() {
        return urlCode + " " + date + " " + title;
    }
}
