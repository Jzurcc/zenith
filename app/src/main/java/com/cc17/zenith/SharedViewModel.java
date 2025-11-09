package com.cc17.zenith;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> title = new MutableLiveData<>();
    private final MutableLiveData<String> subtitle = new MutableLiveData<>();

    public LiveData<String> getTitle() { return title; }
    public LiveData<String> getSubtitle() { return subtitle; }

    public void setTexts(String newTitle, String newSubtitle) {
        title.setValue(newTitle);
        subtitle.setValue(newSubtitle);
    }
}