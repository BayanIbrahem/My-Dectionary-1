plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.bayan_ibrahim.my_dictionary"
    compileSdk = 34

    configurations {
        // for the duplicate annotation problem
        implementation.get().exclude(
            mapOf("group" to "org.jetbrains", "module" to "annotations")
        )
    }
    defaultConfig {
        applicationId = "dev.bayan_ibrahim.my_dictionary"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.named("debug").get()
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
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinComposeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
//    implementation(libs.androidx.profileinstaller)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.androidx.compose.bom))

//    google-dagger-hilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }
//    google-dagger-hilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }
//    google-dagger-hilt-core = { group = "com.google.dagger", name = "hilt-core", version.ref = "hilt" }
//    google-dagger-hilt-ext-compiler = { group = "androidx.hilt", name = "hilt-compiler", version.ref = "hiltExt" }
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.google.dagger.hilt.core)
    implementation(libs.google.dagger.hilt.ext.compiler)
    ksp(libs.google.dagger.hilt.compiler)


//    androidx-room-compiler = { group = "androidx.room", name = "room-compiler", version.ref = "room" }
//    androidx-room-ktx = { group = "androidx.room", name = "room-ktx", version.ref = "room" }
//    androidx-room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
//    androidx-room-testing = { group = "androidx.room", name = "room-testing", version.ref = "room" }
//    androidx-room-paging = { group = "androidx.room", name = "room-paging", version.ref = "room" }
    // room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.paging)
    ksp(libs.androidx.room.compiler)
    testImplementation(libs.androidx.room.testing)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}