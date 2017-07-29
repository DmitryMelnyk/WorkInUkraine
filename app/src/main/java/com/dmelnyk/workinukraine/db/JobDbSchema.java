package com.dmelnyk.workinukraine.db;

/**
 * Created by dmitry on 26.01.17.
 */

public class JobDbSchema {
    public static final class JobTable {
        public static final String[] NAMES = {
                "HEADHUNTERSUA",
                "JOBSUA",
                "RABOTAUA",
                "WORKNEWINFO",
                "WORKUA" };

        public static final String FAVORITE = "MENU_TYPE_FAVORITE";
        public static final String RECENT = "RECENT";
        public static final String NEW = "FRESH";

        public static final class Columns {
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String URL = "urlCode";
        }
    }
}
