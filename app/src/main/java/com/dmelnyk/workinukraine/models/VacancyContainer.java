//package com.dmelnyk.workinukraine.models;
//
//import android.database.Cursor;
//import android.os.Parcelable;
//
//import com.dmelnyk.workinukraine.db.Db;
//import com.dmelnyk.workinukraine.db.Tables;
//import com.google.auto.value.AutoValue;
//
//import io.reactivex.annotations.NonNull;
//import io.reactivex.functions.Function;
//
///**
// * Created by d264 on 8/15/17.
// */
//
//@AutoValue
//public abstract class VacancyContainer implements Parcelable {
//    public abstract VacancyModel getVacancy();
//    public abstract String getType();
//
//    public static VacancyContainer create(VacancyModel vacancyModel, String type) {
//        return new AutoValue_VacancyContainer(vacancyModel, type);
//    }
//
//    public static Function<Cursor, VacancyContainer> MAPPER = new Function<Cursor, VacancyContainer>() {
//        @Override public VacancyContainer apply(@NonNull Cursor cursor) throws Exception {
//            String request = Db.getString(cursor, Tables.SearchSites.Columns.REQUEST);
//            String type = Db.getString(cursor, Tables.SearchSites.Columns.TYPE);
//            String title = Db.getString(cursor, Tables.SearchSites.Columns.TITLE);
//            String date = Db.getString(cursor, Tables.SearchSites.Columns.DATE);
//            String url = Db.getString(cursor, Tables.SearchSites.Columns.URL);
//            boolean isFavorite = Db.getBoolean(cursor, Tables.SearchSites.Columns.IS_FAVORITE);
//
//            VacancyModel vacancy = VacancyModel.builder()
//                    .setRequest(request)
//                    .setTitle(title)
//                    .setDate(date)
//                    .setUrl(url)
//                    .setIsFavorite(isFavorite)
//                    .build();
//
//            return create(vacancy, type);
//        }
//    };
//
//    @Override
//    public boolean equals(Object obj) {
//        VacancyContainer v = (VacancyContainer) obj;
//        return this.getVacancy().url().equals(v.getVacancy().url());
//    }
//}
