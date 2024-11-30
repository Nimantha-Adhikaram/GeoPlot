plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    // Combine the namespaces or use the primary one
    namespace = "com.example.splashscreen"  // You can choose the most appropriate namespace

    compileSdk = 34

    defaultConfig {
        // Retain the base application ID or choose the one that fits your project structure
        applicationId = "com.example.splashscreen"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    // Combine all dependencies from both files, avoiding duplicates
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.10.1")

    // Dependencies for the area page
    // Note: Updating or keeping versions consistent across the project
    implementation("androidx.appcompat:appcompat:1.6.1") // Older version, consider updating to 1.7.0 if compatible
    implementation("com.google.android.material:material:1.11.0") // Older version, consider updating to 1.12.0 if compatible

    // Dependencies from the dashboard project
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.maps:google-maps-services:0.19.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.firebase:firebase-firestore:25.1.0")

    // Dependencies for navigation (if needed)
    // Uncomment if you use these in the project
    // implementation("androidx.navigation:navigation-fragment:2.6.0")
    // implementation("androidx.navigation:navigation-ui:2.6.0")

    // Testing dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Additional testing dependencies from the area page
    androidTestImplementation("androidx.test.ext:junit:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.0")

    implementation("com.google.maps.android:android-maps-utils:2.3.0")
    implementation("androidx.core:core-ktx:1.12.0")

}
