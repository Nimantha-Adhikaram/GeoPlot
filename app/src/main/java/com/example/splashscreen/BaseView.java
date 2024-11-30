package com.example.splashscreen;

import android.app.Activity;

public interface BaseView<T> {
    void setPresenter(T presenter);
    Activity getViewActivity();
}
