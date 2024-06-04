package com.example.sandboxtest.ui.intro

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IntroViewModel : ViewModel() {

    private val _permissions = MutableLiveData(false)
    val permissions: LiveData<Boolean> = _permissions

    fun setPermissions(hasPermissions: Boolean) {
        Log.d("IntroViewModel", "setPermissions: $hasPermissions")
        _permissions.value = hasPermissions
    }
}