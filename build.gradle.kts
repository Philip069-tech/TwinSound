plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "com.example.twinsound"
    compileSdk = 36
    defaultConfig {
        applicationId = "com.example.twinsound"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.2"
    }
}
dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.mediarouter:mediarouter:1.8.1")
}
