package com.dmelnyk.workinukraine.db;

/**
 * Created by d264 on 7/19/17.
 */

public class Tables {

    public static final class SearchRequest {
        public static final String TABLE_REQUEST = "search_requests";

        public static final class Columns {
            public static final String REQUEST = "request";
            public static final String VACANCIES = "vacancies";
            public static final String UPDATED = "updated";
            public static final String NEW_VACANCIES = "new_vacancies";
        }
    }

    public static final class SearchSites {
        // Two tables
        public static final String TABLE_ALL_SITES = "all_sites";
//        public static final String TABLE_FAV_NEW_REC = "favorite_new_recent";

        // Values of TYPE column: TYPE_SITES AND TYPE_FAVORITE, TYPE_NEW, TYPE_RECENT
        public static final String[] TYPE_SITES = {
                "HEADHUNTERSUA",
                "JOBSUA",
                "RABOTAUA",
                "WORKNEWINFO",
                "WORKUA" };

        public static final class Columns {
            public static final String REQUEST = "request";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String URL = "urlCode";
            public static final String TYPE = "type";
            public static final String IS_FAVORITE = "favorite";
            public static final String IS_NEW = "new";
        }
    }
}
