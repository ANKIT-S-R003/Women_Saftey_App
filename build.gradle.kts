// Top-level build file where you can add configuration options common to all sub-projects/modules.
// C:/Users/ankit/AndroidStudioProjects/MyApp/build.gradle.kts
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // ▼ ADD THIS LINE ▼
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    // Add this line to apply the google-services plugin to the project
    alias(libs.plugins.google.gms.google.services) apply false
}
