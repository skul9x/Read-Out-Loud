plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.skul9x.readoutloud"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.skul9x.readoutloud"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
            val storePw = System.getenv("STORE_PASSWORD")
            val keyAl = System.getenv("KEY_ALIAS")
            val keyPw = System.getenv("KEY_PASSWORD")

            if (!keystorePath.isNullOrEmpty() && file(keystorePath).exists()) {
                storeFile = file(keystorePath)
                storePassword = storePw
                keyAlias = keyAl
                keyPassword = keyPw
            } else {
                val localKeystore = rootProject.file("skul9x.jks")
                if (localKeystore.exists()) {
                    storeFile = localKeystore
                    storePassword = "@Colenao123@"
                    keyAlias = "key0"
                    keyPassword = "@Colenao123@"
                }
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            val keystorePath = System.getenv("KEYSTORE_PATH")
            val localKeystore = rootProject.file("skul9x.jks")
            if ((!keystorePath.isNullOrEmpty() && file(keystorePath).exists()) || localKeystore.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.maxHeapSize = "3072m"
                it.jvmArgs("-Xmx3072m", "-XX:+UseG1GC")
                it.forkEvery = 30
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.viewpager2)
    implementation(libs.androidx.fragment.ktx)
    
    // Gemini Integration Dependencies
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.security.crypto)
    
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.mockk)
    testImplementation(libs.androidx.test.core)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}