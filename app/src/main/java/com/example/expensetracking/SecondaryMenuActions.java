package com.example.expensetracking;

import android.view.View;

public class SecondaryMenuActions {
    void DisableMenu(View[] views){
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    void EnableMenu(View[] views){
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }
}