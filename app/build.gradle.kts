plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.urbanin"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.urbanin"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    sourceSets {
        getByName("main").java.srcDirs("build/generated/source/navigation-args")
    }

}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.9.1")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-auth-ktx:22.2.0")
    implementation("com.google.firebase:firebase-database-ktx:20.3.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // to make fragment navigation work (in android studio)
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // to customize the splash screen (default used by android, min-Android 12)
    implementation("androidx.core:core-splashscreen:1.0.1")

    // for Google Maps API
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    // Google Places Autocomplete API
    implementation("com.google.android.libraries.places:places:3.2.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    // Dots Indicaor (custom library - https://github.com/tommybuonomo/dotsindicator)
    implementation("com.tbuonomo:dotsindicator:5.0")

    // Firebase Firestore
    implementation(platform("com.google.firebase:firebase-bom:32.4.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

    // For downloading and caching Firestore media files
    implementation("com.squareup.picasso:picasso:2.8")

    /// Biometric Authentication
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    // Google Sign-In
    implementation("com.google.gms:google-services:4.3.10")

    // Glide
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

    // Firestore Recycler View
    implementation("com.firebaseui:firebase-ui-firestore:8.0.0")



}
