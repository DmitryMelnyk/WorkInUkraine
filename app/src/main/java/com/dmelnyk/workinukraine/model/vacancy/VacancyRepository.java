package com.dmelnyk.workinukraine.model.vacancy;

import com.dmelnyk.workinukraine.business.vacancy.IVacancyInteractor;
import com.dmelnyk.workinukraine.data.VacancyModel;
import com.dmelnyk.workinukraine.db.DbItems;
import com.dmelnyk.workinukraine.db.Tables;
import com.squareup.sqlbrite2.BriteDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import timber.log.Timber;

/**
 * Created by d264 on 7/28/17.
 */

public class VacancyRepository implements IVacancyRepository {

    private static final String SELECT_ALL_FROM = "SELECT * FROM ";

    private final BriteDatabase db;

    public VacancyRepository(BriteDatabase db) {
        this.db = db;
    }

    @Override
    public Observable<Map<String, Map<String, List<VacancyModel>>>> getAllVacancies(String request) {
        Timber.d("\ngetAllVacancies, request =%s", request);

        Observable<Map<String, List<VacancyModel>>> observableSites = Observable.zip(
                getVacancies(request, IVacancyInteractor.VACANCIES_SITE_HH),
                getVacancies(request, IVacancyInteractor.VACANCIES_SITE_JU),
                getVacancies(request, IVacancyInteractor.VACANCIES_SITE_RU),
                getVacancies(request, IVacancyInteractor.VACANCIES_SITE_WN),
                getVacancies(request, IVacancyInteractor.VACANCIES_SITE_WU),
                (vacancyModels, vacancyModels2, vacancyModels3, vacancyModels4, vacancyModels5) -> {

                    Map<String, List<VacancyModel>> result = new HashMap<String, List<VacancyModel>>();
                    if (!vacancyModels.isEmpty())
                        result.put(Tables.SearchSites.SITES[0], vacancyModels);
                    if (!vacancyModels2.isEmpty())
                        result.put(Tables.SearchSites.SITES[1], vacancyModels2);
                    if (!vacancyModels3.isEmpty())
                        result.put(Tables.SearchSites.SITES[2], vacancyModels3);
                    if (!vacancyModels4.isEmpty())
                        result.put(Tables.SearchSites.SITES[3], vacancyModels4);
                    if (!vacancyModels5.isEmpty())
                        result.put(Tables.SearchSites.SITES[4], vacancyModels5);
                    return result;
                });

        Observable<Map<String, List<VacancyModel>>> observableTabs = Observable.zip(
                getVacancies(request, IVacancyInteractor.VACANCIES_NEW),
                getVacancies(request, IVacancyInteractor.VACANCIES_RECENT),
                getVacancies(request, IVacancyInteractor.VACANCIES_FAVORITE),
                (vacancyModels, vacancyModels2, vacancyModels3) -> {

                    Map<String, List<VacancyModel>> result = new HashMap<String, List<VacancyModel>>();
                    result.put(IVacancyInteractor.VACANCIES_NEW, vacancyModels);
                    result.put(IVacancyInteractor.VACANCIES_RECENT, vacancyModels2);
                    result.put(IVacancyInteractor.VACANCIES_FAVORITE, vacancyModels3);
                    return result;
                });

        return Observable.zip(observableSites, observableTabs, ((stringListMap, stringListMap2) -> {
            Map<String, Map<String, List<VacancyModel>>> result =
                    new HashMap<>();

            result.put(IVacancyInteractor.DATA_TAB_SITES, stringListMap);
            result.put(IVacancyInteractor.DATA_OTHER_TABS, stringListMap2);
            return result;
        }));
    }

    @Override
    public Observable<List<VacancyModel>> getVacancies(String request, String table) {
        Timber.d("\ngetVacancies() request=%s from table=%s", request, table);

        String TABLE = "";
        switch (table) {
            case IVacancyInteractor.VACANCIES_FAVORITE:
                TABLE = Tables.SearchSites.FAVORITE;
                break;
            case IVacancyInteractor.VACANCIES_NEW:
                TABLE = Tables.SearchSites.NEW;
                break;
            case IVacancyInteractor.VACANCIES_RECENT:
                TABLE = Tables.SearchSites.RECENT;
                break;

            default:
                TABLE = table;
        }

        return db.createQuery(TABLE, "SELECT * FROM " +
                TABLE + " WHERE " + Tables.SearchSites.Columns.REQUEST + " ='" + request + "'")
                .mapToList(VacancyModel.MAPPER);
    }

    @Override
    public Completable removeFromFavorites(VacancyModel vacancy) {
        Timber.d("\nremoveFromFavorites: %s", vacancy);
        return Completable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return db.delete(Tables.SearchSites.FAVORITE,
                        Tables.SearchSites.Columns.URL + " ='" + vacancy.url() + "'");
            }
        });
    }

    @Override
    public Completable saveToFavorite(VacancyModel vacancy) {
        Timber.d("\nSaving to FAVORITE table %s", vacancy);
        return Completable.fromCallable(() ->
                db.insert(Tables.SearchSites.FAVORITE, DbItems.createVacancyItem(vacancy))
        );
    }
}
