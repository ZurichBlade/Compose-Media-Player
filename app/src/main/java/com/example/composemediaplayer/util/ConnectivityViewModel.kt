package com.example.composemediaplayer.util

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ConnectivityViewModel(application: Application) : AndroidViewModel(application) {

    private val _isNetworkAvailable = MutableLiveData<Boolean>()
    val isNetworkAvailable: LiveData<Boolean> get() = _isNetworkAvailable

    private val networkUtils = NetworkUtils(application)

    init {
        // Register to listen to network changes
        networkUtils.registerNetworkCallback { isConnected ->
            _isNetworkAvailable.postValue(isConnected)
        }
    }

    // You can also provide a method to check network availability when needed
    fun checkNetworkAvailability() {
        _isNetworkAvailable.value = networkUtils.isNetworkAvailable()
    }


}
