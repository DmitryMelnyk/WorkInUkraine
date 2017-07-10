package com.dmelnyk.workinukraine.business.search.model;

import android.graphics.drawable.Drawable;

/**
 * Created by d264 on 6/25/17.
 */

public class SearchRequestModel {
    private Drawable avatar;
    private String request;
    private String city;
    private int jobCount;
    private String lastUpdate;

    SearchRequestModel() {
    }

    public static Builder Builder() {
        return new SearchRequestModel().new Builder();
    }

    public class Builder {

        private Builder() {}

        public Builder withAvatar(Drawable avatar) {
            SearchRequestModel.this.avatar = avatar;
            return this;
        }

        public Builder withRequest(String request) {
            SearchRequestModel.this.request = request;
            return this;
        }

        public Builder withCity(String city) {
            SearchRequestModel.this.city = city;
            return this;
        }

        public Builder withJobCount(int jobCount) {
            SearchRequestModel.this.jobCount = jobCount;
            return this;
        }

        public Builder withLastUpdate(String lastUpdate) {
            SearchRequestModel.this.lastUpdate = lastUpdate;
            return this;
        }

        public SearchRequestModel build() {
            return SearchRequestModel.this;
        }
    }

    public Drawable getAvatar() {
        return avatar;
    }

    public String getRequest() {
        return request;
    }

    public String getCity() {
        return city;
    }

    public int getJobCount() {
        return jobCount;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }
}
