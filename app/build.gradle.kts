plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.xuhuabao.musicPlayer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xuhuabao.musicPlayer"
        minSdk = 27
        targetSdk = 34
        versionCode = 11
        versionName = "2.1"

        // For showing build version name
        buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures{
        // For viewBinding
        viewBinding = true

        // For showing build version name
        buildConfig = true
    }

}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Pull to Refresh
    implementation(libs.legacy.support)

    // Glide for image loading
    implementation(libs.glide)

    // For storing objects in shared preferences
    implementation(libs.gson)

    // Notification
    implementation(libs.androidx.media)

    // Vertical Seekbar
    implementation(libs.verticalseekbar)

}