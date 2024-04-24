plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.sandboxtest"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.sandboxtest"
        minSdk = 23
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
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
    val roomversion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomversion")
    annotationProcessor("androidx.room:room-compiler:$roomversion")
    implementation("androidx.camera:camera-camera2:1.3.2")
    implementation("androidx.camera:camera-core:1.3.2")
    implementation("androidx.camera:camera-lifecycle:1.3.2")
    implementation("androidx.camera:camera-view:1.3.2")
    implementation ("com.google.mlkit:face-detection:16.1.6")
    testImplementation("junit:junit:4.13.2")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":Bcore"))
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

/*
    //implementation("com.android.support:multidex:1.0.3")

    // Promise Support
    implementation("org.jdeferred:jdeferred-android-aar:1.2.4")

    // ThirdParty
    implementation("com.jonathanfinerty.once:once:1.0.3")

    val appCenterSdkVersion = "3.0.0"
    implementation("com.microsoft.appcenter:appcenter-analytics:${appCenterSdkVersion}")
    implementation("com.microsoft.appcenter:appcenter-crashes:${appCenterSdkVersion}")

    //implementation("com.kyleduo.switchbutton:library:1.4.6")
    implementation("com.allenliu.versionchecklib:library:1.8.3")
    implementation("com.github.medyo:android-about-page:1.2.2")
    //implementation("moe.feng:AlipayZeroSdk:1.1")

    // Glide
    /*implementation("com.github.bumptech.glide:glide:4.8.0") {
        exclude(group = "com.android.support")
    }*/
    annotationProcessor("com.github.bumptech.glide:compiler:4.8.0")*/
}