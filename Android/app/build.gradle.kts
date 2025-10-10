plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    signingConfigs {
        getByName("debug") {
            storeFile = file("D:\\Project\\GitHub\\WearEngineDemo\\Android\\app\\keystore.jks")
            storePassword = "Password"
            keyAlias = "key0"
            keyPassword = "Password"
        }
        create("release") {
            storeFile = file("D:\\Project\\GitHub\\WearEngineDemo\\Android\\app\\keystore.jks")
            storePassword = "Password"
            keyAlias = "key0"
            keyPassword = "Password"
        }
    }
    namespace = "com.huawei.sample.wearable.demo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.huawei.sample.wearable.demo"
        minSdk = 29
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.1")
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // Wear Engine SDKを追加する
    implementation("com.huawei.hms:wearengine:5.0.3.304")

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}