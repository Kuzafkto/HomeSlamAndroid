plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
   id("com.google.gms.google-services") version "4.3.15" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.1" apply false
}