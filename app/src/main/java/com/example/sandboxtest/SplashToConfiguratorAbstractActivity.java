package com.example.sandboxtest;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import it.unimi.di.ewlab.iss.common.ui.intro.PlayAccessIntroActivity;

public abstract class SplashToConfiguratorAbstractActivity extends AppCompatActivity {

    abstract String getDestination();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_splashscreen);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        WindowInsetsControllerCompat windowInsetsControllerCompat = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        windowInsetsControllerCompat.setAppearanceLightStatusBars(true);

        Intent activityIntent = new Intent(this, PlayAccessIntroActivity.class);
        activityIntent.putExtra(PlayAccessIntroActivity.DESTINATION_KEY, getDestination());
        startActivity(activityIntent);
        finish();
    }
}
