plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.aplicaciones_android.ae2_abpro1___grupo_1"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.aplicaciones_android.ae2_abpro1___grupo_1"
        minSdk = 27
        targetSdk = 36
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
    testOptions {
        unitTests.all {
            it.jvmArgs(
                "-XX:+EnableDynamicAgentLoading",
                "-Dnet.bytebuddy.experimental=true"
            )
        }
    }
    // Toolchain explícita
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // OkHttp logging
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Lifecycle (ViewModel + LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // RecyclerView & CardView & SwipeRefresh
    implementation("androidx.recyclerview:recyclerview:1.3.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    // Navigation Component
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.3")

    // Testing dependencies
    // Coroutines test utilities
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    // Mocking library for Kotlin (actualizado para soporte de Java 21+)
    testImplementation("io.mockk:mockk:1.13.13")
    // Architecture components testing (LiveData, ViewModel)
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    // MockWebServer para pruebas de Retrofit
    testImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    // SLF4J simple binding para evitar NOP logger warnings en tests
    testImplementation("org.slf4j:slf4j-simple:2.0.13")
    // AndroidX test - instrumentation
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    // Espresso already present; add espresso-contrib if needed
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
}