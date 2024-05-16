package com.example.sandboxtest;

import androidx.annotation.NonNull;

import it.unimi.di.ewlab.iss.common.storage.ModuleDestination;

public class SplashToActionsConfiguratorActivity extends SplashToConfiguratorAbstractActivity {
    @NonNull
    @Override
    public String getDestination() {
        return ModuleDestination.ACTIONSCONFIGURATOR.name();
    }
}