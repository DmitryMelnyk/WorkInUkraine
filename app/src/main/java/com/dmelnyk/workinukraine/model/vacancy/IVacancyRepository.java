package com.dmelnyk.workinukraine.model.vacancy;

import com.dmelnyk.workinukraine.data.VacancyModel;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by d264 on 7/28/17.
 */

public interface IVacancyRepository {

    Observable<List<VacancyModel>> getVacancies(String request, String type);

    Completable removeItem(VacancyModel vacancy);

    Completable saveToFavorite(VacancyModel vacancy);
}
