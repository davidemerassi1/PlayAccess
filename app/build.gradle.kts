plugins {
    id("com.android.application")
    kotlin("android")

}

android {
    namespace = "it.unimi.di.ewlab.iss.playaccess3"
    compileSdk = 34

    defaultConfig {
        applicationId = "it.unimi.di.ewlab.iss.playaccess3"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    sourceSets {
        getByName("main").java.srcDirs("libs")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment:2.7.7")
    implementation("androidx.navigation:navigation-ui:2.7.7")
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.5.30")
    implementation("com.google.dagger:hilt-android:2.40.1")
    implementation(project(":common"))
    implementation("androidx.activity:activity:1.8.0")
    implementation(project(":feature:actionsconfigurator"))
    implementation(project(":feature:accessibilityservice"))
    implementation(project(":feature:gamesconfigurator"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}