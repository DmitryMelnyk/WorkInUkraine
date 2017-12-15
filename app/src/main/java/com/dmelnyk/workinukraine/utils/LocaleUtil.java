package com.dmelnyk.workinukraine.utils;

import java.util.Locale;

/**
 * Created by d264 on 12/15/17.
 */

public class LocaleUtil {

    private static final String UK_LOCALE = "українська";

    private static final String BACK_RU = "назад";



    private static final String MINUTE_AGO0_RU = "минуту назад";
    private static final String MINUTE_AGO1_RU = "минуты назад";
    private static final String MINUTE_AGO2_RU ="минут назад";

    private static final String MINUTE_AGO0_UA = "хвилин тому";
    private static final String MINUTE_AGO1_UA = "хвилини тому";
    private static final String MINUTE_AGO2_UA ="хвилин тому";

    private static final String HOUR_AGO0_RU = "час назад";
    private static final String HOUR_AGO1_RU = "часа назад";
    private static final String HOUR_AGO2_RU ="часов назад";

    private static final String HOUR_AGO0_UA = "годину тому";
    private static final String HOUR_AGO1_UA = "години тому";
    private static final String HOUR_AGO2_UA ="годин тому";

    private static final String DAY_AGO0_RU = "вчера";
    private static final String DAY_AGO1_RU = "дня назад";
    private static final String DAY_AGO2_RU ="дней назад";

    private static final String DAY_AGO0_UA = "вчора";
    private static final String DAY_AGO1_UA = "дні тому";
    private static final String DAY_AGO2_UA ="днів тому";

    private static final String WEEK_AGO0_RU = "неделю назад";
    private static final String WEEK_AGO1_RU = "недели назад";

    private static final String WEEK_AGO0_UA = "тиждень тому";
    private static final String WEEK_AGO1_UA = "тижні тому";

    private static final String MONTH_AGO0_RU = "месяц назад";
    private static final String MONTH_AGO1_RU = "месяца назад";
    private static final String MONTH_AGO2_RU = "месяцы назад";
    private static final String MONTH_AGO3_RU = "месяцов назад";

    private static final String MONTH_AGO0_UA = "місяць тому";
    private static final String MONTH_AGO1_UA = "місяці тому";
    private static final String MONTH_AGO2_UA = "місяці тому";
    private static final String MONTH_AGO3_UA = "місяців тому";

    private static final String JANUARY_RU = "января";
    private static final String FEBRUARY_RU = "февраля";
    private static final String MARCH_RU = "марта";
    private static final String APRIL_RU = "апреля";
    private static final String MAY_RU = "мая";
    private static final String JUNE_RU = "июня";
    private static final String JULY_RU = "июля";
    private static final String AUGUST_RU = "августа";
    private static final String SEPTEMBER_RU = "сентября";
    private static final String OCTOBER_RU = "октября";
    private static final String NOVEMBER_RU = "ноября";
    private static final String DECEMBER_RU = "декабря";

    private static final String JANUARY_UA = "січня";
    private static final String FEBRUARY_UA = "лютого";
    private static final String MARCH_UA = "березня";
    private static final String APRIL_UA = "квітня";
    private static final String MAY_UA = "травня";
    private static final String JUNE_UA = "червня";
    private static final String JULY_UA = "липня";
    private static final String AUGUST_UA = "серпня";
    private static final String SEPTEMBER_UA = "вересня";
    private static final String OCTOBER_UA = "жовтня";
    private static final String NOVEMBER_UA = "листопада";
    private static final String DECEMBER_UA = "грудня";

    private static boolean isUkrainianLocale() {
        return UK_LOCALE.equals(Locale.getDefault().getDisplayLanguage());
    }

    public static String convertToProperLanguage(String date) {
        if (isUkrainianLocale()) return convertToUkrainian(date);
        return date;
    }

    private static String convertToUkrainian(String date) {
        if (date.contains(MINUTE_AGO0_RU)) return date.replace(MINUTE_AGO0_RU, MINUTE_AGO0_UA);
        if (date.contains(MINUTE_AGO1_RU)) return date.replace(MINUTE_AGO1_RU, MINUTE_AGO1_UA);
        if (date.contains(MINUTE_AGO2_RU)) return date.replace(MINUTE_AGO2_RU, MINUTE_AGO2_UA);

        if (date.contains(HOUR_AGO0_RU)) return date.replace(HOUR_AGO0_RU, HOUR_AGO0_UA);
        if (date.contains(HOUR_AGO1_RU)) return date.replace(HOUR_AGO1_RU, HOUR_AGO1_UA);
        if (date.contains(HOUR_AGO2_RU)) return date.replace(HOUR_AGO2_RU, HOUR_AGO2_UA);

        if (date.contains(DAY_AGO0_RU)) return date.replace(DAY_AGO0_RU, DAY_AGO0_UA);
        if (date.contains(DAY_AGO1_RU)) return date.replace(DAY_AGO1_RU, DAY_AGO1_UA);
        if (date.contains(DAY_AGO2_RU)) return date.replace(DAY_AGO2_RU, DAY_AGO2_UA);

        if (date.contains(WEEK_AGO0_RU)) return date.replace(WEEK_AGO0_RU, WEEK_AGO0_UA);
        if (date.contains(WEEK_AGO1_RU)) return date.replace(WEEK_AGO1_RU, WEEK_AGO1_UA);

        if (date.contains(MONTH_AGO0_RU)) return date.replace(MONTH_AGO0_RU, MONTH_AGO0_UA);
        if (date.contains(MONTH_AGO1_RU)) return date.replace(MONTH_AGO1_RU, MONTH_AGO1_UA);
        if (date.contains(MONTH_AGO2_RU)) return date.replace(MONTH_AGO2_RU, MONTH_AGO2_UA);
        if (date.contains(MONTH_AGO3_RU)) return date.replace(MONTH_AGO3_RU, MONTH_AGO3_UA);

        if (date.contains(JANUARY_RU)) return date.replace(JANUARY_RU, JANUARY_UA);
        if (date.contains(FEBRUARY_RU)) return date.replace(FEBRUARY_RU, FEBRUARY_UA);
        if (date.contains(MARCH_RU)) return date.replace(MARCH_RU, MARCH_UA);
        if (date.contains(APRIL_RU)) return date.replace(APRIL_RU, APRIL_UA);
        if (date.contains(MAY_RU)) return date.replace(MAY_RU, MAY_UA);
        if (date.contains(JUNE_RU)) return date.replace(JUNE_RU, JUNE_UA);
        if (date.contains(JULY_RU)) return date.replace(JULY_RU, JULY_UA);
        if (date.contains(AUGUST_RU)) return date.replace(AUGUST_RU, AUGUST_UA);
        if (date.contains(SEPTEMBER_RU)) return date.replace(SEPTEMBER_RU, SEPTEMBER_UA);
        if (date.contains(OCTOBER_RU)) return date.replace(OCTOBER_RU, OCTOBER_UA);
        if (date.contains(NOVEMBER_RU)) return date.replace(NOVEMBER_RU, NOVEMBER_UA);
        if (date.contains(DECEMBER_RU)) return date.replace(DECEMBER_RU, DECEMBER_UA);
        
        // if date is numeric
        return date;
    }
}
