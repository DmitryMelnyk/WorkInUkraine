package com.dmelnyk.workinukraine.helpers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dmitry on 21.01.17.
 */

public class Job implements Parcelable {
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

    protected Job(Parcel in) {
        title = in.readString();
        date = in.readString();
        urlCode = in.readString();
    }

    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };

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

    @Override
    public boolean equals(Object obj) {
        return this.getUrlCode().equals(((Job) obj).getUrlCode());
    }

    @Override
    public String toString() {
        return urlCode + " " + date + " " + title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(date);
        dest.writeString(urlCode);
    }
}
