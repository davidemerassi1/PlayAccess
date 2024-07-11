plugins {
    id("com.android.library")
}

android {
    namespace = "com.example.accessibilityservice"
    compileSdk = 34

    defaultConfig {
        minSdk = 26
        targetSdk = 34
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation(project(":common"))
    implementation(project(":feature:actionsrecognizer"))
    implementation(project(":feature:eventsexecutor+gamesconfigurator"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Camera
    val camerax_version = "1.2.2"
    implementation ("androidx.camera:camera-core:$camerax_version")
    implementation ("androidx.camera:camera-camera2:$camerax_version")
    implementation ("androidx.camera:camera-lifecycle:$camerax_version")
    implementation ("androidx.camera:camera-view:$camerax_version")

    implementation("org.checkerframework:checker-qual:3.18.0")
    implementation ("com.google.guava:guava:31.0.1-android")
    //ML Kit
    implementation ("com.google.mlkit:face-detection:16.1.6")

    //Room
    val room_version = "2.5.1"
    implementation ("androidx.room:room-common:$room_version")
    implementation ("androidx.room:room-runtime:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")
}