package com.dmelnyk.workinukraine.db;

/**
 * Created by d264 on 7/19/17.
 */

public class Tables {

    public static final class SearchRequest {
        public static final String TABLE = "search_requests";

        public static final class Columns {
            public static final String REQUEST = "request";
            public static final String VACANCIES = "vacancies";
            public static final String UPDATED = "updated";
        }
    }

    public static final class SearchSites {
        public static final String[] SITES = {
                "HEADHUNTERSUA",
                "JOBSUA",
                "RABOTAUA",
                "WORKNEWINFO",
                "WORKUA" };

        public static final String FAVORITE = "FAVORITE";
        public static final String RECENT = "RECENT";
        public static final String NEW = "FRESH";

        public static final class Columns {
            public static final String REQUEST = "request";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String URL = "urlCode";
        }
    }
}
