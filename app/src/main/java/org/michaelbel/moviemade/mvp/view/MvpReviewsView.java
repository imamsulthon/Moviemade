package org.michaelbel.moviemade.mvp.view;

import com.arellomobile.mvp.MvpView;

import org.michaelbel.moviemade.rest.model.v3.Review;

import java.util.List;

public interface MvpReviewsView extends MvpView {

    void showResults(List<Review> newReviews);

    void showNoResults();

    void showNoConnection();
}