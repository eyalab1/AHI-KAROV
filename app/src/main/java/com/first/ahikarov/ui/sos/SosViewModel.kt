package com.first.ahikarov.ui.sos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.first.ahikarov.MyApplication

class SosViewModel(application: Application)
    : AndroidViewModel(application) {

    val address : LiveData<String> = LocationUpdatesLiveData(application.applicationContext)
}