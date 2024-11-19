package com.polije.sem3.model;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class NotifyViewModel extends ViewModel {
    // LiveData untuk menampung list notifikasi
    private final MutableLiveData<List<NotifyModelNew>> notifyList = new MutableLiveData<>(new ArrayList<>());

    // Mendapatkan data notifikasi
    public LiveData<List<NotifyModelNew>> getNotifyList() {
        return notifyList;
    }

    // Menambahkan item baru ke dalam list
    public void addNotify(NotifyModelNew newNotify) {
        List<NotifyModelNew> currentList = notifyList.getValue();
        if (currentList != null) {
            currentList.add(newNotify);
            notifyList.setValue(currentList); // Memperbarui LiveData
        }
    }
}
