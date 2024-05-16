package it.unimi.di.ewlab.iss.actionsconfigurator.viewmodel

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel

class AggiungiPulsanteEsternoViewModel(application: Application) : AndroidViewModel(application) {
    val adapter: BluetoothAdapter

    init {
        adapter = (
                application.applicationContext.getSystemService(Context.BLUETOOTH_SERVICE)
                        as BluetoothManager
                ).adapter
    }

    fun checkBluetooth(): Boolean {
        return adapter.isEnabled
    }
}