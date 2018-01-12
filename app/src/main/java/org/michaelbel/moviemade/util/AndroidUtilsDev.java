package org.michaelbel.moviemade.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.michaelbel.moviemade.app.Moviemade;
import org.michaelbel.moviemade.app.annotation.Beta;

public class AndroidUtilsDev {

    private static Context getContext() {
        return Moviemade.AppContext;
    }

    @Beta
    public static boolean scrollbars() {
        SharedPreferences prefs = getContext().getSharedPreferences("devconfig", Context.MODE_PRIVATE);
        return prefs.getBoolean("scrollbars", true);
    }

    @Beta
    public static boolean zoomReview() {
        SharedPreferences prefs = getContext().getSharedPreferences("devconfig", Context.MODE_PRIVATE);
        return prefs.getBoolean("zoom_review", true);
    }


    public static boolean floatingToolbar() {
        SharedPreferences prefs = getContext().getSharedPreferences("devconfig", Context.MODE_PRIVATE);
        return prefs.getBoolean("floating_toolbar", false);
    }

    public static boolean hamburgerIcon() {
        SharedPreferences prefs = getContext().getSharedPreferences("devconfig", Context.MODE_PRIVATE);
        return prefs.getBoolean("burger", false);
    }

    public static boolean searchResultsCount() {
        SharedPreferences prefs = getContext().getSharedPreferences("devconfig", Context.MODE_PRIVATE);
        return prefs.getBoolean("search_results_count", false);
    }
}