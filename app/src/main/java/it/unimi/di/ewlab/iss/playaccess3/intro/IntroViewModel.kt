package it.unimi.di.ewlab.iss.playaccess3.intro

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