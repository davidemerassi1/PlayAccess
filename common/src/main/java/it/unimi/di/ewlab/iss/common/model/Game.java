package it.unimi.di.ewlab.iss.common.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {

    private String bundleId = "";
    private String title = "";
    private String icon = "";
    private List<Configuration> configurations;
    private Configuration selectedConfiguration;
    private String gameScreen = "";

    public Configuration getSelectedConfiguration() {
        return selectedConfiguration;
    }

    public void setSelectedConfiguration(Configuration selectedConfiguration) {
        this.selectedConfiguration = selectedConfiguration;
    }



    public Game() { }

    public Game(String bundleId, String title, String icon, String gameScreen) {

        this.bundleId = bundleId;
        this.title = title;
        this.icon = icon;
        this.configurations = new ArrayList<>();
        this.gameScreen = gameScreen;
    }

    public Game(String bundleId, String title, String icon) {

        this.bundleId = bundleId;
        this.title = title;
        this.icon = icon;
        this.configurations = new ArrayList<>();
    }

    public Game(String bundleId, String title) {
        this.bundleId = bundleId;
        this.title = title;
        this.configurations = new ArrayList<>();
    }

    public Game(String bundleId, String title, String icon, String gameScreen, List<Configuration> configurations) {

        this.bundleId = bundleId;
        this.title = title;
        this.icon = icon;
        this.configurations = configurations;
        this.gameScreen = gameScreen;
    }

    public Game(String bundleId, String title, String icon, List<Configuration> configurations) {
        this.bundleId = bundleId;
        this.title = title;
        this.icon = icon;
        this.configurations = configurations;
    }

    public Game(String bundleId, String title, List<Configuration> configurations) {
        this.bundleId = bundleId;
        this.title = title;
        this.configurations = configurations;
    }

    public boolean addConfiguration(Configuration newConfiguration){

        for (Configuration configuration : configurations)
            if (
                    configuration.getConfId() == newConfiguration.getConfId() ||
                            configuration.getConfName().equals(newConfiguration.getConfName())
            )
                return false;
        configurations.add(newConfiguration);

        return true;
    }

    public boolean removeConfiguration(Configuration config) { return configurations.remove(config); }

    public String getBundleId() {
        return bundleId;
    }

    public String getTitle() {
        return title;
    }

    public String getIcon() {
        return icon;
    }

    public String getGameScreen() {
        return gameScreen;
    }

    public List<Configuration> getConfigurations() {
        return configurations;
    }

    @Nullable
    public Configuration getConfiguration(String confName) {
        for (Configuration conf : configurations)
            if (conf.getConfName().equals(confName))
                return conf;
        return null;
    }

    public boolean equals(Object other) {

        if (!(other instanceof Game)) {
            return false;
        }

        Game otherGame = (Game) other;
        return this.getBundleId().equals(otherGame.getBundleId());
    }

    //--------------------------------NEW VARIABLES AND METHODS-------------------------------------
    private boolean frantic;

    public Game(String bundleId, String title, String icon, boolean frantic) {
        this.bundleId = bundleId;
        this.title = title;
        this.icon = icon;
        this.frantic = frantic;
    }

    public boolean isFrantic() { return frantic; }

    public Bitmap getBitmapIcon(){
        byte[] decodedString = Base64.decode(icon, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    @NonNull
    @Override
    public String toString() {
        return "Game{" +
                "bundleId='" + bundleId + '\'' +
                ", configurations=" + configurations +
                ", gameScreen='" + gameScreen + '\'' +
                '}';
    }
}
