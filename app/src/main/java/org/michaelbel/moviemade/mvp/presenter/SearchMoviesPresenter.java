package org.michaelbel.moviemade.mvp.presenter;

import android.support.annotation.NonNull;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import org.michaelbel.moviemade.app.ApiFactory;
import org.michaelbel.moviemade.app.Url;
import org.michaelbel.moviemade.model.SearchItem;
import org.michaelbel.moviemade.mvp.view.MvpSearchView;
import org.michaelbel.moviemade.rest.api.SEARCH;
import org.michaelbel.moviemade.rest.model.Movie;
import org.michaelbel.moviemade.rest.response.MovieResponse;
import org.michaelbel.moviemade.util.AndroidUtils;
import org.michaelbel.moviemade.util.DateUtils;
import org.michaelbel.moviemade.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@InjectViewState
public class SearchMoviesPresenter extends MvpPresenter<MvpSearchView> {

    private int page;
    private int totalPages;
    private boolean loading;
    private boolean loadingLocked;

    private String currentQuery;

    public void search(String query) {
        page = 1;
        totalPages = 0;
        loading = false;
        loadingLocked = false;
        currentQuery = query;

        getViewState().searchStart();

        if (NetworkUtils.notConnected()) {
            getViewState().showError();
            return;
        }

        SEARCH service = ApiFactory.getRetrofit().create(SEARCH.class);
        Call<MovieResponse> call = service.searchMovies(Url.TMDB_API_KEY, Url.en_US, query, page, AndroidUtils.includeAdult(), null);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!response.isSuccessful()) {
                    getViewState().searchNoResults();
                    return;
                }

                //if (response.body() == null) {
                //    getViewState().searchNoResults();
                //    return;
                //}

                addToSearchHistory(query);

                totalPages = response.body().totalPages;

                List<Movie> newMovies = new ArrayList<>();

                if (AndroidUtils.includeAdult()) {
                    newMovies.addAll(response.body().movies);
                } else {
                    for (Movie movie : response.body().movies) {
                        if (!movie.adult) {
                            newMovies.add(movie);
                        }
                    }
                }

                if (newMovies.isEmpty()) {
                    getViewState().searchNoResults();
                    return;
                }

                getViewState().searchComplete(newMovies, response.body().totalResults);
                page++;
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                getViewState().showError();
            }
        });
    }

    public void loadResults() {
        SEARCH service = ApiFactory.getRetrofit().create(SEARCH.class);
        Call<MovieResponse> call = service.searchMovies(Url.TMDB_API_KEY, Url.en_US, currentQuery, page, AndroidUtils.includeAdult(), null);
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                if (!response.isSuccessful()) {
                    loadingLocked = true;
                    return;
                }

                List<Movie> newMovies = new ArrayList<>();

                if (AndroidUtils.includeAdult()) {
                    newMovies.addAll(response.body().movies);
                } else {
                    for (Movie movie : response.body().movies) {
                        if (!movie.adult) {
                            newMovies.add(movie);
                        }
                    }
                }

                if (newMovies.isEmpty()) {
                    return;
                }

                getViewState().nextPageLoaded(newMovies);
                page++;
                loading = false;
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadingLocked = true;
                loading = false;
            }
        });

        loading = true;
    }

    private void addToSearchHistory(String query) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            SearchItem item = realm1.createObject(SearchItem.class);
            item.queryTitle = query;
            item.queryDate = DateUtils.getCurrentDateAndTimeWithMilliseconds();
        });
    }

    // Getters

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean isLoadingLocked() {
        return loadingLocked;
    }
}