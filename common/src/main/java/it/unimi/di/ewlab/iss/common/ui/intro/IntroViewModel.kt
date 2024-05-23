package it.unimi.di.ewlab.iss.common.ui.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class IntroViewModel : ViewModel() {

    private val _permissions = MutableLiveData(false)
    val permissions: LiveData<Boolean> = _permissions

    fun setPermissions(hasPermissions: Boolean) {
        _permissions.value = hasPermissions
    }
}